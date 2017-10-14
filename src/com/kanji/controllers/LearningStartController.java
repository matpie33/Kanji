package com.kanji.controllers;

import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextField;

import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.Labels;
import com.kanji.model.RangesRow;
import com.kanji.myList.MyList;
import com.kanji.panels.LearningStartPanel;
import com.kanji.range.SetOfRanges;

public class LearningStartController {

	private MyList<RepeatingInformation> repeatsList;
	private SetOfRanges rangesToRepeat;
	private int numberOfWords;
	private int sumOfWords;
	private LearningStartPanel learningStartPanel;
	private Map<Integer, String> errors;
	private List<RangesRow> rangesRows;
	private int problematicLabelRow;
	private ApplicationController applicationController;

	public LearningStartController(MyList<RepeatingInformation> repeatList, int numberOfWords,
			ApplicationController applicationController, LearningStartPanel learningStartPanel) {
		this.applicationController = applicationController;
		this.repeatsList = repeatList;
		this.numberOfWords = numberOfWords;
		this.learningStartPanel = learningStartPanel;
		rangesRows = new ArrayList<>();
		errors = new HashMap<>();
	}

	public void updateNumberOfSelectedKanjiAfterCheckboxToggle(
			boolean isProblematicKanjiCheckboxSelected) {
		rangesToRepeat = addAllRangesToSet();
		int direction;
		if (isProblematicKanjiCheckboxSelected)
			direction = 1;
		else
			direction = -1;
		addOrSubtractProblematicKanjisFromSum(direction);
		learningStartPanel.updateSumOfWordsLabel(getSumOfWords());
		if (isProblematicKanjiCheckboxSelected) {
			int rowNumber = learningStartPanel.showLabelWithProblematicKanjis();
			problematicLabelRow = rowNumber;
		}
		else {
			learningStartPanel.hideLabelWithProblematicKanjis(problematicLabelRow);
			updateRowsNumbers(problematicLabelRow - 1, -1);
		}

	}

	private void addOrSubtractProblematicKanjisFromSum(int direction) {
		Set<Integer> problematics = applicationController.getProblematicKanjis();
		for (Integer i : problematics) {
			if (!rangesToRepeat.isValueInsideThisSet(i)) {
				sumOfWords += direction;
			}
		}
	}

	public int getProblematicKanjiNumber() {
		return applicationController.getProblematicKanjis().size();
	}

	public void addRow(int rowNumber, JTextField from, JTextField to) {
		RangesRow rangesRow = new RangesRow(from, to, rowNumber);
		rangesRows.add(rangesRow);
	}

	public void increaseProblematicLabelRowNumber() {
		problematicLabelRow++;
	}

	public void handleKeyTyped(KeyEvent e, boolean problematicCheckboxSelected) {
		if (!(e.getKeyChar() + "").matches("\\d")) {
			e.consume();
		}
	}

	private void removeError(RangesRow rangesRow) {
		int rowNumber = rangesRow.getTextFieldsRowNumber();
		errors.remove(rowNumber);
		learningStartPanel.removeRowFromPanel(rowNumber + 1);
		updateRowsNumbers(rowNumber, -1);
	}

	private void showError(RangesRow rangesRow, String error) {
		int rowNumber = rangesRow.getTextFieldsRowNumber();
		learningStartPanel.showErrorOnThePanel(error, rowNumber + 1);
		rangesRow.setError(error);
		updateRowsNumbers(rowNumber, 1);
	}

	private RangesRow findRowWithTextFields(JTextField textFieldFrom, JTextField textFieldTo) {
		for (RangesRow row : rangesRows) {
			if (row.gotTextFields(textFieldFrom, textFieldTo)) {
				return row;
			}
		}
		return null;
	}

	public void handleKeyReleased(KeyEvent e, JTextField to, JTextField from,
			boolean problematicCheckboxSelected) {

		if (handleEmptyTextFields(e, to, from, problematicCheckboxSelected)) {
			return;
		}
		processTextFieldsInputs(to, from, problematicCheckboxSelected);
	}

