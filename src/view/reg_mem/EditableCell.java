package view.reg_mem;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import static javax.swing.SpringLayout.*;

public abstract class EditableCell extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;
	
	protected int index;
	protected boolean isRegister = false;
	protected int topX, topY;
	private SpringLayout layout;
	private JLabel label, value;
	protected Color bgDefColor = Color.white, bgHoverColor = Color.lightGray, labelDefColor = Color.lightGray, labelHoverColor = Color.black;
	protected Color bgCurrColor = bgDefColor, labelCurrColor = labelDefColor;
	public EditableCell(int topX, int topY){
		super();
		this.topX = topX;
		this.topY = topY;
		super.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		layout = new SpringLayout();
		super.setLayout(layout);
		super.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		super.setToolTipText("Click to edit...");
		super.addMouseListener(this);
	}
	
	protected void setupLabels(){
		label = new JLabel(getLabel());
		label.setForeground(labelDefColor);
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
		if(isLastEdited()){
			value.setFont(value.getFont().deriveFont(Font.BOLD, 18));
		}else{
			value.setFont(value.getFont().deriveFont(Font.PLAIN, 12));
		}
	}
	
	protected abstract int getValue();
	protected abstract String getLabel();
	protected abstract boolean isLastEdited();
	
	public int getTopX(){
		return topX;
	}
	
	public int getTopY(){
		return topY;
	}
	
	@Override
	public void paint(Graphics g){
		label.setForeground(labelCurrColor);
		this.setBackground(bgCurrColor);
		super.paint(g);
	}

	@Override
	public void mouseEntered(MouseEvent e){
		bgCurrColor = bgHoverColor;
		labelCurrColor = labelHoverColor;
		repaint();
	}
	
	@Override
	public void mouseExited(MouseEvent e){
		bgCurrColor = bgDefColor;
		labelCurrColor = labelDefColor;
		repaint();
	}
	
	
	@Override
	public void mouseReleased(MouseEvent e){
		
	}
	
	@Override
	public void mousePressed(MouseEvent e){
		
	}
}
