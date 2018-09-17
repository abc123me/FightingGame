package jeremiahlowe.fightinggame.client.launcher;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.client.FightingGameClient;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;

public class Launcher extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static final String CONFIG_FILE_NAME = "fg_last_config.properties";
	public static final int MINIMUM_RES = 50;
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 500;
	public static final int MAXIMUM_RES = 100000;

	private static final String PROPERTY_KEY_PING = "ping";
	private static final String PROPERTY_KEY_RES_PRESET = "resolution_preset";
	private static final String PROPERTY_KEY_PORT = "port";
	private static final String PROPERTY_KEY_HOST = "hostname";
	private static final String PROPERTY_KEY_HACKS = "hacks";
	private static final String PROPERTY_KEY_NAME = "player_name";
	private static final String PROPERTY_KEY_HEIGHT = "height";
	private static final String PROPERTY_KEY_WIDTH = "width";
	private static final String PROPERTY_KEY_FULLSCREEN = "fullscreen";
	private static final String PROPERTY_KEY_FOLLOW = "follow";
	
	private JPanel contentPane;
	private JCheckBox chckbxHacks;
	private JSpinner widthSpinner;
	private JSpinner heightSpinner;
	private JComboBox<ResolutionPreset> presetBox;
	private JCheckBox chckbxFullscreen;
	private JTextField txtName;
	private JTextField txtLocalhost;
	private JSpinner portSpinner;
	private JSpinner pingSpinner;
	private JCheckBox chckbxFollowPlayer;
	
	private Properties props;
	
	public static void main(String[] args) {
		Launcher frame = new Launcher();
		frame.setVisible(true);
		frame.loadConfig(CONFIG_FILE_NAME);
	}
	public Launcher() {
		props = new Properties();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("FightingGame Launcher (" + Meta.VERSION + " - " + Meta.VERSION_ID + ")");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblImUsingAn = new JLabel("I'm using an absolute layout because im lazy");
		lblImUsingAn.setBounds(12, 231, 424, 15);
		contentPane.add(lblImUsingAn);
		JLabel lblIfYouHave = new JLabel("If you have a 4K display, fuck you");
		lblIfYouHave.setBounds(12, 246, 424, 15);
		contentPane.add(lblIfYouHave);
		JLabel lblResolution = new JLabel("Resolution:");
		lblResolution.setBounds(12, 12, 205, 15);
		contentPane.add(lblResolution);
		JLabel lblPlayerName = new JLabel("Player name:");
		lblPlayerName.setBounds(12, 68, 117, 15);
		contentPane.add(lblPlayerName);
		JLabel lblIpPort = new JLabel("Hostname:");
		lblIpPort.setBounds(12, 102, 117, 15);
		contentPane.add(lblIpPort);
		JLabel lblMs = new JLabel("simulated ping (in ms)");
		lblMs.setBounds(126, 158, 237, 15);
		contentPane.add(lblMs)  ;
		
		JButton btnLaunch = new JButton("Launch");
		btnLaunch.setBounds(12, 194, 117, 25);
		contentPane.add(btnLaunch);
		chckbxHacks = new JCheckBox("With hacks");
		chckbxHacks.setBounds(137, 195, 129, 23);
		contentPane.add(chckbxHacks);
		widthSpinner = new JSpinner();
		widthSpinner.setModel(new SpinnerNumberModel(DEFAULT_WIDTH, MINIMUM_RES, MAXIMUM_RES, 1));
		widthSpinner.setBounds(137, 39, 110, 20);
		contentPane.add(widthSpinner);
		heightSpinner = new JSpinner();
		heightSpinner.setModel(new SpinnerNumberModel(DEFAULT_HEIGHT, MINIMUM_RES, MAXIMUM_RES, 1));
		heightSpinner.setBounds(253, 39, 110, 20);
		contentPane.add(heightSpinner);
		presetBox = new JComboBox<ResolutionPreset>();
		presetBox.setModel(new DefaultComboBoxModel<ResolutionPreset>(ResolutionPreset.values()));
		presetBox.setBounds(137, 3, 226, 24);
		contentPane.add(presetBox);
		chckbxFullscreen = new JCheckBox("Fullscreen");
		chckbxFullscreen.setBounds(12, 37, 117, 23);
		contentPane.add(chckbxFullscreen);
		txtName = new JTextField();
		txtName.setText(Meta.getRandomName());
		txtName.setBounds(137, 66, 226, 19);
		contentPane.add(txtName);
		txtName.setColumns(10);
		portSpinner = new JSpinner();
		portSpinner.setModel(new SpinnerNumberModel(1234, 0, 65535, 1));
		portSpinner.setBounds(278, 97, 85, 20);
		contentPane.add(portSpinner);
		txtLocalhost = new JTextField();
		txtLocalhost.setText("localhost");
		txtLocalhost.setBounds(137, 97, 129, 19);
		contentPane.add(txtLocalhost);
		txtLocalhost.setColumns(10);
		chckbxFollowPlayer = new JCheckBox("Follow player");
		chckbxFollowPlayer.setBounds(12, 125, 129, 23);
		contentPane.add(chckbxFollowPlayer);
		pingSpinner = new JSpinner();
		pingSpinner.setBounds(12, 156, 96, 20);
		pingSpinner.setModel(new SpinnerNumberModel(0, 0, 1000, 50));
		contentPane.add(pingSpinner);
		
		ActionListener resUpdate = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				updateResolutionEntry();
			}
		};
		ChangeListener setRes = new ChangeListener() {
			@Override public void stateChanged(ChangeEvent e) {
				updateResolutionEntry();
			}
		};
		presetBox.addActionListener(resUpdate);
		chckbxFullscreen.addActionListener(resUpdate);
		widthSpinner.addChangeListener(setRes);
		heightSpinner.addChangeListener(setRes);
		Launcher gui = this;
		btnLaunch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveConfig(CONFIG_FILE_NAME);
				try {
					setVisible(false);
					int exit = exec(FightingGameClient.class, getLaunchArgs());
					if(exit == FightingGameClient.NORMAL_EXITCODE)
						System.exit(0);
					gui.setVisible(true); 
					if(exit == FightingGameClient.CONNECTION_ERROR_EXITCODE)
						JOptionPane.showMessageDialog(gui, "Can't connect to server", "Error", JOptionPane.ERROR_MESSAGE);
					if(exit == FightingGameClient.DISCONNECT_EXITCODE)
						JOptionPane.showMessageDialog(gui, "Disconnected from server, reason unknown", "Error", JOptionPane.INFORMATION_MESSAGE);
					if(exit == FightingGameClient.FATAL_ERROR_EXITCODE)
						JOptionPane.showMessageDialog(gui, "Unknown fatal error, wow this is a helpful dialog!", "Error", JOptionPane.ERROR_MESSAGE);
					if(exit == FightingGameClient.PLAYER_ERROR_EXITCODE)
						JOptionPane.showMessageDialog(gui, "Couldn't get localPlayer from the server?!", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(gui, "Launcher had ioerror: \n" + ioe, 
							"Launch error", JOptionPane.ERROR_MESSAGE);
					ioe.printStackTrace();
				} catch (InterruptedException ie) {
					JOptionPane.showMessageDialog(gui, "Launcher was interrupted: \n" + ie, 
							"Launch error", JOptionPane.ERROR_MESSAGE);
					ie.printStackTrace();
				}
			}
		});
		updateResolutionEntry();
	}
	public String[] getLaunchArgs() {
		String hax = "", full = "", follow = "";
		if(chckbxFullscreen.isSelected()) full = "--full-screen";
		if(chckbxHacks.isSelected()) hax = "--hax";
		if(chckbxFollowPlayer.isSelected()) follow = "--follow";
		int w = getResolutionWidth(), h = getResolutionHeight();
		Object obj = presetBox.getSelectedItem();
		ResolutionPreset r = ResolutionPreset.Custom;
		if(obj instanceof ResolutionPreset)
			r = (ResolutionPreset) obj;
		if(r != ResolutionPreset.Custom) {
			w = r.w;
			h = r.h;
		}
		return new String[] { "launcher",
				"--width", String.valueOf(w),
				"--height", String.valueOf(h),
				"--host", String.valueOf(txtLocalhost.getText()),
				"--port", String.valueOf((int)portSpinner.getValue()),
				"--name", String.valueOf(txtName.getText()),
				"--ping", String.valueOf(pingSpinner.getValue()),
				full, hax, follow, "--dialogs"
			};
	}
	private void updateResolutionEntry() {
		Object obj = presetBox.getSelectedItem();
		if(obj instanceof ResolutionPreset) {
			ResolutionPreset r = (ResolutionPreset) obj;
			if(r == ResolutionPreset.Custom) {
				widthSpinner.setEnabled(true);
				heightSpinner.setEnabled(true);
				presetBox.setEnabled(true);
			}else {
				widthSpinner.setEnabled(false);
				heightSpinner.setEnabled(false);
				presetBox.setEnabled(true);
			}
		}
		else JOptionPane.showMessageDialog(this, "WTF? presetBox.getSelectedItem() isn't instance of ResolutionPreset?", "WTF?", JOptionPane.ERROR_MESSAGE);
		if(chckbxFullscreen.isSelected()) {
			widthSpinner.setEnabled(false);
			heightSpinner.setEnabled(false);
			presetBox.setEnabled(false);
		}
	}
	public int getResolutionWidth() {
		return (int)widthSpinner.getValue();
	}
	public int getResolutionHeight() {
		return (int)heightSpinner.getValue();
	}
	public void loadConfig(String fn) {
		try{
			File f = new File(fn);
			if(!f.exists())
				f.createNewFile();
			FileInputStream fis = new FileInputStream(f);
			props.load(fis);
			fis.close();
			boolean fullscreen = Boolean.parseBoolean(props.getProperty(PROPERTY_KEY_FULLSCREEN, "false"));
			int width = Integer.parseInt(props.getProperty(PROPERTY_KEY_WIDTH, "500"));
			int height = Integer.parseInt(props.getProperty(PROPERTY_KEY_HEIGHT, "500"));
			String name = props.getProperty(PROPERTY_KEY_NAME, Meta.getRandomName());
			boolean hax = Boolean.parseBoolean(props.getProperty(PROPERTY_KEY_HACKS, "true"));
			String host = props.getProperty(PROPERTY_KEY_HOST, "localhost");
			int port = Integer.parseInt(props.getProperty(PROPERTY_KEY_PORT, "1234"));
			int pre = Integer.parseInt(props.getProperty(PROPERTY_KEY_RES_PRESET, "0"));
			int ping = Integer.parseInt(props.getProperty(PROPERTY_KEY_PING, "0"));
			boolean follow = Boolean.parseBoolean(props.getProperty(PROPERTY_KEY_FOLLOW, "true"));
			portSpinner.setValue(port);
			txtLocalhost.setText(host);
			chckbxHacks.setSelected(hax);
			chckbxFullscreen.setSelected(fullscreen);
			widthSpinner.setValue(width);
			heightSpinner.setValue(height);
			txtName.setText(name);
			presetBox.setSelectedIndex(pre);
			pingSpinner.setValue(ping);
			chckbxFollowPlayer.setSelected(follow);
			updateResolutionEntry();
		} catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Error loading last config\n" + e, "Error!", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void saveConfig(String fn) {
		try{
			props.setProperty(PROPERTY_KEY_FULLSCREEN, String.valueOf(chckbxFullscreen.isSelected()));
			props.setProperty(PROPERTY_KEY_HACKS, String.valueOf(chckbxHacks.isSelected()));
			props.setProperty(PROPERTY_KEY_FOLLOW, String.valueOf(chckbxFollowPlayer.isSelected()));
			props.setProperty(PROPERTY_KEY_HEIGHT, String.valueOf(heightSpinner.getValue()));
			props.setProperty(PROPERTY_KEY_WIDTH, String.valueOf(widthSpinner.getValue()));
			props.setProperty(PROPERTY_KEY_RES_PRESET, String.valueOf(presetBox.getSelectedIndex()));
			props.setProperty(PROPERTY_KEY_NAME, String.valueOf(txtName.getText()));
			props.setProperty(PROPERTY_KEY_HOST, String.valueOf(txtLocalhost.getText()));
			props.setProperty(PROPERTY_KEY_PORT, String.valueOf(portSpinner.getValue()));
			props.setProperty(PROPERTY_KEY_PING, String.valueOf(pingSpinner.getValue()));
			FileOutputStream fos = new FileOutputStream(fn);
			props.store(fos, "For F.G. laucher " + Meta.VERSION + " (" + Meta.VERSION_ID + ")");
			fos.close();
		} catch(Exception e) {
			JOptionPane.showMessageDialog(this, "Error saving config\n" + e, "Error!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static int exec(Class<?> klass, String[] args) throws IOException, InterruptedException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = klass.getCanonicalName();
		
		String argGrp = "";
		for(String a : args)
			argGrp += a;
		ProcessBuilder pb = new ProcessBuilder(javaBin, "-cp", classpath, className, argGrp);
		pb.inheritIO();
		Process p = pb.start();
		p.waitFor();
		return p.exitValue();
	}
}
