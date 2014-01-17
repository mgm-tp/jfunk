/*
 * Copyright (c) 2014 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.jfunk.core.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * GUI for starting test scripts
 * 
 */
public final class JFunkFrame extends JFrame {
	private static final String PATH_TO_MAIL_CONFIG_FILES = "config/email_accounts";
	private static final String PROPS_SUFFIX = "properties";

	private static final long serialVersionUID = 1L;

	private final Logger log = Logger.getLogger(getClass());

	private JTree tree;
	private JMenuBar menuBar;
	private JPopupMenu popup;
	private JComboBox jFunkPropertyFilesComboBox;
	private JComboBox testSystemsComboBox;
	private JComboBox mailConfigurationsComboBox;
	private JComboBox threadCountComboBox;
	private JComboBox parallelComboBox;
	private PropertiesComboBoxModel mailConfigurationsModel;
	private PropertiesComboBoxModel testSystemsModel;
	private PropertiesComboBoxModel jFunkPropertyFilesModel;
	private JPanel jPanelUtilities;

	private final List<File> roots;

	private final Action fileOpenScriptsAction = new FileOpenScriptsAction();
	private final Action fileEditPropertiesAction = new FileEditPropertiesAction();
	private final Action fileExitAction = new FileExitAction();
	private final Action toolsRunAction = new ToolsRunAction();

