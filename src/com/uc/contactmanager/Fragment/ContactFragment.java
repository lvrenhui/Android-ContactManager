package com.uc.contactmanager.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.uc.contactmanager.R;

/**
 * Created by lvrh on 2015/5/9.
 */
public class ContactFragment extends BaseFragment {
    private String mobile = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            intent.getIntExtra("pos", 0);

        }

    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View view=inflater.inflate(R.layout.contact_page,null);
//        ((TextView)view.findViewById(R.id.i_name)).setText("lvrh");
//        ((TextView)view.findViewById(R.id.i_mobile)).setText("15018775638");
//
//        return view;
//    }


}
