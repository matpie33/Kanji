package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;

import com.guimaker.enums.FillType;
import com.guimaker.row.SimpleRow;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Prompts;
import com.kanji.constants.SavingStatus;
import com.kanji.utilities.ApplicationController;
import com.kanji.windows.ApplicationWindow;

public class StartingPanel extends AbstractPanelWithHotkeysInfo {

	private ApplicationController applicationController;

	private JSplitPane listsSplitPane;
	private JButton showProblematicKanjis;
	private JLabel saveInfo;
	private ApplicationWindow applicationWindow;
	private JLabel problematicKanjis;
	private boolean problematicKanjiButtonIsVisible;

	public StartingPanel(ApplicationWindow a, ApplicationController maker) {
		applicationWindow = a;
		this.applicationController = maker;
		mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	@Override
	void createElements() {
		// TODO add line separator between panels
		// TODO info panel should have some kind of border or other background
		// color
		createUpperPanel();
		createInformationsPanel();
		// try {
		// createButtonsPanel(addListeners());
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// }
		List<AbstractButton> buttons = null;
		try {
			buttons = addListeners();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mainPanel.addRow(new SimpleRow(FillType.BOTH, listsSplitPane));
		addHotkeysPanelHere();
		mainPanel.addRows(new SimpleRow(FillType.HORIZONTAL, buttons.toArray(new JButton[] {}))
				.nextRow(saveInfo, problematicKanjis));
	}

	private List<AbstractButton> addListeners() throws Exception {
		List<AbstractButton> buttons = new ArrayList<>();
		for (String name : ButtonsNames.BUTTONS_ON_MAIN_PAGE) {
			int keyEvent;
			AbstractAction action;
			String hotkeyDescription;

			switch (name) {
			case ButtonsNames.OPEN:
				hotkeyDescription = HotkeysDescriptions.OPEN_LOAD_KANJI_DIALOG;
				keyEvent = KeyEvent.VK_Q;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.openKanjiProject();
					}
				};
				break;
			case ButtonsNames.ADD:
				hotkeyDescription = HotkeysDescriptions.ADD_WORD;
				keyEvent = KeyEvent.VK_I;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.addWord();
					}
				};
				break;
			case ButtonsNames.SEARCH:
				hotkeyDescription = HotkeysDescriptions.OPEN_SEARCH_WORD_DIALOG;
				keyEvent = KeyEvent.VK_F;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.searchWord();
					}
				};
				break;
			case ButtonsNames.START:
				hotkeyDescription = HotkeysDescriptions.OPEN_START_LEARNING_DIALOG;
				keyEvent = KeyEvent.VK_R;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.startLearning();
					}
				};
				break;
			case ButtonsNames.SAVE:
				hotkeyDescription = HotkeysDescriptions.SAVE_PROJECT;
				keyEvent = KeyEvent.VK_S;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.showSaveDialog();
					}
				};
				break;
			case ButtonsNames.SAVE_LIST:
				hotkeyDescription = HotkeysDescriptions.EXPORT_LIST;
				keyEvent = KeyEvent.VK_T;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.exportList();
					}
				};
				break;
			default:
				throw new Exception("Unsupported button name");
			}
			buttons.add(createButtonWithHotkey(KeyEvent.VK_CONTROL, keyEvent, action, name,
					hotkeyDescription));
		}
		return buttons;
	}

	@SuppressWarnings("rawtypes")
	private void createUpperPanel() {
		listsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				applicationController.getWordsList().getPanel(),
				applicationController.getRepeatsList().getPanel());
	}

	private void createInformationsPanel() {
		saveInfo = new JLabel();
		problematicKanjis = new JLabel();
		showProblematicKanjis = createShowProblematicKanjiButton();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		updateProblematicKanjisAmount(applicationController.getProblematicKanjis().size());
	}

	private JButton createShowProblematicKanjiButton() {
		JButton problematicKanjiButton = new JButton(ButtonsNames.SHOW_PROBLEMATIC_KANJIS);
		problematicKanjiButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationWindow.showProblematicKanjiDialog();
			}
		});
		return problematicKanjiButton;
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.SAVING_STATUS + savingStatus.getStatus());
	}

	public void updateProblematicKanjisAmount(int problematicKanjisNumber) {
		problematicKanjis.setText(Prompts.PROBLEMATIC_KANJI + problematicKanjisNumber);
	}

	public void addButtonIcon() {
		problematicKanjiButtonIsVisible = true;
		mainPanel.addElementsToLastRow(showProblematicKanjis);
	}

	public void removeButtonProblematicsKanji() {
		if (problematicKanjiButtonIsVisible) {
			mainPanel.removeLastElementFromLastRow();
			problematicKanjiButtonIsVisible = false;
		}

	}

}
