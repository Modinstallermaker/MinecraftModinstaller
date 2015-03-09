package installer;

import java.io.InputStream;
import java.io.InputStreamReader;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;


public class Read
{
  public static final Read INSTANCE = new Read();
  public JsonRootNode versionData = null;

  public Read()
  {
	String lang = new OP().optionReader("language");
	
    InputStream installProfile = getClass().getResourceAsStream("src/texte_"+lang+".json");
    JdomParser parser = new JdomParser();
    try
    {
      this.versionData = parser.parse(new InputStreamReader(installProfile, "UTF-8"));
    }
    catch (Exception e)
    {
      System.out.print(e);
    }
  }
  
  public static String getTextwith(String vor, String text)
  {
	  return INSTANCE.versionData.getStringValue(new Object[] { vor, text }); 
  }
  public static JsonNode getVersionInfo()
  {
	  return INSTANCE.versionData.getNode(new Object[] { "versionInfo" });
  }
}