package cn.itcast.emim.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cn.itcast.emim.R;
import cn.itcast.emim.base.Global;
import cn.itcast.emim.interfaces.UIOperation;
import cn.itcast.emim.util.Utils;

/**
 * Activity基类
 */
public abstract class BaseActivity extends FragmentActivity implements UIOperation {

    /**
     * 标题栏
     */
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(getLayoutResId());//选择布局
        mTitle = findView(R.id.tv_title);

        // 系统的根节点, activity布局会添加到这个布局中来
        View rootView = findViewById(android.R.id.content);
        // 查找一个布局里的所有的按钮并设置点击事件
        Utils.findButtonSetOnClickListener(rootView, this);

        initViews();
        setListeners();
        initDatas();
    }


    /**
     * 设置界面标题
     */
    public void setPageTitle(String title) {
        if (mTitle != null)
            mTitle.setText(title);
    }

    /**
     * 查找控件，可以省略强转
     *
     * @param id
     * @return
     */
    public <T> T findView(int id) {
        @SuppressWarnings("unchecked")
        T view = (T) findViewById(id);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:    // 点击了标题栏的返回键
                onBackPressed();
                break;

            default:
                onClick(v, v.getId());
                break;
        }
    }

    public void showToast(String text) {
        Global.showToast(text);
    }

    private ProgressDialog mPDialog;

    /**
     * 显示加载提示框(不能在子线程调用)
     */
    public void showProgressDialog(String message) {
        mPDialog = new ProgressDialog(this);
        mPDialog.setMessage(message);
        // 点击外部时不销毁
        mPDialog.setCanceledOnTouchOutside(false);

        // activity如果正在销毁或已销毁，不能show Dialog，否则出错。
        if (!isFinishing())
            mPDialog.show();
    }

    /**
     * 销毁加载提示框
     */
    public void dismissProgressDialog() {
        if (mPDialog != null) {
            mPDialog.dismiss();
            mPDialog = null;
        }
    }
}
