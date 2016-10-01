package cn.itcast.emim.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

import java.util.List;

import cn.itcast.emim.R;
import cn.itcast.emim.base.Const;
import cn.itcast.emim.base.Global;
import cn.itcast.emim.ui.adapter.ChatAdapter;
import de.greenrobot.event.EventBus;

/**
 * Created by Shur on 2016/9/28.
 */

public class ChatActivity extends BaseActivity {

    /**
     * 聊天对象
     */
    private String username;
    private EMConversation conversation;
    private List<EMMessage> mListDatas;

    private ListView mListView;
    private ChatAdapter mAdapter;

    private EditText editText;

    private SwipeRefreshLayout swipeRefreshLayout;
    /**
     * 每页显示10条
     */
    private int pageSize = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        //  注册监听
        EMChatManager.getInstance().registerEventListener(mMessageListener,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage, // 来了新消息
                        EMNotifierEvent.Event.EventDeliveryAck, // 消息送达回执
                        EMNotifierEvent.Event.EventReadAck  // 已读回执
                });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_chat;
    }

    @Override
    public void initViews() {
        mListView = findView(R.id.lv_chatting);
        editText = findView(R.id.et_input);
        swipeRefreshLayout = findView(R.id.chat_swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(new int[]{
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_red_dark
        });
    }

    /**
     * 是否正在加载上一页数据
     */
    private boolean isLoading = false;

    @Override
    public void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {// 用户执行了下拉操作后调用
                if (!isLoading) {
                    isLoading = true;
                    // 从数据库加载更多的数据到内存中
                    // 参1：从哪条消息开始往前加裁
                    // 参2：要加载多少条
                    try {
                        String msgId = mAdapter.getItem(0).getMsgId();
                        List<EMMessage> messages = conversation.loadMoreMsgFromDB(msgId, pageSize);

                        if (messages.size() > 0) {// 有加载到数据，不是最后一页

                            // 设置会话中所有的消息为已读状态
                            conversation.markAllMessagesAsRead();
                            // 获取所有的聊天消息
                            // load方法：从数据库加载到内存
                            // getAllMessages:直接从内存中读取
                            mListDatas = conversation.getAllMessages();
                            mAdapter.setDatas(mListDatas); // 刷新数据

                        } else {
                            showToast("没有更多消息了");
                        }
                    } catch (Exception e) {
                    }

                    // 隐藏下拉刷新控件
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                }
            }
        });
    }

    @Override
    public void initDatas() {
        //  聊天对象
        username = getIntent().getStringExtra("user");
        setPageTitle(username); // 显示界面标题

        loadMessage();
    }


    /**
     * 加载消息
     */
    private void loadMessage() {
        // 根据用户名获取会话对象 包含聊天的消息
        conversation = EMChatManager.getInstance().getConversation(username);
        // 设置会话中所有的消息为已读状态
        conversation.markAllMessagesAsRead();
        // 获取所有的聊天消息
        mListDatas = conversation.getAllMessages();

        mAdapter = new ChatAdapter(this, mListDatas);
        mListView.setAdapter(mAdapter);
        // 滚动到列表的底部
        scrollToListViewBottom();
    }

    /**
     * 滚动到列表的底部
     */
    private void scrollToListViewBottom() {
        Global.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 滚动到列表的底部
                mListView.setSelection(mAdapter.getCount());
            }
        }, 100); // 延迟一会再滚动到底部
    }

    @Override
    public void onClick(View v, int id) {
        if (id == R.id.btn_send) {
            onBtnSendClick();
            return;
        }
    }

    private void onBtnSendClick() {
        String content = editText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToast("请输入消息");
            return;
        }
        // 发送消息
        sendTxtMessage(content);
    }

    /**
     * 发送文本消息
     *
     * @param content
     */
    private void sendTxtMessage(String content) {
        // 创建一个文本消息
        EMMessage message = EMMessage.createTxtSendMessage(content, username);
        try {
            EMChatManager.getInstance().sendMessage(message); // 发送消息
        } catch (EaseMobException e) {
            e.printStackTrace();
        }

        reloadMessageAndRefreshUI();
        // 清空编辑框输入
        editText.setText("");
    }

    /**
     * 重新加载消息并刷新界面
     */
    private void reloadMessageAndRefreshUI() {
        // 设置会话中所有的消息为已读状态
        conversation.markAllMessagesAsRead();
        // 获取所有的聊天消息
        // load方法：从数据库加载到内存
        // getAllMessages:直接从内存中读取
        mListDatas = conversation.getAllMessages();
        mAdapter.setDatas(mListDatas); // 刷新数据
        scrollToListViewBottom();
    }


    /** 接收新消息监听器 */
    private EMEventListener mMessageListener = new EMEventListener() {
        @Override
        public void onEvent(final EMNotifierEvent event) {
            switch (event.getEvent()) {
                case EventNewMessage:   // 监听到新消息，刷新界面
                    runOnUiThread(new Runnable() { // 指在在主线程运行
                        @Override
                        public void run() {
                            onReceiveNewSms(event);
                        }
                    });
                    break;
                case EventDeliveryAck:  // 送达回执
                case EventReadAck:      // 消息已读回执
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 重新加载数据，刷新列表显示
                            reloadMessageAndRefreshUI();
                        }
                    });
                    break;

            }

        }
    };

    private void onReceiveNewSms(EMNotifierEvent event) {
        // 接收到消息对象
        EMMessage data = (EMMessage) event.getData();
        if (username.equals(data.getFrom())) { // 当前聊天对象发送过来的消息，
            // 重新加载数据，刷新列表显示
            reloadMessageAndRefreshUI();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        EMChatManager.getInstance().unregisterEventListener(mMessageListener);
    }

    /**
     * 接重发消息的evetbus事件
     */
    public void onEventMainThread(Message msg) {
        if (msg.what == Const.RESEND_SMS) {
            // 发送失败的消息实体
            EMMessage message = (EMMessage) msg.obj;
            showResendDialog(message);//显示重新发送消息的对话框

            return;
        }
    }


    /**
     * 重新发送消息确认对话框
     * @param message
     */
    private void showResendDialog(final EMMessage message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("是否重新发送该消息")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        message.status = EMMessage.Status.CREATE; // 修改消息的状态，重新发送该消息
                        try {
                            EMChatManager.getInstance().sendMessage(message);
                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }
                        reloadMessageAndRefreshUI();
                    }
                })
                .setNegativeButton("取消",null)
                .create();
        dialog.show();
    }
}
