package com.kanji.myList;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.Prompts;

public class RowInRepeatingList implements ListRow<RepeatingInformation> {

	private int rowsCounter = 1;
	private MyList<RepeatingInformation> list;

	public RowInRepeatingList(MyList<RepeatingInformation> list) {
		this.list = list;
	}

	@Override
	public MainPanel listRow(RepeatingInformation rep) {
		String word = rep.getRepeatingRange();
		String time = rep.getTimeSpentOnRepeating();
		Date date1 = rep.getRepeatingDate();

		String rowNumber = "" + rowsCounter++ + ".";
		JLabel repeatedWords = createLabel(Prompts.REPEATING_WORDS_RANGE + word);

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		JLabel date = createLabel(
				rowNumber + " " + Prompts.REPEATING_DATE + sdf.format(date1) + ".");
		date.setForeground(BasicColors.OCEAN_BLUE);
		JLabel timeSpent = null;

		if (time != null) {
			timeSpent = createLabel(Prompts.REPEATING_TIME + time);
		}

		JButton delete = createButtonRemove();

		MainPanel panel = new MainPanel(null);
		panel.addRows(new SimpleRow(FillType.HORIZONTAL, date).nextRow(date).nextRow(repeatedWords)
				.nextRow(timeSpent).nextRow(FillType.NONE, delete));
		// addActionListener(delete, wrappingPanel, rep);
		return panel;

	}

	private JLabel createLabel(String word) {
		JLabel l1 = new JLabel(word);
		l1.setForeground(Color.WHITE);
		return l1;
	}

	private JButton createButtonRemove() {
		JButton remove = new JButton("-");
		return remove;
	}

	private void addActionListener(JButton button, JPanel panel, final RepeatingInformation kanji) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!list.showMessage(
						String.format(Prompts.DELETE_ELEMENT, Prompts.REPEATING_ELEMENT))) {
					return;
				}

				// removeRow(panel);
				list.getWords().remove(kanji);
				list.save();
			}
		});
	}

	public void setList(MyList<RepeatingInformation> list) {
		this.list = list;
	}

	// private void removeRow(JPanel row) {
	// rowsPanel.removeRow(row);
	// }

}
