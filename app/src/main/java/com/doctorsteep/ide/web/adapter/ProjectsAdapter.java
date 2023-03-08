package com.doctorsteep.ide.web.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.ProjectsAdapter;
import com.doctorsteep.ide.web.utils.DFUtils;
import com.doctorsteep.ide.web.utils.ProjectUtils;
import java.util.ArrayList;
import java.io.File;
import android.widget.Toast;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectsViewHolder> {

    ArrayList<String> projectsList;
	Context context;

    public ProjectsAdapter(ArrayList<String> projectsList, Context context) {
        this.projectsList = projectsList;
		this.context = context;
    }

    @Override
    public ProjectsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_projects, parent, false);
        ProjectsViewHolder viewHolder=new ProjectsViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ProjectsViewHolder holder, final int position) {
        holder.text.setText(projectsList.get(position));
		
		try {
			holder.date.setText(DFUtils.dateDF(ProjectUtils.pathProjects + File.separator + projectsList.get(position)));
		} catch (Exception e) {}
		
		holder.menu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					ProjectUtils.showProjectMenu(context, p1, ProjectUtils.pathProjects + File.separator + projectsList.get(position));
				}
			});
		holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					ProjectUtils.openProject(context, ProjectUtils.pathProjects + File.separator + projectsList.get(position));
				}
			});
		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View p1) {
					ProjectUtils.showProjectMenu(context, p1, ProjectUtils.pathProjects + File.separator + projectsList.get(position));
					return false;
				}
			});
    }

    @Override
    public int getItemCount() {
        return projectsList.size();
    }

    public static class ProjectsViewHolder extends RecyclerView.ViewHolder{

        protected TextView text, date;
		protected ImageView menu;
		
        public ProjectsViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_id);
			date = itemView.findViewById(R.id.date_id);
			menu = itemView.findViewById(R.id.menu_id);
        }
    }
}
