package jackrabbit128.broggle.swing;

import jackrabbit128.broggle.model.Board;
import jackrabbit128.broggle.model.BoardFactory;
import jackrabbit128.broggle.model.Settings;

import javax.swing.*;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public final class BoardController {
  public enum Status {
    READY,
    STARTED,
    FINISHED,
  }

  private final Settings _settings;

  private final Random _random;
  private long _lastSeed;
  private Status _status;
  private Board _board;

  private final Timer _timer;
  private Duration _gameDuration;
  private Instant _timerStart;

  private BoardView _view;

  public BoardController(Settings settings) {
    _settings = Objects.requireNonNull(settings, "settings");

    _lastSeed = getMinimumSeed() - 1;
    _random = new Random();
    _board = createBoard();
    _status = Status.READY;

    _timer = new Timer(200, e -> onTimerTick());
    _timer.setInitialDelay(0);
    _timer.setRepeats(true);
    _gameDuration = Settings.getDefaultGameDuration();

    _view = NoopView.INSTANCE;
  }

  private Board createBoard() {
    Locale language = _settings.getBoardLanguage();
    try {
      return BoardFactory.createBoard(language);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create board for language " + language.getDisplayLanguage(), e);
    }
  }

  public void setView(BoardView view) {
    _view = view == null ? NoopView.INSTANCE : view;

    _view.onStatusChanged(_status);
  }

  public int generateSeed() {
    int seed = getMinimumSeed() + _random.nextInt(getMaximumSeed() - getMinimumSeed());
    setSeed(seed);
    return seed;
  }

  public int getMinimumSeed() {
    return 0;
  }

  public int getMaximumSeed() {
    return 10_000;
  }

  public void setSeed(int seed) {
    if (seed == _lastSeed) {
      return;
    }

    _random.setSeed(seed);

    _view.onSeedChanged(seed);
  }

  public Board getBoard() {
    return _board;
  }

  public void advanceGame() {
    switch (_status) {
      case FINISHED:
        _view.onBoardHidden();
        generateSeed();
        _status = Status.READY;
        _view.onStatusChanged(_status);
        break;
      case READY:
        applySettings();
        shuffle();
        _view.onBoardShown();
        startTimer();
        _status = Status.STARTED;
        _view.onStatusChanged(_status);
        break;
      case STARTED:
        _status = Status.FINISHED;
        _view.onStatusChanged(_status);
        break;
    }
  }

  private void applySettings() {
    _gameDuration = _settings.getGameDuration();

    if (!_settings.getBoardLanguage().equals(_board.getLanguage())) {
      int oldWidth = _board.getWidth();
      int oldHeight = _board.getHeight();
      _board = createBoard();
      _view.onBoardLanguageChanged(_board);
    }
  }

  public void shuffle() {
    _board.shuffle(_random);

    _view.onBoardChanged(_board);
  }

  private void startTimer() {
    assert !_timer.isRunning();

    _timerStart = Instant.now();
    _timer.start();
  }

  private void onTimerTick() {
    var timePassed = Duration.between(_timerStart, Instant.now());
    var timeRemaining = _gameDuration.minus(timePassed);
    if (!timeRemaining.isNegative()) {
      _view.onRemainingTimeChanged(timeRemaining);
      return;
    }

    stopTimer();
    advanceGame();
  }

  private void stopTimer() {
    assert _timer.isRunning();

    _timer.stop();
  }

  public boolean canShutdownSilently() {
    return !_timer.isRunning();
  }

  public void onShutdown() {
    _timer.stop();
  }

  private static final class NoopView implements BoardView {
    public static final BoardView INSTANCE = new NoopView();

    private NoopView() {
    }

    @Override
    public void onSeedChanged(int seed) {
    }

    @Override
    public void onBoardLanguageChanged(Board board) {
    }

    @Override
    public void onBoardChanged(Board board) {
    }

    @Override
    public void onBoardHidden() {
    }

    @Override
    public void onBoardShown() {
    }

    @Override
    public void onStatusChanged(Status status) {
    }

    @Override
    public void onRemainingTimeChanged(Duration remainingTime) {
    }
  }
}
