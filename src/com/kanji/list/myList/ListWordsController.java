package com.kanji.list.myList;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.constants.strings.Prompts;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.model.ListRow;

public class ListWordsController<Word extends ListElement> {
	private static final long serialVersionUID = -3144332338336535803L;
	private List<ListRow<Word>> wordsList;
	private ListPanelMaker<Word> rowCreator;
	private ApplicationController applicationController;

	public ListWordsController(MyList list, boolean enableWordAdding, ListRowMaker<Word> listRowMaker,
			String title, ApplicationController applicationController) {
		this.applicationController = applicationController;
		wordsList = new ArrayList<>();
		rowCreator = new ListPanelMaker<>(list, enableWordAdding, applicationController, listRowMaker, this);
		rowCreator.createPanel();
		this.rowCreator.setTitle(title);
	}

	public boolean add(Word r) {
		if (!isWordDefined(r)) {
			wordsList.add(rowCreator.addRow(r));
			return true;
		}
		return false;
	}


	public void remove(Word word) {
		ListRow<Word> listRow = findListRowContainingWord(word);
		int rowNumber = rowCreator.removeRow(listRow.getPanel());
		updateRowNumbers(rowNumber);
		wordsList.remove(listRow);
	}

	private void updateRowNumbers(int startingIndex) {
		for (int i = startingIndex; i < wordsList.size(); i++) {
			JLabel label = wordsList.get(i).getIndexLabel();
			label.setText(rowCreator.createTextForRowNumber(i));
		}

	}

	private ListRow<Word> findListRowContainingWord(Word r) {
		for (int i = 0; i < wordsList.size(); i++) {
			Word word = wordsList.get(i).getWord();
			if (word.equals(r)) {
				return wordsList.get(i);
			}
		}
		return null;
	}

	public List<Word> getWords() {
		List<Word> words = new ArrayList<>();
		for (ListRow<Word> listRow : wordsList) {
			words.add(listRow.getWord());
		}
		return words;
	}

	public int getNumberOfWords() {
		return wordsList.size();
	}

	public Word getWordInRow(int rowNumber1Based) {
		return wordsList.get(rowNumber1Based).getWord();
	}

	public void highlightRowAndScroll(int rowNumber, boolean clearLastHighlightedWord) {
		wordsList.get(rowNumber).setHighlighted(true);
		rowCreator.highlightRowAndScroll(rowNumber, clearLastHighlightedWord);
	}

	public int getHighlightedRowNumber() {
		return rowCreator.getHighlightedRowNumber();
	}

	public void scrollToBottom() {
		rowCreator.scrollToBottom();
	}

	public JPanel getPanel() {
		return rowCreator.getPanel();
	}

	public void clear() {
		wordsList.clear();
		rowCreator.clear();
	}

	public boolean isWordDefined (Word word){
		for (ListRow<Word> listRow : wordsList) {
			if (listRow.getWord().isSameAs(word)){
				return true;
			}
		}
		return false;
	}

	public AbstractAction createDeleteRowAction(Word word) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String rowSpecificPrompt = "";
				if (word instanceof KanjiInformation) {
					rowSpecificPrompt = Prompts.KANJI_ROW;
				}
				if (word instanceof RepeatingInformation) {
					rowSpecificPrompt = Prompts.REPEATING_ELEMENT;
				}

				if (!applicationController.showConfirmDialog(
						String.format(Prompts.DELETE_ELEMENT, rowSpecificPrompt))) {
					return;
				}
				remove(word);
				applicationController.saveProject();
			}
		};
	}

	public List<Word> getWordsByHighlight (boolean highlighted){
		List <Word> highlightedWords = new ArrayList<>();
		for (ListRow<Word> word: wordsList){
			if (word.isHighlighted() == highlighted){
				highlightedWords.add(word.getWord());
			}
		}
		return highlightedWords;
	}

	public void scrollToTop (){
		rowCreator.scrollToTop();
	}

}