	private void processTextFieldsInputs(JTextField to, JTextField from,
			boolean problematicCheckboxSelected) {
		boolean fromTextFieldWasFocused = from.hasFocus();
		if (from.getText().isEmpty() || to.getText().isEmpty()) {
			return;
		}
		int valueFrom = Integer.parseInt(from.getText());
		int valueTo = Integer.parseInt(to.getText());
		String error = validateRangesInput(valueFrom, valueTo);
		RangesRow rowWithTextFields = findRowWithTextFields(from, to);
		if (error.isEmpty()) {
			rowWithTextFields.setRangeValues(valueFrom, valueTo);
			if (rowWithTextFields.errorNotEmpty()) {
				removeError(rowWithTextFields);
				rowWithTextFields.setError("");
			}
		}
		else {
			rowWithTextFields.setRangeValues(0, 0);
			if (rowWithTextFields.errorNotEmpty() && !rowWithTextFields.getError().equals(error)) {
				removeError(rowWithTextFields);
			}
			if (!rowWithTextFields.getError().equals(error)) {
				showError(rowWithTextFields, error);
				if (fromTextFieldWasFocused) {
					from.requestFocusInWindow();
				}
				else {
					to.requestFocusInWindow();
				}

			}
		}
		updateNumberOfSelectedKanjis(problematicCheckboxSelected);
	}

	public void updateRowsNumbers(int fromRowNumber, int positiveOrNegativeValue) {
		for (RangesRow row : rangesRows) {
			if (row.getTextFieldsRowNumber() > fromRowNumber) {
				row.setRowNumber(row.getTextFieldsRowNumber() + positiveOrNegativeValue);
			}
		}
	}

	private boolean handleEmptyTextFields(KeyEvent e, JTextField to, JTextField from,
			boolean problematicCheckboxSelected) {
		if (from.getText().isEmpty() || to.getText().isEmpty()) {
			if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
				RangesRow rowWithTextFields = findRowWithTextFields(from, to);
				rowWithTextFields.setRangeValues(0, 0);
				updateNumberOfSelectedKanjis(problematicCheckboxSelected);
			}
			return true;
		}

