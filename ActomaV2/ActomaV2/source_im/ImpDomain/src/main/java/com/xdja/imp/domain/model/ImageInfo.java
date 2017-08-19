package com.xdja.imp.domain.model;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.domain.model</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/18</p>
 * <p>Time:11:51</p>
 */
public class ImageInfo {

        /*
         * 原图信息
         */
        FileInfo primitive;

        /*
         * 缩略图信息
         */
        FileInfo thumbnail;

        /**
         * @return primitive
         */
        public FileInfo getPrimitive() {
            return primitive;
        }

        /**
         * @param primitive the primitive to add
         */
        public void setPrimitive(FileInfo primitive) {
            this.primitive = primitive;
        }

        /**
         * @return thumbnail
         */
        public FileInfo getThumbnail() {
            return thumbnail;
        }

        /**
         * @param thumbnail the thumbnail to add
         */
        public void setThumbnail(FileInfo thumbnail) {
            this.thumbnail = thumbnail;
        }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "primitive=" + primitive +
                ", thumbnail=" + thumbnail +
                '}';
    }
}
