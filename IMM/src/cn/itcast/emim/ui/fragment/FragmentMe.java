package cn.itcast.emim.ui.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

import cn.em.sdk.db.DemoDBManager;
import cn.em.sdk.manager.PreferenceManager;
import cn.itcast.emim.R;
import cn.itcast.emim.ui.activity.LoginActivity;

/**
 * 我的界面
 * Created by Shur on 2016/9/24.
 */

public class FragmentMe extends BaseFragment {

    private TextView tvnickname;//昵称
    private TextView tvusername;//用户名
    private Button btnLogout;

    /** 当前登录用户 */
    private String username;

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.main_fragment_04,container,false);
        this.tvusername = (TextView) view.findViewById(R.id.tv_username);
        this.tvnickname = (TextView) view.findViewById(R.id.tv_nick_name);
        this.btnLogout = (Button) view.findViewById(R.id.btn_logout);
        return view;
    }

    @Override
    protected void initViews(View root) {
        username = PreferenceManager.getInstance().getCurrentUsername();
        // 显示当前登录用户
        tvusername.setText(username);
        tvnickname.setText(username);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();
            }
        });
    }

    /**
     * 注销当前账号
     */
    private void onLogout() {
        // 注销当前账号
        mActivity.showProgressDialog("正在注销...");
        EMChatManager.getInstance().logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                // 销毁提示窗口
                mActivity.dismissProgressDialog();
                // 退出到登录界面
                Intent intent = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();

                // 注销后需要关闭数据库
                DemoDBManager.getInstance().closeDB();
                // Activity界面切换动画
                mActivity.overridePendingTransition(R.anim.push_bottom_in, R.anim.alpha_unchanged);

            }

            @Override
            public void onError(int i, String s) {
                // 销毁提示窗口
                mActivity.dismissProgressDialog();
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
