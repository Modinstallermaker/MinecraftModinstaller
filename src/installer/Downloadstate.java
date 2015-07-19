package installer;

import java.io.File;

import javax.swing.JLabel;

public class Downloadstate implements Runnable 
{
	private Download dow;
	private File Speicherort;
	private JLabel stat;
	private String text;
		
	public Downloadstate(Download dow, File Speicherort, JLabel stat, String text)
	{
		this.dow = dow;
		this.Speicherort = Speicherort;
		this.stat = stat;
		this.text = text;
	}

	   public void run() {
		   									
			while(!Thread.currentThread().isInterrupted())
			{	
				try 
				{
					int ist = dow.groesse(Speicherort);												
					if(ist>1)
					{
						int soll = dow.getGroesse();													
						if(soll>1)
						{	
							double proz = Math.round(((double)ist/(double)soll)*1000.)/10.;
							stat.setText(text+" - "+String.valueOf(proz)+"%");			       //Downloadstatus anzeigen	
							Install.status(proz);
						}																					
					}
					
					Thread.sleep(50);
				} 
				catch (Exception e) 
				{
					Thread.currentThread().interrupt();
				} 
			}
	   }
}