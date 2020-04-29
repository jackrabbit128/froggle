package jackrabbit128.froggle.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Board {
  private final int _width;
  private final int _height;
  private final Locale _language;
  private final List<Die> _sortedDice;

  private List<Die> _dice;

  public Board(int width, int height, Locale language, Collection<Die> dice) {
    _language = language;
    if (width * height != dice.size()) {
      throw new IllegalArgumentException("expected " + width * height + " dice, but got " + dice.size());
    }

    _sortedDice = new ArrayList<>(dice);
    _sortedDice.sort(Comparator.comparing(d -> String.join(",", d.options())));

    _width = width;
    _height = height;
    _dice = List.copyOf(_sortedDice);
  }

  public void shuffle(Random random) {
    var shuffledDice = new ArrayList<Die>(_sortedDice.size());
    var indices = IntStream.range(0, _sortedDice.size()).boxed().collect(Collectors.toList());
    while (!indices.isEmpty()) {
      var index = indices.remove(random.nextInt(indices.size()));
      shuffledDice.add(_sortedDice.get(index));
    }
    for (Die die : shuffledDice) {
      die.roll(random);
    }

    _dice = List.copyOf(shuffledDice);
  }

  public int getWidth() {
    return _width;
  }

  public int getHeight() {
    return _height;
  }

  public String read(int row, int column) {
    return _dice.get(row * _width + column).read();
  }

  public Locale getLanguage() {
    return _language;
  }

  @Override
  public String toString() {
    var builder = new StringBuilder();
    builder.append("Board (").append(_language.getDisplayLanguage()).append(")\n");
    for (int row = 0; row < _height; row++) {
      for (int column = 0; column < _width; column++) {
        builder.append(read(row, column)).append(' ');
      }
      if (builder.length() != 0) {
        builder.deleteCharAt(builder.length() - 1);
      }

      builder.append('\n');
    }
    if (builder.length() != 0) {
      builder.deleteCharAt(builder.length() - 1);
    }
    return builder.toString();
  }
}
