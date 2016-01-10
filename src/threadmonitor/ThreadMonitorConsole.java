package threadmonitor;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class ThreadMonitorConsole
{

public static void main(String[] args)
{
	try
	{	
	
		// connecting to JMX
		System.out.println("Connect to JMX service.");
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

		// Construct proxy for the the MBean object
		ObjectName mbeanName = new ObjectName("com.example:type=ThreadMonitor");
		ThreadMonitorMBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, ThreadMonitorMBean.class, true);

		System.out.println("Connected to: "+mbeanProxy.getName()+", the app is "+(mbeanProxy.isRunning() ? "" : "not ")+"running");

		// parse command line arguments
		if(args[0].equalsIgnoreCase("start"))
		{
			System.out.println("Invoke \"start\" method");
			mbeanProxy.start();
		}
		else if(args[0].equalsIgnoreCase("stop"))
		{
			System.out.println("Invoke \"stop\" method");
			mbeanProxy.stop();
		}

		// clean up and exit
		jmxc.close();
		System.out.println("Done.");	
	}
	catch(Exception e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
