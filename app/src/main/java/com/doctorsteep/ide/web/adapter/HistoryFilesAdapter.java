package com.doctorsteep.ide.web.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.HistoryFilesAdapter;
import com.doctorsteep.ide.web.utils.FileUtils;
import com.doctorsteep.ide.web.utils.LangSyntax;
import com.doctorsteep.ide.web.utils.SyntaxUtils;
import java.io.File;
import java.util.ArrayList;
import android.widget.ImageView;
import android.nfc.cardemulation.HostApduService;
import android.app.Activity;
import com.doctorsteep.ide.web.data.FileManagerProject;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

public class HistoryFilesAdapter extends RecyclerView.Adapter<HistoryFilesAdapter.HistoryFilesViewHolder> {

    ArrayList<String> historyFilesList;
	Context context;

    public HistoryFilesAdapter(ArrayList<String> historyFilesList, Context context) {
        this.historyFilesList = historyFilesList;
		this.context = context;
    }

    @Override
    public HistoryFilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_files_history, parent, false);
        HistoryFilesViewHolder viewHolder = new HistoryFilesViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HistoryFilesViewHolder holder, final int position) {
        holder.text.setText(new File(historyFilesList.get(position)).getName());
		holder.path.setText(new File(historyFilesList.get(position)).getAbsolutePath());
		
		((Activity)context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(EditorActivity.patchFile.equals(historyFilesList.get(position))) {
						holder.active.setVisibility(View.VISIBLE);
					} else {
						holder.active.setVisibility(View.GONE);
					}
				}
			});
		
		holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(new File(historyFilesList.get(position)).isFile()) {
						if(historyFilesList.get(position).equals(EditorActivity.patchFile)) {
							//Toast.makeText(context, "File opened!", Toast.LENGTH_SHORT).show();
						} else {
							String name = new File(historyFilesList.get(position)).getName();
							FileUtils.writeFile(context, EditorActivity.codeEditor.getText().toString(), EditorActivity.patchFile);
							FileManagerProject.openFile(context, name, historyFilesList.get(position));
						}
					}
				}
			});
			
		holder.close.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					((Activity)context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(!EditorActivity.filesHistory.isEmpty()) {
									if(historyFilesList.get(position).equals(EditorActivity.patchFile)) {
										EditorActivity.patchFile = "";
										EditorActivity.codeEditor.setText("");
										//EditorActivity.setOpenFile(context, historyFilesList.get(historyFilesList.size() - 1));
									}
									EditorActivity.filesHistory.remove(historyFilesList.get(position));
									EditorActivity.setUpdateHistoryFiles(EditorActivity.filesHistory, ((Activity)context));
								}
							}
						});
				}
			});
    }

    @Override
    public int getItemCount() {
        return historyFilesList.size();
    }

    public static class HistoryFilesViewHolder extends RecyclerView.ViewHolder{

        protected TextView text;
		protected TextView path;
		protected ImageView close;
		protected LinearLayout active;

        public HistoryFilesViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_id);
			path = itemView.findViewById(R.id.path_id);
			close = itemView.findViewById(R.id.close_id);
			active = itemView.findViewById(R.id.active_id);
		}
    }
}
