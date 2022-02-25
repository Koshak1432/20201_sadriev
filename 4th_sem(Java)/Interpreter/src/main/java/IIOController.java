import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface IIOController {
    Map<String, String> parseArguments(String[] args);
    String readCommands(InputStream stream) throws IOException;
    byte readByte(InputStream stream) throws IOException;
    void print(OutputStream stream, Character symbol) throws IOException;
}
