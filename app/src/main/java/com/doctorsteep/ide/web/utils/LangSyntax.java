package com.doctorsteep.ide.web.utils;

import com.doctorsteep.ide.web.EditorActivity;
import com.doctorsteep.ide.web.language.LanguageHTML;
import com.doctorsteep.ide.web.settings.EditCodeSettings;

public class LangSyntax {
	
	public static String HTML = "html";
	public static String JAVASCRIPT = "js";
	public static String CSS = "css";
	public static String PHP = "php";
	public static String NONE = "";
	
	public static String checkHTML_TAG(String text, boolean value) {
		String tagStart = "";
		if(value == true) {
			tagStart = "<";
		}
		
		try {
			if (LanguageHTML.htmlTag().contains(text)) {
				if(text.equals("area")) {
					return tagStart + text + " href=\"\" alt=\"\">";
				}
				if(text.equals("applet")) {
					return tagStart + text + " code=\"\">";
				}
				if(text.equals("base")) {
					return tagStart + text + " target=\"_blank\">";
				}
				if(text.equals("link")) {
					return tagStart + text + " rel=\"stylesheet\" href=\"\">";
				}
				if(text.equals("meta")) {
					return tagStart + text + " charset=\"utf-8\">";
				}
				if(text.equals("input")) {
					return tagStart + text + " type=\"text\" name=\"\">";
				}
				if(text.equals("img")) {
					return tagStart + text + " src=\"\">";
				}
				if(text.equals("etc") || text.equals("br") || text.equals("hr") || text.equals("isindex") || text.equals("frame") || text.equals("command")) {
					return tagStart + text + ">";
				}
				if(text.equals("colgroup") || text.equals("col") || text.equals("basefont")) {
					return tagStart + text + ">";
				}
				if(text.equals("audio")) {
					return tagStart + text + " src=\"\"></" + text + ">";
				}
				if(text.equals("script")) {
					return tagStart + text + " type=\"text/javascript\"></" + text + ">";
				}
				if(text.equals("a")) {
					return tagStart + text + " href=\"\"></" + text + ">";
				}
				if(text.equals("style")) {
					return tagStart + text + " type=\"text/css\"></" + text + ">";
				}
				if(text.equals("html") && !EditorActivity.codeEditor.getText().toString().contains("<html>")) {
					return tagStart + "!DOCTYPE html>\n<" + text + ">\n" + EditCodeSettings.TAB_SIZE + "<head>\n" + EditCodeSettings.TAB_SIZE + EditCodeSettings.TAB_SIZE + "<title></title>\n" + EditCodeSettings.TAB_SIZE + "</head>\n" + EditCodeSettings.TAB_SIZE + "<body>\n\n" + EditCodeSettings.TAB_SIZE + "</body>\n</" + text + ">";
				}
				if(text.equals("form")) {
					return tagStart + text + " action=\"\" method=\"post\"></" + text + ">";
				}
				if(text.equals("source")) {
					return tagStart + text + " src=\"\">";
				}
				if(text.equals("track")) {
					return tagStart + text + " kind=\"\" src=\"\" srclang=\"en\" label=\"\">";
				}
				if(text.equals("embed")) {
					return tagStart + text + " width=\"\" height=\"\"></" + text + ">";
				}
				if(text.equals("progress")) {
					return tagStart + text + " max=\"\" value=\"\"></" + text + ">";
				}
				if(text.equals("param")) {
					return tagStart + text + " name=\"\" value=\"\"></" + text + ">";
				}
				if(text.equals("optgroup")) {
					return tagStart + text + " label=\"\"></" + text + ">";
				}
				if(text.equals("object")) {
					return tagStart + text + " width=\"\" height=\"\"></" + text + ">";
				}
				if(text.equals("meter")) {
					return tagStart + text + " value=\"\"></" + text + ">";
				}
				if(text.equals("map")) {
					return tagStart + text + " name=\"\"></" + text + ">";
				}
				if(text.equals("details")) {
					return tagStart + text + " open=\"\"></" + text + ">";
				}
				if(text.equals("datalist") || text.equals("datagrid") || text.equals("canvas")) {
					return tagStart + text + " id=\"\"></" + text + ">";
				}
				if(!text.equals("")) {
					return tagStart + text + ">" + "</" + text + ">";
				}
			}
		} catch (Exception e) {}
		return text;
	}
}
