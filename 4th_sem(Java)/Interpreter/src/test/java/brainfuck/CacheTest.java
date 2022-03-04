package brainfuck;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CacheTest {

    @ParameterizedTest
    @CsvSource( {"kek1, kek1Val", "kek2, kek2Val"})
    void putAndGet(String key, String val) {
        cache.put(key, val);
        assertEquals(val, cache.get(key));
    }

    @Test
    void notContainsAndSecondPut() {
        String str = "str1";
        String strVal = "str1Val";
        String nullStr = "nullStr";
        String strNewVal = "strVal";
        cache.put(str, strVal);
        cache.put(str,strNewVal);
        assertAll(() -> assertEquals(strVal, cache.get(str)),
                  () -> assertNull(cache.get(nullStr)));
    }

    private final ICache<String, String> cache = new Cache<>();
}