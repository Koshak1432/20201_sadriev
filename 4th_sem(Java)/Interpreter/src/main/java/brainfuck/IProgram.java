package brainfuck;

public interface IProgram {
    boolean isEnd();
    void jumpTo(int idxToJump);
    int getIdx();
    char getSymbolAt(int idx);
    int findMatchingBracket(boolean forward);
}
