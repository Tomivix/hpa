package view.code;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NumHeader extends JPanel {
	private static final long serialVersionUID = 1L;
	
	ArrayList<JLabel> labels; //TODO: Instead of removing unused labels consider storing them in Map<JLabel, Boolean> and "turning them off"
	public NumHeader(){
		super();
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		labels = new ArrayList<JLabel>();
	}
	
	public void updateHeader(String s, Font f){
		int rowCount = rowCount(s);
		FontMetrics fm = super.getFontMetrics(f);
		while(labels.size() < rowCount){
			int i = labels.size() + 1;
			JLabel label = new JLabel(i+":");
			label.setFont(f);
//			label.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
			labels.add(label);
			super.add(label);
		}
		
		while(labels.size() > rowCount){
			super.remove(labels.get(labels.size()-1));
			labels.remove(labels.size()-1);
		}
		repaint();
	}
	
	private int rowCount(String s){
		int count = 1;
		for(char c : s.toCharArray()){
			if(c == '\n')
				count++;
		}
		return count;
	}
}
