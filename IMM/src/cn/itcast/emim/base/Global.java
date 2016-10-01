package cn.itcast.emim.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * 全局公共类, 封装一些公共公能
 *
 * @author JQ
 */
public class Global {

    public static Context mContext;

    public static float mDensity;

    public static float mScreenWidth;

    public static float mScreenHeight;

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context) {
        mContext = context;
        initScreenSize();
    }

    /**
     * 屏幕密度  高度 宽度
     */
    private static void initScreenSize() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mDensity = dm.density;
        mScreenHeight = dm.heightPixels;
        mScreenWidth = dm.widthPixels;
    }

    public static int dp2px(int dp) {
        return (int) (dp * mDensity);
    }

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static Handler getMainHandler() {
        return mHandler;
    }

    /**
     * 判断当前线程是否是主线程
     *
     * @return true表示当前是在主线程中运行
     */
    public static boolean isUIThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static void runOnUIThread(Runnable run) {
        if (isUIThread()) {
            run.run();
        } else {
            mHandler.post(run);
        }
    }

    private static Toast mToast;

    /**
     * 可以在子线程中调用
     *
     * @param msg toast内容
     */
    public static void showToast(final String msg) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
                }
                mToast.setText(msg);
                mToast.show();
            }
        });
    }

}
