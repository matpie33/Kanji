package com.kanji.constants.enums;

public enum ApplicationPanels {

	STARTING_PANEL("Starting panel"), REPEATING_PANEL(
			"Repeating panel"), PROBLEMATIC_WORDS_PANEL(
			"Problematic words panel");
	private String panelName;

	ApplicationPanels(String panelName) {
		this.panelName = panelName;
	}

	public String getPanelName() {
		return panelName;
	}

}
