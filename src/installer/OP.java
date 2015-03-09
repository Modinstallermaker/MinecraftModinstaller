package installer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.swing.JOptionPane;


/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class OP 
{			
	public boolean del(File dir)
	{
		if(dir.exists())
		{
			File f = dir;
			if (f.isDirectory()) 
			{
				
					File[] files = f.listFiles();
					
						for (File aktFile : files) 
						{
							del(aktFile);
						}
					
				
			}
			return f.delete();
		}
		else
		{
			return false;
		}
	}
	
	boolean del(String dir)
	{
		return del(new File(dir));
	}
	
	public void copy(File quelle, File ziel) throws FileNotFoundException, IOException 
	{
		if(quelle.exists())
		{
			if(quelle.isDirectory())	
			{
				makedirs(ziel);
				copyDir(quelle, ziel);				
			}
			else
			{
				makedirs(ziel.getParentFile());
				copyFile(quelle, ziel);
			}
		}
	}
	public void copy(String quelle, String ziel) throws FileNotFoundException, IOException
	{
		copy(new File(quelle), new File(ziel));
	}
			  
		
	public static void copyFile(File source, File target) throws IOException
	  {
		if(source.exists())
		{
		    if (!target.exists()) {
		      target.createNewFile();
		    }
		    FileChannel sourceChannel = null;
		    FileChannel targetChannel = null;
		    try
		    {
		      sourceChannel = new FileInputStream(source).getChannel();
		      targetChannel = new FileOutputStream(target).getChannel();
		      targetChannel.transferFrom(sourceChannel, 0L, sourceChannel.size());
		    }
		    finally
		    {
		      if (sourceChannel != null) {
		        sourceChannel.close();
		      }
		      if (targetChannel != null) {
		        targetChannel.close();
		      }
		    }
		}
	  }
	  
	  
	  public static void transfer(FileChannel inputChannel, ByteChannel outputChannel, long lengthInBytes, long chunckSizeInBytes, boolean verbose) throws IOException 
	  {
	    long overallBytesTransfered = 0L;
	    long time = -System.currentTimeMillis();
	    while (overallBytesTransfered < lengthInBytes) {
	      long bytesToTransfer = Math.min(chunckSizeInBytes, lengthInBytes - overallBytesTransfered);
	      long bytesTransfered = inputChannel.transferTo(overallBytesTransfered, bytesToTransfer, outputChannel);
	      
	      overallBytesTransfered += bytesTransfered;
	      
	      if (verbose) {
	        long percentageOfOverallBytesTransfered = Math.round(overallBytesTransfered / ((double) lengthInBytes) * 100.0);
	        System.out.printf("overall bytes transfered: %s progress %s%%\n", overallBytesTransfered, percentageOfOverallBytesTransfered);
	      }
	      
	    }
	    time += System.currentTimeMillis();
	    
	    if (verbose) {
	      double kiloBytesPerSecond = (overallBytesTransfered / 1024.0) / (time / 1000.0);
	      System.out.printf("Transfered: %s bytes in: %s s -> %s kbytes/s", overallBytesTransfered, time / 1000, kiloBytesPerSecond);
	    }
	    
	  }
	
	public void copyDir(File quelle, File ziel) throws FileNotFoundException,IOException 
	{
		if(quelle.exists())
		{
			File[] files = quelle.listFiles();
			ziel.mkdirs();
			for (File file : files) 
			{
				if (file.isDirectory()) 
				{
					copyDir(file,new File(ziel.getAbsolutePath()+ System.getProperty("file.separator")+ file.getName()));
				} 
				else 
				{
					copyFile(file,new File(ziel.getAbsolutePath()+ System.getProperty("file.separator")	+ file.getName()));
				}
			}
		}
	}
	
	public void rename(File alt, File neu) throws FileNotFoundException,IOException 
	{     
        alt.renameTo(neu);
	}
	
	public void makedirs(File f)
	{
		if(!f.exists())	f.mkdirs();
	}
		
	public void Textwriter(File datei, String[] lines, boolean weiterschreiben) throws IOException
	{
		BufferedWriter f;	
		boolean umbruch = false;
		if(datei.length()!=0&&weiterschreiben==true) umbruch = true;
	    
        f = new BufferedWriter(new FileWriter(datei.toString(), weiterschreiben));
        for (int i = 0; i < lines.length; ++i) 
        {	
        	if(umbruch) f.newLine();
	        f.write(lines[i]);
	        if(i<lines.length-1) f.newLine();
        }
        f.close();	     	   
	}	
		
	public String[] Textreader(File datei) throws IOException
	{
		BufferedReader f;	  	  
	    String line, alles="";
        f = new BufferedReader(new InputStreamReader(new FileInputStream(datei)));
        while ((line = f.readLine()) != null) 
        {
        	alles+=line+";;;;";
        }        	      
        f.close();
        if(alles.endsWith(";;;;"))
        	alles = alles.substring(0, alles.length()-4);
        
        return alles.split(";;;;");
	}
	
	public String[] Textreader(File datei, String charset) throws IOException
	{
		BufferedReader f;	  	  
	    String line, alles="";
        f = new BufferedReader(new InputStreamReader(new FileInputStream(datei), charset));
        while ((line = f.readLine()) != null) 
        {
        	alles+=line+";;;;";
        }        	      
        f.close();
        if(alles.endsWith(";;;;"))
        	alles = alles.substring(0, alles.length()-4);
        
        return alles.split(";;;;");
	}
	
	public String Textreaders(File datei) throws IOException
	{
		String[] x = Textreader(datei);
        
		String inh="";
		for (int i=0; i<x.length; i++)
			inh+=x[i];
		
        return inh;
	}
	
	public ArrayList<String> Textreadera(File datei) throws IOException
	{
		BufferedReader f;
		String line="";
	    ArrayList<String> list = new ArrayList<String>();
        f = new BufferedReader(new InputStreamReader(new FileInputStream(datei)));
        while ((line = f.readLine()) != null) 
        {
        	list.add(line);
        }        	      
        f.close();
        return list;
	}
	
	public ArrayList<String> Textreadera(File datei, String charset) throws IOException
	{
		BufferedReader f;
		String line="";
	    ArrayList<String> list = new ArrayList<String>();
        f = new BufferedReader(new InputStreamReader(new FileInputStream(datei), charset));
        while ((line = f.readLine()) != null) 
        {
        	list.add(line);
        }        	      
        f.close();
        return list;
	}
	
	public int version (String[] options)
	{
		return JOptionPane.showOptionDialog(null, Read.getTextwith("OP", "modver"), Read.getTextwith("OP", "modverh"), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);		
	}
	
	public String optionReader(String attrib)
	{
		File configf = new File(Start.stamm + "/Modinstaller/config.txt");
		String rueck=null;
		boolean exist=false;
		String[] inhalt;
		try 
		{
			inhalt = Textreader(configf);	
		
			for (int i=0; i<inhalt.length; i++)
			{
				if(inhalt[i].split(":")[0].equals(attrib))
				{
					rueck= inhalt[i].split(":")[1];
					exist=true;
				}
			}
		} 
		catch (IOException e) 
		{
		}
		if(exist==false) rueck="n/a";
		return rueck;		
	}
	
	public void optionWriter(String attrib, String content) throws IOException
	{
		File configf = new File(Start.stamm + "/Modinstaller/config.txt");
		String[] inhalt = Textreader(configf);
		boolean inside = false;
		for (int i=0; i<inhalt.length; i++)
		{
			if(inhalt[i].split(":")[0].equals(attrib))
			{
				inhalt[i]=inhalt[i].split(":")[0]+":"+content;
				inside=true;
			}
		}
		if (inside) Textwriter(configf, inhalt, false);
		else
		{
			String[] neu = {attrib +":"+content};
			Textwriter(configf, neu, true);
		}
	}
	
	  public String getStackTrace(Throwable aThrowable) 
	  {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	  }	
}
