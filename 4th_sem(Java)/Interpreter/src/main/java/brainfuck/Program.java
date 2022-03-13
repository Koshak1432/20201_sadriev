package brainfuck;

public class Program implements IProgram {

    public Program(String commands, char startLoop, char endLoop) {
        commands_ = commands;
        startLoop_ = startLoop;
        endLoop_ = endLoop;
    }

    public boolean isEnd() {
        return commandIdx_ >= commands_.length();
    }

    public void jumpTo(int idxToJump) throws IllegalArgumentException {
        if (idxToJump < 0) {
            throw new IllegalArgumentException("Tried to jump over(before) the commands string");
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

    public int findMatchingBracket(boolean forward) {
        int count = 1;
        int idx = commandIdx_;

        while (count > 0) {
            idx = (forward) ? idx + 1 : idx - 1;
            if (idx < 0 || idx >= commands_.length()) {
                throw new IllegalStateException("Couldn't find a matching bracket");
            }
            char symbol = getSymbolAt(idx);
            if (startLoop_ == symbol) {
                count = (forward) ? count + 1 : count - 1;
            } else if (endLoop_ == symbol) {
                count = (forward) ? count - 1 : count + 1;
            }
        }
        return idx;
    }
    private final String commands_;
    private int commandIdx_ = 0;
    private final char startLoop_;
    private final char endLoop_;
}
