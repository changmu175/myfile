package com.xdja.imp.ui;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.xdja.imp.R;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.domain.model.FileInfo;
import com.xdja.imp.frame.imp.view.ImpActivitySuperView;
import com.xdja.imp.presenter.command.IChatDetailFileCheckCommand;
import com.xdja.imp.receiver.NetworkStateBroadcastReceiver;
import com.xdja.imp.ui.vu.IFileCheckVu;
import com.xdja.imp.util.BitmapUtils;
import java.text.DecimalFormat;

/**
 * Created by guorong on 2016/11/30.
 */
public class ViewFileCheck extends ImpActivitySuperView<IChatDetailFileCheckCommand>
        implements IFileCheckVu {
    private static String TAG = "FileDownload";
    private static String WORD = "word";
    //显示文件图标
    private ImageView fileLogoIv;
    //文件名称
    private TextView fileNameTv;
    //文件下载进度条
    private ProgressBar progressBar;
    //文件下载大小
    private TextView downloadSizeTv;
    //下载控制按钮
    private Button ctrlBtn;
    //安通+无法打开文件提示
    private TextView tipsTv;

    private FileInfo fileInfo;

    private String fileSizeText;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_filecheck;
    }

    @Override
    protected void injectView() {
        super.injectView();
        View view = getView();
        fileLogoIv = (ImageView) view.findViewById(R.id.file_logo);
        fileNameTv = (TextView) view.findViewById(R.id.file_name);
        progressBar = (ProgressBar) view.findViewById(R.id.download_rate);
        downloadSizeTv = (TextView) view.findViewById(R.id.download_size);
        ctrlBtn = (Button) view.findViewById(R.id.ctrl_btn);
        tipsTv = (TextView)view.findViewById(R.id.tips);
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_BACK;
    }

    @Override
    public String getTitleStr() {
        return getCommand().getToolbarTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_file_load, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.action_submit){
            if(fileInfo.getFileState() != ConstDef.DONE){
                //未下载完成的文件不允许转发
                Toast.makeText(getActivity(), getActivity()
                        .getString(R.string.history_transmit_all_noexist),Toast.LENGTH_SHORT).show();
            }else{
                getCommand().forward();
            }
        }
        return true;
    }

    private void updateProgressBar(int percent) {
        progressBar.setProgress(percent);
    }

    @SuppressLint("SetTextI18n")
    private void setDownloadRateText(int percent) {
        String downloadSizeText = getFileSize((fileInfo.getFileSize() * percent)/100);
        String text = downloadSizeText + "/" + fileSizeText;
        downloadSizeTv.setText(getStringRes(R.string.loading) + "(" + text + ")");
    }

    /**
     * 更新下载进度
     */
    @Override
    public void updateDownloadRate(int percent) {
        updateProgressBar(percent);
        setDownloadRateText(percent);
    }

    /**
     * 文件下载完成
     */
    @Override
    public void downloadFileFinish() {
        progressBar.setVisibility(View.INVISIBLE);
        downloadSizeTv.setVisibility(View.INVISIBLE);
        ctrlBtn.setText(R.string.open_with_other_app);
        tipsTv.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setData() {
        fileInfo = getCommand().getFileInfo();
        fileSizeText = getFileSize(fileInfo.getFileSize());
        fileLogoIv.setImageResource(getCommand().getIcon());
        fileNameTv.setText(fileInfo.getFileName());
        tipsTv.setText(BitmapUtils.formatAnTongSpanContent(getStringRes(R.string.actoma_cannot_open),
                getActivity(), 0.75f, BitmapUtils.AN_TONG_DETAIL_PLUS));
        if(toolbar != null){
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCommand().back();
                }
            });
        }
        if (fileInfo.getFileState() == ConstDef.DONE) {
            //文件下载已完成
            progressBar.setVisibility(View.INVISIBLE);
            downloadSizeTv.setVisibility(View.INVISIBLE);
            tipsTv.setVisibility(View.VISIBLE);
            ctrlBtn.setText(R.string.open_with_other_app);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(fileInfo.getPercent());
            downloadSizeTv.setVisibility(View.VISIBLE);
            tipsTv.setVisibility(View.GONE);
            //文件下载未完成
            if(fileInfo.getFileState() == ConstDef.PAUSE){
                ctrlBtn.setText(R.string.resume_download);
            }else {
                if(NetworkStateBroadcastReceiver.getState() != 0){
                    getCommand().pauseDownloadFile();
                    fileInfo.setFileState(ConstDef.PAUSE);
                    ctrlBtn.setText(R.string.resume_download);
                }else{
                    ctrlBtn.setText(R.string.pause_download);
                }

            }
        }
        downloadSizeTv.setText(getStringRes(R.string.loading) + "(" + getFileSize((fileInfo.getPercent() * fileInfo.getFileSize() / 100))
                + "/" + fileSizeText + ")");
        ctrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileInfo.getFileState() == ConstDef.DONE){
                    progressBar.setVisibility(View.INVISIBLE);
                    downloadSizeTv.setVisibility(View.INVISIBLE);
                    getCommand().openFile();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    downloadSizeTv.setVisibility(View.VISIBLE);
                    if(fileInfo.getFileState() == ConstDef.LOADING){
                        getCommand().pauseDownloadFile();
                        ctrlBtn.setText(R.string.resume_download);
                    }else if(fileInfo.getFileState() == ConstDef.PAUSE ){
                        if(NetworkStateBroadcastReceiver.getState() != 0){
                            Toast.makeText(getActivity() , getActivity().getString(R.string.network_disabled)
                                    , Toast.LENGTH_SHORT).show();
                        }else{
                            getCommand().downLoadFile(false);
                        }
                    }
                }
            }
        });
    }

    private String getFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.0");
        DecimalFormat df1 = new DecimalFormat("#");
        String fileSizeString;
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. begin
            fileSizeString = df1.format((int) fileS) + "B";
            if (fileSizeString.contains(".")) {
                if (fileSizeString.contains(".")) {
                    String[] s = fileSizeString.split("\\.");
                    fileSizeString = s[0] + "B";
                }
            }
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. end
        } else if (fileS < 1048576) {
            //guorong@xdja.com 解决大小在1000kb到1024kb之间的时候显示问题. begin
            if (fileS > 1024 * 1000) {
                fileSizeString = "1MB";
                //guorong@xdja.com 解决大小在1000kb到1024kb之间的时候显示问题. end
            } else {
                fileSizeString = df1.format((int) fileS / 1024) + "KB";
                //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. begin
                if (fileSizeString.contains(".")) {
                    String[] s = fileSizeString.split("\\.");
                    fileSizeString = s[0] + "KB";
                }
            }
            //guorong@xdja.com 大小为B或者KB，略去小数点后的数值 bug :5076. end
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    @Override
    public void changeViewSate(int state) {
        if(state != 0){
            if(fileInfo != null && fileInfo.getFileState() == ConstDef.LOADING){
                getCommand().pauseDownloadFile();
                fileInfo.setFileState(ConstDef.PAUSE);
                ctrlBtn.setText(R.string.resume_download);
            }
        }
    }

    @Override
    public void setCtrlBtnText(int res) {
        if(ctrlBtn != null){
            ctrlBtn.setText(res);
        }
    }
}
