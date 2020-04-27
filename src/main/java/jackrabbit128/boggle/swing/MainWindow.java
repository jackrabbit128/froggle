package jackrabbit128.boggle.swing;

import jackrabbit128.boggle.AppInfo;
import jackrabbit128.boggle.model.Board;
import jackrabbit128.boggle.model.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainWindow extends JFrame {
  private final BoardController _boardController;
  private final SettingsController _settingsController;

  public MainWindow(Board board) {
    super(AppInfo.getInstance().getAppName());

    Settings settings = new Settings();

    _boardController = new BoardController(board);
    var boardPane = new BoardPane(_boardController);
    boardPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    _settingsController = new SettingsController(settings);

    add(boardPane, BorderLayout.CENTER);

    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
        desktop.setAboutHandler(event -> AboutPane.show(MainWindow.this, getTitle()));
      }
      if (desktop.isSupported(Desktop.Action.APP_PREFERENCES)) {
        desktop.setPreferencesHandler(event -> SettingsPane.show(MainWindow.this, _settingsController));
      }
    }

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        _boardController.shutdown();
      }
    });
    pack();
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setVisible(true);
  }
}
