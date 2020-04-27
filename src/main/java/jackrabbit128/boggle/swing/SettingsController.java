package jackrabbit128.boggle.swing;

import jackrabbit128.boggle.model.BoardFactory;
import jackrabbit128.boggle.model.Settings;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class SettingsController {
  private final Settings _settings;
  private final Settings _editSettings;

  public SettingsController(Settings settings) {
    _settings = settings;
    _editSettings = new Settings();
  }

  public Collection<Locale> getAvailableBoardLanguages() {
    return BoardFactory.getAvailableBoards();
  }

  public Locale getBoardLanguage() {
    return _editSettings.getBoardLanguage();
  }

  public void setBoardLanguage(Locale language) {
    _editSettings.setBoardLanguage(language);
  }

  public long getGameDurationSec() {
    return _editSettings.getGameDuration().toSeconds();
  }

  public long getGameDurationSecMinimum() {
    long minimumSec = 30;
    return Math.min(minimumSec, getGameDurationSec());
  }

  public long getGameDurationSecMaximum() {
    long maximumSec = TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES);
    return Math.max(maximumSec, getGameDurationSec());
  }

  public void setGameDurationSec(long durationSec) {
    _editSettings.setGameDuration(Duration.of(durationSec, ChronoUnit.SECONDS));
  }

  public void startEditing() {
    _editSettings.copyFrom(_settings);
  }

  public void commitEdit() {
    _settings.copyFrom(_editSettings);
  }

  public void cancelEdit() {
    _editSettings.copyFrom(_settings);
  }
}
