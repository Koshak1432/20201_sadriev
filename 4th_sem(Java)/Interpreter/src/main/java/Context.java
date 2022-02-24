import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Context {
    Context(InputStream inStream, OutputStream outStream, IIOController ioController) {
        inStream_ = inStream;
        outStream_ = outStream;
        ioController_ = ioController;
    }

    public void setInStream(InputStream inStream) {
        inStream_ = inStream;
    }
    public InputStream getInStream() {
        return inStream_;
    }
    public OutputStream getOutStream() {
        return outStream_;
    }

    public IPointer pointer_ = new Pointer();

    public IIOController ioController_;
    private InputStream inStream_;
    private OutputStream outStream_;
}

