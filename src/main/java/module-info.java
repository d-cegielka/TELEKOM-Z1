module telekom {
    requires javafx.controls;
    requires javafx.fxml;

    opens telekom to javafx.fxml;
    exports telekom;
}