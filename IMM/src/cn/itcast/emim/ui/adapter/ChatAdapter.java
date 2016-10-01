package cn.itcast.emim.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.easemob.chat.EMMessage;

import java.util.List;

import cn.itcast.emim.ui.holder.BaseHolder;
import cn.itcast.emim.ui.holder.ChatHolderTxt;

/**
 * Created by Shur on 2016/9/30.
 */

public class ChatAdapter extends MyBaseAdapter<EMMessage> {

    public ChatAdapter(Context context, List<EMMessage> listdatas) {
        super(context, listdatas);
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, EMMessage data) {
        return new ChatHolderTxt(mContext, parent, this, data);
    }

    private static int count = 0;

    private static final int TYPE_SEND_TXT = count++;
    private static final int TYPE_RECEIVE_TXT = count++;

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message.getType() == EMMessage.Type.TXT) {
            return message.direct == EMMessage.Direct.RECEIVE
                    ? TYPE_RECEIVE_TXT : TYPE_SEND_TXT;
        }
        return 0;
    }

    // 返回有多少种列表项布局
    @Override
    public int getViewTypeCount() {
        return count;
    }
}
