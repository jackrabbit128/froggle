package jackrabbit128.boggle;

import jackrabbit128.boggle.model.BoardFactory;
import jackrabbit128.boggle.swing.MainWindow;

import java.io.IOException;

public final class App {
  public static void main(String[] args) throws IOException {
    var board = BoardFactory.createBoard("nl");

    new MainWindow(board);
  }
}
