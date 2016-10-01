package cn.itcast.emim.ui.activity;

import android.text.TextUtils;
import android.view.View;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;

import cn.itcast.emim.R;
import cn.itcast.emim.ui.view.EditLayout;

/**
 * 添加好友（用户）
 * Created by Shur on 2016/1/23.
 */
public class AddUserActivity extends BaseActivity {

    private EditLayout editLayout;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_add_contact;
    }

    @Override
    public void initViews() {
        editLayout = findView(R.id.el_username);
        editLayout.setHint("请输入要添加的用户名");
        setPageTitle("添加好友");
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void onClick(View v, int id) {
        if (id == R.id.btn_add_friend) {
            addContact();
            return;
        }
    }

    /**
     * 添加一个联系人
     */
    private void addContact() {
        final String username = editLayout.getText();
        if (TextUtils.isEmpty(username)) {
            showToast("请输入要添加的用户名");
            return;
        }

        new Thread(){
            @Override
            public void run() {
                // 添加的理由或备注信息
                String reason = "我是" + username;
                try {
                    EMContactManager.getInstance().addContact(username, reason);
                    showToast("添加成功");
                    finish();
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    showToast("添加失败：" + e.getMessage());
                }
            }
        }.start();
    }
}
