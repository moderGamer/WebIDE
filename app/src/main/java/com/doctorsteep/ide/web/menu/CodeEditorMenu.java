package com.doctorsteep.ide.web.menu;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Toast;
import com.doctorsteep.ide.web.EditorActivity;

public class CodeEditorMenu implements OnCreateContextMenuListener {  

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(0, 1, 0, "Duplicate line");
		menu.add(0, 2, 0, "Delete line");
    }
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == 1) {
			EditorActivity.codeEditor.duplicateLine();
		} if(item.getItemId() == 2) {
			EditorActivity.codeEditor.deleteLine();
		}
		return true;
	}
}
