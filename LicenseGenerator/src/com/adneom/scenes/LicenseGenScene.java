package com.adneom.scenes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import org.controlsfx.control.CheckComboBox;

import com.adneom.configuration.Configuration;
import com.adneom.helpers.AlertHelper;
import com.adneom.helpers.OSValidator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * @author Nidhal Chayeb
 * @since LicenseGenerator1.0
 */
public class LicenseGenScene {

	public static final String DEFAULT_AUTH_SEPARATOR = ",";
	// Errors messages
	public static final String ERROR_LOADING_MODULES = "An error occured while generating the license.";
	// root panel
	Stage currentStage = null;
	int width = 600;
	int height = 600;
	TextField editionValue = null;
	TextField keyValue = null;
	TextField keyEmail = null;
	TextField passPhraseValue = null;
	DatePicker startDateValue = null;
	DatePicker endDateValue = null;
	TextField licenseOutLoc = null;
	CheckBox neverExpire = null;
	Button btnGenerate = null;
	TextField secretKeyFileLoc = null;
	// users
	ArrayList<String> usersList = new ArrayList<String>();
	// Dispatchers
	ArrayList<String> dispatchersList = new ArrayList<String>();
	TextArea usersTextArea = null;
	TextArea dispatchersTextArea = null;

	@SuppressWarnings("rawtypes")
	CheckComboBox modulesCheckComboBox;
	// Intro scene
	Scene sceneIntro = null;
	// Scenes list
	Scene initScene = null;
	Scene licenseConfiguration = null;
	Scene licenseConfiguration2 = null;

	public LicenseGenScene(Stage primaryStage, Scene sceneIntro, int width, int height) {
		this.currentStage = primaryStage;
		this.width = width;
		this.height = height;
		this.sceneIntro = sceneIntro;
		initScene();
	}

