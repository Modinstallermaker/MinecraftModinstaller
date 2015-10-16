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
import java.util.Scanner;

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
	private static FileOutputStream fileOutputStream;
	private static FileInputStream fileInputStream;

	final static public boolean del(File dir)
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
	
	final static public boolean del(String dir)
	{
		return del(new File(dir));
	}
	
	final static public void copy(File quelle, File ziel) throws FileNotFoundException, IOException 
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
		
	final static public void copyFile(File source, File target) throws IOException
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
		      fileInputStream = new FileInputStream(source);
		      sourceChannel = fileInputStream.getChannel();
		      fileOutputStream = new FileOutputStream(target);
		      targetChannel = fileOutputStream.getChannel();
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
	  
	  
	final static public void transfer(FileChannel inputChannel, ByteChannel outputChannel, long lengthInBytes, long chunckSizeInBytes, boolean verbose) throws IOException 
	  {
	    long overallBytesTransfered = 0L;
	
	    while (overallBytesTransfered < lengthInBytes) 
	    {
	      long bytesToTransfer = Math.min(chunckSizeInBytes, lengthInBytes - overallBytesTransfered);
	      long bytesTransfered = inputChannel.transferTo(overallBytesTransfered, bytesToTransfer, outputChannel);
	      
	      overallBytesTransfered += bytesTransfered;
	    }
	  }
	
	final static public void copyDir(File quelle, File ziel) throws FileNotFoundException,IOException 
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
	
	final static public void rename(File alt, File neu) throws FileNotFoundException,IOException 
	{     
        alt.renameTo(neu);
	}
	
	final static public void makedirs(File f)
	{
		if(!f.exists())	f.mkdirs();
	}
	
	final static public void Textwriters(File datei, String line, boolean weiterschreiben) throws IOException
	{		
		String lines[] = {line};
		Textwriter(datei, lines, weiterschreiben);
	}	
		
	final static public void Textwriter(File datei, String[] lines, boolean weiterschreiben) throws IOException
	{		
		boolean umbruch = false;
		if(datei.length()!=0&&weiterschreiben==true) umbruch = true;
	    
		BufferedWriter f = new BufferedWriter(new FileWriter(datei.toString(), weiterschreiben));
        for (int i = 0; i < lines.length; ++i) 
        {	
        	if(umbruch) f.newLine();
	        f.write(lines[i]);
	        if(i<lines.length-1) f.newLine();
        }
        f.close();	     	   
	}	
		
	final static public String[] Textreader(File datei) throws IOException
	{		  	  
	    ArrayList<String> text = new ArrayList<String>();
	    BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(datei)));
        String line;
        while ((line = f.readLine()) != null) 
        {
        	text.add(line);
        }        	      
        f.close();
        return text.toArray(new String[text.size()]);
	}
	
	final static public String[] Textreader(File datei, String charset) throws IOException
	{
        ArrayList<String> text = new ArrayList<String>();
	    BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(datei), charset));
        String line;
        while ((line = f.readLine()) != null) 
        {
        	text.add(line);
        }        	      
        f.close();
        return text.toArray(new String[text.size()]);
	}
	
	final static public String Textreaders(File datei) throws IOException
	{
		String[] x = Textreader(datei);
        
		String inh="";
		for (int i=0; i<x.length; i++)
			inh+=x[i];
		
        return inh;
	}
	
	final static public ArrayList<String> Textreadera(File datei) throws IOException
	{		
		String line="";
	    ArrayList<String> list = new ArrayList<String>();
	    BufferedReader  f = new BufferedReader(new InputStreamReader(new FileInputStream(datei)));
        while ((line = f.readLine()) != null) 
        {
        	list.add(line);
        }        	      
        f.close();
        return list;
	}
	
	final static public ArrayList<String> Textreadera(File datei, String charset) throws IOException
	{		
		String line="";
	    ArrayList<String> list = new ArrayList<String>();
	    BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(datei), charset));
        while ((line = f.readLine()) != null) 
        {
        	list.add(line);
        }        	      
        f.close();
        return list;
	}
	
	final static public int version (String[] options)
	{
		return JOptionPane.showOptionDialog(null, Read.getTextwith("OP", "modver"), Read.getTextwith("OP", "modverh"), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);		
	}
	
	final static public String optionReader(String attrib)
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
	
	final static public void optionWriter(String attrib, String content)
	{
		File configf = new File(Start.stamm + "/Modinstaller/config.txt");
		String[] inhalt = null;
		boolean inside = false;
		if(configf.exists())
		{		
			try {
				inhalt = Textreader(configf);
			} catch (IOException e) {}		
		
			for (int i=0; i<inhalt.length; i++)
			{
				if(inhalt[i].split(":")[0].equals(attrib))
				{
					inhalt[i]=inhalt[i].split(":")[0]+":"+content;
					inside=true;
				}
			}
		}
		if (inside)
			try {
				Textwriter(configf, inhalt, false);
			} catch (IOException e) {}
		else
		{
			String[] neu = {attrib +":"+content};
			try {
				Textwriter(configf, neu, true);
			} catch (IOException e) {}
		}
	}
	
	public String getInternalText(String dir)
	{
		String json="";
		Scanner scan = new Scanner(getClass().getResourceAsStream(dir), "UTF-8");
		while (scan.hasNextLine()) 
		{
			json += scan.nextLine();
		}
		scan.close();
		return json;
	}
	
	final static public String getError(Throwable aThrowable) 
	{
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
}
