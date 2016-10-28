package view;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

import view.code.CodePanel;
import view.reg_mem.RMPanel;

import java.awt.Dimension;

import static javax.swing.SpringLayout.*;

public class View {
	public static final byte RR = 0, MR = 1, RM = 2;
	
	public static View Instance;
	
	public final static int FRAME_WIDTH = 1050, FRAME_HEIGHT = 730;
	public final static int PANELS_PAD = 6, DEFAULT_DIR_PANE_HEIGHT = 220, LABEL_HEIGHT = 15, LABEL_DOWN_PAD = 10;;
	public final static int REGISTER_WIDTH = 90, REGISTER_HEIGHT = 30, REGISTER_VERT_PADDING = 10, FONT_VERT_OFF = -3;
	public final static int REG_MEM_PAD = 10, PANEL_DRAW_UP_PAD = 10;
	public final static int MEM_CELL_WIDTH = 200, MEM_CELL_HEIGHT = 30, MEM_CELL_VERT_PADDING = 10;
	public final static int ARROW_WIDTH = 6, ARROW_LENGTH = MEM_CELL_VERT_PADDING/2+1;
	
	public static int MEM_CELL_COL_COUNT = 2;
	
	private JFrame frame;
	private CodePanel codePanel;
	private RMPanel rmPanel;
	public static int[] registers, memoryCells; //FIXME: Placholder, will be replaced with Engine getters
	private SpringLayout layout;
	public View(){
		Instance = this;
		
		frame = new JFrame("Pseudo Assembler Visualizer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(layout = new SpringLayout());
		
		codePanel = new CodePanel();
		frame.add(codePanel);
		layout.putConstraint(WEST, codePanel, PANELS_PAD, WEST, frame.getContentPane());
		layout.putConstraint(NORTH, codePanel, PANELS_PAD, NORTH, frame.getContentPane());
		layout.putConstraint(EAST, codePanel, -PANELS_PAD, HORIZONTAL_CENTER, frame.getContentPane());
		layout.putConstraint(SOUTH, codePanel, -PANELS_PAD, SOUTH, frame.getContentPane());
		
		rmPanel = new RMPanel();
		frame.add(rmPanel);
		layout.putConstraint(NORTH, rmPanel, PANELS_PAD, NORTH, frame.getContentPane());
		layout.putConstraint(SOUTH, rmPanel, -PANELS_PAD, SOUTH, frame.getContentPane());
		layout.putConstraint(EAST, rmPanel, -PANELS_PAD, EAST, frame.getContentPane());
		layout.putConstraint(WEST, rmPanel, PANELS_PAD, HORIZONTAL_CENTER, frame.getContentPane());
		
		frame.setVisible(true);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frame.setLocationRelativeTo(null);
	}
	
	public void setRegisters(int[] regs){
		registers = new int[regs.length];
		for(int i = 0; i < regs.length; i++){
			registers[i] = regs[i];
		}
		rmPanel.setRegisters();
	}
	
	public void setMemoryCells(int[] cells){
		memoryCells = new int[cells.length];
		for(int i = 0; i < cells.length; i++){
			memoryCells[i] = cells[i];
		}
		rmPanel.setMemoryCells();
	}
	
	public void updateValues(int reg, int source, byte mode){
		rmPanel.updateValues(reg, source, mode);
	}
}
