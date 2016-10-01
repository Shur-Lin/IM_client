package cn.itcast.emim.manager;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

import java.util.List;

import cn.em.sdk.manager.EaseNotifier;

/**
 * Created by Shur on 2016/9/30.
 * 信息监听
 */

public class MessageListenManager {

    private static MessageListenManager instance = new MessageListenManager();

    private MessageListenManager() {
    }

    public static MessageListenManager getInstance() {
        return instance;
    }

    /**
     * 设置全局的消息监听，不作会话数据的刷新，只作通知栏的提醒
     */
    public void setMessageListener() {
        // 监听所有类型的消息
        EMChatManager.getInstance().registerEventListener(new EMEventListener() {
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message;
                switch (event.getEvent()) {
                    case EventNewMessage:   // 普通的新消息
                        message = (EMMessage) event.getData();
                        // 提示通知栏通知
                        EaseNotifier.getInstance().onNewMsg(message);
                        break;

                    case EventOfflineMessage:   // 离线消息，可能会有多条
                        List<EMMessage> messages = (List<EMMessage>) event.getData();
                        // 提示通知栏通知,多条消息
                        EaseNotifier.getInstance().onNewMesg(messages);
                        break;
                }
            }
        });
    }
}
