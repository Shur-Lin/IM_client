package cn.itcast.emim.ui.activity;

import android.content.Intent;
import android.os.SystemClock;
import android.view.View;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;

import cn.itcast.emim.R;

/**
 * 启动界面
 * Created by Shur on 2016/9/23.
 */

public class SplashActivity extends BaseActivity {

    @Override
    public int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initViews() {


    }

    @Override
    public void setListeners() {

    }

    @Override
    public void initDatas() {

        new Thread(){

            @Override
            public void run() {
                SystemClock.sleep(1000);
                // 判断环信是否已经登录，如果已登录，直接进入主界面
                if (!EMChat.getInstance().isLoggedIn()) {
                    SystemClock.sleep(1000);

                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 进入主界面之前，从数据库加载会话数据到内存
                    EMChatManager.getInstance().loadAllConversations();

                    // 进入主界面
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v, int id) {

    }
}
