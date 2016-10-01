package cn.itcast.emim.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

import java.util.List;

import cn.em.sdk.db.DemoDBManager;
import cn.itcast.emim.R;
import cn.itcast.emim.base.Const;
import cn.itcast.emim.base.Global;
import cn.itcast.emim.manager.ContactListenerManager;
import cn.itcast.emim.manager.MessageListenManager;
import cn.itcast.emim.ui.fragment.BaseFragment;
import cn.itcast.emim.ui.fragment.FragmentContact;
import cn.itcast.emim.ui.fragment.FragmentConversation;
import cn.itcast.emim.ui.fragment.FragmentDiscover;
import cn.itcast.emim.ui.fragment.FragmentMe;
import cn.itcast.emim.ui.view.GradientTab;
import de.greenrobot.event.EventBus;

/**
 * 主界面
 *
 * @author Shur
 *         底部四个按钮使用自定义控件加载
 */
public class MainActivity extends BaseActivity {

    private ViewPager viewPager;

    private BaseFragment[] fragments = new BaseFragment[4];

    /**
     * 选项卡的父控件
     */
    private LinearLayout mLLTabLayout;

    private String[] tab_names = {"会话", "联系人", "发现", "我"};

    private int[] tab_icons = {
            R.drawable.icon_tab_1,
            R.drawable.icon_tab_2,
            R.drawable.icon_tab_3,
            R.drawable.icon_tab_4,
    };

    /**
     * 底部的四个选项卡控件: 可以实现颜色渐变
     */
    private GradientTab[] tabs = new GradientTab[4];

    /**
     * 当前显示第几项
     */
    private int mCurrentTabPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setGlobalListener();//设置全局监听
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
//        setPageTitle("遇见");
        viewPager = findView(R.id.view_pager);
        mLLTabLayout = findView(R.id.ll_tabs);

        initTitleBar();
        initViewPager();

