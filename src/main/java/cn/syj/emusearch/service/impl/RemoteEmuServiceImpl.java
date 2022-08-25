package cn.syj.emusearch.service.impl;

import cn.syj.emusearch.entity.EmuTrain;
import cn.syj.emusearch.service.EmuService;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import static cn.syj.emusearch.constant.Constants.*;

/**
 * @author syj
 **/
public class RemoteEmuServiceImpl implements EmuService {

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

    @Override
    public List<EmuTrain> searchEmuList(Map<String, Object> conditionMap) {
        conditionMap.forEach((k, v) -> {
            if (null != v && ("".equals(v) || String.valueOf(v).contains("全部"))) {
                conditionMap.put(k, null);
            }
        });
        List<EmuTrain> emuList = Collections.emptyList();
        Object keyword = null;
        String type = null;
        for (String t : types) {
            if (null != (keyword = conditionMap.get(t))) {
                type = t;
                break;
            }
        }
        if (null == keyword) return emuList;
        String queryUrl = String.format(PASS_SEARCH_PARAM_FORMAT, this.remoteServerUrl, type, keyword, 1);
        System.out.println("查询URL=> " + queryUrl);
        try {
            Document doc = Jsoup.parse(new URL(queryUrl), this.timeoutMills);
            Iterator<TextNode> iterator = doc.getAllElements().textNodes().iterator();
            String sumText = null;
            while (iterator.hasNext() && sumText == null) {
                String text;
                if ((text = iterator.next().text()).contains("共有符合条件的记录"))
                    sumText = text;
            }
            if (sumText == null) return emuList;
            //总数
            int total = Integer.parseInt(Pattern.compile("[^0-9]").matcher(sumText).replaceAll("").trim());
            //分页
            int page = (int) Math.ceil((double) total / 50D);
            Document[] documents = new Document[page];
            documents[0] = doc;
            //查询后面的几页
            for (int i = 2; i <= page; i++) {
                documents[i - 1] = Jsoup.parse(new URL(String.format(PASS_SEARCH_PARAM_FORMAT, this.remoteServerUrl, type, keyword, i)), this.timeoutMills);
            }
            emuList = new ArrayList<>(total);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        conditionMap.forEach((k, v) -> {
            if (!StringUtil.isBlank((String) v)) {
                System.out.println(k + ":" + v);
            }
        });
        System.out.println("total:" + emuList.size());
        return emuList;
    }

    public boolean isConditionMatched(String val, String condition) {
        return condition == null || "".equals(condition) || condition.equals(val);
    }

    public boolean isConditionNotMatched(String val, String condition) {
        return !isConditionMatched(val, condition);
    }

}
