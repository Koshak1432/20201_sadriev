import java.io.InputStream;
import java.io.OutputStream;

public class Context {
    Context(InputStream inStream, OutputStream outStream) {
        inStream_ = inStream;
        outStream_ = outStream;
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
    public IIOController ioController_ = new IOController();
    private InputStream inStream_ = null;
    private OutputStream outStream_ = null; //get and set организовать для них
}

