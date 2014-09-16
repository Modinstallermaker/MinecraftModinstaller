package installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


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
	private HttpURLConnection conn;
	private InputStream instr; 
	private int groesse=0;
	
	
	public float[] downloadFile(String url_str, OutputStream outstr)throws IllegalStateException, MalformedURLException,ProtocolException, IOException
	{		
		System.setProperty("http.proxyPort", "443");		
	    URL url = new URL(url_str.replace(" ", "%20"));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setUseCaches(false);
	    conn.setDefaultUseCaches(false);
	    conn.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
	    conn.setRequestProperty("Expires", "0");
	    conn.setRequestProperty("Pragma", "no-cache");
	    conn.setConnectTimeout(20000);
	    conn.setReadTimeout(20000);	    
	    conn.connect();	  
	   	    
		int responseCode = conn.getResponseCode() / 100;
		if (responseCode == 2) 
		{	
			groesse = conn.getContentLength();			
			instr = conn.getInputStream();			
			
			long start = System.nanoTime();			
			long bytesRead = 0L;
	        byte[] buffer = new byte[65536];
			try 
			{
	            int read = instr.read(buffer);
	            while (read >= 1) 
	            {
	              bytesRead += read;	         
	              outstr.write(buffer, 0, read);
	              read = instr.read(buffer);
	            }
	        } 
			finally 
			{
	            instr.close();
	            outstr.close();
	        }
			
			long downloadzeit = System.nanoTime() - start;
			rue[0] = (float) bytesRead;
			rue[1] = (float) downloadzeit;				
		} 		
		else 
		{
			throw new IllegalStateException("HTTP Response Code "+  conn.getResponseCode() + "\nSource: "+url_str+"\nErrorcode: DOx01");
		}		
		conn.disconnect();
		return rue;
	}
	
	public void smartDonwload(String URL, File target) throws IllegalStateException, MalformedURLException, ProtocolException, FileNotFoundException, IOException
	{
		
		new Download().downloadFile(URL, new FileOutputStream(target));
		
	}
	
	public boolean ident (String url_str, File copy) throws IllegalStateException, MalformedURLException,ProtocolException, IOException
	{	    
	    int groe = size(url_str);
	    int ist = (int) copy.length();
	    boolean identisch = true;
	    if(groe==ist)
	    {
	    	identisch = false;
	    }
		return identisch;
	}
	
	public int groesse (File copy) throws IllegalStateException, MalformedURLException,ProtocolException, IOException
	{	    	   
	    int ist = (int) copy.length();	  
		return ist;
	}
	
	public int size(String url_str) throws IllegalStateException, MalformedURLException,ProtocolException, IOException
	{
	    URL url = new URL(url_str.replace(" ", "%20"));
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setUseCaches(false);
	    conn.setDefaultUseCaches(false);
	    conn.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
	    conn.setRequestProperty("Expires", "0");
	    conn.setRequestProperty("Pragma", "no-cache");
	    conn.setConnectTimeout(20000);
	    conn.setReadTimeout(20000);
	    
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
		connection.setConnectTimeout(20000);
		connection.setReadTimeout(20000);
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
	
	public void exit()
	{
		conn.disconnect();
		try 
		{
			instr.close();
		} 
		catch (IOException e) 
		{			
			e.printStackTrace();
		}		
	}

	public int getGroesse() 
	{
		return groesse;
	}		
}
