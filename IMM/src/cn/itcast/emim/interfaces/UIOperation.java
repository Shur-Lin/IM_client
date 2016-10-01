package cn.itcast.emim.interfaces;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * ui布局相关操作接口
 * @author wjq
 */
public interface UIOperation extends OnClickListener {

    /**
     * 返回界面布局文件
     *
     * @return
     */
    int getLayoutResId();

    /**
     * 初始化视图
     */
    void initViews();

    /**
     * 设置监听器
     */
    void setListeners();

    /**
     * 初始化数据
     */
    void initDatas();

    /**
     * 按钮的点击事件，由子类实现
     *
     * @param v
     * @param id
     */
    void onClick(View v, int id);

}

