package installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Download 
{
	float[] rue = new float[2];	
	private int groesse=0;
	boolean laufen =true;
	
	
	public float[] downloadFile(String url_str, OutputStream os) throws IOException
	{	
		InputStream is = null;
		
		System.setProperty("http.proxyPort", "443");
	    URL url = new URL(url_str.replace(" ", "%20"));	
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setUseCaches(false);
	    conn.setDefaultUseCaches(false);
	    conn.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
	    conn.setRequestProperty("Expires", "0");
	    conn.setRequestProperty("Pragma", "no-cache");
	    conn.setConnectTimeout(10000);
	    conn.setReadTimeout(10000);	    
	    conn.connect();	  
	   	    
		int responseCode = conn.getResponseCode() / 100;
		if (responseCode == 2) 
		{	
			groesse = conn.getContentLength();			
			is = conn.getInputStream();			
			
			long start = System.nanoTime();			
			long bytesRead = 0L;
	        byte[] buffer = new byte[65536];
	     
			try 
			{
	            int read = is.read(buffer);
	            while (read >= 1) 
	            {	            	
	              bytesRead += read;	         
	              os.write(buffer, 0, read);
	              read = is.read(buffer);
	            }
	        } 
			finally 
			{
	            if(is!=null) is.close();
	            if(os!=null) os.close();
	        }
			
			long downloadzeit = System.nanoTime() - start;
			rue[0] = (float) bytesRead;
			rue[1] = (float) downloadzeit;				
		} 		
		else 
		{
			throw new IllegalStateException("HTTP Response Code "+  conn.getResponseCode() + "\nby Source: "+url_str);
		}		
		conn.disconnect();
		return rue;
	}
	
	public boolean smartDownload(String url, File speicherort)
	{
		try
		{
			ReadableByteChannel rbc=Channels.newChannel(new URL(url).openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(speicherort);		
			fileOutputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fileOutputStream.close();
			rbc.close();
			return true;
		}
		catch (Exception e)
		{		
			return false;
		}
	}
	
	public boolean ident (String url_str, File copy) throws IOException //Wenn nicht gleich false zurück
	{	    
	    int groe = size(url_str);
	    int ist = (int) copy.length();
	    boolean identisch = true;
	    if(groe!=ist)
	    {
	    	identisch = false;
	    }
		return identisch;
	}
	
	public int groesse (File copy) throws IOException
	{	    	   
	    int ist = (int) copy.length();	  
		return ist;
	}
	
	public int size(String url_str) throws IOException
	{
	    URL url = new URL(url_str.replace(" ", "%20"));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setUseCaches(false);
	    conn.setDefaultUseCaches(false);
	    conn.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
	    conn.setRequestProperty("Expires", "0");
	    conn.setRequestProperty("Pragma", "no-cache");
	    conn.setConnectTimeout(10000);
	    conn.setReadTimeout(10000);
	    
	    int groe = conn.getContentLength();
	 
		conn.disconnect();
		return groe;
	}	
	
	public String post(String urls, String params) throws IOException
	{
		URL url = new URL(urls);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod( "POST" );
		connection.setDoInput( true );
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		connection.setDoOutput( true );
		connection.setUseCaches( false );
		connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty( "Content-Length", String.valueOf(params.length()));

		OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() );
		writer.write(params);
		writer.flush();

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()) );
		
		String erg = "";
		for ( String line; (line = reader.readLine()) != null; )
		{
		  erg+=line+"\n";
		}	
		writer.close();
		reader.close();	
		
		return erg;		
	}	

	public int getGroesse() 
	{
		return groesse;
	}		
}
