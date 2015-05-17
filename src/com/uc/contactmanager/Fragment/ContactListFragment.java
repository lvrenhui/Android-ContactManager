package com.uc.contactmanager.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.example.contactmanager.R;
import com.uc.contactmanager.biz.ContactAdapter;
import com.uc.contactmanager.common.basic.BasicListener;
import com.uc.contactmanager.common.tool.ContactUtil;
import com.uc.contactmanager.common.tool.L;
import com.uc.contactmanager.model.Contact;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lvrh on 2015/5/9.
 */
public class ContactListFragment extends Fragment {

    private List<Contact> mContactlist = new ArrayList<>();
    ;
    private View mRootView;
    private ListView mListView;
    private View mLoadingView;
    private View loadMoreView;
    private Button loadMoreButton;

    private static final int LOADING_HIDE = 0;
    private static final int LOADING_SHOW = 1;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // 接收消息并且去更新UI线程上的控件内容
            if (msg.what == LOADING_SHOW) {
                showLoading(true);
            } else {
                ContactAdapter contactAdapter = new ContactAdapter(getActivity(), mContactlist);
                mListView.setAdapter(contactAdapter);
                showLoading(false);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.contact_list_page, null);
        mLoadingView = mRootView.findViewById(R.id.loading);
        mListView = (ListView) mRootView.findViewById(R.id.contactlist);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Contact c = mContactlist.get(position);
                Toast.makeText(getActivity().getApplicationContext(), c.getMobile(), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(),ContactFragment.class);
                intent.putExtra("pos", position);
                startActivity(intent);

            }
        });
        getContactList();

//        loadMoreView = inflater.inflate(R.layout.loadmore, null);
//        loadMoreButton = (Button) loadMoreView.findViewById(R.id.loadMoreButton);
//        mListView.addFooterView(loadMoreButton);

        return mRootView;
    }

    private void getContactList() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    ContactUtil contactUtil = ContactUtil.instance(getActivity().getApplicationContext());
                    JSONObject contactInfo = contactUtil.getContactInfo(new ContactListener());

                    Iterator it = contactInfo.keys();
                    while (it.hasNext()) {
                        String code = (String) it.next();
                        String name = "";
                        String mobile = "";
                        String address = "";

                        JSONObject people = contactInfo.getJSONObject(code);
                        if (people.has("lastname")) {
                            name = people.getString("lastname");
                        }
                        if (people.has("mobile")) {
                            mobile = people.getString("mobile");
                        }
                        Contact c = new Contact(code, name, mobile, address);
                        mContactlist.add(c);
                    }
                    //通过监听实现加载动画，非常吊
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void showLoading(boolean b) {
        int visible = b ? View.VISIBLE : View.INVISIBLE;
        mLoadingView.setVisibility(visible);
    }

    private class ContactListener implements BasicListener {

        @Override
        public void onStarted() {
            L.d("start");
//            showLoading(true);
            Message msg = new Message();
            msg.what = LOADING_SHOW;
            handler.sendMessage(msg);
        }

        @Override
        public void onFinished() {
            L.d("end");
            Message msg = new Message();
            msg.what = LOADING_HIDE;
            handler.sendMessage(msg);
        }
    }
}
