package com.xdja.presenter_mainframe.chooseImg;

/**
 * Created by geyao on 2015/7/13.
 * 图片详细信息bean
 */
public class ImageRelInfoBean {
    /**
     * 图片id
     */
    private int image_id;
    /**
     * 图片缩略图地址
     */
    private String image_thumb_url;
    /**
     * 图片原图地址
     */
    private String image_original_url;

    private long file_size;

    public ImageRelInfoBean(int image_id, String image_thumb_url, String image_original_url , long file_size) {
        this.image_id = image_id;
        this.image_thumb_url = image_thumb_url;
        this.image_original_url = image_original_url;
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

    public String getImage_thumb_url() {
        return image_thumb_url;
    }

    public void setImage_thumb_url(String image_thumb_url) {
        this.image_thumb_url = image_thumb_url;
    }

    public String getImage_original_url() {
        return image_original_url;
    }

    public void setImage_original_url(String image_original_url) {
        this.image_original_url = image_original_url;
    }
}
