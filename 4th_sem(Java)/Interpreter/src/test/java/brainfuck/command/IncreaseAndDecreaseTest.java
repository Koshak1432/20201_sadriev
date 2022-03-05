package brainfuck.command;

import brainfuck.Context;
import brainfuck.IOController;
import brainfuck.Program;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IncreaseAndDecreaseTest {

    @Test
    void increaseTest() throws IOException {
        ICommand increase = new CommandIncreaseValue();

        for (int i = 0; i < Byte.MAX_VALUE; ++i) {
            increase.execute(ctx);
            assertEquals(i + 1, ctx.getPointer().getValue());
        }

        increase.execute(ctx);
        assertEquals(0, ctx.getPointer().getValue());
    }

    @Test
    void decreaseTest() throws IOException {
        ICommand decrease = new CommandDecreaseValue();

        decrease.execute(ctx);
        assertEquals(Byte.MAX_VALUE, ctx.getPointer().getValue());

        for (int i = Byte.MAX_VALUE; i > 0; --i) {
            decrease.execute(ctx);
            assertEquals(i - 1, ctx.getPointer().getValue());
        }

        decrease.execute(ctx);
        assertEquals(Byte.MAX_VALUE, ctx.getPointer().getValue());
    }

    private final Context ctx = new Context(System.in, System.out, new IOController(),
                        new Program("", '[', ']'));
}