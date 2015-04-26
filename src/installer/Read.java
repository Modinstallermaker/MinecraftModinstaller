package installer;

import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class Read
{
	public static final Read INSTANCE = new Read();
	private Scanner scan;
	static JsonObject js1;

	public Read()
	{
		String lang = new OP().optionReader("language");
		 
		String json="";
		scan = new Scanner(getClass().getResourceAsStream("src/texte_"+lang+".json"), "UTF-8");
		while (scan.hasNextLine()) 
		{
			json += scan.nextLine();
		}
		scan.close();
		
		Gson gson = new Gson();	
		js1 = gson.fromJson(json, JsonObject.class); 
	}
  
	public static String getTextwith(String vor, String nach)
	{
		JsonObject js2 = js1.getAsJsonObject(vor); 	 
		return js2.get(nach).getAsString();
	}
}