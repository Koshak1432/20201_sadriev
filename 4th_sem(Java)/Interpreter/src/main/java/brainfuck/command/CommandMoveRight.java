package brainfuck.command;

import brainfuck.Context;

public class CommandMoveRight implements ICommand {
    @Override
    public void execute(Context ctx) {
        ctx.getPointer().movePointer(ctx.getPointer().getIdx() + 1);
    }
}
