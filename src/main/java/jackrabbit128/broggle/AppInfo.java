package jackrabbit128.broggle;

import jackrabbit128.broggle.io.ResourceLoader;

import java.io.IOException;
import java.util.Properties;

public final class AppInfo {
  private static AppInfo sInstance;

  private final String _appName;
  private final String _version;
  private final String _copyright;

  public static AppInfo getInstance() {
    if (sInstance == null) {
      sInstance = loadAppInfo();
    }

    return sInstance;
  }

  private static AppInfo loadAppInfo() {
    String appName = "Broggle";
    String version = "development";
    String copyright = "copyright by me";

    try {
      Properties properties = ResourceLoader.loadProperties(AppInfo.class, "/app-info.properties");
      appName = getPropertyOrDefault(properties, "appName", appName);
      version = getPropertyOrDefault(properties, "version", version);
      copyright = getPropertyOrDefault(properties, "copyright", copyright);
    } catch (IOException e) {
      System.err.println("Unable to load /app-info.properties");
    }

    return new AppInfo(appName, version, copyright);
  }

  private static String getPropertyOrDefault(Properties properties, String propertyName, String defaultValue) {
    String value = properties.getProperty(propertyName);
    if (value == null || value.contains("${")) {
      return defaultValue;
    }
    return value;
  }

  private AppInfo(String appName, String version, String copyright) {
    _appName = appName;
    _version = version;
    _copyright = copyright;
  }

  public String getAppName() {
    return _appName;
  }

  public String getVersion() {
    return _version;
  }

  public String getCopyright() {
    return _copyright;
  }
}
