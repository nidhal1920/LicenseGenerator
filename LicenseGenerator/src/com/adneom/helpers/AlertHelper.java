package com.adneom.helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * @author Nidhal Chayeb
 * @since LicenseGenerator1.0
 */
public class AlertHelper {

	// Common errors
	public static final String ERROR_GEN_KEYS = "An error occurred while generating the keys.";
	public static final String ERROR_GEN_LIC = "An error occured while generating the license.";
	public static final String ERROR_GEN_LIC_INFO = "An error occured while generating the license release note.";

	public static final String ERROR_LOADING_CONFIG = "An error occured while loading the software configuration file !";

	public static void alertSuccess(String msg) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Sucess!");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.getDialogPane().setPrefHeight(250);
		alert.showAndWait();
	}

	public static void alertError(String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	public static void alertException(String msg, Exception ex) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception");
		alert.setContentText(msg);

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.getDialogPane().setPrefSize(400, 300);
		alert.showAndWait();
	}

	private static Image imageExplorer = new Image("icons/loading.gif");
	private static String lastText = null;
	private static Node lastGraphic = null;

	public static void startLoading(Button btn) {
		btn.setDisable(true);
		lastGraphic = btn.getGraphic();
		btn.setGraphic(new ImageView(imageExplorer));
		lastText = btn.getText();
		btn.setText("Generating...");
	}

	public static void stopLoading(Button btn) {
		btn.setGraphic(lastGraphic);
		btn.setText(lastText);
		btn.setDisable(false);
	}

	private static final String DEFAULT_FILE_PARAGRAPH_SEPARATOR = "-------------------------------------------------------------";
	private static final String LN = System.getProperty("line.separator");

	public static void generateInfoFile(Properties properties, String filePath, String licKey) throws UnsupportedEncodingException, IOException {
		StringBuffer fileContent = new StringBuffer(DEFAULT_FILE_PARAGRAPH_SEPARATOR + LN);
		fileContent.append("License notes");
		fileContent.append(LN);

		fileContent.append(DEFAULT_FILE_PARAGRAPH_SEPARATOR + LN);
		fileContent.append("User = " + System.getProperty("user.name"));
		fileContent.append(LN);
		fileContent.append("Release date = " + new Date());
		fileContent.append(LN);
		fileContent.append("Key = " + licKey);
		fileContent.append(LN);

		fileContent.append(DEFAULT_FILE_PARAGRAPH_SEPARATOR + LN);
		properties.forEach((key, value) -> {
			fileContent.append(key + " = " + value);
			fileContent.append(LN);
		});
		fileContent.append(DEFAULT_FILE_PARAGRAPH_SEPARATOR);
		Files.write(Paths.get(filePath), fileContent.toString().getBytes("utf-8"));

	}

}
