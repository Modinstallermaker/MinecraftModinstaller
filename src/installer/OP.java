package installer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import org.tukaani.xz.XZInputStream;

public class OP 
{
	private static FileOutputStream fos;
	private static FileInputStream fis;	

	final static public boolean del(File dir)
	{
		if(dir.exists())
		{
			File f = dir;
			if (f.isDirectory()) 
			{
				File[] files = f.listFiles();
				
				for (File file : files) 
				{
					del(file);
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
	
	final static public void copy(File source, File target) throws FileNotFoundException, IOException 
	{
		if(source.exists())
		{
			if(source.isDirectory())	
			{
				makedirs(target);
				copyDir(source, target);				
			}
			else
			{
				makedirs(target.getParentFile());
				copyFile(source, target);
			}
		}
	}			  
		
	final static public void copyFile(File source, File target) throws IOException
	{
		if(source.exists())
		{
		    if (!target.exists()) target.createNewFile();
		    
		    FileChannel sourceChannel = null;
		    FileChannel targetChannel = null;		    
		    try
		    {
				fis = new FileInputStream(source);
				sourceChannel = fis.getChannel();
				fos = new FileOutputStream(target);
				targetChannel = fos.getChannel();
				targetChannel.transferFrom(sourceChannel, 0L, sourceChannel.size());
		    }
		    finally
		    {
				if (sourceChannel != null) sourceChannel.close();
				if (targetChannel != null) targetChannel.close();		      
		    }
		}
	}
	
	final static public void copyDir(File source, File target) throws FileNotFoundException,IOException 
	{
		if(source.exists())
		{
			File[] files = source.listFiles();
			target.mkdirs();
			for (File file : files) 
			{
				if (file.isDirectory()) 
				{
					copyDir(file, new File(target.getAbsolutePath()+ System.getProperty("file.separator") + file.getName()));
				} 
				else 
				{
					copyFile(file, new File(target.getAbsolutePath() + System.getProperty("file.separator") + file.getName()));
				}
			}
		}
	}
	
	final static public void rename(File oldFile, File newFile) throws FileNotFoundException,IOException 
	{     
        oldFile.renameTo(newFile);
	}
	

    public static void unpackLibrary(File output, byte[] data) throws IOException
    {
        if (output.exists())
        {
            output.delete();
        }

        byte[] decompressed = readFully(new XZInputStream(new ByteArrayInputStream(data)));
        
        String end = new String(decompressed, decompressed.length - 4, 4);
        if (!end.equals("SIGN"))
        {
            System.out.println("Unpacking failed, signature missing " + end);
            return;
        }

        int x = decompressed.length;
        int len =
                ((decompressed[x - 8] & 0xFF)      ) |
                ((decompressed[x - 7] & 0xFF) << 8 ) |
                ((decompressed[x - 6] & 0xFF) << 16) |
                ((decompressed[x - 5] & 0xFF) << 24);

        File temp = File.createTempFile("art", ".pack");

        byte[] checksums = Arrays.copyOfRange(decompressed, decompressed.length - len - 8, decompressed.length - 8);

        OutputStream out = new FileOutputStream(temp);
        out.write(decompressed, 0, decompressed.length - len - 8);
        out.close();
        decompressed = null;
        data = null;
        System.gc();

        FileOutputStream jarBytes = new FileOutputStream(output);
        JarOutputStream jos = new JarOutputStream(jarBytes);

        Pack200.newUnpacker().unpack(temp, jos);

        JarEntry checksumsFile = new JarEntry("checksums.sha1");
        checksumsFile.setTime(0);
        jos.putNextEntry(checksumsFile);
        jos.write(checksums);
        jos.closeEntry();

        jos.close();
        jarBytes.close();
        temp.delete();
    }     
   
	public static byte[] readFully(InputStream stream) throws IOException
	{
		byte[] data = new byte[4096];
        ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
        int len;
        do
        {
            len = stream.read(data);
            if (len > 0)
            {
                entryBuffer.write(data, 0, len);
            }
        } while (len != -1);

        return entryBuffer.toByteArray();
	}
	
    final static public void makedirs(File folder)
	{
		if(!folder.exists()) folder.mkdirs();
	}
	
	final static public void Textwriters(File file, String line, boolean append) throws IOException
	{		
		String lines[] = {line};
		Textwriter(file, lines, append);
	}	
		
	final static public void Textwriter(File file, String[] lines, boolean append) throws IOException
	{	
		if(!file.exists())
			file.createNewFile();
		
		boolean newLine = false;
		if(file.length()!=0&&append==true) newLine = true;
		
		FileWriter fr = null;
		BufferedWriter bw = null;
		try
		{
			fr = new FileWriter(file.toString(), append);
			bw = new BufferedWriter(fr);
	        for (int i = 0; i < lines.length; ++i) 
	        {	
	        	if(newLine) bw.newLine();
		        bw.write(lines[i]);
		        if(i<lines.length-1) bw.newLine();
	        }
		}
		finally
		{
			if(bw!=null) bw.close();
			if(fr!=null) fr.close();
		}   	   
	}	
		
	final static public String[] Textreader(File file) throws IOException
	{		
	    ArrayList<String> text = new ArrayList<String>();
	   
	    FileReader fr = null;
	    BufferedReader br = null;
	    try
	    {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			String line;
			while ((line = br.readLine()) != null) 
			{
				text.add(line);
			} 
	    }
	    finally
	    {
			if(br != null) br.close();
			if(fr != null) fr.close();
	    }
	   
        return text.toArray(new String[text.size()]);
	}
	
	final static public String[] Textreader(File file, String charset) throws IOException
	{
        ArrayList<String> arrayStr = new ArrayList<String>();
        
        FileInputStream fis = null;
        InputStreamReader isr = null;
	    BufferedReader br = null;
        
	    try
	    {
	        fis = new FileInputStream(file);
	        isr = new InputStreamReader(fis, charset);
	        br = new BufferedReader(isr);
	       
		    String line;
	        while ((line = br.readLine()) != null) 
	        {
	        	arrayStr.add(line);
	        }      
	    }
	    finally
	    {
	    	if(br != null) br.close();
			if(isr != null) isr.close();
			if(fis != null) fis.close();
	    }
	    
        return arrayStr.toArray(new String[arrayStr.size()]);
	}
	
	final static public String Textreaders(File file) throws IOException
	{
		String[] StrArr = Textreader(file);
        
		String inh="";
		for (int i=0; i<StrArr.length; i++)
			inh+=StrArr[i];
		
        return inh;
	}
	
	final static public ArrayList<String> Textreadera(File datei) throws IOException
	{		
		String line="";
	    ArrayList<String> list = new ArrayList<String>();
	    
	    FileInputStream fis = null;
	    InputStreamReader isr = null;
	    BufferedReader  br = null;	    
	    
	    try
	    {
			fis = new FileInputStream(datei);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
	        while ((line = br.readLine()) != null) 
	        {
	        	list.add(line);
	        } 
	    }
	    finally
	    {
	    	if(br != null) br.close();
			if(isr != null) isr.close();
			if(fis != null) fis.close();
	    }
	    
        return list;
	}
	
	final static public ArrayList<String> Textreadera(File datei, String charset) throws IOException
	{		
		String line="";
	    ArrayList<String> list = new ArrayList<String>();
	    
	    FileInputStream fis = null;
	    InputStreamReader isr = null;
	    BufferedReader br = null;
	    
	    try
	    {
		    fis = new FileInputStream(datei);
		    isr = new InputStreamReader(fis, charset);
		    br = new BufferedReader(isr);
	        while ((line = br.readLine()) != null) 
	        {
	        	list.add(line);
	        }   
	    }
	    finally
	    {
	    	if(br != null) br.close();
			if(isr != null) isr.close();
			if(fis != null) fis.close();
	    }
	    
        return list;
	}
		
	final static public String optionReader(String attrib)
	{
		File configf = new File(Start.stamm + "/Modinstaller/config.txt");
		String text=null;
		boolean exist=false;
		String[] inhalt = null;
		try 
		{
			inhalt = Textreader(configf);	
		
			for (int i=0; i<inhalt.length; i++)
			{
				if(inhalt[i].split(":")[0].equals(attrib))
				{
					text= inhalt[i].split(":")[1];
					exist=true;
				}
			}
		} 
		catch (IOException e){}
		
		if(exist==false) text="n/a";
		
		return text;		
	}
	
	final static public void optionWriter(String attrib, String content)
	{
		File configf = new File(Start.stamm + "/Modinstaller/config.txt");
		String[] inhalt = null;
		boolean inside = false;
		if(configf.exists())
		{		
			try 
			{
				inhalt = Textreader(configf);
			} 
			catch (IOException e) {}		
		
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
		{
			try 
			{
				Textwriter(configf, inhalt, false);
			}
			catch (IOException e) {}
		}
		else
		{
			String[] neu = {attrib +":"+content};
			try 
			{
				Textwriter(configf, neu, true);
			} 
			catch (IOException e) {}
		}
	}
	
	public String getSizeAsString(double size)
	{	
		String unit = "Byte";
		if(size>1024)
		{
			size/=1024.;
			unit="KB";
		}
		if(size>1024)
		{
			size/=1024.;
			unit="MB";
		}
		if(size>1024)
		{
			size/=1024.;
			unit="GB";
		}
		size = Math.round(size*10.)/10.;
		
		return String.valueOf(size)+" "+unit;
	}
	
	public String getInternalText(String internalFile)
	{
		String json="";
		Scanner scan = null;
		try
		{
			scan = new Scanner(getClass().getResourceAsStream(internalFile), "UTF-8");
			while (scan.hasNextLine()) 
			{
				json += scan.nextLine();
			}
		}
		finally
		{
			if(scan!=null) scan.close();
		}
		
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
