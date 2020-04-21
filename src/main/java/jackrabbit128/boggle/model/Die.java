package jackrabbit128.boggle.model;

import java.util.Random;

public final class Die {
  final String _letters;

  char _letter;

  public Die(String letters) {
    if (letters.isBlank()) {
      throw new IllegalArgumentException("Die must have some letters, but was blank");
    }

    _letters = letters;
    _letter = _letters.charAt(0);
  }

  public void roll(Random random) {
    _letter = _letters.charAt(random.nextInt(_letters.length()));
  }

  public char read() {
    return _letter;
  }

  @Override
  public String toString() {
    return '[' + _letters + "]: " + _letter;
  }
}
