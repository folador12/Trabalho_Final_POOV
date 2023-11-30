module poov.vacina {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens poov.vacina to javafx.fxml, javafx.graphics;
    opens poov.vacina.controller to javafx.fxml, javafx.graphics;
    opens poov.vacina.model to javafx.base, javafx.graphics;

    exports poov.vacina;
}
