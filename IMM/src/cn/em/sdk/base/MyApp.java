package cn.em.sdk.base;

import android.app.Application;

import cn.itcast.emim.base.Global;
import cn.itcast.emim.base.IMHelper;

/**
 * Created by Shur on 2016/9/23.
 */

public class MyApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Global.init(this);
        //初始化聊天的相关参数
        IMHelper.getInstance().init(this);
    }
}
