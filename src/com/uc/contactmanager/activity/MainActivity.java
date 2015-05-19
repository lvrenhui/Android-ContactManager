package com.uc.contactmanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.uc.contactmanager.Fragment.ContactListFragment;
import com.uc.contactmanager.Fragment.FragmentFactory;
import com.uc.contactmanager.R;
import com.uc.contactmanager.app.ContactApplication;
import com.uc.contactmanager.common.basic.PageSwitcher;

public class MainActivity extends BaseFragmentActivity {
    /**
     * Called when the activity is first created.
     */
    public final static String INTENT_REQUEST = "request";

    //跳转到MainActivity的某个Fragment
    public final static String INTENT_REQUEST_TO_MAIN = "request_to_main";
    public final static String INTENT_TO_MAIN_EXTRA_ARGS = "to_main_args";

    private static Activity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=this;
//        setContentView(R.layout.main);

        PageSwitcher.switchToSubPage(ContactApplication.getInstance(), FragmentFactory.FRAGMENT_TYPE_CONTACT_LIST);

//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//
//        Fragment fragment = new ContactListFragment();
////        ft.add(R.id.container, fragment);
//        ft.replace(R.id.container, fragment);
//        ft.commit();
    }

    public static Activity getMainActivity() {
        return mainActivity;
    }


}
