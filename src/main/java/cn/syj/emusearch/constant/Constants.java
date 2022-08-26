package cn.syj.emusearch.constant;

import java.util.Vector;

/**
 * @author syj
 * 2021-02-24 16:31
 **/
public class Constants {

    public static final String PASS_SEARCH_URL = "http://www.passearch.info/emu.php";

    public final static String MODEL = "model";

    public final static String NUMBER = "number";

    public final static String BUREAU = "bureau";

    public final static String DEPARTMENT = "department";

    public final static String PLANT = "plant";

    public static final String PASS_SEARCH_PARAM_FORMAT = "%s?type=%s&keyword=%s&pagenum=%d";

    public static final Vector<String> RESULT_TABLE_COLUMN_NAME;

    static {
        RESULT_TABLE_COLUMN_NAME = new Vector<>();
        RESULT_TABLE_COLUMN_NAME.add("型号");
        RESULT_TABLE_COLUMN_NAME.add("编号");
        RESULT_TABLE_COLUMN_NAME.add("路局");
        RESULT_TABLE_COLUMN_NAME.add("主机厂");
        RESULT_TABLE_COLUMN_NAME.add("动车所");
        RESULT_TABLE_COLUMN_NAME.add("备注");
    }

}
