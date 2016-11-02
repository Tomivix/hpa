package view.code.documents;

import java.awt.Color;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import core.Engine;
import core.TextObject;
import view.code.NumHeader;

public class OrderStyledDocument extends ColorStyledDocument {
	private static final long serialVersionUID = 1L;
	
	private AttributeSet defStyle, labelStyle, orderStyle, paramStyle, invStyle;
	public OrderStyledDocument(NumHeader numHeader) {
		super(numHeader);
		StyleContext cont = StyleContext.getDefaultStyleContext();
		defStyle = cont.addAttribute(cont.addAttribute(cont.getEmptySet(), StyleConstants.FontFamily, "Monospaced"), StyleConstants.FontSize, 18);
		labelStyle = cont.addAttribute(cont.addAttribute(defStyle, StyleConstants.Italic, true), StyleConstants.Foreground, Color.ORANGE);
		orderStyle = cont.addAttribute(cont.addAttribute(defStyle, StyleConstants.Bold, true), StyleConstants.Foreground, Color.CYAN);
		paramStyle = cont.addAttribute(defStyle, StyleConstants.Bold, true);
		invStyle = cont.addAttribute(defStyle, StyleConstants.Foreground, Color.red);
		dFont = cont.getFont(defStyle);
		numHeader.updateHeader(dFont);
	}
	
	public void recalculateStyles(){
		try{
			super.setCharacterAttributes(0, super.getLength(), defStyle, true);
			List<TextObject> words = new Engine().split(super.getText(0, super.getLength()), true);
			for(TextObject word : words){
				super.setCharacterAttributes(word.getStart(), word.getLength(), getStyle(word.getType()), true);
			}
		}catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private AttributeSet getStyle(int id){
		switch(id){
			case 0: return labelStyle;
			case 1: return orderStyle;
			case 2: return paramStyle;
			case 3: return invStyle;
			default: return defStyle;
		}
	}
}
