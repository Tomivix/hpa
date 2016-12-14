package view.code.documents;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import core.Parser;
import view.code.NumHeader;

public class OrderStyledDocument extends ColorStyledDocument {
	private static final long serialVersionUID = 1L;

	private AttributeSet defStyle, labelStyle, orderStyle, paramStyle, invStyle;
	private AttributeSet defBg, highBg;
	private Color defC = Color.BLACK,
			labelC = new Color(131, 110, 28),
			orderC = new Color(0, 18, 68),
			paramC = new Color(107, 79, 240),
			invC = new Color(216, 29, 29);
	private Color defBgC = Color.WHITE,
			highBgC = new Color(199, 199, 199);
	public OrderStyledDocument(NumHeader numHeader) {
		super(numHeader);
		StyleContext cont = StyleContext.getDefaultStyleContext();
		defStyle = cont.addAttribute(cont.addAttribute(cont.addAttribute(cont.getEmptySet(), StyleConstants.FontFamily, "Monospaced"), StyleConstants.FontSize, 18), StyleConstants.Foreground, defC);
		labelStyle = cont.addAttribute(cont.addAttribute(defStyle, StyleConstants.Italic, true), StyleConstants.Foreground, labelC);
		orderStyle = cont.addAttribute(defStyle, StyleConstants.Foreground, orderC);
		paramStyle = cont.addAttribute(defStyle, StyleConstants.Foreground, paramC);
		invStyle = cont.addAttribute(cont.addAttribute(defStyle, StyleConstants.Foreground, invC), StyleConstants.Underline, true);
		defBg = cont.addAttribute(cont.getEmptySet(), StyleConstants.Background, defBgC);
		highBg = cont.addAttribute(cont.getEmptySet(), StyleConstants.Background, highBgC);
		dFont = cont.getFont(defStyle);
		numHeader.updateHeader(dFont);
	}

	public void recalculateStyles(){
		try{
			super.setCharacterAttributes(0, super.getLength(), defStyle, true);

			int[][] indexes = Parser.split(super.getText(0, super.getLength()), false);

			for(int i = 0; i < indexes[0].length; i++){
				super.setCharacterAttributes(indexes[0][i], indexes[0][++i], labelStyle, true);
			}
			for(int i = 0; i < indexes[1].length; i++){
				super.setCharacterAttributes(indexes[1][i], indexes[1][++i], orderStyle, true);
			}
			for(int i = 0; i < indexes[2].length; i++){
				super.setCharacterAttributes(indexes[2][i], indexes[2][++i], paramStyle, true);
			}
			for(int i = 0; i < indexes[3].length; i++){
				super.setCharacterAttributes(indexes[3][i], indexes[3][++i], invStyle, true);
			}

		}catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void highlightLine(int index){
		try {
			int startI = 0, endI = 0;
			char[] chars = (super.getText(0, super.getLength()) + "\n").toCharArray();
			for(int i = 0, j = 0; j < chars.length && i < index; j++){
				if(chars[j] == '\n'){
					i++;
					if(i == index){
						endI = j;
					}else{
						startI = j+1;
					}
				}
			}
			super.setCharacterAttributes(0, super.getLength(), defBg, false);
			super.setCharacterAttributes(startI, endI-startI, highBg, false);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
