/* Rayyan Athar, Mohammed Shayan Khan */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiClient extends Application{
	TextField usernameField, messageField;
	ListView<String> chatLog;
	ListView<CheckBox> clients, groups;
	ArrayList<String> groupMembers, wholeGroup, groupNames;
	ArrayList<ArrayList<String>>allGroups;
	Button setUsernameBtn, sendMessageBtn, createGroupBtn, leaveServerBtn;
	Text title, errorUsername, name, people, errorMessage;
	HBox  buttonBox, mainBox;
	VBox clientJoin, clientInteractions, clientText, userBox, chatBox;
	HashMap<String, Scene> sceneMap;
	Client clientConnection;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client(data->{
			Platform.runLater(()->{
				if (data instanceof Message) {
					Message msg = (Message)  data;

					if ("invalid_username".equals(msg.type)) {
						errorUsername.setText("Username already taken, please choose another.");
						primaryStage.setScene(sceneMap.get("username")); // Send the user back to the username scene
					}
					else if ("ok_create_user".equals(msg.type)) {
						chatLog.getItems().add("Welcome to the chat! " + msg.users.get(msg.users.size()-1));
						name.setText(msg.users.get(msg.users.size()-1));
						for(int i = 0; i < msg.users.size(); i++) {
							if(i != msg.users.size()-1) {
								clients.getItems().add(new CheckBox(msg.users.get(i)));
							}
						}
						for(int i = 0; i < msg.groups.size(); i++) {
							CheckBox b =  new CheckBox(String.join(" ", msg.groups.get(i)));
							groups.getItems().add(b);
							b.setDisable(true);
						}
						primaryStage.setScene(sceneMap.get("client")); // Proceed to the chat scene
					}
					else if ("ok_update_users".equals(msg.type)){
						chatLog.getItems().add("Welcome to the chat! " + msg.users.get(msg.users.size()-1));
						clients.getItems().add(new CheckBox(msg.users.get(msg.users.size()-1)));
					}
					else if("indiv_message_ok".equals(msg.type)) {
						chatLog.getItems().add(msg.sender + " to you: " + msg.content);
					}
					else if("ok_broadcast".equals(msg.type)) {
						chatLog.getItems().add(msg.sender + " to everyone: " + msg.content);
					}
					else if("ok_new_group".equals(msg.type)) {
						chatLog.getItems().add("Group created: " + String.join(" ", msg.groups.get(msg.groups.size()-1)));
						groups.getItems().add(new CheckBox(String.join(" ", msg.groups.get(msg.groups.size()-1))));
					}
					else if("update_new_group".equals(msg.type)) {
						chatLog.getItems().add("Group created: " + String.join(" ", msg.groups.get(msg.groups.size()-1)));
						CheckBox a =  new CheckBox(String.join(" ", msg.groups.get(msg.groups.size()-1)));
						groups.getItems().add(a);
						if(!msg.groups.get(msg.groups.size()-1).contains(name.getText())) {
							a.setDisable(true);
						}
					}
					else if("ok_group_message".equals(msg.type)) {
						chatLog.getItems().add(msg.sender + " to " + groups.getItems().get(msg.destinationGroup).getText()+ ": " + msg.content);
					}
					else if("ok_leave_user".equals(msg.type)) {
						primaryStage.close();
					}
					else if("update_leave_user".equals(msg.type)) {
						for(int i = 0; i < clients.getItems().size(); i++) {
							if(clients.getItems().get(i).getText().equals(msg.sender)) {
								clients.getItems().remove(i);
							}
						}
					}
				}
			});
		});
							
		clientConnection.start();

		/*/////////////////////////USERNAME SCENE//////////////////////////////////////*/

		usernameField = new TextField();
		usernameField.setMaxWidth(300);
		usernameField.setPromptText("Enter your username");
		usernameField.setStyle("-fx-background-color: #FFFFFF, #FFFFFF; -fx-background-insets: 0, 1 1 1 0; -fx-background-radius: 30, 30; -fx-padding: 5 10; -fx-effect: innershadow(two-pass-box, rgba(0,0,0,0.1), 2, 0.0, 0, 2);");

		setUsernameBtn = new Button("Join Server");
		setUsernameBtn.setStyle("-fx-font-size: 18px; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-pref-width: 200px; -fx-pref-height: 40px; -fx-border-radius: 20; -fx-background-radius: 20;");
		setUsernameBtn.setEffect(new DropShadow(10, Color.BLACK));

		title = new Text("Messaging Application");
		title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-fill: #333333;");

		errorUsername = new Text();
		errorUsername.setStyle("-fx-fill: #F44336; -fx-font-size: 14px;");

		setUsernameBtn.setOnAction(e->{
			String user = usernameField.getText();
			if(!user.isEmpty()) {
				clientConnection.send(new Message("new_user", user));
				usernameField.setText("");
				errorUsername.setText("");
				primaryStage.setScene(sceneMap.get("client"));
			}
			else{
				errorUsername.setText("Username cannot be empty.");
			}
		});


		/*/////////////////////////CHAT SCENE////////////////////////////////////*/

		clients = new ListView<>();
		groups = new ListView<>();
		chatLog = new ListView<>();

		chatLog.setStyle("-fx-background-insets: 0; -fx-padding: 5; -fx-border-insets: 0; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
		clients.setStyle("-fx-background-insets: 0; -fx-padding: 5; -fx-border-insets: 0; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #e0e0e0; -fx-border-width: 1;");
		groups.setStyle("-fx-background-insets: 0; -fx-padding: 5; -fx-border-insets: 0; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #e0e0e0; -fx-border-width: 1;");

		messageField = new TextField();
		messageField.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16));
		messageField.setPromptText("Type your message here...");
		messageField.setStyle("-fx-background-color: #FFFFFF, #FFFFFF; -fx-background-insets: 0, 1 1 1 0; -fx-background-radius: 30, 30; -fx-padding: 5 10; -fx-effect: innershadow(two-pass-box, rgba(0,0,0,0.1), 2, 0.0, 0, 2);");

		chatLog.setPrefWidth(400);
		chatLog.setPrefHeight(600);
		messageField.setMaxWidth(400);

		name = new Text("NAME");
		name.setStyle("-fx-fill: #34495E; -fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Times New Roman'");

		people = new Text("Users and Groups");
		people.setStyle("-fx-fill: #161085; -fx-font-size: 14px; -fx-font-weight: lighter; -fx-font-family: 'Times New Roman'");

		errorMessage = new Text();
		errorMessage.setStyle("-fx-fill: #F44336; -fx-font-size: 14px;");


		sendMessageBtn = new Button("Send Message");
		sendMessageBtn.setStyle("-fx-font-size: 15px; -fx-background-color: #78c800 ; -fx-text-fill: white; -fx-pref-width: 150px; -fx-pref-height: 40px; -fx-border-radius: 20; -fx-background-radius: 20;");
		sendMessageBtn.setEffect(new DropShadow(10, Color.BLACK));

		createGroupBtn = new Button("Create Group");
		createGroupBtn.setStyle("-fx-font-size: 15px; -fx-background-color: #005ecb ; -fx-text-fill: white; -fx-pref-width: 150px; -fx-pref-height: 40px; -fx-border-radius: 20; -fx-background-radius: 20;");
		createGroupBtn.setEffect(new DropShadow(10, Color.BLACK));

		leaveServerBtn = new Button("Leave Server");
		leaveServerBtn.setStyle("-fx-font-size: 15px; -fx-background-color: #c50e29 ; -fx-text-fill: white; -fx-pref-width: 150px; -fx-pref-height: 40px; -fx-border-radius: 20; -fx-background-radius: 20;");
		leaveServerBtn.setEffect(new DropShadow(10, Color.BLACK));


		sendMessageBtn.setOnAction(e->{
			String content = messageField.getText();
			boolean everyone = true;
			if(!content.isEmpty()){
				errorMessage.setText("");
				for (int i = 0; i < clients.getItems().size(); i++) {
					if (clients.getItems().get(i).isSelected()) {
						clientConnection.send(new Message("indiv_messsage", content, name.getText(), clients.getItems().get(i).getText()));
						chatLog.getItems().add("You to " + clients.getItems().get(i).getText() + ": " + content);
						messageField.setText("");
						everyone = false;
					}
				}
				for(int i = 0; i < groups.getItems().size(); i++) {
					if(groups.getItems().get(i).isSelected()) {
						clientConnection.send(new Message("group_message",content,name.getText(),null,i));
						messageField.setText("");
						everyone = false;
					}
				}
				if(everyone) {
					clientConnection.send(new Message("broadcast", content, name.getText(), null));
					chatLog.getItems().add("You to everyone: " + content);
					messageField.setText("");
				}
			}
			else {
				errorMessage.setText("Message cannot be empty");
			}
		});

		createGroupBtn.setOnAction(e->{
			groupMembers = new ArrayList<>();
			wholeGroup = new ArrayList<>();
			allGroups = new ArrayList<>();

			boolean duplicateGroup = false;

			for (int i = 0; i < clients.getItems().size(); i++) {
				if (clients.getItems().get(i).isSelected()) {
					groupMembers.add(clients.getItems().get(i).getText());
					wholeGroup.add(clients.getItems().get(i).getText());
				}
			}
			wholeGroup.add(name.getText());

			for(int i = 0; i < groups.getItems().size(); i++) {
				groupNames = new ArrayList<>(Arrays.asList(groups.getItems().get(i).getText().split(" ")));

				Collections.sort(groupNames);
				Collections.sort(wholeGroup);

				if(groupNames.equals(wholeGroup)){
					duplicateGroup = true;
				}
			}

			if(groupMembers.size() < 2 ){
				errorMessage.setText("Group needs at least 2 other members");
			}
			else if(duplicateGroup) {
				errorMessage.setText("Can not create duplicate group");
			}
			else{
				errorMessage.setText("");
				allGroups.add(groupMembers);
				clientConnection.send(new Message("create_group",name.getText(),allGroups,groupMembers));
			}
		});

		leaveServerBtn.setOnAction(e->{
			clientConnection.send(new Message("leave_user",null, name.getText(),null));
		});

		sceneMap = new HashMap<>();
		sceneMap.put("username",  setClientUsername());
		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
				clientConnection.send(new Message("leave_user",null, name.getText(),null));
				Platform.exit();
                System.exit(0);
            }
        });

		primaryStage.setScene(sceneMap.get("username"));
		primaryStage.setTitle("client");
		primaryStage.show();
	}

	public Scene setClientUsername() {
		clientInteractions = new VBox(20, usernameField, setUsernameBtn);
		clientInteractions.setAlignment(Pos.CENTER);
		clientInteractions.setPadding(new Insets(10, 50, 10, 50)); // Add padding for better spacing

		clientText = new VBox(10, title, errorUsername);
		clientText.setAlignment(Pos.CENTER);
		clientText.setPadding(new Insets(10, 50, 10, 50)); // Add padding for better spacing

		clientJoin = new VBox(20,clientText,clientInteractions);
		clientJoin.setAlignment(Pos.CENTER);
		clientJoin.setStyle("-fx-background-color: grey");

		return new Scene(clientJoin, 400, 250);
	}
	
	public Scene createClientGui() {
		buttonBox = new HBox(20, sendMessageBtn, createGroupBtn, leaveServerBtn);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(15, 0, 15, 0)); // Add padding

		chatBox = new VBox(10, chatLog, errorMessage, messageField, buttonBox);
		chatBox.setPadding(new Insets(20));
		chatBox.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: #E0E0E0; -fx-border-width: 1;");
		chatBox.setAlignment(Pos.CENTER_LEFT);
		chatBox.setPrefWidth(450);

		userBox = new VBox(10, name, people, clients, groups);
		userBox.setPadding(new Insets(20));
		userBox.setAlignment(Pos.CENTER);
		userBox.setPrefWidth(250);
		userBox.setStyle("-fx-background-color: #FFFFFF;");

		mainBox = new HBox(20, chatBox, userBox);
		mainBox.setAlignment(Pos.CENTER);
		mainBox.setBackground(new Background(new BackgroundFill(
		new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web("#fdfbfb")), new Stop(1, Color.web("#ebedee"))), CornerRadii.EMPTY, Insets.EMPTY)));
		mainBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

		return new Scene(mainBox, 700, 550);
	}
}
