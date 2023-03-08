package com.doctorsteep.ide.web.adapter;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.SymbolAdapter;
import com.doctorsteep.ide.web.data.FileManagerProject;
import com.doctorsteep.ide.web.ei.EIData;
import com.doctorsteep.ide.web.ei.utils.AddonUtils;
import com.doctorsteep.ide.web.model.AddonsModel;
import com.doctorsteep.ide.web.utils.FileUtils;
import com.doctorsteep.ide.web.utils.LangSyntax;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.doctorsteep.ide.web.settings.EditCodeSettings;

public class AddonsEiAdapter extends RecyclerView.Adapter<AddonsEiAdapter.AddonsEiViewHolder> {

    List<AddonsModel> addonsEiList;
    Context context;

    public AddonsEiAdapter(List<AddonsModel> addonsEiList, Context context) {
        this.addonsEiList = addonsEiList;
        this.context = context;
    }

    @Override
    public AddonsEiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_addon_ei, parent, false);
        AddonsEiViewHolder viewHolder = new AddonsEiViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AddonsEiViewHolder holder, final int position) {
        final AddonsModel model = addonsEiList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AddonUtils.openAddon(context, model.getPath());
                } catch (Exception e) {
                    Toast.makeText(context, "Error open addon: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.name.post(new Runnable() {
            @Override
            public void run() {
                holder.name.setText(model.getName());
            }
        });
        holder.description.post(new Runnable() {
            @Override
            public void run() {
                holder.description.setText(model.getDescription());
            }
        });
        holder.id.post(new Runnable() {
            @Override
            public void run() {
                holder.id.setText(model.getId() + "");
            }
        });
        holder.version.post(new Runnable() {
            @Override
            public void run() {
                holder.version.setText(model.getVersion() + " | " + model.getVersionCode());
            }
        });
        try {
            if (!model.getIcon().trim().equals("")) {
                File imgFile = new File(model.getPath() + File.separator + model.getIcon());
                File imgFile2 = new File(File.separator + model.getIcon());
                if (imgFile.exists()) {
                    final Bitmap icon = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    holder.icon.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.icon.setImageBitmap(icon);
                        }
                    });
                } if (imgFile2.exists()) {
                    final Bitmap icon = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
                    holder.icon.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.icon.setImageBitmap(icon);
                        }
                    });
                }
            }
        } catch (Exception e) {}


    }

    @Override
    public int getItemCount() {
        return addonsEiList.size();
    }

    public static class AddonsEiViewHolder extends RecyclerView.ViewHolder{

        protected TextView name, version, description, id;
        protected ImageView icon;

        public AddonsEiViewHolder(View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.textName);
            description =  itemView.findViewById(R.id.textDescription);
            id =  itemView.findViewById(R.id.textId);
            version =  itemView.findViewById(R.id.textVersion);
            icon =  itemView.findViewById(R.id.imageIcon);
        }
    }
}

