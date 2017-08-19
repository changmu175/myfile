package com.xdja.presenter_mainframe.global;

import android.content.Context;
import android.content.Intent;

import com.xdja.dependence.annotations.ContextSpe;
import com.xdja.dependence.annotations.DiConfig;
import com.xdja.dependence.uitls.LogUtil;
import com.xdja.frame.AndroidApplication;
import com.xdja.frame.data.chip.TFCardManager;
import com.xdja.presenter_mainframe.autoupdate.AutoUpdate;
import com.xdja.presenter_mainframe.service.UpdateService;
import com.xdja.safeauth.property.SafeAuthProperty;

import javax.inject.Inject;

/**
 * <p>Summary:</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.presenter_mainframe.global</p>
 * <p>Author:fanjiandong</p>
 * <p>Date:2016/4/28</p>
 * <p>Time:10:07</p>
 */
public class GlobalLife implements GlobalLifeCycle {

    private PushController pushController;

    private TFCardManager tfCardManager;

    private Context context;

    @Inject
    public GlobalLife(PushController pushController,
                      TFCardManager tfCardManager,
                      @ContextSpe(DiConfig.CONTEXT_SCOPE_APP)
                      Context context) {
        this.pushController = pushController;
        this.tfCardManager = tfCardManager;
        this.context = context;
    }

    @Override
    public void create() {
        this.tfCardManager.initTFCardManager();
        this.tfCardManager.initUnitePinManager();

        //FOR AUTH。
        SafeAuthProperty.Url = ((AndroidApplication) context.getApplicationContext())
                .getApplicationComponent()
                .defaultConfigCache()
                .get()
                .get("authUrl");

        boolean result = this.pushController.startPush();
        LogUtil.getUtils().i("启动推送结果为 ： " + result);

        // [Start] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
        //启动自动更新服务
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(AutoUpdate.ACTION_CHECK_UPDATE);
        context.startService(intent);
        // [End] modify by LiXiaolong<mailTo:lxl@xdja.com> on 2016-08-10. for check update.
    }

    @Override
    public void destroy() {
        this.pushController.releasePush();
    }
}
