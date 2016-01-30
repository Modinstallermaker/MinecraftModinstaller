package installer;

public class Downloadstate implements Runnable 
{
	private Downloader dow;

	public Downloadstate(Downloader dow) 
	{
		this.dow = dow;
	}

	public void run() 
	{
		while (!Thread.currentThread().isInterrupted()) 
		{
			try 
			{
				int sizeNow = (int) dow.getTargetFile().length();
				if (sizeNow > 1) 
				{
					int sizeExpected = dow.getExpectedDownloadSize();
					if (sizeExpected > 1) {
						double proz = Math.round(((double) sizeNow / (double) sizeExpected) * 1000.) / 10.;
						Install.detState(proz);
					}
				}

				Thread.sleep(50);
			} 
			catch (Exception e) 
			{
				Thread.currentThread().interrupt();
			}
		}
	}
}