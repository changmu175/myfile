package com.xdja.imp.di.module;

import com.xdja.imp.data.di.annotation.PerActivity;
import com.xdja.imp.domain.interactor.def.*;
import com.xdja.imp.domain.interactor.im.*;

import dagger.Module;
import dagger.Provides;
/**
 * <p>Summary:IM用例注入提供者</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.module</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/20</p>
 * <p>Time:11:34</p>
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
@Module
public class IMUseCaseModule {

    @Provides
    @PerActivity
    InitIMProxy provideInitIMProxy(InitIMUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    ReleaseIMProxy provideReleaseIM(ReleaseIMProxyUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    GetConfig provideGetConfig(GetConfigUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    SetConfig provideSetConfig(SetConfigUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    GetSessionList provideGetSessionList(GetSessionListUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    AddCustomTalk provideAddCustomSession(AddCustomTalkUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    ChangeMsgState provideChangeMsgState(ChangeMsgStateUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    DeleteSession provideDeleteSession(DeleteSessionUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    GetAllMissedCount provideGetAllMissedCount(GetAllMissedCountUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    GetMissedCount provideGetMissedCount(GetMissedCountUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    ResendMsg provideResendMsg(ResendMsgUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    AddCustomMsg provideAddCustomMsg(AddCustomMsgUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    DeleteMsg provideDeleteMsg(DeleteMsgUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    GetMsgList provideGetMsgList(GetMsgListUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    SendMessage provideSendMessage(SendMessageUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    SendTextMsg provideSendTextMsg(SendTextMsgUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    SendCustomTextMsg provideSendCustomTextMsg(SendCustomTextMsgUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    SendFileMsg provideSendFileMsg(SendFileMsgUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    SendFileMsgList provideSendFileMsgList(SendFileMsgListUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    ResumeReceiveFile provideResumeReceiveFile(ResumeReceiveFileUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    ResumeSendFile provideResumeSendFile(ResumeSendFileUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    PauseReceiveFile providePauseReceiveFile(PauseReceiveFileUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    PauseSendFile providePauseSendFile(PauseSendFileUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    ClearUnReadMsg provideClearUnReadMsg(ClearUnReadMsgUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    CallBackRegist provideCallBackRegist(CallBackRegistUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    CallBackUnRegist provideCallBackUnRegist(CallBackUnRegistUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    ClearAllMsgInTalk provideClearAllMsgInTalk(ClearAllMsgInTalkUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    DownloadFile provideDownloadFile(DownloadUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    QueryLocalPictures provideQueryLocalPictrues(QueryLocalPicturesUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    QueryLocalFiles provideQueryLocalFiles(QueryLocalFilesUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    QueryLastFiles provideQueryLastFiles(QueryLastFilesUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    GetImageFileList provideGetImageFileList(GetImageFileListUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    GetSessionImageList provideGetSessionImageList(GetSessionImageListUseCase useCase){
        return useCase;
    }

    // Task 2632 [Begin]
    @Provides
    @PerActivity
    ShareFileMsgList provideShareFileMsgList(ShareFileMsgListUseCase useCase) {
        return useCase;
    }

    @Provides
    @PerActivity
    ShareTextMsg provideShareTextMsg(ShareTextMsgUseCase useCase) {
        return useCase;
    }
	// Task 2632 [End]

    // add by ycm 20161110[start]
    @Provides
    @PerActivity
    GetImageFileListForward provideGetImageFileListForForward(GetImageFileListForwardUseCase useCase){
        return useCase;
    }
    // add by ycm 20161110[end]
    @Provides
    @PerActivity
    GetHistoryFileList provideGetHistoryFileListUseCase(GetHistoryFileListUseCase useCase){
        return useCase;
    }

    // add by ycm 2017/2/15. for share and forward function
    @Provides
    @PerActivity
    GetVersion provideGetVersionUseCase(GetVersionUseCase useCase) {
        return useCase;
    }
	
	//add by licong for 网络模块刷新

    @Provides
    @PerActivity
    SynchronizationService provideSynchronizationServiceUseCase(SynchronizationServiceUseCase useCase) {
        return useCase;
    }
	
    @Provides
    @PerActivity
    GetTalkMessageBean provideGetSessionVideoListUseCase(GetTalkMessageBeanUseCase useCase){
        return useCase;
    }

    @Provides
    @PerActivity
    CompressImages provideCompressImagesUseCase(CompressImagesUseCase useCase) {
        return useCase;
    }
}
