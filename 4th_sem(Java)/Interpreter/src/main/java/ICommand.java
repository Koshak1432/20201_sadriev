public interface ICommand<Context> {
    void execute(Context ctx);
}
