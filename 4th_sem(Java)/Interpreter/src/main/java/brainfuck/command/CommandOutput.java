package brainfuck.command;

import brainfuck.Context;

import java.io.IOException;

public class CommandOutput implements ICommand {
    @Override
    public void execute(Context ctx) throws IOException {
        ctx.getIoController().print(ctx.getOutStream(), (char)ctx.getPointer().getValue());
    }
}
