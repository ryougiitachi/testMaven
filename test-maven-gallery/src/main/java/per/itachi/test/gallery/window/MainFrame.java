package per.itachi.test.gallery.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.entity.FrameOperationEntity;

public class MainFrame extends JFrame implements ActionListener, WindowListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5654742466250085432L;
	
	private final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	private JMenuBar menuBar;
	
	private JMenu menuFile;
	private JMenuItem menuItemExit;
	
	private JTextField txtFldUrlLink;
	private JButton btnSubmit;
	
	private DefaultTableModel tableModelUrlLink;
	
	private FrameOperationEntity operations;
	

	public MainFrame() {
		super();
		initWindow();
	}
	
	public MainFrame(FrameOperationEntity operations) {
		super();
		this.operations = operations;
		initWindow();
	}
	
	public MainFrame(GraphicsConfiguration gc) {
		super(gc);
		initWindow();
	}

	public MainFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		initWindow();
	}

	public MainFrame(String title) throws HeadlessException {
		super(title);
		initWindow();
	}

	private void initWindow() {
		initWindowLayout();
		initWindowMenu();
		initWindowInput();
		initWindowList();
		initWindowListener();
	}
	
	private void initWindowLayout() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimScreenSize = toolkit.getScreenSize();
		// initialise the size of main window  
		Dimension dimInitialSize = new Dimension(640, 400);
		Dimension dimPreferredSize = new Dimension(800, 500);
		Dimension dimMinimumSize = new Dimension(320, 200);
		Dimension dimMaximumSize = dimScreenSize;
		this.setSize(dimInitialSize);
		this.setPreferredSize(dimPreferredSize);
		this.setMinimumSize(dimMinimumSize);
		this.setMaximumSize(dimMaximumSize);
		// initialise the location of main window 
		Point pointInitial = new Point((dimScreenSize.width - dimInitialSize.width) >> 1, (dimScreenSize.height - dimInitialSize.height) >> 1);
		this.setLocation(pointInitial);
		this.setLayout(new BorderLayout());
	}
	
	private void initWindowMenu() {
		menuItemExit = new JMenuItem("Exit");
		menuFile = new JMenu("File");
		menuFile.add(menuItemExit);
		menuBar = new JMenuBar();
		menuBar.add(menuFile);
		this.add(menuBar, BorderLayout.NORTH);
	}
	
	private void initWindowInput() {
		txtFldUrlLink = new JTextField();
		btnSubmit = new JButton("submit");
		JPanel paneInput = new JPanel();
		paneInput.setLayout(new BorderLayout());
		paneInput.add(txtFldUrlLink, BorderLayout.CENTER);
		paneInput.add(btnSubmit, BorderLayout.EAST);
		this.add(paneInput, BorderLayout.CENTER);
	}
	
	private void initWindowList() {
		tableModelUrlLink = new DefaultTableModel();
		tableModelUrlLink.setColumnIdentifiers(new String[]{"Sequence", "URL", "Status"});
		JTable table = new JTable(tableModelUrlLink);
//		table.setModel(tableModelUrlLink);
		JScrollPane paneList = new JScrollPane(table);
//		paneList.setViewportView(table);
		this.add(paneList, BorderLayout.SOUTH);
	}
	
	private void initWindowListener() {
		// menu items 
		menuItemExit.addActionListener(this);
		// buttons 
		btnSubmit.addActionListener(this);
		// window 
		this.addWindowListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			JButton btnSource = (JButton)source;
			if (btnSource == btnSubmit) {
				String strUrlLink = txtFldUrlLink.getText();
				if (StringUtils.isNotBlank(strUrlLink)) {
					try {
						operations.putUrlLink(strUrlLink.trim());
					} 
					catch (InterruptedException ie) {
						logger.error("Error occured when putting url into queue.", ie);
					}
				}
			} 
			else {
			}
		}
		else if (source instanceof JMenuItem) {
		}
		else {
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		logger.info("The main frame has opened.");
	}

	@Override
	public void windowClosing(WindowEvent e) {
		logger.info("The main frame is closing.");
	}

	@Override
	public void windowClosed(WindowEvent e) {
		logger.info("The main frame has been closed.");
		System.out.println("The main frame has been closed.");
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
