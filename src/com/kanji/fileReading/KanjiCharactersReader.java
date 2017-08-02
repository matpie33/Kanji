package com.kanji.fileReading;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class KanjiCharactersReader {

	private Font font;
	private List<String> words;

	public void load() {
		;
		words = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("kanjis.txt"), "Utf-8"));
			String line = "";
			while ((line = br.readLine()) != null) {
				words.add(line);
			}
			String first = words.get(0).replace("\uFEFF", "");
			words.remove(0);
			words.add(0, first);
			br.close();

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(words);
		font = new Font("MS PMincho", Font.BOLD, 100);
	}

	public String getKanjiById(int id) {
		return words.get(id - 1);
	}

	public Font getFont() {
		return font;
	}

}