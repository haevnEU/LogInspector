package model;

import java.util.function.Predicate;

public record FilterObject(String name, String value, Predicate<LogEntry> predicate) {
    @Override
    public String toString() {
        return name + " == " + value;
    }
}
