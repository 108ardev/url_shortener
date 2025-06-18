package org.ardev.url_shortener.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

    @Value("${hash.base62-alphabet}")
    private String base62;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    public String encodeNumber(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % base62.length());
            encoded.append(base62.charAt(remainder));
            number /= base62.length();
        }

        return encoded.toString();
    }
}
