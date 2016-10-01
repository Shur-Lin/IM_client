package cn.itcast.emim.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.NetUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import cn.itcast.emim.R;
import cn.itcast.emim.base.Const;
import cn.itcast.emim.ui.activity.ChatActivity;
import cn.itcast.emim.ui.adapter.ConversationAdapter;
import de.greenrobot.event.EventBus;

/**
 * 会话界面
 * Created by Shur on 2016/9/24.
 */

public class FragmentConversation extends BaseFragment {

    private TextView tvconnectioninfo;
    private LinearLayout llerrornetworklayout;
    private ListView listView;
    private List<EMConversation> mListDatas;
    private ConversationAdapter mAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBus.getDefault().register(this);//扫描eventbus事件
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收EventBus事件，处理网络提示
     *
     * @param msg
     */
    public void onEventMainThread(Message msg) {
        System.out.println("----------------------Conversation----------onEventMainThread-");
        if (msg.what == Const.NETWORK_ERROR) {
            // 显示网络出错的提示
            llerrornetworklayout.setVisibility(View.VISIBLE);
            return;
        }
        if (msg.what == Const.NETWORK_CONNECT_SUCCESS) {
            // 网络连接成功隐藏出错的提示
            llerrornetworklayout.setVisibility(View.GONE);
            return;
        }
    }

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container) {
        View item = inflater.inflate(R.layout.main_fragment_01, container, false);
        return item;
    }

    @Override
    protected void initViews(View item) {
        this.listView = (ListView) item.findViewById(R.id.lv_conversation);
        this.llerrornetworklayout = (LinearLayout) item.findViewById(R.id.ll_error_network_layout);
        this.tvconnectioninfo = (TextView) item.findViewById(R.id.tv_connection_info);

        loadConversationDatas();//进入会话界面之后 加载数据

        mAdapter = new ConversationAdapter(mActivity, mListDatas);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation bean = (EMConversation) mAdapter.getItem(position);
                String username = bean.getUserName();// 要聊天的联系人

                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("user", username);
                startActivity(intent);
            }
        });

        // 进入界面时，如果没有网络，则进行提示
        if (!NetUtils.hasNetwork(mActivity)) {
            llerrornetworklayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 重新加载会话数据，并刷新界面
     */
    public void reloadConversationDatas() {
        loadConversationDatas();
        if (mAdapter != null) // 刷新会话数据
            mAdapter.setDatas(mListDatas);
    }

    /**
     * 获取未读消息的总条数
     * @return
     */
//    public int getUnreadSmsCount() {
//        int totalUnreadCount = 0;
//        for (int i = 0; i < mAdapter.getCount(); i++) {
//            EMConversation  con = (EMConversation) mAdapter.getItem(i);
//            totalUnreadCount += con.getUnreadMsgCount();// 取出会话的未读条数
//        }
//        return totalUnreadCount;
//    }

    /**
     * 加载会话数据
     */
    private void loadConversationDatas() {
        // 获取内存中所有会话 和loadAllConversations区别就是loadAllConversations是从本地数据库加载所有的会话到内存，
        // 所以app在使用getAllConversations之前，一定要先调用loadAllConversations 以确保会话都被加载到内存中
        Hashtable<String, EMConversation> allConversations = EMChatManager.getInstance()
                .getAllConversations();

        // Pair<会话中最后一条消息的时间, EMConversation>
        List<Pair<Long, EMConversation>> lists = new ArrayList<Pair<Long, EMConversation>>();
        for (EMConversation conversation : allConversations.values()) {
            // 会话中所有的短信
            List<EMMessage> allMessages = conversation.getAllMessages();
            // 过滤没有短信的会话
            if (allMessages.size() > 0) {
                // 会话最新的一条短信的发送时间
                Long smstime = conversation.getLastMessage().getMsgTime();
                Pair<Long, EMConversation> pair = new Pair<>(smstime, conversation);
                lists.add(pair);
            }
        }

        // 对会话进行排序
        sortConversation(lists);

        // 列表要显示的会话数据
        mListDatas = new ArrayList<>();
        for (Pair<Long, EMConversation> pair : lists) {
            mListDatas.add(pair.second);
        }

        System.out.println("----------------会话列表数据：" + mListDatas.size());
        // showToast("会话列表数据: " + mListDatas.size());
    }

    /**
     * 按最后一条消息的发送时间进行排序
     *
     * @param lists
     */
    private void sortConversation(List<Pair<Long, EMConversation>> lists) {
        Collections.sort(lists, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(Pair<Long, EMConversation> left, Pair<Long, EMConversation> right) {
                long leftMsgTime = left.first;  // 最后一条消息的发送时间
                long rightMsgTime = right.first;
                int r = 0;
                if (leftMsgTime < rightMsgTime) {    // 按时间倒序排列
                    r = -1;
                } else if (leftMsgTime == rightMsgTime) {
                    r = 0;
                } else {
                    r = 1;
                }
                return r;
            }
        });
    }
}
