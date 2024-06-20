package de.haevn.loginspector.ui;

import de.haevn.jfx.tools.creation.elements.ButtonCreator;
import de.haevn.jfx.tools.creation.elements.TextInputCreator;
import de.haevn.jfx.tools.creation.pane.BorderPaneCreator;
import de.haevn.jfx.tools.creation.pane.FormCreator;
import de.haevn.jfx.tools.creation.pane.TabPaneCreator;
import de.haevn.loginspector.model.LogEntry;
import de.haevn.utils.StringUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class EntryView {
    private static EntryView instance = new EntryView();
    public static void showEntry(final LogEntry entry) {
        instance.show(entry);
    }

    private final Stage stage;
    private final SimpleObjectProperty<LogEntry> entry = new SimpleObjectProperty<>();
    private final SimpleStringProperty title = new SimpleStringProperty("Log Entry");
    private final SimpleStringProperty date = new SimpleStringProperty("");
    private final SimpleStringProperty level = new SimpleStringProperty("");
    private final SimpleStringProperty source = new SimpleStringProperty("");
    private final SimpleStringProperty method = new SimpleStringProperty("");
    private final SimpleStringProperty thread = new SimpleStringProperty("");
    private final SimpleStringProperty message = new SimpleStringProperty("");
    private final SimpleStringProperty throwable = new SimpleStringProperty("");
    private final SimpleStringProperty object = new SimpleStringProperty("");

    public EntryView() {
        final BorderPane root = BorderPaneCreator.start()
                .withCenter(createCenter())
                .withBottom(createBottom())
                .withTop(createTop())
                .withPadding(10)
                .withStyle("-fx-background-color: #2b2b2b;")
                .build();

        final Scene scene = new Scene(root, 800, 600);
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Log Entry");


        UIMain.setStyle(stage);
        entry.addListener(this::change);
    }

    private void change(ObservableValue<? extends LogEntry> observableValue, LogEntry logEntry, LogEntry logEntry1) {
        if (null == logEntry1) return;
        date.set(logEntry1.date());
        level.set(logEntry1.level());
        source.set(logEntry1.source());
        method.set(logEntry1.method());
        thread.set(logEntry1.thread());
        message.set(logEntry1.message());
        throwable.set(logEntry1.throwable());
        object.set(logEntry1.object());
    }


    private Pane createTop() {
        return FormCreator.start().withDescriptionFixedWidth(200)
                .addRow("Date", TextInputCreator.startTextField(date).withReadonly().buildTextField())
                .addRow("Level", TextInputCreator.startTextField(level).withReadonly().buildTextField())
                .addRow("Source", TextInputCreator.startTextField(source).withReadonly().buildTextField())
                .addRow("Method", TextInputCreator.startTextField(method).withReadonly().buildTextField())
                .addRow("Thread", TextInputCreator.startTextField(thread).withReadonly().buildTextField())
                .build();
    }

    private Pane createBottom() {
        final HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.getChildren().addAll(
                ButtonCreator.start("Copy").withOnClick(this::copy).build(),
                ButtonCreator.start("Close").withOnClick(this::hide).build());
        return buttons;
    }

    private TabPane createCenter() {
        final TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createTab("Message", message),
                createTab("Stacktrace", throwable),
                createTab("Attached Object", object)
        );
        return tabPane;
    }

    private void copy() {
        StringUtils.copyText(entry.get().toJson());
    }

    public void hide() {
        stage.hide();
    }

    public void show(final LogEntry entry) {
        if (null == entry) return;
        this.entry.set(entry);
        stage.toFront();
        if (stage.isShowing()) return;
        stage.show();
    }


    private Tab createTab(final String title, final SimpleStringProperty content) {
        final TextArea contentArea = TextInputCreator.startTextArea(content).withReadonly().buildTextArea();
        return new TabPaneCreator.TabBuilder().withContent(contentArea).withClosable(false).withTitle(title).build();
    }
}
