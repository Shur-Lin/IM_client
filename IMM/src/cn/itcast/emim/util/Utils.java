package cn.itcast.emim.util;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * @author wjq
 */
public class Utils {

    /**
     * 查找一个布局里的所有的按钮并设置点击事件
     *
     * @param rootView
     * @param listener
     */
    public static void findButtonSetOnClickListener(View rootView, OnClickListener listener) {

        if (rootView instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) rootView;
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                // 如果是按钮设置点击事件
                if (child instanceof Button || child instanceof ImageButton) {
                    child.setOnClickListener(listener); // 设置点击事件
                }
                if (child instanceof ViewGroup) {
                    findButtonSetOnClickListener(child, listener);//递归调用
                }
            }
        }
    }


}











