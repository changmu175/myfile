package com.xdja.comm.cust;

/**
 * Created by gbc on 2017/2/20.
 * 此文件内部定义与BuildConfig.FLAVOR 对应
 * 若指定版本引入不同功能，需要控制，在此文件中以版本信息控制
 */
public class CustInfo {
    /**
     * 通用版本标识
     */
    public static final String VERSION_COMMON = "common";

    /**
     * 国际化版本标识
     */
    public static final String VERSION_INTER = "inter";

    /**
     * 浙江电信版本标识
     */
    public static final String VERSION_TELCOM = "telcom";

    private static String currentFlavor = "";

    public static void setCurrentFlavor(String flavor) {
        currentFlavor = flavor;
    }

    public static String getCurrentFlavor() {
        return currentFlavor;
    }

    public static boolean isInter() {
        return VERSION_INTER.equalsIgnoreCase(getCurrentFlavor());
    }

    public static boolean isTelcom() {
        return VERSION_TELCOM.equalsIgnoreCase(getCurrentFlavor());
    }

    public static boolean isCustom() {
        return isInter() || isTelcom();
    }

}
