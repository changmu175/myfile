package com.xdja.imp.util;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;


/**
 * 项目名称：ActomaV2
 * 类描述：重写textview的触摸移动事件，实现长按超链接则相应LinearLayout的长按事件
 * 创建人：yuchangmu
 * 创建时间：2016/11/25.
 * 修改人：yuchangmu
 * 修改时间：2016/11/30
 * 修改备注：
 * 1)Task 2632, modify for recognize hyperlink function by ycm at 20161129.
 */
public class HyperlinkMovementMethod extends LinkMovementMethod {
    private long lastClickTime;
    private static final long CLICK_DELAY = 500L;
    @Override
    public boolean onTouchEvent(final TextView widget, final Spannable buffer, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - lastClickTime < CLICK_DELAY) {
                        link[0].onClick(widget);
                    }
                } else {
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                    lastClickTime = System.currentTimeMillis();
                }
                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }
        return false;
    }
}
