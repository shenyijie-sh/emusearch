package cn.syj.emusearch.service.impl;

import cn.syj.emusearch.constant.Constants;
import cn.syj.emusearch.entity.EmuTrain;
import cn.syj.emusearch.service.EmuService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static cn.syj.emusearch.constant.Constants.*;

/**
 * @author syj
 **/
public class RemoteEmuServiceImpl implements EmuService {

    //页面缓存
    private static final Cache<String, DetailPage> cache = CacheBuilder.newBuilder()
            .initialCapacity(500)
            //读60秒后销毁
            .expireAfterAccess(300, TimeUnit.SECONDS)
            .removalListener((RemovalListener<String, DetailPage>) n -> System.out.println("Cache ---[key=" + n.getKey() + "]removed"))
            .build();

    private static final Interner<String> strWeekIntern = Interners.newWeakInterner();

    private final String remoteServerUrl;

    private final int timeoutMills;

    private static final String[] types = {MODEL, NUMBER, BUREAU, DEPARTMENT, PLANT};

    public RemoteEmuServiceImpl(String remoteServerUrl, int timeoutMills) {
        this.remoteServerUrl = remoteServerUrl;
        this.timeoutMills = timeoutMills;
    }

    public RemoteEmuServiceImpl(String remoteServerUrl) {
        this(remoteServerUrl, 10000);
    }

    private DetailPage[] getDetailPages(Map<String, Object> conditionMap) {
        conditionMap.forEach((k, v) -> {
            System.out.println(k + "->" + v);
            if (null != v && ("".equals(v) || String.valueOf(v).contains("全部"))) {
                conditionMap.put(k, null);
            }
        });
        DetailPage[] ps;
        Object keyword = null;
        String type = null;
        //优先使用缓存
        for (String t : types) {
            String key = String.format(PASS_SEARCH_PARAM_FORMAT, this.remoteServerUrl, t, keyword = conditionMap.get(t), 1);
            if (null != cache.getIfPresent(key)) {
                type = t;
                break;
            }
        }
        //没有缓存则远程获取
        if (null == type) {
            for (String t : types) {
                if (null != (keyword = conditionMap.get(t))) {
                    type = t;
                    break;
                }
            }
        }
        if (null == keyword) return new DetailPage[0];
        try {
            DetailPage p = this.remoteLoadAndParse(type, keyword, 1);
            //总数
            int total = p.total;
            //分页
            int page = (int) Math.ceil((double) total / 50D);
            ps = new DetailPage[page];
            ps[0] = p;
            //查询后面的几页
            for (int i = 2; i <= page; i++) {
                ps[i - 1] = this.remoteLoadAndParse(type, keyword, i);
            }
            return ps;
        } catch (IOException e) {
            e.printStackTrace();
            return new DetailPage[0];
        }
    }


    private static class DetailPage {

        int total;

        List<Vector<String>> content;

        public DetailPage(int total, List<Vector<String>> content) {
            this.total = total;
            this.content = content;
        }
    }

    /**
     * 远程加载{@link DetailPage}
     *
     * @param type    类型
     * @param keyword 关键字
     * @param page    页面号
     * @return {@link DetailPage}
     * @throws IOException IO异常
     */
    private DetailPage remoteLoadAndParse(String type, Object keyword, int page) throws IOException {
        String key = String.format("__%s_%s_%d__", type, keyword, page);
        DetailPage p = cache.getIfPresent(key);
        if (null == p) {
            System.out.println("正在远程加载第" + page + "页");
            URL url = new URL(String.format(PASS_SEARCH_PARAM_FORMAT, this.remoteServerUrl, type, URLEncoder.encode(String.valueOf(keyword), "utf-8"), page));
            System.out.println("加载URL:" + url);
            long startMs = System.currentTimeMillis();
            Document document = Jsoup.parse(url, this.timeoutMills);
            System.out.println("加载用时:" + -(startMs - (startMs = System.currentTimeMillis())) + "毫秒");
            p = this.parse2Page(document);
            System.out.println("转换用时:" + (System.currentTimeMillis() - startMs) + "毫秒");
            cache.put(key, p);
        }
        return p;
    }

    private DetailPage parse2Page(Document document) {
        Iterator<TextNode> iterator = document.getAllElements().textNodes().iterator();
        String sumText = null;
        while (iterator.hasNext() && sumText == null) {
            String text;
            if ((text = iterator.next().text()).contains("共有符合条件的记录"))
                System.out.println(sumText = text);
        }
        if (sumText == null) return new DetailPage(0, new ArrayList<>());
        //总数
        int total = Integer.parseInt(Pattern.compile("[^0-9]").matcher(sumText).replaceAll("").trim());
        Elements tables = document.getElementsByTag("table");
        Element tbl = tables.get(1);
        Elements trs = tbl.getElementsByTag("tr");
        List<Vector<String>> list = new ArrayList<>();
        for (int i = 1; i <= trs.size() - 1; i++) {
            Element emuInfo = trs.get(i);
            Elements tds = emuInfo.getElementsByTag("td");
            Vector<String> vector = new Vector<>(6);
            for (int j = 0; j <= 5; j++) {
                vector.add(strWeekIntern.intern(tds.get(j).text()));
                tds.get(j).text();
            }
            list.add(vector);
        }
        return new DetailPage(total, list);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<EmuTrain> searchList(Map<String, Object> conditionMap) {
        DefaultTableModel tableModel = (DefaultTableModel) this.searchTableModel(conditionMap);
        Vector<?> dataVector = tableModel.getDataVector();
        List<EmuTrain> emuList = new ArrayList<>();
        dataVector.forEach(dv -> emuList.add(new EmuTrain((Vector<String>) dv)));
        return emuList;
    }

    @Override
    public TableModel searchTableModel(Map<String, Object> conditionMap) {
        DefaultTableModel tableModel = new DefaultTableModel(Constants.RESULT_TABLE_COLUMN_NAME, 0) {
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                //value cannot be reset
            }
        };
        //获取数据
        DetailPage[] pages = getDetailPages(conditionMap);
        if (pages.length == 0) return tableModel;
        //筛选条件数组 condition array
        String[] ca = new String[6];
        ca[0] = (String) conditionMap.get(MODEL);
        ca[1] = (String) conditionMap.get(NUMBER);
        ca[2] = (String) conditionMap.get(BUREAU);
        ca[3] = (String) conditionMap.get(DEPARTMENT);
        ca[4] = (String) conditionMap.get(PLANT);
        for (DetailPage detailPage : pages) {
            List<Vector<String>> content = detailPage.content;
            outer:
            for (Vector<String> vector : content) {
                for (int i = 0; i < ca.length; i++) {
                    if (this.isConditionNotMatched(vector.get(i), ca[i])) {
                        continue outer;
                    }
                }
                tableModel.addRow(vector);
            }
        }
        return tableModel;
    }

    @Override
    public void clearCache() {
        cache.invalidateAll();
    }

    public boolean isConditionMatched(String val, String condition) {
        return condition == null || "".equals(condition) || condition.equals(val);
    }

    public boolean isConditionNotMatched(String val, String condition) {
        return !isConditionMatched(val, condition);
    }

}
