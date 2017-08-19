package com.xdja.imsdk.manager;

import android.text.TextUtils;

import com.xdja.imsdk.constant.ImSdkFileConstant;
import com.xdja.imsdk.constant.ImSdkFileConstant.FileType;
import com.xdja.imsdk.constant.internal.Constant.FileCallType;
import com.xdja.imsdk.constant.internal.Constant.FileOptType;
import com.xdja.imsdk.constant.internal.FileTState;
import com.xdja.imsdk.constant.internal.HttpApiConstant;
import com.xdja.imsdk.constant.internal.State;
import com.xdja.imsdk.db.ImSdkDbUtils;
import com.xdja.imsdk.db.bean.FileMsgDb;
import com.xdja.imsdk.db.bean.HdThumbFileDb;
import com.xdja.imsdk.db.bean.RawFileDb;
import com.xdja.imsdk.db.helper.OptHelper;
import com.xdja.imsdk.db.helper.OptType;
import com.xdja.imsdk.db.helper.OptType.MQuery;
import com.xdja.imsdk.db.helper.UpdateArgs;
import com.xdja.imsdk.db.wrapper.MessageWrapper;
import com.xdja.imsdk.http.HttpUtils;
import com.xdja.imsdk.http.file.FileEntry;
import com.xdja.imsdk.http.file.callback.IFileDownloadCallback;
import com.xdja.imsdk.http.file.callback.IFileUploadCallback;
import com.xdja.imsdk.logger.Logger;
import com.xdja.imsdk.manager.callback.FileCallback;
import com.xdja.imsdk.manager.callback.NetCallback;
import com.xdja.imsdk.model.IMFileInfo;
import com.xdja.imsdk.model.IMMessage;
import com.xdja.imsdk.model.IMSession;
import com.xdja.imsdk.util.ToolUtils;

import java.io.File;
import java.util.List;

/**
 * 项目名称：ImSdk                            <br>
 * 类描述  ：ImSdk文件收发管理类                <br>
 * 创建时间：2016/12/1 20:10                  <br>
 * 修改记录：                                 <br>
 *
 * @author liming@xdja.com                  <br>
 * @version V1.1.7                          <br>
 */
public class ImSdkFileManager {
    private static ImSdkFileManager fileManager;

    private NetCallback netCall;
    private FileCallback fileCall;

    public static ImSdkFileManager getInstance(){
        synchronized(ImSdkFileManager.class) {
            if(fileManager == null){
                fileManager =  Factory.getInstance();
            }
        }
        return fileManager;
    }

    private static class Factory {
        static ImSdkFileManager getInstance() {
            return new ImSdkFileManager();
        }
    }

    /**
     * 初始化
     * @param file 文件回调接口
     * @param net 回调接口
     */
    public void init(FileCallback file, NetCallback net) {
        this.fileCall = file;
        this.netCall = net;
    }

    /**
     * 上传文件
     * @param wrapper wrapper
     */
    public void uploadFile(MessageWrapper wrapper) {
        FileMsgDb msg = wrapper.getFileMsgDb();
        if (msg == null || TextUtils.isEmpty(msg.getFile_path())) {
            Logger.getLogger().e("File is null!!!");
            return;
        }

        int state = msg.getFile_state();

        uploadExpect(state, wrapper, FileType.IS_SHOW);
    }

    /**
     * 文件暂停/继续
     * @param fileInfo fileInfo
     * @param type type
     */
    public void filePauseResume(IMFileInfo fileInfo, FileOptType type) {
        if (fileInfo == null || fileInfo.getMessage() == null) {
            return;
        }
        switch (type) {
            case UP_PAUSE:
                uploadPause(fileInfo);
                break;
            case UP_RESUME:
                uploadResume(fileInfo);
                break;
            case DOWN_PAUSE:
                downloadPause(fileInfo);
                break;
            case DOWN_RESUME:
                downloadResume(fileInfo);
                break;
            default:
                break;
        }
    }

