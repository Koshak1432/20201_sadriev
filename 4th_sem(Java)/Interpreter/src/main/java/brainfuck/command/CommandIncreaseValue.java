package brainfuck.command;

import brainfuck.Context;

public class CommandIncreaseValue implements ICommand {
    @Override
    public void execute(Context ctx) {
        ctx.getPointer().setPointer((byte)(ctx.getPointer().getValue() + 1));
    }
}
