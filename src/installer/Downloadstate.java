package installer;

public class Downloadstate implements Runnable 
{
	private Downloader dow;
	private double max=100.0, start = 0.0;

	public Downloadstate(Downloader dow) 
	{
		this.dow = dow;
	}
	

	public Downloadstate(Downloader dow, double max, double start) 
	{
		this.dow = dow;
		this.max = max;
		this.start = start;
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
						double proz = Math.round(((double) sizeNow / (double) sizeExpected) * max * 10.) / 10.;
						Install.detState(start + proz);
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