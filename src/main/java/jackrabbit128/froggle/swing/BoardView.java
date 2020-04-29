package jackrabbit128.froggle.swing;

import jackrabbit128.froggle.model.Board;

import java.time.Duration;

public interface BoardView {
  void onSeedChanged(int seed);

  void onBoardLanguageChanged(Board board);

  void onBoardChanged(Board board);

  void onBoardHidden();

  void onBoardShown();

  void onStatusChanged(BoardController.Status status);

  void onRemainingTimeChanged(Duration remainingTime);
}
