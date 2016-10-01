package cn.itcast.emim.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.itcast.emim.ui.activity.BaseActivity;

/**
 * Fragment基类
 */
public abstract class BaseFragment extends Fragment {

    protected BaseActivity mActivity;

    protected View mRootView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = onCreateFragmentView(inflater, container);
            initViews(mRootView);
        } else {
            unbindParent(mRootView);
        }

        return mRootView;
    }

    public void unbindParent(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    public abstract View onCreateFragmentView(LayoutInflater inflater, ViewGroup container);

    protected abstract void initViews(View root);//让子类实现该方法

    //toast
    public void showToast(String msg) {
        mActivity.showToast(msg);
    }
}
