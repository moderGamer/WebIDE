package com.doctorsteep.ide.web.comparator;

import java.io.File;
import java.util.Comparator;

public class SortFolder implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
        if (f1.isDirectory() == f2.isDirectory())
            return 0;
        else if (f1.isDirectory() && !f2.isDirectory())
            return -1;
        else
            return 1;
    }
}
