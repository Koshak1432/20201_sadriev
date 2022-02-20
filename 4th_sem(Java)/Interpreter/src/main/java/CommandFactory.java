import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandFactory implements ICommandFactory {
    @Override
    public Optional<Object> createCommandByChar(char ch) throws Exception {
        try {
            Class<?> commandClass = cache_.get(ch);
            if (null == commandClass) {
                String className = commandMap_.get(ch);
                commandClass = Class.forName(className);
                cache_.put(ch, commandClass);
            }
            return Optional.of(commandClass.getConstructor().newInstance());
        } catch (Exception e) {
            System.out.println("Caught an exception!");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void registerCommand(Character cmdName, String className) {
        commandMap_.putIfAbsent(cmdName, className);
    }

    private final Map<Character, String> commandMap_ = new HashMap<>();
    private final ICache<Character, Class<?>> cache_ = new Cache<>();
}
