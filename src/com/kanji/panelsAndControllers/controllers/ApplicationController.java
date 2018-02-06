package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.*;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Titles;
import com.kanji.context.WordTypeContext;
import com.kanji.exception.DuplicatedWordException;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.kanji.list.listRows.RowInRepeatingList;
import com.kanji.list.myList.MyList;
import com.kanji.model.KanjisAndRepeatingInfo;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.problematicWords.ProblematicKanjiDisplayer;
import com.kanji.problematicWords.ProblematicWordsDisplayer;
import com.kanji.range.SetOfRanges;
import com.kanji.repeating.RepeatingJapaneseWordsDisplayer;
import com.kanji.repeating.RepeatingKanjiDisplayer;
import com.kanji.repeating.RepeatingWordDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.LoadingAndSaving;
import com.kanji.saving.SavingInformation;
import com.kanji.swingWorkers.LoadingProjectWorker;
import com.kanji.utilities.JapaneseWordsFileReader;
import com.kanji.utilities.KanjiListFileReader;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class ApplicationController implements ApplicationStateManager {

	private RepeatingWordsController repeatingWordsPanelController;
	private ApplicationWindow parent;
	private MyList<KanjiInformation> kanjiList;
	private MyList<RepeatingInformation> kanjiRepeatingDates;
	private MyList<RepeatingInformation> japaneseWordsRepeatingDates;
	private MyList<JapaneseWordInformation> japaneseWords;
	private LoadingAndSaving loadingAndSaving;
	private Set<KanjiInformation> problematicKanjis;
	private Set<JapaneseWordInformation> problematicJapaneseWords;
	private boolean isClosingSafe;
	private ApplicationStateManager applicationStateManager;
	private Map<ApplicationSaveableState, ApplicationStateManager> applicationStateToManagerMap;
	private ProblematicWordsController problematicWordsController;
	private JapaneseWordsFileReader japaneseWordsFileReader;
	private SavingInformation savingInformation;
	private RepeatingKanjiDisplayer kanjiWordDisplayer;
	private RepeatingJapaneseWordsDisplayer repeatingJapaneseWordsDisplayer;
	private ProblematicKanjiDisplayer problematicKanjiDisplayer;
	private ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer;
	private RowInJapaneseWordInformations rowInJapaneseWordInformations;

	public ApplicationController(ApplicationWindow parent) {
		problematicKanjis = new HashSet<>();
		problematicJapaneseWords = new HashSet<>();
		this.parent = parent;
		isClosingSafe = true;
		applicationStateManager = this;
		applicationStateToManagerMap = new HashMap<>();
		loadingAndSaving = new LoadingAndSaving();
		japaneseWordsFileReader = new JapaneseWordsFileReader();
		repeatingJapaneseWordsDisplayer = new RepeatingJapaneseWordsDisplayer(
				parent.getKanjiFont());
		kanjiWordDisplayer = new RepeatingKanjiDisplayer(parent.getKanjiFont());

	}

	public ProblematicWordsController getProblematicWordsController() {
		return problematicWordsController;
	}

	public RepeatingWordsPanel getRepeatingWordsPanel() {
		return repeatingWordsPanelController.getRepeatingWordsPanel();
	}

	public void initializeApplicationStateManagers() {
		problematicWordsController = new ProblematicWordsController(parent);
		problematicKanjiDisplayer = new ProblematicKanjiDisplayer(parent,
				problematicWordsController);
		problematicJapaneseWordsDisplayer = new ProblematicJapaneseWordsDisplayer(parent,
				problematicWordsController);
		this.repeatingWordsPanelController = new RepeatingWordsController(parent);
		applicationStateToManagerMap
				.put(ApplicationSaveableState.REPEATING_WORDS, repeatingWordsPanelController);
		applicationStateToManagerMap.put(ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS,
				problematicWordsController);
	}

	//TODO dependencies between classes are weird and should be reconsidered

	public void initializeListsElements() {
		initializeKanjiList();
		initializeKanjiRepeatingDates();
		initializeJapaneseWordsList();
		initializeJapaneseRepeatingDates();
	}

	private void initializeJapaneseWordsList() {
		rowInJapaneseWordInformations = new RowInJapaneseWordInformations(parent);
		japaneseWords = new MyList<>(parent, this, rowInJapaneseWordInformations,
				Titles.JAPANESE_WORDS_LIST, true,
				JapaneseWordInformation.getElementsTypesAndLabels(),
				JapaneseWordInformation.getInitializer());

		JapaneseWordInformation cat = new JapaneseWordInformation(PartOfSpeech.NOUN, "kot");
		cat.addWritings("ねこ");
		japaneseWords.addWord(cat);
		JapaneseWordInformation dog = new JapaneseWordInformation(PartOfSpeech.NOUN, "pies");
		dog.addWritings("いぬ", "犬");
		JapaneseWordInformation dog2 = new JapaneseWordInformation(PartOfSpeech.NOUN, "pies");
		dog2.addWritings("いぬ", "犬", "猫", "花");
		japaneseWords.addWord(dog);
		japaneseWords.addWord(dog2);
		JapaneseWordInformation verb = new JapaneseWordInformation(PartOfSpeech.VERB, "otwierać");
		verb.addWritings("あける", "開ける", " 空ける", "明ける");
		verb.addWritings("ひらける", "開ける", " 空ける", "明ける");
		verb.addAditionalInformation(AdditionalInformationTag.VERB_CONJUGATION,
				Labels.VERB_CONJUGATION_GODAN);
		JapaneseWordInformation japaneseWordInformation = new JapaneseWordInformation(
				PartOfSpeech.NOUN, "Test");
		japaneseWordInformation.addWritings("らけ", "務");
		japaneseWords.addWord(japaneseWordInformation);
		japaneseWords.addWord(verb);
	}

	private void initializeJapaneseRepeatingDates() {
		japaneseWordsRepeatingDates = new MyList<>(parent, this, new RowInRepeatingList(),
				Titles.JAPANESE_REPEATING_LIST, false,
				RepeatingInformation.getElementsTypesAndLabels(),
				RepeatingInformation.getInitializer());
		japaneseWordsRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(1993, 11, 13, 13, 25), true,
						"3 minuty"));
		japaneseWordsRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2005, 1, 1, 11, 11), true,
						"4 minuty"));
		japaneseWordsRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2000, 12, 31, 10, 0), true,
						"5 minut"));
	}

	private JFileChooser createFileChooser() {
		// TODO think about some more clever way of determining whether we are
		// in test or deployment directory
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(getWorkingDirectory(fileChooser));
		return fileChooser;
	}

	public void loadList(WordTypeContext wordTypeContext) {
		switch (wordTypeContext.getWordTypeForRepeating()) {
		case KANJIS:
			loadKanjiList();
			break;
		case JAPANESE_WORDS:
			loadJapaneseWordsList();
			break;
		}
	}

	private void loadJapaneseWordsList() {
		JFileChooser fileChooser = createFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		//TODO each file should have a label at the top, parser should parse the word type in kanji list:
		// noun (default), verb (ending in ru, su, mu etc), adjective ending in -i, expression
		int option = fileChooser.showOpenDialog(parent.getContainer());
		if (option == JFileChooser.CANCEL_OPTION) {
			return;
		}
		try {
			List<JapaneseWordInformation> japaneseWords = japaneseWordsFileReader
					.readFiles(fileChooser.getSelectedFiles());
			for (JapaneseWordInformation japaneseWordInformation : japaneseWords) {
				this.japaneseWords.addWord(japaneseWordInformation);
			}

		}
		catch (Exception e) {
			parent.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadKanjiList() {
		JFileChooser fileChooser = createFileChooser();
		int option = fileChooser.showOpenDialog(parent.getContainer());
		if (option == JFileChooser.CANCEL_OPTION) {
			return;
		}
		try {
			loadKanjisListsFromTextFile(fileChooser.getSelectedFile());
		}
		catch (DuplicatedWordException | IOException e) {
			parent.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadKanjisListsFromTextFile(File file)
			throws DuplicatedWordException, IOException {
		KanjiListFileReader fileReader = new KanjiListFileReader();
		KanjisAndRepeatingInfo words = fileReader.readFile(file);
		List<KanjiInformation> kanjiInformations = words.getKanjiInformations();
		List<RepeatingInformation> repeatingInformations = words.getRepeatingInformations();
		Set<Integer> problematicKanjis = words.getProblematicKanjis();
		setProblematicWordsAndUpdateInformation(convertIdsToKanjiInformations(problematicKanjis));
		kanjiList.cleanWords();
		kanjiRepeatingDates.cleanWords();
		for (KanjiInformation kanjiInformation : kanjiInformations) {
			kanjiList.addWord(kanjiInformation);
		}
		for (RepeatingInformation repeatingInformation : repeatingInformations) {
			kanjiRepeatingDates.addWord(repeatingInformation);
		}
	}

	public Set<KanjiInformation> convertIdsToKanjiInformations(Set<Integer> ids) {
		Set<KanjiInformation> kanjiInformations = new HashSet<>();
		for (Integer i : ids) {
			kanjiInformations.add(getKanjiList()
					.findRowBasedOnPropertyStartingFromBeginningOfList(new KanjiIdChecker(), i,
							SearchingDirection.FORWARD));
		}
		return kanjiInformations;
	}

	public void openKanjiProject() {
		File fileToSave = openFile();
		if (!fileToSave.exists())
			return;

		try {
			savingInformation = loadingAndSaving.load(fileToSave);
		}
		catch (Exception e1) {
			parent.showMessageDialog("Exception loading from file.");
			e1.printStackTrace();
			return;
		}
		loadingAndSaving.setFileToSave(fileToSave);
		try {
			if (savingInformation.getKanjiKoohiCookiesHeaders() != null) {
				//TODO this should go to application controller's "restore
				// state method along with filling the mylists"
				problematicKanjiDisplayer
						.setCookies(savingInformation.getKanjiKoohiCookiesHeaders());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			parent.showMessageDialog("Error setting cookies");
		}

		kanjiList.cleanWords();
		setProblematicWordsAndUpdateInformation(savingInformation.getProblematicKanjis());
		setProblematicWordsAndUpdateInformation(savingInformation.getProblematicJapaneseWords());
		parent.updateTitle(fileToSave.toString());
		parent.changeSaveStatus(SavingStatus.NO_CHANGES);

		LoadingProjectWorker loadingProjectWorker = new LoadingProjectWorker(this,
				parent.showProgressDialog());
		loadingProjectWorker.load(japaneseWords, savingInformation.getJapaneseWordInformations());
		loadingProjectWorker.load(kanjiList, savingInformation.getKanjiWords());
		loadingProjectWorker.load(japaneseWordsRepeatingDates,
				savingInformation.getJapaneseWordsRepeatingInformations());
		loadingProjectWorker.load(kanjiRepeatingDates, savingInformation.getRepeatingList());
	}

	private ApplicationStateManager getStateManagerForHandlingState(
			ApplicationSaveableState state) {
		if (state == null) {
			return this;
		}
		return applicationStateToManagerMap.get(state);
	}

	public void finishedLoadingProject() {
		parent.closeDialog();
		if (savingInformation.hasStateToRestore()) {
			getStateManagerForHandlingState(savingInformation.
					getApplicationSaveableState()).restoreState(savingInformation);
		}
	}

	private void initializeKanjiList() {
		kanjiList = new MyList<>(parent, this, new RowInKanjiInformations(parent),
				Titles.KANJI_LIST, true, KanjiInformation.getElementsTypesAndLabels(),
				KanjiInformation.getInitializer());

		for (int i = 1; i <= 15; i++) {
			kanjiList.addWord(new KanjiInformation("Word no. " + i, i));
		}
		kanjiList.addWord(new KanjiInformation(
				"Firstly a trivial correction: the integer ALIGN_JUSTIF"
						+ " should read ALIGN_JUSTIFIED Secondly, I have tried several variations of getting "
						+ "justified text in JTextPane including the solution given above and using a menuitem "
						+ "with alignment action such as: menu.add(new , i, i);", 11));
	}

	private void initializeKanjiRepeatingDates() {
		kanjiRepeatingDates = new MyList<>(parent, this, new RowInRepeatingList(),
				Titles.KANJI_REPEATING_LIST, false,
				RepeatingInformation.getElementsTypesAndLabels(),
				RepeatingInformation.getInitializer());
		kanjiRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(1993, 11, 13, 13, 25), true,
						"3 minuty"));
		kanjiRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2005, 1, 1, 11, 11), true,
						"4 minuty"));
		kanjiRepeatingDates.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2000, 12, 31, 10, 0), true,
						"5 minut"));
	}

	private File openFile() {
		JFileChooser fileChooser = createFileChooser();

		int chosenOption = fileChooser.showOpenDialog(parent.getContainer());
		if (chosenOption == JFileChooser.CANCEL_OPTION)
			return new File("");
		File file = fileChooser.getSelectedFile();
		return file;
	}

	private File getWorkingDirectory(JFileChooser fileChooser) {
		String directory;
		if (System.getProperty("user.dir").contains("dist")) {
			directory = "Powtórki kanji";
		}
		else {
			directory = "Testy do kanji";
		}
		return new File(fileChooser.getCurrentDirectory() + File.separator + directory);
	}

	public boolean showConfirmDialog(String message) {
		return parent.showConfirmDialog(message);
	}

	public void showInsertWordDialog() {
		parent.showInsertDialog(rowInJapaneseWordInformations,
				parent.getStartingPanel().getActiveWordsList());
	}

	public void showSearchWordDialog() {
		parent.showSearchWordDialog(parent.getStartingPanel().getActiveWordsList());
	}

	public void showLearningStartDialog() {

		repeatingWordsPanelController.setWordDisplayer(getWordDisplayerForCurrentWordList());
		problematicWordsController
				.setProblematicWordsDisplayer(getProblematicWordsDisplayerBasedOnActiveWordList());
		parent.showLearningStartDialog(
				parent.getStartingPanel().getActiveWordsList().getNumberOfWords());
	}

	public MyList<JapaneseWordInformation> getJapaneseWords() {
		return japaneseWords;
	}

	public MyList<RepeatingInformation> getJapaneseWordsRepeatingDates() {
		return japaneseWordsRepeatingDates;
	}

	public MyList<KanjiInformation> getKanjiList() {
		return kanjiList;
	}

	public MyList getActiveWordsList() {
		return parent.getStartingPanel().getActiveWordsList();
	}

	public MyList<RepeatingInformation> getKanjiRepeatingDates() {
		return kanjiRepeatingDates;
	}

	public void saveProject() {
		if (!loadingAndSaving.hasFileToSave()) {
			return;
		}
		parent.changeSaveStatus(SavingStatus.SAVING);
		SavingInformation savingInformation = applicationStateManager.getApplicationState();

		try {
			loadingAndSaving.save(savingInformation);
		}
		catch (IOException e1) {
			parent.showMessageDialog("Exception while saving.");
			e1.printStackTrace();
		}
		parent.changeSaveStatus(SavingStatus.SAVED);
	}

	public void saveList() {
		JFileChooser fileChooser = createFileChooser();

		int option = fileChooser.showSaveDialog(parent.getContainer());
		if (option != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File f = fileChooser.getSelectedFile();
		f = new File(f.toString() + ".txt");
		KanjiListFileReader reader = new KanjiListFileReader();
		try {
			reader.writeToFile(f, kanjiList, kanjiRepeatingDates, getProblematicKanjis());
		}
		catch (IOException e) {
			parent.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	public void showSaveDialog() {
		if (!loadingAndSaving.hasFileToSave()) {
			JFileChooser fileChooser = createFileChooser();
			int option = fileChooser.showSaveDialog(this.parent.getContainer());
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				loadingAndSaving.setFileToSave(file);
				saveProject();
			}
		}

	}

	public <Word extends ListElement> void setProblematicWordsAndUpdateInformation(
			Set<Word> problematicWords) {
		if (problematicWords.isEmpty()) {
			return;
		}
		Class wordClass = problematicWords.iterator().next().getClass();
		if (wordClass.equals(KanjiInformation.class)) {
			problematicKanjis.addAll((Set<KanjiInformation>) problematicWords);
			//TODO ugly solution
		}
		else if (wordClass.equals(JapaneseWordInformation.class)) {
			problematicJapaneseWords.addAll((Set<JapaneseWordInformation>) problematicWords);
		}
		else {
			throw new IllegalArgumentException("Invalid active words class name: " + wordClass);
		}
		if (wordClass.equals(getActiveWordsList().getListElementClass())) {
			parent.updateProblematicWordsAmount();
		}

	}

	public Set<KanjiInformation> getProblematicKanjis() {
		return problematicKanjis;
	}

	public Set<? extends ListElement> getProblematicWordsBasedOnCurrentTab() {
		Class activeWordsElementClass = getActiveWordsList().getListElementClass();
		if (activeWordsElementClass.equals(KanjiInformation.class)) {
			return problematicKanjis;
		}
		else if (activeWordsElementClass.equals(JapaneseWordInformation.class)) {
			return problematicJapaneseWords;
		}
		else {
			throw new IllegalArgumentException(
					"Invalid active words class" + activeWordsElementClass);
		}
	}

	public int getProblematicWordsAmountBasedOnCurrentTab() {
		return getProblematicWordsBasedOnCurrentTab().size();
	}

	public void addWordToRepeatingList(RepeatingInformation word) {
		MyList<RepeatingInformation> repeatingList = parent.getStartingPanel()
				.getActiveRepeatingList();
		repeatingList.addWord(word);
		repeatingList.scrollToBottom();
	}

	public void setRepeatingInformation(RepeatingInformation info) {
		repeatingWordsPanelController.setRepeatingInformation(info);
	}

	public void initiateWordsLists(SetOfRanges ranges, boolean withProblematic) {
		repeatingWordsPanelController
				.initiateWordsLists(ranges, getProblematicWordsBasedOnCurrentTab(),
						withProblematic);
	}

	public void startRepeating() {
		parent.showPanel(ApplicationPanels.REPEATING_PANEL);
		isClosingSafe = false;
		kanjiWordDisplayer.addProblematicKanjis(getProblematicKanjis());
		repeatingWordsPanelController.startRepeating();
		applicationStateManager = repeatingWordsPanelController;
	}

	private RepeatingWordDisplayer getWordDisplayerForCurrentWordList() {
		MyList currentList = parent.getStartingPanel().getActiveWordsList();
		Class listClass = currentList.getListElementClass();
		if (listClass.equals(KanjiInformation.class)) {
			return kanjiWordDisplayer;
		}
		else if (listClass.equals(JapaneseWordInformation.class)) {
			return repeatingJapaneseWordsDisplayer;
		}
		return null;
	}

	public void finishedRepeating() {
		isClosingSafe = true;
		applicationStateManager = this;
	}

	public Set<JapaneseWordInformation> getProblematicJapaneseWords() {
		return problematicJapaneseWords;
	}

	public boolean isClosingSafe() {
		return isClosingSafe;
	}

	@Override public SavingInformation getApplicationState() {
		SavingInformation savingInformation = new SavingInformation(kanjiList.getWords(),
				kanjiRepeatingDates.getWords(), getProblematicKanjis(),
				getProblematicJapaneseWords(), japaneseWords.getWords(),
				japaneseWordsRepeatingDates.getWords());
		List<String> kanjiKoohiCookiesHeaders = problematicKanjiDisplayer.getCookieHeaders();
		if (!kanjiKoohiCookiesHeaders.isEmpty()) {
			savingInformation.setKanjiKoohiCookiesHeaders(kanjiKoohiCookiesHeaders);
		}
		return savingInformation;
	}

	@Override public void restoreState(SavingInformation savingInformation) {
		applicationStateManager.restoreState(savingInformation);
	}

	public void switchStateManager(ApplicationStateManager stateManager) {
		this.applicationStateManager = stateManager;
		isClosingSafe = false;
	}

	public ProblematicWordsDisplayer getProblematicWordsDisplayerBasedOnActiveWordList() {
		MyList activeWordList = parent.getStartingPanel().getActiveWordsList();
		Class listElementsClass = activeWordList.getListElementClass();
		if (listElementsClass.equals(KanjiInformation.class)) {
			return problematicKanjiDisplayer;
		}
		else if (listElementsClass.equals(JapaneseWordInformation.class)) {
			return problematicJapaneseWordsDisplayer;
		}
		return null;
	}

	public void switchToList(Class listType) {
		parent.getStartingPanel().switchToList(listType);
	}

}