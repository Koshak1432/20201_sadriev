package brainfuck;

public class Program implements IProgram {

    Program(String commands) {
        commands_ = commands;
    }

    public boolean isEnd() {
        return commandIdx_ == commands_.length();
    }

    public void jumpTo(int idxToJump) {
        commandIdx_ = idxToJump;
    }

    public int getIdx() {
        return commandIdx_;
    }

    public char getSymbolAt(int idx) {
        if (idx < commands_.length()) {
            return commands_.charAt(idx);
        }
        else throw new IllegalArgumentException();

    }

    private final String commands_;
    private int commandIdx_ = 0;
}
