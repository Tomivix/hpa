package view.reg_mem;

import java.awt.event.MouseEvent;

import core.Engine;

public class MemoryCell extends EditableCell {
	private static final long serialVersionUID = 1L;

	private String label;
	public MemoryCell(int topX, int topY, int index) {
		super(topX, topY);
		this.index = index;
		setupLabels();
	}
	
	@Override
	protected int getValue() {
		return Engine.current.getVarFromAdress((index * 4) + 1024);
	}

	@Override
	protected String getLabel() {
		String label = Engine.current.getVarLabel((index * 4) + 1024);
		return (index * 4) + 1024 + (label.equals("") ? "" : " (" + label + ")");
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
