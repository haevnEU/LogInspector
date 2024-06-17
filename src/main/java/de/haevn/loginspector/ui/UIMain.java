package de.haevn.loginspector.ui;

import de.haevn.jfx.elements.Toast;
import de.haevn.jfx.elements.menu.ClickableMenu;
import de.haevn.loginspector.core.Logic;
import de.haevn.loginspector.model.FilterObject;
import de.haevn.loginspector.model.LogEntry;
import de.haevn.utils.StringUtils;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class UIMain extends Application {

    private final OpenedFiles openedFilesListWidget = new OpenedFiles();
    private final SimpleObjectProperty<Logic> selectedItem = new SimpleObjectProperty<>();
    private final ClickableMenu menuCount = new ClickableMenu("Count", this::count).withDisabled();
    private final ClickableMenu menuConvert = new ClickableMenu("Convert", this::convertToCsv).withDisabled();
    private final ClickableMenu menuDump = new ClickableMenu("Dump", this::dump).withDisabled();
    final ClickableMenu menuOpen = new ClickableMenu("Open", openedFilesListWidget::openFiles);
    final ClickableMenu menuDelete = new ClickableMenu("Delete", this::delete).withDisabled();
    final ClickableMenu menuFilter = new ClickableMenu("Filter", this::filter).withDisabled();
    final ClickableMenu menuReload = new ClickableMenu("Reload", this::reload).withDisabled();


    private final TableWidget table = new TableWidget(selectedItem);
    private final BorderPane root = new BorderPane();
    private final List<FilterObject> filters = new ArrayList<>();

    public UIMain() {

        root.setTop(createMenu());

        final VBox center = new VBox();
        center.setSpacing(10);
        Label countLabel = new Label();
        center.getChildren().addAll(table, countLabel);
        root.setCenter(center);
        VBox.setVgrow(table, Priority.ALWAYS);


        root.setLeft(openedFilesListWidget);


        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #1e1e1e;");


        table.addColumn("Date", p -> new SimpleStringProperty(StringUtils.trimStringTo(p.getValue().date(), 64)))
                .addColumn("Level", p -> new SimpleStringProperty(StringUtils.trimStringTo(p.getValue().level(), 64)))
                .addColumn("Source", p -> new SimpleStringProperty(StringUtils.trimStringTo(p.getValue().source(), 64)))
                .addColumn("Method", p -> new SimpleStringProperty(StringUtils.trimStringTo(p.getValue().method(), 64)))
                .addColumn("Thread", p -> new SimpleStringProperty(StringUtils.trimStringTo(p.getValue().thread(), 64)))
                .addColumn("Message", p -> new SimpleStringProperty(StringUtils.trimStringTo(p.getValue().message(), 64)))
                .addColumn("Object", p -> new SimpleStringProperty(StringUtils.trimStringTo(p.getValue().object(), 64)))
                .addColumn("Throwable", p -> new SimpleStringProperty(StringUtils.splitSecure(p.getValue().throwable(), 0, ':')));


        table.addOnSelectItemChanged(EntryView::showEntry);
        selectedItem.bind(openedFilesListWidget.selectedItemProperty());
        openedFilesListWidget.selectedItemProperty().addListener((observable, oldValue, newValue) -> doFilter());
        selectedItem.addListener((observable, oldValue, newValue) -> enableMenu(newValue != null));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setStyle(final Stage stage) {
        stage.getScene().getStylesheets().clear();
        stage.getScene().getStylesheets().add(UIMain.class.getResource("/styles/horde.css").toExternalForm());
        stage.getScene().getStylesheets().add(UIMain.class.getResource("/core.css").toExternalForm());
    }

    private void setup() {

    }

    private void enableMenu(boolean b) {
        if (b) {
            menuConvert.enable();
            menuCount.enable();
            menuDelete.enable();
            menuReload.enable();
            menuDump.enable();
            menuFilter.enable();
        } else {
            menuConvert.disable();
            menuCount.disable();
            menuDelete.disable();
            menuDump.disable();
            menuReload.disable();
            menuFilter.disable();
        }
    }

    private void convertToCsv() {
        if (null == selectedItem.get() || selectedItem.get().count() == 0) {
            Toast.bad("No entries loaded");
            return;
        }
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        final var file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                selectedItem.get().convertToCsv(file.getAbsolutePath());
                Toast.good("Saved " + selectedItem.get().count() + " entries");
            } catch (Exception e) {
                Toast.bad(e.getMessage());
            }
        }
    }

    private void addFilterIfNotEmpty(final String name, final String value, Predicate<LogEntry> filter) {
        if (!value.isBlank()) {
            filters.add(new FilterObject(name, value, filter));
        }
    }

    private void doFilter() {
        table.filter(filters);
    }

    private void filter() {
        if (null == selectedItem.get() || selectedItem.get().count() == 0) {
            Toast.bad("No entries loaded");
            return;
        }
        final Stage popupStage = new Stage();
        final GridPane popupRoot = new GridPane();
        popupRoot.setStyle("-fx-background-color: #1e1e1e;");

        popupRoot.setHgap(10);
        popupRoot.setVgap(10);
        popupRoot.setPadding(new Insets(10));
        final Scene scene = new Scene(popupRoot, 350, 420);
        popupStage.setResizable(false);
        popupStage.setScene(scene);

        popupRoot.add(new Label("Leave empty to ignore field"), 0, 0, 2, 1);
        final TextField threadField = addFormEntry("Thread", popupRoot, 1, "");
        final TextField dateField = addFormEntry("Date", popupRoot, 2, "");
        final TextField levelField = addFormEntry("Level", popupRoot, 3, "");
        final TextField sourceField = addFormEntry("Source", popupRoot, 4, "");
        final TextField methodField = addFormEntry("Method", popupRoot, 5, "");
        final TextField messageField = addFormEntry("Message", popupRoot, 6, "");
        final TextField objectField = addFormEntry("Object", popupRoot, 7, "");
        final TextField throwableField = addFormEntry("Throwable", popupRoot, 8, "");

        final Button button = createButton("Apply", () -> {
            filters.clear();
            addFilterIfNotEmpty("thread", threadField.getText(), entry -> entry.thread().toLowerCase().contains(threadField.getText().toLowerCase()));
            addFilterIfNotEmpty("date", dateField.getText(), entry -> entry.date().toLowerCase().contains(dateField.getText().toLowerCase()));
            addFilterIfNotEmpty("level", levelField.getText(), entry -> entry.level().toLowerCase().contains(levelField.getText().toLowerCase()));
            addFilterIfNotEmpty("source", sourceField.getText(), entry -> entry.source().toLowerCase().contains(sourceField.getText().toLowerCase()));
            addFilterIfNotEmpty("method", methodField.getText(), entry -> entry.method().toLowerCase().contains(methodField.getText().toLowerCase()));
            addFilterIfNotEmpty("message", messageField.getText(), entry -> entry.message().toLowerCase().contains(messageField.getText().toLowerCase()));
            addFilterIfNotEmpty("object", objectField.getText(), entry -> entry.object().toLowerCase().contains(objectField.getText().toLowerCase()));
            addFilterIfNotEmpty("throwable", throwableField.getText(), entry -> entry.throwable().toLowerCase().contains(throwableField.getText().toLowerCase()));

            doFilter();
            popupStage.close();
        });
        final Button cancel = createButton("Cancel", () -> {
            filters.clear();
            doFilter();
            popupStage.close();
        });

        popupRoot.add(button, 0, 9);
        popupRoot.add(cancel, 1, 9);

        filters.forEach(filterObject -> {
            switch (filterObject.name()) {
                case "thread" -> threadField.setText(filterObject.value());
                case "date" -> dateField.setText(filterObject.value());
                case "level" -> levelField.setText(filterObject.value());
                case "source" -> sourceField.setText(filterObject.value());
                case "method" -> methodField.setText(filterObject.value());
                case "message" -> messageField.setText(filterObject.value());
                case "object" -> objectField.setText(filterObject.value());
                case "throwable" -> throwableField.setText(filterObject.value());
                default -> {
                }
            }
        });


        setStyle(popupStage);
        popupStage.showAndWait();
    }

    private void dump() {
        if (null == selectedItem.get() || selectedItem.get().count() == 0) {
            Toast.bad("No entries loaded");
            return;
        }

        final Stage popupStage = new Stage();
        final TextArea textArea = new TextArea();
        final Scene scene = new Scene(textArea, 800, 600);
        textArea.setText(selectedItem.get().dump());
        textArea.setEditable(false);
        textArea.setOnMouseEntered(e -> textArea.selectAll());
        popupStage.setScene(scene);
        textArea.setStyle("-fx-background-color: #1e1e1e;");
        setStyle(popupStage);
        popupStage.showAndWait();

    }

    private void count() {
        if (null == selectedItem.get()) {
            Toast.bad("No entries loaded");
            return;
        } else if (0 == selectedItem.get().count()) {
            Toast.warn("No entries found in " + selectedItem.get().getName());
            return;
        }
        Toast.good("Loaded " + selectedItem.get().count() + " entries in " + selectedItem.get().getName());
    }

    private void delete() {
        if (null == selectedItem.get()) return;
        openedFilesListWidget.getItems().remove(selectedItem.get());
        if (selectedItem.get().delete()) Toast.good("Deleted " + selectedItem.getName());
        else Toast.bad("Could not delete " + selectedItem.getName());
        selectedItem.setValue(null);
    }

    private TextField addFormEntry(final String title, final GridPane gridPane, final int row, String value) {
        gridPane.add(new Label(title), 0, row);
        final TextField textField = new TextField(value);
        gridPane.add(textField, 1, row);
        return textField;
    }

    private @NotNull Button createButton(String text, Runnable action) {
        final Button button = new Button(text);
        button.setMaxWidth(150);
        button.setMinWidth(150);
        button.setOnAction(event -> action.run());
        return button;
    }

    private @NotNull MenuBar createMenu() {
        return new MenuBar(menuOpen, menuReload, menuDelete, menuCount, menuFilter, menuConvert, menuDump);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Log Viewer");
        primaryStage.getIcons().add(new Image(UIMain.class.getResourceAsStream("/icons/main_icon.png")));
        Toast.initialize(primaryStage);
        primaryStage.setScene(new Scene(root, 1200, 900));
        setStyle(primaryStage);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.show();
    }

    private void reload() {
        selectedItem.get().reload();
        var tmp = selectedItem.get();
        selectedItem.setValue(null);
        selectedItem.setValue(tmp);
    }

}
