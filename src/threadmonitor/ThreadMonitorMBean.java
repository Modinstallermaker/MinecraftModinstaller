package threadmonitor;

public interface ThreadMonitorMBean
{
	String getName();
	void start();
	void stop();
	boolean isRunning();
}

