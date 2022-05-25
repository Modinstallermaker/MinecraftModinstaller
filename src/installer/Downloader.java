package installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 
 * Beschreibung
 * 
 * @version 5.0
 * @author Dirk Lippke
 */

public class Downloader implements Runnable 
{
	private String url_str;
	private File targetFile;
	private int expectedDownloadSize=-1;
	private long elapsed=-1;
	private float elapsedSeconds =-1;
	private float kbRead =-1;
	boolean work = false;
		
	public Downloader(String url_str, File targetFile)
	{
		this.url_str = url_str;
		this.targetFile = targetFile;
	}
	
	public void run()
	{	
		do
		{	
			try
			{	
				
			  TrustManager[] trustAllCerts = new TrustManager[] {
				       new X509TrustManager() {
				          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				            return null;
				          }

				          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

				          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

				       }
				    };

			    SSLContext sc = SSLContext.getInstance("SSL");
			    sc.init(null, trustAllCerts, new java.security.SecureRandom());
			    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			    // Create all-trusting host name verifier
			    HostnameVerifier allHostsValid = new HostnameVerifier() {
			        public boolean verify(String hostname, SSLSession session) {
			          return true;
			        }
			    };
			    // Install the all-trusting host verifier
			    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			    /*
			     * end of the fix
			     */

			    URL url = new URL(url_str.replace(" ", "%20"));
			    URLConnection conn = url.openConnection();
			    conn.setUseCaches(false);
			    conn.setDefaultUseCaches(false);
			    conn.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
			    conn.setRequestProperty("Expires", "0");
			    conn.setRequestProperty("Pragma", "no-cache");
			    conn.setConnectTimeout(10000);
			    conn.setReadTimeout(10000);
			    expectedDownloadSize = conn.getContentLength();
			    long start = System.nanoTime();
			    conn.connect();		   
			    elapsed = System.nanoTime() - start;
			    
				int responseCode = ((HttpURLConnection) conn).getResponseCode() / 100;
				if (responseCode == 2||responseCode == 3) 
				{	
					InputStream is = conn.getInputStream();
					
					targetFile.getParentFile().mkdirs();
					if(!targetFile.exists())
						targetFile.createNewFile();
					FileOutputStream os = new FileOutputStream(targetFile);
					
					long startDownload = System.nanoTime();
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
					long elapsedDownload = System.nanoTime() - startDownload;
				
					elapsedSeconds = (float)(1L + elapsedDownload) / 1.0E+009F;
			        kbRead = (float)bytesRead / 1024.0F;
				}
				else if (responseCode == 4)
		        {
					throw new IllegalStateException("Remote file not found: " + url_str);
		        }
				else 
				{
					if(!work)
					{
						System.out.println("Try downloading "+url_str+ " again.");
						work = true;
					}
					else
						throw new IllegalStateException("HTTP Response Code " + ((HttpURLConnection) conn).getResponseCode());
				}		
				((HttpURLConnection) conn).disconnect();
				if(expectedDownloadSize == targetFile.length() || expectedDownloadSize==-1)
					work = false;
				else
				{
					System.out.println("Size different "+url_str+ ": " +expectedDownloadSize +" vs "+targetFile.length());
				}
			    
			}
			catch (IOException | NoSuchAlgorithmException | KeyManagementException ioe)
			{
				if(!work)
				{
					System.out.println("Try downloading "+url_str+ " again.");
					work = true;
				}
				else
					throw new IllegalStateException(ioe);
			}
		}
		while (work);
	}	
	
	
	public int getExpectedDownloadSize()
	{
		return expectedDownloadSize;
	}
	
	public boolean isDownloadSizeEqual() throws IOException //Wenn nicht gleich false zurück
	{	    
	    int groe = getDownloadSize();
	    int ist = (int) targetFile.length();
	    boolean identisch = true;
	    if(groe!=ist)
	    {
	    	identisch = false;
	    }
		return identisch;
	}
	
	public long getElapsed() {
		return elapsed;
	}
	
	public float getKbRead() {
		return kbRead;
	}

	public float getElapsedSeconds() {
		return elapsedSeconds;
	}	

	public int getDownloadSize() throws IOException
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
	    
	    expectedDownloadSize = conn.getContentLength();
	 
		conn.disconnect();
		return expectedDownloadSize;
	}
	
	public File getTargetFile()
	{
		return targetFile;
	}
	
}
