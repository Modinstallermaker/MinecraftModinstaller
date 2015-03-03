package installer;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class MCLauncher 
{	
	public MCLauncher(final String webplace, final String stamm)
	{
		new OP().makedirs(new File(stamm +"/Modinstaller/"));
		boolean ausf = true;
		
		String str = System.getProperty("os.name").toLowerCase(); 
		File speicherort;
		String downloadort = webplace + "Launcher2.jar";
		
		 if (str.contains("win"))	
		 {
			File spo = new File(System.getenv("programfiles(x86)")+"/Minecraft/MinecraftLauncher.exe");			
			if(spo.exists())
				runExe(spo);
			
			File spo2 = new File(System.getenv("programfiles")+"/Minecraft/MinecraftLauncher.exe");
			if(spo2.exists())
				runExe(spo);
			
			speicherort = new File(stamm +"/Modinstaller/MCLauncher.exe");
		 	downloadort = webplace + "Launcher2.exe";
		 }
		 else
		 {
			 speicherort = new File(stamm +"/Modinstaller/MCLauncher.jar");	
			 downloadort = webplace + "Launcher2.jar";
		 }
			
		new OP().makedirs(speicherort.getParentFile());	
		try 
		{
			if(!new Download().ident(downloadort, speicherort)||speicherort.length()==0)						
			{		
				new Download().downloadFile(downloadort, new FileOutputStream(speicherort));		
				ausf =true;				
			}
		} 
		catch (Exception e) 
		{
			ausf =false;
		} 
		if(ausf ==false)
		{
			int eingabe = JOptionPane.showConfirmDialog(null, Read.getTextwith("startLauncher", "prog1"), Read.getTextwith("startLauncher", "prog1h"), JOptionPane.YES_NO_OPTION);
			if(eingabe == 0)
			{
				JFileChooser FC = new JFileChooser();
				FC.setDialogTitle(Read.getTextwith("startLauncher", "prog2"));
				FC.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FC.addChoosableFileFilter(new SimpleGraphicFileFilter());
				FC.setMultiSelectionEnabled(false);
				if (FC.showOpenDialog(FC) == JFileChooser.APPROVE_OPTION) // Ordner �ffnen
				{
					try
					{
						new OP().copy(FC.getSelectedFile(), speicherort);		
						ausf=true;
					}
					catch (Exception ex)
					{	
						JOptionPane.showMessageDialog(null, ex + "\n\nErrorcode: STx02");
					}
				}        
			}
		}
	 
		if(ausf==true)
		{
			 if (str.contains("win"))	
				 runExe(speicherort);
			 else
				 runJar(speicherort);
		}	
	}
	
	void runExe(File exe)
	{
		try 
		{				
			Runtime.getRuntime().exec(exe.toString());				
		} 
		catch (Exception ex) 
		{					
		}
		System.exit(0);
	}
	
	void runJar(File jar)
	{
		try 
		{				
			Runtime.getRuntime().exec("java -jar " + jar.toString());			
		} 
		catch (Exception ex) 
		{			
		}
		System.exit(0);
	}
}