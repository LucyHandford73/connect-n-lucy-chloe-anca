package com.thg.accelerator23.connectn.ai.lucychloeanca;

import com.thehutgroup.accelerator.connectn.player.*;

import java.util.*;
import java.util.stream.IntStream;


public class ConnectyBot extends Player {
  public ConnectyBot(Counter counter) {
    //TODO: fill in your name here
    super(counter, ConnectyBot.class.getName());
  }

  @Override
  public int makeMove(Board board) {
    System.out.println("getting column");
    int position = 0;
    try {
      position = getOptimalCol(board);
    } catch (InvalidMoveException e) {
      throw new RuntimeException(e);
    }
    //TODO: some crazy analysis
    //TODO: make sure said analysis uses less than 2G of heap and returns within 10 seconds on whichever machine is running it
    return position;
  }


  private int randomMove(Board board) {
    int position = new Random().nextInt(0, 10);
    if (!isSpaceAvailable(board, position)){
      position = randomMove(board);
    }
    return position;
  }

  private boolean isSpaceAvailable(Board board, int position){
    for (int i = 0; i < board.getConfig().getHeight(); i++){
      if (!board.hasCounterAtPosition(new Position(position, i))){
        return true;
      }
    }
    return false;
  }

  private List<Integer> getAvailableCol(Board board){
    List<Integer> availableCol = new ArrayList<>();
    for (int col = 0; col<board.getConfig().getWidth(); col++){
      if (this.isSpaceAvailable(board, col)){
        availableCol.add(col);
      }
    }
    return availableCol;
  }


  private int columnScoreCalculator(Board board, int columnNumber){
    int score = 0;
    GameConfig newConfig = board.getConfig();
    LCABoardAnalyser boardAnalyser = new LCABoardAnalyser(newConfig);
    if (boardAnalyser.calculateGameState(board).isEnd()) {
      Counter winner = boardAnalyser.calculateGameState(board).getWinner();
      if (winner.equals(this.getCounter())) {
        score = 1;
      }else {
        score = -1;
      }
    } else {
      score = 0;
    }

    return score;
  }

  private int miniMax (int position, int depth, boolean maximisingPlayer, Board board) throws InvalidMoveException {
    boolean gameOver = new LCABoardAnalyser(board.getConfig()).calculateGameState(board).isEnd();
    Counter counter = Counter.O;
    if (maximisingPlayer){
      counter = this.getCounter();
    }
    else {
      switch (this.getCounter()){
      case O -> counter = Counter.X;
        case X -> counter = Counter.O;
    }}
    Board possibleNewBoard = new Board(board, position, counter);
    if (depth == 0 || gameOver) {
      return columnScoreCalculator(possibleNewBoard, position);
    }
    List<Integer> availableCol = getAvailableCol(possibleNewBoard);
    if (maximisingPlayer){
      int maxEval = -1000000;
      for (int column:availableCol
           ) {
        int eval = miniMax(column , depth - 1, false, possibleNewBoard);
        maxEval = Math.max(maxEval, eval);
      }
      return maxEval;
    } else {
      int minEval = 1000000;
      for (int column:availableCol
      ) {
        int eval = miniMax(column, depth -1, true, possibleNewBoard);
        minEval = Math.min(minEval, eval);
      }
      return minEval;
    }
  }

  private Map<Integer, Integer> getAllColScores(Board board) throws InvalidMoveException {
    Map<Integer, Integer> colScores = new HashMap<>();
    List<Integer> availableCol = getAvailableCol(board);
    for (int colNumber: availableCol
         ) {
      int score = miniMax(colNumber, 4, true, board);
      colScores.put(colNumber, score);
    }
    return colScores;
  }

  private int getOptimalCol(Board board) throws InvalidMoveException {
    Map<Integer, Integer> colScores = getAllColScores(board);
    System.out.println(colScores);
    int highestScoringCol = colScores.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
    int highestScore = colScores.get(highestScoringCol);
    if (highestScore>0) {
      System.out.println("hi");
      System.out.println(highestScore);
      System.out.println(highestScoringCol);
      return highestScoringCol;
    } else if (highestScore == -1) {
      System.out.println("hi");
      System.out.println(highestScore);
      System.out.println(highestScoringCol);
      return highestScoringCol;
    } else {
      System.out.println("random");
      return randomMove(board);
    }
  }
}