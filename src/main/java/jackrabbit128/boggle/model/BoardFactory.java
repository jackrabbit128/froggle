package jackrabbit128.boggle.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public final class BoardFactory {
  public static Board createBoard(String language) throws IOException {
    Properties configuration = loadConfiguration("/dice_" + language + ".properties");

    return parseConfiguration(configuration);
  }

  private static Board parseConfiguration(Properties properties) throws IOException {
    var width = loadInt(properties, "width");
    var height = loadInt(properties, "height");

    var dice = new ArrayList<Die>();
    for (int i = 0; properties.containsKey("die" + i); ++i) {
      String letters = properties.getProperty("die" + i);
      dice.add(new Die(letters));
    }
    return new Board(width, height, dice);
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

  private static Properties loadConfiguration(String path) throws IOException {
    var resource = BoardFactory.class.getResource(path);
    if (resource == null) {
      throw new IllegalStateException("Unable to find resource " + path);
    }

    var properties = new Properties();
    try (var in = BoardFactory.class.getResourceAsStream(path)) {
      properties.load(in);
    }
    return properties;
  }
}
