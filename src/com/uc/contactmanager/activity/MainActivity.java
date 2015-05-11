package com.uc.contactmanager.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.uc.contactmanager.Fragment.ContactFragment;
import com.example.contactmanager.R;
import com.uc.contactmanager.Fragment.ContactListFragment;

public class MainActivity extends FragmentActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment = new ContactListFragment();
        ft.add(R.id.container, fragment);
        ft.commit();
    }
}
