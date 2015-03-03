package installer;

public class Modinfo
{
	private String name="", textde="", texten="", source="", lang="", MC="";
	int cat=3;
	private double rating=0.0;
		
	public void setName(String name)
	{
		this.name = name;
	}	
	public void setTextDe(String textde)
	{
		this.textde = textde;
	}
	public void setTextEn(String texten)
	{
		this.texten = texten;
	}
	public void setSource(String source)
	{
		this.source = source;
	}
	public void setRating(double rating)
	{
		this.rating = rating;
	}
	public void setLanguage(String lang)
	{
		this.lang = lang;
	}
	public void setMC(String MC)
	{
		this.MC = MC;
	}
	public void setCat(int cat)
	{
		this.cat = cat;
	}
	
	public String getName()
	{
		return name;
	}
	public String getText()
	{
		if(lang.equals("de"))
			return textde;
		else
			return texten;		
	}
	public String getSource()
	{
		return source;
	}
	public double getRating()
	{
		return rating;
	}	
	public String getMC()
	{
		return MC;
	}	
	public int getCat()
	{
		return cat;
	}	
}
