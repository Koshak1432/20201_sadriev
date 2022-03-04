package brainfuck;

import java.io.InputStream;
import java.io.OutputStream;

public class Context {
    Context(InputStream inStream, OutputStream outStream, IIOController ioController, IProgram program) {
        inStream_ = inStream;
        outStream_ = outStream;
        ioController_ = ioController;
        program_ = program;
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

    private final IPointer pointer_ = new Pointer();
    private final IProgram program_;
    private final IIOController ioController_;
    private final InputStream inStream_;
    private final OutputStream outStream_;

}

