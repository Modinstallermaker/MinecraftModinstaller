package installer;

import static installer.OP.getError;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class Read
{
	public static final Read INSTANCE = new Read();
	static JsonObject js1;

	public Read()
	{
		String json=new OP().getInternalText("src/texte_"+Start.lang+".json");
		
		Gson gson = new Gson();	
		js1 = gson.fromJson(json, JsonObject.class); 
	}
  
	public static String getTextwith(String vor, String nach)
	{
		String back="";
		try
		{
			JsonObject js2 = js1.getAsJsonObject(vor); 	 
			back = js2.get(nach).getAsString();
		}
		catch (Exception e)
		{
			new Error("Text NOT found/readable: "+vor+", "+nach+" \n"+getError(e));
		}
		return back;
	}
}