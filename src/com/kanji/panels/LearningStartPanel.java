package com.kanji.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.guimaker.utilities.CommonActionsMaker;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Labels;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.controllers.LearningStartController;
import com.kanji.myList.MyList;
import com.kanji.utilities.ApplicationController;
import com.kanji.utilities.LimitDocumentFilter;

public class LearningStartPanel extends AbstractPanelWithHotkeysInfo {

	private JScrollPane scrollPane;
	private JTextField sumRangeField;
	private JCheckBox problematicCheckbox;
	private LearningStartController controller;
	private MainPanel rangesPanel;

	public LearningStartPanel(ApplicationController applicationController, int numberOfWords,
			MyList<RepeatingInformation> list) {
		controller = new LearningStartController(list, numberOfWords, applicationController, this);
	}

	@Override
	void createElements() {

		JTextArea prompt = createPrompt();
		problematicCheckbox = createProblematicKanjiCheckbox();
		rangesPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE, true);
		rangesPanel.addRow(
				new SimpleRow(FillType.NONE, Anchor.CENTER, new JLabel(Titles.KANJI_RANGES)));
		scrollPane = createRangesPanelScrollPane();
		addRowToRangesPanel();

		JTextField problematicKanjis = createProblematicRangeField(Prompts.PROBLEMATIC_KANJI);
		JButton newRow = createButtonAddRow(ButtonsNames.ADD_ROW, rangesPanel);
		sumRangeField = GuiMaker.createTextField(1, Prompts.RANGE_SUM);
		AbstractButton cancel = createButtonWithHotkey(KeyEvent.VK_ESCAPE,
				CommonActionsMaker.createDisposeAction(parentDialog.getContainer()),
				ButtonsNames.CANCEL, HotkeysDescriptions.CLOSE_WINDOW);
		AbstractButton approve = createButtonStartLearning(ButtonsNames.APPROVE,
				rangesPanel.getPanel());

