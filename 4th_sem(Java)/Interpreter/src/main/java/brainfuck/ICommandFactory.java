package brainfuck;

public interface ICommandFactory {
    Object createCommandByChar(char ch);
}
