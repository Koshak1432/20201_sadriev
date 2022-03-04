package brainfuck;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {

    @Test
    void allInOne() {
        final String commands = "0123456789";
        IProgram program = new Program(commands, '[', ']');

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

    @ParameterizedTest
    @ValueSource(strings = {"[[->]-[-<]>-]", "[]", "[[]]"})
    void findMatchingBracketForward(String commands) {
        IProgram program = new Program(commands, '[', ']');

        assertEquals(commands.length() - 1, program.findMatchingBracket(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"[sa]", "[[ddd[s]]]", "[]"})
    void findMatchingBracketBackward(String commands) {
        IProgram program = new Program(commands, '[', ']');

        program.jumpTo(commands.length() - 1);
        assertEquals(0, program.findMatchingBracket(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"[[s]", "[[ddd[s]"})
    void throwTestForward(String commands) {
        IProgram program = new Program(commands, '[', ']');

        assertThrows(IllegalStateException.class, () -> program.findMatchingBracket(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"[s]]]", "[ddd[s]]]"})
    void throwTestBackward(String commands) {
        IProgram program = new Program(commands, '[', ']');

        assertThrows(IllegalStateException.class, () -> program.findMatchingBracket(false));
    }
}