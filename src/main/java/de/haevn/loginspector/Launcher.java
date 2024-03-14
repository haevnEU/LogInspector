package de.haevn.loginspector;

import de.haevn.utils.AppLauncher;
import de.haevn.loginspector.tui.CliMain;
import de.haevn.loginspector.ui.UIMain;

import java.util.Arrays;

public class Launcher extends AppLauncher {


    public Launcher(String[] args) {
        super("LogInspector", args);
    }

    public static void main(String[] args) {
        new Launcher(args);
    }

    @Override
    public void setup(String... args) {
        Arrays.stream(args).filter(arg -> arg.equalsIgnoreCase("--debug")).findAny().ifPresent(arg -> super.enableDebug());
        UIMain.main(args);
    }

    @Override
    public void onShutdown() {

    }
}
