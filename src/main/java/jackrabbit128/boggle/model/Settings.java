package jackrabbit128.boggle.model;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public final class Settings {
  private static final Duration DEFAULT_GAME_DURATION = determineDefaultGameDuration();
  private static final Locale DEFAULT_BOARD_LANGUAGE = determineDefaultBoardLanguage();

  private Duration _gameDuration;
  private Locale _boardLanguage;

  private static Duration determineDefaultGameDuration() {
    long gameDurationSec = 90;
    try {
      String gameDurationSecProperty = System.getProperty("gameDurationSec", String.valueOf(gameDurationSec));
      gameDurationSec = Long.parseLong(gameDurationSecProperty);
    } catch (NumberFormatException e) {
      System.err.println("unable to parse system property 'gameDurationSec', using default value: " + gameDurationSec);
    }
    return Duration.of(gameDurationSec, ChronoUnit.SECONDS);
  }

  private static Locale determineDefaultBoardLanguage() {
    Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
    if (BoardFactory.getAvailableBoards().contains(locale)) {
      return locale;
    }

    return BoardFactory.getAvailableBoards().iterator().next();
  }

  public Settings() {
    _gameDuration = DEFAULT_GAME_DURATION;
    _boardLanguage = DEFAULT_BOARD_LANGUAGE;
  }

  public void copyFrom(Settings settings) {
    _gameDuration = settings.getGameDuration();
    _boardLanguage = settings.getBoardLanguage();
  }

  public static Duration getDefaultGameDuration() {
    return DEFAULT_GAME_DURATION;
  }

  public Duration getGameDuration() {
    return _gameDuration;
  }

  public void setGameDuration(Duration gameDuration) {
    _gameDuration = gameDuration;
  }

  public static Locale getDefaultBoardLanguage() {
    return DEFAULT_BOARD_LANGUAGE;
  }

  public Locale getBoardLanguage() {
    return _boardLanguage;
  }

  public void setBoardLanguage(Locale boardLanguage) {
    _boardLanguage = boardLanguage;
  }
}
