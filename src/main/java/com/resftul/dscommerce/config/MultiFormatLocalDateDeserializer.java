package com.resftul.dscommerce.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public final class MultiFormatLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter MULTI = new DateTimeFormatterBuilder()
            .appendOptional(ISO_LOCAL_DATE)
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/uuuu"))
            .appendOptional(DateTimeFormatter.ofPattern("dd-MM-uuuu"))
            .toFormatter();

    @Override
    public LocalDate deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException {
        final String text = jsonParser.getValueAsString();
        if (text == null || text.isBlank()) return null;
        final String v = text.trim();
        try {
            return LocalDate.parse(v, MULTI);
        } catch (DateTimeParseException ex) {
            throw deserializationContext.weirdStringException(
                    text,
                    LocalDate.class,
                    "Invalid date format. Expected one of: yyyy-MM-dd, dd/MM/yyyy, dd-MM-yyyy"
            );
        }
    }
}