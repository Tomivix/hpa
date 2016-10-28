package view.reg_mem;

import java.awt.Color;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import static javax.swing.SpringLayout.*;

public abstract class EditableCell extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected int topX, topY;
	private SpringLayout layout;
	private JLabel label, value;
	public EditableCell(int topX, int topY){
		super();
		this.topX = topX;
		this.topY = topY;
		super.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		layout = new SpringLayout();
		super.setLayout(layout);
	}
	
	protected void setupLabels(){
		label = new JLabel(getLabel());
		label.setForeground(Color.lightGray);
		super.add(label);
		FontMetrics fm = label.getFontMetrics(label.getFont());
		layout.putConstraint(WEST, label, 0, WEST, this);
		layout.putConstraint(NORTH, label, 0, NORTH, this);
		layout.putConstraint(EAST, label, fm.stringWidth(label.getText()), WEST, label);
		layout.putConstraint(SOUTH, label, fm.getHeight(), NORTH, label);
		
		value = new JLabel();
		super.add(value);
		layout.putConstraint(HORIZONTAL_CENTER, value, 0, HORIZONTAL_CENTER, this);
		layout.putConstraint(VERTICAL_CENTER, value, 0, VERTICAL_CENTER, this);
		updateValue();
	}
	
	public void updateValue(){
		value.setText(""+getValue());
	}
	
	protected abstract int getValue();
	protected abstract String getLabel();
	
	public int getTopX(){
		return topX;
	}
	
	public int getTopY(){
		return topY;
	}
	//TODO: Value edition
}
