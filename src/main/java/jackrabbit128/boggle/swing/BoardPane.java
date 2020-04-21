package jackrabbit128.boggle.swing;

import jackrabbit128.boggle.model.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class BoardPane extends JPanel {
  private static final int HGAP = 4;
  private static final int VGAP = 4;

  private final BoardController _controller;

  private final JButton _readyGoButton;
  private final JButton _genSeedButton;
  private final JSpinner _seedField;

  private final List<JLabel> _dieLabels;

  private final JLabel _statusLabel;

  public BoardPane(BoardController controller) {
    _controller = controller;

    _readyGoButton = new JButton("Ready");
    _genSeedButton = new JButton("New Seed");
    _seedField = new JSpinner(new SpinnerNumberModel(_controller.generateSeed(),
                                                     _controller.getMinimumSeed(), _controller.getMaximumSeed(),
                                                     1));

    int width = _controller.getBoard().getWidth();
    int height = _controller.getBoard().getHeight();
    _dieLabels = new ArrayList<>(width * height);
    for (int i = 0; i < width * height; i++) {
      _dieLabels.add(new JLabel());
    }

    _statusLabel = new JLabel("Press Start to play!");

    initLayout();
    initBehavior();
  }

  private void initLayout() {
    setLayout(new BorderLayout(HGAP, VGAP));

    var topPanel = new JToolBar(JToolBar.HORIZONTAL);
    topPanel.setFloatable(false);
    topPanel.add(_readyGoButton);
    topPanel.add(Box.createHorizontalGlue());
    topPanel.add(_genSeedButton);
    topPanel.add(_seedField);
    add(topPanel, BorderLayout.NORTH);

    int height = _controller.getBoard().getHeight();
    int width = _controller.getBoard().getWidth();
    var boardPanel = new JPanel(new GridLayout(height, width, HGAP, VGAP));
    Color gridColor = boardPanel.getBackground().darker();
    for (JLabel dieLabel : _dieLabels) {
      dieLabel.setHorizontalAlignment(SwingConstants.CENTER);
      dieLabel.setVerticalAlignment(SwingConstants.CENTER);
      dieLabel.setBorder(BorderFactory.createLineBorder(gridColor, 1, true));
      dieLabel.setPreferredSize(new Dimension(100, 100));
      boardPanel.add(dieLabel);
    }
    add(boardPanel, BorderLayout.CENTER);

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
      public void onBoardChanged(Board board) {
        int width = board.getWidth();
        for (int i = 0; i < _dieLabels.size(); i++) {
          var value = board.read(i / width, i % width);
          JLabel dieLabel = _dieLabels.get(i);
          dieLabel.setText(String.valueOf(value));
        }
      }

      @Override
      public void onBoardHidden() {
        for (JLabel dieLabel : _dieLabels) {
          dieLabel.setText("?");
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
        String text = DateTimeFormatter.ISO_LOCAL_TIME.format(remainingTime.addTo(LocalTime.MIDNIGHT));
        _statusLabel.setText(text);
      }
    });
  }

  private void adjustFontSizes() {
    JLabel prototype = _dieLabels.get(0);
    Dimension available = prototype.getSize();

    Font oldFont = prototype.getFont();
    FontMetrics metrics = prototype.getFontMetrics(oldFont);
    Rectangle2D needed = metrics.getStringBounds("M", prototype.getGraphics());
    double factor = Math.min(available.getWidth() / needed.getWidth(),
                             available.getHeight() / needed.getHeight());

    float newSize = (int) Math.floor(oldFont.getSize2D() * factor);
    Font font;
    do {
      if (newSize < 10) {
        return;
      }

      font = oldFont.deriveFont(newSize--);
      metrics = prototype.getFontMetrics(font);
      needed = metrics.getStringBounds("M", prototype.getGraphics());
    } while (needed.getWidth() > available.getWidth() ||
             needed.getHeight() > available.getHeight());

    for (JLabel dieLabel : _dieLabels) {
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
