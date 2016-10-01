package cn.itcast.emim.ui.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;

import java.util.Date;

import cn.em.sdk.util.EaseCommonUtils;
import cn.itcast.emim.R;

/**
 * Created by Shur on 2016/9/29.
 */

public class ConversationHolder extends BaseHolder<EMConversation> {

    private ImageView ivavatar;
    private TextView tvunreadmsgnumber;
    private RelativeLayout avatarcontainer;
    private TextView tvusername;
    private TextView tvmessage;
    private LinearLayout listitlayout;
    private TextView tvtime;

    public ConversationHolder(Context context, ViewGroup parent, BaseAdapter adapter) {
        super(context, parent, adapter);
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mParent) {
        View view = mInflater.inflate(R.layout.item_conversation, mParent, false);
        return view;
    }

    @Override
    protected void initViews(View view) {
        this.tvtime = (TextView) view.findViewById(R.id.tv_time);
        this.listitlayout = (LinearLayout) view.findViewById(R.id.list_itlayout);
        this.tvmessage = (TextView) view.findViewById(R.id.tv_message);
        this.tvusername = (TextView) view.findViewById(R.id.tv_username);
        this.avatarcontainer = (RelativeLayout) view.findViewById(R.id.avatar_container);
        this.tvunreadmsgnumber = (TextView) view.findViewById(R.id.tv_unread_msg_number);
        this.ivavatar = (ImageView) view.findViewById(R.id.iv_avatar);
    }

    /**
     * 刷新会话界面
     * @param data
     * @param position
     */
    @Override
    protected void onRefreshViews(EMConversation data, int position) {
        // 会话中的最新的一条消息
        EMMessage lastMessage = data.getLastMessage();

        // 显示联系人姓名
        this.tvusername.setText(lastMessage.getUserName());

        // 显示短信内容
        String smsBody =  EaseCommonUtils.getMessageDigest(lastMessage, mContext);
        this.tvmessage.setText(smsBody);

        // 显示最新的一条消息的发送时间
        this.tvtime.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));


        // 显示未读消息的条数
        int unreadCount = data.getUnreadMsgCount();
        if (unreadCount > 0) {
            tvunreadmsgnumber.setVisibility(View.VISIBLE);
            this.tvunreadmsgnumber.setText("" + unreadCount);
        } else {
            tvunreadmsgnumber.setVisibility(View.GONE);
            this.tvunreadmsgnumber.setText("" + unreadCount);
        }
    }
}
