package installer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Extract 
{
	public Extract(File archive, File destDir) throws Exception 
	{
	        destDir.mkdir();	       
	 
	        ZipFile zipFile = new ZipFile(archive);
	        Enumeration<? extends ZipEntry> entries = zipFile.entries();
	 
	        byte[] buffer = new byte[16384];
	        int len;
	        while (entries.hasMoreElements()) 
	        {
	        	ZipEntry entry = (ZipEntry) entries.nextElement();
	 
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
	                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir, entryFileName)));	 
	                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
	 
	                while ((len = bis.read(buffer)) > 0) 
	                {
	                    bos.write(buffer, 0, len);
	                }
	 
	                bos.flush();
	                bos.close();
	                bis.close();
	            }	            
	        }
	                zipFile.close();
	                File meti = new File(destDir+"/META-INF");
	                new OP().del(meti);
	    }
	 
	    private File buildDirectoryHierarchyFor(String entryName, File destDir) 
	    {
	        int lastIndex = entryName.lastIndexOf('/');	       
	        String internalPathToEntry = entryName.substring(0, lastIndex + 1);
	        return new File(destDir, internalPathToEntry);
	    }
}
