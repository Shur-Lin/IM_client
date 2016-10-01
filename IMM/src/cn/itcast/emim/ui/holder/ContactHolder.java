package cn.itcast.emim.ui.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.em.sdk.bean.EaseUser;
import cn.itcast.emim.R;
import cn.itcast.emim.ui.adapter.ContactAdapter;

/**
 * Created by Shur on 2016/9/27.
 */

public class ContactHolder extends BaseHolder<EaseUser> {

    /** 显示首字母 */
    private TextView tvcatalog;

    private ImageView ivicon;
    private TextView tvname;
    private ImageView ivreddot;

    public ContactHolder(Context context, ViewGroup parent, BaseAdapter adapter) {
        super(context, parent, adapter);
    }

    /**
     * 创建列表项布局
     *
     * @param mInflater
     * @param mParent
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mParent) {
        View item = mInflater.inflate(R.layout.item_contact, mParent, false);
        return item;
    }

    /**
     * 初始化列表项子控件
     * @param item
     */
    @Override
    protected void initViews(View item) {
        this.ivreddot = (ImageView) item.findViewById(R.id.iv_red_dot);
        this.tvname = (TextView) item.findViewById(R.id.tv_name);
        this.ivicon = (ImageView) item.findViewById(R.id.iv_icon);
        this.tvcatalog = (TextView) item.findViewById(R.id.tv_catalog);
    }

    /**
     * 刷新列表项子控件的显示
     *
     * @param data
     * @param position
     */
    @Override
    protected void onRefreshViews(EaseUser data, int position) {
        // 显示用户名
        this.tvname.setText(data.getUsername());

        // 显示首字母
        // position位置的首字母在列表中是第一次出现，则应显示
        if (getAdapter().isFirstCharacter(position)) {
            tvcatalog.setVisibility(View.VISIBLE);
            tvcatalog.setText(data.getInitialLetter());
        } else {
            tvcatalog.setVisibility(View.GONE);
        }
    }

    public ContactAdapter getAdapter() {
        return (ContactAdapter) mAdapter;
    }


}
