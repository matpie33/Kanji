package com.kanji.window;

import com.kanji.constants.ButtonsNames;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.fileReading.ExcelReader;
import com.kanji.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class RepeatingWordsPanel
  extends JPanel
{
  private static final long serialVersionUID = 5557984078176822840L;
  private MyList words;
  private List<String> wordsToRepeat;
  private ExcelReader excel;
  private BaseWindow parent;
  private Set<Integer> problematicKanjis;
  private String currentWord;
  private JLabel remainingLabel;
  private JLabel time;
  private double timeElapsed;
  private double interval = 0.1D;
  private String timeLabel = "Czas: ";
  private Thread timerThread;
  private boolean timerRunning;
  private JTextArea kanjiTextArea;
  private JTextArea wordTextArea;
  private JButton pauseOrResume;
  private JButton showWord;
  private JButton recognizedWord;
  private JButton notRecognizedWord;
  private JPanel repeatingPanel;
  private static final String PAUSE_TEXT = "Pauza";
  private static final String RESUME_TEXT = "Wznow";
  private static final String RECOGNIZED_WORD_TEXT = "Znam";
  private static final String NOT_RECOGNIZED_WORD_TEXT = "Nie pamietam";
  private final Color repeatingBackgroundColor = Color.white;
  private final Color windowBackgroundColor = Color.GREEN;
  private final int kanjiFontSize = 80;
  
  public RepeatingWordsPanel(BaseWindow parent)
  {
    this.wordsToRepeat = new LinkedList();
    this.parent = parent;
    this.timerRunning = false;
    initialize();
    createPanel();
  }
  
  private void initialize()
  {
    setLayout(new GridBagLayout());
    setBackground(this.windowBackgroundColor);
    this.repeatingPanel = new JPanel(new GridBagLayout());
    this.repeatingPanel.setBackground(this.repeatingBackgroundColor);
  }
  
  private void createPanel()
  {
    int level = 0;
    addTitleAndTime(Titles.repeatingWordsTitle, level);
    if (!this.wordsToRepeat.isEmpty())
    {
      level++;
      initiateRepeatingPanel(level);
    }
    level++;
    addButtons(level);
  }
  
  private void addTitleAndTime(String title, int level)
  {
    GridBagConstraints c = createDefaultConstraints();
    c.gridx = 0;
    c.gridy = level;
    c.anchor = 10;
    add(new JLabel(title), c);
    
    c.weightx = 0.0D;
    c.anchor = 13;
    this.time = new JLabel(this.timeLabel);
    add(this.time, c);
  }
  
  private GridBagConstraints createDefaultConstraints()
  {
    GridBagConstraints c = new GridBagConstraints();
    c.weightx = 1.0D;
    int a = 5;
    c.insets = new Insets(a, a, a, a);
    return c;
  }
  
  private void initiateRepeatingPanel(int level)
  {
    createElementsForRepeatingPanel();
    setButtonsToLearningAndAddThem();
    
    GridBagConstraints d = createDefaultConstraints();
    d.gridy = level;
    d.anchor = 10;
    d.weighty = 1.0D;
    d.weightx = 0.0D;
    
    add(this.repeatingPanel, d);
  }
  
  private void setButtonsToLearningAndAddThem()
  {
    addElementsToRepeatingPanel(showWordButtons());
  }
  
  private JButton[] showWordButtons()
  {
    return new JButton[] { this.pauseOrResume, this.showWord };
  }
  
  private void createElementsForRepeatingPanel()
  {
    createWordLabel();
    createWordArea();
    createShowWordButton();
    createPauseOrResumeButton();
    createRecognizedWordButton();
    createNotRecognizedWordButton();
  }
  
  private void createWordLabel()
  {
    this.wordTextArea = new JTextArea(10, 10);
    this.wordTextArea.setEditable(false);
    this.wordTextArea.setLineWrap(true);
    this.wordTextArea.setWrapStyleWord(true);
    this.wordTextArea.setText(pickRandomWord());
  }
  
  private void createWordArea()
  {
    Font f = new Font(this.excel.getFontName(), 1, 80);
    
    this.kanjiTextArea = new JTextArea(10, 10);
    this.kanjiTextArea.setFont(f);
    this.kanjiTextArea.setEditable(false);
    this.kanjiTextArea.setLineWrap(true);
    this.kanjiTextArea.setWrapStyleWord(true);
  }
  
  private String pickRandomWord()
  {
    Random randomizer = new Random();
    int index = randomizer.nextInt(this.wordsToRepeat.size());
    this.currentWord = ((String)this.wordsToRepeat.get(index));
    return (String)this.wordsToRepeat.get(index) + " " + this.words.getIdOfTheWord(this.currentWord);
  }
  
  private void createShowWordButton()
  {
    this.showWord = new JButton(ButtonsNames.buttonShowKanjiText);
    this.showWord.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        RepeatingWordsPanel.this.setButtonsToRecognizeWord();
        RepeatingWordsPanel.this.showKanji();
      }
    });
  }
  
  private void setButtonsToRecognizeWord()
  {
    addElementsToRepeatingPanel(recognizeWordButtons());
  }
  
  private JButton[] recognizeWordButtons()
  {
    return new JButton[] { this.pauseOrResume, this.recognizedWord, this.notRecognizedWord };
  }
  
  private void showKanji()
  {
    this.kanjiTextArea.setText(this.excel.getKanjiById(this.words.getIdOfTheWord(this.currentWord)));
  }
  
  private void createPauseOrResumeButton()
  {
    this.pauseOrResume = new JButton("Pauza");
    this.pauseOrResume.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        RepeatingWordsPanel.this.pauseOrResume();
      }
    });
  }
  
  private void pauseOrResume()
  {
    if (this.pauseOrResume.getText().equals(ButtonsNames.buttonPauseText))
    {
      stopTimer();
      this.pauseOrResume.setText(ButtonsNames.buttonResumeText);
    }
    else
    {
      startTimer();
      this.pauseOrResume.setText(ButtonsNames.buttonPauseText);
    }
  }
  
  private void createRecognizedWordButton()
  {
    this.recognizedWord = new JButton(ButtonsNames.buttonRecognizedWordText);
    AbstractAction a = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        RepeatingWordsPanel.this.removeWordIfItsProblematic();
        RepeatingWordsPanel.this.getNextWord();
        System.out.println("problematics: " + RepeatingWordsPanel.this.problematicKanjis);
      }
    };
    this.recognizedWord.addActionListener(a);
    
    this.recognizedWord.getInputMap(2)
      .put(KeyStroke.getKeyStroke(32, 0), "pressed");
    this.recognizedWord.getActionMap().put("pressed", a);
  }
  
  private void removeWordIfItsProblematic()
  {
    int id = getCurrentWordId();
    this.problematicKanjis.remove(Integer.valueOf(id));
  }
  
  private int getCurrentWordId()
  {
    return this.words.getIdOfTheWord(this.currentWord);
  }
  
  private void createNotRecognizedWordButton()
  {
    this.notRecognizedWord = new JButton(ButtonsNames.buttonNotRecognizedText);
    
    AbstractAction a = new AbstractAction()
    {
      private static final long serialVersionUID = 5973495342336199749L;
      
      public void actionPerformed(ActionEvent e)
      {
        RepeatingWordsPanel.this.addToProblematic();
        RepeatingWordsPanel.this.getNextWord();
      }
    };
    this.notRecognizedWord.addActionListener(a);
    this.notRecognizedWord.getInputMap(2)
      .put(KeyStroke.getKeyStroke(65, 0), "pressed");
    this.notRecognizedWord.getActionMap().put("pressed", a);
  }
  
  private void addToProblematic()
  {
    int num = getCurrentWordId();
    System.out.println(num);
    this.problematicKanjis.add(Integer.valueOf(num));
  }
  
  private void getNextWord()
  {
    String word = this.wordTextArea.getText();
    this.wordsToRepeat.remove(this.currentWord);
    if (parent.getProblematicKanjis().contains(words.getIdOfTheWord(currentWord)))
    	parent.getProblematicKanjis().remove(words.getIdOfTheWord(currentWord));
    System.out.println("removed: " + word);
    if (!this.wordsToRepeat.isEmpty())
    {
      setButtonsToLearningAndAddThem();
      this.wordTextArea.setText(pickRandomWord());
      this.kanjiTextArea.setText("");
    }
    else
    {
      this.parent.showMessageDialog(Prompts.repeatingIsDonePrompt);
      stopTimer();
      this.parent.setProblematicKanjis(this.problematicKanjis);
      this.parent.showCardPanel(BaseWindow.LIST_PANEL);
      this.parent.save();
    }
    this.remainingLabel.setText(createRemainingPrompt());
    this.showWord.requestFocusInWindow();
  }
  
  private void addElementsToRepeatingPanel(JButton[] buttons)
  {
    this.repeatingPanel.removeAll();
    
    GridBagConstraints c = createDefaultConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = buttons.length;
    c.anchor = 10;
    c.fill = 2;
    this.repeatingPanel.add(this.wordTextArea, c);
    
    c.gridy += 1;
    c.fill = 0;
    this.repeatingPanel.add(this.kanjiTextArea, c);
    
    c.gridwidth = 1;
    c.gridy += 1;
    JButton[] arrayOfJButton;
    int j = (arrayOfJButton = buttons).length;
    for (int i = 0; i < j; i++)
    {
      JButton button = arrayOfJButton[i];
      this.repeatingPanel.add(button, c);
      c.gridx += 1;
    }
    repaint();
  }
  
  private void addButtons(int level)
  {
    JButton returnButton = new JButton(ButtonsNames.buttonGoBackText);
    returnButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        RepeatingWordsPanel.this.parent.showCardPanel(BaseWindow.LIST_PANEL);
        RepeatingWordsPanel.this.stopTimer();
      }
    });
    GridBagConstraints c = createDefaultConstraints();
    
    c.gridy = level;
    c.gridx = 0;
    c.anchor = 10;
    c.fill = 0;
    this.remainingLabel = new JLabel(createRemainingPrompt());
    add(this.remainingLabel, c);
    
    c.anchor = 13;
    c.fill = 0;
    add(returnButton, c);
  }
  
  private String createRemainingPrompt()
  {
    return Prompts.remainingKanjiPrompt+" " + this.wordsToRepeat.size();
  }
  
  public void setRepeatingWords(MyList wordsList)
  {
    this.wordsToRepeat = new LinkedList();
    this.words = wordsList;
  }
  
  public void setRangesToRepeat(SetOfRanges ranges){
		for (Range range: ranges.getRangesAsList()){
			for (int i=range.getRangeStart(); i<=range.getRangeEnd(); i++){
				wordsToRepeat.add(words.findWordInRow(i-1));
			}
		}		
	}
  
  public void setProblematicKanjis(Set<Integer> problematicKanjis)
  {
    this.problematicKanjis = problematicKanjis;
    for (Iterator localIterator = problematicKanjis.iterator(); localIterator.hasNext();)
    {
      int i = ((Integer)localIterator.next()).intValue();
      String word = this.words.getWordForId(i);
      if (!this.wordsToRepeat.contains(word)) {
        this.wordsToRepeat.add(word);
      }
    }
  }
  
  public void startRepeating()
  {
    removeAll();
    createPanel();
    revalidate();
    repaint();
    startTimer();
    System.out.println("P: " + this.problematicKanjis);
  }
  
  public void reset()
  {
    this.timeElapsed = 0.0D;
    this.problematicKanjis = new HashSet();
  }
  
  private void startTimer()
  {
    this.timerRunning = true;
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        while (RepeatingWordsPanel.this.timerRunning)
        {
          RepeatingWordsPanel.this.timeElapsed += RepeatingWordsPanel.this.interval;
          RepeatingWordsPanel.this.time.setText(RepeatingWordsPanel.this.timeLabel + String.format("%.2f", new Object[] { Double.valueOf(RepeatingWordsPanel.this.timeElapsed) }));
          try
          {
            Thread.sleep((int)(RepeatingWordsPanel.this.interval * 1000));
          }
          catch (InterruptedException e)
          {
            RepeatingWordsPanel.this.parent.showMessageDialog(e.getMessage());
          }
        }
      }
    };
    this.timerThread = new Thread(runnable);
    this.timerThread.start();
  }
  
  private void stopTimer()
  {
    this.timerRunning = false;
  }
  
  public void setExcelReader(ExcelReader excel)
  {
    this.excel = excel;
  }
}