		return false;
	}

	private String validateRangesInput(int rangeStart, int rangeEnd) {
		String error = "";
		if (rangeStart == 0) {
			error = ExceptionsMessages.RANGE_START_MUST_BE_POSITIVE;
		}
		else if (rangeEnd <= rangeStart) {
			error = ExceptionsMessages.RANGE_TO_VALUE_LESS_THAN_RANGE_FROM_VALUE;
		}
		else if (isNumberHigherThanMaximum(rangeStart) || isNumberHigherThanMaximum(rangeEnd)) {
			error = ExceptionsMessages.RANGE_VALUE_HIGHER_THAN_MAXIMUM_KANJI_NUMBER;
			error += " (" + numberOfWords + ").";
		}

		return error;
	}

	private boolean isNumberHigherThanMaximum(int number) {
		return number > numberOfWords;
	}

	private void updateNumberOfSelectedKanjis(boolean problematicCheckboxSelected) {
		recalculateSumOfKanji(problematicCheckboxSelected);
		learningStartPanel.updateSumOfWordsLabel(getSumOfWords());
	}

	public void removeRangeRow(JTextField from, JTextField to,
			boolean problematicCheckboxSelected) {

		RangesRow rowWithTextFields = findRowWithTextFields(from, to);
		int rowWithTextFieldsNumber = rowWithTextFields.getTextFieldsRowNumber();
		boolean wasError = rowWithTextFields.errorNotEmpty();
		learningStartPanel.removeRow(rowWithTextFieldsNumber);
		if (wasError) {
			learningStartPanel.removeRow(rowWithTextFieldsNumber);
		}

		int decreaseBy = -1;
		if (wasError) {
			decreaseBy = -2;
		}
		updateRowsNumbers(rowWithTextFieldsNumber, decreaseBy);
		rangesRows.remove(rowWithTextFields);
		if (getNumberOfRangesRows() == 1) {
			learningStartPanel.changeVisibilityOfDeleteButtonInFirstRow(false);
		}
		recalculateSumOfKanji(problematicCheckboxSelected);
		learningStartPanel.updateSumOfWordsLabel(getSumOfWords());
	}

	private void recalculateSumOfKanji(boolean problematicKanjisSelected) {
		rangesToRepeat = addAllRangesToSet();
		sumOfWords = 0;
		this.sumOfWords += rangesToRepeat.sumRangeInclusive();

		if (problematicKanjisSelected) {
			addOrSubtractProblematicKanjisFromSum(+1);
		}
	}

	private void validateAndStart(boolean problematicCheckboxSelected) {

		rangesToRepeat = addAllRangesToSet();
		if (rangesToRepeat.toString().isEmpty()) {
			makeSureTheresNoInput(problematicCheckboxSelected);
		}
		if (rangesToRepeat.toString().isEmpty() && !problematicCheckboxSelected) {
			learningStartPanel.showErrorDialog(ExceptionsMessages.NO_INPUT_SUPPLIED);
			return;
		}

		addRangesToRepeatsList(problematicCheckboxSelected);
		learningStartPanel.switchToRepeatingPanel();
	}

	private void makeSureTheresNoInput(boolean problematicCheckboxSelected) {
		for (RangesRow range : rangesRows) {
			processTextFieldsInputs(range.getTextFieldTo(), range.getTextFieldFrom(),
					problematicCheckboxSelected);
		}
	}

	private void addRangesToRepeatsList(boolean problematicCheckboxSelected) {
		String repeatingInfo = "";
		if (problematicCheckboxSelected) {
			repeatingInfo = Labels.PROBLEMATIC_KANJI_OPTION;
			if (rangesToRepeat.getRangesAsList().size() > 0) {
				repeatingInfo += ", ";
			}
		}
		repeatingInfo += rangesToRepeat;
		repeatingInfo += ".";
		applicationController.setRepeatingInformation(
				new RepeatingInformation(repeatingInfo, LocalDateTime.now(), false));
		repeatsList.scrollToBottom();
	}

	public void switchPanelAndSetWordsRangesToRepeat(boolean problematicCheckboxSelected) {
		applicationController.initiateWordsLists(rangesToRepeat, problematicCheckboxSelected);
		applicationController.startRepeating();
	}

	private SetOfRanges addAllRangesToSet() {
		SetOfRanges setOfRanges = new SetOfRanges();
		for (RangesRow r : rangesRows) {
			if (!r.getRange().isEmpty()) {
				setOfRanges.addRange(r.getRange());
			}

		}
		return setOfRanges;
	}

	public int getSumOfWords() {
		return sumOfWords;
	}

	private String concatenateErrors() {
		String concatenated = "";
		for (Map.Entry<Integer, String> error : errors.entrySet()) {
			concatenated += "Błąd w wierszu " + error.getKey() + ": " + error.getValue();
			concatenated += "\n\n";
		}
		return concatenated;
	}

	public int getNumberOfRangesRows() {
		return rangesRows.size();
	}

	public boolean gotErrors() {
		boolean gotError = false;
		for (RangesRow r : rangesRows) {
			if (r.errorNotEmpty()) {
				errors.put(r.getTextFieldsRowNumber(), r.getError());
				gotError = true;
			}
		}
		return gotError;
	}

	public void showErrorsOrStart(boolean problematicCheckboxSelected) {
		if (gotErrors()) {
			String errors = concatenateErrors();
			learningStartPanel.showErrorDialog(errors);
		}
		else {
			validateAndStart(problematicCheckboxSelected);
		}
	}

}
