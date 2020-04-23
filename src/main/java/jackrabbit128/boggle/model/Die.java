package jackrabbit128.boggle.model;

import java.util.Random;

public final class Die {
  final String _letters;

  String _letter;

  public Die(String letters) {
    if (letters.isBlank()) {
      throw new IllegalArgumentException("Die must have some letters, but was blank");
    }

    _letters = letters;
    _letter = _letters.substring(0, 1);
  }

  public void roll(Random random) {
    int index = random.nextInt(_letters.length());
    _letter = _letters.substring(index, index + 1);
  }

  public String read() {
    return _letter;
  }

  @Override
  public String toString() {
    return '[' + _letters + "]: " + _letter;
  }
}
