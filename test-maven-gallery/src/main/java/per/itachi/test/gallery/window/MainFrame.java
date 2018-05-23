package per.itachi.test.gallery.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.EventObject;

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

import per.itachi.test.gallery.entity.FrameGalleryItemEntity;
import per.itachi.test.gallery.entity.FrameOperationEntity;

public class MainFrame extends JFrame implements ActionListener, WindowListener, GalleryParserListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8062964715207008969L;

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
//		this.add(menuBar, BorderLayout.NORTH);
		this.setJMenuBar(menuBar);
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
	
	private void exit() {
		logger.info("Shutting down controllable threads... ");
		operations.shutdownControllableRunnable();
		logger.info("Shutting down gallery executor service... ");
		operations.shutdownExecutorService();
		logger.info("Exiting main program... ");
		System.exit(0);
		logger.info("Exited main program. "); // won't print 
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Runnable runnable = new EventQueueRunnable(EventQueueRunnable.EVENT_ID_ACTION_PERFORMED, event);
		EventQueue.invokeLater(runnable);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		Runnable runnable = new EventQueueRunnable(EventQueueRunnable.EVENT_ID_WINDOW_OPENED, e);
		EventQueue.invokeLater(runnable);
	}
	
	/**
	 * EventQueue.invokeLater will execute after current event, so runnable may not execute if program exit. 
	 * */
	@Override
	public void windowClosing(WindowEvent e) {
		Runnable runnable = new EventQueueRunnable(EventQueueRunnable.EVENT_ID_WINDOW_CLOSING, e);
		EventQueue.invokeLater(runnable);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		Runnable runnable = new EventQueueRunnable(EventQueueRunnable.EVENT_ID_WINDOW_CLOSED, e);
		EventQueue.invokeLater(runnable);
	}

	@Override
	public void windowIconified(WindowEvent e) {
		Runnable runnable = new EventQueueRunnable(EventQueueRunnable.EVENT_ID_WINDOW_ICONIFIED, e);
		EventQueue.invokeLater(runnable);
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		Runnable runnable = new EventQueueRunnable(EventQueueRunnable.EVENT_ID_WINDOW_DEICONIFIED, e);
		EventQueue.invokeLater(runnable);
	}

	@Override
	public void windowActivated(WindowEvent e) {
		Runnable runnable = new EventQueueRunnable(EventQueueRunnable.EVENT_ID_WINDOW_ACTIVATED, e);
		EventQueue.invokeLater(runnable);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		Runnable runnable = new EventQueueRunnable(EventQueueRunnable.EVENT_ID_WINDOW_DEACTIVATED, e);
		EventQueue.invokeLater(runnable);
	}

	@Override
	public void galleryStateChanged(EventObject event) {
		Runnable runnable = new EventQueueRunnable(EventQueueRunnable.EVENT_ID_ITEM_STATE_CHANGED, event);
		EventQueue.invokeLater(runnable);
	}
	
	/**
	 * If this class is static, member variables, such as btnSubmit, are not allowed to refer to. 
	 * it prompts, No enclosing instance of the type MainFrame is accessible in scope. 
	 * 
	 * */
	private class EventQueueRunnable implements Runnable {
		
		public static final int EVENT_ID_ACTION_PERFORMED = 1;
		public static final int EVENT_ID_WINDOW_OPENED = 2;
		public static final int EVENT_ID_WINDOW_CLOSING = 3;
		public static final int EVENT_ID_WINDOW_CLOSED = 4;
		public static final int EVENT_ID_WINDOW_ICONIFIED = 5;
		public static final int EVENT_ID_WINDOW_DEICONIFIED = 6;
		public static final int EVENT_ID_WINDOW_ACTIVATED = 7;
		public static final int EVENT_ID_WINDOW_DEACTIVATED = 8;
		public static final int EVENT_ID_ITEM_STATE_CHANGED = 9;
		
		private int eventID;
		
		private EventObject event;
		
		public EventQueueRunnable(int eventID, EventObject event) {
			this.eventID = eventID;
			this.event = event;
		}

		@Override
		public void run() {
			int eventID = this.eventID;
			EventObject event = this.event;
			ActionEvent actionEvent = null;
			WindowEvent windowEvent = null;
			if (event instanceof ActionEvent) {
				actionEvent = (ActionEvent)event; 
			}
			else if (event instanceof WindowEvent) {
				windowEvent = (WindowEvent)event;
			}
			else {
			}
			switch (eventID) {
			case EVENT_ID_ACTION_PERFORMED:
				actionPerformed(actionEvent);
				break;
			case EVENT_ID_WINDOW_OPENED:
				windowOpened(windowEvent);
				break;
			case EVENT_ID_WINDOW_CLOSING:
				windowClosing(windowEvent);
				break;
			case EVENT_ID_WINDOW_CLOSED:
				windowClosed(windowEvent);
				break;
			case EVENT_ID_WINDOW_ICONIFIED:
				windowIconified(windowEvent);
				break;
			case EVENT_ID_WINDOW_DEICONIFIED:
				windowDeiconified(windowEvent);
				break;
			case EVENT_ID_WINDOW_ACTIVATED:
				windowActivated(windowEvent);
				break;
			case EVENT_ID_WINDOW_DEACTIVATED:
				windowDeactivated(windowEvent);
				break;
			case EVENT_ID_ITEM_STATE_CHANGED:
				galleryStateChanged(event);
				break;
			default:
				break;
			}
		}
		
		private void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source instanceof JButton) {
				JButton btnSource = (JButton)source;
				if (btnSource == MainFrame.this.btnSubmit) {
					String strUrlLink = txtFldUrlLink.getText();
					if (StringUtils.isNotBlank(strUrlLink)) {
						try {
							int iRowCount = tableModelUrlLink.getRowCount();
							FrameGalleryItemEntity entity = new FrameGalleryItemEntity();
							entity.setSeqID(++iRowCount);
							entity.setUrlLink(strUrlLink.trim());
							entity.setStatus("Added");
							operations.putUrlLink(entity);
							tableModelUrlLink.addRow(new Object[]{entity.getSeqID(), entity.getUrlLink(), entity.getStatus()});
							logger.info("{} has been put into queue. ", strUrlLink);
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
				JMenuItem menuItem = (JMenuItem)source;
				if (menuItem == MainFrame.this.menuItemExit) {
					MainFrame.this.exit();
				} 
				else {
				}
			}
			else {
			}
		}
		
		private void windowOpened(WindowEvent e) {
			logger.info("The main frame has opened.");
		}
		
		private void windowClosing(WindowEvent e) {
			logger.info("The main frame is closing.");
			MainFrame.this.exit();
		}
		
		private void windowClosed(WindowEvent e) {
			logger.info("The main frame has been closed.");
			System.out.println("The main frame has been closed.");
		}
		
		private void windowIconified(WindowEvent e) {
		}

		private void windowDeiconified(WindowEvent e) {
		}

		private void windowActivated(WindowEvent e) {
		}

		private void windowDeactivated(WindowEvent e) {
		}
		
		private void galleryStateChanged(EventObject event) {
			Object source = event.getSource();
			FrameGalleryItemEntity entity = (FrameGalleryItemEntity)source;
			//Both row and column are based on 0. 
			tableModelUrlLink.setValueAt(entity.getStatus(), entity.getSeqID() - 1, 2);
		}
	}
}
