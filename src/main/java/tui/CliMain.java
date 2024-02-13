package tui;

import core.Logic;
import de.haevn.utils.exceptions.ExceptionUtils;
import de.haevn.utils.io.TUI;
import de.haevn.utils.system.Tokenizer;
import model.LogEntry;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class CliMain {
    private static final Logic LOGIC = new Logic();
    private static Scanner in;
    private static PrintStream out;
    public static void main(String[] args) {
        final TUI.TuiEntry[] methods = {
                new TUI.TuiEntry("Load", "load", "Loads a log file into the LogInspector", CliMain::load),
                new TUI.TuiEntry("Filter", "filter", "Filters the loaded logfile", CliMain::filter),
                new TUI.TuiEntry("Count", "count", "Counts how many entries exists", CliMain::count),
                new TUI.TuiEntry("Convert", "convert", "Converts the logfile to a CSV file", CliMain::convertToCsv),
                new TUI.TuiEntry("Debug", "debug", "DEBGUG", CliMain::debug),
                new TUI.TuiEntry("Dump", "dump", "Dumps the logfile to the console", CliMain::dump)
        };

        final TUI.TuiEntry[] shortcuts = {
                new TUI.TuiEntry("Exit", "!q", "Exit the LogInspector", () -> System.exit(0)),
                new TUI.TuiEntry("List", "ls", "List all entries, e.g. dump", CliMain::dump)
        };

        try (TUI terminal = new TUI("LogInspector", "exit",methods)) {
            in = terminal.getIn();
            out = terminal.getOut();
            terminal.setShortCuts(shortcuts).show();
        } catch (final Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void load(){
        out.print("Enter log file: ");
        final String logFile = in.nextLine();
        out.println("Loading log file");
        try {
            LOGIC.load(logFile);
            out.println("Loaded " + LOGIC.count() + " entries");
        } catch (IOException e) {
            System.err.println("Something went wrong: " + e.getMessage());
            System.err.println(ExceptionUtils.getStackTrace(e));
        }
    }

    private static void dump(){
        System.out.println(LOGIC.dump());
    }

    private static void filter() {
        out.print("Enter filter: ");
        final String filter = in.nextLine();
        var result = LOGIC.filter(filter);
        out.println("Found " + result.size() + " entries");
        result.stream().filter(Objects::nonNull).filter(entry -> entry.message() != null).map(LogEntry::toString).forEach(out::println);
    }

    private static void count() {
        out.println(LOGIC.count());
    }

    public static void convertToCsv() {
        try {
            out.println("Enter output file: ");
            final String path = in.nextLine();
            LOGIC.convertToCsv(path);
        } catch (IOException e) {
            System.err.println("Failed to convert");
            System.err.println(e.getMessage());
            System.err.println(ExceptionUtils.getStackTrace(e));
        }
    }

    private static void debug() {
        final String query = in.nextLine();
        final var tokenPair = Tokenizer.getInstance("and").tokenize(query);
        out.println(tokenPair.getFirst() + " && " + tokenPair.getSecond());
    }

}
