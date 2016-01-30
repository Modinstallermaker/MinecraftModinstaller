package installer;

import java.awt.Desktop;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import static installer.OP.*;

/**
 * 
 * Beschreibung
 * 
 * @version 5.0
 * @author Dirk Lippke
 */

public class MCLauncher 
{	
	boolean hasstarted =false;
	
	public MCLauncher()
	{
		makedirs(new File(Start.stamm +"/Modinstaller/"));
		boolean ausf = true;		
		
		String str = System.getProperty("os.name").toLowerCase(); 
		File speicherort;
		String downloadort = Start.webplace + "Launcher2.jar";
		
		 if (str.contains("win"))	
		 {
			File spo = new File(System.getenv("programfiles(x86)")+"/Minecraft/MinecraftLauncher.exe");			
			if(spo.exists())
				runExe(spo);
			else
			{
				File spo2 = new File(System.getenv("programfiles")+"/Minecraft/MinecraftLauncher.exe");
				if(spo2.exists())
					runExe(spo);
			}
			speicherort = new File(Start.stamm +"/Modinstaller/MCLauncher.exe");
		 	downloadort = Start.webplace + "Launcher2.exe";
		 }
		 else
		 {
			 speicherort = new File(Start.stamm +"/Modinstaller/MCLauncher.jar");	
			 downloadort = Start.webplace.replace("\\", "/") + "Launcher2.jar";
		 }
		if(!hasstarted)	
		{
			makedirs(speicherort.getParentFile());	
			try 
			{
				Downloader dow = new Downloader(downloadort, speicherort);
				if(!dow.isDownloadSizeEqual()||speicherort.length()==0)						
				{		
					dow.run();		
					ausf =true;				
				}
			} 
			catch (Exception e) 
			{
				ausf =false;
			} 
			if(ausf ==false)
			{
				int eingabe = JOptionPane.showConfirmDialog(null, Read.getTextwith("MCLauncher", "prog1"), 
						Read.getTextwith("MCLauncher", "prog1h"), JOptionPane.YES_NO_OPTION);
				if(eingabe == 0)
				{
					JFileChooser FC = new JFileChooser();
					FC.setDialogTitle(Read.getTextwith("MCLauncher", "prog2"));
					FC.setFileSelectionMode(JFileChooser.FILES_ONLY);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("MC Launcher (.JAR)", "jar");
					FC.setFileFilter(filter);
					FC.setMultiSelectionEnabled(false);
					if (FC.showOpenDialog(FC) == JFileChooser.APPROVE_OPTION) // Ordner �ffnen
					{
						try
						{
							copy(FC.getSelectedFile(), speicherort);		
							ausf=true;
						}
						catch (Exception ex)
						{	
							JOptionPane.showMessageDialog(null, ex + "\n\nErrorcode: STx02");
						}
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

	private void runExe(File exe)
	{
		if(!hasstarted)
		{
			try 
			{	
				Desktop.getDesktop().open(exe);	
				hasstarted = true;
			} 
			catch (Exception ex) 
			{			
			}
		}
	}
	
	private void runJar(File jar)
	{
		if(!hasstarted)
		{
			try 
			{				
				Runtime.getRuntime().exec("java -jar " + jar.toString().replace("\\", "/"));
				hasstarted = true;
			} 
			catch (Exception ex) 
			{			
			}
		}
	}
}