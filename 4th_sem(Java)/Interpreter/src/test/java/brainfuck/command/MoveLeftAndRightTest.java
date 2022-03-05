package brainfuck.command;

import brainfuck.Context;
import brainfuck.IOController;
import brainfuck.Program;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MoveLeftAndRightTest {

    @Test
    void moveLeftTest() throws IOException {
        ICommand moveLeft = new CommandMoveLeft();

        for (int i = 0; i < TAPE_SIZE; ++i) {
            assertEquals((TAPE_SIZE - i) % TAPE_SIZE, ctx.getPointer().getIdx());
            moveLeft.execute(ctx);
        }
    }

    @Test
    void moveRightTest() throws IOException {
        ICommand moveRight = new CommandMoveRight();

        for (int i = 0; i < TAPE_SIZE; ++i) {
            assertEquals(i % (TAPE_SIZE - 1), ctx.getPointer().getIdx());
            moveRight.execute(ctx);
        }
    }

    private final int TAPE_SIZE = 30000;
    private final Context ctx = new Context(System.in, System.out, new IOController(),
                                new Program("", '[', ']'));
}