	public void initScene() {
		Pane panel = new Pane();
		initScene = new Scene(panel, this.width, this.height);

		Label configuration = new Label("Generate new license");
		configuration.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		panel.getChildren().add(configuration);
		configuration.relocate(120, 30);

		Label keySizeInfo = new Label("Please fill the license properties.");
		keySizeInfo.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(keySizeInfo);
		keySizeInfo.relocate(20, 80);

		// Edition
		Label editionLbl = new Label("Edition");
		editionLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(editionLbl);
		editionLbl.relocate(20, 120);

		editionValue = new TextField();
		editionValue.setText("ADNEOM");
		panel.getChildren().add(editionValue);
		editionValue.relocate(150, 120);

		// Start date
		Label startDateLbl = new Label("Starting from");
		startDateLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(startDateLbl);
		startDateLbl.relocate(20, 170);

		startDateValue = new DatePicker();
		panel.getChildren().add(startDateValue);
		startDateValue.relocate(150, 170);
		startDateValue.setValue(LocalDate.now());
		startDateValue.setConverter(new StringConverter<LocalDate>() {
			private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			@Override
			public String toString(LocalDate localDate) {
				if (localDate == null)
					return "";
				return dateTimeFormatter.format(localDate);
			}

			@Override
			public LocalDate fromString(String dateString) {
				if (dateString == null || dateString.trim().isEmpty()) {
					return null;
				}
				return LocalDate.parse(dateString, dateTimeFormatter);
			}
		});
		// End date
		Label endDateLbl = new Label("Expiration date");
		endDateLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(endDateLbl);
		endDateLbl.relocate(20, 220);

		endDateValue = new DatePicker();
		panel.getChildren().add(endDateValue);
		endDateValue.relocate(150, 220);
		endDateValue.setConverter(new StringConverter<LocalDate>() {
			private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			@Override
			public String toString(LocalDate localDate) {
				if (localDate == null)
					return "";
				return dateTimeFormatter.format(localDate);
			}

			@Override
			public LocalDate fromString(String dateString) {
				if (dateString == null || dateString.trim().isEmpty()) {
					return null;
				}
				return LocalDate.parse(dateString, dateTimeFormatter);
			}
		});
		endDateValue.valueProperty().addListener((ov, oldValue, newValue) -> {
			enableNext();
		});

		neverExpire = new CheckBox("Never expire");
		panel.getChildren().add(neverExpire);
		neverExpire.relocate(210, 250);
		neverExpire.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				endDateValue.setDisable(neverExpire.isSelected());
				if (neverExpire.isSelected()) {
					endDateValue.setValue(null);
				}
				enableNext();
			}
		});

		// key and passphrase
		// key
		Label keyLbl = new Label("Key[User,Email]");
		keyLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(keyLbl);
		keyLbl.relocate(20, 300);

		keyValue = new TextField();
		panel.getChildren().add(keyValue);
		keyValue.relocate(150, 300);
		keyValue.setPrefWidth(150);
		keyValue.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				enableNext();
			}
		});

		keyEmail = new TextField();
		panel.getChildren().add(keyEmail);
		keyEmail.relocate(320, 300);
		keyEmail.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				enableNext();
			}
		});

		// passphrase
		Label passPhraseLbl = new Label("Passphrase");
		passPhraseLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(passPhraseLbl);
		passPhraseLbl.relocate(20, 350);

		passPhraseValue = new TextField();
		panel.getChildren().add(passPhraseValue);
		passPhraseValue.relocate(150, 350);
		passPhraseValue.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				enableNext();
			}
		});
		// Secret key
		Label secretKey = new Label("Secret key");
		secretKey.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(secretKey);
		secretKey.relocate(20, 400);

		secretKeyFileLoc = new TextField();
		panel.getChildren().add(secretKeyFileLoc);
		secretKeyFileLoc.relocate(170, 400);
		secretKeyFileLoc.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				enableNext();
			}
		});

		Image imageExplorer = new Image("icons/folder-explorer-icon.png");
		Button btnSelectKey = new Button("");
		btnSelectKey.setGraphic(new ImageView(imageExplorer));
		panel.getChildren().add(btnSelectKey);
		btnSelectKey.relocate(350, 400);
		btnSelectKey.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select secret key");
				fileChooser.setInitialDirectory(new File(OSValidator.getDefaultKeyStorePath()));
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Key rings", Configuration.DEFAULT_PRIVATE_KEY_EXT_FILTER));
				File selectedFile = fileChooser.showOpenDialog(null);
				if (selectedFile != null) {
					secretKeyFileLoc.setText(selectedFile.getAbsolutePath());
				}
			}
		});

		// License dest file
		Label destFolder = new Label("Destination folder");
		destFolder.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(destFolder);
		destFolder.relocate(20, 450);

		licenseOutLoc = new TextField();
		panel.getChildren().add(licenseOutLoc);
		licenseOutLoc.relocate(170, 450);

		Button btnSelectOut = new Button("");
		btnSelectOut.setGraphic(new ImageView(imageExplorer));
		panel.getChildren().add(btnSelectOut);
		btnSelectOut.relocate(350, 450);
		btnSelectOut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Destination folder");
				File defaultDirectory = new File(OSValidator.getDefaultKeyStorePath());
				chooser.setInitialDirectory(defaultDirectory);
				File selectedDirectory = chooser.showDialog(currentStage);
				if (selectedDirectory != null) {
					licenseOutLoc.setText(selectedDirectory.getAbsolutePath());
				}
			}
		});

		Button btnNext = new Button("Next");
		panel.getChildren().add(btnNext);
		btnNext.relocate(350, 550);
		btnNext.setOnAction(e -> goForward());

		Button btnBack = new Button("Back");
		panel.getChildren().add(btnBack);
		btnBack.relocate(300, 550);
		btnBack.setOnAction(e -> goBack());

		enableNext();

		currentStage.setScene(initScene);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setLicenseConfiguration() {
		Pane panel = new Pane();
		licenseConfiguration = new Scene(panel, this.width, this.height);

		Label configuration = new Label("Generate new license");
		configuration.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		panel.getChildren().add(configuration);
		configuration.relocate(120, 30);

		Label keySizeInfo = new Label("Please fill the license properties.");
		keySizeInfo.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(keySizeInfo);
		keySizeInfo.relocate(20, 80);

		// Modules
		Label modulesLbl = new Label("Authorized modules");
		modulesLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(modulesLbl);
		modulesLbl.relocate(20, 130);

		ObservableList modulesList = getAllModulesFromProperties();

		// Create the CheckComboBox with the data
		modulesCheckComboBox = new CheckComboBox(modulesList);

		// and listen to the relevant events (e.g. when the selected indices or
		// selected items change).
		modulesCheckComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
			public void onChanged(ListChangeListener.Change c) {
			}
		});

		modulesCheckComboBox.setMaxWidth(250);
		modulesCheckComboBox.setPrefWidth(250);
		panel.getChildren().add(modulesCheckComboBox);
		modulesCheckComboBox.relocate(200, 130);

		// Users
		Label usersLbl = new Label("Authorized users");
		usersLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(usersLbl);
		usersLbl.relocate(20, 220);

		TextField userTextField = new TextField();
		panel.getChildren().add(userTextField);
		userTextField.relocate(200, 220);
		userTextField.setPrefWidth(150);

		Image imageExplorer = new Image("icons/add.png");
		Button addUserBtn = new Button("");
		addUserBtn.setGraphic(new ImageView(imageExplorer));
		panel.getChildren().add(addUserBtn);
		addUserBtn.relocate(350, 220);
		addUserBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if ((userTextField.getText() != null) && (!userTextField.getText().isEmpty())) {
					usersList.add(userTextField.getText());
					updateUsersListArea();
					userTextField.setText(null);
				}
			}
		});
		Image imageExplorer2 = new Image("icons/del.png");
		Button delUserBtn = new Button("");
		delUserBtn.setGraphic(new ImageView(imageExplorer2));
		panel.getChildren().add(delUserBtn);
		delUserBtn.relocate(380, 220);
		delUserBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if ((userTextField.getText() != null) && (!userTextField.getText().isEmpty())) {
					usersList.remove(userTextField.getText());
					updateUsersListArea();
					userTextField.setText(null);
				}
			}
		});

		usersTextArea = new TextArea();
		panel.getChildren().add(usersTextArea);
		usersTextArea.relocate(200, 250);
		usersTextArea.setPrefSize(220, 200);
		usersTextArea.setDisable(true);
		Button btnNext = new Button("Next");
		panel.getChildren().add(btnNext);
		btnNext.relocate(350, 550);
		btnNext.setOnAction(e -> goForward());

		Button btnBack = new Button("Back");
		panel.getChildren().add(btnBack);
		btnBack.relocate(300, 550);
		btnBack.setOnAction(e -> goBack());

		enableNext();

		currentStage.setScene(licenseConfiguration);
	}

	public void setLicenseConfiguration2() {
		Pane panel = new Pane();
		licenseConfiguration2 = new Scene(panel, this.width, this.height);

		Label configuration = new Label("Generate new license");
		configuration.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		panel.getChildren().add(configuration);
		configuration.relocate(120, 30);

		Label keySizeInfo = new Label("Please fill the license properties.");
		keySizeInfo.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(keySizeInfo);
		keySizeInfo.relocate(20, 80);

		// Dispatchers
		Label modulesLbl = new Label("Authorized dispatchers");
		modulesLbl.setFont(Font.font("Verdana", 15));
		panel.getChildren().add(modulesLbl);
		modulesLbl.relocate(20, 130);

		TextField userTextField = new TextField();
		panel.getChildren().add(userTextField);
		userTextField.relocate(200, 130);
		userTextField.setPrefWidth(150);

		Image imageExplorer = new Image("icons/add.png");
		Button addPropertyBtn = new Button("");
		addPropertyBtn.setGraphic(new ImageView(imageExplorer));
		panel.getChildren().add(addPropertyBtn);
		addPropertyBtn.relocate(350, 130);
		addPropertyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if ((userTextField.getText() != null) && (!userTextField.getText().isEmpty())) {
					dispatchersList.add(userTextField.getText());
					updateDispatchersListArea();
					userTextField.setText(null);
				}
			}
		});
		Image imageExplorer2 = new Image("icons/del.png");
		Button delPropertyBtn = new Button("");
		delPropertyBtn.setGraphic(new ImageView(imageExplorer2));
		panel.getChildren().add(delPropertyBtn);
		delPropertyBtn.relocate(380, 130);
		delPropertyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if ((userTextField.getText() != null) && (!userTextField.getText().isEmpty())) {
					dispatchersList.remove(userTextField.getText());
					updateDispatchersListArea();
					userTextField.setText(null);
				}
			}
		});

		dispatchersTextArea = new TextArea();
		panel.getChildren().add(dispatchersTextArea);
		dispatchersTextArea.relocate(200, 160);
		dispatchersTextArea.setPrefSize(320, 250);
		dispatchersTextArea.setDisable(true);

		btnGenerate = new Button("Generate");
		panel.getChildren().add(btnGenerate);
		btnGenerate.relocate(350, 550);
		btnGenerate.setOnAction(e -> goForward());

		Button btnBack = new Button("Back");
		panel.getChildren().add(btnBack);
		btnBack.relocate(300, 550);
		btnBack.setOnAction(e -> goBack());

		enableNext();

		currentStage.setScene(licenseConfiguration2);
	}

	private void updateUsersListArea() {
		StringBuffer usersListBuff = new StringBuffer();
		Collections.sort(usersList);
		usersList.forEach(user -> usersListBuff.append(user + System.getProperty("line.separator")));
		usersTextArea.setText(usersListBuff.toString());
	}

	private void updateDispatchersListArea() {
		StringBuffer dispListBuff = new StringBuffer();
		Collections.sort(dispatchersList);
		dispatchersList.forEach(user -> dispListBuff.append(user + System.getProperty("line.separator")));
		dispatchersTextArea.setText(dispListBuff.toString());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ObservableList getAllModulesFromProperties() {
		Properties modulesList = new Properties();
		try {
			modulesList.load(ClassLoader.class.getResourceAsStream("/configuration/modules.properties"));
		} catch (IOException e) {
			AlertHelper.alertException(ERROR_LOADING_MODULES, e);
		}
		// create the data to show in the CheckComboBox
		ObservableList strings = FXCollections.observableArrayList();
		modulesList.values().forEach(strings::add);
		strings.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return strings;
	}

	private void goBack() {
		if (currentStage.getScene().equals(initScene)) {
			currentStage.setScene(sceneIntro);
		} else if (currentStage.getScene().equals(licenseConfiguration)) {
			currentStage.setScene(initScene);
		} else if (currentStage.getScene().equals(licenseConfiguration2)) {
			currentStage.setScene(licenseConfiguration);
		}
	}

	private void goForward() {
		if (currentStage.getScene().equals(initScene)) {
			// Lazy inst
			if (licenseConfiguration == null)
				setLicenseConfiguration();

			currentStage.setScene(licenseConfiguration);

		} else if (currentStage.getScene().equals(licenseConfiguration)) {
			// Lazy inst
			if (licenseConfiguration2 == null)
				setLicenseConfiguration2();

			currentStage.setScene(licenseConfiguration2);

		} else if (currentStage.getScene().equals(licenseConfiguration2)) {
			AlertHelper.startLoading(btnGenerate);
			String licenseFilePath = null;
			if (!(licenseFilePath = generateLicense()).isEmpty()) {
				AlertHelper.alertSuccess("The license has been generated successfully !\n  Location:" + licenseFilePath);
				currentStage.setScene(sceneIntro);
			}
			AlertHelper.stopLoading(btnGenerate);
		}

	}

	private void enableNext() {
		// Expiration date or approve that the license never expires is mandatory
		if (btnGenerate != null)
			btnGenerate.setDisable((((endDateValue == null) || (endDateValue.getValue() == null)) && (!neverExpire.isSelected())) || secretKeyFileLoc.getText().isEmpty() || keyValue.getText().isEmpty() || passPhraseValue.getText().isEmpty());
	}

	private String filLocationFolder;
	private boolean result;

	@SuppressWarnings("unchecked")
	public String generateLicense() {
		String licenseFilePath = null;
		Properties properties = new Properties();
		StringBuffer keyBuff = null;
		try {

			properties.setProperty("edition", editionValue != null ? editionValue.getText() : "");
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Configuration.DEFAULT_LICENSE_DATE_FORMAT);
			properties.setProperty("valid-from", startDateValue != null ? startDateValue.getValue().format(dateTimeFormatter) : LocalDateTime.now().format(dateTimeFormatter));
			properties.setProperty("valid-until", ((endDateValue != null) && (endDateValue.getValue() != null)) ? endDateValue.getValue().format(dateTimeFormatter) : "");
			filLocationFolder = (!licenseOutLoc.getText().isEmpty()) ? licenseOutLoc.getText() : OSValidator.getDefaultKeyStorePath();

			String modulesString = String.join(DEFAULT_AUTH_SEPARATOR, modulesCheckComboBox.getCheckModel().getCheckedItems());
			properties.setProperty("modules-auth", modulesString);

			String usersString = String.join(DEFAULT_AUTH_SEPARATOR, usersList);
			properties.setProperty("users-auth", usersString);

			String dispatchersString = String.join(DEFAULT_AUTH_SEPARATOR, dispatchersList);
			properties.setProperty("dispatchers-auth", dispatchersString);

			String filePath = filLocationFolder + "/" + "WTBLicense" + System.currentTimeMillis() + ".tmp";
			File file = new File(filePath);
			FileOutputStream fileOut = new FileOutputStream(file);
			properties.store(fileOut, "WebToolBox License");
			fileOut.close();

			// Create container folder
			String containerFolder = filLocationFolder + "/" + Configuration.DEFAULT_GENERATED_LICENSE_FOLDER;
			File theDir = new File(containerFolder);
			if (!theDir.exists()) {
				theDir.mkdir();
			}
			// License generation
			licenseFilePath = containerFolder + "/" + "WTBLicense" + System.currentTimeMillis() + Configuration.DEFAULT_LICENSE_EXT;

			keyBuff = new StringBuffer(keyValue.getText());
			if ((keyEmail != null) && (keyEmail.getText() != null) && (!keyEmail.getText().isEmpty())) {
				keyBuff.append(" ");
				keyBuff.append("<" + keyEmail.getText() + ">");
			}

			result = OSValidator.executeLicenseGen(licenseFilePath, filePath, secretKeyFileLoc.getText(), keyBuff.toString(), passPhraseValue.getText());
			file.delete();

		} catch (IOException e) {
			AlertHelper.alertException(AlertHelper.ERROR_GEN_LIC + "\nPlease verify the user r/w permissions on:" + filLocationFolder, e);
		}
		if (result && (licenseFilePath != null) && ((new File(licenseFilePath)).exists())) {
			try {
				AlertHelper.generateInfoFile(properties, licenseFilePath + "LicenseNote.txt", keyBuff.toString());
			} catch (IOException e) {
				AlertHelper.alertException(AlertHelper.ERROR_GEN_LIC_INFO + "\nPlease verify the user r/w permissions on:" + filLocationFolder, e);
			}
			return licenseFilePath;
		} else
			return "";

	}

}
