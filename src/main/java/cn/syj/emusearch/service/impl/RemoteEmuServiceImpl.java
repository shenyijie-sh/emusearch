package cn.syj.emusearch.service.impl;

import cn.syj.emusearch.constant.Constants;
import cn.syj.emusearch.entity.EmuTrain;
import cn.syj.emusearch.service.EmuService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
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
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.syj.emusearch.constant.Constants.*;

/**
 * @author syj
 **/
public class RemoteEmuServiceImpl implements EmuService {

    //页面缓存
    private static final Cache<URL, Document> cache = CacheBuilder.newBuilder()
            .initialCapacity(500)
            //读60秒后销毁
            .expireAfterAccess(300, TimeUnit.SECONDS)
            .removalListener((RemovalListener<URL, Document>) n -> System.out.println("Cache ---[key=" + n.getKey() + "]removed"))
            .build();

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

    private Document[] getDocuments(Map<String, Object> conditionMap) {
        conditionMap.forEach((k, v) -> {
            System.out.println(k + "->" + v);
            if (null != v && ("".equals(v) || String.valueOf(v).contains("全部"))) {
                conditionMap.put(k, null);
            }
        });
        Document[] documents;
        Object keyword = null;
        String type = null;
        for (String t : types) {
            if (null != (keyword = conditionMap.get(t))) {
                type = t;
                break;
            }
        }
        if (null == keyword) return new Document[0];
        try {
            Document doc = this.remoteLoadAndParse(type, keyword, 1);
            Iterator<TextNode> iterator = doc.getAllElements().textNodes().iterator();
            String sumText = null;
            while (iterator.hasNext() && sumText == null) {
                String text;
                if ((text = iterator.next().text()).contains("共有符合条件的记录"))
                    System.out.println(sumText = text);
            }
            if (sumText == null) return new Document[]{};
            //总数
            int total = Integer.parseInt(Pattern.compile("[^0-9]").matcher(sumText).replaceAll("").trim());
            //分页
            int page = (int) Math.ceil((double) total / 50D);
            documents = new Document[page];
            documents[0] = doc;
            //查询后面的几页
            for (int i = 2; i <= page; i++) {
                documents[i - 1] = this.remoteLoadAndParse(type, keyword, i);
            }
            return documents;
        } catch (IOException e) {
            e.printStackTrace();
            return new Document[0];
        }
    }

    /**
     * 远程加载{@link Document}
     *
     * @param type    类型
     * @param keyword 关键字
     * @param page    页面号
     * @return {@link Document}
     * @throws IOException IO异常
     */
    private Document remoteLoadAndParse(String type, Object keyword, int page) throws IOException {
        System.out.println("正在远程加载第" + page + "页");
        URL url = new URL(String.format(PASS_SEARCH_PARAM_FORMAT, this.remoteServerUrl, type, URLEncoder.encode(String.valueOf(keyword), "utf-8"), page));
        System.out.println("加载URL:" + url);
        long startMs = System.currentTimeMillis();
        Document document = cache.getIfPresent(url);
        if (null == document) {
            document = Jsoup.parse(url, this.timeoutMills);
            cache.put(url, document);
        }
        System.out.println("加载用时:" + (System.currentTimeMillis() - startMs) + "毫秒");
        return document;
    }

    @Override
    public List<EmuTrain> searchList(Map<String, Object> conditionMap) {
        Document[] documents = this.getDocuments(conditionMap);
        List<EmuTrain> emuList = new ArrayList<>(documents.length);
        //列出所有条件
        String _model = (String) conditionMap.get(MODEL);
        String _number = (String) conditionMap.get(NUMBER);
        String _bureau = (String) conditionMap.get(BUREAU);
        String _department = (String) conditionMap.get(DEPARTMENT);
        String _plant = (String) conditionMap.get(PLANT);
        for (Document document : documents) {
            Elements tables = document.getElementsByTag("table");
            Element tbl = tables.get(1);
            Elements trs = tbl.getElementsByTag("tr");
            for (int i = 1; i <= trs.size() - 1; i++) {
                Element emuInfo = trs.get(i);
                Elements tds = emuInfo.getElementsByTag("td");
                String model = tds.get(0).text();
                if (isConditionNotMatched(model, _model)) continue;
                String number = tds.get(1).text();
                if (isConditionNotMatched(number, _number)) continue;
                String bureau = tds.get(2).text();
                if (isConditionNotMatched(bureau, _bureau)) continue;
                String department = tds.get(3).text();
                if (isConditionNotMatched(department, _department)) continue;
                String plant = tds.get(4).text();
                if (isConditionNotMatched(plant, _plant)) continue;
                String description = tds.get(5).text();
                EmuTrain emu = new EmuTrain(model, number, bureau, department, plant, description);
                emuList.add(emu);
            }
        }
        return emuList;
    }

    @Override
    public TableModel searchTableModel(Map<String, Object> conditionMap) {
        DefaultTableModel tableModel = new DefaultTableModel(Constants.RESULT_TABLE_COLUMN_NAME, 0){
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                //value cannot be reset
            }
        };
        //获取数据
        Document[] documents = getDocuments(conditionMap);
        //筛选条件数组 condition array
        String[] ca = new String[6];
        ca[0] = (String) conditionMap.get(MODEL);
        ca[1] = (String) conditionMap.get(NUMBER);
        ca[2] = (String) conditionMap.get(BUREAU);
        ca[3] = (String) conditionMap.get(DEPARTMENT);
        ca[4] = (String) conditionMap.get(PLANT);
        //临时数据存放数组
        String[] tmp = new String[6];
        for (Document document : documents) {
            Elements tables = document.getElementsByTag("table");
            Element tbl = tables.get(1);
            Elements trs = tbl.getElementsByTag("tr");
            outer:
            for (int i = 1; i <= trs.size() - 1; i++) {
                Element emuInfo = trs.get(i);
                Elements tds = emuInfo.getElementsByTag("td");
                //数据筛选
                for (int j = 0; j <= 5; j++) {
                    if (this.isConditionNotMatched((tmp[j] = tds.get(j).text()), ca[j])) {
                        //不符合条件的出去
                        continue outer;
                    }
                }
                //把临时存放区的数据转到vector中
                Collection<String> collect = Arrays.stream(tmp).collect(Collectors.toCollection(
                        (Supplier<Collection<String>>) () -> new Vector<>(6))
                );
                tableModel.addRow((Vector<String>) collect);
            }
        }
        return tableModel;
    }

    public boolean isConditionMatched(String val, String condition) {
        return condition == null || "".equals(condition) || condition.equals(val);
    }

    public boolean isConditionNotMatched(String val, String condition) {
        return !isConditionMatched(val, condition);
    }

}
