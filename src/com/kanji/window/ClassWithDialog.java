package com.kanji.window;

import java.awt.Point;

import javax.swing.JFrame;

import com.kanji.dialogs.MyDialog;
import com.kanji.myList.MyList;

@SuppressWarnings("serial")
public abstract class ClassWithDialog extends JFrame {
	private MyDialog dialog;
	private boolean isExcelReaderLoaded;

	// TODO remove this class, add dialog property to base window, add method
	// "openNewWindow" in new dialog
	// TODO which will check whether dialog is opened or not, and will take as
	// parameter panel to be painted

	public void showDialogToSearch(MyList list) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this); // TODO moze skrocic?
			dialog.showSearchWordDialog(list);
			dialog.setLocationAtLeftUpperCornerOfParent(this);
		}

	}

	private boolean notOpenedYet() {
		return (dialog == null || !dialog.isOpened()); // TODO try to avoid
														// nulls
	}

	public void showDialogToAddWord(MyList list) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);
			dialog.showInsertDialog(list);
			// dialog.setLocationAtLeftUpperCornerOfParent(this);
			dialog.setLocation(getRightComponentOfSplitPanePosition());
		}
	}

	public void showMessageDialog(String message, boolean modal) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);
			dialog.showMsgDialog(message, modal);
			dialog.setLocationAtCenterOfParent(this);
		}
	}

	public void showLearnStartDialog(MyList list, int maximumNumber) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);
			dialog.showLearningStartDialog(list, maximumNumber);
			dialog.setLocationRelativeTo(this);
		}
	}

	public boolean showConfirmDialog(String prompt) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);

			dialog.showConfirmDialog(prompt);

			// dialog.setLocationAtCenterOfParent(this);
			// dialog.setLocationRelativeTo(null);

		}
		return dialog.isAccepted();
	}

	public boolean isDialogOpened() {
		return dialog.isOpened();
	}

	public abstract Point getRightComponentOfSplitPanePosition();

}
