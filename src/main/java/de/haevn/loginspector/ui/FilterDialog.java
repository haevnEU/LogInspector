package de.haevn.loginspector.ui;

import de.haevn.loginspector.model.FilterObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class FilterDialog {
    private final Stage stage;

    private final SimpleStringProperty thread = new SimpleStringProperty();
    private final SimpleStringProperty date = new SimpleStringProperty();
    private final SimpleStringProperty level = new SimpleStringProperty();
    private final SimpleStringProperty source = new SimpleStringProperty();
    private final SimpleStringProperty method = new SimpleStringProperty();
    private final SimpleStringProperty message = new SimpleStringProperty();
    private final SimpleStringProperty object = new SimpleStringProperty();
    private final SimpleStringProperty throwable = new SimpleStringProperty();


    public FilterDialog() {
        final GridPane root = new GridPane();

        final TextField threadField = addFormEntry("Thread", root, 1, "", thread);
        final TextField dateField = addFormEntry("Date", root, 2, "", date);
        final TextField levelField = addFormEntry("Level", root, 3, "", level);
        final TextField sourceField = addFormEntry("Source", root, 4, "", source);
        final TextField methodField = addFormEntry("Method", root, 5, "", method);
        final TextField messageField = addFormEntry("Message", root, 6, "", message);
        final TextField objectField = addFormEntry("Object", root, 7, "", object);
        final TextField throwableField = addFormEntry("Throwable", root, 8, "", throwable);


        final Scene scene = new Scene(root, 800, 600);
        stage = new Stage();
        stage.setScene(scene);
    }


    public void show(final FilterObject filter) {

        stage.showAndWait();
    }

    public FilterObject hide() {
        stage.hide();
        return null;
    }

    private TextField addFormEntry(final String title, final GridPane gridPane, final int row, String value, SimpleStringProperty property) {
        gridPane.add(new Label(title), 0, row);
        final TextField textField = new TextField(value);
        textField.textProperty().bind(property);
        textField.setEditable(false);
        gridPane.add(textField, 1, row);
        return textField;
    }

}
