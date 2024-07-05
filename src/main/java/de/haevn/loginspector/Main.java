package de.haevn.loginspector;


import de.haevn.annotations.Launcher;

@Launcher(name = "LogInspector")
public interface Main {
    static void main(String[] args) {
        UIMain.main(args);
    }
}
