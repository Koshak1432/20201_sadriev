package brainfuck;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PointerTest {

    @ParameterizedTest
    @CsvSource({"10, 9", "-1, 11", "35000, 35"})
    void ptr(int idx, byte value) {
        int MAX_SIZE = 30000;

        ptr.movePointer(idx);
        ptr.setPointer(value);

        assertEquals(value, ptr.getValue());
        if (idx >= MAX_SIZE - 1) {
            assertEquals(0, ptr.getIdx());
        } else if (idx < 0) {
            assertEquals(MAX_SIZE - 1, ptr.getIdx());
        } else {
            assertEquals(idx, ptr.getIdx());
        }
    }

    private final IPointer ptr = new Pointer();
}