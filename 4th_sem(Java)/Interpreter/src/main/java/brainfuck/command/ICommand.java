package brainfuck.command;

import brainfuck.Context;

import java.io.IOException;

public interface ICommand {
    void execute(Context ctx) throws IOException;
}
