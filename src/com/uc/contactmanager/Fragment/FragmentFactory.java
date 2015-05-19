package com.uc.contactmanager.Fragment;

/**
 * Created by lvrh on 15/5/18.
 */

import android.content.Context;
import android.util.SparseArray;


import android.content.Context;
import android.util.SparseArray;
import com.uc.contactmanager.common.tool.L;


/**
 * Created by yangyl on 7/1/14.
 */
public class FragmentFactory {

    public final static int FRAGMENT_TYPE_CONTACT_LIST = 1001;
    public static final int FRAGMENT_TYPE_CONTACT = 1002;


    private SparseArray<BaseFragment> mFragmentCache = new SparseArray<BaseFragment>();

    private Context mContext;

    public FragmentFactory(Context context) {
        mContext = context;
    }

    public BaseFragment getFragment(int type, boolean useCache) {
        BaseFragment fragment = null;

        if (useCache && (fragment = mFragmentCache.get(type)) != null) {
            L.d("Hit the Fragment Cache, Fragment: %d, Type: %d", fragment.hashCode(), type);
            return fragment;
        }
        L.d("FragmentFactory create fragment, type: " + type + " useCache: " + useCache);
        switch (type) {
            case FRAGMENT_TYPE_CONTACT_LIST:
                fragment = new ContactListFragment();
                break;
            case FRAGMENT_TYPE_CONTACT:
                fragment=new ContactFragment();
                break;
            default:
                throw new RuntimeException(String.format("Can not find fragment with type: %d" , type));

        }

        if (fragment != null) {
            fragment.setFragmentType(type);
        }

        if (useCache) {
            L.d("Set the Fragment Cache, Fragment: %d, Type: %d", fragment.hashCode(), type);
            mFragmentCache.put(type, fragment);
        }

        return fragment;
    }

    public BaseFragment getFragmentFromCache(int type) {
        BaseFragment fragment = mFragmentCache.get(type);
        return fragment;
    }

    public void removeFragment(int type) {
        mFragmentCache.remove(type);
    }

    public void clearCache() {
        mFragmentCache.clear();
    }
}

