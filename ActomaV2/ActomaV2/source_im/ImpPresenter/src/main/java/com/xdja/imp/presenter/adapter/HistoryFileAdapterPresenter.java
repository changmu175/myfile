package com.xdja.imp.presenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.contactopproxy.ContactService;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.data.cache.UserCache;
import com.xdja.imp.data.eventbus.BusProvider;
import com.xdja.imp.data.repository.im.IMProxyEvent;
import com.xdja.imp.domain.interactor.def.DownloadFile;
import com.xdja.imp.domain.interactor.def.PauseReceiveFile;
import com.xdja.imp.domain.interactor.def.ResumeReceiveFile;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.HistoryFileCategory;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.handler.OkSubscriber;
import com.xdja.imp.presenter.command.IHistoryFileListAdapterCommand;
import com.xdja.imp.ui.ViewHistoryFileContent;
import com.xdja.imp.ui.ViewHistoryFileTitle;
import com.xdja.imp.util.HistoryFileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/14 21:01
 * 修改人：xdjaxa
 * 修改时间：2016/12/14 21:01
 * 修改备注：
 */
public class HistoryFileAdapterPresenter extends BaseFileItemAdapterPresenter<IHistoryFileListAdapterCommand,TalkMessageBean>
        implements IHistoryFileListAdapterCommand{

    private ExpandableListView listView;

    @Inject
    Lazy<DownloadFile> downloadFile;

    @Inject
    Lazy<PauseReceiveFile> pauseReceiveFile;

    @Inject
    Lazy<ResumeReceiveFile> resumeReceiveFile;

    @Inject
    BusProvider busProvider;

    @Inject
    Lazy<ContactService> contactService;

    private Activity mActivity;

    private final UserCache userCache;

    public HistoryFileAdapterPresenter(Context context, BusProvider busProvider,
                                       Map<HistoryFileCategory,List<TalkMessageBean>> datas,
                                       UserCache userCache){
        mContext = context;
        mDatas = datas;
        this.busProvider = busProvider;
        this.userCache = userCache;
        this.busProvider.register(this);

    }

    @Override
    public List<Class<? extends AdapterVu<IHistoryFileListAdapterCommand, TalkMessageBean>>> getVuClasses() {
        if(mVuClass == null){
            mVuClass = new ArrayList<>();
            mVuClass.add(ViewHistoryFileContent.class);
        }

        return mVuClass;
    }

    @Override
    public List<Class<? extends AdapterVu<IHistoryFileListAdapterCommand, String>>> getGroupVuClasses() {
        if(mGroupVuClass == null) {
            mGroupVuClass = new ArrayList<>();
            mGroupVuClass.add(ViewHistoryFileTitle.class);
        }
        return mGroupVuClass;
    }

    @Override
    public IHistoryFileListAdapterCommand getCommand() {
        return this;
    }

    @Override
    public void notifyDataSetChanged() {
        mTitles = new ArrayList<>(mDatas.keySet());
        Collections.sort(mTitles);
        super.notifyDataSetChanged();
    }

    public void updateUI(boolean isShow){
        for(Map.Entry<HistoryFileCategory,List<TalkMessageBean>> entry : mDatas.entrySet()) {
            List<TalkMessageBean> tempLists = entry.getValue();
            for(TalkMessageBean dataSource : tempLists){
                dataSource.setSelect(isShow);
                if(isShow){
                    dataSource.setCheck(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void clickToDownloadOfOpen(TalkMessageBean bean) {
        FileInfo fileInfo = bean.getFileInfo();

        boolean isOpen = HistoryFileUtils.isFileExist(fileInfo.getFilePath());

        if(!isOpen && (bean.isMine() || fileInfo.getFileState() == ConstDef.DONE)){
            //发送的文件如果不存在，提示并返回;
            Toast.makeText(getActivity(),getActivity().getString(R.string.history_send_file_not_exist),Toast.LENGTH_SHORT).show();
            return;
        }

        List<FileInfo> fileInfos = new ArrayList<>();
        fileInfos.add(fileInfo);

        int state = fileInfo.getFileState();

        if(state == ConstDef.PAUSE){
            pauseReceiveFile.get().pause(fileInfo).execute(new OkSubscriber<Integer>(null) {
                @Override
                public void onNext(Integer integer) {
                    super.onNext(integer);
                    if (integer == 0) {
                        LogUtil.getUtils().d("zhu->暂停下载文件");
                    }
                }
            });
        } else if(state == ConstDef.LOADING && fileInfo.getPercent() > 0){
            resumeReceiveFile.get().resume(fileInfo).execute(new OkSubscriber<Integer>(null) {
                @Override
                public void onNext(Integer integer) {
                    super.onNext(integer);
                    if (integer == 0) {
                        LogUtil.getUtils().d("zhu->恢复下载文件");
                    }
                }
            });
        } else if(state == ConstDef.LOADING && fileInfo.getPercent() == 0){
            downloadFile.get().downLoad(fileInfos).execute(new OkSubscriber<Integer>(null) {
                @Override
                public void onNext(Integer integer) {
                    super.onNext(integer);
                    if (integer == 0) {
                        LogUtil.getUtils().d("zhu->开始下载文件");
                    }
                }
            });
        } else {
            if(isOpen){
                HistoryFileUtils.intentBuilder(getActivity(),bean.getFileInfo().getFilePath(),
                        bean.getFileInfo().getSuffix());
            } else {
                Toast.makeText(getActivity(),getActivity().getString(R.string.history_send_file_not_exist),Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void refreshItem(long msgId) {
        updateItem(getPosition(msgId),msgId);
    }

    private int getPosition(long msgId) {
        TalkMessageBean msgBean = getTalkMessageBean(msgId);

        int groupPosition = -1;
        if(msgBean != null){
            String categoryId = msgBean.getCategoryId();
            HistoryFileCategory category = new HistoryFileCategory();
            category.setCategoryId(categoryId);
            groupPosition = mTitles.indexOf(category);
        }
        return groupPosition;
    }

    private TalkMessageBean getTalkMessageBean(long msgId){
        TalkMessageBean msgBean = null;
        for(Map.Entry<HistoryFileCategory,List<TalkMessageBean>> entry : mDatas.entrySet()){
            for(TalkMessageBean bean : entry.getValue()){
                if(msgId == bean.get_id()){
                    msgBean = bean;
                }
            }
        }
        return msgBean;
    }

    @Override
    public void updateItem(int position,long msgId) {
        if(position < 0){
            return ;
        }
        //fix bug 7809 by zya 20170104
        HistoryFileCategory cate = mTitles.get(position);

        List<TalkMessageBean> beans = mDatas.get(cate);
        TalkMessageBean tBean = new TalkMessageBean();
        tBean.set_id(msgId);

        int childPosition = beans.indexOf(tBean);
        if(childPosition > -1){
            int childCount = listView.getChildCount();
            //匹配到子View
            if(position == 0 && listView.isGroupExpanded(0)){
                int firstVisiblePos = listView.getFirstVisiblePosition();
                if(childPosition + 1 < firstVisiblePos){
                    return ;
                }

                int aPos = childPosition - firstVisiblePos  + 1;
                LogUtil.getUtils().d("zhu->childPos:" + childPosition + ",firstPos:" + firstVisiblePos + ",aPos:" + aPos);

                View childView = listView.getChildAt(aPos);
                if(childView != null) {
                    ProgressBar pb = (ProgressBar) childView.findViewById(R.id.pb_file_download);
                    pb.setProgress(beans.get(childPosition).getFileInfo().getPercent());
                }
            }
        }
        /*if (listView != null) {
            notifyDataSetChanged();
        }*/
        //end by zya
    }

    @Override
    public void toRefreshSelectHint() {
        IMProxyEvent.HistoryRefreshSelectHintEvent event = new IMProxyEvent.HistoryRefreshSelectHintEvent();
        busProvider.post(event);
    }

    @Override
    public void longClickOnItem(int groupPosition,TalkMessageBean bean) {
        if(mItemLongClickListener != null){
            mItemLongClickListener.itemLongClick(groupPosition,bean);
        }
    }

    @Override
    public ContactInfo getContactInfo(String account) {
        return contactService.get().getContactInfo(account);
    }

    @Override
    public ContactInfo getGroupMemberInfo(String groupId, String account) {
        return contactService.get().GetGroupMemberInfo(groupId,account);
    }

    public void setListView(ExpandableListView listView){
        this.listView = listView;
    }

    public void setActivity(Activity activity){
        mActivity = activity;
    }

    @Override
    public Activity getActivity() {
        return mActivity;
    }


    private List<Class<? extends AdapterVu<IHistoryFileListAdapterCommand,String>>> mGroupVuClass = null;

    private List<Class<? extends AdapterVu<IHistoryFileListAdapterCommand, TalkMessageBean>>> mVuClass = null;

    private ItemLongClickListener mItemLongClickListener;

    public interface ItemLongClickListener{
        void itemLongClick(int groupPosition,TalkMessageBean bean);
    }

    public void setItemLongClickListener(ItemLongClickListener listener){
        mItemLongClickListener = listener;
    }

    public void onDestroy(){
        if (null != busProvider) {
            //注销事件总线回调
            busProvider.unregister(this);
        }
    }
}

