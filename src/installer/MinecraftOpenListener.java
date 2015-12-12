package installer;

import static installer.OP.optionReader;
import static installer.OP.optionWriter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class MinecraftOpenListener implements ActionListener
{
	boolean askIfMCclosed = false;
	boolean open = false;
	long lastChangeTimeDiff =-1;
	boolean run = true;
	boolean closedDialoge =true;
	
	JButton yesb = new JButton(Read.getTextwith("seite1", "compj"));
	JButton db = new JButton(Read.getTextwith("seite1", "compk"));
	JButton nob = new JButton(Read.getTextwith("seite1", "compn"));
	JButton errorb = new JButton(Read.getTextwith("seite1", "compe"));
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);		
	int sel=-1;
	
	public MinecraftOpenListener()
	{
		new Thread()
		{
			public void run()
			{				
				boolean change = false;
				long logfileDateOLD=0;
				while(run) //Minecraft Watchdog
				{					
					File logfile = new File(Start.mineord+"logs/latest.log");
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(logfile.lastModified());
					Date logfileDate = cal.getTime(); //Letzer Eintrag
					Date now = new Date();
					lastChangeTimeDiff = now.getTime() - logfileDate.getTime(); //Zeitdifferenz in Millisekunden
				
					if(logfile.exists()&&logfileDate.getTime()!=logfileDateOLD)
					{
						try
						{
							String content = OP.Textreaders(logfile);
							if(!content.contains("Stopping!")&& !content.contains("FATAL"))	
								open=true;
							else
								open=false;
							if(content.contains("FATAL"))
							{
								//TODO: Absturzbericht analyisieren
								//JOptionPane.showMessageDialog(null, "Dürften wir Deinen Minecraft Absturzbericht analysieren?");
							}
						}
						catch (Exception e)
						{
							open=true;
							e.getStackTrace();
						}
						change=true;
					}
					
					if(change)
					{
						if(open)
						{	
							if(!askIfMCclosed)
							{
								new Thread()
								{
									public void run()
									{
										if(closedDialoge)
										{
											closedDialoge = false;
											//TODO: Close Minecraft
											/*
											Runtime rt = Runtime.getRuntime();
											try {
												rt.exec("taskkill /F /IM javaw.exe");
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											*/											
											JOptionPane.showMessageDialog(null, Read.getTextwith("installer", "exitmc"));
											closedDialoge = true;
										}
									}
								}.start();	
							}
						}
						else
						{
							if(askIfMCclosed)
								buildAskFrame();														
						}
						change=false;
						logfileDateOLD=logfileDate.getTime();
					}
					try 
					{
						Thread.sleep(1000);
					} 
					catch (InterruptedException e) 
					{						
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void buildAskFrame()
	{
		run =false;
		if(!new File(Start.stamm + "Modinstaller/Importo/").exists()&&
				!optionReader("lastmods").equals(optionReader("slastmods"))&&
				optionReader("changed").equals("true"))
		{
			optionWriter("changed", "false");			
		   
	        JFrame frame = new JFrame("Minecraft Modinstaller - Mods ok?");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        addComponentsToPane(frame.getContentPane());
	        frame.pack();
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);	
		}
		else
			System.exit(0);
	}
	
	public void addComponentsToPane(Container pane) {  
        pane.setLayout(new BorderLayout());
         
        JLabel question = new JLabel(Read.getTextwith("seite1", "compt"));
        question.setHorizontalAlignment(SwingConstants.CENTER);
        question.setFont(new Font("Dialog", Font.BOLD, 14));
        question.setCursor(c);
        question.setPreferredSize(new Dimension(600, 80));
        pane.add(question, BorderLayout.PAGE_START);
      
        yesb.addActionListener(this);
        yesb.setCursor(c);
        yesb.setPreferredSize(new Dimension(200, 100));
        pane.add( yesb, BorderLayout.LINE_START);
                
        db.addActionListener(this);
        db.setCursor(c);
        db.setPreferredSize(new Dimension(200, 100));
        pane.add(db, BorderLayout.CENTER);  
      
        nob.addActionListener(this);
        nob.setCursor(c);
        nob.setPreferredSize(new Dimension(200, 100));
        pane.add(nob, BorderLayout.LINE_END);
        
        errorb.addActionListener(this);
        errorb.setCursor(c);
        errorb.setPreferredSize(new Dimension(600, 60));
        pane.add(errorb, BorderLayout.PAGE_END);         
    }
	
	private void send()
	{
		String body = "Mods=" + optionReader("lastmods") + "&" + "Rate=" + sel;		        	
		try {
			new Download().post("http://www.minecraft-installer.de/api/compSet.php", body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public void askUser(boolean var)
	{
		askIfMCclosed = var;
	}
	public boolean isMinecraftOpen()
	{
		return open;
	}
	public long getLastChangeTimeDiff()
	{
		return lastChangeTimeDiff;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if(s==yesb){
			sel=3;
			send();
		}
		else if(s==db){
			sel=2;
			send();
		}
		else if(s==nob){
			sel=1;
			send();
		}
		else if(s==errorb)
		{
			sel=0;
			send();
		}
	}
}
