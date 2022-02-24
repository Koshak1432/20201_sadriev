import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface IIOController {
    Map<String, String> parseArguments(String[] args);
    String readCommands(InputStream stream);
    byte readByte(InputStream stream);
    void print(OutputStream stream, Character symbol);
}
