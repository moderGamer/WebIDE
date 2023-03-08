package com.doctorsteep.ide.web.adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.doctorsteep.ide.web.R;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.doctorsteep.ide.web.language.LanguageHTML;

public class SyntaxHTMLAdapter extends ArrayAdapter {

    private List<String> dataList;
    private Context mContext;
    private int itemLayout;

    private ListFilter listFilter = new ListFilter();
    private List<String> dataListAllItems;



    public SyntaxHTMLAdapter(Context context, int resource, List<String> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        }
		TextView strName = (TextView) view.findViewById(R.id.text_id);
		TextView strType = (TextView) view.findViewById(R.id.type_id);
		ImageView imgType = (ImageView) view.findViewById(R.id.img_id);

		try {
			if (LanguageHTML.htmlTag().contains(getItem(position))) {
				imgType.setImageResource(R.drawable.ic_html_48);
				strType.setText("tag");
			} if (LanguageHTML.htmlAttr().contains(getItem(position))) {
				imgType.setImageResource(R.drawable.ic_html_48);
				strType.setText("attr");
			}
		}
		catch (Exception e){}
        strName.setText(getItem(position));
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<String>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<String> matchValues = new ArrayList<String>();

                for (String dataItem : dataListAllItems) {
                    if (dataItem.toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<String>)results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
} 

