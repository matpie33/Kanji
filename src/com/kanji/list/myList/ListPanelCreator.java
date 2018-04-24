package com.kanji.list.myList;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.list.listElements.ListElement;
import com.kanji.model.ListRow;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.range.Range;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ListPanelCreator<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private static final Color BACKGROUND_COLOR = BasicColors.VERY_BLUE;
	private ListWordsController<Word> listWordsController;
	private MainPanel rowsPanel;
	private JScrollPane parentScrollPane;
	private final Dimension scrollPanesSize = new Dimension(350, 100);
	private JLabel titleLabel;
	private ListRowCreator<Word> listRow;
	private ApplicationController applicationController;
	private MainPanel rootPanel;
	private boolean enableWordAdding;
	private AbstractButton buttonLoadNextWords;
	private AbstractButton buttonLoadPreviousWords;
	private LoadNextWordsHandler loadNextWordsHandler;
	private LoadPreviousWordsHandler loadPreviousWordsHandler;
	private List<AbstractButton> navigationButtons;
	private JComponent listElementsPanel;
	private boolean isScrollBarInherited;
	private boolean enableWordSearching;
	private boolean showButtonsNextAndPrevious;
	private boolean isSkipTitle;
	private Color labelsColor = Color.WHITE;
	private boolean scrollBarSizeFittingContent;

	public ListPanelCreator(ListConfiguration listConfiguration,
			ApplicationController applicationController,
			ListRowCreator<Word> listRow,
			ListWordsController<Word> controller) {
		this.applicationController = applicationController;
		listWordsController = controller;

		isSkipTitle = listConfiguration.isSkipTitle();
		rowsPanel = new MainPanel(null, true);
		rootPanel = new MainPanel(null);
		titleLabel = GuiElementsCreator.createLabel(new ComponentOptions());
		loadNextWordsHandler = new LoadNextWordsHandler(listWordsController,
				rowsPanel);
		loadPreviousWordsHandler = new LoadPreviousWordsHandler(
				listWordsController, rowsPanel);
		this.listRow = listRow;

		navigationButtons = new ArrayList<>();
		unwrapConfiguration(listConfiguration);

	}

	private void unwrapConfiguration(ListConfiguration listConfiguration) {
		this.enableWordAdding = listConfiguration.isWordAddingEnabled();
		this.isScrollBarInherited = listConfiguration.isScrollBarInherited();
		this.enableWordSearching = listConfiguration.isWordSearchingEnabled();
		showButtonsNextAndPrevious = listConfiguration
				.isShowButtonsLoadNextPreviousWords();
		scrollBarSizeFittingContent = listConfiguration
				.isScrollBarSizeFittingContent();
		addNavigationButtons(
				listConfiguration.getAdditionalNavigationButtons());
	}

	public void inheritScrollPane() {
		isScrollBarInherited = false;
	}

	private void addNavigationButtons(AbstractButton... buttons) {
		for (AbstractButton button : buttons) {
			navigationButtons.add(button);
		}
	}

	public LoadNextWordsHandler getLoadNextWordsHandler() {
		return loadNextWordsHandler;
	}

	public LoadPreviousWordsHandler getLoadPreviousWordsHandler() {
		return loadPreviousWordsHandler;
	}

	private void createAndAddButtonsShowNextAndPrevious() {
		buttonLoadNextWords = createAndAddButtonLoadWords(
				ButtonsNames.SHOW_NEXT_WORDS_ON_LIST);
		buttonLoadPreviousWords = createAndAddButtonLoadWords(
				ButtonsNames.SHOW_PREVIOUS_WORDS_ON_LIST);
		buttonLoadNextWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadNextWordsHandler));
		buttonLoadPreviousWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadPreviousWordsHandler));
		rowsPanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, buttonLoadPreviousWords));
		rowsPanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, buttonLoadNextWords));
		if (!showButtonsNextAndPrevious) {
			buttonLoadPreviousWords.setVisible(false);
			buttonLoadNextWords.setVisible(false);
		}
	}

	private AbstractButton createAndAddButtonLoadWords(String buttonName) {
		AbstractButton button = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(buttonName), null);
		button.setEnabled(false);
		return button;
	}

	public ListRow<Word> addRow(Word word, int rowNumber,
			boolean shouldShowWord, LoadWordsHandler loadWordsHandler,
			boolean forSearchPanel) {
		JLabel rowNumberLabel = new JLabel(createTextForRowNumber(rowNumber));
		AbstractButton remove = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.REMOVE_ROW),
				listWordsController.createDeleteRowAction(word));
		AbstractButton addNewWord = createButtonAddRow(forSearchPanel);
		CommonListElements commonListElements = new CommonListElements(remove,
				rowNumberLabel, addNewWord, labelsColor, false);
		rowNumberLabel.setForeground(labelsColor);
		JComponent row = null;
		if (shouldShowWord) {
			AbstractSimpleRow abstractSimpleRow = SimpleRowBuilder
					.createRow(FillType.HORIZONTAL, Anchor.NORTH,
							listRow.createListRow(word, commonListElements,
									forSearchPanel).getPanel());
			row = loadWordsHandler.showWord(abstractSimpleRow);
		}
		else if (!buttonLoadNextWords.isEnabled()) {
			buttonLoadNextWords.setEnabled(true);
		}
		rowsPanel.updateView();
		return new ListRow<>(word, row, rowNumberLabel);
	}

	private AbstractButton createButtonAddRow(boolean forSearchPanel) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(ButtonsNames.ADD_ROW),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						listWordsController.addNewWord(forSearchPanel);
					}
				});
	}

	private AbstractAction createButtonShowNextOrPreviousWords(
			LoadWordsHandler loadWordsHandler) {

		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int numberOfAddedWords = listWordsController
						.addNextHalfOfMaximumWords(loadWordsHandler);
				if (numberOfAddedWords > 0) {
					removeWordsFromRangeInclusive(loadWordsHandler
							.getRangeOfWordsToRemove(numberOfAddedWords));
				}
				boolean hasMoreWordsToShow = numberOfAddedWords
						== listWordsController.getMaximumWordsToShow() / 2;
				loadWordsHandler
						.enableOrDisableLoadWordsButtons(buttonLoadNextWords,
								buttonLoadPreviousWords, hasMoreWordsToShow);
				rowsPanel.updateView();
			}
		};

	}

	public String createTextForRowNumber(int rowNumber) {
		return "" + rowNumber + ". ";
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	@Override
	public void createElements() {
		createRootPanel();
		createAndAddButtonsShowNextAndPrevious();

		if (!isSkipTitle) {
			rootPanel.addRow(SimpleRowBuilder
					.createRow(FillType.NONE, Anchor.CENTER, titleLabel));
		}
		rootPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, listElementsPanel));
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, rootPanel.getPanel()));

		if (enableWordAdding) {
			navigationButtons.add(createButtonAddWord());
		}
		if (enableWordSearching) {
			navigationButtons.add(createButtonFindWord());
		}

		setNavigationButtons(
				navigationButtons.toArray(new AbstractButton[] {}));
	}

	private void createRootPanel() {
		if (!isScrollBarInherited) {
			parentScrollPane = GuiElementsCreator.createScrollPane(
					new ScrollPaneOptions().opaque(false)
							.componentToWrap(rowsPanel.getPanel()));
			if (!scrollBarSizeFittingContent) {
				parentScrollPane.setPreferredSize(scrollPanesSize);
			}
			listElementsPanel = parentScrollPane;
		}
		else {
			listElementsPanel = rowsPanel.getPanel();
		}

	}

	public void removeWordsFromRangeInclusive(Range range) {
		rowsPanel.removeRowsInclusive(range.getRangeStart(),
				range.getRangeEnd());
	}

	private AbstractButton createButtonAddWord() {
		String name = ButtonsNames.ADD;
		String hotkeyDescription = HotkeysDescriptions.ADD_WORD;
		int keyEvent = KeyEvent.VK_I;
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationController.showInsertWordDialog();
			}
		};
		//TODO add in my list a parameter with hotkeys mapping for add/search panels
		return createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent, action,
				name, hotkeyDescription);

	}

	private AbstractButton createButtonFindWord() {
		String name = ButtonsNames.SEARCH;
		//TODO differentiate between my list - kanji vs repeating list vs problematic kanjis?
		String hotkeyDescription = HotkeysDescriptions.OPEN_SEARCH_WORD_DIALOG;
		int keyEvent = KeyEvent.VK_F;
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationController.showSearchWordDialog();
			}
		};
		return createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent, action,
				name, hotkeyDescription);
	}

	public void clearHighlightedRow(JComponent row) {
		rowsPanel.clearPanelColor(row);
	}

	public void highlightRowAndScroll(JComponent row) {
		int rowNumber = rowsPanel.getIndexOfPanel(row);
		changePanelColor(rowNumber, BasicColors.WATER_BLUE);
		scrollTo(rowsPanel.getRows().get(rowNumber));
		this.rowsPanel.getPanel().repaint();
	}

	private void changePanelColor(int rowNumber, Color color) {
		rowsPanel.setPanelColor(rowNumber, color);
	}

	public void scrollTo(JComponent panel) {
		SwingUtilities.invokeLater(() -> {
			int r = panel.getY();
			this.parentScrollPane.getViewport()
					.setViewPosition(new Point(0, r));
		});
	}

	public void scrollToBottom() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO swing utilities
				JScrollBar scrollBar = parentScrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});

	}

	public int removeRow(JComponent panel) {
		int rowNumber = rowsPanel.getIndexOfPanel(panel);
		rowsPanel.removeRow(rowNumber);
		return rowNumber;
	}

	public void clear() {
		rowsPanel.clear();
		createAndAddButtonsShowNextAndPrevious();
	}

	public void scrollToTop() {
		SwingUtilities.invokeLater(
				() -> parentScrollPane.getVerticalScrollBar().setValue(0));
	}

	public int getNumberOfListRows() {
		return rowsPanel.getNumberOfRows() - 2;
	}

	public void enableButtonShowPreviousWords() {
		buttonLoadPreviousWords.setEnabled(true);
	}
}
