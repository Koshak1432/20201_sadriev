package brainfuck;

import brainfuck.command.ICommand;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        String commands;
        IIOController ioController = new IOController();
        CommandFactory factory = new CommandFactory();
        Map<String, String> files = ioController.parseArguments(args);
        Properties props = new Properties();

        try (InputStream stream = new FileInputStream(files.get(PROGRAM_PREFIX))) {
            commands = ioController.readCommands(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (InputStream stream = getConfigStream(files)) {
            props.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            if (1 == ((String)entry.getKey()).length()) {
                factory.registerCommand(((String)entry.getKey()).charAt(0), (String)entry.getValue());
            }
        }

        try (OutputStream oStream = new FileOutputStream(DEFAULT_OUTPUT_NAME)) {
            Context context = new Context(System.in, oStream, ioController,
                              new Program(commands, props.getProperty(START_LOOP).charAt(0), props.getProperty(END_LOOP).charAt(0)));

            interpret(factory, context);
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            System.err.println("Interpretation failed");
            e.printStackTrace();
        }
    }

    private static void interpret(CommandFactory factory, Context context) throws IOException {
        while (!context.getProgram().isEnd()) {
            char c = context.getProgram().getSymbolAt(context.getProgram().getIdx());
            ICommand cmd;
            if (factory.createCommandByChar(c).isPresent()) {
                cmd = (ICommand) factory.createCommandByChar(c).get();
            } else {
                throw new IllegalArgumentException("Couldn't handle the symbol:%c".formatted(c));
            }
            cmd.execute(context);
            context.getProgram().jumpTo(context.getProgram().getIdx() + 1);
        }
    }

    private static InputStream getConfigStream(Map<String, String> files) throws FileNotFoundException {
        InputStream stream;
        if (files.containsKey(CONFIG_PREFIX)) {
            stream = new FileInputStream(files.get(CONFIG_PREFIX));
        }
        else {
            stream = ClassLoader.getSystemResourceAsStream(DEFAULT_CONFIG_NAME);
        }
        return stream;
    }

    public static final String DEFAULT_OUTPUT_NAME = "result.txt";
    public static final String DEFAULT_CONFIG_NAME = "config.txt";
    public static final String START_LOOP = "left";
    public static final String END_LOOP = "right";
    public static final String CONFIG_PREFIX = "--config";
    public static final String PROGRAM_PREFIX = "--program";
}
