package installer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Postrequest 
{	
	private String answer="";
	
	public Postrequest(String urls, String params) throws IOException
	{
		OutputStreamWriter osw = null;		
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try
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
	
			osw = new OutputStreamWriter( connection.getOutputStream() );
			osw.write(params);
			osw.flush();
	
			isr = new InputStreamReader(connection.getInputStream());
			br = new BufferedReader(isr);
			
			answer = "";
			for ( String line; (line = br.readLine()) != null; )
			{
				answer+=line+"\n";
			}
		}
		finally
		{
			if(br != null) br.close();
			if(isr != null) isr.close();
			if(osw != null) osw.close();
		}
	}
	
	public String toString()
	{
		return answer;
	}
}
