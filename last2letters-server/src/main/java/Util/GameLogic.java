package Util;

import java.util.Stack;

public class GameLogic {

    private final Stack<String> wordHist = new Stack<>();
    private Game turn = Game.PLAYER_ONE;
    private Game winner = Game.NONE;
    private boolean isGameEnded = false;
    private int minMatch = 2;

    public Game getTurn() {
        return turn;
    }

    public Game getWinner() {
        return winner;
    }

    public Stack<String> getWordHist() {
        return wordHist;
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }

    public void endGame() {
        isGameEnded = true;
    }

    public int getMinMatch() {
        return minMatch;
    }

    public void setMinMatch(int minMatch) {
        this.minMatch = minMatch;
    }

    public Game doAction(String word) {
        if (isGameEnded) {
            return winner;
        } else {
            if (isLegal(word)) {
                if (wordHist.isEmpty()) {
                    wordHist.add(word);
                    changeTurn();
                    return Game.NONE;
                } else {
                    if (isValid(word) && isViable(wordHist.peek(), word)) {
                        wordHist.add(word);
                        changeTurn();
                        return Game.NONE;
                    } else {
                        changeTurn();
                        winner = turn;
                        isGameEnded = true;
                        turn = Game.NONE;
                        return winner;
                    }
                }
            } else {
                changeTurn();
                winner = turn;
                isGameEnded = true;
                turn = Game.NONE;
                return winner;
            }
        }
    }

    public boolean isLegal(String str1) {
        if (str1.length() >= minMatch) {
            for (char c : str1.toCharArray()) {
                if (!Character.isLetter(c)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isValid(String str1) {
        return !wordHist.contains(str1);
    }

    public boolean isViable(String str1, String str2) {
        return str1.substring(str1.length() - minMatch).toUpperCase().equals(str2.substring(0, minMatch).toUpperCase());
    }

    private void changeTurn() {
        turn = (turn == Game.PLAYER_ONE) ? Game.PLAYER_TWO : Game.PLAYER_ONE;
    }

    public enum Game {
        NONE(0),
        PLAYER_ONE(1),
        PLAYER_TWO(2);

        public int num;

        Game(int num) {
            this.num = num;
        }
    }

}
