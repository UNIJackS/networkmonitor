module com.github.unijacks {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.unijacks to javafx.fxml;
    exports com.github.unijacks;
}
