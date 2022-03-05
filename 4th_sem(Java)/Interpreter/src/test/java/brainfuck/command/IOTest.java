package brainfuck.command;

import brainfuck.Context;
import brainfuck.IOController;
import brainfuck.Program;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IOTest {

    @Test
    void outputTest() throws IOException {
        ICommand output = new CommandOutput();

        for (int i = 0; i < chars.length();) {
            ctx.getPointer().setPointer((byte)chars.charAt(i));
            output.execute(ctx);
            ctx.getPointer().movePointer(++i);
        }

        assertArrayEquals(chars.getBytes(), oStream.toByteArray());
    }

    @Test
    void inputTest() throws IOException {
        ICommand input = new CommandInput();

        for (int i = 0; i < chars.length(); ++i) {
            input.execute(ctx);
            assertEquals(chars.charAt(i), ctx.getPointer().getValue());
            ctx.getPointer().movePointer(ctx.getPointer().getIdx() + 1);
        }
    }

    private final String chars = "Disco Elysium";
    private final byte[] buf = chars.getBytes();
    private final ByteArrayInputStream iStream = new ByteArrayInputStream(buf);
    private final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    private final Context ctx = new Context(iStream, oStream, new IOController(),
                                            new Program("", '[', ']'));
}