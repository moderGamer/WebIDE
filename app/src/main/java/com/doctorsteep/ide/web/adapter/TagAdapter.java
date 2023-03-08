package com.doctorsteep.ide.web.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.TagAdapter;
import com.doctorsteep.ide.web.data.Data;
import com.doctorsteep.ide.web.utils.LangSyntax;
import java.util.ArrayList;
import com.doctorsteep.ide.web.data.TagManager;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    ArrayList<String> tagList;
	Context context;

    public TagAdapter(ArrayList<String> tagList, Context context) {
        this.tagList = tagList;
		this.context = context;
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tags, parent, false);
        TagViewHolder viewHolder = new TagViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TagViewHolder holder, final int position) {
        holder.text.setText(tagList.get(position));
		holder.value.setText(LangSyntax.checkHTML_TAG(tagList.get(position), true));
		
		holder.info.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					Data.openUrl(context, TagManager.urlTagInfo + tagList.get(position), "Documentation");
				}
			});
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder{

        protected TextView text;
		protected TextView value;
		protected ImageView info;
		
        public TagViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_id);
			value = itemView.findViewById(R.id.value_id);
			info = itemView.findViewById(R.id.info_id);
		}
    }
}
