package view.reg_mem;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import core.Engine;
import view.View;

import static javax.swing.SpringLayout.*;

public class MemoryPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private SpringLayout layout;
	private JLabel panelLabel;
	private MemoryCell[] memoryCells;
	public MemoryPanel(){
		super();
		memoryCells = new MemoryCell[0];
		super.setLayout(layout = new SpringLayout());
		super.setBorder(BorderFactory.createDashedBorder(null, 5, 5));
		panelLabel = new JLabel("Memory Cells");
		super.add(panelLabel);
		layout.putConstraint(NORTH, panelLabel, 5, NORTH, this);
		layout.putConstraint(WEST, panelLabel, 5, WEST, this);
		layout.putConstraint(EAST, panelLabel, 0, EAST, this);
		layout.putConstraint(SOUTH, panelLabel, View.LABEL_HEIGHT, NORTH, panelLabel);
	}
	
	public void setMemoryCells() {
		if(memoryCells != null){
			for(int i = 0; i < memoryCells.length; i++){
				super.remove(memoryCells[i]);
			}
		}
		this.memoryCells = new MemoryCell[Engine.current.getVarCount()];
		for(int i = 0; i < Engine.current.getVarCount(); i++){
			memoryCells[i]= new MemoryCell(View.MEM_CELL_WIDTH*(i%View.MEM_CELL_COL_COUNT), (int) (Math.floor(i/View.MEM_CELL_COL_COUNT)*(View.MEM_CELL_HEIGHT+View.MEM_CELL_VERT_PADDING))+View.LABEL_DOWN_PAD+View.LABEL_HEIGHT, i);
		}
		
		for(int i = 0; i < memoryCells.length; i++){
			MemoryCell memCell = memoryCells[i];
			super.add(memCell);
		}
		recalculateCellsPosition();
		super.revalidate();
	}
	
	public Point getRelativeCellLoc(int memCellIndex){
		return new Point(memoryCells[memCellIndex].getTopX()+View.MEM_CELL_WIDTH/2, memoryCells[memCellIndex].getTopY()+View.MEM_CELL_HEIGHT/2);
	}
	
	public void recalculateCellsPosition(){
		for(int i = 0; i < Engine.current.getVarCount(); i++){
			MemoryCell memCell = memoryCells[i];
			memCell.setTopX(View.MEM_CELL_WIDTH*(i%View.MEM_CELL_COL_COUNT));
			memCell.setTopY((int) (Math.floor(i/View.MEM_CELL_COL_COUNT)*(View.MEM_CELL_HEIGHT+View.MEM_CELL_VERT_PADDING))+View.LABEL_DOWN_PAD+View.LABEL_HEIGHT);
			layout.putConstraint(WEST, memCell, memCell.getTopX(), WEST, this);
			layout.putConstraint(EAST, memCell, View.MEM_CELL_WIDTH, WEST, memCell);
			layout.putConstraint(NORTH, memCell, memCell.getTopY(), NORTH, this);
			layout.putConstraint(SOUTH, memCell, View.MEM_CELL_HEIGHT, NORTH, memCell);
		}
		Dimension panelDim = new Dimension(View.MEM_CELL_COL_COUNT*View.MEM_CELL_WIDTH, (int) (Math.ceil((float)memoryCells.length/(float)View.MEM_CELL_COL_COUNT)*(View.MEM_CELL_HEIGHT+View.MEM_CELL_VERT_PADDING))+View.LABEL_DOWN_PAD+View.LABEL_HEIGHT);
		super.setPreferredSize(panelDim);
		super.doLayout();
	}
	
	public void updateCell(int iCell){
		memoryCells[iCell].updateValue();
	}
}
