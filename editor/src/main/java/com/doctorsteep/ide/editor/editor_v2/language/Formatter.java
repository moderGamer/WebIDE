package com.doctorsteep.ide.editor.editor_v2.language;

public interface Formatter
{
		public int createAutoIndent(CharSequence text);
		
		public CharSequence format(CharSequence text, int width);

}
