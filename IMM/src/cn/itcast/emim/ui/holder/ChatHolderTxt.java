package cn.itcast.emim.ui.holder;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;

import java.util.Date;

import cn.itcast.emim.R;
import cn.itcast.emim.base.Const;
import cn.itcast.emim.base.Global;
import de.greenrobot.event.EventBus;

/**
 * Created by Shur on 2016/9/30.
 */

public class ChatHolderTxt extends BaseHolder<EMMessage> {

    /**
     * 时间
     */
    private TextView tvTime;
    /**
     * 消息内容
     */
    private TextView tvContent;

    /**
     * 消息正在发送提示
     */
    private ProgressBar progressBar;
    /**
     * 消息送达回执
     */
    private TextView tvDeliverAck;
    /**
     * 消息已读回执
     */
    private TextView tvReadAck;
    /**
     * 发送失败的提示
     */
    private ImageView ivSendFail;

    public ChatHolderTxt(Context context, ViewGroup parent, BaseAdapter adapter, EMMessage data) {
        super(context, parent, adapter);
        this.data = data;
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mParent) {
        int layout = data.direct == EMMessage.Direct.RECEIVE ?
                R.layout.chat_row_received_message :
                R.layout.chat_row_sent_message;

        View view = mInflater.inflate(layout, mParent, false);
        return view;
    }

    @Override
    protected void initViews(View root) {
        tvTime = (TextView) root.findViewById(R.id.timestamp);
        tvContent = (TextView) root.findViewById(R.id.tv_chatcontent);

        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
        tvDeliverAck = (TextView) root.findViewById(R.id.tv_delivered);
        tvReadAck = (TextView) root.findViewById(R.id.tv_ack);
        ivSendFail = (ImageView) root.findViewById(R.id.msg_status);

        //发送失败
        if (ivSendFail != null) {// 接收到消息，没有该提示
            ivSendFail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 需要进行重发的消息实体
                    EMMessage mesage = data;
                    Message msg = new Message();
                    msg.what = Const.RESEND_SMS;
                    msg.obj = mesage; // 消息对象
                    EventBus.getDefault().post(msg);
                }
            });
        }
    }


    /**
     * 刷新界面
     *
     * @param data
     * @param position
     */
    @Override
    protected void onRefreshViews(EMMessage data, int position) {
        // 显示时间
        tvTime.setText(DateUtils.getTimestampString(new Date(data.getMsgTime())));

        // 显示文本消息
        TextMessageBody body = (TextMessageBody) data.getBody();
        String content = body.getMessage();
        tvContent.setText(content);

        if (progressBar != null) { // 默认选隐藏正在发送提示
            progressBar.setVisibility(View.GONE);
        }

        if (data.direct == EMMessage.Direct.SEND) {

            // 设置发送消息的状态
            setMessageStatus(data);
            // 设置消息发送的状态的回调
            setMessageSendCallback();

            // 送达回执
            if (tvDeliverAck != null) {
                if (data.isDelivered()) { // 已送达
                    tvDeliverAck.setVisibility(View.VISIBLE);
                } else {
                    tvDeliverAck.setVisibility(View.GONE);
                }
            }

            // 已读回执
            if (tvReadAck != null) {
                if (data.isAcked()) {// 表示消息已读
                    // 隐藏送达回执显示
                    tvDeliverAck.setVisibility(View.GONE);
                    tvReadAck.setVisibility(View.VISIBLE);
                } else {
                    tvReadAck.setVisibility(View.GONE);
                }
            }
        } else { // 接收到的消息
            // 给服务器发送通知，标识消息已读取
            if (!data.isAcked() && // 消息为未读取
                    data.getChatType() == EMMessage.ChatType.Chat) {// 单聊
                try {
                    EMChatManager.getInstance().ackMessageRead(data.getFrom(), data.getMsgId());
                    data.isAcked = true; // 表示消息已读
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ui线程更新界面
     */
    private void updateViewOnMainThread() {
        Global.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (data.status == EMMessage.Status.FAIL) {
                    Global.showToast("消息发送失败了");
                }
                System.out.println("-------------已读");
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private EMCallBack messageSendCallback;

    protected void setMessageSendCallback() {
        if (messageSendCallback == null) {
            messageSendCallback = new EMCallBack() {
                @Override
                public void onSuccess() {
                    updateViewOnMainThread();
                }

                @Override
                public void onProgress(final int progress, String status) {
                }

                @Override
                public void onError(int code, String error) {
                    updateViewOnMainThread();
                }
            };
        }
        // 设置消息回执监听
        data.setMessageStatusCallback(messageSendCallback);
    }

    /**
     * 设置发送消息的状态
     *
     * @param data
     */
    private void setMessageStatus(EMMessage data) {
        switch (data.status) {
            case CREATE: // 短信刚创建
                progressBar.setVisibility(View.VISIBLE);
                ivSendFail.setVisibility(View.GONE);
                break;
            case INPROGRESS: // 正在发送
                progressBar.setVisibility(View.VISIBLE);
                ivSendFail.setVisibility(View.GONE);
                break;
            case FAIL:    // 发送失败
                progressBar.setVisibility(View.GONE);
                ivSendFail.setVisibility(View.VISIBLE);
                break;
            case SUCCESS: // 消息发送成功
                progressBar.setVisibility(View.GONE);
                ivSendFail.setVisibility(View.GONE);
                break;
        }
    }

}
