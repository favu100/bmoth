package de.bmoth.app;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class OptionController {

    private static final String NONNUMERICWARNING = "Not Numeric or out of Integer-Range: ";
    @FXML
    Button cancelButton;
    @FXML
    Button okButton;
    @FXML
    Button applyButton;
    @FXML
    TextField minInt;
    @FXML
    TextField maxInt;
    @FXML
    TextField maxInitState;
    @FXML
    TextField maxTrans;
    @FXML
    TextField z3Timeout;

    private Stage stage;

    Stage getStage(Parent root) {
        if (stage != null) return stage;
        Scene scene = new Scene(root);
        this.stage = new Stage();
        stage.setScene(scene);
        setupStage();
        return stage;
    }

    private void setupStage() {
        stage.setTitle("Options");
        // selects end of first Textfield for Caret
        setUpPrefs();
        minInt.requestFocus();
        minInt.selectRange(99, 99);
    }

    void setUpPrefs() {
        minInt.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MIN_INT)));
        maxInt.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INT)));
        maxInitState.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE)));
        maxTrans.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS)));
        z3Timeout.setText(String.valueOf(BMothPreferences.getIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT)));
    }

    boolean checkPrefs() {
        if (!StringUtils.isNumeric(maxInt.getText())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + minInt.getId()).show();
            return false;
        }
        if (!StringUtils.isNumeric(maxInt.getText())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + maxInt.getId()).show();
            return false;
        }
        if (!StringUtils.isNumeric(maxInitState.getText())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + maxInitState.getId()).show();
            return false;
        }

        if (!StringUtils.isNumeric(maxTrans.getText())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + maxTrans.getId()).show();
            return false;
        }
        if (!StringUtils.isNumeric(z3Timeout.getText())) {
            new Alert(Alert.AlertType.ERROR, NONNUMERICWARNING + z3Timeout.getId()).show();
            return false;
        }

        if (Integer.parseInt(z3Timeout.getText()) < 0) {
            new Alert(Alert.AlertType.ERROR, "Timout needs to be a positive Value").show();
            return false;
        }


        if (Integer.parseInt(minInt.getText()) > Integer.parseInt(maxInt.getText())) {
            new Alert(Alert.AlertType.ERROR, "MIN_INT bigger than MAX_INT").show();
            return false;
        }
        if (Integer.parseInt(maxInitState.getText()) < 1) {
            new Alert(Alert.AlertType.ERROR, "InitialStates needs to be bigger than 0").show();
            return false;
        }
        if (Integer.parseInt(maxTrans.getText()) < 1) {
            new Alert(Alert.AlertType.ERROR, "Maximum transitions needs to be bigger than 0").show();
            return false;
        }


        return true;
    }


    void savePrefs() {
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MIN_INT, minInt.getText());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INT, maxInt.getText());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_INITIAL_STATE, maxInitState.getText());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.MAX_TRANSITIONS, maxTrans.getText());
        BMothPreferences.setIntPreference(BMothPreferences.IntPreference.Z3_TIMEOUT, z3Timeout.getText());
    }


    public void handleApply() {
        if (checkPrefs())
            savePrefs();
    }

    public void handleClose() {
        stage.close();
    }

    public void handleOk() {
        if (checkPrefs()) {
            savePrefs();
            stage.close();
        }
    }
}
