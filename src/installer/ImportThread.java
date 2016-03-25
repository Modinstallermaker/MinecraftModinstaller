package installer;

import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;

public class ImportThread extends Thread implements Runnable {
	
	List<File> files;
	MenuGUI men;
	
	public ImportThread(List<File> list, MenuGUI men)
	{
		this.files = list;
		this.men = men;
	}
	
	public void run()
	{		
		men.picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif"))); 		
		for (File f :  files) 
		{	
			men.modNameLabel.setText("Loading "+f.getName()+"...");
			new Import(f, men);											
		}	
	}	
}
