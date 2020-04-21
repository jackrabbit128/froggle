package jackrabbit128.boggle.swing;

import jackrabbit128.boggle.model.Board;

import java.time.Duration;

public interface BoardView {
  void onSeedChanged(int seed);

  void onBoardChanged(Board board);

  void onBoardHidden();

  void onBoardShown();

  void onStatusChanged(BoardController.Status status);

  void onRemainingTimeChanged(Duration remainingTime);
}
