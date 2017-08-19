package com.xdja.imp.ui.vu;

import android.support.annotation.NonNull;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.frame.presenter.mvp.view.SuperView;
import com.xdja.imp.frame.mvp.view.AdapterVu;
import com.xdja.imp.presenter.command.IChatDetailMediaCommand;
import com.xdja.imp.ui.ViewVideoPreview;

/**
 * 项目名称：短视频、图片预览             <br>
 * 类描述  ：短视频在ViewPager中进行左右滑动预览     <br>
 * 创建时间：2017/3/4        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */
public abstract class FilePreviewView<T extends Command,D>
                                            extends SuperView<T> implements AdapterVu<T ,D> {
    private D dataSource;

    private int position;

    public void bindDataSource(int position, @NonNull D dataSource) {
        this.dataSource = dataSource;
        this.position = position;
		//jyg add 2017/3/13 start 
        //当点击的第一个不是VideoPreview时，设置FirstItem为-1
        int firstItemPos = ((IChatDetailMediaCommand) getCommand()).getFirstItem();
        if (firstItemPos == getPosition() && !(this instanceof ViewVideoPreview)){
            ((IChatDetailMediaCommand) getCommand()).setFirstItem(-1);
        }
		//jyg add 2017/3/13 end 
    }

    public void onPause(){}

    public void onResume(){}

    public void onPageSelected(int lastPos, int curPos){}

    public D getDataSource() {
        return dataSource;
    }

    public int getPosition() {
        return position;
    }
}
