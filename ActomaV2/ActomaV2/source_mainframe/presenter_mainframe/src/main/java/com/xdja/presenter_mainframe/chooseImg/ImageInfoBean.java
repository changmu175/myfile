package com.xdja.presenter_mainframe.chooseImg;

/**
 * Created by geyao on 2015/7/10.
 * 图片信息bean
 */
public class ImageInfoBean {
    /**
     * 图片id
     */
    private int image_id;
    /**
     * 图片地址
     */
    private String image_url;

    private long file_size;

    public ImageInfoBean(int image_id, String image_url , long file_size) {
        this.image_id = image_id;
        this.image_url = image_url;
        this.file_size = file_size;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
