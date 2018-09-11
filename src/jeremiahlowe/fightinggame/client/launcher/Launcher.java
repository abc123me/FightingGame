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

import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;

public class Launcher extends JFrame {
	public static final int MINIMUM_RES = 50;
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 500;
	public static final int MAXIMUM_RES = 100000;
	
	private JPanel contentPane;
	private JCheckBox chckbxHacks;
	private JSpinner widthSpinner;
	private JSpinner heightSpinner;
	private JComboBox<ResolutionPreset> presetBox;
	private JCheckBox chckbxFullscreenplaysBest;
	
	public static void main(String[] args) {
		Launcher frame = new Launcher();
		frame.setVisible(true);
	}
	public Launcher() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("FightingGame Launcher (" + Meta.VERSION + " - " + Meta.VERSION_ID + ")");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		Launcher gui = this;
		
		JLabel lblImUsingAn = new JLabel("I'm using an absolute layout because im lazy");
		lblImUsingAn.setBounds(12, 231, 424, 15);
		contentPane.add(lblImUsingAn);
		JLabel lblIfYouHave = new JLabel("If you have a 4K display, fuck you");
		lblIfYouHave.setBounds(12, 246, 424, 15);
		contentPane.add(lblIfYouHave);
		JLabel lblResolution = new JLabel("Resolution:");
		lblResolution.setBounds(12, 12, 205, 15);
		contentPane.add(lblResolution);
		
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
		chckbxFullscreenplaysBest = new JCheckBox("Fullscreen");
		chckbxFullscreenplaysBest.setBounds(12, 37, 117, 23);
		contentPane.add(chckbxFullscreenplaysBest);
		updateResolutionEntry(presetBox, widthSpinner, heightSpinner);
		
		ActionListener resUpdate = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				updateResolutionEntry(presetBox, widthSpinner, heightSpinner);
			}
		};
		ChangeListener setRes = new ChangeListener() {
			@Override public void stateChanged(ChangeEvent e) {
				resUpdate.actionPerformed(null);
			}
		};
		presetBox.addActionListener(resUpdate);
		widthSpinner.addChangeListener(setRes);
		heightSpinner.addChangeListener(setRes);
		chckbxFullscreenplaysBest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(chckbxFullscreenplaysBest.isSelected()) {
					JOptionPane.showMessageDialog(gui, "Plays best on windowed", "Warning", JOptionPane.WARNING_MESSAGE);
					widthSpinner.setEnabled(false);
					heightSpinner.setEnabled(false);
					presetBox.setEnabled(false);
				}
				else updateResolutionEntry(presetBox, widthSpinner, heightSpinner);
			}
		});
		btnLaunch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String hax = "", full = "";
				if(isFullscreen())
					full = "-F";
				if(haxEnabled())
					hax = "-H";
				FightingGameClient.main(new String[] { "launcher",
					"-w", String.valueOf(getResolutionWidth()),
					"-h", String.valueOf(getResolutionHeight()),
					full, hax
				});
			}
		});
	}
	private void updateResolutionEntry(JComboBox<ResolutionPreset> presetBox, JSpinner widthSpinner, JSpinner heightSpinner) {
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
	}
	public int getResolutionWidth() {
		return (int)widthSpinner.getValue();
	}
	public int getResolutionHeight() {
		return (int)heightSpinner.getValue();
	}
	public boolean isFullscreen() {
		return chckbxFullscreenplaysBest.isSelected();
	}
	public boolean haxEnabled() {
		return chckbxHacks.isSelected();
	}
}
