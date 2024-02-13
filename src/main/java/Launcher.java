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
        Arrays.stream(args).filter(arg -> arg.equalsIgnoreCase("--ui")).forEach(arg -> UIMain.main(args));
        Arrays.stream(args).filter(arg -> arg.equalsIgnoreCase("--cli")).forEach(arg -> CliMain.main(args));
        super.enableDebug();
        for (String arg : args) {
            System.out.println(arg);
        }
    }

    @Override
    public void onShutdown() {

    }
}
