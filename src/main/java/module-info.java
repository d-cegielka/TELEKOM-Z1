module org.openjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.lang3;

    opens telekom to javafx.fxml;
    exports telekom;
}