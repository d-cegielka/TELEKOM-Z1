package telekom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    public void openFile() throws IOException {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wczytaj dane");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pliki tekstowe (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        /*String path = "C:\\TELEKOM-Z1\\data.txt";
        inputFile = new File(path);*/
        inputFile = fileChooser.showOpenDialog(new Stage());
        reportArea.setText(Files.readString(Paths.get(inputFile.getPath())));
    }

    @FXML
    public void encodeData(){
        try {
            MistakeDetector encoder = new MistakeDetector();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz dane");

            //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pliki tekstowe (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

           // String path = "C:\\TELEKOM-Z1\\data_out.txt";
            String path1 = "D:\\TELEKOM-Z1\\data_out_encoded.txt";
            //Show save file dialog
            //File encodedFile = fileChooser.showSaveDialog(new Stage());
            File encodedFile = new File(path1);
            reportArea.setText(encoder.encodeFile(inputFile,encodedFile).toString());
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
    public void decodeData() {
        try {
            MistakeDetector decoder = new MistakeDetector();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz dane");

            //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pliki tekstowe (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            // String path = "C:\\TELEKOM-Z1\\data_out.txt";
            String path1 = "D:\\TELEKOM-Z1\\data_out_decoded.txt";
            //Show save file dialog
            //File encodedFile = fileChooser.showSaveDialog(new Stage());
            File decodedFile = new File(path1);
            reportArea.setText(decoder.decodeFile(inputFile,decodedFile).toString());
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
