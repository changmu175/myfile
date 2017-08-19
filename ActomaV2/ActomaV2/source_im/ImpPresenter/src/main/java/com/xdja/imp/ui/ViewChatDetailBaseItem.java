package com.xdja.imp.ui;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdja.imp.R;
import com.xdja.imp.domain.model.TalkMessageBean;
import com.xdja.imp.frame.mvp.view.AdapterSuperView;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.presenter.command.ChatDetailAdapterCommand;
import com.xdja.imp.util.DateUtils;
import com.xdja.imp.util.ObjectUtil;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by jing on 2015/12/28.
 * 功能描述
 * Modify history description:
 * 1)Task 2632, modify for share and forward function by ycm at 20161101.
 * 2)Task 2632, modify for share and forward function by ycm at 20161130.
 */
public class ViewChatDetailBaseItem
        extends AdapterSuperView<ChatDetailAdapterCommand,TalkMessageBean>
        implements AdapterVu<ChatDetailAdapterCommand, TalkMessageBean> {

    /**
     * 时间线
     */
    private TextView timeTextView;

    @Override
    protected void injectView() {
        super.injectView();

        View view = getView();
        if (view != null) {
            timeTextView = (TextView)view.findViewById(R.id.timeLine);
        }

    }

    @Override
    public int getColorRes(@ColorRes int res) {
        return super.getColorRes(res);
    }

    @Override
    public void bindDataSource(int position, @NonNull TalkMessageBean bean) {
        super.bindDataSource(position, bean);
        if (!bean.isShowTimeLine()) {// 已经显示的就不能消失
            if (position == 0) {
                bean.setShowTimeLine(true);
            } else {
                //[S]modify by lixiaolong on 20160919. fix bug 4207.review by myself.
                if (bean.getShowTime() - getCommand().getTalkMsgBean(position - 1).getShowTime() > 300000 ||
                    position - getCommand().getLastTimeLineIsShowPosition() >= 30 ){
                    bean.setShowTimeLine(true);
                } else {
                    bean.setShowTimeLine(false);
                }
            }
        }

        if (bean.isShowTimeLine()) {
            setTimeLineIsShow(true, bean.getShowTime());
        } else {
            setTimeLineIsShow(false, bean.getShowTime());
        }
        //[E]modify by lixiaolong on 20160902. fix bug 3158. review by gbc.
    }

    private void setTimeLineIsShow(boolean isShowTimeLine, long timeText) {
        if (!ObjectUtil.objectIsEmpty(timeTextView)) {
            timeTextView.setVisibility(isShowTimeLine ? View.VISIBLE : View.GONE);
            timeTextView.setText(DateUtils.displayTime(getContext(), timeText));
        }
    }

    // add by ycm 20161129 [start]
    public interface LongClickCbk {
        void onLongClick();
    }

    public abstract class MyLongClick implements LongClickCbk {
        public MyLongClick() {
        }
    }
    // add by ycm 20161129 [end]

    /**
     * 文件是否存在
     * @param fileURL
     * @return
     */
    public boolean isFileExist(String fileURL){
        if (TextUtils.isEmpty(fileURL)){
            return false;
        }
        File file = new File(fileURL);
        if (file != null && file.exists()){
            return true;
        }
        return false;
    }
    /**
     * 反射获得ImageView设置的最大宽度和高度
     * @param object
     * @param fieldName
     * @return
     */
    public int getImageViewFieldValue(Object object, String fieldName){
        int value = 0;
        try{
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE){
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
