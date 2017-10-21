package com.kanji.utilities;

import java.awt.Color;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;

public class CommonGuiElementsMaker {

	public static JTextComponent createKanjiWordInput(String defaultContent) {
		return GuiMaker.createTextArea(
				new TextAreaOptions().text(defaultContent).rowsAndColumns(3, 15));
	}

	public static JTextComponent createKanjiIdInput() {
		return GuiMaker
				.createTextField(new TextComponentOptions().maximumCharacters(5).digitsOnly(true).rowsAndColumns(1,5));
	}

	public static JLabel createErrorLabel(String message) {
		return GuiMaker
				.createLabel(new ComponentOptions().text(message).foregroundColor(Color.RED));
	}

}
