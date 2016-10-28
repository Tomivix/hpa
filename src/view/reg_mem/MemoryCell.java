package view.reg_mem;

import view.View;

public class MemoryCell extends EditableCell {
	private static final long serialVersionUID = 1L;

	private String label;
	private int index;
	public MemoryCell(int topX, int topY, String label, int index) {
		super(topX, topY);
		this.label = label;
		this.index = index;
		setupLabels();
	}
	
	@Override
	protected int getValue() {
		return View.memoryCells[index];
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

}
