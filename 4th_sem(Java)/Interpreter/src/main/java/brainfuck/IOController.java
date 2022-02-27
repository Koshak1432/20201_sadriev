package brainfuck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class IOController implements IIOController {
    @Override
    public Map<String, String> parseArguments(String[] args) {

        Map<String, String> map = new HashMap<>(args.length);
        for (String argument : args) {
            if (argument.startsWith(Main.CONFIG_PREFIX)) {
                map.put(Main.CONFIG_PREFIX, argument.substring((Main.CONFIG_PREFIX + "=").length()));
            }
            else if (argument.startsWith(Main.PROGRAM_PREFIX)) {
                map.put(Main.PROGRAM_PREFIX, argument.substring((Main.PROGRAM_PREFIX + "=").length()));
            }
        }
        return map;
    }

    @Override
    public String readCommands(InputStream stream) throws IOException {
        return new String(stream.readAllBytes());
    }

    @Override
    public byte readByte(InputStream stream) throws IOException {
        return (byte)stream.read();
    }

    @Override
    public void print(OutputStream stream, Character symbol) throws IOException {
        stream.write(symbol);
    }
}