    /**
     * 开始下载文件
     * @param files 文件
     */
    public void downloadStart(List<IMFileInfo> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        for (IMFileInfo file : files) {
            if (file != null && file.getMessage() != null) {
                MessageWrapper message = queryMessage(file.getMessage().getIMMessageId(), file.getFileType());
                FileEntry entry = getFileEntry(message, file.getFileType());
                downloadExpect(entry.getState(), entry);
            }
        }
    }

    /**
     * 开始预下载文件
     * @param files 文件
     */
    public void downloadPreStart(List<MessageWrapper> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        for (MessageWrapper file : files) {
            FileEntry entry = getFileEntry(file, FileType.IS_SHOW);
            downloadStart(entry);
        }
    }

    /**
     * 下载完成回调上层刷新
     * @param fileInfo fileInfo
     */
    public void downFinishCallback(IMFileInfo fileInfo) {
        ImSdkCallbackManager.getInstance().callFile(fileInfo, FileCallType.DOWN_FINISH);
    }


    /**
     * 根据文件上传状态判定动作
     * @param state state
     * @param msg msg
     * @param type type
     */
    private void uploadExpect(int state, MessageWrapper msg, FileType type) {
        switch (state) {
            case FileTState.ENCRYPT_SUCCESS:
            case FileTState.UP_FID_FAIL:
            case FileTState.UP_FAIL:
            case FileTState.UP_FID:
                FileEntry start = getFileEntry(msg, type);
                start.settSize(0L);
                uploadStart(start);
                break;

            case FileTState.UP_PAUSE:
                FileEntry resume = getFileEntry(msg, type);
                uploadResume(resume);// TODO: 2017/1/3 liming 断点续传存在问题
                break;
            case FileTState.UP_DONE:
                uploadNext(msg, type);
                break;
            case FileTState.UP_NON:
            case FileTState.ENCRYPT_FAIL:
                Logger.getLogger().e("should encrypt file first...");
                break;
            case FileTState.UP_LOADING:
                Logger.getLogger().d("file already uploading...");
                break;
            default:
                break;
        }
    }

    /**
     * 根据文件下载状态判定动作
     * @param state state
     */
    private void downloadExpect(int state, FileEntry entry) {
        switch (state) {
            case FileTState.DOWN_NON:
                downloadStart(entry);
                break;
            case FileTState.DOWN_PAUSE:
            case FileTState.DOWN_FAIL:
                downloadResume(entry);
                break;

            case FileTState.DOWN_DONE:
            case FileTState.DECRYPT_FAIL:
                fileDownFinish(entry);
                break;
            case FileTState.DECRYPT_SUCCESS:
                IMFileInfo fileInfo = ModelMapper.getIns().mapFileInfo(entry);
                downFinishCallback(fileInfo);
                break;
            case FileTState.DOWN_LOADING:
                Logger.getLogger().d("file already downloading...");
                break;
            default:
                break;
        }
    }

    /**
     * 开始上传文件, 无fid
     * @param entry 文件
     */
    private void uploadStart(FileEntry entry) {
        HttpUtils.getInstance().uploadStart(entry, uploadCallback);
    }

    /**
     * 续传文件，有fid之后
     * @param entry 文件
     */
    private void uploadResume(FileEntry entry) {
        HttpUtils.getInstance().uploadFileResume(entry);
    }

    /**
     * 文件上传状态为完成，判定下一个动作
     * @param msg msg
     */
    private void uploadNext(MessageWrapper msg, FileType type) {
        if (type == FileType.IS_SHOW) {
            FileMsgDb file = msg.getFileMsgDb();
            if (file == null) {
                return;
            }
            if (file.isImage()) {
                sendHd(msg);
                return;
            }

            if (file.isVideo()) {
                sendRaw(msg);
                return;
            }

            // 发送文本文件消息
            FileEntry entry = getFileEntry(msg, FileType.IS_SHOW);
            uploadFinish(entry);
            return;
        }

        if (type == FileType.IS_HD) {
            sendRaw(msg);
            return;
        }

        if (type == FileType.IS_RAW) {
            // 发送文本文件消息
            FileEntry entry = getFileEntry(msg, FileType.IS_RAW);
            if (entry == null || TextUtils.isEmpty(entry.getPath())) {
                return;
            }
            uploadFinish(entry);
        }
    }

