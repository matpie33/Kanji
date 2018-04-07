package com.kanji.model;

import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.JapaneseWordWritingsChecker;
import com.kanji.list.listElements.ListElement;

import javax.swing.text.JTextComponent;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class KanaAndKanjiStrings  {
	private String kana;
	private Set<String> kanji;
	private String modifiedValue;
	private boolean kanaModified;
	private final static String DEFAULT_VALUE = Prompts.KANJI_TEXT;
	//TODO should be replaced with japanese writings class

	public KanaAndKanjiStrings(String kana, Set<String> kanji,
			String modifiedValue, boolean kanaModified) {
		removeDefaultValues(kanji);
		this.kana = kana;
		this.kanji = kanji;
		this.modifiedValue = modifiedValue;
		this.kanaModified = kanaModified;
	}

	public KanaAndKanjiStrings(JTextComponent kana, List<JTextComponent> kanji,
			String modifiedValue, boolean kanaModified) {
		this(kana.getText(), convertKanjiTextfieldsToStrings(kanji),
				modifiedValue, kanaModified);
	}

	private void removeDefaultValues(Set<String> kanjis) {
		for (Iterator<String> iterator = kanjis.iterator(); iterator.hasNext();){
			String kanji = iterator.next();
			if (kanji.equals(DEFAULT_VALUE)) {
				iterator.remove();
			}
		}
	}

	public String getKana() {
		return kana;
	}

	public Set<String> getKanji() {
		return kanji;
	}

	@Override
	public String toString() {
		return modifiedValue;
	}

	public boolean isKanaModified() {
		return kanaModified;
	}

	public String getModifiedValue() {
		return modifiedValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getModifiedValue());
	}

	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(getClass())) {
			return false;
		}
		KanaAndKanjiStrings otherKana = (KanaAndKanjiStrings) other;
		return otherKana.getModifiedValue().equals(getModifiedValue());
	}

	public static Set<String> convertKanjiTextfieldsToStrings(
			List<JTextComponent> kanjiTextFields) {
		return kanjiTextFields.stream().map(JTextComponent::getText)
				.collect(Collectors.toSet());
	}

}
