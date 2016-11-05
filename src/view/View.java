package view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import core.Engine;
import view.code.CodePanel;
import view.reg_mem.RMPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class View {
	public static final byte RR = 0, MR = 1, RM = 2;
	
	public static View Instance;
	
	public final static int FRAME_WIDTH = 1070, FRAME_HEIGHT = 730;
	public final static int PANELS_PAD = 6, DEFAULT_DIR_PANE_HEIGHT = 220, LABEL_HEIGHT = 15, LABEL_DOWN_PAD = 10;;
	public final static int REGISTER_WIDTH = 90, REGISTER_HEIGHT = 30, REGISTER_VERT_PADDING = 10, FONT_VERT_OFF = -3;
	public final static int REG_MEM_PAD = 10, PANEL_DRAW_UP_PAD = 10;
	public final static int MEM_CELL_WIDTH = 200, MEM_CELL_HEIGHT = 30, MEM_CELL_VERT_PADDING = 10;
	public final static int ARROW_WIDTH = 6, ARROW_LENGTH = MEM_CELL_VERT_PADDING/2+1;
	
	public static int MEM_CELL_COL_COUNT = 5;
	
	private JFrame frame;
	private CodePanel codePanel;
	private RMPanel rmPanel;
	public View(){
		Instance = this;
		
		frame = new JFrame("Pseudo Assembler Visualizer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel buttonPanel = new JPanel();
		JButton buildButton = new JButton("Build");
		buildButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Engine.current.buildDirectivesFromString(codePanel.getDirectives());
				Engine.current.buildOrdersFromString(codePanel.getOrders());;
			}
		});
		
		JButton runButton = new JButton("Run");
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Engine.current.run();
			}
		});
		
		JButton stepButton = new JButton("Step");
		stepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Engine.current.step();
			}
		});
		
		buttonPanel.add(buildButton);
		buttonPanel.add(runButton);
		buttonPanel.add(stepButton);
		
		c.fill = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		frame.add(buttonPanel, c);
		
		Dimension minDim = new Dimension(REGISTER_WIDTH + REG_MEM_PAD + MEM_CELL_WIDTH + 5, 0);
		
		codePanel = new CodePanel();
		codePanel.setMinimumSize(minDim);
		
		rmPanel = new RMPanel();
		rmPanel.setMinimumSize(minDim);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, codePanel, rmPanel);
		splitPane.setDividerLocation(FRAME_WIDTH/2);
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
	
	public void updateValues(int reg, int source, byte mode){
		rmPanel.updateValues(reg, source, mode);
	}
	
	public void updateRegister(int index){
		rmPanel.updateRegister(index);
	}
	
	public void updateMemCell(int index){
		rmPanel.updateMemCell(index);
	}
}
