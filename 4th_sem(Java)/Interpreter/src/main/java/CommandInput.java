import java.io.IOException;

public class CommandInput implements ICommand {
    @Override
    public void execute(Context ctx) throws IOException {
        ctx.getPointer().setPointer(ctx.getIoController().readByte(ctx.getInStream()));
    }
}
