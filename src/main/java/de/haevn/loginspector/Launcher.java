package de.haevn.loginspector;

import de.haevn.loginspector.ui.UIMain;
import de.haevn.utils.AppLauncher;

public class Launcher extends AppLauncher {

    public static void main(String[] args) {
        new Launcher(args);
    }

    Launcher(String[] args) {
        super("LogInspector", args);
    }

    @Override
    public void setup(String... args) {
        UIMain.main(args);
    }

    @Override
    public void onShutdown() {

    }
}
