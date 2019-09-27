package application;

import java.io.File;
import java.io.IOException;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;

/**
 * The class will do the long task of create process
 * @author ywu660
 *
 */

public class PlayCreationTask extends Task<Void>{

	private ListView<String> _creations;
	private String _dir2;
	 public PlayCreationTask(String dir2,ListView<String> creations){
		 _creations = creations;
		 _dir2 = dir2;
	 }
	@Override
	protected Void call() throws Exception {
		String itemToPlay = _dir2 + File.separator + _creations.getSelectionModel().getSelectedItem()+".mkv";
		String command = "ffplay -autoexit "+ itemToPlay;
		ProcessBuilder ffplay = new ProcessBuilder("bash", "-c", command);
		Process p;
		try {
			p = ffplay.start();
			//p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

}
