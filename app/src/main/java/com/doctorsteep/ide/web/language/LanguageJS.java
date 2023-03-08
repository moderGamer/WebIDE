package com.doctorsteep.ide.web.language;

import java.util.ArrayList;

public class LanguageJS {
	
	public static ArrayList<String> js() throws Exception {
        return jsMethod();
	}

    public static ArrayList<String> jsMethod() throws Exception {
        ArrayList<String> list = new ArrayList<String>();

        list.add("document");
        list.add("window");
        list.add("alert");

        return list;
    }
}
