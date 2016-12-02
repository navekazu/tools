package tools.pomodorotimer.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

public class PomodoroListController implements Initializable {
    @FXML
    private GridPane pomodoroGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GridPane.clearConstraints(pomodoroGrid);
        pomodoroGrid.getColumnConstraints().clear();
        pomodoroGrid.getRowConstraints().clear();

        SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
        Calendar calFrom = Calendar.getInstance();
        calFrom.set(Calendar.HOUR_OF_DAY, 9);
        calFrom.set(Calendar.MINUTE, 0);
        Calendar calTo = Calendar.getInstance();
        calTo.set(Calendar.HOUR_OF_DAY, 9);
        calTo.set(Calendar.MINUTE, 30);

        for (int loop=0; loop<17; loop++) {
            Label label = new Label();
            label.setText(sdf.format(calFrom.getTime())+"-"+sdf.format(calTo.getTime()));
            pomodoroGrid.addColumn(0, label);
            calFrom.add(Calendar.MINUTE, 30);
            calTo.add(Calendar.MINUTE, 30);
        }
    }

    public void showPomodoroList() {

    }
}