		mainPanel.addRows(new SimpleRow(FillType.HORIZONTAL, prompt).nextRow(problematicCheckbox)
				.nextRow(problematicKanjis).nextRow(FillType.BOTH, scrollPane)
				.nextRow(FillType.HORIZONTAL, newRow, sumRangeField)
				.fillHorizontallySomeElements(sumRangeField));
		addHotkeysPanelHere();
		mainPanel.addRow(new SimpleRow(FillType.NONE, Anchor.EAST, cancel, approve));
	}

	private JScrollPane createRangesPanelScrollPane() {
		Border b = BorderFactory.createLineBorder(BasicColors.VERY_BLUE);
		return GuiMaker.createScrollPane(BasicColors.DARK_BLUE, b, rangesPanel.getPanel(),
				new Dimension(350, 200));
	}

	private JTextArea createPrompt() {
		JTextArea prompt = GuiMaker.createTextArea(false);
		prompt.setOpaque(false);
		prompt.setText(Prompts.LEARNING_START);
		return prompt;
	}

	private JCheckBox createProblematicKanjiCheckbox() {
		final JCheckBox problematicCheckbox = new JCheckBox(Labels.PROBLEMATIC_KANJI_OPTION);
		if (controller.getProblematicKanjiNumber() == 0) {
			problematicCheckbox.setEnabled(false);
		}
		ItemListener action = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				controller.updateNumberOfSelectedKanjiAfterCheckboxToggle(
						problematicCheckbox.isSelected());
			}
		};

		@SuppressWarnings("serial")
		AbstractAction action2 = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (problematicCheckbox.isEnabled()) {
					problematicCheckbox.setSelected(!problematicCheckbox.isSelected());
				}
			}
		};

		problematicCheckbox.addItemListener(action);
		addHotkey(KeyEvent.VK_P, action2, mainPanel.getPanel(),
				HotkeysDescriptions.ADD_PROBLEMATIC_KANJIS);

		return problematicCheckbox;

	}

	public int showLabelWithProblematicKanjis() {
		Component c = parentDialog.getContainer().getFocusOwner();
		JLabel label = new JLabel(Prompts.PROBLEMATIC_KANJIS_ADDED);
		label.setForeground(BasicColors.NAVY_BLUE);
		int rowNumber = rangesPanel.getNumberOfRows();
		rangesPanel.addRow(new SimpleRow(FillType.NONE, Anchor.CENTER, label));
		c.requestFocusInWindow();
		return rowNumber;

	}

	public void hideLabelWithProblematicKanjis(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
	}

	private void addRowToRangesPanel() {

		boolean problematicCheckboxSelected = problematicCheckbox.isSelected();
		int nextRowNumber = rangesPanel.getNumberOfRows();
		if (problematicCheckboxSelected) {
			nextRowNumber -= 1;
		}
		JTextField[] textFields = createTextFieldsForRangeInput(nextRowNumber);
		JTextField fieldFrom = textFields[0];
		SwingUtilities.invokeLater(new Runnable() { // TODO not nice to put
													// swing utilities here
			@Override
			public void run() {
				fieldFrom.requestFocusInWindow();
			}
		});

		JLabel from = new JLabel(Labels.RANGE_FROM_LABEL);
		JLabel labelTo = new JLabel(Labels.RANGE_TO_LABEL);
		JTextField fieldTo = textFields[1];
		JButton delete = createDeleteButton(fieldFrom, fieldTo);
		if (controller.getNumberOfRangesRows() == 1) {
			delete.setVisible(false);
		}

		SimpleRow newRow = new SimpleRow(FillType.NONE, Anchor.NORTH, from, fieldFrom, labelTo,
				fieldTo, delete);

		if (problematicCheckboxSelected) {
			controller.increaseProblematicLabelRowNumber();
			rangesPanel.insertRow(nextRowNumber, newRow);
		}
		else {
			rangesPanel.addRow(newRow);
		}

		if (controller.getNumberOfRangesRows() == 2) {
			changeVisibilityOfDeleteButtonInFirstRow(true);
		}

		scrollToBottom();

	}

	private JTextField[] createTextFieldsForRangeInput(int rowNumber) {
		JTextField[] textFields = new JTextField[2];
		for (int i = 0; i < 2; i++) {
			textFields[i] = new JTextField(5);
			((AbstractDocument) textFields[i].getDocument()).setDocumentFilter(
					new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
		}
		final JTextField from = textFields[0];
		final JTextField to = textFields[1];
		controller.addRow(rowNumber, from, to);
		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				controller.handleKeyTyped(e, to, from, problematicCheckbox.isSelected());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				controller.handleKeyReleased(e, to, from, problematicCheckbox.isSelected());
			}

		};

		from.addKeyListener(keyAdapter);
		to.addKeyListener(keyAdapter);

		textFields[0] = from;
		textFields[1] = to;
		return textFields;
	}

	public boolean showErrorOnThePanel(String message, int rowNumber) {
		// TODO when pressing start, show more detailed info: add row number to
		// the error information
		rangesPanel.insertRow(rowNumber,
				new SimpleRow(FillType.NONE, Anchor.NORTH, GuiMaker.createErrorLabel(message))
						.fillAllVertically());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rangesPanel.getPanel()
						.scrollRectToVisible(rangesPanel.getRows().get(rowNumber).getBounds());
			}
		});
		return true;
	}

	private void scrollToBottom() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				scrollPane.getVerticalScrollBar()
						.setValue(scrollPane.getVerticalScrollBar().getMaximum());
			}
		});

	}

	public boolean removeRowFromPanel(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
		return true;
	}

	public void updateSumOfWordsLabel(int sumOfWords) {
		sumRangeField.setText(Prompts.RANGE_SUM + sumOfWords);
	}

	private JButton createDeleteButton(JTextField from, JTextField to) {
		JButton delete = new JButton(ButtonsNames.REMOVE_ROW);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.removeRangeRow(from, to, problematicCheckbox.isSelected());
			}
		});
		return delete;
	}

	public void removeRow(int rowNumber) {
		rangesPanel.removeRow(rowNumber);
	}

	public void removeDeleteButtonFromFirstRow() {
		rangesPanel.removeLastElementFromRow(0);
	}

	private JButton createButtonAddRow(String text, final MainPanel panel) {
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRowToRangesPanel();
			}
		});
		return button;
	}

	private JTextField createProblematicRangeField(String text) {
		JTextField sumRange = new JTextField(text);
		sumRange.setEditable(false);
		sumRange.setText(sumRange.getText() + controller.getProblematicKanjiNumber());
		return sumRange;
	}

	private AbstractButton createButtonStartLearning(String text, final JPanel panel) {
		@SuppressWarnings("serial")
		AbstractAction a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.showErrorsOrStart(problematicCheckbox.isSelected());
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_ENTER, a, text,
				HotkeysDescriptions.START_LEARNING);
	}

	public void switchToRepeatingPanel() {
		parentDialog.getContainer().dispose();
		controller.switchPanelAndSetWordsRangesToRepeat(problematicCheckbox.isSelected());
	}

	public void showErrorDialog(String message) {
		parentDialog.showMessageDialog(message);
	}

	public void changeVisibilityOfDeleteButtonInFirstRow(boolean visibility) {
		rangesPanel.changeVisibilityOfLastElementInRow(1, visibility);
	}

}
