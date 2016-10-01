package cn.itcast.emim.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cn.em.sdk.bean.EaseUser;
import cn.em.sdk.db.UserDao;
import cn.em.sdk.util.EaseCommonUtils;
import cn.itcast.emim.R;
import cn.itcast.emim.base.Const;
import cn.itcast.emim.ui.activity.ChatActivity;
import cn.itcast.emim.ui.adapter.ContactAdapter;
import cn.itcast.emim.ui.view.LetterBar;
import de.greenrobot.event.EventBus;

/**
 * 好友界面
 * Created by Shur on 2016/9/24.
 */

public class FragmentContact extends BaseFragment {

    private UserDao mUserDao;//环信联系人的实体

    private ListView mListView;
    private ContactAdapter adapter;

    private LetterBar lbletterbar;//左侧字母条

    /**
     * 显示在屏幕中间的按下的首字母提示
     */
    private TextView letterDialog;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.main_fragment_02, container, false);
        this.lbletterbar = (LetterBar) view.findViewById(R.id.lb_letter_bar);

        letterDialog = (TextView) view.findViewById(R.id.tv_show_pressed_letter);
        mListView = (ListView) view.findViewById(R.id.lv_list_view);

        // 注册列表长按时弹出的上下文菜单
        mListView.setOnCreateContextMenuListener(this);

        // 设置快速字母导航栏按下时的监听器
        lbletterbar.setOnPressedLetterChangedListener(new LetterBar.OnPressedLetterChangedListener() {

            @Override // 按下滑动时会回调此方法
            public void onPressedLetterChanged(String s) {
                int character = s.charAt(0); // 首字母
                // 该首字母在列表中第一次出现的位置
                int pos = adapter.getPosition(character);
                // 列表滚动到相应的位置
                mListView.setSelection(pos);
            }
        });
        // 设置显示在屏幕中间的按下的首字母提示
        lbletterbar.setTextView(letterDialog);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // 注册EventBus,接收事件
        EventBus.getDefault().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销EventBus事件
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void initViews(View root) {
        mUserDao = new UserDao(mActivity);
        loadContactDatas();
    }



    /**
     * 根据首字字母对联系人进行排序
     *
     * @param listDatas
     */
    private void sortContactList(List<EaseUser> listDatas) {
        //参2：排序的比较器
        Collections.sort(listDatas, new Comparator<EaseUser>() {
            @Override
            public int compare(EaseUser left, EaseUser right) {
                // left < right   left会排在前面   1
                // left == right                  0
                // left > right   right          -1
                int r = left.getInitialLetter().compareTo(right.getInitialLetter());
                if (r == 0) { // 首字母相同，根据用户名进行排序
                    r = left.getUsername().compareTo(right.getUsername());
                }
                return r;
            }
        });
    }

    /**
     * 加载联系人数据
     */
    private void loadContactDatas() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // 从网络读取联系人数据
                    List<String> usernames = EMContactManager.getInstance().getContactUserNames();
                    System.out.println("------------usernames大小: " + usernames.size());

                    //列表显示的实体数据
                    List<EaseUser> listDatas = new ArrayList<EaseUser>();

                    for (String username : usernames) {
                        EaseUser user = new EaseUser(username);
                        // 中001 z
                        // 设置用户名首字母
                        EaseCommonUtils.setUserInitialLetter(user);
                        listDatas.add(user);
                    }
                    // 保存联系人列表数据到本地数据库中
                    mUserDao.saveContactList(listDatas);

                    // 使用EventBus通知界面刷新数据
                    EventBus.getDefault().post(listDatas);

                    showToast("联系人个数：" + listDatas.size());
                    System.out.println("联系人个数：" + listDatas.size());

                } catch (EaseMobException e) {
                    e.printStackTrace();
                    System.out.println("-------------获取联系人失败：" + e.getMessage());
                    // 读取本地数据库联系人,使用EventBus通知界面刷新数据
                    EventBus.getDefault().post(getContactsFromDB());
                }
            }
        }.start();
    }


    /**
     * 从本地数据库读取联系人
     *
     * @return
     */
    private List<EaseUser> getContactsFromDB() {
        // Map<用户名, EaseUser>
        Map<String, EaseUser> contactmap = mUserDao.getContactList();
        List<EaseUser> contactList = new ArrayList<>(contactmap.values());
        return contactList;
    }


    /**
     * 在主线程接收EventBus事件，刷新联系人列表
     */
    public void onEventMainThread(List<EaseUser> listDatas) {
        // 根据首字字母对联系人进行排序
        sortContactList(listDatas);
        adapter = new ContactAdapter(mActivity, listDatas);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                EaseUser user = adapter.getItem(position);
                String username = user.getUsername();// 要聊天的联系人

                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("user", username);
                startActivity(intent);
            }
        });
    }

    /**
     * 在主线程接收EventBus事件: 联系人发生了变化：新增或删除
     */
    public void onEventMainThread(Message msg) {
        if (msg.what == Const.ACTION_ADD_CONTACT) {
            showToast("新增了联系人：" + msg.obj);
            // 读取本地数据库联系人,使用EventBus通知界面刷新数据
            EventBus.getDefault().post(getContactsFromDB());
            return;
        }

        if (msg.what == Const.ACTION_DEL_CONTACT) {
            showToast("删除了联系人：" + msg.obj);
            // 读取本地数据库联系人,使用EventBus通知界面刷新数据
            EventBus.getDefault().post(getContactsFromDB());
            return;
        }
    }



    // =====================联系人删除操作(begin)============================

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        // 创建长按弹出来的菜单选项：删除联系人
        getActivity().getMenuInflater().inflate(R.menu.contact_list,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 用户点击了删除联系人菜单项
        if (item.getItemId() == R.id.delete_contact) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // 用户长按的列表项位置
            int position = info.position;
            // 要删除的实体对象
            EaseUser user = adapter.getItem(position);
            // 删除联系人
            deleteContact(user);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 删除联系人
     * @param user
     */
    private void deleteContact(final EaseUser user) {
        new Thread(){
            @Override
            public void run() {
                // 根据用户删除一个联系人
                try {
                    EMContactManager.getInstance().deleteContact(user.getUsername());

                    // 删除本地数据库里的联系人
                    mUserDao.deleteContact(user.getUsername());

                    // 删除一个联系人，刷新列表显示
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 不能直接在子线程调用此方法
                            adapter.removeContact(user);
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    showToast("删除失败：" + e.getMessage());
                }
            }
        }.start();
    }

    // =====================联系人删除操作(end)============================


}
