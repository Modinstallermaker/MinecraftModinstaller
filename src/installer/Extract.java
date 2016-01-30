package installer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static installer.OP.del;

public class Extract 
{
	private File archive, destDir;
	private double max = 100.0, start = 0.0;
	
	public Extract(File archive, File destDir, double max, double start) throws Exception 
	{
		this.archive = archive;
		this.destDir = destDir;
		this.max = max;
		this.start = start;
		work();
	}
	public Extract(File archive, File destDir) throws Exception 
	{
		this.archive = archive;
		this.destDir = destDir;
        work();
    }
	
	private void work() throws ZipException, IOException
	{
		destDir.mkdir();	       
	     
    	ZipFile zipFile = null;
    
    	try
    	{
	        zipFile = new ZipFile(archive);
	        
	        double add = max/(double)zipFile.size();
	        double val = start;
	       
	        Enumeration<? extends ZipEntry> entries = zipFile.entries();	 
	        byte[] buffer = new byte[4096];
	        int len;
	        ZipEntry entry = null;
	        while (entries.hasMoreElements()) 
	        {	        	 
	        	entry = (ZipEntry) entries.nextElement();
	        	
	            String entryFileName = entry.getName();
	            
	            if(entryFileName.equals("aux.class")) 
	            	entryFileName="_aux.class";
	 
	            File  dir = buildDirectoryHierarchyFor(entryFileName, destDir);
	            if (!dir.exists()) 
	            {
	                dir.mkdirs();
	            }
		        if (!entry.isDirectory()&&!entryFileName.endsWith(".")&&!entryFileName.endsWith("..")) 
	            {
		        	FileOutputStream fos = null;
		        	BufferedOutputStream bos=null;
		           	BufferedInputStream bis =null;		           
		        	try
		        	{
		        		fos = new FileOutputStream(new File(destDir, entryFileName));
		                bos = new BufferedOutputStream(fos);	 
		                bis = new BufferedInputStream(zipFile.getInputStream(entry));
		 
		                while ((len = bis.read(buffer)) > 0) 
		                {
		                    bos.write(buffer, 0, len);
		                }
		                Install.detState(val += add);
		        	}
		        	finally
		        	{
		        		if(bos!=null)
		        		{
			        		bos.flush();
			        		bos.close();
		        		}
		        		if(bis!=null) bis.close();
		        		if(fos!=null) fos.close();
		        	}
	            }	            
	        }
    	}
    	finally
    	{
    		if(zipFile!=null) zipFile.close();
    	}
    	
        del(new File(destDir+"/META-INF"));
        
        Install.detState(max);
	}
	 
    private File buildDirectoryHierarchyFor(String entryName, File destDir) 
    {
        int lastIndex = entryName.lastIndexOf('/');	       
        String internalPathToEntry = entryName.substring(0, lastIndex + 1);
        
        return new File(destDir, internalPathToEntry);
    }
}
