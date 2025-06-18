package org.ardev.url_shortener.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class Base62EncoderTest {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final Base62Encoder encoder = new Base62Encoder();

    @BeforeEach
    void injectAlphabet() {
        // имитируем то, что обычно делает Spring при старте приложения
        setField(encoder, "base62", ALPHABET);
    }

    @ParameterizedTest(name = "{0} -> \"{1}\"")
    @CsvSource({
            "0,''",      // для нуля ожидаем пустую строку
            "1,1",
            "61,z",
            "62,01",     // 62  = '01' (LSB-first)
            "3843,zz"    // 62^2-1
    })
    @DisplayName("encodeNumber: корректно преобразует число в Base62-строку")
    void encodeNumber_returnsExpectedString(long input, String expected) {
        assertEquals(expected, encoder.encodeNumber(input));
    }

    @Test
    @DisplayName("encode: кодирует список чисел, сохраняя порядок элементов")
    void encode_encodesListPreservingOrder() {
        List<Long> numbers = List.of(12345L, 7654L, 8769L);      // произвольные данные
        // ожидаемые «LSB-first» строки для заданного алфавита
        List<String> expected = List.of("7D3", "Sz1", "RH2");

        List<String> actual = encoder.encode(numbers);

        assertIterableEquals(expected, actual);
    }

    @Test
    @DisplayName("encode: все символы результата присутствуют в алфавите")
    void encode_producesOnlyAlphabetChars() {
        List<String> hashes = encoder.encode(List.of(1L, 62L, 123_456L));

        boolean allCharsValid = hashes.stream()
                .flatMapToInt(String::chars)
                .allMatch(ch -> ALPHABET.indexOf(ch) >= 0);

        assertTrue(allCharsValid, "В результирующих строках обнаружен символ вне алфавита");
    }
}