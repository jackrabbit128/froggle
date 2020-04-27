package jackrabbit128.broggle.model;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class Die {
  final List<String> _options;

  String _value;

  public Die(Collection<String> options) {
    if (options.isEmpty()) {
      throw new IllegalArgumentException("Die must have some options, but was empty");
    }

    _options = List.copyOf(options);
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
