package de.haevn.loginspector.model;

import de.haevn.utils.SerializationUtils;
import de.haevn.utils.logging.SanitizedLogEntry;

/**
 * This class represents a log entry
 *
 * @param thread  Thread name
 * @param date    Date
 * @param level   Log level
 * @param source  Source
 * @param method  Method
 * @param message Message
 */
public record LogEntry(String date, String level, String source, String method, String thread, String object,
                       String throwable, String message) {


    /**
     * Returns a log entry from a line
     * <br>
     * <b>Line format</b>
     * <pre>
     * {@code
     * [DATE] [LEVEL] [SOURCE] [METHOD] [THREAD] [OBJECT] MESSAGE
     * }
     * </pre>
     * <b>Exmaple</b>
     * <pre>
     * {@code
     * Example: [Dez. 12,2023 19:15:39] [INFO] [Test.java:39] [de.haevn.app.de.haevn.loginspector.core.Test#update] [] This is a testsource
     * }
     * </pre>
     *
     * @param line Line to parse
     * @return Log entry
     */
    public static LogEntry getEntryFromLine(String line) {
        final var entryOpt = SerializationUtils.parseJsonSecure(line, SanitizedLogEntry.class);
        if (entryOpt.isPresent()) {
            final var entry = entryOpt.get();

            final var obj = entry.object() != null ? SerializationUtils.exportJson(entry.object()).orElse("") : "";
            return new LogEntry(entry.date(), entry.level(), entry.source(), entry.method(), entry.thread(), obj, entry.throwable(), entry.message());
        }
        return null;
    }

    public String toCsvEntry(char separator) {
        return date + separator + level + separator + source + separator + method + separator + thread + separator + message + separator + object + separator + throwable;
    }

    public String toCsvEntry() {
        return toCsvEntry(';');
    }

    public String toJson() {
        return "{\n" +
                "  \"date\": \"" + date + "\",\n" +
                "  \"level\": \"" + level + "\",\n" +
                "  \"source\": \"" + source + "\",\n" +
                "  \"method\": \"" + method + "\",\n" +
                "  \"thread\": \"" + thread + "\",\n" +
                "  \"message\": \"" + message + "\",\n" +
                "  \"object\": \"" + object + "\",\n" +
                "  \"throwable\": \"" + throwable + "\"\n" +
                "}";
    }
}
