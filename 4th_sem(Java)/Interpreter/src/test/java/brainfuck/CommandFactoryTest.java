package brainfuck;

import brainfuck.command.CommandDecreaseValue;
import brainfuck.command.ICommand;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {

    @Test
    void createCommandByChar() throws IOException {
        IOController io = new IOController();
        String commandsStr = ",.<>-+[]";
        byte[] commandsList = commandsStr.getBytes();
        ByteArrayInputStream iStream = new ByteArrayInputStream(commandsList);

        assertEquals(commandsStr, io.readCommands(iStream));

        ICommand cmd = new CommandDecreaseValue();
        char registeredChar = '-';
        factory.registerCommand(registeredChar, cmd.getClass().getName());
        assertTrue(factory.createCommandByChar(registeredChar).isPresent());
        char unregisteredChar = '+';
        assertFalse(factory.createCommandByChar(unregisteredChar).isPresent());
    }

    CommandFactory factory = new CommandFactory();
}