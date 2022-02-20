public class CommandIncreaseValue implements ICommand {

    @Override
    public void execute(Context ctx) {
        ctx.pointer_.setPointer((byte)(ctx.pointer_.getValue() + 1));
    }
}
