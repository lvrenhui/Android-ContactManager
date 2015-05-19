package com.uc.contactmanager.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.Animation;
import com.uc.contactmanager.activity.BaseSubActivity;
import com.uc.contactmanager.app.ContactApplication;
import com.uc.contactmanager.common.tool.L;

/**
 * Created by lvrh on 15/5/18.
 */
public class BaseFragment extends Fragment{

    public static final String ARGS_URL = "url";
    public static final String ARGS_TAB_INDEX = "args_tab_index";
    public static final String ARGS_H5_PARAMS = "h5Params";
    private int mFragmentType;
    private Bundle mArgs = new Bundle();

    protected ContactApplication mApp;
//    protected MessagePump mMessagePump;
    protected View mRootView;

//    protected MenuLogicInfo mMenuLogicInfo;
//    protected ArrayList<PopupMenuItem> mMenuList;

    public int getFragmentType() {
        return mFragmentType;
    }

    public void setFragmentType(int fragmentType) {
        this.mFragmentType = fragmentType;
    }

    public View findViewById(int id) {
        return mRootView == null ? null : mRootView.findViewById(id);
    }

    protected String getName() {
        return ((Object) this).getClass().getSimpleName();
    }

    protected BaseSubActivity getSubActivity() {
        return (BaseSubActivity) getActivity();
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        L.d("BaseFragment# onInflate " + getName());
        super.onInflate(activity, attrs, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        L.d("BaseFragment# onActivityCreated " + getName());
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d("BaseFragment# onActivityResult " + getName());
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Activity activity) {
        L.d("BaseFragment# onAttach " + getName());
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        L.d("BaseFragment# onCreate " + getName());
        super.onCreate(savedInstanceState);
        mApp = ContactApplication.getInstance();
//        mMessagePump = mApp.getMessagePump();
//        registerMessagePump();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        L.d("BaseFragment# onCreateView " + getName());
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeAllViewsInLayout();
            }
            return mRootView;
        }
//        CollectFluencyData.getInstance().CalFPS(getFragmentType()+"",mRootView);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        L.d("BaseFragment# onDestroy " + getName());
        super.onDestroy();
        unRegisterMessagePump();

    }

    @Override
    public void onDestroyView() {
        L.d("BaseFragment# onDestroyView " + getName());
//        hideKeyboard();
        super.onDestroyView();
        //对于不在缓存区的Fragment才调用反注册。否则。如果Fragment在缓存区，destroy的时候就反注册，将收不到Message，
        //而onCreateView的时候mRootView非空，不会重新创建，可能导致状态错乱

    }

    protected void hideKeyboard() {
        Activity activity = getActivity();
        if(activity == null) return;
        try {
            View v = getActivity().getCurrentFocus();
            if(v != null){
//                Util.hideKeyboard(mApp, v.getWindowToken());
            }
        } catch (Exception e) {
            L.w(e);
        }
    }

    @Override
    public void onDetach() {
        L.d("BaseFragment# onDetach " + getName());
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        L.d("BaseFragment# onHiddenChanged " + getName() + " hidden " + hidden);
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onLowMemory() {
        L.d("BaseFragment# onLowMemory " + getName());
        super.onLowMemory();
    }

    @Override
    public void onPause() {

        L.d("BaseFragment# onPause " + getName());
        hideKeyboard();
        super.onPause();
    }

    @Override
    public void onResume() {
        L.d("BaseFragment# onResume " + getName());
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        L.d("BaseFragment# onSaveInstanceState " + getName());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        L.d("BaseFragment# onStart " + getName());
        super.onStart();
    }

    @Override
    public void onStop() {
        L.d("BaseFragment# onStop " + getName());
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        L.d("BaseFragment# onViewCreated " + getName());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        L.d("BaseFragment# onConfigurationChanged " + getName() + " newConfig: " + newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        L.d("BaseFragment# onContextItemSelected " + getName());
        return super.onContextItemSelected(item);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        L.d("BaseFragment# onCreateAnimation " + getName());
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        L.d("BaseFragment# onCreateContextMenu " + getName());
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        L.d("BaseFragment# onCreateOptionsMenu " + getName());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyOptionsMenu() {
        L.d("BaseFragment# onDestroyOptionsMenu " + getName());
        super.onDestroyOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        L.d("BaseFragment# onOptionsItemSelected " + getName());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        L.d("BaseFragment# onOptionsMenuClosed " + getName());
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        L.d("BaseFragment# onPrepareOptionsMenu " + getName());
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        L.d("BaseFragment# onPrepareOptionsMenu " + getName());
        super.onViewStateRestored(savedInstanceState);
    }

    public void setBundleArguments(Bundle args) {
        mArgs = args;
    }

    public Bundle getBundleArguments() {
        return mArgs;
    }

    public void onBackPressed() {
        if (getActivity() != null) {
//            hideKeyboard();
            getActivity().onBackPressed();
        }

    }

    public void scrollToTop() {

    }

    /**
     * 处理返回事件
     * @return true表示在fragment内部已经完成了对返回事件的处理，外部不需要再处理;
     * false表示外部需要对返回事件继续处理
     * */
    public boolean goBack() {
        return false;
    }

    public void popCurrentFragment() {
        Activity activity = getActivity();
        if (activity instanceof BaseSubActivity) {
            ((BaseSubActivity) activity).popFragment();
        }
    }

    protected void registerMessagePump() {
        L.d("BaseFragment# registerMessagePump " + getName());
    }

    protected void unRegisterMessagePump() {
        L.d("BaseFragment# unRegisterMessagePump " + getName());
    }

//    public void runOnUiThread(Runnable action) {
//        if (Looper.myLooper() != Looper.getMainLooper()) {
//            TaskExecutor.runTaskOnUiThread(action);
//        } else {
//            action.run();
//        }
//
//    }

    public String originFrom() {
        return null;
    }


    /**
     *  在BaseFragment里面取更多菜单的按钮判断展示还是隐藏
     *  FIXME 需要所有子界面在命名以及排版上都统一，否则会出现获取不到这个按钮或者标题部分偏右的UI问题
     * **/
//    private void toggleMoreBtn(int size){
//        if(mRootView != null) {
//            View btnMore = mRootView.findViewById(R.id.btnMore);
//            if (btnMore != null) {
//                btnMore.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
//            }
//        }
//    }

//    public void showErrorToast(String msg){
//        if(TextUtils.isEmpty(msg)){
//            mApp.showToastMessage(R.string.operate_fail_tips);
//        }else{
//            mApp.showToastMessage(msg);
//        }
//    }
}

