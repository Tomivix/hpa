package view;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import static javax.swing.SpringLayout.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

public class RMPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final byte UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

	private SpringLayout layout;
	private RegisterPanel registerPanel;
	private MemoryPanel memoryPanel;
	private Point p1, p2, p5, p6;
	public static int lastReg1 = -1, lastReg2 = -1, lastCell = -1;
	public static byte lastOrderMode = -1;
	public RMPanel(){
		super();
		super.setLayout(layout = new SpringLayout());
		
		registerPanel = new RegisterPanel();
		layout.putConstraint(WEST, registerPanel, 0, WEST, this);
		layout.putConstraint(EAST, registerPanel, View.REGISTER_WIDTH+1, WEST, registerPanel);
		layout.putConstraint(NORTH, registerPanel, 0, NORTH, this);
		super.add(registerPanel);
		
		memoryPanel = new MemoryPanel();
		layout.putConstraint(WEST, memoryPanel, View.REG_MEM_PAD, EAST, registerPanel);
		layout.putConstraint(EAST, memoryPanel, 0, EAST, this);
		layout.putConstraint(NORTH, memoryPanel, 0, NORTH, this);
		super.add(memoryPanel);
	}


	public void setRegisters(int[] registers) {
		registerPanel.setRegisters(registers);
		layout.putConstraint(SOUTH, registerPanel, registers.length*(View.REGISTER_HEIGHT+View.REGISTER_VERT_PADDING)+View.PANEL_DRAW_UP_PAD, NORTH, registerPanel);
		layout.putConstraint(SOUTH, memoryPanel, 0, SOUTH, registerPanel);
	}
	
	public void setMemoryCells(int[] cells){
		memoryPanel.setMemoryCells(cells);
	}
	
	@Override
	public void paint(Graphics g){ //TODO: Recalculate line with every repaint
		super.paint(g);
		
		int memCellOffset;
		switch(lastOrderMode){
		case View.RR:
			p1 = registerPanel.getRelativeRegisterLoc(lastReg1);
			p1.y += View.REGISTER_HEIGHT/2;
			p2 = new Point(p1.x, p1.y+View.REGISTER_VERT_PADDING/2);
			p6 = registerPanel.getRelativeRegisterLoc(lastReg2);
			p6.y -= View.REGISTER_HEIGHT/2;
			p5 = new Point(p6.x, p6.y-View.REGISTER_VERT_PADDING/2);
			break;
		case View.RM:
			p1 = registerPanel.getRelativeRegisterLoc(lastReg1);
			p1.y += View.REGISTER_HEIGHT/2;
			p2 = new Point(p1.x, p1.y+View.REGISTER_VERT_PADDING/2);
			p6 = memoryPanel.getRelativeCellLoc(lastCell);
			memCellOffset = layout.getConstraints(registerPanel).getWidth().getValue()+View.REG_MEM_PAD;
			p6.x += memCellOffset;
			p6.y -= View.MEM_CELL_HEIGHT/2;
			p5 = new Point(p6.x, p6.y-View.MEM_CELL_VERT_PADDING/2);
			break;
		case View.MR:
			p1 = memoryPanel.getRelativeCellLoc(lastCell);
			memCellOffset = layout.getConstraints(registerPanel).getWidth().getValue()+View.REG_MEM_PAD;
			p1.x += memCellOffset;
			p1.y += View.MEM_CELL_HEIGHT/2;
			p2 = new Point(p1.x, p1.y+View.MEM_CELL_VERT_PADDING/2);
			p6 = registerPanel.getRelativeRegisterLoc(lastReg1);
			p6.y -= View.REGISTER_HEIGHT/2;
			p5 = new Point(p6.x, p6.y-View.REGISTER_VERT_PADDING/2);
			break;
		case -1:
			break;
		}
		
		//g.setColor(Color.LIGHT_GRAY);
		View.MEM_CELL_COL_COUNT = (int) (Math.floor(layout.getConstraints(memoryPanel).getWidth().getValue()/View.REGISTER_WIDTH));
		
		if (p1 != null && p6 != null) {
			Point p3 = new Point(layout.getConstraints(registerPanel).getWidth().getValue()+View.REG_MEM_PAD/2, p2.y);
			Point p4 = new Point(p3.x, p5.y);
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(2));
			
			g2.draw(new Line2D.Float(p1, p2));
			g2.draw(new Line2D.Float(p2, p3));
			g2.draw(new Line2D.Float(p3, p4));
			g2.draw(new Line2D.Float(p4, p5));
			drawArrow(p6, g);
		}
		
	}
	
	public void drawArrow(Point p, Graphics g){
		int[] xP = new int[3];
		int[] yP = new int[3];
		xP[0] = p.x;
		yP[0] = p.y;
		xP[1] = p.x-View.ARROW_WIDTH;
		yP[1] = p.y-View.ARROW_LENGTH;
		xP[2] = p.x+View.ARROW_WIDTH;
		yP[2] = p.y-View.ARROW_LENGTH;
//		g.setColor(Color.red);
		g.fillPolygon(xP, yP, 3);
		g.setColor(Color.black);
	}
	
	public void updateValues(int source, int dest, byte mode){
		lastOrderMode = mode;
		switch(mode){
		case View.RR:
			lastReg1 = source;
			lastReg2 = dest;
			lastCell = -1;
			break;
		case View.RM:
			lastReg1 = source;
			lastReg2 = -1;
			lastCell = dest;
			break;
		case View.MR:
			lastReg1 = dest;
			lastReg2 = -1;
			lastCell = source;
			break;
		default:
			lastOrderMode = -1;
		}
		
	}
}
