package com.xdja.imp.di.component;

import com.xdja.imp.data.di.annotation.PerActivity;
import com.xdja.imp.di.module.IMUseCaseModule;
import com.xdja.imp.di.module.MxUseCaseModule;
import com.xdja.imp.presenter.activity.AnTongTeamOperationPresenter;
import com.xdja.imp.presenter.activity.ChatDetailActivity;
import com.xdja.imp.presenter.activity.ChatDetailFileCheckPresenter;
import com.xdja.imp.presenter.activity.ChatDetailPicPreviewActivity;
import com.xdja.imp.presenter.activity.FileExplorerPresenter;
import com.xdja.imp.presenter.activity.GroupChatSettingsPresenter;
import com.xdja.imp.presenter.activity.HistoryFileListActivity;
import com.xdja.imp.presenter.activity.PicturePreviewActivity;
import com.xdja.imp.presenter.activity.PictureSelectActivity;
import com.xdja.imp.presenter.activity.SingleChatSettingsPresenter;
import com.xdja.imp.presenter.adapter.ChatDetailAdapterPresenter;
import com.xdja.imp.presenter.adapter.ChatDetailMediaAdapter;
import com.xdja.imp.presenter.adapter.ChatListAdapterPresenter;
import com.xdja.imp.presenter.adapter.ChooseIMSessionAdapterPresenter;
import com.xdja.imp.presenter.adapter.HistoryFileAdapterPresenter;
import com.xdja.imp.presenter.adapter.PictureSelectAdapterPresenter;
import com.xdja.imp.presenter.fragment.ChatListFragmentPresenter;
import com.xdja.imp.presenter.activity.ChooseIMSessionActivity;
import com.xdja.imp.presenter.fragment.FileListPresenter;
import com.xdja.imp.presenter.fragment.LastFileListPresenter;
import com.xdja.imp.presenter.activity.SinglePhotoPresenter;
import com.xdja.imp.service.SimcUiService;
import com.xdja.imp.util.NotificationUtil;

import dagger.Component;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.imp.di.component</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2015/11/2</p>
 * <p>Time:10:44</p>
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 */
@PerActivity
@Component(dependencies = {
        UserComponent.class
        },
        modules = {
                MxUseCaseModule.class,
                IMUseCaseModule.class
        })
public interface UseCaseComponent extends UserComponent {

    void inject(ChatDetailActivity chatDetailActivity);

    void inject(PictureSelectActivity pictureSelectActivity);

    void inject(PicturePreviewActivity picturePreviewActivity);

    void inject(AnTongTeamOperationPresenter antongTeamActivity);

    void inject(ChatListFragmentPresenter chatListFragmentPresenter);

    void inject(SingleChatSettingsPresenter presenter);

    void inject(ChatDetailAdapterPresenter presenter);

    void inject (PictureSelectAdapterPresenter presenter);

    void inject(SimcUiService service);

    void inject(GroupChatSettingsPresenter service);

    void inject(ChatListAdapterPresenter presenter);

    void inject(NotificationUtil notificationUtil);

    void inject(ChatDetailPicPreviewActivity chatDetailPicPreviewActivity);

    void inject(FileExplorerPresenter fileExplorerPresenter);

    void inject(HistoryFileListActivity historyFileListActivity);

    void inject(HistoryFileAdapterPresenter historyFileAdapterPresenter);
    void inject(LastFileListPresenter chatFileListPresenter);

    void inject(FileListPresenter fileListPresenter);

    void inject(ChooseIMSessionActivity chooseIMSessionActivity);//Task 2632

    void inject(ChooseIMSessionAdapterPresenter chooseIMSessionAdapterPresenter);//Task 2632

    void inject(ChatDetailFileCheckPresenter chatDetailFileCheckPresenter);

    void inject(SinglePhotoPresenter singlePhotoPresenter);

    void inject(ChatDetailMediaAdapter chatDetailMediaAdapter);
}
