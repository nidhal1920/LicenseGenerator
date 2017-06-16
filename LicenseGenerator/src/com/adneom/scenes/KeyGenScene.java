package com.adneom.scenes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.adneom.configuration.Configuration;
import com.adneom.configuration.Configuration.KeyType;
import com.adneom.helpers.AlertHelper;
import com.adneom.helpers.OSValidator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * @author Nidhal Chayeb
 * @since LicenseGenerator1.0
 */
public class KeyGenScene {
	Stage currentStage = null;
	int width = 500;
	int height = 500;
	Configuration configuration = new Configuration();

	// Intro scene
	Scene sceneIntro = null;
	// Scenes list
	Scene initScene = null;
	Scene keySizeScene = null;
	Scene keyValidPeriod = null;
	Scene identificationScene = null;

	// InitScene
	ToggleGroup groupKeyType = null;
	RadioButton rbKeyType1 = null;
	RadioButton rbKeyType2 = null;
	// Key Size
	TextField keySizeTextField = null;
	// Validity period
	ToggleGroup groupValidPeriod = null;
	RadioButton rbValidPeriod1 = null;
	RadioButton rbValidPeriod2 = null;
	RadioButton rbValidPeriod3 = null;
	RadioButton rbValidPeriod4 = null;
	RadioButton rbValidPeriod5 = null;
	TextField validityPeriodValue = null;
	// Identification
	TextField userIDTextField = null;
	TextField emailTextField = null;
	TextField textFieldPassphrase = null;
	TextField KeysOutLoc = null;
	Button btnGenerate = null;

	public KeyGenScene(Stage primaryStage, Scene sceneIntro, int width, int height) {
		this.currentStage = primaryStage;
		this.width = width;
		this.height = height;
		this.sceneIntro = sceneIntro;
		initScene();
	}

	public void initScene() {
		Pane panel = new Pane();
		initScene = new Scene(panel, this.width, this.height);

		Label configuration = new Label("Generate Private/Public keys");
		configuration.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		panel.getChildren().add(configuration);
		configuration.relocate(120, 30);

		Label keySelection = new Label("Please select what kind of key you want:");
		keySelection.setFont(Font.font("Verdana", 20));
		panel.getChildren().add(keySelection);
		keySelection.relocate(20, 110);

		groupKeyType = new ToggleGroup();

		rbKeyType1 = new RadioButton("(1) DSA");
		rbKeyType1.setToggleGroup(groupKeyType);
		rbKeyType1.setSelected(true);

		rbKeyType2 = new RadioButton("(2) RSA");
		rbKeyType2.setToggleGroup(groupKeyType);

		VBox box = new VBox(20, rbKeyType1, rbKeyType2);
		panel.getChildren().add(box);
		box.relocate(20, 150);

		Button btnNext = new Button("Next");
		panel.getChildren().add(btnNext);
		btnNext.relocate(300, 300);
		btnNext.setOnAction(e -> goForward());

		Button btnBack = new Button("Back");
		panel.getChildren().add(btnBack);
		btnBack.relocate(250, 300);
		btnBack.setOnAction(e -> goBack());

		currentStage.setScene(initScene);
	}

