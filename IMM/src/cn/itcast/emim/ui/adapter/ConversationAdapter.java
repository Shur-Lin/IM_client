package cn.itcast.emim.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

import cn.itcast.emim.ui.holder.BaseHolder;
import cn.itcast.emim.ui.holder.ConversationHolder;

/**
 * Created by Shur on 2016/9/29.
 */

public class ConversationAdapter extends MyBaseAdapter {

    /**
     * 构造方法
     * @param context
     * @param listdatas
     */
    public ConversationAdapter(Context context, List listdatas) {
        super(context, listdatas);
    }

    //
    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, Object data) {
        return new ConversationHolder(mContext, parent, this);
    }

}
