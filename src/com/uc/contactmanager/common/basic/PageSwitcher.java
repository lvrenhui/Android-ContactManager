package com.uc.contactmanager.common.basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.uc.contactmanager.activity.BaseFragmentActivity;
import com.uc.contactmanager.activity.MainActivity;
import com.uc.contactmanager.activity.SubActivity;
import com.uc.contactmanager.app.ContactApplication;
import com.uc.contactmanager.common.tool.L;

import java.util.Stack;

/**
 * Created by lvrh on 15/5/18.
 */
public class PageSwitcher {

    /**
     * Activity栈
     */
    private static Stack<Activity> mBackStack = new Stack<Activity>();

    /**
     *
     * 跳转到主页面，附带Bundle参数
     *
     * @param bundle
     * */
    public static void switchToMainPage(Bundle bundle) {
        ContactApplication app = ContactApplication.getInstance();
        Intent intent = new Intent(app, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.INTENT_REQUEST, MainActivity.INTENT_REQUEST_TO_MAIN);
        if (bundle != null) {
            intent.putExtra(MainActivity.INTENT_TO_MAIN_EXTRA_ARGS, bundle);
        }

        startActivity(intent);
//        Activity currentAct = SubActivity.getSubActivity();
//        if (currentAct != null) {
//            currentAct.finish();
//        }
    }


    /**
     *
     * 跳转到二级页面，不带参数
     *
     * @param context
     * @param fragmentType
     * */
    public static void switchToSubPage(Context context, int fragmentType) {
        switchToSubPage(context, fragmentType, null);
    }

    public static void switchToSubPage2(Fragment fragment, int fragmentType, Bundle bundle) {
        Intent intent = new Intent(fragment.getActivity(), SubActivity.class);
        intent.putExtra(SubActivity.INTENT_EXTRA_FRAGMENT_TYPE, fragmentType);
        if (bundle != null) {
            intent.putExtra(BaseFragmentActivity.INTENT_EXTRA_FRAGMENT_ARGS, bundle);
        }
        fragment.startActivityForResult(intent, 1);
    }

    /**
     *
     * 跳转到二级页面，附带参数对paramKey-paramValue
     *
     * @param context
     * @param fragmentType
     * @param paramKey
     * @param paramValue
     * */
    public static void switchToSubPage(Context context, int fragmentType, String paramKey, String paramValue) {
        Bundle bundle = new Bundle();
        bundle.putString(paramKey, paramValue);
        switchToSubPage(context, fragmentType, bundle);
    }

    /**
     *
     * 跳转到二级页面，附带参数对paramKey-paramValue
     *
     * @param context
     * @param fragmentType
     * @param paramKey
     * @param paramValue
     * */
    public static void switchToSubPage(Context context, int fragmentType, String paramKey, int paramValue) {
        Bundle bundle = new Bundle();
        bundle.putInt(paramKey, paramValue);
        switchToSubPage(context, fragmentType, bundle);
    }

    /**
     *
     * 跳转到二级页面，附带Bundle参数
     *
     * @param context
     * @param fragmentType
     * @param bundle
     * */
    public static void switchToSubPage(Context context, int fragmentType, Bundle bundle) {
        Intent intent = new Intent(context, SubActivity.class);
        intent.putExtra(SubActivity.INTENT_EXTRA_FRAGMENT_TYPE, fragmentType);
        if (bundle != null) {
            intent.putExtra(BaseFragmentActivity.INTENT_EXTRA_FRAGMENT_ARGS, bundle);
        }
        startActivity(intent);
    }

    /**
     *
     * 跳转到二级页面，附带Bundle参数,还有fragment缓存参数
     *
     * @param context
     * @param fragmentType
     * @param bundle
     * */
    public static void switchToSubPage(Context context, int fragmentType, boolean useCache, Bundle bundle) {
        ContactApplication app = ContactApplication.getInstance();
        Intent intent = new Intent(app, SubActivity.class);
        intent.putExtra(SubActivity.INTENT_EXTRA_FRAGMENT_TYPE, fragmentType);
//        intent.putExtra(SubActivity.INTENT_EXTRA_FRAGMENT_USE_CACHE, useCache);
        if (bundle != null) {
            intent.putExtra(BaseFragmentActivity.INTENT_EXTRA_FRAGMENT_ARGS, bundle);
        }
        startActivity(intent);
    }



    private static void startActivity(Intent intent) {
        Activity act = MainActivity.getMainActivity();

        if (act != null) {
            try {
                act.startActivity(intent);
            } catch (Exception e) {
                L.e(e);
            }
        }
    }

    /**
     * Activity进栈
     */
    public static void pushAtivityStack(Activity activity) {
        L.d("AtivityStack# onpushSize = %d", mBackStack.size());
        if (mBackStack.size() > 0) {
            Activity topActivity = mBackStack.peek();
            if (topActivity.getClass().isInstance(activity)) {
                return;
            }
        }
        mBackStack.push(activity);
        L.d("AtivityStack# pushedSize = %d", mBackStack.size());
    }

    /**
     * 弹出栈顶Activity类对象
     */
    public static Activity popAtivityStack() {
        if (mBackStack.isEmpty()) {
            return null;
        }
        L.d("AtivityStack# popSize = %d", mBackStack.size());
        return mBackStack.pop();
    }

    /**
     * 清空Activity栈
     */
    public static void clearAllActivity() {
        L.d("AtivityStack# clearAllActivity");
        mBackStack.clear();
    }


    /**
     * 获取回退栈栈顶Activity类对象
     */
    @Nullable
    public static Activity peekActivityStack() {
        if (mBackStack.isEmpty()) {
            return null;
        }
        return mBackStack.peek();
    }

}
