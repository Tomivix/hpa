package view.reg_mem;

import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import core.Engine;
import view.View;

import static javax.swing.SpringLayout.*;

public class RegisterPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private SpringLayout layout;
	private JLabel panelLabel;
	private RegisterCell[] registerCells;
	public RegisterPanel(){
		super();
		super.setLayout(layout = new SpringLayout());
		super.setBorder(BorderFactory.createDashedBorder(null, 5, 5));
		panelLabel = new JLabel("Registers");
		super.add(panelLabel);
		layout.putConstraint(NORTH, panelLabel, 5, NORTH, this);
		layout.putConstraint(WEST, panelLabel, 5, WEST, this);
		layout.putConstraint(EAST, panelLabel, 0, EAST, this);
		layout.putConstraint(SOUTH, panelLabel, View.LABEL_HEIGHT, NORTH, panelLabel);
	}
	
	public void setRegisters() {
		this.registerCells = new RegisterCell[Engine.current.getRegCount()];
		for(int i = 0; i < Engine.current.getRegCount(); i++){
			registerCells[i]= new RegisterCell(0, i*(View.REGISTER_HEIGHT+View.REGISTER_VERT_PADDING)+View.LABEL_DOWN_PAD+View.LABEL_HEIGHT, i);
		}
		for(int i = 0; i < registerCells.length; i++){
			RegisterCell regCell = registerCells[i];
			super.add(regCell);
			layout.putConstraint(WEST, regCell, 1, WEST, this);
			layout.putConstraint(EAST, regCell, -1, EAST, this);
			layout.putConstraint(NORTH, regCell, regCell.getTopY(), NORTH, this);
			layout.putConstraint(SOUTH, regCell, View.REGISTER_HEIGHT, NORTH, regCell);
		}
	}
	
	public Point getRelativeRegisterLoc(int regIndex){
		return new Point(registerCells[regIndex].getTopX()+View.REGISTER_WIDTH/2, registerCells[regIndex].getTopY()+View.REGISTER_HEIGHT/2);
	}

	public void updateRegister(int iReg){
		registerCells[iReg].updateValue();
	}
}
