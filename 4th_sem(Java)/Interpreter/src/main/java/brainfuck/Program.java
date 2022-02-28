package brainfuck;

public class Program implements IProgram {

    Program(String commands) {
        commands_ = commands;
    }

    public boolean isEnd() {
        return commandIdx_ >= commands_.length();
    }

    public void jumpTo(int idxToJump) throws IllegalArgumentException {
        if (idxToJump < 0 || idxToJump >= commands_.length()) {
            throw new IllegalArgumentException("Tried to jump over the commands string");
        }
        commandIdx_ = idxToJump;
    }

    public int getIdx() {
        return commandIdx_;
    }

    public char getSymbolAt(int idx) throws IllegalArgumentException {
        if (idx < 0 || idx >= commands_.length()) {
            throw new IllegalArgumentException("Tried to get symbol at illegal idx");
        }
        return commands_.charAt(idx);
    }

    private final String commands_;
    private int commandIdx_ = 0;
}
