package com.uc.contactmanager.biz;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.example.contactmanager.R;
import com.uc.contactmanager.model.Contact;

import java.util.List;

/**
 * Created by lvrh on 2015/5/10.
 */
public class ContactAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Contact> mData;
    private int mIconSize = 0;
    private int mImageSize = 0;

    public ContactAdapter(Context context, List<Contact> data) {
        this.mInflater = LayoutInflater.from(context);
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.contact, null);

            holder.tvName = (TextView) convertView.findViewById(R.id.contact_name);
            holder.tvMobile = (TextView) convertView.findViewById(R.id.contact_mobile);
            convertView.setTag(holder);
            convertView.setClickable(false);
            convertView.setFocusable(false);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = mData.get(position).getName();
        String mobile = mData.get(position).getMobile();
        holder.tvName.setText(name);
        holder.tvMobile.setText(mobile);

        return convertView;
    }

    public Contact getItemByCode(String code) {
        if (code.isEmpty()) {
            return null;
        }
        for (Contact model : mData) {
            if (code.equals(model.getCode())) {
                return model;
            }
        }
        return null;
    }

    public final class ViewHolder {
        TextView tvName;
        TextView tvMobile;
    }
}
