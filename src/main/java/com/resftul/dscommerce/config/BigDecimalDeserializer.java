package com.resftul.dscommerce.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.math.BigDecimal;

public final class BigDecimalDeserializer extends StdDeserializer<BigDecimal> {

    private static final char NO_SEP = '\0';
    private static final char DOT = '.';
    private static final char COMMA = ',';
    private static final String NBSP = "\u00A0";
    private static final String STRIP_REGEX = "[^0-9,.-]";

    public BigDecimalDeserializer() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        var t = jsonParser.getCurrentToken();
        return switch (t) {
            case VALUE_NUMBER_INT, VALUE_NUMBER_FLOAT -> jsonParser.getDecimalValue();
            case VALUE_STRING -> parseStringToken(jsonParser);
            default -> throw InvalidFormatException.from(
                    jsonParser, "Token JSON inválido para preço",
                    jsonParser.getText(),
                    BigDecimal.class
            );
        };
    }

    private static BigDecimal parseStringToken(JsonParser jsonParser) throws IOException {
        final String raw = jsonParser.getText();
        try {
            BigDecimal parsed = parseFlexibleDecimal(raw);
            if (parsed == null) throw new IllegalArgumentException("Preço vazio ou inválido");
            return parsed;
        } catch (IllegalArgumentException illegalArgumentException) {
            throw InvalidFormatException.from(
                    jsonParser,
                    illegalArgumentException.getMessage(),
                    raw,
                    BigDecimal.class
            );
        }
    }

    private static BigDecimal parseFlexibleDecimal(String input) {
        if (input == null) return null;

        final String s = normalizeRaw(input);
        if (s == null) return null;

        final char decimalSep = detectDecimalSep(s);
        final String normalized = normalizeNumber(s, decimalSep);

        validateNormalized(normalized);
        return new BigDecimal(normalized);
    }

    private static String normalizeRaw(String input) {
        final String s = input.trim();
        if (s.isEmpty()) return null;
        return s.replace(NBSP, "").replaceAll(STRIP_REGEX, "");
    }

    private static String normalizeNumber(String s, char decimalSep) {
        StringBuilder out = new StringBuilder(s.length() + 2);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> out.append(c);
                case DOT, COMMA -> {
                    if (decimalSep != NO_SEP && c == decimalSep) out.append(DOT);
                }
                case '-' -> {
                    if (out.isEmpty()) out.append('-');
                }
                default -> {
                }
            }
        }
        return out.toString();
    }

    private static void validateNormalized(String normalized) {
        if (normalized.isEmpty() || "-".equals(normalized)) {
            throw new IllegalArgumentException("Formato numérico inválido");
        }
    }

    private static char detectDecimalSep(String s) {
        if (s.isEmpty() || "-".equals(s)) throw new IllegalArgumentException("Formato numérico vazio");

        final int lastDot = s.lastIndexOf(DOT);
        final int lastComma = s.lastIndexOf(COMMA);
        final boolean hasDot = lastDot >= 0;
        final boolean hasComma = lastComma >= 0;

        final int state = (hasDot ? 1 : 0) | (hasComma ? 2 : 0);
        return switch (state) {
            case 3 -> {
                int lastSepIdx = Math.max(lastDot, lastComma);
                char lastSep = (lastSepIdx == lastDot) ? DOT : COMMA;
                if (decimalsAfter(s, lastSepIdx) == 2) yield lastSep;
                int otherIdx = (lastSep == DOT) ? lastComma : lastDot;
                if (decimalsAfter(s, otherIdx) == 2) yield (lastSep == DOT) ? COMMA : DOT;
                yield lastSep;
            }
            case 2 -> (decimalsAfter(s, lastComma) == 2) ? COMMA : NO_SEP;
            case 1 -> (decimalsAfter(s, lastDot) == 2) ? DOT : NO_SEP;
            default -> NO_SEP;
        };
    }

    private static int decimalsAfter(String s, int sepIdx) {
        return (sepIdx >= 0)
                ? (s.length() - sepIdx - 1)
                : 0;
    }
}
