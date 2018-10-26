package com.kanji.panelsAndControllers.panels;

import com.guimaker.application.DialogWindow;
import com.guimaker.colors.BasicColors;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.list.myList.MyList;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.JapaneseApplicationButtonsNames;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.StartingController;
import com.kanji.utilities.CommonGuiElementsCreator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;

import static com.kanji.constants.strings.JapaneseApplicationButtonsNames.*;

public class StartingPanel extends AbstractPanelWithHotkeysInfo {

	private static final String UNIQUE_NAME = "Starting panel";
	private final String KANJI_TAB_TITLE = "Powtórki kanji";
	private final String JAPANESE_TAB_TITLE = "Powtórki słówek";
	private JTabbedPane tabbedPane;
	private JLabel problematicKanjisLabel;
	private JLabel saveInfo;
	private AbstractButton showProblematicWordsButton;
	private ApplicationController applicationController;
	private Map<String, JSplitPane> tabTitleToContentMap = new LinkedHashMap<>();
	private StartingController startingController;
	private Map<String, String> tabTitleToWordStateControllerMap = new HashMap<>();
	private static final String[] BUTTONS_ON_MAIN_PAGE = { START, LOAD_PROJECT,
			LOAD_LIST, SAVE, SAVE_LIST, SHOW_PROBLEMATIC_KANJIS };
	private JSplitPane kanjiListsSplitPane;
	private JSplitPane japaneseWordsListsSplitPane;

	public StartingPanel(StartingController startingController,
			ApplicationController applicationController) {
		tabbedPane = new JTabbedPane();
		this.startingController = startingController;
		this.applicationController = applicationController;
	}

