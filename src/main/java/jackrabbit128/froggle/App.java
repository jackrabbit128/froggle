package jackrabbit128.froggle;

import jackrabbit128.froggle.swing.MainWindow;

public final class App {
  public static void main(String[] args) {
    var window = new MainWindow();

    window.pack();
    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }
}
