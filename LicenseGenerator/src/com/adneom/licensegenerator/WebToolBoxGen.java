package com.adneom.licensegenerator;

import java.io.IOException;
import java.util.Properties;

import com.adneom.helpers.AlertHelper;
import com.adneom.helpers.OSValidator;
import com.adneom.scenes.KeyGenScene;
import com.adneom.scenes.LicenseGenScene;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * The main entry point of this application.
 * 
 * @author Nidhal Chayeb
 * @since LicenseGenerator1.0 .
 * 
 */
public class WebToolBoxGen extends Application {
	Properties configuration = new Properties();

	/*
	 * The start method is called after the init method has returned, and after the system is ready for the application to begin running.
	 *
	 * @param primaryStage the primary stage for this application.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		loadConfiguration();

		Pane root = new Pane();

		Label version = new Label("Version :");
		version.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		root.getChildren().add(version);
		version.relocate(20, 30);

		Label versionValue = new Label("Unknown");
		versionValue.setFont(Font.font("Verdana", 20));
		root.getChildren().add(versionValue);
		versionValue.relocate(200, 40);

		if(configuration.containsKey("version")){
			versionValue.setText(configuration.getProperty("version"));
		}
		Label os = new Label("OS :");
		os.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		root.getChildren().add(os);
		os.relocate(20, 80);

		Label osValue = new Label(OSValidator.getOs() + OSValidator.getOs().getVersion());
		osValue.setFont(Font.font("Verdana", 20));
		root.getChildren().add(osValue);
		osValue.relocate(200, 90);

		final ToggleGroup group = new ToggleGroup();

		RadioButton rb1 = new RadioButton("Generate new license");
		rb1.setToggleGroup(group);
		rb1.setSelected(true);

		RadioButton rb2 = new RadioButton("Generate Private/Public keys");
		rb2.setToggleGroup(group);

		VBox box = new VBox(20, rb1, rb2);
		root.getChildren().add(box);
		box.relocate(100, 180);

		Scene sceneIntro = new Scene(root, 500, 500);

		Button btnStart = new Button("Start");
		root.getChildren().add(btnStart);
		btnStart.relocate(250, 270);
		btnStart.setDisable(!(OSValidator.OS.UNIX.equals(OSValidator.getOs()) || OSValidator.OS.WINDOWS.equals(OSValidator.getOs())));
		btnStart.setOnAction(e -> start(primaryStage, sceneIntro, group, rb1, rb2));

		primaryStage.setTitle("License and private/public keys generator");
		primaryStage.setScene(sceneIntro);
		primaryStage.show();
		primaryStage.setResizable(false);
	}

	private void start(Stage primaryStage, Scene sceneIntro, ToggleGroup group, RadioButton rb1, RadioButton rb2) {
		if ((group.getSelectedToggle() == null))
			return;

		if (rb1.equals(group.getSelectedToggle())) {
			new LicenseGenScene(primaryStage, sceneIntro, 600, 600);
		} else if (rb2.equals(group.getSelectedToggle())) {
			new KeyGenScene(primaryStage, sceneIntro, 500, 500);
		}
	}

	private void loadConfiguration() {
		configuration = new Properties();
		try {
			configuration.load(ClassLoader.class.getResourceAsStream("/configuration/configuration.properties"));
		} catch (IOException e) {
			AlertHelper.alertException(AlertHelper.ERROR_LOADING_CONFIG, e);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}