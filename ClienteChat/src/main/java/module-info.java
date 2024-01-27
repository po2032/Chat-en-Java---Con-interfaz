module com.clientechat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.clientechat to javafx.fxml;
    exports com.clientechat;
}
