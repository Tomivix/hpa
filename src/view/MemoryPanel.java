package view;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

public class MemoryPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private int[] memoryCells;
	public void setMemoryCells(int[] cells){
		this.memoryCells = cells;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		
		FontMetrics fm = g.getFontMetrics();
		int strH = fm.getHeight();
		for(int i = 0; i < memoryCells.length; i++){
			g.drawRect(View.MEM_CELL_WIDTH*(i%View.MEM_CELL_COL_COUNT), 
					(int) (Math.floor(i/View.MEM_CELL_COL_COUNT)*(View.MEM_CELL_HEIGHT+View.MEM_CELL_VERT_PADDING))+View.PANEL_DRAW_UP_PAD, 
					View.MEM_CELL_WIDTH, View.MEM_CELL_HEIGHT);
			if(i == RMPanel.lastCell){
				g.setFont(g.getFont().deriveFont(Font.BOLD));
				fm = g.getFontMetrics();
			}
			int strW = fm.stringWidth(""+memoryCells[i]);
			g.drawString(""+memoryCells[i], View.MEM_CELL_WIDTH*(i%View.MEM_CELL_COL_COUNT)+View.MEM_CELL_WIDTH/2-strW/2, 
					(int) (Math.floor(i/View.MEM_CELL_COL_COUNT)*(View.MEM_CELL_HEIGHT+View.MEM_CELL_VERT_PADDING)+(View.MEM_CELL_HEIGHT/2)+(strH/2)+View.FONT_VERT_OFF)+View.PANEL_DRAW_UP_PAD);
			if(i == RMPanel.lastCell){
				g.setFont(g.getFont().deriveFont(Font.PLAIN));
				fm = g.getFontMetrics();
			}
		}
	}
	
	public Point getRelativeCellLoc(int cellIndex){
		return new Point(View.MEM_CELL_WIDTH*(cellIndex%View.MEM_CELL_COL_COUNT)+View.MEM_CELL_WIDTH/2,
				(int) (Math.floor(cellIndex/View.MEM_CELL_COL_COUNT)*(View.MEM_CELL_HEIGHT+View.MEM_CELL_VERT_PADDING)+View.MEM_CELL_HEIGHT/2)+View.PANEL_DRAW_UP_PAD);
	}
}
