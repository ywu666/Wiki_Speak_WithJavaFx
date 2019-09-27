package application;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.HBox;

import javafx.scene.layout.VBox;


public class Main extends Application {

	private Button btnCreate;
	private Button btnPlay;
	private Button btnDelete;

	private HBox buttons;
	private String dir = "$HOME/creationHistory";
	private String dir2 = System.getProperty("user.home") + File.separator + "creationHistory";
	private Label creationsLbl;
	private Label status ;
	private Label changeReport;

	// Create the ListView
	private ListView<String> _creations;


	@Override
	public void start(Stage primaryStage) {
		try {

			primaryStage.setTitle("Welcome WikiSpeak");

			BorderPane root = new BorderPane();
			
			buttons = new HBox();
			
            // create the directory store and creations and templete files during the create process
			new File(System.getProperty("user.home") + File.separator + "creationHistory").mkdir();
			new File(System.getProperty("user.home") + File.separator + "templete").mkdir();

			//print the main menue
			this.printMainMenue();

			// Create the Label
			creationsLbl = new Label("Creation List: ");
			status = new Label();
			changeReport = new Label();

			// Create the ListView
			_creations = new ListView<String>();
			// Add the items to the List 
			_creations.getItems().addAll(listCreatoin());
			// Enable multiple selection
			_creations.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			
			_creations.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
				@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					changeReport.setText("Selection changed from '" + oldValue + "' to '" + newValue + "'");
				}
			});
			//_creations = listView(_creations,listCreatoin(),changeReport);
			
			VBox creationsSelection = new VBox();
			// Set Spacing to 10 pixels
			creationsSelection.setSpacing(10);
			// Add the Label and the List to the HBox
			creationsSelection.getChildren().addAll(creationsLbl,_creations);

			root.setCenter(creationsSelection);
             
			btnCreate.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					Create c = new Create(root,buttons,_creations);
					c.setLabel(status, changeReport);
					changeReport.setText("");
					c.btnCreateActoin();
				}

			});


            // delete the creation that user selected on the list view 
			btnDelete.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					
					final int selectedIdx = _creations.getSelectionModel().getSelectedIndex();
					if (selectedIdx != -1) {
						String itemToRemove = _creations.getSelectionModel().getSelectedItem()+".mkv";
						
						Alert alert = new Alert(AlertType.CONFIRMATION, "Delete " + itemToRemove + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
						alert.showAndWait();

						if (alert.getResult() == ButtonType.YES) {
					
						String directory = dir2 + File.separator + itemToRemove;
						try {
							Files.deleteIfExists(Paths.get(directory));
						}catch(NoSuchFileException e) 
						{ 
							status.setText("No such file/directory exists"); 
						} 
						catch(DirectoryNotEmptyException e) 
						{ 
							status.setText("Directory is not empty."); 
						} 
						catch(IOException e) 
						{ 
							status.setText("Invalid permissions."); 
						} 


						final int newSelectedIdx =
								(selectedIdx == _creations.getItems().size() - 1)
								? selectedIdx - 1
										: selectedIdx;

						_creations.getItems().remove(selectedIdx);

						status.setText("Removed " + itemToRemove);
						_creations.getSelectionModel().select(newSelectedIdx);
						}
					}
				}

               



			});

             //play the creation that user create and list on the list view.
			 btnPlay.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					final int selectedIdx = _creations.getSelectionModel().getSelectedIndex();
					if (selectedIdx != -1) {
						
						PlayCreationTask task = new PlayCreationTask(dir2,_creations);
						task.setOnRunning((succeesesEvent) -> {
				            status.setText("Please waiting .... ");
				         });

				         task.setOnSucceeded((succeededEvent) -> {
				             status.setText("Finished creating ... please press exit to check.");
				         });

				         ExecutorService executorService
				            = Executors.newFixedThreadPool(1);
				         executorService.execute(task);
				         executorService.shutdown();

						
					}
						
					
				}
             	
             });
            
			 // set the layout 
			root.setCenter(layout(buttons,_creations,status,changeReport));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public  VBox layout(HBox buttons,ListView<String> creations, Label status,Label changeReport) {
        _creations = creations;
		//layout
		VBox layout = new VBox(10);
		layout.setAlignment(Pos.CENTER);

		layout.getChildren().setAll(
				_creations, 
				buttons,
				status,
				changeReport
				);

		layout.setPrefWidth(320);
         
		return layout;
	}

/**
 * This method will display the main menu. With the create,play and delete buttons.
 * It will also show the creations list on the list view.
 */
	public void printMainMenue() {

		//display the buttons
		buttons.getChildren().clear();
		btnCreate = new Button("Create");
		btnPlay = new Button("Play");
		btnDelete = new Button("Delete");
		btnCreate.setMaxWidth(Double.MAX_VALUE);
		btnPlay.setMaxWidth(Double.MAX_VALUE);
		btnDelete.setMaxWidth(Double.MAX_VALUE);
		buttons.setSpacing(10);	
		buttons.getChildren().addAll(btnCreate,btnPlay,btnDelete);


	}

/**
 * This method will list the creations use the list command 
 * @return
 * @throws InterruptedException
 * @throws IOException
 */
	public ArrayList<String> listCreatoin() throws InterruptedException, IOException {
		//view creation
		ArrayList<String> list = new ArrayList<String>();
		String command ="ls "+dir +" | sort |sed 's/\\.mkv$//'";
		ProcessBuilder ls = new ProcessBuilder("bash", "-c", command);
		Process process = ls.start();

		BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		process.waitFor();

		int exitStatus = process.exitValue();

		if(exitStatus == 0) {
			String line;			
			while ((line = stdout.readLine()) != null) {
				list.add(line);
				
			}
		} else {
			String line;
			while ((line = stderr.readLine()) != null) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error Encounted");
				alert.setHeaderText("Problem reading directory");
				alert.setContentText(line);
				alert.showAndWait();
			}
		}
		return list;

	}
	

	public static void main(String[] args) {
		launch(args);
	}
}
