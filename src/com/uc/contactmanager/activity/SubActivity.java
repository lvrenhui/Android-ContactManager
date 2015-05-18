package com.uc.contactmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.uc.contactmanager.Fragment.BaseFragment;
import com.uc.contactmanager.R;
import com.uc.contactmanager.common.tool.L;

/**
 * Created by lvrh on 15/5/18.
 */
public class SubActivity extends BaseFragmentActivity {
    public final static String INTENT_EXTRA_FRAGMENT_TYPE = "type";
    public final static String INTENT_EXTRA_FRAGMENT_ARGS = "args";
    public final static String BUNDLE_FRAGMENT_CACHE = "cache";
    public final static String BUNDLE_FRAGMENT_ANIM = "anim";
    protected final static int LAYOUT_CONTAINER = android.R.id.content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isFinishing()) {
            return;
        }
        handleIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void init() {
        handleIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        Fragment baseFragment = getCurrentFragment();
        if(baseFragment instanceof BaseFragment){
            if(((BaseFragment)baseFragment).goBack()){
                return;
            }
        }

        popFragment();
    }

    /**
     * 关闭当前Fragment，不检查goback状态。
     * 用于解决 Bug：341481
     */
    public void closeWithoutCheckGoBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        } else {
            try {
                super.onBackPressed();
            } catch (Exception e) {
                L.w(e);
            }
        }
    }

    public void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        setIntent(intent);

        int type = intent.getIntExtra(INTENT_EXTRA_FRAGMENT_TYPE, 0);
        Bundle args = intent.getBundleExtra(INTENT_EXTRA_FRAGMENT_ARGS);
        boolean useCache = false;
        boolean useAnim = false;

        if (args != null) {
            useCache = args.getBoolean(BUNDLE_FRAGMENT_CACHE);
            useAnim = args.getBoolean(BUNDLE_FRAGMENT_ANIM);
        }

        if (!useAnim) {
            L.d("push fragment %s, cache %s, anim %s", type, useCache, false);
            pushFragment(type, args, LAYOUT_CONTAINER, useCache, false);

        } else if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
            L.d("push fragment %s, cache %s, anim %s", type, useCache, false);
            pushFragment(type, args, LAYOUT_CONTAINER, useCache, false);

        } else {
            L.d("push fragment %s, cache %s, anim %s", type, useCache, true);
            pushFragment(type, args, LAYOUT_CONTAINER, useCache, true);
        }
    }

    public boolean isFragmentInCache(BaseFragment f) {
//        if (mAddedFragments != null && mAddedFragments.contains(f)) {
//            return true;
//        }
        return false;
    }

    public Fragment getCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(LAYOUT_CONTAINER);
        return fragment;
    }

    public Fragment getFragmentByType(int type) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(type));
        if (fragment == null) {
            fragment = getFragmentFactory().getFragmentFromCache(type);
        }
        return fragment;
    }



    /**
     * 启动一个Fragment，当前Activity继承IActivityIntentHandler时，则直接调用IActivityIntentHandler.handleIntent
     * 不用再通过StartActivity启动。这样避免重复执行Activity.onCreate, 此方法默认播放压栈动画
     *
     * @param context 当前context
     * @param fragmentType 需要打开的fragment类型
     * @param args fragment参数
     */
    public void startFragment(Context context, int fragmentType, Bundle args) {
        startFragment(context, fragmentType, args, true);
    }

    public void startFragment(Context context, int fragmentType, Bundle args, boolean useAnim) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (args == null) {
            args = new Bundle();
        }

        intent.putExtra(INTENT_EXTRA_FRAGMENT_TYPE, fragmentType);
        args.putBoolean(BUNDLE_FRAGMENT_ANIM, useAnim);
        intent.putExtra(INTENT_EXTRA_FRAGMENT_ARGS, args);

        if (context instanceof IActivityIntentHandler) {
            // 如当前Activity已经实现IActivityIntentHandler，则使用接口打开Fragment
            handleIntent(intent);

        } else {
            intent.setClass(context, getClass());
            context.startActivity(intent);
            if (context instanceof Activity) {
                if (useAnim) {
                    ((Activity) context).overridePendingTransition(R.anim.open_slide_in, R.anim.open_slide_out);

                } else {
                    ((Activity) context).overridePendingTransition(0, 0);
                }
            }
        }
    }

    /**
     * 启动一个Activity，当Activity完成后接收回调
     *
     * @param fragment 当前Fragment
     * @param type 启动ResultActivity打开的fragment类名
     * @param args fragment参数
     * @param requestCode 回调code
     */
    public void startFragmentForResult(Fragment fragment, int type, Bundle args, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(fragment.getActivity(), getClass());
        intent.putExtra(INTENT_EXTRA_FRAGMENT_TYPE, type);
        intent.putExtra(INTENT_EXTRA_FRAGMENT_ARGS, args);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 将fragment加到backStack
     * @param type Fragment对应的clazz
     * @param args Fragment参数
     * @param container 放置Fragment的View节点
     * @param useCache 是否缓存，如果缓存则使用完需要调用removeFragment清除缓存
     * @param useAnim 是否播放切换动画
     */
    protected void pushFragment(int type, Bundle args, int container, boolean useCache, boolean useAnim) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fT = fragmentManager.beginTransaction();

        if (useAnim) {
            fT.setCustomAnimations(R.anim.open_slide_in, R.anim.open_slide_out,
                    R.anim.close_slide_in, R.anim.close_slide_out);
        }

        Fragment fragment = null;
        if (useCache) {
            fragment = fragmentManager.findFragmentByTag(String.valueOf(type));
            if (fragment == null) {
                fragment = (Fragment) getFragmentFactory().getFragment(type, useCache);
            }

        } else {
            removeFragment(type);
            fragment = (Fragment) getFragmentFactory().getFragment(type, false);
        }

        if (fragment == fragmentManager.findFragmentById(container)) {
            return;
        }

        if (fragment != null) {
            if (args != null) {
                ((BaseFragment) fragment).setBundleArguments(args);
            }

            fT.replace(container, fragment, String.valueOf(type));
            fT.addToBackStack(String.valueOf(type));
        }

        fT.commitAllowingStateLoss();
    }

    /**
     * 将fragment加到backStack
     * @param fragment Fragment
     * @param args Fragment参数
     * @param container 放置Fragment的View节点
     * @param useCache 是否缓存，如果缓存则使用完需要调用removeFragment清除缓存
     * @param useAnim 是否播放切换动画
     */
    private void pushFragment(Fragment fragment, Bundle args, int container, boolean useCache, boolean useAnim) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fT = fragmentManager.beginTransaction();

        if (useAnim) {
            fT.setCustomAnimations(R.anim.open_slide_in, R.anim.open_slide_out,
                    R.anim.close_slide_in, R.anim.close_slide_out);
        }

        if (fragment != null) {
            if (args != null) {
                ((BaseFragment) fragment).setBundleArguments(args);
            }
            fT.add(fragment, null);
            fT.replace(container, fragment, String.valueOf(fragment.getClass().hashCode()));
            fT.addToBackStack(String.valueOf(String.valueOf(fragment.getClass().hashCode())));
        }

        fT.commitAllowingStateLoss();
    }

    public void popFragment() {
        /* 解决fragment addToBackStack后，按返回键出现空白的Activity问题 */
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            finish();

        } else {
            try {
                super.onBackPressed();//FIXME : 这里不应该用onBackPressed来关闭页面
            } catch (Exception e) {
                L.w(e);
            }
        }
    }

    /**
     * 将指定fragment类型对应到Fragment替换调当前Fragment
     * @param type Fragment对应的clazz
     * @param args Fragment参数
     * @param container 放置Fragment的View节点
     */
    protected void replaceFragment(int type, Bundle args, int container) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fT = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(String.valueOf(type));
        if (fragment == null) {
            fragment = (Fragment) getFragmentFactory().getFragment(type, true);
        }

        if (fragment == fragmentManager.findFragmentById(container)) {
            return;
        }

        if (fragment != null) {
            L.d("replace fragment name: %s, hashCode: %s", type, fragment.hashCode());
            if (args != null) {
                fragment.setArguments(args);
            }
            fT.replace(container, fragment, String.valueOf(type));
            fT.addToBackStack(String.valueOf(type));
        }
        fT.commitAllowingStateLoss();
    }

    /**
     * 从缓存中移除Fragment
     * @param type
     */
    public void removeFragment(int type) {
        getFragmentFactory().removeFragment(type);
    }

    public interface IActivityIntentHandler {
        void handleIntent(Intent intent);
    }
}
