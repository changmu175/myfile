package com.xdja.imp.presenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;
import com.xdja.comm.uitl.ImageLoader;
import com.xdja.imp.R;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.LocalFileInfo;
import com.xdja.imp.frame.imp.presenter.IMActivityPresenter;
import com.xdja.imp.presenter.adapter.LocalFileListAdapter;
import com.xdja.imp.presenter.command.IFileExplorerCommand;
import com.xdja.imp.presenter.fragment.LastFileListPresenter;
import com.xdja.imp.presenter.fragment.LocalFileListPresenter;
import com.xdja.imp.ui.ViewFileExplorer;
import com.xdja.imp.ui.vu.IFileExplorerVu;
import com.xdja.imp.util.FileInfoCollection;
import com.xdja.imp.util.XToast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * <p>Author: leiliangliang</p>
 * <p>Date: 2016/11/29 10:40</p>
 * <p>Package: com.xdja.imp.presenter.activity</p>
 * <p>Description: IM文件浏览器</p>
 */
public class FileExplorerPresenter extends IMActivityPresenter<IFileExplorerCommand, IFileExplorerVu>
        implements IFileExplorerCommand {

    /**
     * 最大可发送文件大小
     */
    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;

    //事件总线
    @Inject
    BusProvider busProvider;

    private LocalFileListAdapter mAdapter;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        super.onBindView(savedInstanceState);

        //初始化注入
        useCaseComponent.inject(this);

        //初始化事件总线
        busProvider.register(this);

        List<Fragment> fragments = new ArrayList<>();
        //最近聊天文件fragment
        fragments.add(new LastFileListPresenter());
        //本地文件fragment
        fragments.add(new LocalFileListPresenter());

        mAdapter = buildAdapter(fragments);

        getVu().setFragmentAdapter(mAdapter);

        getVu().setCurrentSelectedFileSize(0);
    }

    @NonNull
    @Override
    protected Class<? extends IFileExplorerVu> getVuClass() {
        return ViewFileExplorer.class;
    }

    @NonNull
    @Override
    protected IFileExplorerCommand getCommand() {
        return this;
    }


    private LocalFileListAdapter buildAdapter(final List<Fragment> fragments) {
        return new LocalFileListAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        };
    }

    @Override
    public void onLastFileTabClick(View view) {
        getVu().setViewPagerCurrentItem(0);
    }

    @Override
    public void onLocalFileTabClick(View view) {
        getVu().setViewPagerCurrentItem(1);
    }

    @Override
    public void onSendBtnClick(View view) {

        getVu().setProgressBarVisibility(View.VISIBLE);
        //发送文件之前进行文件大小校验
        Observable.from(FileInfoCollection.getInstance().getAllSelectFiles())
                .flatMap(new Func1<LocalFileInfo, Observable<LocalFileInfo>>() {
                    @Override
                    public Observable<LocalFileInfo> call(LocalFileInfo localFileInfo) {
                        long fileSize = localFileInfo.getFileSize();
                        if (fileSize > MAX_FILE_SIZE || fileSize < 0) {
                            return Observable.error(new IllegalArgumentException("file is too larger!"));
                        }
                        return Observable.just(localFileInfo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LocalFileInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getVu().setProgressBarVisibility(View.GONE);
                        new XToast(FileExplorerPresenter.this).display(R.string.file_is_too_larger);
                    }

                    @Override
                    public void onNext(LocalFileInfo localFileInfo) {
                        getVu().setProgressBarVisibility(View.GONE);
                        setResult();
                    }
        });

    }

    /**
     * 返回发送文件
     */
    private void setResult() {
        //发送文件
        Intent intent = new Intent();
        //数据绑定
        Bundle bundle = new Bundle();
        ArrayList<LocalFileInfo> bundleList = new ArrayList<>();
        bundleList.addAll(FileInfoCollection.getInstance().getAllSelectFiles());
        bundle.putParcelableArrayList(ConstDef.TAG_SELECTFILE, bundleList);
        //添加数据到Intent
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 选择文件相关提示信息设置更新
     *
     * @param event
     */
    @Subscribe
    public void onRefreashFileSelectStatus(IMProxyEvent.FileSelectedEvent event) {
        //更新已选文件大小
        getVu().setCurrentSelectedFileSize(FileInfoCollection.getInstance().getSelectedFileSize());
        //更新已选文件个数
        getVu().setCurrentSelectedFileCount(FileInfoCollection.getInstance().getSelectedFileCount());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (busProvider != null) {
            busProvider.unregister(this);
        }

        Glide.get(this).clearMemory();
        ImageLoader.getInstance().clearCache();
        //清除缓存相关
        FileInfoCollection.getInstance().clearCache();
    }
}
