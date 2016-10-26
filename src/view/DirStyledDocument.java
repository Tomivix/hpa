package view;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import placeholder.Placeholder;

public class DirStyledDocument extends DefaultStyledDocument {
	private static final long serialVersionUID = 1L;
	
	private AttributeSet defStyle, labelStyle, dirStyle;
	public DirStyledDocument() {
		super();
		StyleContext cont = StyleContext.getDefaultStyleContext();
		defStyle = cont.addAttribute(cont.addAttribute(cont.getEmptySet(), StyleConstants.FontFamily, "Times New Roman"), StyleConstants.FontSize, 18);
		labelStyle = cont.addAttribute(cont.addAttribute(defStyle, StyleConstants.Bold, true), StyleConstants.Foreground, Color.RED);
		dirStyle = cont.addAttribute(cont.addAttribute(defStyle, StyleConstants.Bold, true), StyleConstants.Foreground, Color.BLUE);
	}
	
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
	
	public void recalculateStyles(){
		try{
			super.setCharacterAttributes(0, super.getLength(), defStyle, true);
			int[][] indexes = Placeholder.getIndexes(super.getText(0, super.getLength()));
			for(int i = 0; i < indexes[0].length; i++){
				super.setCharacterAttributes(indexes[0][i], indexes[0][++i], labelStyle, true);
			}
			for(int i = 0; i < indexes[1].length; i++){
				super.setCharacterAttributes(indexes[1][i], indexes[1][++i], dirStyle, true);
			}
		}catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
