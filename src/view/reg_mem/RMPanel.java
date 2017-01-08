package view.reg_mem;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import core.Engine;
import view.View;

import static javax.swing.SpringLayout.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;

public class RMPanel extends JPanel implements ComponentListener, AdjustmentListener, MouseListener{
	private static final long serialVersionUID = 1L;

	private SpringLayout layout;
	private RegisterPanel registerPanel;
	private MemoryPanel memoryPanel;
	private JScrollPane regScrollPane, memScrollPane;
	private Point p1, p2, p5, p6;
	private final Color aritmColor = Color.blue,
			storeColor = Color.green,
			loadColor = Color.red,
			loadAddressColor = Color.DARK_GRAY;
	private Color currColor;

	public static int lastReg1 = -1, lastReg2 = -1, lastCell = -1;
	public static byte lastOrderMode = -1, lastOperationType = -1;
	public RMPanel(){
		super();
		super.setLayout(layout = new SpringLayout());
		super.addComponentListener(this);

		registerPanel = new RegisterPanel();
		regScrollPane = new JScrollPane(registerPanel);
		regScrollPane.getVerticalScrollBar().addAdjustmentListener(this);
		regScrollPane.getVerticalScrollBar().addMouseListener(this);
		regScrollPane.getVerticalScrollBar().setUnitIncrement(View.REGISTER_HEIGHT);
		layout.putConstraint(WEST, regScrollPane, 0, WEST, this);
		layout.putConstraint(EAST, regScrollPane, (int) (View.REGISTER_WIDTH+regScrollPane.getVerticalScrollBar().getMinimumSize().getWidth())+5, WEST, regScrollPane);
		layout.putConstraint(NORTH, regScrollPane, 0, NORTH, this);
		layout.putConstraint(SOUTH, regScrollPane, 0, SOUTH, this);
		super.add(regScrollPane);

		memoryPanel = new MemoryPanel();
		memScrollPane = new JScrollPane(memoryPanel);
		memScrollPane.getVerticalScrollBar().addAdjustmentListener(this);
		memScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		memScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		memScrollPane.getVerticalScrollBar().setUnitIncrement(View.MEM_CELL_HEIGHT);
		layout.putConstraint(WEST, memScrollPane, View.REG_MEM_PAD, EAST, regScrollPane);
		layout.putConstraint(EAST, memScrollPane, 0, EAST, this);
		layout.putConstraint(NORTH, memScrollPane, 0, NORTH, this);
		layout.putConstraint(SOUTH, memScrollPane, 0, SOUTH, this);
		super.add(memScrollPane);
	}


	public void setRegisters() {
		registerPanel.setRegisters();
		layout.putConstraint(SOUTH, registerPanel, Engine.current.getRegCount()*(View.REGISTER_HEIGHT+View.REGISTER_VERT_PADDING)+View.PANEL_DRAW_UP_PAD+View.LABEL_HEIGHT, NORTH, registerPanel);
		layout.putConstraint(SOUTH, memoryPanel, 0, SOUTH, registerPanel);
	}

	public void setMemoryCells(){
		memoryPanel.setMemoryCells();
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);

		g.setColor(currColor);
		int memCellOffset;
		switch(lastOrderMode){
		case View.RR:
			p1 = registerPanel.getRelativeRegisterLoc(lastReg1);
			p1.y += View.REGISTER_HEIGHT/2;
			p1.y -= regScrollPane.getVerticalScrollBar().getValue();
			p2 = new Point(p1.x, p1.y+View.REGISTER_VERT_PADDING/2);
			p6 = registerPanel.getRelativeRegisterLoc(lastReg2);
			p6.y -= View.REGISTER_HEIGHT/2;
			p6.y -= regScrollPane.getVerticalScrollBar().getValue();
			p5 = new Point(p6.x, p6.y-View.REGISTER_VERT_PADDING/2);
			break;
		case View.RM:
			p1 = registerPanel.getRelativeRegisterLoc(lastReg1);
			p1.y += View.REGISTER_HEIGHT/2;
			p1.y -= regScrollPane.getVerticalScrollBar().getValue();
			p2 = new Point(p1.x, p1.y+View.REGISTER_VERT_PADDING/2);
			p6 = memoryPanel.getRelativeCellLoc(lastCell);
			memCellOffset = layout.getConstraints(regScrollPane).getWidth().getValue()+View.REG_MEM_PAD;
			p6.x += memCellOffset;
			p6.y -= View.MEM_CELL_HEIGHT/2;
			p6.y -= memScrollPane.getVerticalScrollBar().getValue();
			p5 = new Point(p6.x, p6.y-View.MEM_CELL_VERT_PADDING/2);
			break;
		case View.MR:
			p1 = memoryPanel.getRelativeCellLoc(lastCell);
			memCellOffset = layout.getConstraints(regScrollPane).getWidth().getValue()+View.REG_MEM_PAD;
			p1.x += memCellOffset;
			p1.y += View.MEM_CELL_HEIGHT/2;
			p1.y -= memScrollPane.getVerticalScrollBar().getValue();
			p2 = new Point(p1.x, p1.y+View.MEM_CELL_VERT_PADDING/2);
			p6 = registerPanel.getRelativeRegisterLoc(lastReg1);
			p6.y -= View.REGISTER_HEIGHT/2;
			p6.y -= regScrollPane.getVerticalScrollBar().getValue();
			p5 = new Point(p6.x, p6.y-View.REGISTER_VERT_PADDING/2);
			break;
		case -1:
			break;
		}

