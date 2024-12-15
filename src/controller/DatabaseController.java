package controller;

import database_connections.DataBaseManager;
import database_connections.MotionRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DatabaseController implements InteractionWithPreview, Initializable {
    @FXML
    private VBox recordsContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataBaseManager manager = new DataBaseManager();
        List<MotionRecord> records = manager.retrieveRecords();

        for (MotionRecord record : records) {
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(20);
            grid.setVgap(10);

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setHalignment(HPos.CENTER);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHalignment(HPos.CENTER);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setHalignment(HPos.CENTER);
            grid.getColumnConstraints().addAll(col1, col2, col3);

            Text idText = new Text("ID: " + record.id());
            Text timestampText = new Text("Timestamp: " + record.timestamp());
            ImageView imageView = new ImageView();

            if (record.imageData() != null) {
                Image img = new Image(new ByteArrayInputStream(record.imageData()));
                imageView.setImage(img);
                imageView.setFitWidth(150);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
            }

            grid.add(idText, 0, 0);
            grid.add(timestampText, 1, 0);
            grid.add(imageView, 2, 0);

            recordsContainer.getChildren().add(grid);
        }
    }
    @Override
    public void navigateToPreview(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Preview.fxml"));
            Parent previewRoot = loader.load();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(previewRoot));
            currentStage.setTitle("Preview Frame");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteContent() {
        DataBaseManager manager = new DataBaseManager();
        try {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to delete all records?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                manager.deleteAllRecords();
                JOptionPane.showMessageDialog(null, "Records deleted successfully. You can go back to Preview Frame");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + ", could not delete content");
        }
    }

}
