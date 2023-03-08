package com.doctorsteep.ide.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doctorsteep.ide.web.R;
import com.doctorsteep.ide.web.adapter.HistoryFilesAdapter;
import com.doctorsteep.ide.web.adapter.SymbolAdapter;
import com.doctorsteep.ide.web.adapter.SyntaxCSSAdapter;
import com.doctorsteep.ide.web.adapter.SyntaxHTMLAdapter;
import com.doctorsteep.ide.web.adapter.SyntaxJSAdapter;
import com.doctorsteep.ide.web.data.Data;
import com.doctorsteep.ide.web.data.EditorCodeData;
import com.doctorsteep.ide.web.data.FileManagerProject;
import com.doctorsteep.ide.web.data.ManagerData;
import com.doctorsteep.ide.web.data.TagManager;
import com.doctorsteep.ide.web.language.LanguageCSS;
import com.doctorsteep.ide.web.language.LanguageHTML;
import com.doctorsteep.ide.web.language.LanguageJS;
import com.doctorsteep.ide.web.language.LanguagePHP;
import com.doctorsteep.ide.web.tokenizer.TokenizerCSS;
import com.doctorsteep.ide.web.tokenizer.TokenizerHTML;
import com.doctorsteep.ide.web.tokenizer.TokenizerJS;
import com.doctorsteep.ide.web.tokenizer.TokenizerPHP;
import com.doctorsteep.ide.web.utils.FileUtils;
import com.doctorsteep.ide.web.utils.LangSyntax;
import com.doctorsteep.ide.web.utils.SyntaxUtils;
import com.doctorsteep.ide.web.utils.UndoRedoUtils;
import com.doctorsteep.ide.web.widget.CodeEditor;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class EditorActivity extends AppCompatActivity {

	private Toolbar toolbar;
	private static RecyclerView recyclerViewSymbol;
	private static RecyclerView.Adapter adapterSymbol;
	private static RecyclerView.LayoutManager layoutManagerSymbol;
	private static RecyclerView recyclerViewHistoryFiles;
	private static RecyclerView.Adapter adapterHistoryFiles;
	private static RecyclerView.LayoutManager layoutManagerHistoryFiles;
	public static CodeEditor codeEditor;
	public static String typeProject = "project";
	public static String patchProject = "";
	public static String patchProjectUpdate = "";
	public static String patchFile = "";
	public static String patchFileFixed = "";
	private static DrawerLayout drawer;
	private static Handler handlerCode;
	private static Handler handlerAds;
	private static UndoRedoUtils codeEditorSettings;
	public static Context eContext;
	public static ArrayList<String> filesHistory = new ArrayList<String>();

	public static boolean OPENING = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Data.setTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		eContext = EditorActivity.this;
		drawer = findViewById(R.id.drawer);
		codeEditor = findViewById(R.id.codeEditor);
		recyclerViewHistoryFiles = (RecyclerView) drawer.findViewById(R.id.recycler_viewHistoryFiles);
		recyclerViewSymbol = (RecyclerView) findViewById(R.id.recycler_viewSymbol);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		if (new File(getIntent().getStringExtra("path")).getParentFile() != null) {
			patchProject = new File(getIntent().getStringExtra("path")).getParentFile().toString();
			patchProjectUpdate = new File(getIntent().getStringExtra("path")).getParentFile().toString();
		}
		patchFile = new File(getIntent().getStringExtra("path")).getAbsolutePath();
		patchFileFixed = new File(getIntent().getStringExtra("path")).getAbsolutePath();
		setTitle(new File(patchProject).getName());
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
			
		codeEditor.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
					// TODO: Implement this method
				}
				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
					int lines = codeEditor.getLineCount();
					String linesText = "";
					for(int i = 1; i <= lines; i++) {
						linesText = linesText + i + "\n";
					}
				}
				@Override
				public void afterTextChanged(Editable p1) {
					// TODO: Implement this method
				}
			});
			
		handlerCode = new Handler();
		handlerAds = new Handler();
		codeEditorSettings = new UndoRedoUtils(codeEditor);

		setUpdateSymbol(SyntaxUtils.HTML_symbol(), this);
		FileManagerProject.openFile(this, new File(patchFile).getName(), new File(patchFile).getAbsolutePath());
		setAddHistoryFiles(patchFile, this);
		
		//Реклама
		/*handlerAds.postDelayed(new Runnable() {
				@Override public void run() {
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mInterAd = new InterstitialAd(EditorActivity.this); 
								mInterAd.setAdUnitId(appPubAdMob); 
								final AdRequest adReg = new AdRequest.Builder().addTestDevice("126CCC9311321EE784C3F8E5F70ECC00").build(); 
								mInterAd.loadAd(adReg); 
								mInterAd.setAdListener(new AdListener(){ 
										public void onAdClosed(){ 
											mInterAd.loadAd(adReg);
										} 
									});
							}
						});
				}
			}, 500);*/
		
		/*codeEditor.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					codeEditor.showDropDown();
					codeEditor.requestFocus();
					return false;
				}
			});*/
	}

	@Override
	protected void onStart() {
		super.onStart();
		OPENING = true;
	}

	@Override
	protected void onStop() {
		OPENING = false;
		super.onStop();
	}

	@Override
	protected void onDestroy(){
		OPENING = false;
		super.onDestroy();
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(!filesHistory.isEmpty()) {
						filesHistory.clear();
					}
				}
			});
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
		/*if(menu.findItem(R.id.action_add_tag) != null) {
			if(new File(patchFile).getName().endsWith(".html")) {
				menu.findItem(R.id.action_add_tag).setVisible(true);
			} else {
				menu.findItem(R.id.action_add_tag).setVisible(false);
			}
		}*/
        return true;
    }

	@SuppressLint("WrongConstant")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_undo) { //Отменить
			if(codeEditorSettings.getCanUndo()) {
				codeEditorSettings.undo();
			}
		} if(item.getItemId() == R.id.action_redo) { //Вернуть
			if(codeEditorSettings.getCanRedo()) {
				codeEditorSettings.redo();
			}
		} if(item.getItemId() == R.id.action_file_manager) { //Проводник
            FileManagerProject.fileManagerProject(this, patchProjectUpdate, 1);
        } if(item.getItemId() == R.id.action_save) { //Сохранить
			FileUtils.writeFile(this, codeEditor.getText().toString(), patchFile);
		} if(item.getItemId() == R.id.action_run_project) { //Запустить проект
			setOpenProject(patchFileFixed);
		} if(item.getItemId() == R.id.action_run_open_file) { //Запустить открытый файл
			if(patchFile.endsWith(".html") || patchFile.endsWith(".htm")) {
				setOpenProject(patchFile);
			} else {
				Toast.makeText(getApplicationContext(), "Not supported open file, please choose file .html or .htm", Toast.LENGTH_SHORT).show();
			}
		} if(item.getItemId() == R.id.action_history) { //Открыть историю
			drawer.openDrawer(Gravity.END);
		} if(item.getItemId() == R.id.action_replace) { //Заменить
			EditorCodeData.replaceText(this, codeEditor);
		} if(item.getItemId() == R.id.action_go_to_line) { //Перейти к строке
			EditorCodeData.goToLine(this, codeEditor);
		} if(item.getItemId() == R.id.action_tags) { //Теги
			TagManager.openTagList(this);
		}
        return true;
    }
	
	private void setOpenProject(String urlHtml) {
		startActivity(new Intent(this, RunActivity.class).putExtra("url", urlHtml).putExtra("host", "/WebMark/" + new File(patchProject).getName() + "/public_html"));
	}
	
	public static void setOpenFile(final Context context, final String patch) {
		codeEditorSettings.clearHistory();
		codeEditor.setText("");
		codeEditor.setHint("Please wait...");
		if(codeEditor.type.equals(LangSyntax.HTML)) {
			try{
				codeEditor.setTokenizer(new TokenizerHTML());
				codeEditor.setAdapter(new SyntaxHTMLAdapter(context, R.layout.row_syntax_editor, LanguageHTML.html()));
			} catch (Exception e) {}
		} if(codeEditor.type.equals(LangSyntax.CSS)) {
			try{
				codeEditor.setTokenizer(new TokenizerCSS());
				codeEditor.setAdapter(new SyntaxCSSAdapter(context, R.layout.row_syntax_editor, LanguageCSS.cssProperty()));
			} catch (Exception e) {}
		} if(codeEditor.type.equals(LangSyntax.JAVASCRIPT)) {
			try{
				codeEditor.setTokenizer(new TokenizerJS());
				codeEditor.setAdapter(new SyntaxJSAdapter(context, R.layout.row_syntax_editor, LanguageJS.js()));
			} catch (Exception e) {}
		} if(codeEditor.type.equals(LangSyntax.PHP)) {
			try{
				codeEditor.setTokenizer(new TokenizerPHP());
				codeEditor.setAdapter(new SyntaxHTMLAdapter(context, R.layout.row_syntax_editor, LanguagePHP.php()));
			} catch (Exception e) {}
		}
		handlerCode.postDelayed(new Runnable() {
				@Override public void run() {
					codeEditor.setText(FileUtils.readFile(context, patch).trim(), TextView.BufferType.EDITABLE);
					codeEditor.setHint("");
					patchFile = patch;
					codeEditorSettings.clearHistory();
					updateHistoryFiles(context);
				}
			}, 500);
	}
	
	public static void setUpdateSymbol(ArrayList<String> array, Context context) {
		try {
			layoutManagerSymbol = new LinearLayoutManager(((Activity)context), LinearLayoutManager.HORIZONTAL, false);
			recyclerViewSymbol.setLayoutManager(layoutManagerSymbol);
			adapterSymbol = new SymbolAdapter(array, (((Activity)context)));
			recyclerViewSymbol.setAdapter(adapterSymbol);
		} catch (Exception e){}
	}
	
	public static void setUpdateHistoryFiles(ArrayList<String> array, Context context) {
		try {
			layoutManagerHistoryFiles = new LinearLayoutManager(((Activity)context), LinearLayoutManager.VERTICAL, false);
			recyclerViewHistoryFiles.setLayoutManager(layoutManagerHistoryFiles);
			adapterHistoryFiles = new HistoryFilesAdapter(array, (((Activity)context)));
			recyclerViewHistoryFiles.setAdapter(adapterHistoryFiles);
		} catch (Exception e){}
	}
	
	public static void setAddHistoryFiles(final String path, final Context context) {
		((Activity)context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/*if (filesHistory.isEmpty() || !filesHistory.get(filesHistory.size() - 1).equals(path)) {
						
					}*/
					if(!filesHistory.contains(path)) {
						filesHistory.add(path);
						updateHistoryFiles(context);
					} else {
						updateHistoryFiles(context);
					}
				}
			});
	}
	
	public void setPositionCursor(int min_position) {
		codeEditor.setSelection(codeEditor.getSelectionEnd() - min_position);
	}
	
	public static void updateHistoryFiles(Context context) {
		setUpdateHistoryFiles(filesHistory, context);
	}
}
