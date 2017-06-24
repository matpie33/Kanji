package com.kanji.windows;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.kanji.actions.CommonActionsMaker;
import com.kanji.constants.Titles;
import com.kanji.panels.ConfirmPanel;
import com.kanji.panels.KanjiPanel;
import com.kanji.panels.MessagePanel;
import com.kanji.panels.PanelCreator;
import com.kanji.panels.ProblematicKanjiPanel;

public class DialogWindow {

	protected DialogWindow childWindow;
	private JPanel mainPanel;
	private DialogWindow parentWindow;
	private boolean isAccepted;
	private Position position;
	private JDialog container;
	private KanjiPanel kanjiPanel;

	public enum Position {
		CENTER, LEFT_CORNER, NEXT_TO_PARENT
	}

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			return false;
		}
	}

	public DialogWindow(DialogWindow b) {
		if (b instanceof ApplicationWindow) {
			ApplicationWindow w = (ApplicationWindow) b;
			container = new JDialog(w.getContainer());
		}
		else if (b != null && b.getContainer() != null) {
			container = new JDialog(b.getContainer());
		}
		else {
			container = new JDialog();
		}
		container.setAutoRequestFocus(true);
		parentWindow = b;
		initialize();
	}

	private void initialize() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());
	}

	public void setPanel(JPanel panel) {
		mainPanel = panel;
	}

	public void showYourself(String title) {
		showYourself(title, false);
	}

	public void showYourself(String title, boolean modal) {
		container.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		container.setContentPane(mainPanel);
		CommonActionsMaker.addHotkey(KeyEvent.VK_ESCAPE,
				CommonActionsMaker.createDisposeAction(this), mainPanel.getRootPane());
		container.pack();
		setCoordinatesBasedOnPosition();
		container.setModal(modal);
		container.setTitle(title);
		container.setVisible(true);
	}

	private void setCoordinatesBasedOnPosition() {
		switch (position) {
		case CENTER:
			container.setLocationRelativeTo(parentWindow.getContainer());
			break;
		case LEFT_CORNER:
			container.setLocation(parentWindow.getContainer().getLocation());
			break;

		case NEXT_TO_PARENT:
			setChildNextToParent(parentWindow.getContainer(), container);
			// Point parentLocation =
			// parentWindow.getContainer().getLocationOnScreen();
			// Dimension parentSize = parentWindow.getContainer().getSize();
			// container.setLocation(parentLocation.x + parentSize.width,
			// parentLocation.y);
			break;
		}
	}

	private void setChildNextToParent(Window parentContainer, Window childContainer) {
		Point parentLocation = parentContainer.getLocationOnScreen();
		Dimension parentSize = parentContainer.getSize();
		childContainer.setLocation(parentLocation.x + parentSize.width, parentLocation.y);
	}

	public void showKanjiDialog(String message, ProblematicKanjiPanel problematicKanjiPanel) {
		if (kanjiPanel == null) {
			kanjiPanel = new KanjiPanel(message, problematicKanjiPanel);
			showPanel(kanjiPanel, "TODO", false, Position.NEXT_TO_PARENT);
			childWindow.getContainer().addWindowListener(new WindowAdapter() {
				// TODO init the listeners somewhere else
				@Override
				public void windowClosed(WindowEvent e) {
					kanjiPanel = null;
				}
			});
			container.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentMoved(ComponentEvent e) {
					setChildNextToParent(container, childWindow.getContainer());
				}
			});
		}
		else {
			kanjiPanel.changeKanji(message);
		}

	}

	public void showMsgDialog(String message) {
		showPanel(new MessagePanel(message), Titles.messageDialogTitle, true, Position.CENTER);
	}

	public void showPanel(PanelCreator panel, String title, boolean modal, Position position,
			boolean closeOnEscape) {
		// TODO use this now for each dialog that closes on escape, remove
		// buttons mapping to escape key

		if (childWindowIsClosed()) {
			childWindow = new DialogWindow(this);
			panel.setParentDialog(childWindow);
			childWindow.setPosition(position);
			JPanel panell = panel.createPanel();
			childWindow.setPanel(panell);
			childWindow.showYourself(title, modal);
			if (closeOnEscape) {
				CommonActionsMaker.addHotkey(KeyEvent.VK_ESCAPE,
						CommonActionsMaker.createDisposeAction(childWindow), panell.getRootPane());
			}
		}
	}

	public void showPanel(PanelCreator panel, String title, boolean modal, Position position) {
		showPanel(panel, title, modal, position, false);
	}

	private boolean childWindowIsClosed() {
		return childWindow == null || !childWindow.getContainer().isVisible();
	}

	public boolean showConfirmDialog(String message) {
		showPanel(new ConfirmPanel(message), Titles.confirmDialogTitle, true, Position.CENTER);
		return isAccepted();
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void save() { // TODO this should go to application window to avoid
							// cast
		if (parentWindow instanceof ApplicationWindow) {
			ApplicationWindow parent = (ApplicationWindow) parentWindow;
			parent.save();
		}
	}

	public void setAccepted(boolean accepted) {
		this.isAccepted = accepted;
	}

	public boolean isAccepted() {
		return childWindow.isAccepted;
	}

	public Window getContainer() {
		return container;
	}

	public void addHotkeyToWindow(int keyEvent, AbstractAction a) {
		CommonActionsMaker.addHotkey(keyEvent, a, container.getRootPane());
	}

	public DialogWindow getParent() {
		return parentWindow;
	}

	public void closeChild() {
		childWindow.getContainer().dispose();
	}

}