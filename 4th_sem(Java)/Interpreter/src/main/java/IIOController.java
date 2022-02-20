import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public interface IIOController {
    void parseArguments(InputStream stream);
    Collection<Character> readCommands(InputStream stream);
    byte readByte(InputStream stream);
    void print(OutputStream stream, Character symbol);
}
