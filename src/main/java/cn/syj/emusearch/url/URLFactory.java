package cn.syj.emusearch.url;

import cn.syj.emusearch.constant.Constants;

/**
 * @program: emusearch
 * @ClassName URLFactory
 * @description:
 * @author: syj
 * @create: 2021-02-24 16:39
 **/
public class URLFactory {

    public static String searchByModel(String model) {
        return Constants.BASE_URL + Constants.MODEL + model;
    }

    public static String searchByBureau(String bureau) {
        return Constants.BASE_URL + Constants.BUREAU + bureau;
    }

    public static String searchByDepartment(String department) {
        return Constants.BASE_URL + Constants.DEPARTMENT + department;
    }

    public static String searchByNumber(String number) {
        return Constants.BASE_URL + Constants.NUMBER + number;
    }

    public static String searchByPlant(String plant) {
        return Constants.BASE_URL + Constants.PLANT + plant;
    }
}
