package de.haevn.loginspector.ui;


import de.haevn.jfx.html.H;
import de.haevn.loginspector.core.Logic;
import de.haevn.loginspector.model.FilterObject;
import de.haevn.loginspector.model.LogEntry;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TableWidget extends GridPane {
    private final TableView<LogEntry> table = new TableView<>();
    private final List<LogEntry> cache = new ArrayList<>();
    private final List<FilterObject> currentFilterList = new ArrayList<>();
    private final StringProperty summary = new SimpleStringProperty("Noth ing loaded");

    private final StringProperty title = new SimpleStringProperty("N/A");

    public TableWidget(final ReadOnlyObjectProperty<Logic> logEntry) {
        final H title = H.ofH1("N/A");
        final Label summary = new Label("Nothing loaded");
        title.textProperty().bind(this.title);
        summary.textProperty().bind(this.summary);

        add(title, 0, 0);
        add(table, 0, 1);
        add(summary, 0, 2);
        GridPane.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(table, javafx.scene.layout.Priority.ALWAYS);

        logEntry.addListener(this::logFileChanged);
    }

    public TableWidget addColumn(final String name, Callback<TableColumn.CellDataFeatures<LogEntry, String>, ObservableValue<String>> factory) {
        TableColumn<LogEntry, String> column = new TableColumn<>(name);
        column.setCellValueFactory(factory);
        table.getColumns().add(column);
        return this;
    }

    public void addOnSelectItemChanged(final Consumer<LogEntry> consume) {
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                consume.accept(newValue);
            }
        });
    }

    public void filter(final List<FilterObject> filterList) {
        resetFilter();
        currentFilterList.addAll(filterList);
        cache.clear();
        cache.addAll(table.getItems());
        table.getItems().clear();

        table.getItems().addAll(cache.stream()
                .filter(entry -> filterList.stream().map(FilterObject::predicate).allMatch(predicate -> predicate.test(entry)))
                .toList());

        summary.set("Showing " + table.getItems().size() + " entries. Filter: " + String.join(" && ", filterList.stream().map(FilterObject::toString).toList()));
    }

    public void resetFilter() {
        table.getItems().clear();
        table.getItems().addAll(cache);
        summary.set("Showing " + table.getItems().size() + " entries.");
        currentFilterList.clear();
    }

    public void filter() {
        filter(currentFilterList);
    }


    private void logFileChanged(ObservableValue<? extends Logic> observableValue, Logic oldValue, Logic newValue) {
        table.getItems().clear();
        table.getItems().addAll(newValue.getEntries());
        cache.clear();
        cache.addAll(newValue.getEntries());
        currentFilterList.clear();
        title.set(newValue.getName());
        summary.set("Showing " + table.getItems().size() + " entries.");
    }
}
