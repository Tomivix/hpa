package view;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import core.Engine;
import view.buttons.ButtonPanel;
import view.code.CodePanel;
import view.reg_mem.RMPanel;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;


public class View{
	public static final byte RR = 0, MR = 1, RM = 2;
	public static final byte ARITM = 0, STORE = 1, LOAD = 2, LOAD_ADDR = 3;
	
	public static View Instance;
	
	public static int FRAME_WIDTH = 890, FRAME_HEIGHT = 780;
	public static final float MAX_SCREEN_PERC_SIZE = 0.9f;
	public static double IMAGE_BUTTON_SCALE = 0.4;
	public static final int SLIDER_MIN_VAL = 100, SLIDER_MAX_VAL = 20 * SLIDER_MIN_VAL;
	public static final int PANELS_PAD = 6, DEFAULT_DIR_PANE_HEIGHT = 220, LABEL_HEIGHT = 15, LABEL_DOWN_PAD = 10;;
	public static final int REGISTER_WIDTH = 110, REGISTER_HEIGHT = 30, REGISTER_VERT_PADDING = 10, FONT_VERT_OFF = -3;
	public static final int REG_MEM_PAD = 10, PANEL_DRAW_UP_PAD = 10;
	public static final int MEM_CELL_WIDTH = 200, MEM_CELL_HEIGHT = 30, MEM_CELL_VERT_PADDING = 10;
	public static final int ARROW_WIDTH = 6, ARROW_LENGTH = MEM_CELL_VERT_PADDING/2+1;
	public static final int DEF_CODE_AREA_WIDTH = 300;
	
	public static int MEM_CELL_COL_COUNT = 5;
	
	private JFrame frame;
	private CodePanel codePanel;
	private RMPanel rmPanel;
	private ButtonPanel buttonPanel;
	private boolean running = false;
	public View(){
		Instance = this;
		
		frame = new JFrame("Pseudo Assembler Visualizer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		if(screenDim.getHeight() * MAX_SCREEN_PERC_SIZE < FRAME_HEIGHT){
			FRAME_HEIGHT = (int) (screenDim.getHeight() * MAX_SCREEN_PERC_SIZE);
		}
		if(screenDim.getWidth() * MAX_SCREEN_PERC_SIZE < FRAME_WIDTH){
			FRAME_WIDTH = (int) (screenDim.getWidth() * MAX_SCREEN_PERC_SIZE);
		}
		
		buttonPanel = new ButtonPanel();
		setIsBuilt(false);
		
		c.fill = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		frame.add(buttonPanel, c);
		
		Dimension minCodeDim = new Dimension(DEF_CODE_AREA_WIDTH, 0);
		
		codePanel = new CodePanel();
		codePanel.setMinimumSize(minCodeDim);
		
		Dimension minRMDim = new Dimension(REGISTER_WIDTH+MEM_CELL_WIDTH+50, 0);
		
		rmPanel = new RMPanel();
		rmPanel.setMinimumSize(minRMDim);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codePanel, rmPanel);
		splitPane.setDividerLocation(DEF_CODE_AREA_WIDTH);
		c.weighty = 100;
		c.gridy = 2;
		frame.add(splitPane, c);
		
		frame.setVisible(true);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frame.setLocationRelativeTo(null);
	}
	
	public void setRegisters(){
		rmPanel.setRegisters();
	}
	
	public void setMemoryCells(){
		rmPanel.setMemoryCells();
	}
	
	public void updateValues(final int reg, final int source, final byte mode, final byte opType){
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				rmPanel.updateValues(reg, source, mode, opType);
				
			}
		});
	}
	
	public void updateRegister(int index){
		rmPanel.updateRegister(index);
	}
	
	public void updateMemCell(int index){
		rmPanel.updateMemCell(index);
	}

	public void resetLastEdited() {
		rmPanel.resetLastEdited();
	}
	
	public void highlightLine(int index){
		codePanel.highlightLine(index);
	}
	
	public void setIsBuilt(boolean built){
		if(!running && !built){
			buttonPanel.setRunStepButtonsEnabled(false);
		}else{
			buttonPanel.setRunStepButtonsEnabled(true);
		}
	}
	
	public void build(){
		Engine.current.buildDirectivesFromString(codePanel.getDirectives());
		Engine.current.buildOrdersFromString(codePanel.getOrders());
		setIsBuilt(true);
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}
}
