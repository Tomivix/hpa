package view;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

public class RegisterPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private int[] registers;
	public void setRegisters(int[] registers) {
		this.registers = registers;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		
		FontMetrics fm = g.getFontMetrics();
		int strH = fm.getHeight();
		for(int i = 0; i < registers.length; i++){
			g.drawRect(0, i*(View.REGISTER_HEIGHT+View.REGISTER_VERT_PADDING)+View.PANEL_DRAW_UP_PAD, View.REGISTER_WIDTH, View.REGISTER_HEIGHT);
			if(i == RMPanel.lastReg1 || i == RMPanel.lastReg2){
				g.setFont(g.getFont().deriveFont(Font.BOLD));
				fm = g.getFontMetrics();
			}
			int strW = fm.stringWidth(""+registers[i]);
			g.drawString(""+registers[i], View.REGISTER_WIDTH/2-strW/2, i*(View.REGISTER_HEIGHT+View.REGISTER_VERT_PADDING)+(View.REGISTER_HEIGHT/2)+View.PANEL_DRAW_UP_PAD+(strH/2)+View.FONT_VERT_OFF);
			if(i == RMPanel.lastReg1 || i == RMPanel.lastReg2){
				g.setFont(g.getFont().deriveFont(Font.PLAIN));
				fm = g.getFontMetrics();
			}
		}
	}
	
	public Point getRelativeRegisterLoc(int regIndex){
		return new Point(View.REGISTER_WIDTH/2, View.REGISTER_HEIGHT/2 + regIndex*(View.REGISTER_HEIGHT+View.REGISTER_VERT_PADDING)+View.PANEL_DRAW_UP_PAD);
	}

}
