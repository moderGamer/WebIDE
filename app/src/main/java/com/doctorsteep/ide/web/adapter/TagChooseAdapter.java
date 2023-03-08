package com.doctorsteep.ide.web.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.TagChooseAdapter;
import java.util.ArrayList;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

public class TagChooseAdapter extends RecyclerView.Adapter<TagChooseAdapter.TagChooseViewHolder> {

    ArrayList<String> attrList;
	Context context;

    public TagChooseAdapter(ArrayList<String> attrList, Context context) {
        this.attrList = attrList;
		this.context = context;
    }

    @Override
    public TagChooseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_choose_attr, parent, false);
        TagChooseViewHolder viewHolder = new TagChooseViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TagChooseViewHolder holder, final int position) {
        holder.check.setText(attrList.get(position));

		holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {

				}
			});
    }

    @Override
    public int getItemCount() {
        return attrList.size();
    }

    public static class TagChooseViewHolder extends RecyclerView.ViewHolder{

        protected CheckBox check;

        public TagChooseViewHolder(View itemView) {
            super(itemView);
            check = (CheckBox) itemView.findViewById(R.id.check_id);
		}
    }
}
