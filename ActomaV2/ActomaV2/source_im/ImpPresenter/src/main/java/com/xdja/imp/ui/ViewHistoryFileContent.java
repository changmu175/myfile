package com.xdja.imp.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xdja.contactopproxy.Bean.ContactInfo;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.frame.mvp.view.AdapterSuperView;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.presenter.command.IHistoryFileListAdapterCommand;
import com.xdja.imp.receiver.NetworkStateBroadcastReceiver;
import com.xdja.imp.util.DateUtils;
import com.xdja.imp.util.FileSizeUtils;
import com.xdja.imp.util.HistoryFileUtils;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：xdjaxa
 * 创建时间：2016/12/14 21:08
 * 修改人：xdjaxa
 * 修改时间：2016/12/14 21:08
 * 修改备注：
 */
public class ViewHistoryFileContent extends AdapterSuperView<IHistoryFileListAdapterCommand,TalkMessageBean>
        implements AdapterVu<IHistoryFileListAdapterCommand, TalkMessageBean> {

    private TextView mFileNameTv;

    private ImageView mFileIconIv;

    private TextView mFileSizeTv;

    private TextView mFileDateTv;

    private CheckBox mFileSelectCb;

    private TextView mFileDownloadBtn;

    private ProgressBar mFileDownloadPb;

    private LinearLayout mParentLayout;
    //add by zya ,add out of date for file and come from,20161221
    private TextView mFileOutDateTv;

    private TextView mFileFromTv;

    private LinearLayout mFileDateLayout;
    //end by zya ,20161221
    public ViewHistoryFileContent() {
        super();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.history_filelist_item;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        if(view != null) {
            mParentLayout = (LinearLayout) view.findViewById(R.id.history_file_content_layout);
            mFileNameTv = (TextView) view.findViewById(R.id.tv_file_title);
            mFileIconIv = (ImageView) view.findViewById(R.id.img_file_icon);
            mFileSizeTv = (TextView) view.findViewById(R.id.tv_file_desc);
            mFileDateTv = (TextView) view.findViewById(R.id.tv_file_date);
            mFileSelectCb = (CheckBox) view.findViewById(R.id.history_file_select);
            mFileDownloadBtn = (TextView) view.findViewById(R.id.history_file_op);
            mFileDownloadPb = (ProgressBar) view.findViewById(R.id.pb_file_download);
            //add by zya ,add out of date for file and come from,20161221
            mFileOutDateTv = (TextView) view.findViewById(R.id.tv_file_out_date);
            mFileFromTv = (TextView) view.findViewById(R.id.tv_file_from);
            mFileDateLayout = (LinearLayout) view.findViewById(R.id.layout_history_date_from);
            //end by zya ,20161221
        }
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean dataSource) {
        super.bindDataSource(position, dataSource);
        initView(position);
    }

    private void initView(final int groupPosition) {
        if (dataSource != null) {
            final FileInfo fileInfo = dataSource.getFileInfo();

            int resId = HistoryFileUtils.getIconWithSuffix(dataSource);
            mFileIconIv.setImageResource(resId);

            mFileNameTv.setText(fileInfo.getFileName());
            mFileSizeTv.setText(FileSizeUtils.FormetFileSize(fileInfo.getFileSize()));
            mFileDateTv.setText(DateUtils.displayShowTime(getActivity(),dataSource.getShowTime()));
            mFileOutDateTv.setText(String.format(getStringRes(R.string.history_file_outdate),
                    DateUtils.stringOfOverdue(getActivity(),dataSource.getShowTime())));

            isDownload(!dataSource.isSelect());
            clickBtnState(dataSource.getFileInfo().getFileState());

            mFileDownloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FileInfo fInfo = dataSource.getFileInfo();
                    int fileState = fInfo.getFileState();

                    if(NetworkStateBroadcastReceiver.getState() != NetworkStateBroadcastReceiver.NORMAL){

                        if(fileState == ConstDef.PAUSE || fileState == ConstDef.INACTIVE){
                            Toast.makeText(getActivity(),getStringRes(R.string.network_disabled),Toast.LENGTH_SHORT).show();
                            return ;
                        }
                    }


                    if(fileState == ConstDef.LOADING){
                        fInfo.setFileState(ConstDef.PAUSE);
                    } else if(fileState == ConstDef.PAUSE){
                        fInfo.setFileState(ConstDef.LOADING);
                    } else if(fileState == ConstDef.INACTIVE){
                        //if(!isOpen()){
                        fInfo.setFileState(ConstDef.LOADING);
                        //}
                    } else if(fileState == ConstDef.FAIL){
                        fInfo.setFileState(ConstDef.INACTIVE);
                    }

                    clickBtnState(dataSource.getFileInfo().getFileState());
                    getCommand().clickToDownloadOfOpen(dataSource);
                }
            });

            mFileSelectCb.setChecked(dataSource.isCheck());
            mFileSelectCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    dataSource.setCheck(isChecked);
                    getCommand().toRefreshSelectHint();
                }
            });
            mParentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    getCommand().longClickOnItem(groupPosition,dataSource);
                    return true;
                }
            });

            //add by zya 七天内的文件显示来自哪里,20161221
            setSenderName();
            //end by zya
        }
    }

    private void isDownload(boolean isDownload){
        mFileSelectCb.setVisibility(isDownload ? View.GONE : View.VISIBLE);
        mFileDownloadBtn.setVisibility(isDownload ? View.VISIBLE : View.GONE);
        mFileDownloadPb.setVisibility(isDownload ? View.VISIBLE : View.GONE);
    }

    private boolean isOpen(){
        FileInfo fileInfo = dataSource.getFileInfo();
        return HistoryFileUtils.isFileExist(fileInfo.getFilePath());
    }

    private void clickBtnState(int state){
        Drawable btnDrawable = null;
        if(dataSource.isMine()){
            //作为发送方
            mFileDateLayout.setVisibility(View.VISIBLE);
            mFileOutDateTv.setVisibility(View.GONE);
            mFileFromTv.setVisibility(View.VISIBLE);
            mFileDownloadPb.setVisibility(View.GONE);
            btnDrawable = getDrawableRes(R.drawable.history_lookup);
        } else {
            //作为接收方
            if(DateUtils.isOverdue(dataSource.getShowTime())){
                mFileDateLayout.setVisibility(View.VISIBLE);
                mFileOutDateTv.setVisibility(View.GONE);
                //add by zya 20161230 fix bug 7712
                mFileDownloadPb.setVisibility(View.GONE);
                //end by zya
                if(isOpen()){
                    //过期查看
                    btnDrawable = getDrawableRes(R.drawable.history_lookup);
                } else {
                    //过期未下载
                    btnDrawable = getDrawableRes(R.drawable.history_download);
                    mFileDownloadBtn.setEnabled(false);
                }
            } else {
                //七天内
                LogUtil.getUtils().d("zhu->UI Content:progress=" + dataSource.getFileInfo().getPercent()
                        + ",state=" + state);
                mFileDownloadBtn.setEnabled(true);
                switch (state) {
                    case ConstDef.INACTIVE:
                        //fix bug 7829 by zya 20170104
                        btnDrawable = getDrawableRes(R.drawable.history_download);
                        mFileDateLayout.setVisibility(View.VISIBLE);
                        mFileDownloadPb.setVisibility(View.GONE);
                        mFileOutDateTv.setVisibility(View.VISIBLE);
                        //end by zya
                        break;
                    case ConstDef.LOADING://下载中
                        btnDrawable = getDrawableRes(R.drawable.history_pause);
                        mFileDateLayout.setVisibility(View.GONE);
                        mFileDownloadPb.setVisibility(View.VISIBLE);
                        mFileOutDateTv.setVisibility(View.VISIBLE);
                        break;
                    case ConstDef.DONE://下载完成后变成查看
                        btnDrawable = getDrawableRes(R.drawable.history_lookup);
                        mFileDateLayout.setVisibility(View.VISIBLE);
                        mFileDownloadPb.setVisibility(View.GONE);
                        mFileOutDateTv.setVisibility(View.GONE);
                        break;
                    case ConstDef.PAUSE://暂停
                        btnDrawable = getDrawableRes(R.drawable.history_resume);
                        mFileDateLayout.setVisibility(View.GONE);
                        mFileDownloadPb.setVisibility(View.VISIBLE);
                        mFileOutDateTv.setVisibility(View.VISIBLE);
                        break;
                    case ConstDef.FAIL://下载失败
                        btnDrawable = getDrawableRes(R.drawable.history_download);
                        mFileDateLayout.setVisibility(View.VISIBLE);
                        mFileDownloadPb.setVisibility(View.GONE);
                        mFileOutDateTv.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

        mFileDownloadBtn.setBackground(btnDrawable);
        mFileDownloadPb.setProgress(dataSource.getFileInfo().getPercent());
    }

    /**
     * 设置文件来自哪里
     */
    private void setSenderName(){
		//fix bug 7707 by zya 20161230
        ContactInfo info;
        if (dataSource.isGroupMsg()) {
            info = getCommand().getGroupMemberInfo(dataSource.getTo(), dataSource.getFrom());
        } else {
            info = getCommand().getContactInfo(dataSource.isMine() ? dataSource.getTo() : dataSource.getFrom());
        }
        String senderName = String.format(getStringRes(dataSource.isMine() ? R.string.history_file_come_to :
                R.string.history_file_come_from),info.getName());
        mFileFromTv.setText(senderName);
		//end by zya
    }
}