    /**
     * 上传高清缩略图
     * @param wrapper 文件信息
     */
    private void sendHd(MessageWrapper wrapper) {
        HdThumbFileDb hd = wrapper.getHdThumbFileDb();
        // 无高清缩略图，上报上层
        if (hd == null || TextUtils.isEmpty(hd.getHd_file_path())) {
            uploadErrorCallback(wrapper, FileType.IS_HD);
            return;
        }

        int hdState = hd.getHd_state();
        uploadExpect(hdState, wrapper, FileType.IS_HD);
    }

    /**
     * 发送原始文件
     * @param wrapper 文件信息
     */
    private void sendRaw(MessageWrapper wrapper) {
        RawFileDb raw = wrapper.getRawFileDb();

        if (raw == null || TextUtils.isEmpty(raw.getRaw_file_path())) {
            if (wrapper.getFileMsgDb().isImage()) {
                // 无原图，发送文本文件消息
                FileEntry entry = getFileEntry(wrapper, FileType.IS_RAW);
                uploadFinish(entry);
                return;
            }
            // 无原始文件，上报上层
            uploadErrorCallback(wrapper, FileType.IS_RAW);
            return;
        }

        // 有原始文件
        int rawState = raw.getRaw_state();
        uploadExpect(rawState, wrapper, FileType.IS_RAW);
    }

    /**
     * 文件上传完成，回调刷新上层，发送文本消息
     * @param entry entry
     */
    private void uploadFinish(FileEntry entry) {
        // 文件上传完成，回调上层文件传输完成。
        ImSdkCallbackManager.getInstance().callFile(ModelMapper.getIns().mapFileInfo(entry), FileCallType.UP_FINISH);

        // 文件上传完成，发送文件文本
        fileCall.SendFileText(entry.getId());

        // 文件上传完成，删除加密后的文件
        clearFileCache(entry.getId());
    }

    /**
     * 发送过程中出现错误，回调给上层错误信息
     * 1、更新消息，文件状态
     * 2、回调刷新界面
     * @param wrapper 文件
     */
    private void uploadErrorCallback(MessageWrapper wrapper, FileType type) {
        if (wrapper == null || wrapper.getMsgEntryDb() == null) {
            return;
        }
        UpdateArgs updateArgs = OptHelper.getIns().getMCUpdate(
                ToolUtils.getLong(wrapper.getMsgEntryDb().getId()), State.UP_FAIL);
        ImSdkDbUtils.update(updateArgs);

        IMMessage message = ModelMapper.getIns().mapMessage(wrapper);
        IMFileInfo file = new IMFileInfo(message);
        file.setPercent(0);
        file.setFileType(type);
        file.setState(ImSdkFileConstant.FileState.FAIL);
        uploadError(file);
    }

    /**
     * 暂停上传文件
     * @param file file
     */
    private void uploadPause(IMFileInfo file) {
        long id = file.getMessage().getIMMessageId();
        FileType type = file.getFileType();

        updateState(type, FileTState.UP_PAUSE, id);

        FileEntry entry = queryFileEntry(id, type);
        HttpUtils.getInstance().uploadFilePause(entry);
    }

