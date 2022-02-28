package brainfuck.command;

import brainfuck.Context;

public class CommandEndLoop implements ICommand {
    @Override
    public void execute(Context ctx) throws IllegalArgumentException, IllegalStateException {
        if (0 != ctx.getPointer().getValue()) {
            ctx.getProgram().jumpTo(ctx.findMatchingBracket(ctx, false));
        }
    }
}
