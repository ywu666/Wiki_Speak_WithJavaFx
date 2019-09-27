package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javafx.concurrent.Task;


/**
 * This class will create a shell script file. Create audio and video files by using the 
 * already exists text file and then combine them to .mkv file.
 * @author ywu660
 *
 */

public class CreateProcessTask extends Task<Void> {

	private String _variable;
	private String _name;
	private int _number;
	private String _history;
	private String _dir;
	private String _dir2;
	  public CreateProcessTask(String variable,String name,String history,String dir,String dir2,int number) {
		  _variable = variable;
		  _name = name;
		  _history = history;
		  _dir = dir;
		  _dir2 = dir2;
		  _number =number;
		  
		  
	  }
	@Override
	protected Void call() throws Exception {
		try {
			File tempScript = File.createTempFile("temp",null);
			//FileWriter fileWriter = new FileWriter(tempScript.getName());
			Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
			PrintWriter printWriter = new PrintWriter(streamWriter);
			printWriter.printf("sed -n 1,%dp %s|text2wave -o %s/%s.wav &> /dev/null"+"\n",_number,_history,_dir,_name );
			String s = "ffmpeg -f lavfi -i color=c=blue:s=320x240:d=0.5 -vf \"drawtext=fontfile=myfont.ttf:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=%s\" %s/%s.mp4 &> /dev/null";
			printWriter.printf(s+"\n",_variable,_dir,_name,_name);
			printWriter.printf("ffmpeg -i %s/%s.mp4 -i %s/%s.wav -c copy %s/%s.mkv&> /dev/null"+"\n", _dir,_name,_dir,_name,_dir2,_name);
			printWriter.printf("rm -f %s/%s.wav"+"\n", _dir,_name);
			printWriter.printf("rm -f %s/%s.mp4"+"\n",_dir,_name);
			printWriter.close();

			ProcessBuilder create = new ProcessBuilder("bash",tempScript.toString());
			create.inheritIO();
			Process p  = create.start();
			p.waitFor();
			
			tempScript.delete();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	

}
