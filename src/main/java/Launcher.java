import de.haevn.utils.AppLauncher;
import tui.CliMain;
import ui.UIMain;

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
        super.enableDebug();
        Arrays.stream(args).filter(arg -> arg.equalsIgnoreCase("--ui")).findAny().ifPresent(arg -> UIMain.main(args));
        Arrays.stream(args).filter(arg -> arg.equalsIgnoreCase("--cli")).findAny().ifPresent(arg -> CliMain.main(args));
    }

    @Override
    public void onShutdown() {

    }
}
