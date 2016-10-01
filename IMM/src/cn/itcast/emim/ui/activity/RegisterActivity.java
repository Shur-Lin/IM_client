package cn.itcast.emim.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import cn.itcast.emim.R;
import cn.itcast.emim.ui.view.EditLayout;

/**
 * 注册界面
 * Created by Shur on 2016/9/24.
 */

public class RegisterActivity extends BaseActivity {

    private EditLayout etaccount;
    private EditLayout etpassword;
    private EditLayout etpassword2;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_register;
    }

    @Override
    public void initViews() {

        setPageTitle("注册");

        this.etaccount = (EditLayout) findViewById(R.id.el_username);
        this.etpassword = (EditLayout) findViewById(R.id.el_password);
        this.etpassword2 = (EditLayout) findViewById(R.id.el_password2);

        etaccount.setHint("请输入用户名");
        etpassword.setHint("请输入密码");
        etpassword2.setHint("请再次输入密码");

        // 设置显示成密码的样式，显示成*
        etpassword.setPasswordStyle();
        etpassword2.setPasswordStyle();
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void onClick(View v, int id) {

        if (id == R.id.btn_register) {
            onRegister();
            return;
        }
    }

    private void onRegister() {

        final String username = etaccount.getText();
        final String password = etpassword.getText();
        String password2 = etpassword2.getText();

        if (TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(password2)) {
            showToast("用户名和密码不能为空");
            return;
        }

        if (!password.equals(password2)) {
            showToast("两次输入密码不一致");
            return;
        }

        new Thread() {

            @Override
            public void run() {
                super.run();
                // 创建账号：注册
                try {
                    EMChatManager.getInstance().createAccountOnServer(username, password);

                    // 注册成功
                    showToast("注册成功");

//                    goToMainActivity();
                    // 按下返回键，退回到登录界面
                    finish();

                } catch (EaseMobException e) {
                    e.printStackTrace();

                    int errorCode = e.getErrorCode();
                    if (errorCode == EMError.NONETWORK_ERROR) {
                        showToast("网络异常，请检查网络！");
                    } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                        showToast("用户已存在！");
                    } else if (errorCode == EMError.UNAUTHORIZED) {
                        showToast("注册失败，无权限！");
                    } else {
                        showToast("注册失败: " + e.getMessage());
                    }
                }
            }
        }.start();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
