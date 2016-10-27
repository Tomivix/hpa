package view.code.documents;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public abstract class ColorStyledDocument extends DefaultStyledDocument {
	private static final long serialVersionUID = 1L;

	@Override
	public void insertString(int offset, String str, AttributeSet a){
		try {
			super.insertString(offset, str, a);
			recalculateStyles();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void remove(int offset, int length){
		try {
			super.remove(offset, length);
			recalculateStyles();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void recalculateStyles();
}
