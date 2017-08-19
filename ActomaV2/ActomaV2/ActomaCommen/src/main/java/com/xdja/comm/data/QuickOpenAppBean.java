package com.xdja.comm.data;

/**
 * Created by geyao on 2015/11/9.
 * 快速开启第三方应用对象
 */
public class QuickOpenAppBean {
    /**
     * 选择显示
     */
    public static final int TYPT_SHOW = 0;
    /**
     * 未选择显示
     */
    public static final int TYPT_NOT_SHOW = 1;
    /**
     * 标题
     */
    public static final int TYPT_TITLE = 2;
    /**
     * 第三方应用包名
     */
    private String packageName;
    /**
     * 第三方应用名称
     */
    private String appName;
    /**
     * 第三方应用状态
     */
    private int type;
    /**
     * 排序
     */
    private int sort;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "QuickOpenAppBean{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", type=" + type +
                ", sort=" + sort +
                '}';
    }
}
