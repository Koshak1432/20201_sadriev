public class CommandMoveRight implements ICommand {

    @Override
    public void execute(Context ctx) {
        ctx.pointer_.movePointer(ctx.pointer_.getPointer() + 1);
    }
}
