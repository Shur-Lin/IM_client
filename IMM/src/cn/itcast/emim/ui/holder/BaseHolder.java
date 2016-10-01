package cn.itcast.emim.ui.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Shur on 2016/9/27.
 */

public abstract class BaseHolder<T> {

    protected Context mContext;//上下文
    protected ViewGroup mParent;//父布局
    protected BaseAdapter mAdapter;//适配器

    /** 列表项的实体数据 */
    protected T data;

    /**列表项布局*/
    protected View mRootView;

    protected LayoutInflater mInflater;

    /** 当前列表项位置 */
    protected int position;

    public  BaseHolder(Context context, ViewGroup parent, BaseAdapter adapter) {
        this.mContext = context;
        this.mParent = parent;
        this.mAdapter = adapter;
    }

    /**
     * 初始化holder对象
     */
    public void init() {
        mInflater = LayoutInflater.from(mContext);
        // 由子类实现，创建列表项布局文件
        mRootView = onCreateView(mInflater, mParent);
        // 初始化列表项里面的子控件
        initViews(mRootView);
        // 保存holder对象到列表项视图中
        mRootView.setTag(this);
    }

    /**
     * 初始化列表项里面的子控件
     *
     * @param root 列表项根节点
     */
    protected abstract void initViews(View root);

    /**
     * 由子类实现，创建列表项布局文件
     * @param mInflater
     * @param mParent
     * @return
     */
    public abstract View onCreateView(LayoutInflater mInflater, ViewGroup mParent) ;

    /**
     *  刷新列表项数据
     * @param data
     * @param position
     */
    public void refreshViews(T data, int position) {
        this.data = data;
        this.position = position;

        // 刷新列表项子控件的数据显示
        onRefreshViews(data, position);
    }

    /**
     * 刷新列表项子控件的数据显示
     *
     * @param data
     * @param position
     */
    protected abstract void onRefreshViews(T data, int position);

    /**
     * 返回列表项布局
     * @return
     */
    public View getRootView() {
        return mRootView;
    }
}
