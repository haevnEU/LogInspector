package core;

import de.haevn.utils.system.Tokenizer;
import model.LogEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Logic {
    private File file;
    private String name;
    private final List<LogEntry> logEntries = new ArrayList<>();

    public void load(final String logFile) throws IOException {
        logEntries.clear();
        logEntries.addAll(Files.readAllLines(new File(logFile).toPath()).stream().map(LogEntry::getEntryFromLine).toList().stream().filter(Objects::nonNull)
                .filter(e->e.level() != null && !e.level().isEmpty())
                .filter(e->e.date() != null && !e.date().isEmpty())
                .toList());
        file = new File(logFile);
        name = file.getName();
    }

    public List<LogEntry> filter(String query) {
        if (logEntries.isEmpty()) {
            return List.of();
        }
        var tokenPair = Tokenizer.getInstance("=").tokenize(query);

        Predicate<LogEntry> filterPredicate = entry -> switch (tokenPair.getFirst().toLowerCase()) {
            case "thread" -> entry.thread().toLowerCase().contains(tokenPair.getSecond().toLowerCase());
            case "date" -> entry.date().toLowerCase().contains(tokenPair.getSecond().toLowerCase());
            case "level" -> entry.level().toLowerCase().contains(tokenPair.getSecond().toLowerCase());
            case "source" -> entry.source().toLowerCase().contains(tokenPair.getSecond().toLowerCase());
            case "method" -> entry.method().toLowerCase().contains(tokenPair.getSecond().toLowerCase());
            case "message" -> entry.message().toLowerCase().contains(tokenPair.getSecond().toLowerCase());
            default -> false;
        };
        return logEntries.stream().filter(Objects::nonNull).filter(entry -> entry.message() != null).filter(filterPredicate).toList();
    }

    public long count() {
        return logEntries.size();
    }

    public void convertToCsv(final String path) throws IOException {
        if (logEntries.isEmpty() || path.isBlank()) {
            return;
        }
        final File output = new File(path);
        final StringBuilder builder = new StringBuilder();
        builder.append("Date;Level;Source;Method;Thread;Message;Object;Throwable\n");
        logEntries.stream().filter(Objects::nonNull).filter(entry -> entry.message() != null).forEach(entry -> builder.append(entry.toCsvEntry()).append("\n"));
        Files.writeString(output.toPath(), builder.toString());
    }

    public String dump() {
        final StringBuilder builder = new StringBuilder();
        logEntries.stream().filter(Objects::nonNull).filter(entry -> entry.message() != null).map(LogEntry::toString).forEach(e -> builder.append(e).append("\n"));
        return builder.toString();
    }

    @Override
    public String toString() {
        return name;
    }

    public List<LogEntry> getEntries() {
        return logEntries;
    }

    public String getName() {
        return name;
    }

    public boolean delete() {
        return file.delete();
    }
}
