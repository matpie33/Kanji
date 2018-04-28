package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.ListPropertiesNames;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.InsertWordController;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;

public class InsertWordPanel<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private InsertWordController<Word> controller;
	private Word word;
	private MyList<Word> wordsList;
	private Color labelsColor;
	private ListRowData<Word> listRowData;

	public InsertWordPanel(MyList<Word> list,
			ApplicationWindow applicationWindow) {
		wordsList = list;
		controller = new InsertWordController(list,
				applicationWindow.getApplicationController(), this);

	}

	@Override
	public void setParentDialog(DialogWindow parentDialog) {
		super.setParentDialog(parentDialog);
		controller.setParentDialog(parentDialog);
		initializeOneTimeOnlyElements();
	}

	private void initializeOneTimeOnlyElements() {
		wordsList.getListRowCreator().addValidationListener(controller);
		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate();
		setNavigationButtons(cancel, approve);
	}

	@Override
	public void createElements() {
		initializeWord();
		initializeAddWordPanel();
	}

	private void initializeWord() {
		word = wordsList.getWordInitializer().initializeElement();
	}

	private void initializeAddWordPanel() {
		labelsColor = Color.WHITE;

		listRowData = wordsList.getListRowCreator().createListRow(word,
				CommonListElements.forSingleRowOnly(labelsColor), InputGoal.ADD);
		MainPanel addWordPanel = listRowData.getRowPanel();
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, addWordPanel.getPanel())
				.useAllExtraVerticalSpace());
	}

	public void reinitializePanel() {
		mainPanel.clear();
		createPanel();
		mainPanel.updateView();
	}

	private AbstractButton createButtonValidate() {
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionValidateFocusedElement(),
				ButtonsNames.ADD, HotkeysDescriptions.ADD_WORD);
	}

	public Word getWord() {
		return word;
	}

	public Map<JTextComponent, ListElementPropertyManager<?, Word>> getInputsWithManagers() {
		return listRowData.getRowPropertiesData()
				.get(ListPropertiesNames.JAPANESE_WORD_WRITINGS)
				.getTextFieldsWithPropertyManagers();
	}
}
