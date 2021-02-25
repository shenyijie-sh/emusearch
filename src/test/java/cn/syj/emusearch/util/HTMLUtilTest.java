package cn.syj.emusearch.util;

import cn.syj.emusearch.url.URLFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class HTMLUtilTest {

    @Test
    void getEMUListFromInternet() {
        HTMLUtil.getEMUListFromInternet(URLFactory.searchByModel("CR400BF-B")).forEach(emu -> System.out.println(emu.toString()));
    }

    @AfterEach
    public void cleanup() {
        System.out.println("Finish testing");
    }
}