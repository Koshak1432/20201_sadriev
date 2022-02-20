public class CommandInput implements ICommand {
    @Override
    public void execute(Context ctx) {
        ctx.pointer_.setPointer(ctx.ioController_.readByte(ctx.getInStream()));
    }
}
