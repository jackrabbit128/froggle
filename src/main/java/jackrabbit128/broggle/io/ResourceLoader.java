package jackrabbit128.broggle.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ResourceLoader {
  private ResourceLoader() {
  }

  public static boolean resourceExists(Class<?> clazz, String path) {
    Path filePath = getFilePath(path);
    if (Files.isRegularFile(filePath) &&
        Files.isReadable(filePath)) {
      return true;
    }

    var resource = clazz.getResource(path);
    return resource != null;
  }

  public static Properties loadProperties(Class<?> clazz, String path) throws IOException {
    try (var in = openResource(clazz, path);
         var reader = new InputStreamReader(in, StandardCharsets.UTF_8);
         var buffered = new BufferedReader(reader)) {
      var properties = new Properties();
      properties.load(buffered);
      return properties;
    }
  }

  private static InputStream openResource(Class<?> clazz, String path) throws IOException {
    Path filePath = getFilePath(path);
    if (Files.isRegularFile(filePath) &&
        Files.isReadable(filePath)) {
      return Files.newInputStream(filePath);
    }

    var resource = clazz.getResource(path);
    if (resource == null) {
      throw new IOException("Unable to find resource " + path);
    }

    return clazz.getResourceAsStream(path);
  }

  private static Path getFilePath(String path) {
    return Path.of(path.startsWith("/") ? path.substring(1) : path);
  }
}
