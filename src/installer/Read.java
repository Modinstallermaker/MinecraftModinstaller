package installer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static installer.OP.*;


public class Read
{
	public static final Read INSTANCE = new Read();
	static JsonObject js1;

	public Read()
	{
		String lang = optionReader("language");
		 
		String json=new OP().getInternalText("src/texte_"+lang+".json");
		
		Gson gson = new Gson();	
		js1 = gson.fromJson(json, JsonObject.class); 
	}
  
	public static String getTextwith(String vor, String nach)
	{
		JsonObject js2 = js1.getAsJsonObject(vor); 	 
		return js2.get(nach).getAsString();
	}
}