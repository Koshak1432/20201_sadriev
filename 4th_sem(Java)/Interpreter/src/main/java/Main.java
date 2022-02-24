import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        //first arg -- configName, second -- commandsFile
        IIOController ioController = new IOController();
        Context context = new Context(System.in, System.out, ioController); //change outStream
        Map<String, String> files = context.ioController_.parseArguments(args);
        CommandFactory factory = new CommandFactory();
        Properties props = new Properties();
        String commands;
        try (InputStream stream = ClassLoader.getSystemResourceAsStream(files.get(CONFIG_PREFIX))) {
            props.load(stream);
        }
        try (InputStream stream = new FileInputStream(files.get(PROG_PREFIX))) {
            commands = new String(stream.readAllBytes());
        }
        Program prog = new Program(commands);

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            factory.registerCommand(((String)entry.getKey()).charAt(0), (String)entry.getValue());
        }

        while (!prog.isEnd()) {
            char c = prog.getChar();
            ICommand cmd;
            if (factory.createCommandByChar(c).isPresent()) {
                cmd = (ICommand)factory.createCommandByChar(c).get();
            }
            else {
                //??????
                throw new IllegalArgumentException();
            }
            cmd.execute(context);
            prog.jumpTo(prog.getIdx() + 1);
        }
    }

    public static final String DEFAULT_CONFIG = "config.txt";
    public static final String CONFIG_PREFIX = "--config";
    public static final String PROG_PREFIX = "--prog";
}
