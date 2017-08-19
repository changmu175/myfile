package com.xdja.comm.data;

/**
 * Created by geyao on 2015/11/4.
 * 快速开启第三方应用设置列表对象
 */
public class QuickOpenThirdAppListBean {
    /**
     * 类型
     */
    private int type;
    /**
     * item非标题数据
     */
    private QuickOpenAppBean quickOpenAppBean;
    /**
     * type=1时数据
     */
    private String title;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public QuickOpenAppBean getQuickOpenAppBean() {
        return quickOpenAppBean;
    }

    public void setQuickOpenAppBean(QuickOpenAppBean quickOpenAppBean) {
        this.quickOpenAppBean = quickOpenAppBean;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "QuickOpenThirdAppListBean{" +
                "type=" + type +
                ", quickOpenAppBean=" + quickOpenAppBean +
                ", title='" + title + '\'' +
                '}';
    }
}
