package threadmonitor;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ThreadMonitor implements ThreadMonitorMBean
{
private Thread m_thrd = null;

public ThreadMonitor(Thread thrd)
{
	m_thrd = thrd;
}

@Override
public String getName()
{
	return "JMX Controlled App";
}

@Override
public void start()
{
	// TODO: start application here
	System.out.println("remote start called");
}

@Override
public void stop()
{
	// TODO: stop application here
	System.out.println("remote stop called");

	m_thrd.interrupt();
}

public boolean isRunning()
{
	return Thread.currentThread().isAlive();
}

public static void main(String[] args)
{
	try
	{
		System.out.println("JMX started");

		ThreadMonitorMBean monitor = new ThreadMonitor(Thread.currentThread());

		MBeanServer server = ManagementFactory.getPlatformMBeanServer();

		ObjectName name = new ObjectName("com.example:type=ThreadMonitor");

		server.registerMBean(monitor, name);

		while(!Thread.interrupted())
		{
			// loop until interrupted
			System.out.println(".");
            try 
            {
                Thread.sleep(1000);
            } 
            catch(InterruptedException ex) 
            {
                Thread.currentThread().interrupt();
            }
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally
	{
		// TODO: some final clean up could be here also
		System.out.println("JMX stopped");
	}
}
}
