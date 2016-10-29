package view.code;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

public class NumHeader extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<JLabel> labels; //TODO: Instead of removing unused labels consider storing them in Map<JLabel, Boolean> and "turning them off"
	private JTextPane textPane;
	private String paneString;
	public NumHeader(JTextPane textPane){
		super();
		this.textPane = textPane;
		labels = new ArrayList<JLabel>();
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	public void updateHeader(Font f){
		try {
			paneString = textPane.getDocument().getText(0, textPane.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		boolean[] newLineArray = newLineArray();
		while(labels.size() < newLineArray.length){
			JLabel label = new JLabel();
			label.setFont(f);
			labels.add(label);
			super.add(label);
		}
		
		while(labels.size() > newLineArray.length){
			super.remove(labels.get(labels.size()-1));
			labels.remove(labels.size()-1);
		}
		
		int lineIndex = 1;
		labels.get(0).setText((lineIndex++) + ":");
		for(int i = 1; i < newLineArray.length; i++){
			if(newLineArray[i-1]){
				labels.get(i).setText((lineIndex++) + ":");
			}else{
				labels.get(i).setText(" ");
			}
		}
		
		repaint();
	}
	
	private int rowCount(){
		int offs = paneString.length();
		int count = offs == 0 ? 1 : 0;
		
		try {
			while(offs >= 0){
				offs = Utilities.getRowStart(textPane, offs) - 1;
				count++;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	private boolean[] newLineArray(){
		if(paneString.length() == 0){
			return new boolean[]{true};
		}
		
		boolean[] out = new boolean[rowCount()];
		char[] chars = paneString.toCharArray();
		
		try {
			int outIndex = 0;
			for (int i = 0; i < chars.length - 1; i++) {
				if(Utilities.getRowEnd(textPane, i) == i){
					out[outIndex++] = chars[i] == '\n';
				}
			}
			out[out.length - 1] = true;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return out;
	}
}
