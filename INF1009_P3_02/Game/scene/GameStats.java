package INF1009_P3_02.Game.scene;

public class GameStats {

    private int score        = 0;
    private int correctCount = 0;
    private int wrongCount   = 0;

    public void recordCorrect() { correctCount++; score++; }
    public void recordWrong()   { wrongCount++;   score--; }

    public int getScore()        { return score; }
    public int getCorrectCount() { return correctCount; }
    public int getWrongCount()   { return wrongCount; }
}
