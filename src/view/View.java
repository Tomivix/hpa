package view;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.Engine;
import view.buttons.ButtonPanel;
import view.code.CodePanel;
//import view.drag_area.DragAreaDialog;
import view.reg_mem.RMPanel;

//import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
//import java.awt.Point;
import java.awt.Toolkit;
//import java.awt.event.ComponentEvent;
//import java.awt.event.ComponentListener;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class View{
	public static enum Button{
		SAVE, LOAD, BUILD, STEP, BACKSTEP, RUN
	}

	public static final byte RR = 0, MR = 1, RM = 2;
	public static final byte ARITM = 0, STORE = 1, LOAD = 2, LOAD_ADDR = 3;

	public static View Instance;

	public static int FRAME_WIDTH = 890, FRAME_HEIGHT = 830;
	public static final float MAX_SCREEN_PERC_SIZE = 0.9f;
	public static double IMAGE_BUTTON_SCALE = 0.4;
	public static final int SLIDER_MIN_VAL = 100, SLIDER_MAX_VAL = 20 * SLIDER_MIN_VAL;
	public static final int PANELS_PAD = 6, DEFAULT_DIR_PANE_HEIGHT = 220, LABEL_HEIGHT = 15, LABEL_DOWN_PAD = 10;;
	public static final int REGISTER_WIDTH = 110, REGISTER_HEIGHT = 30, REGISTER_VERT_PADDING = 10, FONT_VERT_OFF = -3;
	public static final int REG_MEM_PAD = 10, PANEL_DRAW_UP_PAD = 10;
	public static final int MEM_CELL_WIDTH = 200, MEM_CELL_HEIGHT = 30, MEM_CELL_VERT_PADDING = 10;
	public static final int ARROW_WIDTH = 6, ARROW_LENGTH = MEM_CELL_VERT_PADDING/2+1;
	public static final int DEF_CODE_AREA_WIDTH = 300;
	public static final int DRAG_AREA_BUTTON_WIDTH = 50, DRAG_AREA_WIDTH = 100;
	public static final int DRAG_AREA_HOR_PAD = 8, DRAG_AREA_VERT_PAD = 30, DRAG_AREA_HEIGHT_PAD = 10;
	public static final int FLAG_REGISTER_WIDTH = 140, FLAG_REGISTER_HEIGHT = 50;

	public static int MEM_CELL_COL_COUNT = 5;

	//private DragAreaDialog dragDialog;
	private JFrame frame;
	private CodePanel codePanel;
	private RMPanel rmPanel;
	private ButtonPanel buttonPanel;
	private boolean running = false;// dragDialogShown = false;
	public View(){
		Instance = this;

		frame = new JFrame("HPA Studio 0.9.9");
//		dragDialog = new DragAreaDialog();
//
//		frame.addWindowListener(new WindowListener() {
//			@Override
//			public void windowActivated(WindowEvent e) {
//				if(dragDialogShown)
//					dragDialog.toFront();
//			}
//			@Override
//			public void windowIconified(WindowEvent e) {
//				hideDragDialog();
//			}
//			@Override
//			public void windowDeiconified(WindowEvent e) {}
//
//			@Override
//			public void windowDeactivated(WindowEvent e) {}
//			@Override
//			public void windowOpened(WindowEvent e) {}
//			@Override
//			public void windowClosing(WindowEvent e) {}
//			@Override
//			public void windowClosed(WindowEvent e) {}
//		});
//		frame.addComponentListener(new ComponentListener() {
//			@Override
//			public void componentShown(ComponentEvent e) {
//				if(dragDialogShown)
//					dragDialog.setVisible(true);
//			}
//			@Override
//			public void componentResized(ComponentEvent e) {
//				Dimension d = e.getComponent().getSize();
//				d.height -= buttonPanel.getSize().getHeight() + DRAG_AREA_VERT_PAD + DRAG_AREA_HEIGHT_PAD;
//				d.width = DRAG_AREA_WIDTH;
//				dragDialog.setSize(d);
//				if(dragDialogShown){
//					showDragDialog();
//				}
//			}
//			@Override
//			public void componentMoved(ComponentEvent e) {
//				Component frame = e.getComponent();
//				Point p = frame.getLocation();
//				p.translate((p.x > DRAG_AREA_WIDTH ? -dragDialog.getWidth() : DRAG_AREA_BUTTON_WIDTH + 2) + DRAG_AREA_HOR_PAD, (int) buttonPanel.getSize().getHeight() + DRAG_AREA_VERT_PAD);
//				dragDialog.setLocation(p);
//			}
//			@Override
//			public void componentHidden(ComponentEvent e) {
//				if(dragDialogShown)
//					dragDialog.setVisible(false);
//			}
//		});

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
			setRunStepButtonsEnabled(false);
		}else{
			setRunStepButtonsEnabled(true);
		}
	}

	private void setRunStepButtonsEnabled(boolean e){
		setButtonState(Button.RUN, e);
		setButtonState(Button.STEP, e);
		setButtonState(Button.BACKSTEP, e);
	}

	public void build(){
		Engine.current.buildDirectivesFromString(codePanel.getDirectives());
		Engine.current.buildOrdersFromString(codePanel.getOrders());
		setIsBuilt(true);
		Engine.current.pause();
		setRunning(false);
	}

	public boolean isRunning(){
		return running;
	}

	public void setRunning(boolean running){
		this.running = running;
		buttonPanel.setRunButtonImg(running ? 2 : 1);
		setButtons_Running(!running);
		codePanel.setCodeAreasEnabled(!running);
	}

	private void setButtons_Running(boolean b){
		setButtonState(Button.STEP, b);
		setButtonState(Button.BACKSTEP, b);
		setButtonState(Button.BUILD, b);
		setButtonState(Button.LOAD, b);
		setButtonState(Button.SAVE, b);
	}

	public void setButtonState(Button button, boolean state){
		buttonPanel.setButtonState(button, state);
	}

	public void save() throws FileNotFoundException {
		JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
		fileChooser.setFileFilter(new FileNameExtensionFilter("HPA source code", "hpa"));
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if(!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".hpa"))
				file = new File(fileChooser.getSelectedFile() + ".hpa");
			PrintWriter writer = new PrintWriter(file);
			String directives = codePanel.getDirectives(), orders = codePanel.getOrders();
			writer.println(directives); writer.println("<<<SEPARATOR>>>");
			writer.println(orders); writer.close();
		}
	}

	public void load() throws IOException {
		JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
		fileChooser.setFileFilter(new FileNameExtensionFilter("HPA source code", "hpa"));
		while (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			if(!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".hpa")) continue;
			File file = fileChooser.getSelectedFile(); String line = "", raw = "";
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			while((line = bufferedReader.readLine()) != null) raw += line + '\n';
			String[] code = raw.split("<<<SEPARATOR>>>"); if(code.length != 2) continue;
			codePanel.setDirectives(code[0]); codePanel.setOrders(code[1].trim());
			bufferedReader.close();	break;
		}
	}

	public void setFlagRegisterState(FlagRegister.STATE state){
		buttonPanel.setFlagRegisterState(state);
	}

//	public void showDragDialog(){
//		setDialogStatus(false);
//	}
//
//	public void hideDragDialog(){
//		setDialogStatus(true);
//	}
//
//	private void setDialogStatus(boolean hidden){
//		dragDialog.setVisible(!hidden);
//		dragDialogShown = !hidden;
//		codePanel.setDragAreaButtonText(hidden);
//	}
//
//	public boolean isDragDialogShown(){
//		return dragDialogShown;
//	}
}