		if (p1 != null && p6 != null && lastOrderMode != -1) {
			Point p3;
			Point p4;
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(2));
			if (lastReg1 != lastReg2 - 1) {
				p3 = new Point(layout.getConstraints(regScrollPane).getWidth().getValue() + View.REG_MEM_PAD / 2, p2.y);
				p4 = new Point(p3.x, p5.y);

				g2.draw(new Line2D.Float(p1, p2));
				g2.draw(new Line2D.Float(p2, p3));
				g2.draw(new Line2D.Float(p3, p4));
				g2.draw(new Line2D.Float(p4, p5));
			}else{
				g2.draw(new Line2D.Float(p1, p6));
			}


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
		g.fillPolygon(xP, yP, 3);
	}

	public void updateValues(int source, int dest, byte mode, byte opType){
		lastOrderMode = mode;
		int lr1 = lastReg1, lr2 = lastReg2, lc = lastCell;
		switch(mode){
		case View.RR:
			lastReg1 = source;
			lastReg2 = dest;
			lastCell = -1;
			registerPanel.updateRegister(source);
			registerPanel.updateRegister(dest);
			break;
		case View.RM:
			lastReg1 = source;
			lastReg2 = -1;
			lastCell = dest;
			registerPanel.updateRegister(source);
			memoryPanel.updateCell(dest);
			break;
		case View.MR:
			lastReg1 = dest;
			lastReg2 = -1;
			lastCell = source;
			memoryPanel.updateCell(source);
			registerPanel.updateRegister(dest);
			break;
		default:
			if (lr1 != -1)
				registerPanel.updateRegister(lr1);
			if (lr2 != -1)
				registerPanel.updateRegister(lr2);
			if (lc != -1)
				memoryPanel.updateCell(lc);
			lastReg1 = -1;
			lastReg2 = -1;
			lastCell = -1;
			lastOrderMode = -1;
		}

		if (lr1 != -1)
			registerPanel.updateRegister(lr1);
		if (lr2 != -1)
			registerPanel.updateRegister(lr2);
		if (lc != -1)
			memoryPanel.updateCell(lc);

		switch(opType){
		case View.ARITM:
			currColor = aritmColor;
			break;
		case View.LOAD:
			currColor = loadColor;
			break;
		case View.LOAD_ADDR:
			currColor = loadAddressColor;
			break;
		case View.STORE:
			currColor = storeColor;
			break;
		}
		repaint();
	}

	public void updateRegister(int index){
		registerPanel.updateRegister(index);
	}

	public void updateMemCell(int index){
		memoryPanel.updateCell(index);
	}


	@Override
	public void componentHidden(ComponentEvent e) {
	}


	@Override
	public void componentMoved(ComponentEvent e) {
	}


	@Override
	public void componentResized(ComponentEvent e) {
		recalculateCells();
	}


	@Override
	public void componentShown(ComponentEvent e) {
		recalculateCells();
	}

	private void recalculateCells(){
		if(layout.getConstraints(memoryPanel).getWidth().getValue() <= 0){
			return;
		}
		View.MEM_CELL_COL_COUNT = (int) (Math.floor((layout.getConstraints(memScrollPane).getWidth().getValue()-memScrollPane.getVerticalScrollBar().getWidth())/View.MEM_CELL_WIDTH));
		memoryPanel.recalculateCellsPosition();
		repaint();
	}

	public void resetLastEdited(){
		int lr1 = lastReg1;
		int lr2 = lastReg2;
		lastOrderMode = -1;
		lastCell = -1;
		lastReg1 = -1;
		lastReg2 = -1;
		if (lr1 != -1)
			registerPanel.updateRegister(lr1);
		if (lr2 != -1)
			registerPanel.updateRegister(lr2);
		repaint();
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {

	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		repaint();
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		repaint();
	}


	@Override
	public void mousePressed(MouseEvent arg0) {

	}


	@Override
	public void mouseReleased(MouseEvent arg0) {

	}


	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		repaint();
	}
}
