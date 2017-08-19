package com.xdja.imp.frame.mvp.presenter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xdja.frame.presenter.mvp.Command;
import com.xdja.imp.domain.model.ConstDef;
import com.xdja.imp.ui.ImageViewPagerVu;
import com.xdja.imp.ui.ViewVideoPreview;
import com.xdja.imp.ui.vu.FilePreviewView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 *PagerAdapter 基类
 *@author guorong
 *create at 2017/3/9 14:50
 **/
public abstract class BaseViewPagerAdapter<C extends Command, D> extends PagerAdapter{
    private static String TAG = "BaseViewPagerAdapter";

    private LayoutInflater inflater;

    private Activity activity;

    public List<D> datasource;

    private int firstPos;

    //是否需要强制刷新
    protected boolean isForce = false;

    /**根据类型存储废弃的item中view，用于复用.*/
    private Map<Integer, LinkedList<View>> discardView = new HashMap<>();

    public Map<Integer,FilePreviewView<C,D>> adapterVus = new ConcurrentHashMap<>();

    public BaseViewPagerAdapter(Activity activity , List<D> datasource){
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.datasource = datasource;
    }
    @Override
    public int getCount() {
        return datasource.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int viewType = getType(datasource.get(position));//根据数据源判断FilePreviewView类型
        Class<? extends FilePreviewView> clazz = getViewFromType(viewType);
        FilePreviewView<C,D> viewPagerVu = null;
        View contentView;
        if (discardView.get(viewType) != null && discardView.get(viewType).size() > 0) {
            viewPagerVu = (FilePreviewView<C, D>) discardView.get(viewType).getFirst().getTag();
            discardView.get(viewType).removeFirst();
            viewPagerVu.onViewReused();
        } else {
            if(clazz != null){
                try {
                    viewPagerVu = clazz.newInstance();
                   
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if(viewPagerVu != null){
            viewPagerVu.setActivity(activity);
            viewPagerVu.setCommand(getCommand());
            viewPagerVu.init(inflater, null);
            contentView = viewPagerVu.getView();
            //将FilePreviewView作为Tag设置到View中，复用View时可以通过获取Tag获取到相应的FilePreviewView
            contentView.setTag(viewPagerVu);
            viewPagerVu.bindDataSource(position, datasource.get(position));
            container.addView(contentView);
            adapterVus.put(position, viewPagerVu);
            return contentView;
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //modify by guorong 解决删除图片控件复用产生的类型转换错误 start
        if(object instanceof View){
            FilePreviewView filePreviewView = (FilePreviewView) ((View) object).getTag();
            if(filePreviewView instanceof ImageViewPagerVu){
                if(!discardView.containsKey(ConstDef.IMAGE_ITEM)){
                    discardView.put(ConstDef.IMAGE_ITEM , new LinkedList<View>());
                }
                discardView.get(ConstDef.IMAGE_ITEM).addLast((View) object);
            }else if(filePreviewView instanceof ViewVideoPreview){
                if(!discardView.containsKey(ConstDef.TINY_VIDEO_ITEM)){
                    discardView.put(ConstDef.TINY_VIDEO_ITEM , new LinkedList<View>());
                }
                discardView.get(ConstDef.TINY_VIDEO_ITEM).addLast((View) object);
            }
            container.removeView((View) object);
        }
        //modify by guorong 解决删除图片控件复用产生的类型转换错误 end
        adapterVus.remove(position);
    }
    public abstract Class<? extends FilePreviewView> getViewFromType(@ConstDef.MediaType int type);

    public abstract @ConstDef.MediaType int getType(D d);

    public abstract C getCommand();

    public abstract void onPause(int position);

    public abstract void onResume(int position);

    public abstract void onDestroy();

    public abstract void onPageSelected(int lastPos, int curPos);

    public abstract void isNeedForceRefresh(boolean isForce);

    public int getFirstPos() {
        return firstPos;
    }

    public void setFirstPos(int firstPos) {
        this.firstPos = firstPos;
    }

    @Override
    public int getItemPosition(Object object) {
        //强制刷新
        if(isForce){
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
