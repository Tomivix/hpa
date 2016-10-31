package view.code.documents;

import java.awt.Font;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import view.code.NumHeader;

public abstract class ColorStyledDocument extends DefaultStyledDocument {
	private static final long serialVersionUID = 1L;

	private NumHeader numHeader;
	protected Font dFont;
	public ColorStyledDocument(NumHeader numHeader) {
		this.numHeader = numHeader;
	}

	@Override
	public void insertString(int offset, String str, AttributeSet a){
		try {
			super.insertString(offset, str.toUpperCase(), a);
			recalculateStyles();
			numHeader.updateHeader(dFont);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void remove(int offset, int length){
		try {
			super.remove(offset, length);
			recalculateStyles();
			numHeader.updateHeader(dFont);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void recalculateStyles();
}
