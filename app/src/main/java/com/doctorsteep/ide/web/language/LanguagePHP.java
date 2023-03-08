package com.doctorsteep.ide.web.language;

import java.util.ArrayList;

public class LanguagePHP {

    public static ArrayList<String> php() throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(LanguageHTML.html());
        list.addAll(phpFunctions());
        return list;
    }

    public static ArrayList<String> phpFunctions() throws Exception {
        ArrayList<String> list = new ArrayList<String>();

        list.add("$_COOKIE");
        list.add("$_SESSION");
        list.add("$_POST");
        list.add("$_GET");

        return list;
    }
}
