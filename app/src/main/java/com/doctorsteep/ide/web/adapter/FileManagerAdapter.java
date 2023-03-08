package com.doctorsteep.ide.web.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.FileManagerAdapter;
import com.doctorsteep.ide.web.data.FileManagerProject;
import com.doctorsteep.ide.web.data.ManagerData;
import com.doctorsteep.ide.web.utils.FileUtils;
import com.doctorsteep.ide.web.utils.LangSyntax;
import com.doctorsteep.ide.web.utils.SyntaxUtils;
import java.io.File;
import java.util.ArrayList;
import java.io.FilenameFilter;

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.FileManagerViewHolder> {

    ArrayList<String> fileManagerList;
	Context context;
	
    public FileManagerAdapter(ArrayList<String> fileManagerList, Context context) {
        this.fileManagerList = fileManagerList;
		this.context = context;
    }

    @Override
    public FileManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_file_manager, parent, false);
        FileManagerViewHolder viewHolder=new FileManagerViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FileManagerViewHolder holder, final int position) {
        holder.text.setText(new File(fileManagerList.get(position)).getName());
		if(new File(fileManagerList.get(position)).isFile()) {
			holder.type.setImageResource(R.drawable.baseline_insert_drive_file_black_24);
		} if(new File(fileManagerList.get(position)).isDirectory()) {
			holder.type.setImageResource(R.drawable.baseline_folder_black_24);
		} if(new File(fileManagerList.get(position)).isHidden()) {
			holder.text.setAlpha(0.5f);
			holder.type.setAlpha(0.5f);
		} else {
			holder.text.setAlpha(1.0f);
			holder.type.setAlpha(1.0f);
		}
		
		if(FileManagerProject.valueP == 0) {
			if(fileManagerList.get(position).endsWith(ManagerData.INDEX_FILE)) {
				((Activity)context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							FileManagerProject.linOpenProject.setVisibility(View.VISIBLE);
						}
					});
			}
		}
		
		holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(new File(fileManagerList.get(position)).isDirectory()) {
						FileManagerProject.setListLoad(context, fileManagerList.get(position));
					} if(new File(fileManagerList.get(position)).isFile()) {
						if(fileManagerList.get(position).equals(EditorActivity.patchFile)) {
							//Toast.makeText(context, "File opened!", Toast.LENGTH_SHORT).show();
						} else {
							if (EditorActivity.OPENING == true) {
								String name = new File(fileManagerList.get(position)).getName();
								FileUtils.writeFile(context, EditorActivity.codeEditor.getText().toString(), EditorActivity.patchFile);
								FileManagerProject.openFile(context, name, fileManagerList.get(position));
							} else {
								FileManagerProject.openFileDefault(context, fileManagerList.get(position));
							}
						}
					}
				}
			});
		holder.menu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					FileManagerProject.showFileDirMenu(context, p1, fileManagerList.get(position));
				}
			});
		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View p1) {
					FileManagerProject.showFileDirMenu(context, p1, fileManagerList.get(position));
					return false;
				}
			});
    }

    @Override
    public int getItemCount() {
        return fileManagerList.size();
    }

    public static class FileManagerViewHolder extends RecyclerView.ViewHolder{

        protected TextView text;
		protected ImageView type, menu;

        public FileManagerViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_id);
			type = itemView.findViewById(R.id.type_id);
			menu = itemView.findViewById(R.id.menu_id);
        }
    }
}
