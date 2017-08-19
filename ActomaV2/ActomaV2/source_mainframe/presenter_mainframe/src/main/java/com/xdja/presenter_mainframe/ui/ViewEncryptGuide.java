package com.xdja.presenter_mainframe.ui;


import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xdja.comm.uitl.GcMemoryUtil;
import com.xdja.frame.presenter.mvp.annotation.ContentView;
import com.xdja.presenter_mainframe.R;
import com.xdja.presenter_mainframe.cmd.EncryptGuideCommand;
import com.xdja.presenter_mainframe.ui.uiInterface.EncryptGuideVu;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by geyao
 * 第三方加密服务引导页
 */
@ContentView(value = R.layout.main_encdec_popupwindow_layout)
public class ViewEncryptGuide extends ActivityView<EncryptGuideCommand>
        implements EncryptGuideVu {

    @Bind(R.id.encrypt_guide_1_next)
    ImageView encryptGuide1Next;
    @Bind(R.id.encrypt_guide_1_cancel)
    ImageView encryptGuide1Cancel;
    @Bind(R.id.main_guide_layout1_1)
    ImageView layout1Image1;
    @Bind(R.id.encrypt_guide_root)
    RelativeLayout mainGuideLayout1;

    @Bind(R.id.encrypt_guide_2_next)
    ImageView encryptGuide2Next;
    @Bind(R.id.encrypt_guide_2_cancel)
    ImageView encryptGuide2Cancel;
    @Bind(R.id.main_guide_layout2_1)
    ImageView layout2Image1;

    @Bind(R.id.encrypt_guide_3_next)
    ImageView encryptGuide3Next;
    @Bind(R.id.encrypt_guide_3_cancel)
    ImageView encryptGuide3Cancel;
    @Bind(R.id.main_guide_layout3_1)
    ImageView layout3Image1;

    @Bind(R.id.encrypt_guide_4_again)
    ImageView encryptGuide4Again;
    @Bind(R.id.encrypt_guide_4_over)
    ImageView encryptGuide4Over;
    @Bind(R.id.main_guide_layout4_1)
    ImageView layout4Image1;

    @Override
    public void onCreated() {
        super.onCreated();
        initFirstLayout(false);
    }

    public void setFirState(int STATE) {
        layout1Image1.setVisibility(STATE);
        encryptGuide1Cancel.setVisibility(STATE);
        encryptGuide1Next.setVisibility(STATE);
    }

    public void setSecState(int STATE) {
        encryptGuide2Next.setVisibility(STATE);
        encryptGuide2Cancel.setVisibility(STATE);
        layout2Image1.setVisibility(STATE);
    }

    public void setThrState(int STATE) {
        encryptGuide3Next.setVisibility(STATE);
        encryptGuide3Cancel.setVisibility(STATE);
        layout3Image1.setVisibility(STATE);
    }

    public void setFouState(int STATE) {
        encryptGuide4Again.setVisibility(STATE);
        encryptGuide4Over.setVisibility(STATE);
        layout4Image1.setVisibility(STATE);
    }

    public void initFirstLayout(boolean isFir) {
        if(!isFir) {
            layout4Image1.setBackgroundResource(0);
            encryptGuide4Again.setBackgroundResource(0);
            encryptGuide4Over.setBackgroundResource(0);
        }

        mainGuideLayout1.setBackgroundResource(0);
        mainGuideLayout1.setBackgroundResource(R.drawable.tip_1_1);

        setFirState(View.VISIBLE);
        setSecState(View.GONE);
        setThrState(View.GONE);
        setFouState(View.GONE);

        layout1Image1.setBackgroundResource(R.drawable.tip_1_1_con);
        encryptGuide1Cancel.setBackgroundResource(R.drawable.encrypt_guide_cancel_selector);
        encryptGuide1Next.setBackgroundResource(R.drawable.btn_next_normal);
    }

    @OnClick(R.id.encrypt_guide_1_next)
    public void encryptGuideOneNextClick() {

        layout1Image1.setBackgroundResource(0);
        encryptGuide1Cancel.setBackgroundResource(0);
        encryptGuide1Next.setBackgroundResource(0);

        mainGuideLayout1.setBackgroundResource(0);
        mainGuideLayout1.setBackgroundResource(R.drawable.tip_1_2);

        setFirState(View.GONE);
        setSecState(View.VISIBLE);
        setThrState(View.GONE);
        setFouState(View.GONE);

        layout2Image1.setBackgroundResource(R.drawable.tip_1_2_con);
        encryptGuide2Cancel.setBackgroundResource(R.drawable.encrypt_guide_cancel_selector);
        encryptGuide2Next.setBackgroundResource(R.drawable.btn_next_normal);
    }

    @OnClick(R.id.encrypt_guide_2_next)
    public void encryptGuideTwoNextClick() {

        layout2Image1.setBackgroundResource(0);
        encryptGuide2Cancel.setBackgroundResource(0);
        encryptGuide2Next.setBackgroundResource(0);

        mainGuideLayout1.setBackgroundResource(0);
        mainGuideLayout1.setBackgroundResource(R.drawable.tip_1_3);

        setFirState(View.GONE);
        setSecState(View.GONE);
        setThrState(View.VISIBLE);
        setFouState(View.GONE);

        layout3Image1.setBackgroundResource(R.drawable.tip_1_3_con);
        encryptGuide3Cancel.setBackgroundResource(R.drawable.encrypt_guide_cancel_selector);
        encryptGuide3Next.setBackgroundResource(R.drawable.btn_next_normal);

    }

    @OnClick(R.id.encrypt_guide_3_next)
    public void encryptGuideThreeNextClick() {

        layout3Image1.setBackgroundResource(0);
        encryptGuide3Cancel.setBackgroundResource(0);
        encryptGuide3Next.setBackgroundResource(0);

        mainGuideLayout1.setBackgroundResource(0);
        mainGuideLayout1.setBackgroundResource(R.drawable.tip_1_4);

        setFirState(View.GONE);
        setSecState(View.GONE);
        setThrState(View.GONE);
        setFouState(View.VISIBLE);

        layout4Image1.setBackgroundResource(R.drawable.tip_1_4_con);
        encryptGuide4Again.setBackgroundResource(R.drawable.btn_guide_again);
        encryptGuide4Over.setBackgroundResource(R.drawable.btn_guide_over);

    }

    @OnClick(R.id.encrypt_guide_4_again)
    public void encryptGuideFpurAgainClick() {
        initFirstLayout(false);
    }

    @OnClick(R.id.encrypt_guide_1_cancel)
    public void encryptGuideOneCancelClick() {
        getActivity().finish();
    }

    @OnClick(R.id.encrypt_guide_2_cancel)
    public void encryptGuideTwoCancelClick() {
        getActivity().finish();
    }

    @OnClick(R.id.encrypt_guide_3_cancel)
    public void encryptGuideThreeCancelClick() {
        getActivity().finish();
    }

    @OnClick(R.id.encrypt_guide_4_over)
    public void encryptGuideFpurOverClick() {
        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        GcMemoryUtil.clearMemory(mainGuideLayout1);
    }
}
