public class CommandOutput implements ICommand {
    @Override
    public void execute(Context ctx) {
        ctx.ioController_.print(ctx.getOutStream(), (char)ctx.pointer_.getValue());
    }
}
