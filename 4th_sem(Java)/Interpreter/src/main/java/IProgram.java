public interface IProgram {
    boolean isEnd();
    void jumpTo(int idxToJump);
    int getIdx();
    char getChar();
}
