package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Urls;
import com.kanji.context.ContextOwner;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.webPanel.ConnectionFailMessagePage;
import com.kanji.webPanel.WebPagePanel;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ProblematicJapaneseWordsPanel extends AbstractPanelWithHotkeysInfo
			implements ContextOwner {

	private ProblematicWordsController problematicWordsController;
	private MyList<JapaneseWordInformation> problematicWords;
	private MainPanel kanjiInformationPanel;
	private WebPagePanel englishDictionaryPanel;
	private WebPagePanel japaneseEnglishDictionaryPanel;
	private static final String TANGORIN_URL = "http://tangorin.com/";

	public ProblematicJapaneseWordsPanel(ProblematicWordsController problematicWordsController,
			ApplicationWindow parent) {
		parentDialog = parent;
		this.problematicWordsController = problematicWordsController;
		kanjiInformationPanel = new MainPanel(null);
	}

	public void initialize (){
		problematicWords = problematicWordsController.getWordsToReviewList();
		englishDictionaryPanel = new WebPagePanel(this,
				new ConnectionFailMessagePage());

		japaneseEnglishDictionaryPanel = new WebPagePanel(this,
				new ConnectionFailMessagePage());
		japaneseEnglishDictionaryPanel.showPage(TANGORIN_URL);
		englishDictionaryPanel.showPage(Urls.DICTIONARY_PL_EN_MAIN_PAGE);
	}

	public void searchWord (String word){
		japaneseEnglishDictionaryPanel.showPage(TANGORIN_URL+"/general/"+word);
	}

	@Override
	public void setParentDialog (DialogWindow parentDialog){
		super.setParentDialog(parentDialog);
		parentDialog.maximize();
		addHotkey(KeyEvent.VK_SPACE, problematicWordsController.createActionShowNextWordOrCloseDialog(),
				((JDialog) parentDialog.getContainer()).getRootPane(),
				HotkeysDescriptions.SHOW_NEXT_KANJI);
	}

	@Override public void createElements() {
		JSplitPane wordAndKanjiInformationSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.VERTICAL, problematicWords.getPanel(),
				kanjiInformationPanel.getPanel(),0.5);
		JSplitPane dictionariesSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.VERTICAL, japaneseEnglishDictionaryPanel.getPanel(),
				englishDictionaryPanel.getPanel(),0.5);

		JSplitPane wordAndDictionariesSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.HORIZONTAL, wordAndKanjiInformationSplitPane,
				dictionariesSplitPane,0.1);

		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, wordAndDictionariesSplitPane));

	}

	@Override public Object getContext() {
		return null; //TODO
	}

	public Component getFocusOwner(){
		return getDialog().getContainer().getFocusOwner();
	}

}
