package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Titles;
import com.kanji.panelsAndControllers.controllers.RepeatingWordsController;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;

public class RepeatingWordsPanel extends AbstractPanelWithHotkeysInfo {

	private AbstractButton showKanjiOrRecognizeWord;
	private AbstractButton notRecognizedWord;
	private MainPanel repeatingPanel;
	private final Color repeatingBackgroundColor = Color.white;
	private AbstractButton showPreviousWord;
	private JLabel time;
	private MainPanel centerPanel;
	private JLabel remainingLabel;
	private JTextComponent wordTextArea;
	private AbstractButton pauseOrResume;
	private RepeatingWordsController repeatingWordsController;
	private JPanel wordInformationPanel;
	private final static String RECOGNIZING_WORD_PANEL_NAME = "Recognizing word";
	private final static String WORD_FULL_INFORMATION_PANEL_NAME = "Word full information";

	public RepeatingWordsPanel(RepeatingWordsController controller) {
		centerPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		repeatingPanel = new MainPanel(this.repeatingBackgroundColor);
		repeatingPanel.setBorder(getDefaultBorder());
		this.repeatingWordsController = controller;
		initializeWordInformationPanel();
	}

	private void initializeWordInformationPanel() {
		wordInformationPanel = new JPanel(new CardLayout());
		wordInformationPanel.setBackground(BasicColors.VERY_LIGHT_BLUE);
		addWordInformationPanelCards(new JPanel(), new JPanel());
	}

	public void addWordInformationPanelCards(JPanel panelForRecognizingWord,
			JPanel wordFullInformationPanel) {
		wordInformationPanel.removeAll();
		wordInformationPanel.add(RECOGNIZING_WORD_PANEL_NAME, panelForRecognizingWord);
		wordInformationPanel.add(WORD_FULL_INFORMATION_PANEL_NAME, wordFullInformationPanel);
	}

	@Override public void createElements() {
		JLabel titleLabel = new JLabel(Titles.REPEATING_WORDS_DIALOG);
		time = new JLabel();
		remainingLabel = new JLabel(repeatingWordsController.createRemainingWordsPrompt());
		AbstractButton returnButton = createReturnButton();
		createRepeatingPanel();
		setButtonsToRecognizingState();
		mainPanel.getPanel().repaint();
		centerPanel.addRows(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTH, titleLabel, time)
						.nextRow(FillType.BOTH, repeatingPanel.getPanel())
						.setBorder(getDefaultBorder())
						.nextRow(FillType.NONE, Anchor.CENTER, remainingLabel, returnButton));
		//TODO in gui maker enable me to put some element in some anchor so that remaining label can be positioned vertically center
		mainPanel.addRows(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, centerPanel.getPanel())
						.useAllExtraVerticalSpace());
	}

	private void setButtonsToRecognizingState() {
		notRecognizedWord.setEnabled(false);
		showPreviousWord.setEnabled(repeatingWordsController.previousWordExists());
	}

	private void createRepeatingPanel() {
		createWordDescriptionTextArea();
		createPauseOrResumeButton();
		createRecognizedWordButton();
		createNotRecognizedWordButton();
		createGoToPreviousWordButton();
		addElementsToRepeatingPanel();
	}

	private void addElementsToRepeatingPanel() {
		AbstractButton[] navigationButtons = new AbstractButton[] { this.pauseOrResume,
				showKanjiOrRecognizeWord, notRecognizedWord, this.showPreviousWord };
		repeatingPanel.addRows(
				SimpleRowBuilder.createRow(FillType.BOTH, wordTextArea).setColor(BasicColors.GREY)
						.nextRow(FillType.NONE, Anchor.CENTER, wordInformationPanel)
						.nextRow(FillType.HORIZONTAL, navigationButtons).fillHorizontallyEqually()
						.disableBorder());
	}

	private void createGoToPreviousWordButton() {
		showPreviousWord = createButtonWithHotkey(KeyEvent.VK_G,
				repeatingWordsController.createActionGoToPreviousWord(), ButtonsNames.PREVIOUS_WORD,
				HotkeysDescriptions.SHOW_PREVIOUS_KANJI);
		showPreviousWord.setFocusable(false);
	}

	private void createWordDescriptionTextArea() {
		wordTextArea = GuiMaker.createTextPane(
				new TextPaneOptions().textAlignment(TextAlignment.CENTERED).text("")
						.enabled(false));
	}

	public void setButtonsToRecognizing() {
		showKanjiOrRecognizeWord.setText(ButtonsNames.RECOGNIZED_WORD);
		notRecognizedWord.setEnabled(true);
		showPreviousWord.setEnabled(repeatingWordsController.previousWordExists());
	}

	private void createPauseOrResumeButton() {
		pauseOrResume = createButtonWithHotkey(KeyEvent.VK_P,
				repeatingWordsController.createActionPause(), ButtonsNames.PAUSE,
				HotkeysDescriptions.PAUSE);
	}

	private void createRecognizedWordButton() {
		showKanjiOrRecognizeWord = createButtonWithHotkey(KeyEvent.VK_SPACE,
				repeatingWordsController.createShowFullInformationOrMarkWordAsRecognizedAction(),
				ButtonsNames.SHOW_KANJI,
				HotkeysDescriptions.SHOW_KANJI_OR_SET_KANJI_AS_KNOWN_KANJI);
	}

	private void createNotRecognizedWordButton() {
		notRecognizedWord = createButtonWithHotkey(KeyEvent.VK_A,
				repeatingWordsController.createNotRecognizedWordAction(),
				ButtonsNames.NOT_RECOGNIZED, HotkeysDescriptions.SET_KANJI_AS_PROBLEMATIC);
	}

	public void setElementsToRecognizingState() {
		showKanjiOrRecognizeWord.setText(ButtonsNames.SHOW_KANJI);
		setButtonsToRecognizingState();
	}

	public void updateRemainingWordsText(String remainingKanjisPrompt) {
		remainingLabel.setText(remainingKanjisPrompt);
	}

	private AbstractButton createReturnButton() {
		return createButtonWithHotkey(KeyModifiers.ALT, KeyEvent.VK_H,
				repeatingWordsController.createActionExit(), ButtonsNames.GO_BACK,
				HotkeysDescriptions.RETURN_FROM_LEARNING);
	}

	public void updateTime(String timePassed) {
		time.setText(Labels.TIME_LABEL + timePassed);
	}

	public void showWord(String word) {
		wordTextArea.setText(word);
	}

	public void showCardForRecognizingWord() {
		showPanel(RECOGNIZING_WORD_PANEL_NAME);
	}

	public void showCardWithFullInformationAboutWord() {
		showPanel(WORD_FULL_INFORMATION_PANEL_NAME);
	}

	private void showPanel(String name) {
		((CardLayout) wordInformationPanel.getLayout()).show(wordInformationPanel, name);
	}

	public void toggleGoToPreviousWordButton() {
		showPreviousWord.setEnabled(!showPreviousWord.isEnabled());
	}

}
