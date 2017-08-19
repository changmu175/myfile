package com.xdja.imp.presenter.holder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.comm.uitl.ImageLoader;
import com.xdja.imp.R;
import com.xdja.imp.data.utils.IMFileUtils;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.util.DateUtils;
import com.xdja.imp.util.FileSizeUtils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * <p>Author: leiliangliang   </br>
 * <p>Date: 2016/12/6 10:46   </br>
 * <p>Package: com.xdja.imp.presenter.holder</br>
 * <p>Description:            </br>
 */
public class FileListViewHolder extends ViewHolder<LocalFileInfo> {

    private final Context context;

    /**
     * 文件展示图标
     */
    private ImageView mFileIconImg;

    /**
     * 标题
     */
    private TextView mFileTitleTv;

    /**
     * 文件大小
     */
    private TextView mFileSizeTv;

    /**
     * 日期
     */
    private TextView mFileDateTv;

    /**
     * 文件选择按钮
     */
    private CheckBox mSelectChx;

    public FileListViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
    }

    @Override
    protected void bindViews(View itemView) {
        mFileIconImg = (ImageView) itemView.findViewById(R.id.img_file_icon);
        mFileTitleTv = (TextView) itemView.findViewById(R.id.tv_file_title);
        mFileSizeTv = (TextView) itemView.findViewById(R.id.tv_file_size);
        mFileDateTv = (TextView) itemView.findViewById(R.id.tv_file_date);
        mSelectChx = (CheckBox) itemView.findViewById(R.id.chx_select);
    }

    @Override
    public void bindData(LocalFileInfo dataSource) {
        if (dataSource != null) {
            mFileTitleTv.setText(dataSource.getFileName());
            mFileSizeTv.setText(FileSizeUtils.FormetFileSize(dataSource.getFileSize()));
            mFileDateTv.setText(DateUtils.convertFileModifyDate(dataSource.getModifiedDate()));
            mSelectChx.setChecked(dataSource.isSelected());
            if (dataSource.getFileType() == ConstDef.TYPE_PHOTO) {
                /*Glide.with(context)
                        .load(dataSource.getFilePath())
                        .placeholder(R.drawable.ic_jpg)
                        .error(R.drawable.ic_jpg)
                        .centerCrop()
                        .crossFade()
                        .into(mFileIconImg);*/
                ImageLoader.getInstance().crateBuilder()
                        .load(dataSource.getFilePath())
                        .preLoad(360, 360)
                        .error(R.drawable.ic_jpg)
                        .centerCrop()
                        .into(mFileIconImg)
                        .build();
            } else {
                if (dataSource.getFileType() == 0) { //类型不正确，由filePath去匹配
                    getIconResIdByPath(dataSource.getFilePath(), dataSource.getFileSize());
                } else {
                    mFileIconImg.setImageResource(getFileIconResId(dataSource.getFileType()));
                }
            }
        }
    }

    /**
     * 根据文件类型，获取对应的icon
     *
     * @param fileType
     * @return
     */
    private int getFileIconResId(int fileType) {
        int resId;
        switch (fileType) {
            case ConstDef.TYPE_VOICE:
                resId = R.drawable.ic_music;
                break;
            case ConstDef.TYPE_VIDEO:
                resId = R.drawable.ic_video;
                break;
            case ConstDef.TYPE_PHOTO:
                resId = R.drawable.ic_jpg;
                break;
            case ConstDef.TYPE_WORD:
                resId = R.drawable.ic_doc;
                break;
            case ConstDef.TYPE_TXT:
                resId = R.drawable.ic_text;
                break;
            case ConstDef.TYPE_EXCEL:
                resId = R.drawable.ic_excel;
                break;
            case ConstDef.TYPE_PDF:
                resId = R.drawable.ic_pdf;
                break;
            case ConstDef.TYPE_PPT:
                resId = R.drawable.ic_ppt;
                break;
            case ConstDef.TYPE_APK:
                resId = R.drawable.ic_apk;
                break;
            case ConstDef.TYPE_ZIP:
                resId = R.drawable.ic_others;
                break;
            default:
                resId = R.drawable.ic_others;
        }
        return resId;
    }

    /**
     * 根据文件后缀来匹配文件对应的图标（存在耗时，需要异步来实现）
     *
     * @param filePath
     * @return
     */
    private void getIconResIdByPath(final String filePath, final long fileSize) {
        Observable.just(filePath)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<String, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(String path) {
                        return Observable.just(IMFileUtils.getFileTypeFromFilepath(path));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (integer == ConstDef.TYPE_PHOTO) {
                            ImageLoader.getInstance().crateBuilder()
                                    .load(filePath)
                                    .preLoad(360, 360)
                                    .error(R.drawable.ic_jpg)
                                    .centerCrop()
                                    .into(mFileIconImg)
                                    .build();

                            /*Glide.with(context)
                                    .load(filePath)
                                    .placeholder(R.drawable.ic_jpg)
                                    .error(R.drawable.ic_jpg)
                                    .centerCrop()
                                    .into(mFileIconImg);*/
                        } else {
                            mFileIconImg.setImageResource(getFileIconResId(integer));
                        }
                    }
                });
    }
}
