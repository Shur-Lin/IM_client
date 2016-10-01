package cn.itcast.emim.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 发现界面
 * Created by Shur on 2016/9/24.
 */

public class FragmentDiscover extends BaseFragment {

    @Override
    public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container) {
        TextView textView = new TextView(mActivity);
        textView.setText("发现");
        return textView;
    }

    @Override
    protected void initViews(View root) {

    }
}
