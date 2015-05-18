package com.uc.contactmanager.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.uc.contactmanager.Fragment.BaseFragment;
import com.uc.contactmanager.Fragment.FragmentFactory;
import com.uc.contactmanager.common.basic.PageSwitcher;


/**
 * Created by yangyl on 7/16/14.
 */
public class BaseFragmentActivity extends FragmentActivity {

    public final static String INTENT_EXTRA_FRAGMENT_TYPE = "type";

    public final static String INTENT_EXTRA_FRAGMENT_ARGS = "args";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        PageSwitcher.pushAtivityStack(this);
    }

    private FragmentFactory mFragmentFactory;

    public FragmentFactory getFragmentFactory() {
        if (mFragmentFactory == null) {
            mFragmentFactory = new FragmentFactory(this);
        }
        return mFragmentFactory;
    }

    public boolean isFragmentInCache(BaseFragment f) {
        if (mFragmentFactory != null) {
            return mFragmentFactory.getFragmentFromCache(f.getFragmentType()) != null;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityStatusManager.getInstance().pushActivity(this);
    }

    @Override
    protected void onStop() {
        ActivityStatusManager.getInstance().popActivity(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PageSwitcher.popAtivityStack();
    }
}

