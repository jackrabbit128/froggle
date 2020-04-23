package jackrabbit128.boggle.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Board {
  private final int _width;
  private final int _height;

  List<Die> _dice;

  public Board(int width, int height, Collection<Die> dice) {
    if (width * height != dice.size()) {
      throw new IllegalArgumentException("expected " + width * height + " dice, but got " + dice.size());
    }

    var sortedDice = new ArrayList<>(dice);
    sortedDice.sort(Comparator.comparing(d -> String.join(",", d.options())));

    _width = width;
    _height = height;
    _dice = List.copyOf(sortedDice);
  }

  public void shuffle(Random random) {
    var shuffledDice = new ArrayList<Die>(_dice.size());
    var indices = IntStream.range(0, _dice.size()).boxed().collect(Collectors.toList());
    while (!indices.isEmpty()) {
      var index = indices.remove(random.nextInt(indices.size()));
      shuffledDice.add(_dice.get(index));
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

  @Override
  public String toString() {
    var builder = new StringBuilder();
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
