package installer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static installer.OP.*;

public class Compress 
{	
	private BufferedInputStream bis;
	private JarOutputStream zipOut;
	private int fileNo = 0;
	private double addi = 1, val = 0.0, max = 100.0, start=0.0;
	private File source, target;
	private File sourcecpy;
	
	public Compress(File source, File target, double max, double start)
	{
		this.source=source;
		this.target=target;
		this.max = max;
		this.start = start;
		work();
	}		

	public Compress(File source, File target)
	{
		this.source=source;
		this.target=target;
		work();
	}
	
	void work()
	{
		sourcecpy = source;
		getFileNumber(source);
		addi = max/(double)fileNo;
		val = start;
		
		makedirs(target.getParentFile());
		
		try 
		{
			zipOut = new JarOutputStream(new FileOutputStream(target));
			zip(source);		
		}
		catch (Exception ex) 
		{
			Install.errors+=getError(ex)+"\n\n";
		}
		finally
		{
			try 
			{
				zipOut.close();
			}
			catch (IOException e) {} 
		} 
		Install.detState(max);
	}
	
	private void getFileNumber(File source)
	{
		File[] files = source.listFiles();
		for(File f: files)
		{
			if(f.isDirectory())
				getFileNumber(f);
			else
				fileNo++;			
		}
	}
	
	public void zip(File source)
	{		
		try 
		{
			File[] files = source.listFiles();
			if (files != null) 
			{			
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].isDirectory()) 
					{
						zip(files[i]);
					} 
					else 
					{
						FileInputStream fis = new FileInputStream(files[i]);
						bis = new BufferedInputStream(fis);
						int avail = bis.available();
						byte[] buffer = new byte[avail];
						if (avail > 0) 
						{
							bis.read(buffer, 0, avail);
						}
						
						String entryName = files[i].getAbsolutePath().substring(sourcecpy.getAbsolutePath().length()+1).replace("\\", "/");
						if (entryName.contains("_aux.class")) 
						{
							entryName = entryName.replace("_aux.class", "aux.class");
						}
						//Write file in JAR file
						JarEntry ze = new JarEntry(entryName);
						zipOut.putNextEntry(ze);
						int length = (int)files[i].length() ;
						zipOut.write(buffer, 0, length);
						Install.detState(val += addi);
						zipOut.closeEntry();
					}
				}
			}		
		} 
		catch (Exception ex) 
		{
			Install.errors+=getError(ex)+"\n\n";
		} 
		finally 
		{
			try 
			{
				if(bis != null)	bis.close();		
			} 
			catch (Exception ex) {}				
		}	
	}
}
