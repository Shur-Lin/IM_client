package cn.itcast.emim.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.itcast.emim.ui.holder.BaseHolder;

/**
 * 适配器s
 * Created by Shur on 2016/9/27.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    //上下文
    protected Context mContext;

    //列表显示的数据
    protected List<T> mListDatas;

    public MyBaseAdapter(Context context, List<T> listdatas) {
        this.mContext = context;
        this.mListDatas = listdatas;
    }

    /**
     * 供外部调用，进行显示数据的刷新
     *
     * @param newDatas
     */
    public void setDatas(List<T> newDatas) {
        this.mListDatas = newDatas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListDatas == null ? 0 : mListDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mListDatas.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder = null;
        // 列表项实体数据
        T data = getItem(position);

        if (convertView == null) {
            // 创建ViewHolder对象，
            holder = onCreateViewHolder(parent, data);
            // 初始化holder对象
            holder.init();
        } else {
            holder = (BaseHolder) convertView.getTag();
        }

        // 刷新列表项数据
        holder.refreshViews(data, position);
        // 返回列表项布局
        return holder.getRootView();
    }

    /**
     * 创建ViewHolder对象
     *
     * @param parent
     * @param data
     * @return
     */
    public abstract BaseHolder onCreateViewHolder(ViewGroup parent, T data);

    @Override
    public long getItemId(int position) {
        return position;
    }
}
