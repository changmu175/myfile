package com.xdja.presenter_mainframe.ui;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.xdja.comm.circleimageview.XToast;
import com.xdja.comm.uitl.TextUtil;
import com.xdja.dependence.uitls.NetworkUtil;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.AboutActomaCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.AboutActomaVu;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by geyao
 * 关于安通+
 */
@ContentView(value = R.layout.activity_view_about_actoma)
public class ViewAboutActoma extends ActivityView<AboutActomaCommand> implements AboutActomaVu {
    /**
     * 欢迎页所在布局Layout
     */
    @Bind(R.id.aboutactoma_welcomepagelayout)
    RelativeLayout aboutactomaWelcomepagelayout;
    /**
     * 功能介绍所在布局Layout
     */
    @Bind(R.id.aboutactoma_functionintroductionlayout)
    RelativeLayout aboutactomaFunctionintroductionlayout;
    /**
     * 常见问题所在布局Layout
     */
    @Bind(R.id.aboutactoma_commonproblemslayout)
    RelativeLayout aboutactomaCommonproblemslayout;
    /**
     * 版本更新所在布局Layout
     */
    @Bind(R.id.aboutactoma_versionupdatelayout)
    RelativeLayout aboutactomaVersionupdatelayout;
    /**
     * 使用条款和隐私政策所在布局Layout
     */
    @Bind(R.id.aboutactoma_termsandpolicylayout)
    RelativeLayout aboutactomaTermsandpolicylayout;
    /**
     * 退出所在布局Layout
     */
    @Bind(R.id.aboutactoma_exitlayout)
    RelativeLayout aboutactomaExitlayout;
    /**
     * 版本更新提示
     */
    @Bind(R.id.aboutactoma_updateprompt)
    TextView aboutactomaUpdateprompt;
    /**
     * 安通版本号
     */
    @Bind(R.id.aboutcatoma_title)
    TextView aboutcatomaTitle;

    @Bind(R.id.help_feedback_line)
    View helpFeedbackLine;
    /**
     * 隐藏版本更新new字样提醒
     */
    @Override
    public void hideUpdatePrompt() {
        if (aboutactomaUpdateprompt != null) {
            aboutactomaUpdateprompt.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示版本更新new字样提示
     */
    @Override
    public void showUpdatePrompt() {
        if (aboutactomaUpdateprompt != null) {
            aboutactomaUpdateprompt.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setTitle(String title) {
        aboutcatomaTitle.setText(TextUtil.getActomaText(getContext(),
                TextUtil.ActomaImage.IMAGE_VERSION_BIG,
                0, 0, 0, title));
    }


    /**
     * 点击欢迎页所在布局 打开欢迎页
     */
    @OnClick(R.id.aboutactoma_welcomepagelayout)
    public void clickWelcomPageLayout() {
        getCommand().openWelcomPage();
    }

    /**
     * 点击功能介绍所在布局 打开功能介绍页面
     */
    @OnClick(R.id.aboutactoma_functionintroductionlayout)
    public void clickIntroduceLayout() {
        getCommand().openIntroduce();
    }

    /**
     * 点击常见问题所在布局 打开常见问题页面
     */
    @OnClick(R.id.aboutactoma_commonproblemslayout)
    public void clickProblemLayout() {
        getCommand().openProblem();
    }

    /**
     * 点击版本更新所在布局 打开版本更新对话框
     */
    @OnClick(R.id.aboutactoma_versionupdatelayout)
    public void clickUpdateLayout() {
        //添加网络状态判断
        if (NetworkUtil.isNetworkConnect(getContext())) {
            //点击后取消new展示
            freshUpdateNew(false);
            updateVersion();
        } else {
            XToast.show(getContext(), getStringRes(R.string.netNotWork));
        }
    }

    private void updateVersion() {
        getCommand().openUpdate();
    }


    /**
     * 点击使用条款和隐私政策所在布局 打开使用条款和隐私政策页面
     */
    @OnClick(R.id.aboutactoma_termsandpolicylayout)
    public void clickTermPolicyLayout() {
        getCommand().openTermPolicy();
    }

    /**
     * 点击退出所在布局 执行退出操作
     */
    @OnClick(R.id.aboutactoma_exitlayout)
    public void clickExitLayout() {
        getCommand().exit();
    }

    @Override
    protected int getToolbarType() {
        return ToolbarDef.NAVIGATE_OTHER;
    }

    /**
     * 刷新界面展示 是否有新的更新信息
     *
     * @param isHaveNew
     */
    @Override
    public void freshUpdateNew(boolean isHaveNew) {
        if (isHaveNew) {
            aboutactomaUpdateprompt.setVisibility(View.VISIBLE);
        } else {
            aboutactomaUpdateprompt.setVisibility(View.GONE);
        }
    }

    /*[S]modify by tangsha@20161011 for multi language*/
    @Override
    public String getTitleStr(){
        return getStringRes(R.string.title_setting_about_soft);
    }
    /*[E]modify by tangsha@20161011 for multi language*/

    @Override
    public void updateHelpFeedBack(boolean isHaveHelpFeedBack) {
        aboutactomaCommonproblemslayout.setVisibility(isHaveHelpFeedBack?View.VISIBLE:View.GONE);
        helpFeedbackLine.setVisibility(isHaveHelpFeedBack?View.VISIBLE:View.GONE);//add by xnn for private version @20170221
    }
}
