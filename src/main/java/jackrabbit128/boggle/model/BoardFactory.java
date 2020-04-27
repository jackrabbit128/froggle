package jackrabbit128.boggle.model;

import jackrabbit128.boggle.io.ResourceLoader;

import java.io.IOException;
import java.util.*;

public final class BoardFactory {
  private static final Collection<Locale> AVAILABLE_BOARDS = detectAvailableBoards();

  private BoardFactory() {
  }

  private static List<Locale> detectAvailableBoards() {
    Map<String, Locale> available = new HashMap<>();
    for (Locale locale : Locale.getAvailableLocales()) {
      if (ResourceLoader.resourceExists(BoardFactory.class, getBoardResourcePath(locale))) {
        available.merge(locale.getLanguage(), locale,
                        (theOld, theNew) -> theNew.getCountry().isEmpty() ? theNew : theOld);
      }
    }
    return List.copyOf(available.values());
  }

  private static String getBoardResourcePath(Locale locale) {
    return "/config/dice_" + locale.getLanguage() + ".properties";
  }

  public static Collection<Locale> getAvailableBoards() {
    return AVAILABLE_BOARDS;
  }

  public static Board createBoard(Locale locale) throws IOException {
    String path = getBoardResourcePath(locale);
    Properties configuration = ResourceLoader.loadProperties(BoardFactory.class, path);

    var width = loadInt(configuration, "width");
    var height = loadInt(configuration, "height");
    var dice = new ArrayList<Die>();
    for (int i = 0; configuration.containsKey("die" + i); ++i) {
      var value = configuration.getProperty("die" + i);
      var options = List.of(value.split(","));
      dice.add(new Die(options));
    }
    return new Board(width, height, locale, dice);
  }

  private static int loadInt(Properties properties, String key) throws IOException {
    var value = properties.getProperty(key);
    if (value == null) {
      throw new IOException("Expected board configuration to contain " + key);
    }

    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new IOException("Expected value of " + key + " to be an int, but was: " + value);
    }
  }
}
