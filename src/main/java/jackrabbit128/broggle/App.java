package jackrabbit128.broggle;

import jackrabbit128.broggle.swing.MainWindow;

public final class App {
  public static void main(String[] args) {
    var window = new MainWindow();

    window.pack();
    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }
}