    /**
     * 续传文件
     * @param file file
     */
    private void uploadResume(IMFileInfo file) {
        long id = file.getMessage().getIMMessageId();
        FileType type = file.getFileType();

        MessageWrapper message = queryMessage(id, type);

        if (type == FileType.IS_SHOW) {
            FileMsgDb fileDb = message.getFileMsgDb();
            if (fileDb == null) {
                return;
            }

            uploadExpect(fileDb.getFile_state(), message, type);
        }

        if (type == FileType.IS_HD) {
            HdThumbFileDb hdDb = message.getHdThumbFileDb();
            if (hdDb == null) {
                return;
            }

            uploadExpect(hdDb.getHd_state(), message, type);
        }

        if (type == FileType.IS_RAW) {
            RawFileDb rawDb = message.getRawFileDb();
            if (rawDb == null) {
                return;
            }

            uploadExpect(rawDb.getRaw_state(), message, type);
        }
    }

    /**
     * 上传文件错误
     * @param file file
     */
    private void uploadError(IMFileInfo file) {
        ImSdkCallbackManager.getInstance().callFile(file, FileCallType.UP_FAIL);
    }

    /**
     * 开始下载文件
     * @param entry file
     */
    private void downloadStart(FileEntry entry) {
        HttpUtils.getInstance().downloadStart(entry, downloadCallback);
    }

    private void downloadResume(FileEntry entry) {
        updateState(entry.getType(), FileTState.DOWN_LOADING, entry.getId());
        HttpUtils.getInstance().downloadFileResume(entry, downloadCallback);
    }

    /**
     * 暂停下载文件
     * @param file file
     */
    private void downloadPause(IMFileInfo file) {
        long id = file.getMessage().getIMMessageId();
        FileType type = file.getFileType();

        updateState(type, FileTState.DOWN_PAUSE, id);

        FileEntry entry = queryFileEntry(id, type);
        HttpUtils.getInstance().downloadFilePause(entry);
    }

    /**
     * 继续下载文件
     * @param file file
     */
    private void downloadResume(IMFileInfo file) {
        long id = file.getMessage().getIMMessageId();
        FileType type = file.getFileType();

        MessageWrapper message = queryMessage(id, type);

        if (type == FileType.IS_SHOW) {
            FileMsgDb fileDb = message.getFileMsgDb();
            if (fileDb == null) {
                return;
            }

            downloadExpect(fileDb.getFile_state(), getFileEntry(message, type));
        }

        if (type == FileType.IS_HD) {
            HdThumbFileDb hdDb = message.getHdThumbFileDb();
            if (hdDb == null) {
                return;
            }

            downloadExpect(hdDb.getHd_state(), getFileEntry(message, type));
        }

        if (type == FileType.IS_RAW) {
            RawFileDb rawDb = message.getRawFileDb();
            if (rawDb == null) {
                return;
            }

            downloadExpect(rawDb.getRaw_state(), getFileEntry(message, type));
        }

    }

    /**
     * 文件下载完成，回调解密
     */
    private void fileDownFinish(FileEntry entry) {
        if (ImSdkConfigManager.getInstance().needEncrypt()) {
            MessageWrapper message = ImSdkDbUtils.queryMessage(OptHelper.getIns().getAMQuery(entry.getId()), MQuery.ALL);
            if (message == null) {
                return;
            }
            fileCall.DecryptFile(message, entry.getType());
        } else {
            IMFileInfo fileInfo = ModelMapper.getIns().mapFileInfo(entry);
            downFinishCallback(fileInfo);
        }
    }

    /**
     * 消息状态回调
     */
    private void stateCallback(FileEntry entry) {
        long id = entry.getId();
        MessageWrapper wrapper = ImSdkDbUtils.queryMessage(OptHelper.getIns().getAMQuery(id), MQuery.ALL);
        if (wrapper.getMsgEntryDb() == null) {
            return;
        }

        IMMessage message = ModelMapper.getIns().mapMessage(wrapper);
        String tag = wrapper.getMsgEntryDb().getSession_flag();
        IMSession session = ModelMapper.getIns().mapSession(ImSdkDbUtils.
                querySession(OptHelper.getIns().getSMQuery(tag), OptType.SQuery.HAVE));

        if (message == null || session == null) {
            return;
        }

        ImSdkCallbackManager.getInstance().callChange(session, message);
    }

