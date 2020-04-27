package jackrabbit128.boggle.swing;

import jackrabbit128.boggle.model.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BoardPane extends JPanel {
  private static final int HGAP = 4;
  private static final int VGAP = 4;

  private final BoardController _controller;

  private final JButton _readyGoButton;
  private final JLabel _languageLabel;
  private final JButton _genSeedButton;
  private final JSpinner _seedField;

  private final JPanel _dicePanel;
  private final List<JLabel> _diceLabels;

  private final JLabel _statusLabel;

  public BoardPane(BoardController controller) {
    _controller = controller;

    _readyGoButton = new JButton("Ready");
    _languageLabel = new JLabel("Language");
    _genSeedButton = new JButton("New Seed");
    _seedField = new JSpinner(new SpinnerNumberModel(_controller.generateSeed(),
                                                     _controller.getMinimumSeed(), _controller.getMaximumSeed(),
                                                     1));
    _seedField.setPreferredSize(new Dimension(100, _seedField.getPreferredSize().height));

    _dicePanel = new JPanel(new GridLayout(1, 1, HGAP, VGAP));
    _diceLabels = new ArrayList<>();

    _statusLabel = new JLabel("Press Start to play!");

    initLayout();
    initBehavior();
    applyBoardStructure();
  }

  private void initLayout() {
    setLayout(new BorderLayout(HGAP, VGAP));

    JToolBar topPanel = new JToolBar(SwingConstants.HORIZONTAL);
    topPanel.setFloatable(false);
    topPanel.add(_readyGoButton);
    topPanel.add(Box.createHorizontalStrut(HGAP));
    topPanel.add(_languageLabel);
    topPanel.add(Box.createHorizontalGlue());
    topPanel.add(_genSeedButton);
    topPanel.add(_seedField);
    add(topPanel, BorderLayout.NORTH);

    add(_dicePanel, BorderLayout.CENTER);

    var bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    bottomPanel.add(_statusLabel);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  private void initBehavior() {
    _readyGoButton.addActionListener(e -> _controller.advanceGame());
    _genSeedButton.addActionListener(e -> _controller.generateSeed());
    _seedField.addChangeListener(e -> setSeed());
    ((JSpinner.DefaultEditor) _seedField.getEditor()).getTextField().addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        setSeed();
      }
    });

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        adjustFontSizes();
      }
    });

    _controller.setView(new BoardView() {
      @Override
      public void onSeedChanged(int seed) {
        _seedField.setValue(seed);
      }

      @Override
      public void onBoardLanguageChanged(Board board) {
        applyBoardStructure();
      }

      @Override
      public void onBoardChanged(Board board) {
        int width = board.getWidth();
        for (int i = 0; i < _diceLabels.size(); i++) {
          var value = board.read(i / width, i % width);
          JLabel dieLabel = _diceLabels.get(i);
          dieLabel.setText(value);
        }
        adjustFontSizes();
      }

      @Override
      public void onBoardHidden() {
        for (JLabel dieLabel : _diceLabels) {
          dieLabel.setText(" ");
        }
      }

      @Override
      public void onBoardShown() {
        onBoardChanged(_controller.getBoard());
      }

      @Override
      public void onStatusChanged(BoardController.Status status) {
        switch (status) {
          case READY:
            setControlsEnabled(true, true);
            _readyGoButton.setText("Start");
            _statusLabel.setText("Press Start to play!");
            break;
          case STARTED:
            setControlsEnabled(false, false);
            break;
          case FINISHED:
            Toolkit.getDefaultToolkit().beep();
            setControlsEnabled(true, false);
            _readyGoButton.setText("Next");
            _statusLabel.setText("Press Next to start over.");
            break;
        }
      }

      @Override
      public void onRemainingTimeChanged(Duration remainingTime) {
        var text = String.format("%d:%02d", remainingTime.toMinutes(), remainingTime.toSecondsPart());
        _statusLabel.setText(text);
      }
    });
  }

  private void applyBoardStructure() {
    _dicePanel.removeAll();
    _diceLabels.clear();

    int width = _controller.getBoard().getWidth();
    int height = _controller.getBoard().getHeight();
    _dicePanel.setLayout(new GridLayout(height, width, HGAP, VGAP));
    var gridColor = _dicePanel.getBackground().darker();
    for (int i = 0; i < width * height; i++) {
      JLabel dieLabel = new JLabel();
      dieLabel.setHorizontalAlignment(SwingConstants.CENTER);
      dieLabel.setVerticalAlignment(SwingConstants.CENTER);
      dieLabel.setBorder(BorderFactory.createLineBorder(gridColor, 1, true));
      dieLabel.setPreferredSize(new Dimension(100, 100));
      _diceLabels.add(dieLabel);
      _dicePanel.add(dieLabel);
    }

    // We need this to happen immediately because otherwise adjustFontSizes
    // does not get correct component sizes. All other invalidation methods
    // recompute the layout only on the next repaint.
    _dicePanel.validate();

    Locale locale = _controller.getBoard().getLanguage();
    _languageLabel.setText(locale.getDisplayLanguage(locale));
  }

  private void adjustFontSizes() {
    for (JLabel dieLabel : _diceLabels) {
      Dimension size = dieLabel.getSize();
      Insets insets = dieLabel.getInsets();
      var available = new Dimension((int) Math.floor(size.getWidth() - insets.left - insets.right - 2),
                                    (int) Math.floor(size.getHeight() - insets.top - insets.bottom - 2));

      var oldFont = dieLabel.getFont();
      var metrics = dieLabel.getFontMetrics(oldFont);
      var needed = metrics.getStringBounds(dieLabel.getText(), dieLabel.getGraphics());
      var factor = Math.min(available.getWidth() / needed.getWidth(),
                            available.getHeight() / needed.getHeight());

      float newSize = (int) Math.floor(oldFont.getSize2D() * factor);
      Font font;
      do {
        if (newSize < 10) {
          return;
        }

        font = oldFont.deriveFont(newSize--);
        metrics = dieLabel.getFontMetrics(font);
        needed = metrics.getStringBounds(dieLabel.getText(), dieLabel.getGraphics());
      } while (needed.getWidth() > available.getWidth() ||
               needed.getHeight() > available.getHeight());

      dieLabel.setFont(font);
    }
  }

  private void setSeed() {
    _controller.setSeed(((Number) _seedField.getValue()).intValue());
  }

  private void setControlsEnabled(boolean readyGoEnabled, boolean seedEnabled) {
    _readyGoButton.setEnabled(readyGoEnabled);

    _seedField.setEnabled(seedEnabled);
    _genSeedButton.setEnabled(seedEnabled);
  }
}
