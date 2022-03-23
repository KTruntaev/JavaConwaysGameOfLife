module com.lifegame.gameoflifejfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.lifegame.gameoflifejfx to javafx.fxml;
    exports com.lifegame.gameoflifejfx;
}