import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public class IOController implements IIOController {
    @Override
    public void parseArguments(InputStream stream) {

    }

    @Override
    public Collection<Character> readCommands(InputStream stream) {

        return null;
    }

    @Override
    public byte readByte(InputStream stream) {
        return 0;
    }

    @Override
    public void print(OutputStream stream, Character symbol) {

    }
}
