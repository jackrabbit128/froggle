package jackrabbit128.broggle.swing;

import jackrabbit128.broggle.AppInfo;
import jackrabbit128.broggle.model.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.QuitStrategy;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainWindow extends JFrame {
  private final BoardController _boardController;
  private final SettingsController _settingsController;

  public MainWindow() {
    super(AppInfo.getInstance().getAppName());

    Settings settings = new Settings();
    _boardController = new BoardController(settings);
    _settingsController = new SettingsController(settings);

    initLayout();
    initMenuBar();
    initBehavior();
  }

  private void initLayout() {
    setLayout(new BorderLayout());

    var boardPane = new BoardPane(_boardController);
    boardPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    add(boardPane, BorderLayout.CENTER);
  }

  private void initMenuBar() {
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
        desktop.setAboutHandler(event -> AboutPane.show(MainWindow.this, getTitle()));
      }
      if (desktop.isSupported(Desktop.Action.APP_PREFERENCES)) {
        desktop.setPreferencesHandler(event -> SettingsPane.show(MainWindow.this, _settingsController));
      }
      if (desktop.isSupported(Desktop.Action.APP_QUIT_STRATEGY)) {
        desktop.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
      }
      if (desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
        desktop.setQuitHandler((event, response) -> {
          if (_boardController.canShutdownSilently()) {
            response.performQuit();
            return;
          }

          int confirmation = JOptionPane.showConfirmDialog(MainWindow.this, "Game is in progress. Really quit?",
                                                           "Confirm Exit", JOptionPane.YES_NO_OPTION);
          if (confirmation == JOptionPane.YES_OPTION) {
            response.performQuit();
          } else {
            response.cancelQuit();
          }
        });
      }
    }
  }

  private void initBehavior() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        _boardController.onShutdown();
      }
    });
  }
}
