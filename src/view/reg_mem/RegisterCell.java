package view.reg_mem;

import java.awt.event.MouseEvent;

import view.View;

public class RegisterCell extends EditableCell {
	private static final long serialVersionUID = 1L;

	public RegisterCell(int topX, int topY, int index) {
		super(topX, topY);
		this.index = index;
		isRegister = true;
		setupLabels();
	}
	
	@Override
	protected int getValue() {
		return View.registers[index];
	}

	@Override
	protected String getLabel() {
		
		return index+":";
	}

	@Override
	protected boolean isLastEdited() {
		return index == RMPanel.lastReg1 || index == RMPanel.lastReg2;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		ValueEditor.displayRegisterValue(index);
	}
	
	

}
