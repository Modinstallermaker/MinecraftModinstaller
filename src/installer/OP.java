package installer;

import java.io.BufferedOutputStream;
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
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

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

        //Snag the checksum signature
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
        System.out.println("  Signed");
        System.out.println("  Checksum Length: " + len);
        System.out.println("  Total Length:    " + (decompressed.length - len - 8));
        System.out.println("  Temp File:       " + temp.getAbsolutePath());

        byte[] checksums = Arrays.copyOfRange(decompressed, decompressed.length - len - 8, decompressed.length - 8);

        //As Pack200 copies all the data from the input, this creates duplicate data in memory.
        //Which on some systems triggers a OutOfMemoryError, to counter this, we write the data
        //to a temporary file, force GC to run {I know, eww} and then unpack.
        //This is a tradeoff of disk IO for memory.
        //Should help mac users who have a lower standard max memory then the rest of the world (-.-)
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
    
    public static void copyAndStrip(File sourceJar, File targetJar) throws IOException
    {
        ZipFile in = new ZipFile(sourceJar);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(targetJar)));

        for (ZipEntry e : Collections.list(in.entries()))
        {
            if (e.isDirectory())
            {
                out.putNextEntry(e);
            }
            else if (e.getName().startsWith("META-INF"))
            {
            }
            else
            {
                ZipEntry n = new ZipEntry(e.getName());
                n.setTime(e.getTime());
                out.putNextEntry(n);
                out.write(readEntry(in, e));
            }
        }

        in.close();
        out.close();
    }
    
    private static byte[] readEntry(ZipFile inFile, ZipEntry entry) throws IOException
    {
        return readFully(inFile.getInputStream(entry));
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
	
	 public static boolean downloadFileEtag(String libName, File libPath, String libURL)
	    {
		 try
	        {
	            URL url = new URL(libURL);
	            URLConnection connection = url.openConnection();
	            connection.setConnectTimeout(5000);
	            connection.setReadTimeout(5000);

	            String etag = connection.getHeaderField("ETag");
	            if (etag == null)
	            {
	              etag = "-";
	            }
	            else if ((etag.startsWith("\"")) && (etag.endsWith("\"")))
	            {
	                etag = etag.substring(1, etag.length() - 1);
	            }

	            InputSupplier<InputStream> urlSupplier = new URLISSupplier(connection);
	            Files.copy(urlSupplier, libPath);

	            if (etag.indexOf('-') != -1) return true; //No-etag, assume valid
	            try
	            {
	                byte[] fileData = Files.toByteArray(libPath);
	                String md5 = Hashing.md5().hashBytes(fileData).toString();
	                System.out.println("  ETag: " + etag);
	                System.out.println("  MD5:  " + md5);
	                return etag.equalsIgnoreCase(md5);
	            }
	            catch (IOException e)
	            {
	                e.printStackTrace();
	                return false;
	            }
	        }
	        catch (FileNotFoundException fnf)
	        {
	            if (!libURL.endsWith(PACK_NAME))
	            {
	                fnf.printStackTrace();
	            }
	            return false;
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	            return false;
	        }
	    }
	
	public static boolean checksumValid(File libPath, List<String> checksums)
{
	  try
        {
            byte[] fileData = Files.toByteArray(libPath);
            boolean valid = checksums == null || checksums.isEmpty() || checksums.contains(Hashing.sha1().hashBytes(fileData).toString());
            if (!valid && libPath.getName().endsWith(".jar"))
            {
                valid = validateJar(libPath, fileData, checksums);
            }
            return valid;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }	

    public static boolean validateJar(File libPath, byte[] data, List<String> checksums) throws IOException
    {
        System.out.println("Checking \"" + libPath.getAbsolutePath() + "\" internal checksums");

        HashMap<String, String> files = new HashMap<String, String>();
        String[] hashes = null;
        JarInputStream jar = new JarInputStream(new ByteArrayInputStream(data));
        JarEntry entry = jar.getNextJarEntry();
        while (entry != null)
        {
            byte[] eData = readFully(jar);

            if (entry.getName().equals("checksums.sha1"))
            {
                hashes = new String(eData, Charset.forName("UTF-8")).split("\n");
            }
            if (!entry.isDirectory())
            {
                files.put(entry.getName(),convertByteArrayToHexString(eData));
            }
            entry = jar.getNextJarEntry();
        }
        jar.close();

        if (hashes != null)
        {
            boolean failed = !checksums.contains(files.get("checksums.sha1"));
            if (failed)
            {
                System.out.println("    checksums.sha1 failed validation");
            }
            else
            {
                System.out.println("    checksums.sha1 validated successfully");
                for (String hash : hashes)
                {
                    if (hash.trim().equals("") || !hash.contains(" ")) continue;
                    String[] e = hash.split(" ");
                    String validChecksum = e[0];
                    String target = hash.substring(validChecksum.length() + 1);
                    String checksum = files.get(target);

                    if (!files.containsKey(target) || checksum == null)
                    {
                        System.out.println("    " + target + " : missing");
                        failed = true;
                    }
                    else if (!checksum.equals(validChecksum))
                    {
                        System.out.println("    " + target + " : failed (" + checksum + ", " + validChecksum + ")");
                        failed = true;
                    }
                }
            }

            if (!failed)
            {
                System.out.println("    Jar contents validated successfully");
            }

            return !failed;
        }
        else
        {
            System.out.println("    checksums.sha1 was not found, validation failed");
            return false; //Missing checksums
        }
    } 
    
    private static String hashString(String message)
    {     
    	byte[] hashedBytes = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            hashedBytes = digest.digest(message.getBytes("UTF-8"));                
        } 
        catch (Exception ex) {}
        return convertByteArrayToHexString(hashedBytes);
    }
    
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
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
