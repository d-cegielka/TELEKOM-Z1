package telekom;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {
    private File inputFile;
    @FXML
    private TextArea reportArea;

    @FXML
    public File openFile(String info) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(info);
        /*String path = "C:\\TELEKOM-Z1\\data.txt";
        inputFile = new File(path);*/
        File getFile = fileChooser.showOpenDialog(new Stage());
        return getFile;
        //reportArea.setText(new String(Files.readAllBytes(Paths.get(inputFile.getPath()))));
    }

    @FXML
    public File saveFile(String info) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(info);
        /*String path = "C:\\TELEKOM-Z1\\data.txt";
        inputFile = new File(path);*/
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Wszystkie pliki (*.*)", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        File saveFile = fileChooser.showSaveDialog(new Stage());
        return saveFile;
        //reportArea.setText(new String(Files.readAllBytes(Paths.get(inputFile.getPath()))));
    }

    @FXML
    public void encodeFile(){
        try {
            MistakeDetector encoder = new MistakeDetector();
            File inputFile = openFile("Wczytaj dane do kodowania");
            File outputFile = saveFile("Zapisz zakodowane dane");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz dane");

            //Set extension filter for text files
            //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pliki tekstowe (*.txt)", "*.txt");
            //fileChooser.getExtensionFilters().add(extFilter);

            //String path = "C:\\TELEKOM-Z1\\data_out_encoded.txt";
            //String path1 = "D:\\TELEKOM-Z1\\data_out_encoded.txt";
            //Show save file dialog
            //File encodedFile = fileChooser.showSaveDialog(new Stage());
            //File encodedFile = new File(path);
            encoder.encodeFile(inputFile,outputFile);
            alertInformation("Plik został zakodowany pomyślnie. \n"+ outputFile.getPath());

        } catch (IOException e) {
            alertHandling(e.getMessage());
        }
    }

    @FXML
    public void encodeText(){
        MistakeDetector encoder = new MistakeDetector();
        try {
            reportArea.setText(encoder.encodeText(reportArea.getText()).toString());
        } catch (IOException e) {
            alertHandling(e.getMessage());
        }
    }

    @FXML
    public void decodeText(){
        MistakeDetector decoder = new MistakeDetector();
        try {
            reportArea.setText(decoder.decodeText(reportArea.getText()).toString());
        } catch (IOException e) {
            alertHandling(e.getMessage());
        }
    }

    @FXML
    public void alertHandling(String alertInfo) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Look, a Warning Dialog");
        alert.setContentText(alertInfo);

        alert.showAndWait();
    }

    @FXML
    public void alertInformation(String alertInfo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Look, a Information Dialog");
        alert.setContentText(alertInfo);

        alert.showAndWait();
    }

    @FXML
    public void decodeFile() {
        try {
            MistakeDetector encoder = new MistakeDetector();
            File inputFile = openFile("Wczytaj dane do odkodowania");
            File outputFile = saveFile("Zapisz odkodowane dane");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz dane");

            //Set extension filter for text files
            //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pliki tekstowe (*.txt)", "*.txt");
            //fileChooser.getExtensionFilters().add(extFilter);

            //String path = "C:\\TELEKOM-Z1\\data_out_encoded.txt";
            //String path1 = "D:\\TELEKOM-Z1\\data_out_encoded.txt";
            //Show save file dialog
            //File encodedFile = fileChooser.showSaveDialog(new Stage());
            //File encodedFile = new File(path);
            encoder.decodeFile(inputFile,outputFile);
            alertInformation("Plik został odkodowany pomyślnie. \n"+ outputFile.getPath());

        } catch (IOException e) {
            alertHandling(e.getMessage());
        }
    }

    @FXML
    public void quitApp()
    {
        Platform.exit();
    }
}
