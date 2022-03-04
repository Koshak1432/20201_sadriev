package brainfuck;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {

    @Test
    void allInOne() {
        int idxToJump = 0;
        for (; idxToJump < commands.length() + 10; ++idxToJump) {
            int finalIdxToJump = idxToJump;
            if (idxToJump >= commands.length()) {
                assertThrows(IllegalArgumentException.class, () -> program.jumpTo(finalIdxToJump));
                assertThrows(IllegalArgumentException.class, () -> program.getSymbolAt(finalIdxToJump));
            } else if (idxToJump > 0) {
                program.jumpTo(idxToJump);
                assertAll(() -> assertEquals(finalIdxToJump, program.getIdx()),
                      () ->assertEquals(commands.charAt(finalIdxToJump), program.getSymbolAt(finalIdxToJump)));
            }
        }
    }

    private final String commands = "0123456789";
    private final IProgram program = new Program(commands);
}