package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.Colors;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Titles;
import com.kanji.panelsAndControllers.controllers.RepeatingWordsController;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;

public class RepeatingWordsPanel extends AbstractPanelWithHotkeysInfo {

	private AbstractButton showWordOrMarkAsRecognizedButton;
	private AbstractButton notRecognizedWordButton;
	private MainPanel repeatingDataPanel;
	private AbstractButton showPreviousWordButton;
	private JLabel timeElapsedLabel;
	private MainPanel rootPanel;
	private JLabel remainingWordsAmountLabel;
	private JTextComponent wordHintTextPane;
	private AbstractButton pauseButton;
	private RepeatingWordsController repeatingWordsController;
	private JLabel titleLabel;
	private AbstractButton returnButton;
	private MainPanel wordDataPanel;

	public RepeatingWordsPanel(RepeatingWordsController controller) {
		rootPanel = new MainPanel(null);
		repeatingDataPanel = new MainPanel(Colors.BACKGROUND_PANEL_COLOR);
		repeatingDataPanel.setRowsBorder(getDefaultBorder());
		this.repeatingWordsController = controller;
		initializeWordDataPanel();
	}

	private void initializeWordDataPanel() {
		wordDataPanel = new MainPanel(null, true);
		wordDataPanel.setRowColor(BasicColors.PURPLE_DARK_2);
		wordDataPanel.setRowsBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED,
						BasicColors.BLUE_NORMAL_1, BasicColors.BLUE_NORMAL_7));
	}

	@Override
	public void createElements() {
		titleLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Titles.REPEATING_WORDS_DIALOG));
		timeElapsedLabel = GuiElementsCreator
				.createLabel(new ComponentOptions());
		remainingWordsAmountLabel = GuiElementsCreator.createLabel(new ComponentOptions());
		returnButton = createReturnButton();
		createRepeatingPanelElements();

		addElementsToPanels();
		mainPanel.getPanel().repaint();
	}

	public AbstractButton getShowWordOrMarkAsRecognizedButton() {
		return showWordOrMarkAsRecognizedButton;
	}

	public AbstractButton getNotRecognizedWordButton() {
		return notRecognizedWordButton;
	}

	public AbstractButton getShowPreviousWordButton() {
		return showPreviousWordButton;
	}

	private void createRepeatingPanelElements() {
		createWordHintTextPane();
		createPauseButton();
		createShowWordOrMarkAsRecognizedButton();
		createNotRecognizedWordButton();
		createShowPreviousWordButton();
	}

	private void addElementsToPanels() {
		repeatingDataPanel.addRows(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, wordHintTextPane)
				.nextRow(FillType.BOTH, Anchor.CENTER, wordDataPanel
						.getPanel())
				.nextRow(FillType.NONE, pauseButton,
						showWordOrMarkAsRecognizedButton,
						notRecognizedWordButton, showPreviousWordButton)
				.fillHorizontallyEqually().disableBorder());

		rootPanel.addRows(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.NORTH, titleLabel,
						timeElapsedLabel)
				.nextRow(FillType.BOTH, repeatingDataPanel.getPanel())
				.setBorder(getDefaultBorder())
				.nextRow(FillType.NONE, Anchor.CENTER,
						remainingWordsAmountLabel,
						returnButton));

		//TODO in gui maker enable me to put some element in some anchor so that remaining label can be positioned vertically center
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, Anchor.CENTER, rootPanel.getPanel())
				.useAllExtraVerticalSpace());
	}

	private void createShowPreviousWordButton() {
		showPreviousWordButton = createButtonWithHotkey(KeyEvent.VK_G,
				repeatingWordsController.createActionGoToPreviousWord(),
				ButtonsNames.SHOW_PREVIOUS_WORD,
				HotkeysDescriptions.SHOW_PREVIOUS_WORD);
		showPreviousWordButton.setFocusable(false);
	}

	private void createWordHintTextPane() {
		wordHintTextPane = GuiElementsCreator.createTextPane(
				new TextPaneOptions().textAlignment(TextAlignment.CENTERED)
						.text("").editable(false).fontSize(30f)
						.backgroundColor(Colors.CONTENT_PANEL_COLOR)
						.font(ApplicationWindow.getKanjiFont()));
	}

	private void createPauseButton() {
		pauseButton = createButtonWithHotkey(KeyEvent.VK_P,
				repeatingWordsController.createActionPause(),
				ButtonsNames.PAUSE, HotkeysDescriptions.PAUSE);
	}

	private void createShowWordOrMarkAsRecognizedButton() {
		showWordOrMarkAsRecognizedButton = createButtonWithHotkey(
				KeyEvent.VK_SPACE, repeatingWordsController
						.createShowFullInformationOrMarkWordAsRecognizedAction(),
				ButtonsNames.SHOW_WORD,
				HotkeysDescriptions.SHOW_WORD_OR_SET_AS_RECOGNIZED);
	}

	private void createNotRecognizedWordButton() {
		notRecognizedWordButton = createButtonWithHotkey(KeyEvent.VK_A,
				repeatingWordsController.createNotRecognizedWordAction(),
				ButtonsNames.NOT_RECOGNIZED_WORD,
				HotkeysDescriptions.SET_WORD_AS_PROBLEMATIC);
	}

	public JLabel getRemainingWordsAmountLabel() {
		return remainingWordsAmountLabel;
	}

	private AbstractButton createReturnButton() {
		return createButtonWithHotkey(KeyModifiers.CONTROL, KeyEvent.VK_E,
				repeatingWordsController.createActionExit(),
				ButtonsNames.GO_BACK, HotkeysDescriptions.RETURN_FROM_LEARNING);
	}

	public JLabel getTimeElapsedLabel() {
		return timeElapsedLabel;
	}

	public JTextComponent getWordHintTextPane() {
		return wordHintTextPane;
	}

	public MainPanel getWordDataPanel() {
		return wordDataPanel;
	}
}
