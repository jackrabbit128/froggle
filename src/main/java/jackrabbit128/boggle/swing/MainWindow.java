package jackrabbit128.boggle.swing;

import jackrabbit128.boggle.model.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainWindow extends JFrame {
  private final BoardController _controller;

  public MainWindow(Board board) {
    super("Boggle");

    _controller = new BoardController(board);
    var boardPane = new BoardPane(_controller);
    boardPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    add(boardPane, BorderLayout.CENTER);

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        _controller.shutdown();
      }
    });
    pack();
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setVisible(true);
  }
}