    /**
     * 查询文件信息
     * @param id id
     * @param type type
     * @return FileEntry
     */
    private FileEntry queryFileEntry(long id, FileType type) {
        MessageWrapper message = queryMessage(id, type);

        return getFileEntry(message, type);
    }

    /**
     * 查询文件消息信息
     * @param id id
     * @param type type
     * @return MessageWrapper
     */
    private MessageWrapper queryMessage(long id, FileType type) {
        return ImSdkDbUtils.queryMessage(OptHelper.getIns().getFQuery(id, type), type);
    }

    /**
     * 转换类型
     * @param message message
     * @param type type
     * @return FileEntry
     */
    private FileEntry getFileEntry(MessageWrapper message, FileType type) {
        return ModelMapper.getIns().getFileEntry(message, type);
    }

    /**
     * 更新文件fid
     * @param entry 文件
     */
    private void updateFid(FileEntry entry) {
        ImSdkDbUtils.update(OptHelper.getIns().getFFUpdate(entry.getType(),
                entry.getFid(), entry.getState(), entry.getId()));
    }

    /**
     * 更新文件传输大小
     * @param entry 文件
     */
    private void updateTSize(FileEntry entry) {
        ImSdkDbUtils.update(OptHelper.getIns().getFTUpdate(entry.getType(), entry.gettSize(),
                entry.getState(), entry.getId()));
    }

    /**
     * 更新文件状态
     * @param type type
     * @param state state
     * @param id id
     */
    private void updateState(FileType type, int state, long id) {
        ImSdkDbUtils.update(OptHelper.getIns().getFTUpdate(type, -1, state, id));
    }

    /**
     * 更新文件状态，更新消息状态
     * @param entry 文件
     */
    private void updateUpState(FileEntry entry) {
        UpdateArgs file = OptHelper.getIns().getFTUpdate(entry.getType(), entry.gettSize(),
                entry.getState(), entry.getId());

        UpdateArgs message = OptHelper.getIns().getMSUpdate(entry.getId(), State.UP_FAIL);

        ImSdkDbUtils.updateMF(message, file);
    }

    /**
     * 判断文件状态，决定下一步的动作：
     * @param entry 文件
     */
    private void uploadNext(FileEntry entry) {
        FileType type;
        if (entry.isShow()) {
            if (entry.isImage()) {
                // 上传高清缩略图
                type = FileType.IS_HD;
                MessageWrapper message = queryMessage(entry.getId(), type);
                FileEntry hdFile = getFileEntry(message, type);
                uploadStart(hdFile);
                return;
            }

            if (entry.isVideo()) {
                // 上传原始视频文件
                type = FileType.IS_RAW;
                MessageWrapper message = queryMessage(entry.getId(), type);
                FileEntry hdFile = getFileEntry(message, type);
                uploadStart(hdFile);
                return;
            }
        }

        if (entry.getType() == FileType.IS_HD) {
            if (entry.isImage()) {
                // 上传原图
                type = FileType.IS_RAW;
                MessageWrapper wrapper = queryMessage(entry.getId(), type);
                if (wrapper.getRawFileDb() != null) {
                    FileEntry rawFile = getFileEntry(wrapper, type);
                    uploadStart(rawFile);
                    return;
                }
            }
        }

        uploadFinish(entry);
    }

    /**
     * 回调网络状态
     * @param code 状态码
     */
    private void networkCallback(int code) {
        netCall.NetChanged(String.valueOf(code));
    }

    /**
     * 文件进度信息回调
     */
    private void fileCallback(FileEntry entry, int percent, FileCallType type) {
        IMFileInfo fileInfo = ModelMapper.getIns().mapFileInfo(entry);
        fileInfo.setPercent(percent);
        ImSdkCallbackManager.getInstance().callFile(fileInfo, type);
    }

    /**
     * 文件信息回调
     * @param entry entry
     * @param type type
     */
    private void fileCallback(FileEntry entry,FileCallType type) {
        IMFileInfo fileInfo = ModelMapper.getIns().mapFileInfo(entry);
        ImSdkCallbackManager.getInstance().callFile(fileInfo, type);
    }

