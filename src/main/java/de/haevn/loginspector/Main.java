package de.haevn.loginspector;


import de.haevn.loginspector.ui.UIMain;

@de.haevn.annotations.Launcher(name = "LogInspector")
public interface Main {
    static void main(String[] args) {
        UIMain.main(args);
    }
}
