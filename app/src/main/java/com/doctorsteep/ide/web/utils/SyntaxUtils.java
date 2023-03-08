package com.doctorsteep.ide.web.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SyntaxUtils {

	public static String TAB = "TAB";
	public static String COMMENT = "COMMENT";
	public static String COMMENT_PHP = "PHP COMM";
	
	//SYMBOL'S
	public static ArrayList<String> HTML_symbol() {
        ArrayList<String> symbolList = new ArrayList<String>();  
		symbolList.add(TAB);
		symbolList.add("<");
		symbolList.add(">");
		symbolList.add("/");
		symbolList.add("\"");
		symbolList.add("=");
		symbolList.add("(");
		symbolList.add(")");
		symbolList.add("{");
		symbolList.add("}");
		symbolList.add(":");
		symbolList.add(";");
		symbolList.add(".");
		symbolList.add(",");
		symbolList.add(COMMENT);
		symbolList.add(COMMENT_PHP);
    	return symbolList;
    }
	public static ArrayList<String> PHP_symbol() {
		ArrayList<String> symbolList = new ArrayList<String>();
		symbolList.add(TAB);
		symbolList.add("$");
		symbolList.add("<");
		symbolList.add(">");
		symbolList.add("/");
		symbolList.add("\"");
		symbolList.add("=");
		symbolList.add("(");
		symbolList.add(")");
		symbolList.add("{");
		symbolList.add("}");
		symbolList.add(":");
		symbolList.add(";");
		symbolList.add(".");
		symbolList.add(",");
		symbolList.add(COMMENT);
		return symbolList;
	}
	public static ArrayList<String> CSS_symbol() {
        ArrayList<String> symbolList = new ArrayList<String>();  
		symbolList.add(TAB);
		symbolList.add("{");
		symbolList.add("}");
		symbolList.add(":");
		symbolList.add(";");
		symbolList.add("#");
		symbolList.add(".");
		symbolList.add("(");
		symbolList.add(")");
		symbolList.add("<");
		symbolList.add(">");
		symbolList.add("\"");
		symbolList.add(",");
		symbolList.add("=");
		symbolList.add("[");
		symbolList.add("]");
		symbolList.add(COMMENT);
    	return symbolList;
    }
	public static ArrayList<String> JS_symbol() {
        ArrayList<String> symbolList = new ArrayList<String>();  
		symbolList.add(TAB);
		symbolList.add("{");
		symbolList.add("}");
		symbolList.add("(");
		symbolList.add(")");
		symbolList.add(";");
		symbolList.add(".");
		symbolList.add(",");
		symbolList.add("\"");
		symbolList.add("'");
		symbolList.add("=");
		symbolList.add(":");
		symbolList.add("+");
		symbolList.add("-");
		symbolList.add("[");
		symbolList.add("]");
		symbolList.add("<");
		symbolList.add(">");
		symbolList.add(COMMENT);
    	return symbolList;
    }
	public static ArrayList<String> ALL_symbol() {
        ArrayList<String> symbolList = new ArrayList<String>();
		symbolList.add("{");
		symbolList.add("}");
		symbolList.add("(");
		symbolList.add(")");
		symbolList.add(";");
		symbolList.add(".");
		symbolList.add(",");
		symbolList.add("\"");
		symbolList.add("'");
		symbolList.add(":");
		symbolList.add("+");
		symbolList.add("-");
		symbolList.add("=");
		symbolList.add("[");
		symbolList.add("]");
		symbolList.add("<");
		symbolList.add(">");
    	return symbolList;
    }
	
	//HTML
	public static Pattern HTML_START_ELEMENT = Pattern.compile("(?<=<)[^/\\s>?]+(?=(?:>|\\s))");
    public static Pattern HTML_END_ELEMENT = Pattern.compile("(?<=</).*?(?=>)");
    public static Pattern HTML_COMMENTS = Pattern.compile("(?:<!--)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?-->");
	public static Pattern HTML_COMMENTS_TWO = Pattern.compile("(?:<!)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?>");
	public static Pattern HTML_COMMENTS_XML = Pattern.compile("(?:<\\?)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?\\?>");
	public static Pattern HTML_START_TAG = Pattern.compile("(?<=\\s)[^:\\s=]*?(?=:)");
    public static Pattern HTML_END_TAG = Pattern.compile("\\b.\\w*?(?:=)");
	public static Pattern HTML_STRING_PATTERN = Pattern.compile("\".*?\"");
	
	//CSS
	public static Pattern CSS_START_TAG = Pattern.compile("(?<=\\s)[^:\\s=]*?(?=:)");
    public static Pattern CSS_END_TAG = Pattern.compile("\\b.\\w*?(?:=)");
	public static Pattern CSS_COMMENTS_MULTI_LINE = Pattern.compile("(?:/\\*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?\\*/");
	public static Pattern CSS_KEYWORDS_PATTERN = Pattern.compile("\\b(px|vh|flex|center|block|inline-block|bold|normal|default|both|none|absolute|fixed|relative|static|border-box|transform)\\b");
    public static Pattern CSS_STRING_PATTERN = Pattern.compile("\".*?\"");
	
	//JS
	public static Pattern JS_KEYWORDS_PATTERN = Pattern.compile("\\b(int|on|var|in|const|let|console|.log|.debug|.info|.warn|.error|.assert|.dirxml|.dir|.group|.groupEnd|.time|.timeEnd|.count|.trace|.profile|.profileEnd|.clear|.open|.close|float)\\b");
	public static Pattern JS_KEYWORDS_PATTERN_TWO = Pattern.compile("\\b(try|catch|if|this|prompt|alert|navigator|function|return|boolean|new|for|window|else|document|switch|case|void|super|break|location|import|package|protected|public|private|static|class|this|extends|throw)\\b");
	public static Pattern JS_KEYWORDS_PATTERN_THREE = Pattern.compile("\\b(@Override|@NanNull)\\b");
	public static Pattern JS_KEYWORDS_PATTERN_FOUR = Pattern.compile("\\b(.innerHTML|.outerHTML|.textContent|.add|.indexOf|.push|.sort|.substr|.replace|.exec|.style|.userAgent|.value|.offsetWidth|.offsetHeight|.focus|.getElementById|.getElementsByTagName|.getElementsByClassName|.onclick|.preventDefault|.stopPropagation|.remove|.classList|.removeAttribute|.createElement|.setAttribute|.length|.size|.attachEvent|.addEventListener|.detachEvent|.removeEventListener|.keyCode|.metaKey|.ctrlKey|.shiftKey|.body|.appendChild|.src|.lastIndexOf|.ownerDocument|.scrollTop|.scrollLeft|.scrollRight|.scrollBottom|.className|.id|.all|.srcElement|.target|.documentElement|.pop|.join|.appender)\\b");
	public static Pattern JS_BUILTIN_PATTERN = Pattern.compile("\\b(true|false|null)\\b");
	public static Pattern JS_SYMBOL_PATTERN = Pattern.compile("\\b()\\b");
	public static Pattern JS_SYMBOL_PATTERN_TWO = Pattern.compile("\\b(\\.)\\b");
	public static Pattern JS_SYMBOL_PATTERN_THREE = Pattern.compile("\\b(\\||)\\b");
	public static Pattern JS_COMMENTS_CONSOLE = Pattern.compile("\\b(log:|debug:|info:|warn:|error:|assert:|dir:|dirxml:|group:|groupEnd:|time:|timeEnd:|count:|trace:|profile:|profileEnd:|clear:|open:|close:)\\b");
	public static Pattern JS_COMMENTS_SINGLE_LINE = Pattern.compile("(//)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$).*");
    public static Pattern JS_COMMENTS_MULTI_LINE = Pattern.compile("(?:/\\*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?:.|\\n)*?\\*/");
    public static Pattern JS_STRING_PATTERN = Pattern.compile("\".*?\"");
	public static Pattern JS_STRING_PATTERN_TWO = Pattern.compile("\'.*?\'");

	//ALL
	public static Pattern COLOR_HEX = Pattern.compile("#([0-9a-fA-F]{8}|[0-9a-fA-F]{6}|[0-9a-fA-F]{3})");
}
