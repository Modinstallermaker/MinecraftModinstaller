package installer;

import java.io.File;
import java.io.IOException;

public class Modinfo
{
	private String[] spl;
	private String inhalt="";
	private String modname="";
	private String res="";
	private File speicherort;
	private boolean fertig=false;
	private boolean fertig2=false;
	
	public Modinfo(final String modname) 
	{
		
		this.modname = modname;	
		try {
			if(new OP().optionReader("language").equals("de"))
					speicherort= new File(Start.stamm +"/Modinstaller/Texte/"+modname+".txt");
			else
				speicherort= new File(Start.stamm +"/Modinstaller/Texte_en/"+modname+".txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		new Thread() 
		{			
			public void run() 
			{	
				if(!speicherort.exists())
				{					
					try {
						if(new OP().optionReader("language").equals("de"))
								speicherort= new File(Start.stamm +"/Modinstaller/Texte/404.txt");
						else
							speicherort= new File(Start.stamm +"/Modinstaller/Texte_en/404.txt");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(speicherort.exists())
				{			
					try 
					{
						String[] modi = new OP().Textreader(speicherort);
						for(int o=0; o<modi.length; o++)
						{
							inhalt+=modi[o];
						}
						if (inhalt.length()>1) 
				        {
					        spl = inhalt.split(";;");
				        }
					}
					catch (Exception e)
					{
						
					}
				}
				fertig=true;				
			}
		}.start();			
	}
	
	public String getModname()
	{
		return modname;
	}
	
	public String getDescription()
	{		
		if(spl!=null&&spl.length>0)
    		return spl[0];
    	else
    		return "error";		
	}
	
	public String getHyperlink()
	{		
		if(spl!=null&&spl.length>1)
    		return spl[1];
    	else
    		return "error";	
	}
	
	public String getCompatibleWith()
	{		
		if(spl!=null&&spl.length>2)
    		return spl[2];
    	else
    		return "error";	
	}
	
	public String getIncompatibleWith()
	{		
		if(spl!=null&&spl.length>3)
    		return spl[3];
    	else
    		return "error";	
	}
	
	public void setRating(String Rating)
	{
		this.res = Rating;
		fertig2=true;
	}
	
	public double getRating()
	{
		if(fertig2)
			if(!res.equals("error"))
				return Double.parseDouble(res);
			else
				return -1;
		else
			return 0;			
	}
	
	public boolean fertig()
	{
		if(fertig)
			return true;	
		else
			return false;
	}
	
	public boolean fertig2()
	{
		if(fertig2)
			return true;	
		else
			return false;
	}
}
