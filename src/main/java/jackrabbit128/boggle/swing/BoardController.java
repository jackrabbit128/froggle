package jackrabbit128.boggle.swing;

import jackrabbit128.boggle.model.Board;

import javax.swing.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class BoardController {
  private static final long GAME_DURATION;

  static {
    long gameDurationSec = 90;
    String gameDurationSecProperty = System.getProperty("gameDurationSec", String.valueOf(gameDurationSec));
    try {
      gameDurationSec = Long.parseLong(gameDurationSecProperty);
    } catch (NumberFormatException e) {
      System.err.println("unable to parse system property 'gameDurationSec', using default value: " + gameDurationSec);
    }
    GAME_DURATION = TimeUnit.MILLISECONDS.convert(gameDurationSec, TimeUnit.SECONDS);
  }

  public enum Status {
    READY,
    STARTED,
    FINISHED,
  }

  private final Board _board;
  private final Random _random;

  private Status _status;

  private final Timer _timer;
  private long _timerStartTime;

  private BoardView _view;

  public BoardController(Board board) {
    _board = Objects.requireNonNull(board, "board");
    _random = new Random();

    _view = NoopView.INSTANCE;
    _status = Status.READY;

    _timer = new Timer(200, e -> onTimerTick());
    _timer.setInitialDelay(0);
    _timer.setRepeats(true);
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
        _status = Status.READY;
        _view.onStatusChanged(_status);
        break;
      case READY:
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

  public void shuffle() {
    _board.shuffle(_random);

    _view.onBoardChanged(_board);
  }

  private void startTimer() {
    assert !_timer.isRunning();

    _timerStartTime = System.currentTimeMillis();
    _timer.start();
  }

  private void onTimerTick() {
    long remaining = (_timerStartTime + GAME_DURATION) - System.currentTimeMillis();
    if (remaining > 0) {
      _view.onRemainingTimeChanged(Duration.of(remaining, ChronoUnit.MILLIS));
      return;
    }

    stopTimer();
    advanceGame();
  }

  private void stopTimer() {
    assert _timer.isRunning();

    _timer.stop();
  }

  public void shutdown() {
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
