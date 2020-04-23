package jackrabbit128.boggle.model;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class Die {
  final List<String> _options;

  String _value;

  public Die(String letters) {
    if (letters.isBlank()) {
      throw new IllegalArgumentException("Die must have some letters, but was blank");
    }

    _options = List.copyOf(letters.chars()
                               .mapToObj(v -> String.valueOf((char) v))
                               .collect(Collectors.toList()));
    _value = _options.get(0);
  }

  public void roll(Random random) {
    int index = random.nextInt(_options.size());
    _value = _options.get(index);
  }

  public String read() {
    return _value;
  }

  public List<String> options() {
    return _options;
  }

  @Override
  public String toString() {
    return _options.stream().collect(Collectors.joining(", ", "[", "]")) + ": " + _value;
  }
}
