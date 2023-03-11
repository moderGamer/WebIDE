package com.doctorsteep.ide.web.comparator;

import java.io.File;
import java.util.Comparator;

public class SortFileName implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
        return f1.getName().compareTo(f2.getName());
    }
}
