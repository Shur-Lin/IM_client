package cn.itcast.emim.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import cn.em.sdk.manager.PreferenceManager;
import cn.itcast.emim.R;
import cn.itcast.emim.ui.view.EditLayout;

/**
 * 登录界面
 * Created by Shur on 2016/9/23.
 */

public class LoginActivity extends BaseActivity {

    private EditLayout etaccount;
    private EditLayout etpassword;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews() {

        setPageTitle("登录");
        this.etpassword = (EditLayout) findViewById(R.id.et_password);
        this.etaccount = (EditLayout) findViewById(R.id.et_account);


        etaccount.setHint("请输入用户名");
        etpassword.setHint("请输入密码");

        // 获取之前登录的用户名
        String username = PreferenceManager.getInstance().getCurrentUsername();
        etaccount.setText(username);

        // 光标定位到密码编辑框
        etpassword.requestFocus();
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void onClick(View v, int id) {
        if (id == R.id.btn_login) {
            onLogin();
            return;
        }

        if (id == R.id.btn_register) {
            // 进入注册界面
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            return;
        }
    }

    //登录
    private void onLogin() {
        final String username = etaccount.getText();
        String password = etpassword.getText();

        if (TextUtils.isEmpty(username) ||TextUtils.isEmpty(password)) {
            showToast("用户名和密码不能为空");
            return ;
        }

        // 调用sdk进行登录
        showProgressDialog("正在登录...");
        EMChatManager.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                // 保存登录账号
                PreferenceManager.getInstance().setCurrentUserName(username);

                // 登录成功后加载会话数据： 从数据库加载到内存
                EMChatManager.getInstance().loadAllConversations();

                goToMainActivity();
                dismissProgressDialog();
            }

            @Override
            public void onError(int i, String s) {
                // 销毁加载窗口
                dismissProgressDialog();
                showToast("登录失败：" + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
