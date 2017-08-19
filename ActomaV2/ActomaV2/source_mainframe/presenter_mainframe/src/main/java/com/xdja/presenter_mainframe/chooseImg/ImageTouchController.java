package com.xdja.presenter_mainframe.chooseImg;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.xdja.dependence.uitls.LogUtil;


/**
 * <p>summary:</p>
 * <p>description:</p>
 * <p>author:fanjiandong</p>
 * <p>time:2015/4/29 15:54</p>
 */
public class ImageTouchController implements View.OnTouchListener {
    /**
     * 默认的处理当前变化的矩阵对象
     */
    private Matrix matrix = new Matrix();

    /**
     * 设置矩阵初始状态
     *
     * @param matrix
     */
    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    //[S]add by lixiaolong on 20160901. fix bug 1534. review by wangchao1.
    public Matrix getMatrix() {
        return matrix;
    }
    //[E]add by lixiaolong on 20160901. fix bug 1534. review by wangchao1.
    /**
     * 用于保存上一次状态的矩阵对象
     */
    private Matrix savedMatrix = new Matrix();
    /**
     * 用于记录原来的位置（拖拽操作）
     */
    private PointF startPoint = new PointF();
    /**
     * 用于记录中间点的位置（缩放操作）
     */
    private PointF mid = new PointF();
    /**
     * 记录两点间的距离
     */
    private float oldDist = 1.0f;

    /**
     * 无操作
     */
    static final int NONE = 0;
    /**
     * 拖拽操作
     */
    static final int DRAG = 1;
    /**
     * 缩放操作
     */
    static final int ZOOM = 2;
    /**
     * 用于记录当前操作，默认无操作
     */
    private int mode = NONE;

    /**
     * 共缩放的比例
     */
    private float totalScale = 1;

    /**
     * 目标scale
     */
    private float targetScale = 1.0f;

    public float getTargetScale() {
        return targetScale;
    }

    public void setTargetScale(float targetScale) {
        this.targetScale = targetScale;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //手指按下操作
            case MotionEvent.ACTION_DOWN:
                //初始化矩阵信息
                savedMatrix.set(matrix);
                //设置初始位置，为拖拽操作做准备
                startPoint.set(event.getX(), event.getY());
                //一个手指按下，默认为拖拽操作
                mode = DRAG;
                break;
            //另一个手指按下
            case MotionEvent.ACTION_POINTER_DOWN:
                //记录两个手指（点）之间的距离
                oldDist = spacing(event);
                if (oldDist > 10.0f) {
                    //同步矩阵信息
                    savedMatrix.set(matrix);
                    //计算中心点的位置
                    midPoint(mid, event);
                    //缩放状态
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            //手指移动
            case MotionEvent.ACTION_MOVE:
                //拖拽状态
                if (mode == DRAG) {
                    //同步矩阵信息
                    matrix.set(savedMatrix);
                    //根据平移量进行矩阵平移
                    matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                    view.setImageMatrix(matrix);
                }
                //缩放状态
                else if (mode == ZOOM) {
                    //两个手指/点之间的新距离
                    float newDist = spacing(event);
                    if (newDist > 10.0f) {
                        //同步矩阵信息
//                        matrix.set(savedMatrix);
//                        float scale = newDist / oldDist;
//                        matrix.postScale(scale, scale, mid.x, mid.y);
//                        view.setImageMatrix(matrix);
                        //根据两次手指间的距离计算缩放倍数
                        float scale = newDist / oldDist;
//                        LogUtil.getUtils().i("scale : " + scale);

                        if (scale >=1 ){
//                            LogUtil.getUtils().i("scale 》=1  : " + scale);
//                            matrix.set(savedMatrix);
                            matrix.postScale(scale, scale, mid.x, mid.y);
                            totalScale = totalScale * scale;
//                            LogUtil.getUtils().i("scale 》=1  totalScale: " + totalScale);
                            view.setImageMatrix(matrix);
                        }else {
                            if (totalScale <= targetScale){
                                return true;
                            }
//                            matrix.set(savedMatrix);

//                            LogUtil.getUtils().i("totalScale * scale : " + totalScale * scale);
                            if (totalScale * scale <= targetScale) {
                                scale = targetScale/totalScale;
                                totalScale = targetScale;
                            }else {
                                totalScale = totalScale * scale;
                            }
                                LogUtil.getUtils().i("scale : " + scale);
                                //已两个手指中间的点为中心进行缩放
                                matrix.postScale(scale, scale, mid.x, mid.y);
                                view.setImageMatrix(matrix);
                            }
                            oldDist = newDist;
                        }
                }
                break;
        }

        return true; // indicate event was handled
    }

    /**
     * Determine the space between the first two fingers
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


}
