package org.bsc.com.india;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sanjit on 11/11/18.
 */

public class SubDashboardAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Model.Categories.Subcategories> subcategories;

    // Constructor
    public SubDashboardAdapter(Context c, ArrayList<Model.Categories.Subcategories> subcategories) {
        mContext = c;
        this.subcategories = subcategories;

    }

    public int getCount() {
        return subcategories.size();
    }

    public Object getItem(int position) {
        return subcategories.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_view, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Model.Categories.Subcategories currentItem = (Model.Categories.Subcategories) getItem(position);


        if(currentItem!=null){
            viewHolder.textView.setText(currentItem.getTitle());
        }
        return convertView;
    }

    private class ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.textView);
        }
    }
}