    /**
     * 清除缓存加密文件
     * @param id id
     */
    private void clearFileCache(long id) {
        MessageWrapper message = ImSdkDbUtils.queryMessage(OptHelper.getIns().getAMQuery(id), MQuery.ALL);

        if (message == null || message.getMsgEntryDb() == null) {
            return;
        }
        // modified by ycm 2017/4/1 for sharing web message
        if (message.isFile() || message.isWeb()) {
            FileMsgDb show = message.getFileMsgDb();
            if (show != null) {
                String showPath = show.getEncrypt_path();
                deleteFile(showPath);
            }

            HdThumbFileDb hd = message.getHdThumbFileDb();
            if (hd != null) {
                String hdPath = hd.getHd_encrypt_path();
                deleteFile(hdPath);
            }

            RawFileDb raw = message.getRawFileDb();
            if (raw != null) {
                String rawPath = raw.getRaw_encrypt_path();
                deleteFile(rawPath);
            }
        }
    }

    /**
     * 删除文件
     * @param path path
     */
    private void deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        file.delete();
    }

    /**
     * 上传文件的回调接口
     */
    IFileUploadCallback uploadCallback = new IFileUploadCallback() {

        /**
         * 文件上传进度，上传进度更新达到设置的最小百分比的时候回调该方法通知上层上传进度
         *
         * @param percent 文件上传进度百分比
         * @param entry   需要上传文件的信息
         */
        @Override
        public void uploadFileProgressUpdate(int percent, FileEntry entry) {
            Logger.getLogger().d("[UPLOAD UPDATE] percent = " + percent + ", entry = " + entry);
            if (entry == null) {
                return;
            }
            int state = entry.getState();
            if (state == FileTState.UP_FID ||
                    state == FileTState.UP_PAUSE ||
                    state == FileTState.UP_FAIL) {
                updateState(entry.getType(), FileTState.UP_LOADING, entry.getId());
            }
            entry.setState(FileTState.UP_LOADING);
            networkCallback(HttpApiConstant.HTTP_OK);                                       // 回调网络状态
            fileCallback(entry, percent, FileCallType.UP_UPDATE);                                // 回调文件状态
        }

        /**
         * 文件上传完成时回调上层
         *
         * @param entry 需要上传文件的信息
         */
        @Override
        public void uploadFileFinish(FileEntry entry) {
            Logger.getLogger().d("[UPLOAD FINISH] entry = " + entry);
            if (entry == null) {
                return;
            }
            entry.setState(FileTState.UP_DONE);
            networkCallback(HttpApiConstant.HTTP_OK);                             // 回调网络状态
            updateTSize(entry);                            // 更新文件已传输大小
            uploadNext(entry);                             // 成功后下一步处理，是发送文件信息还是继续其他附加文件的上传
        }

        /**
         * 文件上传过程中出错时回调上层
         *
         * @param code  文件上传出错错误码
         * @param entry 需要上传文件的信息
         */
        @Override
        public void uploadFileError(int code, FileEntry entry) {
            Logger.getLogger().d("[UPLOAD ERROR] entry = " + entry);
            if (entry == null) {
                return;
            }
            entry.setState(FileTState.UP_FAIL);
            updateTSize(entry);
            updateUpState(entry);
            fileCallback(entry, FileCallType.UP_FAIL);
            stateCallback(entry);
        }

        /**
         * 文件上传过程中，网络发生变化
         *
         * @param code  状态码
         * @param entry 上传文件的信息
         */
        @Override
        public void uploadFileNetChanged(int code, FileEntry entry) {
            networkCallback(code);
        }

        /**
         * 文件上传暂停时回调上层
         *
         * @param entry 需要暂停上传文件的信息
         */
        @Override
        public void uploadFilePause(FileEntry entry) {
            Logger.getLogger().d("[UPLOAD PAUSE] entry = " + entry);
            if (entry == null) {
                return;
            }
            networkCallback(HttpApiConstant.HTTP_OK);
            entry.setState(FileTState.UP_PAUSE);
            updateTSize(entry);
            fileCallback(entry, FileCallType.UP_PAUSE);
        }

        /**
         * 文件开始上传时回调上层
         *
         * @param entry 需要上传文件的信息
         */
        @Override
        public void uploadFileStart(FileEntry entry) {
            Logger.getLogger().d("[UPLOAD START] entry = " + entry);
            if (entry == null) {
                return;
            }
            networkCallback(HttpApiConstant.HTTP_OK);

            if (TextUtils.isEmpty(entry.getFid())) {
                entry.setState(FileTState.UP_FID_FAIL);
            } else {
                entry.setState(FileTState.UP_FID);
            }
            updateFid(entry);
        }
    };

    /**
     * 下载文件的回调接口
     */
    IFileDownloadCallback downloadCallback = new IFileDownloadCallback() {

        /**
         * 文件下载进度回调
         *
         * @param percent 下载进度百分比
         * @param entry   下载文件的信息
         */
        @Override
        public void downloadFileProgressUpdate(int percent, FileEntry entry) {
            Logger.getLogger().d("[DOWNLOAD UPDATE] percent = " + percent + ", entry = " + entry);
            if (entry == null) {
                return;
            }
            networkCallback(HttpApiConstant.HTTP_OK);                             // 回调网络状态
            int state = entry.getState();
            if (state == FileTState.DOWN_NON ||
                    state == FileTState.DOWN_PAUSE ||
                    state == FileTState.DOWN_FAIL) {
                updateState(entry.getType(), FileTState.DOWN_LOADING, entry.getId());
            }
            entry.setState(FileTState.DOWN_LOADING);
            fileCallback(entry, percent, FileCallType.DOWN_UPDATE);                                // 回调文件状态
        }

        /**
         * 文件下载完成回调
         *
         * @param entry 下载文件的信息
         */
        @Override
        public void downloadFileFinish(FileEntry entry) {
            Logger.getLogger().d("[DOWNLOAD FINISH] entry = " + entry);
            if (entry == null) {
                return;
            }
            networkCallback(HttpApiConstant.HTTP_OK);                             // 回调网络状态
            entry.setState(FileTState.DOWN_DONE);
            updateTSize(entry);                                                   // 更新文件已传输大小
//            fileCallback(entry, FileCallType.DOWN_FINISH);                        // 回调文件状态
            fileDownFinish(entry);                                                 // 文件下载完成，需要回调解密
        }

        /**
         * 文件下载错误回调
         *
         * @param code  错误码
         * @param entry 下载文件的信息
         */
        @Override
        public void downloadFileError(int code, FileEntry entry) {
            Logger.getLogger().d("[DOWNLOAD ERROR] code = " + code + ", entry = " + entry);
            if (entry == null) {
                return;
            }
            entry.setState(FileTState.DOWN_FAIL);
            updateTSize(entry);
            fileCallback(entry, FileCallType.DOWN_FAIL);
            stateCallback(entry);
        }

        /**
         * 文件下载网络发生变化
         *
         * @param code  状态码
         * @param entry 下载的文件信息
         */
        @Override
        public void downloadNetChanged(int code, FileEntry entry) {
            networkCallback(code);
        }

        /**
         * 文件下载暂停回调
         *
         * @param entry 暂停下载的文件信息
         */
        @Override
        public void downloadFilePause(FileEntry entry) {
            Logger.getLogger().d("[DOWNLOAD PAUSE] entry = " + entry);
            if (entry == null) {
                return;
            }
            networkCallback(HttpApiConstant.HTTP_OK);
            entry.setState(FileTState.DOWN_PAUSE);
            updateTSize(entry);
            fileCallback(entry, FileCallType.DOWN_PAUSE);
        }
    };
}
