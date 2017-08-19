package com.xdja.imp.presenter.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.xdja.imp.R;

import org.junit.Test;

/**
 * 项目名称：ActomaV2
 * 类描述：
 * 创建人：yuchangmu
 * 创建时间：2016/12/26.
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ChooseIMSessionActivityTest extends ActivityInstrumentationTestCase2  {

    private Instrumentation instrumentation;
    private Activity chooseActivity;
    private EditText search_et;
    public ChooseIMSessionActivityTest() {
        super(ChooseIMSessionActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        instrumentation = getInstrumentation();
        chooseActivity = getActivity();
        View search_view = chooseActivity.findViewById(R.id.search_layout_et);
        search_et = (EditText) search_view.findViewById(R.id.search_ed);

    }

    private void testPreCondition() {
        assertNotNull(chooseActivity);
        assertNotNull(search_et);
    }
//
//    public void testSearchInput() {
//        chooseActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                search_et.requestFocus();
//                search_et.performClick();
//            }
//        });
//        instrumentation.waitForIdleSync();
//        sendKeys(KeyEvent.KEYCODE_AT);
//    }
}
