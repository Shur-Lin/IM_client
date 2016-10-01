package cn.itcast.emim.manager;

import android.os.Message;

import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;

import java.util.List;

import cn.em.sdk.bean.EaseUser;
import cn.em.sdk.db.UserDao;
import cn.em.sdk.util.EaseCommonUtils;
import cn.itcast.emim.base.Const;
import cn.itcast.emim.base.Global;
import de.greenrobot.event.EventBus;

/**
 * 联系人变化监听器
 * Created by Shur on 2016/9/29.
 */

public class ContactListenerManager {
    private UserDao userDao;

    //定义一个对象实例
    private static ContactListenerManager instance = new ContactListenerManager();

    private ContactListenerManager() {
        userDao = new UserDao(Global.mContext);
    }

    //获取对象实例 提供使用
    public static ContactListenerManager getInstance() {
        return instance;
    }

    /**
     * 设置联系人监听器
     */
    public void setListener() {
        EMContactManager.getInstance().setContactListener(new EMContactListener() {
            @Override   // 添加了联系人
            public void onContactAdded(List<String> list) {
                System.out.println("----------onContactAdded(): list.size(): " + list.size());

                if (list != null) {
                    StringBuilder sb = new StringBuilder();
                    for (String username : list) {
                        EaseUser user = new EaseUser(username);
                        // 设置实体类对象的首字母
                        EaseCommonUtils.setUserInitialLetter(user);

                        // 保存新增的联系人到本地数据库
                        userDao.saveContact(user);
                        // 拼接用户名：,a001,a002
                        sb.append(",").append(username);
                    }
                    // ,a001,a002,a003....

                    // 新增了联系人之后，通知列表人列表界面刷新数据
                    Message msg = new Message();
                    msg.what = Const.ACTION_ADD_CONTACT; // 事件标识
                    msg.obj = sb.substring(1);  // 把第一个逗号截取掉：a001,a002
                    EventBus.getDefault().post(msg);
//                    System.out.println("----------onContactAdded(): " + msg.obj);
                }
            }
            // 删除了联系人
            @Override
            public void onContactDeleted(List<String> list) {
                if (list != null) {
                    System.out.println("----------onContactDeleted(): " + list.size());
                    StringBuilder sb = new StringBuilder();
                    for (String username : list) {
                        // 删除本地数据库联系人
                        userDao.deleteContact(username);
                        // 拼接用户名：,a001,a002
                        sb.append(",").append(username);
                    }
                    // ,a001,a002,a003....

                    // 新增了联系人之后，通知列表人列表界面刷新数据
                    Message msg = new Message();
                    msg.what = Const.ACTION_DEL_CONTACT; // 事件标识
                    msg.obj = sb.substring(1);  // 把第一个逗号截取掉：a001,a002
                    EventBus.getDefault().post(msg);
                }
            }

            @Override // 收到好友邀请
            public void onContactInvited(String s, String s1) {
                System.out.println("----------onContactInvited()");
            }

            @Override // 对方同意好友邀请
            public void onContactAgreed(String s) {
                System.out.println("----------onContactAgreed()");
            }

            @Override// 对方拒绝好友邀请
            public void onContactRefused(String s) {

            }
        });
    }
}
