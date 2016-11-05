package view.reg_mem;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class RestrictedInputDocument extends DefaultStyledDocument {
	private static final long serialVersionUID = 1L;
	
	private char[] acceptableChars;
	public RestrictedInputDocument(char[] acceptableChars) {
		this.acceptableChars = acceptableChars;
	}
	
	@Override
	public void insertString(int offset, String str, AttributeSet a){
		String out = "";
		for(char c : str.toCharArray()){
			if(isAcceptable(c)){
				out += c;
			}
		}
		try {
			super.insertString(offset, out, a);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isAcceptable(char ch){
		for(char c : acceptableChars){
			if(Character.toUpperCase(c) == Character.toUpperCase(ch)){
				return true;
			}
		}
		return false;
	}
	
}
