package jackrabbit128.froggle.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Comparator;
import java.util.Locale;
import java.util.Vector;

import static javax.swing.GroupLayout.Alignment.BASELINE;

public final class SettingsPane extends JPanel {
  private final SettingsController _controller;

  private final JComboBox<Locale> _boardLanguagePicker;
  private final JSpinner _gameDurationSecField;

  public static void show(Component parent, SettingsController controller) {
    controller.startEditing();

    int option = JOptionPane.showConfirmDialog(parent, new SettingsPane(controller), "Preferences",
                                               JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (option == JOptionPane.OK_OPTION) {
      controller.commitEdit();
    } else {
      controller.cancelEdit();
    }
  }

  private SettingsPane(SettingsController controller) {
    _controller = controller;

    var boardLanguages = new Vector<>(_controller.getAvailableBoardLanguages());
    boardLanguages.sort(Comparator.comparing(SettingsPane::getDisplayValue));
    _boardLanguagePicker = new JComboBox<>(boardLanguages);
    _boardLanguagePicker.setSelectedItem(_controller.getBoardLanguage());

    _gameDurationSecField = new JSpinner(new SpinnerNumberModel(_controller.getGameDurationSec(),
                                                                _controller.getGameDurationSecMinimum(),
                                                                _controller.getGameDurationSecMaximum(),
                                                                10));

    initLayout();
    initBehavior();
  }

  private static String getDisplayValue(Locale locale) {
    return locale.getDisplayLanguage(locale);
  }

  private void initLayout() {
    var boardLanguageLabel = new JLabel("Board language");
    var gameDurationLabel = new JLabel("Game duration (seconds)");

    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);
    layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);

    GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
    hGroup.addGroup(layout.createParallelGroup()
                        .addComponent(boardLanguageLabel)
                        .addComponent(gameDurationLabel));
    hGroup.addGroup(layout.createParallelGroup()
                        .addComponent(_boardLanguagePicker)
                        .addComponent(_gameDurationSecField));
    layout.setHorizontalGroup(hGroup);

    GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
    vGroup.addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(boardLanguageLabel)
                        .addComponent(_boardLanguagePicker));
    vGroup.addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(gameDurationLabel)
                        .addComponent(_gameDurationSecField));
    layout.setVerticalGroup(vGroup);
  }

  private void initBehavior() {
    _boardLanguagePicker.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
        Object displayValue = value;
        if (value instanceof Locale) {
          displayValue = getDisplayValue((Locale) value);
        }
        return super.getListCellRendererComponent(list, displayValue, index, isSelected, cellHasFocus);
      }
    });
    _boardLanguagePicker.addItemListener(event -> {
      if (event.getStateChange() == ItemEvent.SELECTED) {
        _controller.setBoardLanguage((Locale) _boardLanguagePicker.getSelectedItem());
      }
    });

    _gameDurationSecField.addChangeListener(event -> {
      var value = _gameDurationSecField.getValue();
      _controller.setGameDurationSec(((Number) value).longValue());
    });
  }
}