	private JSplitPane createWordsAndRepeatingDataListSplitPane(
			MyList wordsList, MyList<RepeatingData> repeatingList) {
		return CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						wordsList.getPanel(), repeatingList.getPanel(), 0.8);
	}

	@Override
	public void setParentDialog(DialogWindow dialog) {
		super.setParentDialog(dialog);
	}

	@Override
	public void createElements() {

		createTabPane();
		createInformationsPanel();
		List<AbstractButton> navigationButtons = createNavigationButtons();

		MainPanel bottomPanel = new MainPanel();
		bottomPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				navigationButtons.toArray(new AbstractButton[] {}))
				.setNotOpaque().disableBorder()
				.nextRow(saveInfo, problematicKanjisLabel));

		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, tabbedPane));
		addHotkeysPanelHere();
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, bottomPanel.getPanel()));
		startingController.setInitialStartingPanelState();
	}

	private void createTabPane() {
		japaneseWordsListsSplitPane = createWordsAndRepeatingDataListSplitPane(
				applicationController.getJapaneseWords(),
				applicationController.getJapaneseWordsRepeatingDates());
		kanjiListsSplitPane = createWordsAndRepeatingDataListSplitPane(
				applicationController.getKanjiList(),
				applicationController.getKanjiRepeatingDates());

		tabTitleToWordStateControllerMap
				.put(KANJI_TAB_TITLE, Kanji.MEANINGFUL_NAME);
		tabTitleToWordStateControllerMap
				.put(JAPANESE_TAB_TITLE, JapaneseWord.MEANINGFUL_NAME);

		tabTitleToContentMap.put(KANJI_TAB_TITLE,
				kanjiListsSplitPane);
		tabTitleToContentMap.put(JAPANESE_TAB_TITLE,
				japaneseWordsListsSplitPane);

		for (Map.Entry<String, JSplitPane> listAndTabLabel : tabTitleToContentMap
				.entrySet()) {
			tabbedPane.addTab(listAndTabLabel.getKey(),
					listAndTabLabel.getValue());
		}
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			tabbedPane.setBackgroundAt(i, BasicColors.BLUE_NORMAL_1);
		}

		tabbedPane.addChangeListener(
				startingController.createTabChangedListener());
		tabbedPane.setSelectedIndex(0);
		addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_W,
				startingController.createActionSwitchTab(tabbedPane),
				getPanel(), HotkeysDescriptions.SWITCH_WORD_TAB);
	}

	private List<AbstractButton> createNavigationButtons() {
		List<AbstractButton> buttons = new ArrayList<>();
		for (String name : BUTTONS_ON_MAIN_PAGE) {
			int keyEvent;
			AbstractAction action;
			String hotkeyDescription;

			switch (name) {
			case JapaneseApplicationButtonsNames.LOAD_LIST:
				keyEvent = KeyEvent.VK_D;
				hotkeyDescription = HotkeysDescriptions.LOAD_LISTS_FROM_TEXT_FILE;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.loadWordsFromTextFiles();
					}
				};
				break;
			case JapaneseApplicationButtonsNames.LOAD_PROJECT:
				hotkeyDescription = HotkeysDescriptions.OPEN_LOAD_KANJI_DIALOG;
				keyEvent = KeyEvent.VK_Q;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.openKanjiProject();
					}
				};
				break;
			case JapaneseApplicationButtonsNames.START:
				hotkeyDescription = HotkeysDescriptions.OPEN_START_LEARNING_DIALOG;
				keyEvent = KeyEvent.VK_R;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.showLearningStartDialog();
					}
				};
				break;
			case JapaneseApplicationButtonsNames.SAVE:
				hotkeyDescription = HotkeysDescriptions.SAVE_PROJECT;
				keyEvent = KeyEvent.VK_S;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.showSaveDialog();
					}
				};
				break;
			case JapaneseApplicationButtonsNames.SAVE_LIST:
				hotkeyDescription = HotkeysDescriptions.EXPORT_LIST;
				keyEvent = KeyEvent.VK_T;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.saveList();
					}
				};
				break;
			case JapaneseApplicationButtonsNames.SHOW_PROBLEMATIC_KANJIS:
				hotkeyDescription = HotkeysDescriptions.REVIEW_PROBLEMATIC_KANJIS;
				keyEvent = KeyEvent.VK_P;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController
								.showProblematicWordsDialogForCurrentList();
					}
				};
				break;
			default:
				throw new RuntimeException("Unsupported button name: " + name);
			}
			AbstractButton button = createButtonWithHotkey(KeyModifiers.CONTROL,
					keyEvent, action, name, hotkeyDescription);
			if (name.equals(
					JapaneseApplicationButtonsNames.SHOW_PROBLEMATIC_KANJIS)) {
				showProblematicWordsButton = button;
				showProblematicWordsButton.setEnabled(false);
			}
			buttons.add(button);
		}
		return buttons;
	}

	private void createInformationsPanel() {
		saveInfo = GuiElementsCreator.createLabel(new ComponentOptions());
		problematicKanjisLabel = GuiElementsCreator
				.createLabel(new ComponentOptions());
		showProblematicWordsButton = GuiElementsCreator
				.createButtonLikeComponent(new ButtonOptions(ButtonType.BUTTON)
						.text(JapaneseApplicationButtonsNames.SHOW_PROBLEMATIC_KANJIS));
	}

	public AbstractButton getShowProblematicWordsButton() {
		return showProblematicWordsButton;
	}

	public void refreshAllTabs() {
		tabbedPane.repaint();
		tabbedPane.revalidate();
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}

	public String getActiveWordListControllerName() {
		return tabTitleToWordStateControllerMap
				.get(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()));
	}

	public JLabel getSaveInfoLabel() {
		return saveInfo;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public JLabel getProblematicKanjisLabel() {
		return problematicKanjisLabel;
	}

	public JSplitPane getKanjiListsSplitPane() {
		return kanjiListsSplitPane;
	}

	public JSplitPane getJapaneseWordsListsSplitPane() {
		return japaneseWordsListsSplitPane;
	}
}

