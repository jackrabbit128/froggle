package jackrabbit128.froggle.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BoardTest {
  private Board _board;
  private Random _random;

  @BeforeEach
  public void setUp() {
    _board = new Board(2, 2, Locale.getDefault(), List.of(
        new Die(List.of("A", "B", "C", "D")),
        new Die(List.of("E", "F", "G", "H")),
        new Die(List.of("I", "J", "K", "L")),
        new Die(List.of("M", "N", "O", "P"))
    ));

    _random = new Random();
  }

  @Test
  public void shuffleWithSameSeedYieldsSameResult() {
    long seed = 1;
    Supplier<String> shuffleAndReadBoard = () -> {
      _random.setSeed(seed);
      _board.shuffle(_random);
      return _board.toString();
    };

    var one = shuffleAndReadBoard.get();
    var two = shuffleAndReadBoard.get();
    assertEquals(one, two);
  }

  @Test
  public void shuffleWithoutSeedingYieldsDifferentResult() {
    _random.setSeed(1);
    Supplier<String> shuffleAndReadBoard = () -> {
      _board.shuffle(_random);
      return _board.toString();
    };

    var one = shuffleAndReadBoard.get();
    var two = shuffleAndReadBoard.get();
    assertNotEquals(one, two);
  }
}