	public void setKeySize() {
		Pane panel = new Pane();
		keySizeScene = new Scene(panel, this.width, this.height);

		Label configuration = new Label("Generate Private/Public keys");
		configuration.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		panel.getChildren().add(configuration);
		configuration.relocate(120, 30);

		Label keySizeInfo = new Label("RSA keys may be between 1024 and 4096 bits long.\n What keysize do you want?:");
		keySizeInfo.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(keySizeInfo);
		keySizeInfo.relocate(20, 110);
		keySizeTextField = new TextField();
		keySizeTextField.setText("2048");
		panel.getChildren().add(keySizeTextField);
		keySizeTextField.relocate(150, 170);
		// force the field to be numeric only
		final Button btnNext = new Button("Next");
		panel.getChildren().add(btnNext);
		btnNext.relocate(300, 300);
		btnNext.setOnAction(e -> goForward());

		Button btnBack = new Button("Back");
		panel.getChildren().add(btnBack);
		btnBack.relocate(250, 300);
		btnBack.setOnAction(e -> goBack());

		keySizeTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					keySizeTextField.setText(newValue.replaceAll("[^\\d]", ""));
				} else {
					int keySizeValue = 0;
					try {
						keySizeValue = Integer.parseInt(newValue);
					} catch (NumberFormatException exception) {
						// Nothing to Do : revert
					}
					btnNext.setDisable((keySizeValue < 1024) || (keySizeValue > 4096));
				}
			}
		});

	}

	public void setKeyValidationPeriod() {
		Pane panel = new Pane();
		keyValidPeriod = new Scene(panel, this.width, this.height);

		Label configuration = new Label("Generate Private/Public keys");
		configuration.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		panel.getChildren().add(configuration);
		configuration.relocate(120, 30);

		Label keySizeInfo = new Label("Please specify how long the key should be valid.");
		keySizeInfo.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(keySizeInfo);
		keySizeInfo.relocate(20, 110);

		groupValidPeriod = new ToggleGroup();

		rbValidPeriod1 = new RadioButton("Key does not expire");
		rbValidPeriod1.setToggleGroup(groupValidPeriod);

		rbValidPeriod2 = new RadioButton("Days");
		rbValidPeriod2.setToggleGroup(groupValidPeriod);

		rbValidPeriod3 = new RadioButton("Weeks");
		rbValidPeriod3.setToggleGroup(groupValidPeriod);
		rbValidPeriod3.setSelected(true);

		rbValidPeriod4 = new RadioButton("Months");
		rbValidPeriod4.setToggleGroup(groupValidPeriod);

		rbValidPeriod5 = new RadioButton("Years");
		rbValidPeriod5.setToggleGroup(groupValidPeriod);

		VBox box1 = new VBox(20, rbValidPeriod1, rbValidPeriod2, rbValidPeriod3);
		VBox box2 = new VBox(20, rbValidPeriod4, rbValidPeriod5);
		HBox hBox = new HBox(40, box1, box2);
		panel.getChildren().add(hBox);
		hBox.relocate(20, 150);

		validityPeriodValue = new TextField();
		validityPeriodValue.setText("2");
		panel.getChildren().add(validityPeriodValue);
		validityPeriodValue.relocate(20, 260);
		// force the field to be numeric only
		final Button btnNext = new Button("Next");
		panel.getChildren().add(btnNext);
		btnNext.relocate(300, 300);
		btnNext.setOnAction(e -> confirmNeverExpire(groupValidPeriod, rbValidPeriod1));

		Button btnBack = new Button("Back");
		panel.getChildren().add(btnBack);
		btnBack.relocate(250, 300);
		btnBack.setOnAction(e -> goBack());

		validityPeriodValue.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					validityPeriodValue.setText(newValue.replaceAll("[^\\d]", ""));
				} else {
					btnNext.setDisable(newValue.length() > 5);
				}
			}
		});

		groupValidPeriod.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if ((groupValidPeriod.getSelectedToggle() != null) && (rbValidPeriod1.equals(groupValidPeriod.getSelectedToggle()))) {
					// Never expire radio button
					validityPeriodValue.setDisable(true);
					validityPeriodValue.setText("0");
					btnNext.setDisable(false);

				} else {
					// All others radio button
					validityPeriodValue.setDisable(false);
					btnNext.setDisable("0".equals(validityPeriodValue.getText()));
				}
			}
		});

	}

	public void setIdentification() {
		Pane panel = new Pane();
		identificationScene = new Scene(panel, this.width, this.height);

		Label configuration = new Label("Generate Private/Public keys");
		configuration.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		panel.getChildren().add(configuration);
		configuration.relocate(120, 30);

		Label keySizeInfo = new Label("You need a user ID to identify your key.");
		keySizeInfo.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(keySizeInfo);
		keySizeInfo.relocate(20, 110);

		Label nameLbl = new Label("UserID");
		nameLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(nameLbl);
		nameLbl.relocate(20, 150);

		userIDTextField = new TextField();
		panel.getChildren().add(userIDTextField);
		userIDTextField.relocate(110, 150);
		userIDTextField.setPrefWidth(200);

		Label emailLbl = new Label("Email");
		emailLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(emailLbl);
		emailLbl.relocate(20, 180);

		emailTextField = new TextField();
		panel.getChildren().add(emailTextField);
		emailTextField.relocate(110, 180);
		emailTextField.setPrefWidth(200);

		Label keyParaph = new Label("You need a Passphrase to protect your secret key.");
		keyParaph.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(keyParaph);
		keyParaph.relocate(20, 220);

		Label passphrase = new Label("Passphrase");
		passphrase.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(passphrase);
		passphrase.relocate(20, 250);

		textFieldPassphrase = new TextField();
		textFieldPassphrase.setText("A Passphrase here...");
		panel.getChildren().add(textFieldPassphrase);
		textFieldPassphrase.relocate(110, 250);
		textFieldPassphrase.setPrefWidth(340);

		// License dest file
		Label destFolder = new Label("Destination folder");
		destFolder.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(destFolder);
		destFolder.relocate(20, 300);

		KeysOutLoc = new TextField();
		panel.getChildren().add(KeysOutLoc);
		KeysOutLoc.relocate(170, 300);
		KeysOutLoc.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				validateGenerateBtn();
			}

		});

		Button btnSelectOut = new Button("");
		Image imageExplorer = new Image("icons/folder-explorer-icon.png");
		btnSelectOut.setGraphic(new ImageView(imageExplorer));
		panel.getChildren().add(btnSelectOut);
		btnSelectOut.relocate(350, 300);
		btnSelectOut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Destination folder");
				File defaultDirectory = new File(OSValidator.getDefaultKeyStorePath());
				chooser.setInitialDirectory(defaultDirectory);
				File selectedDirectory = chooser.showDialog(currentStage);
				if (selectedDirectory != null) {
					KeysOutLoc.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});

		// force the field to be numeric only
		btnGenerate = new Button("Generate");
		panel.getChildren().add(btnGenerate);
		btnGenerate.relocate(300, 350);
		btnGenerate.setOnAction(e -> goForward());
		validateGenerateBtn();
		Button btnBack = new Button("Back");
		panel.getChildren().add(btnBack);
		btnBack.relocate(250, 350);
		btnBack.setOnAction(e -> goBack());

	}

	private void confirmNeverExpire(ToggleGroup group, RadioButton rb1) {
		if ((group.getSelectedToggle() != null) && (rb1.equals(group.getSelectedToggle()))) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText("Key does not expire at all");
			alert.setContentText("are you sure ?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				goForward();
			} else {
				// Nothing to do
			}
		} else {
			goForward();
		}
	}

	private void goBack() {
		if (currentStage.getScene().equals(initScene)) {
			currentStage.setScene(sceneIntro);
		} else if (currentStage.getScene().equals(keySizeScene)) {
			currentStage.setScene(initScene);
		} else if (currentStage.getScene().equals(keyValidPeriod)) {
			currentStage.setScene(keySizeScene);
		} else if (currentStage.getScene().equals(identificationScene)) {
			currentStage.setScene(keyValidPeriod);
		}
	}

	private void goForward() {
		if (currentStage.getScene().equals(initScene)) {
			// Lazy inst
			if (keySizeScene == null)
				setKeySize();

			if (groupKeyType.getSelectedToggle().equals(rbKeyType1)) {
				configuration.setKeyType(KeyType.DSA);
			} else if (groupKeyType.getSelectedToggle().equals(rbKeyType2)) {
				configuration.setKeyType(KeyType.RSA);
			}
			currentStage.setScene(keySizeScene);

		} else if (currentStage.getScene().equals(keySizeScene)) {
			// Lazy inst
			if (keyValidPeriod == null)
				setKeyValidationPeriod();
			int keySizeValue = -1;
			try {
				keySizeValue = Integer.parseInt(keySizeTextField.getText());
				if (keySizeValue > 0)
					configuration.setKeySize(keySizeValue);
			} catch (NumberFormatException exception) {
				// Nothing to Do
			}

			currentStage.setScene(keyValidPeriod);
		} else if (currentStage.getScene().equals(keyValidPeriod)) {
			// Lazy inst
			if (identificationScene == null)
				setIdentification();
			validateGenerateBtn();
			int keySizeValue = -1;
			try {
				keySizeValue = Integer.parseInt(validityPeriodValue.getText());
			} catch (NumberFormatException exception) {
				// Nothing to Do
			}

			if (groupValidPeriod.getSelectedToggle().equals(rbValidPeriod1)) {
				configuration.setValidityPeriod("0");
			} else if (groupValidPeriod.getSelectedToggle().equals(rbValidPeriod2)) {
				configuration.setValidityPeriod(keySizeValue + "");
			} else if (groupValidPeriod.getSelectedToggle().equals(rbValidPeriod3)) {
				configuration.setValidityPeriod(keySizeValue + "w");
			} else if (groupValidPeriod.getSelectedToggle().equals(rbValidPeriod4)) {
				configuration.setValidityPeriod(keySizeValue + "m");
			} else if (groupValidPeriod.getSelectedToggle().equals(rbValidPeriod5)) {
				configuration.setValidityPeriod(keySizeValue + "y");
			}

			currentStage.setScene(identificationScene);
		} else if (currentStage.getScene().equals(identificationScene)) {
			configuration.setUserID(userIDTextField.getText());
			configuration.setEmail(emailTextField.getText());
			configuration.setPassPhrase(textFieldPassphrase.getText());

			validateGenerateBtn();

			AlertHelper.startLoading(btnGenerate);
			String sucessMsg = null;
			if (!(sucessMsg = generateKeys()).isEmpty()) {
				AlertHelper.stopLoading(btnGenerate);
				AlertHelper.alertSuccess(sucessMsg);
				currentStage.setScene(sceneIntro);
			}
			AlertHelper.stopLoading(btnGenerate);
		}

	}

	private String generateKeys() {
		try {
			// Get the file reference
			Path path = Paths.get(System.getProperty("java.io.tmpdir") + "/AA" + System.currentTimeMillis());
			// Use try-with-resource to get auto-closeable writer instance
			try (BufferedWriter writer = Files.newBufferedWriter(path)) {
				writer.write("Key-Type: " + configuration.getKeyType().getValue());
				writer.newLine();
				writer.write("Key-Length: " + configuration.getKeySize());
				writer.newLine();
				writer.write("Expire-Date: " + configuration.getValidityPeriod());
				writer.newLine();
				writer.write("Name-Real: " + configuration.getUserID());
				writer.newLine();
				if (!configuration.getEmail().isEmpty()) {
					writer.write("Name-Email: " + configuration.getEmail());
					writer.newLine();
				}
				writer.write("Passphrase: " + configuration.getPassPhrase());
				writer.newLine();
			}

			String secretAbsPAth = KeysOutLoc.getText() + "/SecKey" + System.currentTimeMillis() + Configuration.DEFAULT_PRIVATE_KEY_EXT;
			String publicAbsPath = KeysOutLoc.getText() + "/PubKey" + System.currentTimeMillis() + Configuration.DEFAULT_PUBLIC_KEY_EXT;
			// Key generation
			OSValidator.executeKeyGen(path.toFile().getAbsolutePath(), secretAbsPAth, publicAbsPath);
			path.toFile().delete();
			if ((new File(secretAbsPAth)).exists()) {
				return "The keys have been generated successfully !\n  Private:\n" + secretAbsPAth + "\n Public:\n" + publicAbsPath;
			}
		} catch (IOException e) {
			AlertHelper.alertException(AlertHelper.ERROR_GEN_KEYS + "\nPlease verify the user r/w permissions on:" + KeysOutLoc, e);
			return "";
		}
		return "";
	}

	private void validateGenerateBtn() {
		if (btnGenerate != null)
			btnGenerate.setDisable(KeysOutLoc.getText().isEmpty());

	}

}
