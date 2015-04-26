package installer;

import java.io.File;
import java.util.LinkedList;

public class FileFinder
{
	public LinkedList<File> findings = new LinkedList<File>();
	
	public void sucheDatei(String name, File directory, FoundListener fnd)
	{
		if(directory.exists())
		{
			File[] files = directory.listFiles();
			for(int i=0; i< files.length; i++)
			{
				if(files[i] != null && files[i].exists())
				{
						if(files[i].getName().contains(name))
						{
							findings.add(files[i]);
							if(fnd != null) fnd.fileFound(files[i]);
						}
						if(files[i].isDirectory())
							sucheDatei(name, files[i], fnd);
				}
			}
		}
	}
	
	public void sucheDateiNamen(String name, File directory, FoundListener fnd)
	{
		if(directory.exists())
		{
			File[] files = directory.listFiles();
			for(int i=0; i< files.length; i++)
			{
				if(files[i] != null && files[i].exists())
				{
						if(files[i].getName().equals(name))
						{
							findings.add(files[i]);
							if(fnd != null) fnd.fileFound(files[i]);
						}
						if(files[i].isDirectory())
							sucheDateiNamen(name, files[i], fnd);
				}
			}
		}
	}
	
	public void sucheDateiMitRegEx(String regEx, File directory, FoundListener fnd)
	{
		if(directory.exists())
		{
			File[] files = directory.listFiles();
			for(int i=0; i< files.length; i++)
			{
				if(files[i] != null && files[i].exists())
				{
						if(files[i].getName().matches(regEx))
						{
							findings.add(files[i]);
							if(fnd != null) fnd.fileFound(files[i]);
						}
						if(files[i].isDirectory())
							sucheDateiMitRegEx(regEx, files[i], fnd);
				}
			}
		}
	}
	
}
