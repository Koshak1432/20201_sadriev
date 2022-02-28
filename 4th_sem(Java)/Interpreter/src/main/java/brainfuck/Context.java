package brainfuck;

import java.io.InputStream;
import java.io.OutputStream;

public class Context {
    Context(InputStream inStream, OutputStream outStream, IIOController ioController,
            IProgram program, char startLoop, char endLoop) {
        inStream_ = inStream;
        outStream_ = outStream;
        ioController_ = ioController;
        program_ = program;
        startLoop_ = startLoop;
        endLoop_ = endLoop;
    }

    public InputStream getInStream() {
        return inStream_;
    }

    public OutputStream getOutStream() {
        return outStream_;
    }

    public IIOController getIoController() {
        return ioController_;
    }

    public IProgram getProgram() {
        return program_;
    }

    public IPointer getPointer() {
        return pointer_;
    }

    public char getStartLoop() {
        return startLoop_;
    }

    public char getEndLoop() {
        return endLoop_;
    }

    public int findMatchingBracket(Context ctx, boolean forward) {
        int count = 1;
        int idx = ctx.getProgram().getIdx();

        while (count > 0) {
            if (ctx.getProgram().isEnd()) {
                throw new IllegalStateException("Couldn't find matching bracket");
            }
            idx = (forward) ? idx + 1 : idx - 1;
            char symbol = ctx.getProgram().getSymbolAt(idx);
            if (ctx.getStartLoop() == symbol) {
                count = (forward) ? count + 1 : count - 1;
            }
            else if (ctx.getEndLoop() == symbol) {
                count = (forward) ? count - 1 : count + 1;
            }
        }
        return idx;
    }

    private final IPointer pointer_ = new Pointer();
    private final IProgram program_;
    private final IIOController ioController_;
    private final InputStream inStream_;
    private final OutputStream outStream_;
    private final char startLoop_;
    private final char endLoop_;
}

