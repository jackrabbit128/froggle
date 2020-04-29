package jackrabbit128.froggle.swing;

import jackrabbit128.froggle.AppInfo;
import jackrabbit128.froggle.model.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.QuitStrategy;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainWindow extends JFrame {
  private final BoardController _boardController;
  private final SettingsController _settingsController;

  private boolean _didConfirmExit;

  public MainWindow() {
    super(AppInfo.getInstance().getAppName());

    Settings settings = new Settings();
    _boardController = new BoardController(settings);
    _settingsController = new SettingsController(settings);

    _didConfirmExit = false;

    boolean fullyIntegrated = initDesktopIntegration();
    initLayout(!fullyIntegrated);
    initBehavior();
  }

  private void initLayout(boolean addMenuBar) {
    setLayout(new BorderLayout());

    if (addMenuBar) {
      add(createMenuBar(), BorderLayout.NORTH);
    }

    var boardPane = new BoardPane(_boardController);
    boardPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    add(boardPane, BorderLayout.CENTER);
  }

  @SuppressWarnings("ConstantConditions")
  private boolean initDesktopIntegration() {
    if (!Desktop.isDesktopSupported()) {
      return false;
    }

    Desktop desktop = Desktop.getDesktop();
    boolean allSupported = true;
    if (allSupported &= desktop.isSupported(Desktop.Action.APP_ABOUT)) {
      desktop.setAboutHandler(event -> showAboutDialog());
    }
    if (allSupported &= desktop.isSupported(Desktop.Action.APP_PREFERENCES)) {
      desktop.setPreferencesHandler(event -> showSettingsDialog());
    }
    if (allSupported &= desktop.isSupported(Desktop.Action.APP_QUIT_STRATEGY)) {
      desktop.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
    }
    if (allSupported &= desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
      desktop.setQuitHandler((event, response) -> {
        if (confirmExit()) {
          response.performQuit();
        } else {
          response.cancelQuit();
        }
      });
    }
    return allSupported;
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    JMenu appMenu = new JMenu(getTitle());
    appMenu.add(createMenuItem("About", event -> showAboutDialog()));
    appMenu.add(createMenuItem("Preferences", event -> showSettingsDialog()));
    appMenu.addSeparator();
    appMenu.add(createMenuItem("Exit", event -> attemptExit()));
    menuBar.add(appMenu);

    return menuBar;
  }

  private static JMenuItem createMenuItem(String title, ActionListener handler) {
    JMenuItem menuItem = new JMenuItem(title);
    menuItem.addActionListener(handler);
    return menuItem;
  }

  private void initBehavior() {
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        attemptExit();
      }

      @Override
      public void windowClosed(WindowEvent e) {
        _boardController.onShutdown();
      }
    });
  }

  private void showAboutDialog() {
    AboutPane.show(this, getTitle());
  }

  private void showSettingsDialog() {
    SettingsPane.show(this, _settingsController);
  }

  private void attemptExit() {
    if (confirmExit()) {
      for (Window window : Window.getWindows()) {
        window.dispose();
      }
    }
  }

  private boolean confirmExit() {
    if (_didConfirmExit || _boardController.canShutdownSilently()) {
      return true;
    }

    int confirmation = JOptionPane.showConfirmDialog(MainWindow.this, "Game is in progress. Really quit?",
                                                     "Confirm Exit", JOptionPane.YES_NO_OPTION);
    _didConfirmExit = confirmation == JOptionPane.YES_OPTION;
    return _didConfirmExit;
  }
}
