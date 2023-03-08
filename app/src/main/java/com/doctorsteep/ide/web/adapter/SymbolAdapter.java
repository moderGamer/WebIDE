package com.doctorsteep.ide.web.adapter;

import android.annotation.SuppressLint;
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
import com.doctorsteep.ide.web.adapter.SymbolAdapter;
import com.doctorsteep.ide.web.data.FileManagerProject;
import com.doctorsteep.ide.web.utils.FileUtils;
import com.doctorsteep.ide.web.utils.LangSyntax;
import java.io.File;
import java.util.ArrayList;
import com.doctorsteep.ide.web.settings.EditCodeSettings;
import com.doctorsteep.ide.web.utils.SyntaxUtils;

public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.SymbolViewHolder> {

    ArrayList<String> symbolList;
	Context context;

    public SymbolAdapter(ArrayList<String> symbolList, Context context) {
        this.symbolList = symbolList;
		this.context = context;
    }

    @Override
    public SymbolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_symbol, parent, false);
        SymbolViewHolder viewHolder = new SymbolViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SymbolViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.text.setText(symbolList.get(position).toString());

		holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View p1) {
					if(symbolList.get(position).equals(SyntaxUtils.TAB)) {
						EditorActivity.codeEditor.insertTab();
						return;
					}
					if (symbolList.get(position).equals(SyntaxUtils.COMMENT)) {
					    EditorActivity.codeEditor.goCommentSelectedLine("default");
					    return;
                    }
                    if (symbolList.get(position).equals(SyntaxUtils.COMMENT_PHP)) {
                        EditorActivity.codeEditor.goCommentSelectedLine("php");
                        return;
                    }
                    EditorActivity.codeEditor.addTextCursorPosition(symbolList.get(position).toString());
				}
			});
    }

    @Override
    public int getItemCount() {
        return symbolList.size();
    }

    public static class SymbolViewHolder extends RecyclerView.ViewHolder{

        protected TextView text;
		
        public SymbolViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text_id);
		}
    }
}
