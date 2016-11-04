package view.reg_mem;

import java.awt.event.MouseEvent;

import core.Engine;

public class MemoryCell extends EditableCell {
	private static final long serialVersionUID = 1L;

	private String label;
	public MemoryCell(int topX, int topY, String label, int index) {
		super(topX, topY);
		this.label = label;
		this.index = index;
		setupLabels();
	}
	
	@Override
	protected int getValue() {
		return Engine.current.getVar(index);
	}

	@Override
	protected String getLabel() {
		return label;
	}
	
	public void setTopX(int newX){
		this.topX = newX;
	}
	
	public void setTopY(int newY){
		this.topY = newY;
	}

	@Override
	protected boolean isLastEdited() {
		return index == RMPanel.lastCell;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		ValueEditor.displayCellValue(index, label);
	}

}
