package jackrabbit128.boggle.model;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public final class Settings {
  private static final Duration DEFAULT_GAME_DURATION = determineDefaultGameDuration();
  private static final Locale DEFAULT_BOARD_LANGUAGE = determineDefaultBoardLanguage();

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

  public static Duration getDefaultGameDuration() {
    return DEFAULT_GAME_DURATION;
  }

  public static Locale getDefaultBoardLanguage() {
    return DEFAULT_BOARD_LANGUAGE;
  }
}
