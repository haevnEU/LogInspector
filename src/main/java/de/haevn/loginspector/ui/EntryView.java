package de.haevn.loginspector.ui;

import de.haevn.jfx.tools.creation.elements.ButtonCreator;
import de.haevn.jfx.tools.creation.elements.TextInputCreator;
import de.haevn.jfx.tools.creation.pane.BorderPaneCreator;
import de.haevn.jfx.tools.creation.pane.FormCreator;
import de.haevn.jfx.tools.creation.pane.TabPaneCreator;
import de.haevn.loginspector.model.LogEntry;
import de.haevn.utils.StringUtils;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class EntryView {
    public static void showEntry(final LogEntry entry) {
        new EntryView(entry).show();
    }

    private final Stage stage;
    private final LogEntry entry;

    public EntryView(final LogEntry entry) {
        this.entry = entry;
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
    }

    private Pane createTop() {
        return FormCreator.start().withDescriptionFixedWidth(200)
                .addRow("Date", TextInputCreator.startTextField(entry.date()).withReadonly().buildTextField())
                .addRow("Level", TextInputCreator.startTextField(entry.level()).withReadonly().buildTextField())
                .addRow("Source", TextInputCreator.startTextField(entry.source()).withReadonly().buildTextField())
                .addRow("Method", TextInputCreator.startTextField(entry.method()).withReadonly().buildTextField())
                .addRow("Thread", TextInputCreator.startTextField(entry.thread()).withReadonly().buildTextField())
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
                createTab("Message", entry.message()),
                createTab("Stacktrace", entry.throwable()),
                createTab("Attached Object", entry.object())
        );
        return tabPane;
    }

    private void copy() {
        StringUtils.copyText(entry.toJson());
    }

    public void hide() {
        stage.hide();
    }

    public void show() {
        if (null == entry) return;
        if (stage.isShowing()) return;
        stage.show();
    }


    private Tab createTab(final String title, final String content) {
        final TextArea contentArea = TextInputCreator.startTextArea(content).withReadonly().buildTextArea();
        return new TabPaneCreator.TabBuilder().withContent(contentArea).withClosable(false).withTitle(title).build();
    }
}
