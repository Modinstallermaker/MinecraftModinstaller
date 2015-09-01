package installer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static installer.OP.*;

public class Compress 
{
	private File sourcecpy;
	private BufferedInputStream buffinstr;
	private JarOutputStream zipOut;
	
	public Compress(File source, File target)
	{
		sourcecpy = source;
		
		makedirs(target.getParentFile());
		
		try 
		{
			zipOut = new JarOutputStream(new FileOutputStream(target));
		} 
		catch (Exception ex) 
		{
			Install.Fehler+=getError(ex)+"\n\n";
		}
		zip(source);
		try 
		{
			zipOut.close(); // Jar Datei schlie�en
		} 
		catch (Exception ex) 
		{
			Install.Fehler+=getError(ex)+"\n\n";
		}
		
	}
	public Compress() 
	{	
	}
	
	public void zip(File source)
	{
		try 
		{
			File[] files = source.listFiles();
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
						String eintragname = files[i].getAbsolutePath().substring(sourcecpy.getAbsolutePath().length()).replace("\\", "/").substring(1);
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
			Install.Fehler+=getError(ex)+"\n\n";
		} 
		finally 
		{
			try 
			{
				if(buffinstr != null)	buffinstr.close(); // Inputstream beenden				
			} 
			catch (Exception ex) 
			{
				Install.Fehler+=getError(ex)+"\n\n";
			}				
		}	
	}
}
