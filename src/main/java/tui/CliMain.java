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
    public static void main(String[] args) {
        System.out.println("Welcome to the Log Analyzer!");
        System.out.println("Due library changes and updates, the CLI is not fully functional yet.");
        System.out.println("Please use the GUI for now.");
        System.out.println("run LogAnalyzer --ui");
        System.out.println("Thank you for your understanding.");
        System.out.println("Goodbye!");
        System.exit(0);
    }

}
