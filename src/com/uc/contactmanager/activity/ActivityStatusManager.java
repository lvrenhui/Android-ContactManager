package com.uc.contactmanager.activity;

import android.app.Activity;
import com.uc.contactmanager.common.tool.L;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lvrh on 15/5/18.
 */
public class ActivityStatusManager {
    private static ActivityStatusManager sInstance;

    public static ActivityStatusManager getInstance() {
        if (sInstance == null) {
            synchronized (ActivityStatusManager.class) {
                if (sInstance == null) {
                    sInstance = new ActivityStatusManager();
                }
            }
        }
        return sInstance;
    }


    private List<String> mActivityNameList = new ArrayList<String>();
    private HashSet<WeakReference<ActivityStatusListener>> mListeners =
            new HashSet<WeakReference<ActivityStatusListener>>();

    private ActivityStatusManager() {}


    public void registerListener(ActivityStatusListener listener) {
        if (listener == null) {
            return;
        }
        mListeners.add(new WeakReference<ActivityStatusListener>(listener));
    }

    public void unregisterListener(ActivityStatusListener listener) {
        if (listener == null) {
            return;
        }

        WeakReference<ActivityStatusListener> listenerToRemove = null;
        for (WeakReference<ActivityStatusListener> listenerRef : mListeners) {
            if (listenerRef.get() != null && listenerRef.get() == listener) {
                listenerToRemove = listenerRef;
                break;
            }
        }

        if (listenerToRemove != null) {
            mListeners.remove(listenerToRemove);
        }
    }

    public void pushActivity(final Activity activity) {
//        TaskExecutor.runTaskOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        if (activity != null) {
            String activityName = activity.getClass().getName() + activity.hashCode();
            L.d("pushActivity: " + activityName);
            mActivityNameList.add(activity.getClass().getName() + activity.hashCode());
            if (mActivityNameList.size() == 1) {
                notifyOnAppIntoForeground();
            }
        }
//            }
//        });
    }

    public void popActivity(final Activity activity) {
//        TaskExecutor.runTaskOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        if (activity != null) {
            mActivityNameList.remove(activity.getClass().getName() + activity.hashCode());
            if (mActivityNameList.size() == 0) {
                notifyOnAppIntoBackground();
            }
        }
//            }
//        });
    }

    public boolean isAppForeground() {
        return mActivityNameList.size() > 0;
    }

    private void notifyOnAppIntoForeground() {
        L.i("app into foreground!");
        for (WeakReference<ActivityStatusListener> listenerRef : mListeners) {
            if (listenerRef.get() != null) {
                listenerRef.get().onAppIntoForeground();
            }
        }
    }

    private void notifyOnAppIntoBackground() {
        L.i("app into background!");
        for (WeakReference<ActivityStatusListener> listenerRef : mListeners) {
            if (listenerRef.get() != null) {
                listenerRef.get().onAppIntoBackground();
            }
        }
    }

    public interface ActivityStatusListener {
        public void onAppIntoForeground();
        public void onAppIntoBackground();
    }
}
