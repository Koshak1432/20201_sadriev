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
            else if (argument.startsWith(Main.PROG_PREFIX)) {
                map.put(Main.PROG_PREFIX, argument.substring((Main.PROG_PREFIX + "=").length()));
            }
        }
        return map;
    }

    @Override
    public String readCommands(InputStream stream) {
        String result = "";
        Scanner scanner = new Scanner(stream); //todo mb use delimiter("");
        StringBuilder sb = new StringBuilder(result);
        
        while(scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        return sb.toString();
    }

    @Override
    public byte readByte(InputStream stream) {
        return 0;
    }

    @Override
    public void print(OutputStream stream, Character symbol) {

    }
}
