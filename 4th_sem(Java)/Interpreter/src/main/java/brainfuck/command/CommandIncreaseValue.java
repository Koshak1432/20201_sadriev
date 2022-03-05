package brainfuck.command;

import brainfuck.Context;

public class CommandIncreaseValue implements ICommand {
    @Override
    public void execute(Context ctx) {
        byte value = ctx.getPointer().getValue();
        if (Byte.MAX_VALUE == value) {
            value = -1;
        }
        ctx.getPointer().setPointer((byte)(value + 1));
    }
}
