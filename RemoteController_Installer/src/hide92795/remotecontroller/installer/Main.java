package hide92795.remotecontroller.installer;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("hide92795.remotecontroller.installer.string");

	private JFrame frmRemoteControllerInstaller;
	private JTextField textfieldFile;
	private SpringLayout springLayout;
	private JButton buttonSelect;
	private JLabel labelFile;
	private JButton buttonOk;
	private JLabel labelInfomation1;
	private JLabel labelInfomation2;
	private JLabel labelState;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmRemoteControllerInstaller.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmRemoteControllerInstaller = new JFrame();
		frmRemoteControllerInstaller.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/hide92795/remotecontroller/installer/image/icon.png")));
		frmRemoteControllerInstaller.setTitle("RemoteController Installer");
		frmRemoteControllerInstaller.setBounds(100, 100, 450, 140);
		frmRemoteControllerInstaller.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		springLayout = new SpringLayout();
		frmRemoteControllerInstaller.getContentPane().setLayout(springLayout);
		frmRemoteControllerInstaller.getContentPane().add(getTextfieldFile());
		frmRemoteControllerInstaller.getContentPane().add(getButtonSelect());
		frmRemoteControllerInstaller.getContentPane().add(getLabelFile());
		frmRemoteControllerInstaller.getContentPane().add(getButtonOk());
		frmRemoteControllerInstaller.getContentPane().add(getLabelInfomation1());
		frmRemoteControllerInstaller.getContentPane().add(getLabelInfomation2());
		frmRemoteControllerInstaller.getContentPane().add(getLabelState());
		frmRemoteControllerInstaller.setLocationRelativeTo(null);
	}

	private JTextField getTextfieldFile() {
		if (textfieldFile == null) {
			textfieldFile = new JTextField();
			springLayout.putConstraint(SpringLayout.NORTH, textfieldFile, 10, SpringLayout.NORTH, frmRemoteControllerInstaller.getContentPane());
			textfieldFile.setColumns(10);
		}
		return textfieldFile;
	}

	private JButton getButtonSelect() {
		if (buttonSelect == null) {
			buttonSelect = new JButton(BUNDLE.getString("str_select"));
			buttonSelect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser filechooser = new JFileChooser();
					filechooser.setFileFilter(new FileNameExtensionFilter("*.jar", "jar"));
					filechooser.setAcceptAllFileFilterUsed(false);
					int selected = filechooser.showOpenDialog(frmRemoteControllerInstaller);

					switch (selected) {
					case JFileChooser.APPROVE_OPTION:
						File selectedfile = filechooser.getSelectedFile();
						try {
							getTextfieldFile().setText(selectedfile.getCanonicalPath());
						} catch (IOException e1) {
						}
						break;
					default:
						break;
					}
				}
			});
			springLayout.putConstraint(SpringLayout.NORTH, buttonSelect, 10, SpringLayout.NORTH, frmRemoteControllerInstaller.getContentPane());
			springLayout.putConstraint(SpringLayout.EAST, getTextfieldFile(), -6, SpringLayout.WEST, buttonSelect);
			springLayout.putConstraint(SpringLayout.EAST, buttonSelect, -10, SpringLayout.EAST, frmRemoteControllerInstaller.getContentPane());
		}
		return buttonSelect;
	}

	private JLabel getLabelFile() {
		if (labelFile == null) {
			labelFile = new JLabel(BUNDLE.getString("str_file"));
			springLayout.putConstraint(SpringLayout.NORTH, labelFile, 13, SpringLayout.NORTH, frmRemoteControllerInstaller.getContentPane());
			springLayout.putConstraint(SpringLayout.WEST, getTextfieldFile(), 6, SpringLayout.EAST, labelFile);
			springLayout.putConstraint(SpringLayout.WEST, labelFile, 10, SpringLayout.WEST, frmRemoteControllerInstaller.getContentPane());
		}
		return labelFile;
	}

	private JButton getButtonOk() {
		if (buttonOk == null) {
			buttonOk = new JButton(BUNDLE.getString("str_ok"));
			buttonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String selected_s = getTextfieldFile().getText();
					File selected = new File(selected_s);
					if (selected.exists()) {
						getButtonOk().setEnabled(false);
						getTextfieldFile().setEnabled(false);
						getTextfieldFile().setEditable(false);
						getButtonSelect().setEnabled(false);
						getLabelState().setText(BUNDLE.getString("str_processiong"));
						Patcher patcher = new Patcher(Main.this, selected);
						patcher.execute();
					} else {
						getLabelState().setText(BUNDLE.getString("str_file_not_found"));
					}

				}
			});
			springLayout.putConstraint(SpringLayout.SOUTH, buttonOk, -10, SpringLayout.SOUTH, frmRemoteControllerInstaller.getContentPane());
			springLayout.putConstraint(SpringLayout.EAST, buttonOk, 0, SpringLayout.EAST, getButtonSelect());
		}
		return buttonOk;
	}

	private JLabel getLabelInfomation1() {
		if (labelInfomation1 == null) {
			labelInfomation1 = new JLabel(BUNDLE.getString("str_infomation_1"));
			springLayout.putConstraint(SpringLayout.NORTH, labelInfomation1, 7, SpringLayout.SOUTH, getTextfieldFile());
			springLayout.putConstraint(SpringLayout.WEST, labelInfomation1, 0, SpringLayout.WEST, getLabelFile());
		}
		return labelInfomation1;
	}

	private JLabel getLabelInfomation2() {
		if (labelInfomation2 == null) {
			labelInfomation2 = new JLabel(BUNDLE.getString("str_infomation_2")); //$NON-NLS-1$
			springLayout.putConstraint(SpringLayout.NORTH, labelInfomation2, 6, SpringLayout.SOUTH, getLabelInfomation1());
			springLayout.putConstraint(SpringLayout.WEST, labelInfomation2, 0, SpringLayout.WEST, getLabelFile());
		}
		return labelInfomation2;
	}

	private JLabel getLabelState() {
		if (labelState == null) {
			labelState = new JLabel(); //$NON-NLS-1$
			springLayout.putConstraint(SpringLayout.SOUTH, labelState, 0, SpringLayout.SOUTH, getButtonOk());
			springLayout.putConstraint(SpringLayout.EAST, labelState, -6, SpringLayout.WEST, getButtonOk());
		}
		return labelState;
	}

	public void errorOnProcess(String message) {
		getLabelState().setText(BUNDLE.getString("str_error_in_process") + message);
	}

	public void success() {
		getLabelState().setText(BUNDLE.getString("str_success"));
	}

	public void publish(String message) {
		getLabelState().setText(BUNDLE.getString("str_processiong") + " : " + message);
	}
}
