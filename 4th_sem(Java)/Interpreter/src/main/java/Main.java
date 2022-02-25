import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        //first arg -- configName, second -- commandsFile
        String commands;
        IIOController ioController = new IOController();
        Map<String, String> files = ioController.parseArguments(args);

        try (InputStream stream = new FileInputStream(files.get(PROGRAM_PREFIX))) {
            commands = ioController.readCommands(stream);
        }

        Context context = new Context(System.in, System.out, ioController, new Program(commands)); //todo change outStream
        CommandFactory factory = new CommandFactory();
        Properties props = new Properties();
        try (InputStream stream = ClassLoader.getSystemResourceAsStream(files.get(CONFIG_PREFIX))) {
            props.load(stream);
        }

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            factory.registerCommand(((String)entry.getKey()).charAt(0), (String)entry.getValue());
        }

        while (!context.getProgram().isEnd()) {
            char c = context.getProgram().getChar();
            ICommand cmd;
            if (factory.createCommandByChar(c).isPresent()) {
                cmd = (ICommand)factory.createCommandByChar(c).get();
            }
            else {
                //??????
                throw new IllegalArgumentException();
            }
            cmd.execute(context);
            context.getProgram().jumpTo(context.getProgram().getIdx() + 1);
        }
    }

    public static final String CONFIG_PREFIX = "--config";
    public static final String PROGRAM_PREFIX = "--program";
}