	public static void createAndShow(final List<File> roots) {
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				final JFunkFrame frame = new JFunkFrame(roots);
				frame.setUpGui();
				frame.setVisible(true);
				frame.tree.requestFocus();
			}
		};
		SwingUtilities.invokeLater(r);
	}

	public static void main(final String[] args) {
		List<File> roots = new ArrayList<File>(Math.max(1, args.length));
		if (args.length > 0) {
			for (String arg : args) {
				File file = new File(arg);
				if (file.exists()) {
					roots.add(new File(arg));
				}
			}
		} else {
			roots.add(new File("scripts"));
		}
		JFunkFrame.createAndShow(roots);
	}

	private JFunkFrame(final List<File> roots) {
		super("jFunk SkriptRunner UI");
		this.roots = roots;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private boolean readState() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream("jFunkFrame.state"));
			setBounds((Rectangle) ois.readObject());
			setExtendedState(ois.readInt());
			tree.setSelectionPaths((TreePath[]) ois.readObject());
			jFunkPropertyFilesComboBox.setSelectedIndex(ois.readInt());
			testSystemsComboBox.setSelectedIndex(ois.readInt());
			mailConfigurationsComboBox.setSelectedIndex(ois.readInt());
			threadCountComboBox.setSelectedIndex(ois.readInt());
			parallelComboBox.setSelectedIndex(ois.readInt());
			return true;
		} catch (final FileNotFoundException ex) {
			log.warn("Could not find saved state (which is fine during first start)");
			return false;
		} catch (final Exception ex) {
			log.error("Error reading state", ex);
			return false;
		} finally {
			IOUtils.closeQuietly(ois);
		}
	}

	private void writeState() {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream("jFunkFrame.state"));
			oos.writeObject(getBounds());
			oos.writeInt(getExtendedState());
			oos.writeObject(tree.getSelectionPaths());
			oos.writeInt(jFunkPropertyFilesComboBox.getSelectedIndex());
			oos.writeInt(testSystemsComboBox.getSelectedIndex());
			oos.writeInt(mailConfigurationsComboBox.getSelectedIndex());
			oos.writeInt(threadCountComboBox.getSelectedIndex());
			oos.writeInt(parallelComboBox.getSelectedIndex());
		} catch (final IOException ex) {
			log.error("Error writing prferences.", ex);
		} finally {
			IOUtils.closeQuietly(oos);
		}
	}

	private void setUpGui() {
		buildMenuBar();
		buildToolBar();
		buildPopup();
		buildTree();

		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResource("/jFunk.png"));
			setIconImage(image);
		} catch (Exception e) {
			log.warn("Could not read icon image, standard icon will be used. Exception was: " + e.getMessage());
		}

		setJMenuBar(menuBar);

		if (!readState()) {
			setSize(520, 600);
			setLocationRelativeTo(null);
		}

		final JScrollPane scrollPane = new JScrollPane(tree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(jPanelUtilities, BorderLayout.NORTH);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				exit();
			}
		});
	}

	private void buildMenuBar() {
		menuBar = new JMenuBar();

		JMenu menu = new JMenu("File");
		menu.setMnemonic('F');
		menu.add(fileOpenScriptsAction);
		menu.add(fileEditPropertiesAction);
		menu.addSeparator();
		menu.add(fileExitAction);
		menuBar.add(menu);

		menu = new JMenu("Tools");
		menu.setMnemonic('T');
		menu.add(toolsRunAction);
		menuBar.add(menu);
	}

	private void buildPopup() {
		popup = new JPopupMenu();
		popup.add(fileOpenScriptsAction);
		popup.add(toolsRunAction);
	}

	private void buildToolBar() {
		// Toolbar for the first line
		JToolBar toolBarFirstLine = new JToolBar("Toolbar");
		toolBarFirstLine.setRequestFocusEnabled(false);
		toolBarFirstLine.setFloatable(false);

		toolBarFirstLine.add(fileOpenScriptsAction);
		toolBarFirstLine.add(toolsRunAction);
		toolBarFirstLine.addSeparator();

		// Build component jFunk property files
		jFunkPropertyFilesModel = new PropertiesComboBoxModel("config", "jfunk", PROPS_SUFFIX, null, true);
		jFunkPropertyFilesComboBox = new JComboBox(jFunkPropertyFilesModel);
		jFunkPropertyFilesComboBox.setBorder(BorderFactory.createTitledBorder("jFunk configuration"));
		// Multi-line tooltip
		jFunkPropertyFilesComboBox.setToolTipText("<html>List of jFunk property files containing listener and modules"
				+ "<br>Location: all files in directory 'config'</br></html>");
		toolBarFirstLine.add(jFunkPropertyFilesComboBox);

		// Build component testsystems
		testSystemsModel = new PropertiesComboBoxModel("config/system", null, PROPS_SUFFIX, "baseurl", false);
		testSystemsComboBox = new JComboBox(testSystemsModel);
		testSystemsComboBox.setBorder(BorderFactory.createTitledBorder("Test system"));
		// Multi-line tooltip
		testSystemsComboBox.setToolTipText("<html>List of test systems"
				+ "<br>Defined by all files in directory 'config/system'</br></html>");
		testSystemsComboBox.setSelectedItem(0);
		toolBarFirstLine.add(testSystemsComboBox);

		// Toolbar for the second line
		JToolBar toolBarSecondLine = new JToolBar();
		toolBarSecondLine.setRequestFocusEnabled(false);
		toolBarSecondLine.setFloatable(false);

		// Build component mail configuration
		mailConfigurationsModel = new PropertiesComboBoxModel(PATH_TO_MAIL_CONFIG_FILES, null, PROPS_SUFFIX, null, false);
		mailConfigurationsComboBox = new JComboBox(mailConfigurationsModel);
		mailConfigurationsComboBox.setBorder(BorderFactory.createTitledBorder("Mail configuration"));
		// Multi-line tooltip
		mailConfigurationsComboBox.setToolTipText("<html>List of mail configurations"
				+ "<br>Defined by all files in directory 'config/email_accounts'</br></html>");
		toolBarSecondLine.add(mailConfigurationsComboBox);

		// Build component thread count
		threadCountComboBox = new JComboBox(createNumberArray(30));
		threadCountComboBox.setBorder(BorderFactory.createTitledBorder("Threads"));
		threadCountComboBox.setToolTipText("Number of threads which will be used for the execution of the selected script files");
		toolBarSecondLine.add(threadCountComboBox);

		// Build component parallel
		parallelComboBox = new JComboBox(new String[] { "yes", "no" });
		parallelComboBox.setSelectedIndex(1);
		parallelComboBox.setBorder(BorderFactory.createTitledBorder("Parallel?"));
		parallelComboBox
				.setToolTipText("If set to 'yes', a single script will be executed "
						+ "multiple times using the specified number of threads");
		toolBarSecondLine.add(parallelComboBox);

		jPanelUtilities = new JPanel();
		jPanelUtilities.setLayout(new BorderLayout());
		jPanelUtilities.add(toolBarFirstLine, BorderLayout.NORTH);
		jPanelUtilities.add(toolBarSecondLine, BorderLayout.CENTER);
	}

	private Integer[] createNumberArray(final int max) {
		Integer[] numbers = new Integer[max];
		for (int i = 0; i < max; i++) {
			numbers[i] = i + 1;
		}
		return numbers;
	}

	private void buildTree() {
		tree = new JTree(new ScriptsTreeModel(roots));
		tree.setCellRenderer(new ScriptsTreeCellRenderer());
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					runScripts();
				}
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (!e.isPopupTrigger() && e.getClickCount() == 2) {
					if (tree.getModel().isLeaf(tree.getSelectionPath().getLastPathComponent())) {
						runScripts();
					}
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					TreePath[] paths = tree.getSelectionPaths();
					boolean newPath = true;
					for (TreePath path : paths) {
						if (selPath.equals(path)) {
							newPath = false;
							break;
						}
					}
					if (newPath) {
						tree.setSelectionPath(selPath);
					}
					popup.show((Component) e.getSource(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					TreePath[] paths = tree.getSelectionPaths();
					boolean newPath = true;
					for (TreePath path : paths) {
						if (selPath.equals(path)) {
							newPath = false;
							break;
						}
					}
					if (newPath) {
						tree.setSelectionPath(selPath);
					}
					popup.show((Component) e.getSource(), e.getX(), e.getY());
				}
			}
		});
	}

	private void exit() {
		writeState();
		System.exit(0);
	}

	private void runScripts() {
		try {
			TreePath[] paths = tree.getSelectionPaths();

			if (paths == null) {
				JOptionPane.showMessageDialog(JFunkFrame.this, "No script(s) selected!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			String testSystem;
			boolean parallel = false;
			if (StringUtils.equals((String) parallelComboBox.getSelectedItem(), "yes")) {
				parallel = true;
			}
			testSystem = testSystemsModel.getSelectedItem();

			int threads = (Integer) threadCountComboBox.getSelectedItem();

			List<String> commandsList = new ArrayList<String>(paths.length + 5);
			commandsList.add("cmd.exe");
			commandsList.add("/X");
			commandsList.add("/C");
			commandsList.add("start");
			commandsList.add("run_testskript.bat");
			//Threadcount wird als argument weiter gereicht.
			commandsList.add("-threadcount=" + threads);
			//Flag parallel wird als Parameter weiter gereicht.
			if (parallel) {
				commandsList.add("-parallel");
			}

			if (paths.length == 1) {
				commandsList.add(((File) paths[0].getLastPathComponent()).getPath());
			} else {
				boolean hasScriptOrDir = false;
				for (TreePath path : paths) {
					File file = (File) path.getLastPathComponent();
					if (file.isFile()) {
						commandsList.add(file.getPath());
						hasScriptOrDir = true;
					}
				}
				if (!hasScriptOrDir) {
					// Es wurden nur Verzeichnisse ausgewählt.
					// Erstes Verzeichnis nehmen!
					commandsList.add(((File) paths[0].getLastPathComponent()).getPath());
				}
			}

			final ProcessBuilder pb = new ProcessBuilder(commandsList);
			pb.environment().put("EXIT_AFTER_RUNNING", "true");
			StringBuilder opts = new StringBuilder("-Djfunk.props.file=" + jFunkPropertyFilesModel.getSelectedItem());
			opts.append(" -Dtestsystem=" + testSystem);
			String mailConfig = mailConfigurationsModel.getSelectedItem();
			if (!StringUtils.equals(mailConfig, "default")) {
				opts.append(" -Dsystem.properties.mailconfig=email_accounts/" + mailConfig + "." + PROPS_SUFFIX);
			}
			pb.environment().put("APP_OPTS", opts.toString());
			pb.start();
		} catch (final Exception ex) {
			log.error(ex.getMessage(), ex);
			JOptionPane.showMessageDialog(JFunkFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private class FileOpenScriptsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public FileOpenScriptsAction() {
			super("Open script(s)");
			final URL url = getClass().getResource("/Open16.gif");
			putValue(SMALL_ICON, new ImageIcon(url));
			putValue(SHORT_DESCRIPTION, "Open script(s)");
			putValue(MNEMONIC_KEY, KeyEvent.VK_S);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				TreePath[] paths = tree.getSelectionPaths();
				if (paths == null) {
					return;
				}

				for (TreePath path : paths) {
					File file = (File) path.getLastPathComponent();
					if (file.isFile()) {
						if (System.getProperty("os.name").toLowerCase().contains("windows")) {
							String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
							Runtime.getRuntime().exec(cmd);
						} else {
							Desktop.getDesktop().edit(file);
						}
					}
				}
			} catch (final Exception ex) {
				log.error(ex.getMessage(), ex);
				JOptionPane.showMessageDialog(JFunkFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class FileEditPropertiesAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public FileEditPropertiesAction() {
			super("Edit properties file");
			final URL url = getClass().getResource("/Edit16.gif");
			putValue(SMALL_ICON, new ImageIcon(url));
			putValue(SHORT_DESCRIPTION, "Edit properties file");
			putValue(MNEMONIC_KEY, KeyEvent.VK_E);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				File f = new File("config/" + jFunkPropertyFilesModel.getSelectedItem()
						+ (jFunkPropertyFilesModel.isIncludeSuffix() ? "" : "." + PROPS_SUFFIX));
				if (System.getProperty("os.name").toLowerCase().contains("windows")) {
					String cmd = "rundll32 url.dll,FileProtocolHandler " + f.getCanonicalPath();
					Runtime.getRuntime().exec(cmd);
				} else {
					Desktop.getDesktop().edit(f);
				}
			} catch (final Exception ex) {
				log.error(ex.getMessage(), ex);
				JOptionPane.showMessageDialog(JFunkFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class FileExitAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public FileExitAction() {
			super("Exit");
			putValue(MNEMONIC_KEY, KeyEvent.VK_X);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			exit();
		}
	}

	private class ToolsRunAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ToolsRunAction() {
			super("Run script(s)");
			final URL url = getClass().getResource("/Play16.gif");
			putValue(SMALL_ICON, new ImageIcon(url));
			putValue(SHORT_DESCRIPTION, "Run script(s)");
			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			runScripts();
		}
	}
}