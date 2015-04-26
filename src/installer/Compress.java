package installer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class Compress 
{
	private File ortkop;
	private BufferedInputStream buffinstr;
	private JarOutputStream zipOut;
	
	public Compress(File ort2, File ziel)
	{
		ortkop = ort2;
		
		new OP().makedirs(ziel.getParentFile());
		
		try 
		{
			zipOut = new JarOutputStream(new FileOutputStream(ziel));
		} 
		catch (Exception ex) 
		{
			Install.Fehler+=new OP().getStackTrace(ex)+"\n\n";
		}
		zip(ort2);
		try 
		{
			zipOut.close(); // Jar Datei schlie�en
		} 
		catch (Exception ex) 
		{
			Install.Fehler+=new OP().getStackTrace(ex)+"\n\n";
		}
		
	}
	public Compress() 
	{	
	}
	
	public void zip(File ort)
	{
		try 
		{
			File[] files = ort.listFiles();
			if (files != null) 
			{
				for (int i = 0; i < files.length; i++) // Alle Dateien und Unterordner auflisten
				{
					if (files[i].isDirectory()) 
					{
						zip(files[i]); // Wenn Unterordner vorhanden diese erst durchsuchen
					} 
					else 
					{
						buffinstr = new BufferedInputStream(new FileInputStream(files[i])); // Datei einlesen
						int avail = buffinstr.available();
						byte[] buffer = new byte[avail];
						if (avail > 0) 
						{
							buffinstr.read(buffer, 0, avail);
						}
						String eintragname = files[i].getAbsolutePath().substring(ortkop.getAbsolutePath().length()).replace("\\", "/").substring(1);
						if (eintragname.equals("_aux.class")) 
						{
							eintragname = "aux.class";
						}
						JarEntry ze = new JarEntry(eintragname); // Datei in Jar Datei schreiben
						zipOut.putNextEntry(ze);
						zipOut.write(buffer, 0, buffer.length); // Byte buffer speichern
						zipOut.closeEntry();
					}
				}
			}
		} 
		catch (Exception ex) 
		{
			Install.Fehler+=new OP().getStackTrace(ex)+"\n\n";
		} 
		finally 
		{
			try 
			{
				if(buffinstr != null)	buffinstr.close(); // Inputstream beenden				
			} 
			catch (Exception ex) 
			{
				Install.Fehler+=new OP().getStackTrace(ex)+"\n\n";
			}				
		}	
	}
}
