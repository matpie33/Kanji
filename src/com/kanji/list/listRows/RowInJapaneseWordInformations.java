package com.kanji.list.listRows;

import com.guimaker.panels.MainPanel;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelServiceAddMode;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelServiceEditMode;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.list.myList.MyList;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

public class RowInJapaneseWordInformations
		implements ListRowMaker<JapaneseWordInformation> {
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private ApplicationWindow applicationWindow;

	public RowInJapaneseWordInformations(ApplicationWindow applicationWindow) {
		japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController());
		this.applicationWindow = applicationWindow;
	}

	@Override
	public MainPanel createListRow(JapaneseWordInformation japaneseWord,
			CommonListElements commonListElements) {
		MainPanel panel = japaneseWordPanelCreator
				.createPanel(japaneseWord, new JapanesePanelServiceEditMode(japaneseWord),
						applicationWindow);
		return panel;
	}

	public MyList<JapaneseWriting> getJapaneseWritings (){
		return japaneseWordPanelCreator.getJapaneseWritingsList();
	}

	public JapaneseWordPanelCreator getJapaneseWordPanelCreator() {
		return japaneseWordPanelCreator;
	}
}
