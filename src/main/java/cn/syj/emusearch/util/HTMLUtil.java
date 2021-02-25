package cn.syj.emusearch.util;


import cn.syj.emusearch.model.EMU;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @program: emusearch
 * @ClassName HTMLUtil
 * @description:
 * @author: syj
 * @create: 2021-02-24 12:54
 **/
public class HTMLUtil {

    public static List<EMU> getEMUListFromInternet(String url) {
        List<EMU> emuList = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(new URL(url), 10000);
            String numText = null;
            for (TextNode textNode : doc.getAllElements().textNodes()) {
                if (textNode.text().contains("共有符合条件的记录")) {
                    numText = textNode.text().trim();
                    break;
                }
            }
            if (numText == null) return emuList;
            System.out.println(numText);
            int emuNum = Integer.parseInt(Pattern.compile("[^0-9]").matcher(numText).replaceAll("").trim());
            int page = (int) Math.ceil((double) emuNum / 50D);
            System.out.println("page " + page);
            putEMUInfoFromDocToList(doc, emuList);
            for (int i = 2; i <= page; i++) {
                Document doc2 = Jsoup.parse(new URL(url + "&pagenum=" + page), 10000);
                putEMUInfoFromDocToList(doc2, emuList);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return emuList;
    }


    private static void putEMUInfoFromDocToList(Document doc, List<EMU> emuList) {
        Elements es = doc.getElementsByTag("table");
        Element emuInfoTable = es.get(1);
        Elements emuInfoList = emuInfoTable.getElementsByTag("tr");
        for (int i = 1; i <= emuInfoList.size() - 1; i++) {
            Element emuInfo = emuInfoList.get(i);
            Elements e = emuInfo.getElementsByTag("td");
            EMU emu = new EMU(e.get(0).text(), e.get(1).text(), e.get(2).text(), e.get(3).text(), e.get(4).text(), e.get(5).text());
            emuList.add(emu);
        }
    }
}
