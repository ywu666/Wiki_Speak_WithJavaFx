package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * This class will set GUI after press the create button. and create the creation based on the name that user's given.
 * 
 * */
public class Create {
	private BorderPane _root;
	private HBox _buttons;
	private String dir =System.getProperty("user.home") + File.separator + "templete";
	private String dir2 = System.getProperty("user.home") + File.separator + "creationHistory";
	private String _history =dir + File.separator + "history.txt";
	private TextField tfName1;
	private TextField tfName2;
	private ListView<String> _creations;
	private String name;
	private String variable;
	private ListView<String> text;
	private Button btnSubmit1;
	private Button btnExit1;
	private Button btnSubmit;
	private Button btnContinue;
	private Button btnExit;
	private Main main = new Main();
	private Label _status ;
	private Label _changeReport;
	private int _count;
	private int _number;
	private boolean _success;
	private ArrayList<String> l;
	private Pane _p;
	private VBox _v;
    private ProgressBar progressBar;

	public Create(BorderPane root,HBox buttons,ListView<String> creations) {
		_root = root;
		_buttons = buttons;
		_creations = creations;

	}


	/**This method will do all the actions after pressing the create button.
	 * The GUI pane after press create button in main pane
	 */
	public void btnCreateActoin() {
		//set to the search content
		_p = searchContent();
		_root.setCenter(_p);
		
		
		tfName1.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER)  {
						btnSubmit1Action();						
		          
		        }	
			}
			
		});

		//exist to the main menu
		btnExit1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				_root.setCenter(main.layout(_buttons,_creations,_status,_changeReport));
				_status.setText("");	
			}

		});
		
		
		//display the result of wiki commmand line by line 
		btnSubmit1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				btnSubmit1Action();


			}
		});

	}

	/**
	 * This method will start create process and display the create GUI  for create content
	 */
	public void btnContinueAction() {
		
		_status.setText("");
		// Don't allow user enter the number greater than range
		if(_number >= _count || _number<1) {
			_status.setText("please enter the number with in the range");
		}else {
				
		Pane p = createContent();
		_root.setCenter(p);
		
		
		tfName2.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER)  { 
						btnSubmitAction();						
		          
		        }	
			}
			
		});

		// exit to main menu, and refresh the new content
		btnExit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				updateListOfCreations();
			}

		});
		
		//start to create 
		btnSubmit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {	
                btnSubmitAction();             

			}

		});
				
		}

	}
	
	
	public void btnSubmit1Action() {
		//get the user input, delete the tab and  multiple space
		// also allow user to search upper case variable
		variable = tfName1.getText().trim();
        variable = variable.toLowerCase();
        
		// didn't allow user submit nothing
		if(variable == null || variable == "" || variable.length() == 0) {
			_status.setText("Please enter before submit.");
			return;
		}

		if(variable != null && variable != "") {
			// search wiki using wikit command 
			searchWikit();
			tfName1.setText("");
			//add the layout of enter number of lines
			_v =new VBox(10);
			TextField tfName3 = new TextField();
			btnContinue = new Button("continue");
			Label lblName3 = new Label ("Enter the number of line:");
			_v.getChildren().addAll(lblName3, tfName3,btnContinue);

			// only allow user to enter the digits 
			tfName3.textProperty().addListener(new ChangeListener<String>() {

				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

					if ((!newValue.matches("\\d{0,3}"))) {
						tfName3.setText(oldValue);
						_status.setText("Please enter the number");

					}

				}
			});
			
			
			tfName3.setOnKeyPressed(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.ENTER)  {
			          _number = Integer.parseInt(tfName3.getText());
							btnContinueAction();						
			          
			        }	
				}
				
			});


			// get the user input: number of line and start to create 
			btnContinue.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					String num = tfName3.getText().trim();
					if(num!=null && num!="" && num.length()!=0) {
					_number = Integer.parseInt(tfName3.getText().trim());
                    btnContinueAction();

					}
					
				}

			});	

		}
		
	}
	
	public void btnSubmitAction() {
		name = tfName2.getText().trim();
		tfName2.setText("");
		
		if(name.contains("%") || name.contains("*") || name.contains("@") || name.contains(".")) {
			name = null;
			_status.setText("please enter the valid name");
			return;
		}
		if(name != null && name != "" && name.length()!= 0) {

			// if the file is already in the folder, ask user if they want to overwrite or not;
		File file = new File(dir2+"/"+name+".mkv");
		if(file.exists()) {
			String info = "Do you want to overwrite exist file?";
			Alert alert = new Alert(AlertType.CONFIRMATION, info, ButtonType.YES, ButtonType.NO);
			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES) {
				try{
					ProcessBuilder rm = new ProcessBuilder("bash", "-c", "rm "+dir2+"/"+name+".mkv");
					rm.start();
                      
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else {
				return;
			}
		}

		//start to create 
		createProcess();	
		
		}
		
	}

	// use the main pane's status and change report for layout
	public void setLabel(Label status, Label changeReport) {
		_status = status;
		_changeReport = changeReport;

	}



	/**
	 * This method will get the layout of the create 
	 * @return
	 */
	public Pane createContent() {

		BorderPane pane =new BorderPane(); 
		VBox labels = new VBox();


		HBox hbButtons = new HBox();
		hbButtons.setSpacing(10.0);
		hbButtons.setAlignment(Pos.CENTER);  // Aligns HBox and controls in HBox
		btnSubmit = new Button("Submit");
		btnExit = new Button("Exit");
		progressBar = new ProgressBar(0);
		Label lblName2 = new Label("Creation Name:");
		tfName2 = new TextField();
		hbButtons.getChildren().addAll(btnSubmit,btnExit);
		labels.getChildren().addAll(lblName2,tfName2);
		VBox layout = new VBox(10);
		layout.setAlignment(Pos.CENTER);

		layout.getChildren().setAll(
				labels,
				hbButtons,
				_status,
				progressBar
				);
		layout.setPrefWidth(320);

		pane.setCenter(layout);
		return pane;

	}
	
	public void updateListOfCreations() {
		try {
			ArrayList<String> newlist = main.listCreatoin();
			_creations.getItems().clear();
			//newCreation = new ListView<String>();
			_creations.getItems().addAll(newlist);
			//main.NewCreatoins(newCreation);
			_root.setCenter(main.layout(_buttons,_creations,_status,_changeReport));
			_status.setText("");	
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * This method will get the layout of the search 
	 * @return
	 */
	public Pane searchContent() {

		BorderPane pane =new BorderPane(); 
		HBox hbButtons = new HBox();
		VBox labels = new VBox();
		hbButtons.setSpacing(10.0);
		hbButtons.setAlignment(Pos.CENTER);  // Aligns HBox and controls in HBox
		text = new ListView<String>();

		btnSubmit1 = new Button("Submit");
		btnExit1 = new Button("Exit");
		hbButtons.getChildren().addAll(btnSubmit1,btnExit1);

		tfName1 = new TextField();
		Label lblName1 = new Label ("Search variable:");
		labels.getChildren().addAll(lblName1,tfName1);

		VBox layout = new VBox(10);
		layout.setAlignment(Pos.CENTER);

		layout.getChildren().setAll(
				labels,
				text,
				hbButtons,
				_status
				);
		layout.setPrefWidth(320);

		pane.setCenter(layout);
		return pane;

	}

	/**
	 * This method will search user input by using wikit command 
	 */
	public void searchWikit() {
		SearchWikiTask task = new SearchWikiTask(variable,_history);

		task.setOnRunning((succeesesEvent) -> {
			_status.setText("Please wait...");
			btnSubmit1.setDisable(true);
			tfName1.setDisable(true);

		});

		task.setOnSucceeded((succeededEvent) -> {
			btnSubmit1.setDisable(false);
			tfName1.setDisable(false);
			_status.setText("please enter the number of lines with in the range");
			text.getItems().clear();
			try {

				File f = new File(_history);
				f.createNewFile();			
				_status.setText("please enter the number of lines with in the range");
				BufferedReader out= new BufferedReader(new FileReader(f));
				String line2;
				int count = 1;
				l = new ArrayList<String>();
				while((line2 = out.readLine()) != null) {
					if(line2.equals(variable+ " not found :^(")) {
						_status.setText("Sorry, you find nothing in wikipedia.Please enter a new word");
						text.getItems().clear();
						_success = false;
					}else {
						_success = true;
						l.add(count+"."+ line2);// +"\n";
						count++;
					}

				}
				out.close();
				text.getItems().addAll(l);
				_count = count; 
				Runnable finishedJob = new FinishedJob(_v,_p,_success);
				Platform.runLater(finishedJob);
                

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		});

		ExecutorService _executorService = Executors.newSingleThreadExecutor();
		_executorService.execute(task);
		_executorService.shutdown();

	}

	/**
	 * This method will create the creation
	 */
	public void createProcess(){
		CreateProcessTask task = new CreateProcessTask(variable,name,_history,dir,dir2,_number);
		
		task.setOnRunning((succeesesEvent) -> {
			_status.setText("Please waiting .... ");
            
		});

		task.setOnSucceeded((succeededEvent) -> {
			_status.setText("Finished creating ... please press exit to check.");
			progressBar.progressProperty().unbind();
			progressBar.setProgress(0);
			updateListOfCreations();			
			
		});

		progressBar.progressProperty().bind(task.progressProperty());
        
		ExecutorService executorService
		= Executors.newFixedThreadPool(1);
		executorService.execute(task);
		executorService.shutdown();
	}





}
