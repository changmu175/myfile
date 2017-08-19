package com.xdja.domain_mainframe.model;

import java.io.File;

/**
 * Created by ldy on 16/4/22.
 */
public class ImgCompressResult {
    private File imgFile;
    private File thumbnailImgFile;

    public ImgCompressResult(File imgFile, File thumbnailImgFile) {
        this.imgFile = imgFile;
        this.thumbnailImgFile = thumbnailImgFile;
    }

    public File getImgFile() {
        return imgFile;
    }

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }

    public File getThumbnailImgFile() {
        return thumbnailImgFile;
    }

    public void setThumbnailImgFile(File thumbnailImgFile) {
        this.thumbnailImgFile = thumbnailImgFile;
    }
}
