package installer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Browser 
{
	public Browser(String url)
	{
		try 
		{
			Desktop.getDesktop().browse(new URI(url));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
		}
	}
}
