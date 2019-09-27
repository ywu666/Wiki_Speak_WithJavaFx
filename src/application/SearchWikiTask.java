package application;


import javafx.concurrent.Task;

/**
 * The class will do long search wikit class
 */
public class SearchWikiTask extends Task<Void>{

	private String _variable ;
	private String _history;
	
	public SearchWikiTask(String variable,String dir) {
		_variable = variable;
	    _history = dir;
	}
		
		
	@Override 
	protected Void call() throws Exception {
		String command = "wikit "+ _variable+">"+_history+";sed -i 's/[.!?]  */&\\n/g' "+_history;

		ProcessBuilder redirect = new ProcessBuilder("bash", "-c",command);
		Process p = redirect.start();
        p.waitFor();
		
		return null;
	}
	


}
