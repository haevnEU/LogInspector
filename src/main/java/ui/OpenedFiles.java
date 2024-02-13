package ui;

import core.Logic;
import de.haevn.jfx.elements.Toast;
import de.haevn.utils.io.FileIO;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class OpenedFiles extends ListView<Logic> {
    private final SimpleObjectProperty<Logic> selectedItem = new SimpleObjectProperty<>();


    public OpenedFiles() {
        setPadding(new Insets(10));
        setMaxWidth(200);
        setOnMouseClicked(event -> {
            if (getSelectionModel().getSelectedItem() != null) {
                selectedItem.set(getSelectionModel().getSelectedItem());
            }
        });
    }

    public void openFiles(){

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Log File");
        fileChooser.setInitialDirectory(new File(FileIO.getUserHomeWithSeparator() + "haevn"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Log Files", "*.log"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        final var files = fileChooser.showOpenMultipleDialog(null);
        final AtomicLong entries = new AtomicLong();
        final AtomicLong loadedFiles = new AtomicLong();
        if (files != null && !files.isEmpty()) {
            files.forEach(file ->
            {
                final Logic logic = new Logic();
                try {
                    logic.load(file.getAbsolutePath());
                } catch (IOException ignored) {}
                if(getItems().stream().filter(item -> item.toString().equalsIgnoreCase(logic.toString())).findFirst().isEmpty()) {
                    getItems().add(logic);
                    loadedFiles.incrementAndGet();
                    entries.addAndGet(logic.count());
                }else{
                    Toast.bad("File " + logic.toString() + "  already loaded");
                }
            });
            Toast.good("Loaded " + loadedFiles + " files " + entries + " entries");

        }

    }

    public ReadOnlyObjectProperty<Logic> selectedItemProperty() {
        return selectedItem;
    }
}
