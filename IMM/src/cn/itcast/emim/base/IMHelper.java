package cn.itcast.emim.base;

import android.content.Context;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

import cn.em.sdk.manager.EaseNotifier;
import cn.em.sdk.manager.PreferenceManager;

/**
 * 用户做聊天的初始化操作
 * Created by Shur on 2016/9/23.
 */

public class IMHelper {

    private static IMHelper instance;

    public IMHelper() {
    }

    public static IMHelper getInstance() {
        if (instance == null) {
            instance = new IMHelper();
        }
        return instance;
    }

    //初始化环信sdk
    public void init(Context context) {
        //初始化环信的sdk
        EMChat.getInstance().init(context);

        //sdk的调试模式 在做打包混淆时要设置为false  避免消耗不要的资源
        EMChat.getInstance().setDebugMode(true);

        //使用前初始化
        EaseNotifier.init(context);
        PreferenceManager.init(context);

        initEMChatOptions();
    }


    private void initEMChatOptions() {
        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(true);
        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
        options.setUseRoster(true);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);
        // 设置从db初始化加载时, 每个conversation需要加载msg的个数
        options.setNumberOfMessagesLoaded(10);
    }

}