        // 初始化底部的选项卡控件
        initTabs();
    }

    @Override
    public void onClick(View v, int id) {
        // 点击了添加用户按钮
        if (id == R.id.title_bar_btn_right) {
            Intent intent = new Intent(this, AddUserActivity.class);
            startActivity(intent);
            return;
        }
    }

    private void initTabs() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT);
        param.weight = 1; // 设置选项卡宽度，权重为1

        int padding = Global.dp2px(3);
        for (int i = 0; i < tab_icons.length; i++) {
            // 创建选项卡
            GradientTab tab = new GradientTab(this);
            // 设置显示的文本和图标
            tab.setTextAndIcon(tab_names[i], tab_icons[i]);
            tab.setTag(i);    // 设置选项的标识：位置

            tab.setPadding(padding, padding, padding, padding);

            // 设置选项卡点击事件，点击时实现界面切换
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    // 点击了选项卡
                    onTabClick(pos);
                }
            });

            mLLTabLayout.addView(tab, param);

            tabs[i] = tab;
        }

        // 进入主界面后，默认选中第一项
        mCurrentTabPos = 0;
        tabs[mCurrentTabPos].setTabSelected(true);
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        findViewById(R.id.ib_back).setVisibility(View.GONE);
        setPageTitle("遇见");
        findViewById(R.id.title_bar_btn_right).setVisibility(View.VISIBLE);
    }

    private void initViewPager() {
        fragments[0] = new FragmentConversation();
        fragments[1] = new FragmentContact();
        fragments[2] = new FragmentDiscover();
        fragments[3] = new FragmentMe();
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private BaseFragment[] fragments;

        public MyFragmentPagerAdapter(FragmentManager fm, BaseFragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments[i];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    //给选项卡设置监听
    @Override
    public void setListeners() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int i1) {
                // offset： Viewpager滚动的百分比： 0 - 1
                // 切换到最后一个界面时，position==3，此时越界

                if (position != tab_names.length - 1) {
                    // System.out.println("------------position: " + position);
                    // 要实现颜色渐变的左右两个选项
                    GradientTab tabLeft = tabs[position];
                    GradientTab tabRight = tabs[position + 1];

                    // 改变选项卡的透明度，实现滚动的渐变效果
                    tabLeft.updateTabAlpha(1 - offset);
                    tabRight.updateTabAlpha(offset);
                }
            }

            @Override
            public void onPageSelected(int pos) {
                onTabClick(pos);//选择选项卡
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    private void onTabClick(int pos) {
        // 恢复之前选中的选项卡的状态：非选中状态
        tabs[mCurrentTabPos].setTabSelected(false);
        mCurrentTabPos = pos; // 当前选中的位置
        // 设置当前点击了选项卡为选中
        tabs[mCurrentTabPos].setTabSelected(true);

        // 改变ViewPager显示的界面
        // 参2： 切换时不显示动画
        viewPager.setCurrentItem(mCurrentTabPos, false);

//		// 测试
//		if (pos == 0) {
//			// 显示未读取条数
//			tabs[0].setUnreadCount(2);
//			// 有新消息时的红点提示
//			tabs[1].setRedDotVisible(true);
//		} else if (pos == 3) {
//			// 未读取条数
//			tabs[0].setUnreadCount(0);
//			// 有新消息时的红点提示
//			tabs[1].setRedDotVisible(false);
//		}
    }

    @Override
    public void initDatas() {
        //  底部选项卡显示未读条数
        int unreadSmsCount = EMChatManager.getInstance().getUnreadMsgsCount();
        tabs[0].setUnreadCount(unreadSmsCount);
    }


    /**
     * 设置全局的监听器：
     */
    private void setGlobalListener() {
        // 注册联系人变动的监听器
        ContactListenerManager.getInstance().setListener();
        // 注册全局的新消息监听
        MessageListenManager.getInstance().setMessageListener();

        // 网络连接相关的监听器：网络断开/网络重新连接上/账号冲突(强制下线)
        EMChatManager.getInstance().addConnectionListener(connectionListener);

        // 设置完监听器之后，调用方法通知服务器发送离线消息
        EMChat.getInstance().setAppInited();
    }

    // ====================新消息的监听(begin)====================
    private EMEventListener mEMEventListener = new EMEventListener() {
        @Override // 此方法会在子线程调用，刷新界面要注意
        public void onEvent(EMNotifierEvent event) {
            System.out.println("----------------EMEventListener-onEvent（）");
            switch (event.getEvent()) {
                case EventNewMessage:    // 表示收到新消息
                    EMMessage msg = (EMMessage) event.getData();
                    System.out.println("-----------------接收到新消息:" + msg);

                    // 在通知栏提示新消息
                    // EaseNotifier.getInstance().onNewMsg(msg);

                    // 刷新会话列表数据以及选项卡的未读条数显示
                    // 要在主线程操作
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshConversationListAndTabUnreadCount();
                        }
                    });
                    break;

                case EventOfflineMessage:    // 表示收到离线消息
                    List<EMMessage> messages = (List<EMMessage>) event.getData();
                    // 提示通知栏通知,多条消息
                    // EaseNotifier.getInstance().onNewMesg(messages);
                    // 刷新会话列表数据以及选项卡的未读条数显示
                    // 要在主线程操作
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshConversationListAndTabUnreadCount();
                        }
                    });
                    break;
            }
        }
    };

    /**
     * 刷新会话列表数据以及选项卡的未读条数显示
     */
    private void refreshConversationListAndTabUnreadCount() {
        // 刷新会话列表数据
        FragmentConversation fragment = (FragmentConversation) fragments[0];
        // 重新加载会话数据，并刷新界面
        fragment.reloadConversationDatas();

        // 设置未读取条数
        int unreadSmsCount = EMChatManager.getInstance().getUnreadMsgsCount();
        tabs[0].setUnreadCount(unreadSmsCount);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // 从其它界面回到主界面时，重新加载刷新会话列表数据，以及底部选项卡未读条数显示
        refreshConversationListAndTabUnreadCount();

        // 主界面显示在前台的时候，注册监听新消息
        EMChatManager.getInstance().registerEventListener(mEMEventListener,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,    // 新消息
                        EMNotifierEvent.Event.EventOfflineMessage // 离线消息
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 主界面显示到后台的时候，取消监听新消息
        EMChatManager.getInstance().unregisterEventListener(mEMEventListener);
    }

    // ====================新消息的监听(end)====================


    //========网络连接监听=========================

    /**
     * 网络连接的监听器
     */
    private EMConnectionListener connectionListener = new EMConnectionListener() {
        // 网络重新连接
        @Override
        public void onConnected() {
            System.out.println("------------------------onConnected--");
            Message message = new Message();
            message.what = Const.NETWORK_CONNECT_SUCCESS; // 网络连接成功
            EventBus.getDefault().post(message);
        }

        @Override// 网络断开
        public void onDisconnected(int error) {
            System.out.println("------------------------onDisconnected--" + error);

            if (error == EMError.USER_REMOVED) {
                // 显示帐号已经被移除
            } else if (error == EMError.CONNECTION_CONFLICT) {
                System.out.println("----------------CONNECTION_CONFLICT");

                // 显示帐号在其他设备登陆
                // 弹出对话框强制下线
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						showConnectionConflitDialog();
//					}
//				});

                // MainActivity的启动模式设置为singletask, 启动MainActivity时
                // 会清理MainActivity实例之上的所有对象，并回调onNewIntent();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("conflit", true);    // true表示账号冲突
                startActivity(intent);    // 执行此方法之后，会调用onNewIntent()
            } else {
                Message message = new Message();
                message.what = Const.NETWORK_ERROR; // 网络错误
                EventBus.getDefault().post(message);

//				if (NetUtils.hasNetwork(MainActivity.this)) {
//					//连接不到聊天服务器
//
//				} else {
//					//当前网络不可用，请检查网络设置
//				}
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean conflit = intent.getBooleanExtra("conflit", false);
        if (conflit) {
            showConnectionConflitDialog();
        }
    }


    /**
     * 账号在其它设置登录，则弹出对话框强制下线
     */
    private void showConnectionConflitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("强制下线")
                .setMessage("你的账号在其他设备登录了")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 注销操作
                        EMChatManager.getInstance().logout(new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                goToLoginActivity();
                            }

                            @Override
                            public void onError(int i, String s) {
                                goToLoginActivity();
                            }

                            @Override
                            public void onProgress(int i, String s) {
                            }
                        });
                    }
                })
                .create();
        // 点击返回键不可以取消
        dialog.setCancelable(false);
        dialog.show();
    }


    /**
     * 退回登录界面
     */
    private void goToLoginActivity() {
        // 关闭数据
        DemoDBManager.getInstance().closeDB();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //====================网络连接监听=========================


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 主界面退出后，取消监听网络连接
        EMChatManager.getInstance().removeConnectionListener(connectionListener);
    }

}
