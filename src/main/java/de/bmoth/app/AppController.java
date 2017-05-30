package de.bmoth.app;

import com.google.common.eventbus.Subscribe;
import de.bmoth.checkers.initialstateexists.InitialStateExistsChecker;
import de.bmoth.checkers.initialstateexists.InitialStateExistsCheckingResult;
import de.bmoth.checkers.invariantsatisfiability.InvariantSatisfiabilityChecker;
import de.bmoth.checkers.invariantsatisfiability.InvariantSatisfiabilityCheckingResult;
import de.bmoth.eventbus.ErrorEvent;
import de.bmoth.eventbus.EventBusProvider;
import de.bmoth.modelchecker.ModelChecker;
import de.bmoth.modelchecker.ModelCheckingResult;
import de.bmoth.preferences.BMothPreferences;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppController implements Initializable {
    private static final String APPNAME = "Bmoth";
    private final Logger logger = Logger.getLogger(getClass().getName());
    @FXML
    MenuItem customCheck;
    @FXML
    MenuItem newFile;
    @FXML
    MenuItem open;
    @FXML
    MenuItem save;
    @FXML
    MenuItem saveAs;
    @FXML
    MenuItem options;
    @FXML
    MenuItem exit;
    @FXML
    MenuItem modelCheck;
    @FXML
    CodeArea codeArea;
    @FXML
    TextArea infoArea;
    private Stage primaryStage = new Stage();
    private String currentFile;
    private Boolean hasChanged = false;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY));
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_ANY));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_ANY));
        primaryStage.setTitle(APPNAME);
        codeArea.selectRange(0, 0);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
            .subscribe(change -> codeArea.setStyleSpans(0, Highlighter.computeHighlighting(codeArea.getText())));
        codeArea.setStyleSpans(0, Highlighter.computeHighlighting(codeArea.getText()));

        EventBusProvider.getInstance();
        EventBusProvider.getInstance().getEventBus().register(this);
    }

    void setupStage(Stage stage) {
        primaryStage = stage;
        setupPersonalPreferences();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            handleExit();
        });
    }

    private void setupPersonalPreferences() {
        if (!BMothPreferences.getStringPreference(BMothPreferences.StringPreference.LAST_FILE).isEmpty()) {
            currentFile = BMothPreferences.getStringPreference(BMothPreferences.StringPreference.LAST_FILE);
            if (new File(currentFile).exists()) {
                File file = new File(currentFile);
                String fileContent = openFile(file);
                codeArea.replaceText(fileContent);
                primaryStage.setTitle(APPNAME + " - " + file.getName().substring(0, file.getName().length() - 4));
            }
            codeArea.getUndoManager().forgetHistory();

        }
        codeArea.textProperty().addListener((observableValue, s, t1) -> {
            hasChanged = true;
            infoArea.setText("Unsaved changes");
        });
    }

    // <editor-fold desc="Menu handlers">
    @FXML
    public void handleNew() {
        int nextStep = -1;
        if (hasChanged) {
            nextStep = handleUnsavedChanges();
        }
        if (nextStep != 0) {
            codeArea.replaceText("");
            codeArea.getUndoManager().forgetHistory();
            codeArea.selectRange(0, 0);
            currentFile = null;
            hasChanged = false;
            primaryStage.setTitle(APPNAME + " - " + "New Machine");
            infoArea.clear();
        }
    }

    @FXML
    public void handleOpen() {
        int nextStep = -1;
        if (hasChanged) {
            nextStep = handleUnsavedChanges();
        }
        if (nextStep == -1 || !hasChanged) {
            String fileContent = openFileChooser();
            if (fileContent != null) {
                codeArea.replaceText(fileContent);
                codeArea.getUndoManager().forgetHistory();
                codeArea.selectRange(0, 0);
                hasChanged = false;
                infoArea.clear();
            }
        }
    }

    @FXML
    public void handleSave() {
        if (currentFile != null) {
            try {
                saveFile(currentFile);
                hasChanged = false;
                infoArea.clear();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "While Saving", e);
            }
        } else {
            handleSaveAs();
        }
    }

    @FXML
    public Boolean handleSaveAs() {
        try {
            Boolean saved = saveFileAs();
            if (saved) {
                hasChanged = false;
                infoArea.clear();
            }
            return saved;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "While Saving AS", e);
        }
        return false;
    }

    @FXML
    public void handleExit() {
        if (hasChanged) {
            int nextStep = handleUnsavedChanges();
            if (nextStep == -1 || !hasChanged) {
                Platform.exit();
            }

        } else {
            Platform.exit();
        }
    }

    @FXML
    public void handleOptions() throws IOException {
        Stage optionStage = new Stage();
        optionStage.setTitle("Options");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("options.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        optionStage.setScene(scene);
        optionStage.show();
    }

    @FXML
    public void handleCheck() {
        if (codeArea.getText().replaceAll("\\s+", "").length() > 0) {
            ModelCheckingResult result = ModelChecker.doModelCheck(codeArea.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Model Checking Result");
            alert.setHeaderText("The model is...");
            if (result.isCorrect()) {
                alert.setContentText("...correct!\nNo counter-example found.");
            } else if (result.getMessage().equals("")) {
                alert.setContentText("...not correct!\nCounter-example found in state " + result.getLastState().toString()
                    + ".\nReversed path: " + ModelCheckingResult.getPath(result.getLastState()));
            } else {
                alert.setContentText("...Schrödinger's cat.\nSomething went wrong.\n"
                    + result.getMessage());
            }
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    @FXML
    public void handleCustomCheck() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customCheck.fxml"));
        Parent root = loader.load();
        CustomCheckController customCheckController = loader.getController();
        Stage customCheckStage = customCheckController.getStage(root);
        customCheckController.setAppControllerReference(this);
        customCheckStage.show();
    }

    @FXML
    public void handleInvariantSatisfiability() {
        if (codeArea.getText().replaceAll("\\s+", "").length() > 0) {
            InvariantSatisfiabilityCheckingResult result = InvariantSatisfiabilityChecker.doInvariantSatisfiabilityCheck(codeArea.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invariant Satisfiability Checking Result");
            alert.setHeaderText("The invariant is...");
            switch (result.getResult()) {

                case UNSATISFIABLE:
                    alert.setContentText("...unsatisfiable!\nThe model is probably not correct.");
                    break;
                case UNKNOWN:
                    alert.setContentText("...unknown!\nThe invariant is too complex for the backend.");
                    break;
                case SATISFIABLE:
                    alert.setContentText("...satisfiable!");
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled result: " + result.toString());
            }

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    @FXML
    public void handleREPL() throws IOException {
        Stage replStage = new Stage();
        replStage.setTitle("REPL");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("repl.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 300);
        replStage.setScene(scene);
        replStage.show();

    }
    // </editor-fold>

    private int handleUnsavedChanges() {
        int nextStep = saveChangedDialog();
        switch (nextStep) {
            case 0:
                break;
            case 1:
                handleSave();
                break;
            case 2:
                handleSaveAs();
                break;
            case -1:
                break;
            default:
                break;
        }
        return nextStep;
    }

    /**
     * Open a confirmation-alert to decide how to proceed with unsaved changes.
     *
     * @return UserChoice as Integer: -1 = Ignore, 0 = Cancel, 1 = Save , 2 = SaveAs
     */
    private int saveChangedDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("UNSAVED CHANGES!");
        alert.setHeaderText("Unsaved Changes! What do you want to do?");
        alert.setContentText(null);

        ButtonType buttonTypeSave = new ButtonType("Save");
        ButtonType buttonTypeSaveAs = new ButtonType("Save As");
        ButtonType buttonTypeIgnoreChanges = new ButtonType("Ignore");
        ButtonType buttonTypeCancel = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeSaveAs, buttonTypeIgnoreChanges, buttonTypeCancel);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonTypeSave) return 1;
            if (result.get() == buttonTypeSaveAs) return 2;
            if (result.get() == buttonTypeCancel) return 0;
            if (result.get() == buttonTypeIgnoreChanges) return -1;
        }
        return 0;
    }

    // <editor-fold desc="File operations">

    /**
     * Save codeArea to a file.
     *
     * @param path Save-location
     * @throws IOException
     */
    private void saveFile(String path) throws IOException {
        File file = new File(path);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(codeArea.getText());
            primaryStage.setTitle(APPNAME + " - " + file.getName().substring(0, file.getName().length() - 4));
        }
    }

    /**
     * Ask for location and name and save codeArea.
     *
     * @throws IOException
     * @see #saveFile(String)
     */
    private Boolean saveFileAs() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(BMothPreferences.getStringPreference(BMothPreferences.StringPreference.LAST_DIR)));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MCH File", "*.mch"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {     //add .mch ending if not added by OS
            if (!file.getAbsolutePath().endsWith(".mch")) {
                saveFile(file.getAbsolutePath() + ".mch");
            } else {
                saveFile(file.getAbsolutePath());
            }
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_FILE, file.getAbsolutePath());
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_DIR, file.getParent());
            return true;
        } else return false;
    }

    /**
     * Ask the user which file to open into the textarea. If the file is found, openFile is called.
     *
     * @return Returns the filepath as string or null if cancelled
     * @see #openFile(File)
     */
    private String openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open MCH File", "*.mch"));
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(new File(BMothPreferences.getStringPreference(BMothPreferences.StringPreference.LAST_DIR)));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            currentFile = file.getPath();
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_FILE, file.getAbsolutePath());
            BMothPreferences.setStringPreference(BMothPreferences.StringPreference.LAST_DIR, file.getParent());
            primaryStage.setTitle(APPNAME + " - " + file.getName().substring(0, file.getName().length() - 4));
            return openFile(file);
        }
        return null;
    }

    /**
     * Load a given file into the CodeArea and change the title of the stage.
     *
     * @param file File to read from
     */
    private String openFile(File file) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "While Reading File", e);
        }
        return content;
    }
    // </editor-fold>

    // <editor-fold desc="Event Bus Subscriptions">
    @Subscribe
    public void showException(ErrorEvent event) {
        new ErrorAlert(Alert.AlertType.ERROR, event.getErrorType(), event.getMessage());
    }
    // </editor-fold>


    public void handleInitialStateExists() {
        if (codeArea.getText().replaceAll("\\s+", "").length() > 0) {
            InitialStateExistsCheckingResult result = InitialStateExistsChecker.doInitialStateExistsCheck(codeArea.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invariant Satisfiability Checking Result");
            alert.setHeaderText("Initial state...");
            switch (result.getResult()) {
                case UNSATISFIABLE:
                    alert.setContentText("...does not exists!\nThe model is probably not correct.");
                    break;
                case UNKNOWN:
                    alert.setContentText("...is unknown!\nThe initialization is too complex for the backend.");
                    break;
                case SATISFIABLE:
                    alert.setContentText("...exists!");
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled result: " + result.toString());
            }

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }
}
