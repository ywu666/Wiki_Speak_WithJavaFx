package application;


import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * This class will help to update the GUI after get the valid user input 
 * @author ywu660
 *
 */
public class FinishedJob implements Runnable {

	private VBox _layout;
	private boolean _success;
	private Pane _p;
     public  FinishedJob(VBox layout,Pane p,boolean success) {

    	 _layout= layout;
    	 _success = success;
    	 _p = p;
     }
	@Override
	public void run() {
		
		if (_success) {
			((BorderPane) _p).setBottom(_layout);		
		}else {
			((BorderPane) _p).setBottom(null);	
		}
		
	}
        
		
	

}
	

