package installer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class InstallGUI extends JFrame implements MouseListener {
	/**
	 * Install GUI JFrame
	 */
	private static final long serialVersionUID = 1L;	
	public JLabel info = new JLabel();
	public JLabel stateIcon = new JLabel();	
	public JLabel startMCButton = new JLabel();
	public JLabel banner = new JLabel();
	public JLabel[] socialIcons;	
	public JLabel detBarInf = new JLabel(), mainBarInf = new JLabel();
	public JLabel startinfo = new JLabel();	
	public static JProgressBar detBar = new JProgressBar();
	public static JProgressBar mainBar = new JProgressBar();
	
	private JLabel headlineLabel = new JLabel();
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);		
	private int width = 784, height=441, rand = 20;
	private JLabel backButton = new JLabel();
	private JLabel exitButton = new JLabel();
	private JLabel maxButton = new JLabel();
	private JLabel minButton = new JLabel();
	public boolean installcomplete = false;

	public void GUI()
	{		
		setUndecorated(true);			
		setSize(width, height);
		setTitle(Read.getTextwith("installer", "name"));		
		setLocationRelativeTo(null);	
		setResizable(false);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
		
		//Mouse postion changer
		final Point point = new Point(0,0); 
		addMouseListener(new MouseAdapter() 
		{  			
			public void mousePressed(MouseEvent e) 
			{  
				 if(!e.isMetaDown())
				 {  					
					 point.x = e.getX();  
					 point.y = e.getY();  
					 setCursor(new Cursor(Cursor.MOVE_CURSOR));
				 } 
			} 			
			public void mouseReleased(MouseEvent e) 
			{		
				 setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});  
		addMouseMotionListener(new MouseMotionAdapter() 
		{  
			 public void mouseDragged(MouseEvent e) 
			 {  				 
				 if(!e.isMetaDown())
				 {  
					 
					 Point p = getLocation();  
					 setLocation(p.x + e.getX() - point.x,  
					 p.y + e.getY() - point.y); 					
				 } 				
			 } 			
		});
		
		JPanel cp = new GraphicsPanel(false);
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.setLayout(null);
		add(cp);
		
		//Button: Minimize
		minButton.setBounds(width-(35+35+3+3)-3, 3, 35, 27);	
		minButton.setIcon(new ImageIcon(this.getClass().getResource("src/mini.png")));			
		minButton.addMouseListener(this);
		cp.add(minButton);
		
		//Button: Maximize
		maxButton.setBounds(width-(35+35+3)-3, 3, 35, 27);
		maxButton.setIcon(new ImageIcon(this.getClass().getResource("src/maxi.png")));			
		maxButton.addMouseListener(this);
		maxButton.setEnabled(false);
		//cp.add(maxButton);
		
		//Button: Exit
		exitButton.setBounds(width-35-3, 3, 35, 27);	
		exitButton.setIcon(new ImageIcon(this.getClass().getResource("src/closeme.png")));			
		exitButton.addMouseListener(this);
		cp.add(exitButton);
		
		//Label: Minecraft Modinstaller
		headlineLabel.setBounds(0, 30, (int)(width), 35);
		headlineLabel.setText(Read.getTextwith("installer", "name"));
		headlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
		headlineLabel.setVerticalAlignment(SwingConstants.CENTER);
		headlineLabel.setFont(headlineLabel.getFont().deriveFont(Font.BOLD,45));
		cp.add(headlineLabel);
		
		//Image: Advertisment
		if(Start.online)
		{
			try 
			{
				banner.setIcon(new ImageIcon(ImageIO.read(
						new URL("http://minecraft-installer.de/api/getAds.php?channel=installlarge&type=img"))));
				banner.setBounds(0, 65, (int)(width), 105);
				banner.setVerticalAlignment(SwingConstants.CENTER);
				banner.setCursor(c);
				banner.setHorizontalAlignment(SwingConstants.CENTER);
				banner.addMouseListener(this);
				cp.add(banner);
			}
			catch (Exception e) 
			{			
				e.printStackTrace();
			}
		}
		
		//Image: Installation Symbol
		stateIcon.setBackground(null);
		stateIcon.setForeground(null);
		stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
		stateIcon.setBounds(rand, 225, 80, 80);
		cp.add(stateIcon);			
		
		int start = 110;
		
		//Label: Main Bar
		mainBarInf.setBounds(start+5, 200, width-start-5-rand*2, 20);   
		mainBarInf.setFont(mainBarInf.getFont().deriveFont(Font.PLAIN, 15));
		cp.add(mainBarInf);
		
		//Bar: Main Bar
		mainBar.setBounds(start, 223, width-start-rand*2, 30);
		cp.add(mainBar);
			
		//Label: Detailed Bar
		detBarInf.setBounds(start+5, 270, width-start-5-rand*2, 20); //Detailed Bar
		detBarInf.setFont(mainBarInf.getFont().deriveFont(Font.PLAIN, 15));
		cp.add(detBarInf);	
		
		//Bar: Detailed Bar
		detBar.setBounds(start, 293, width-start-rand*2, 30);
		cp.add(detBar);
		
		//Label: Installation complete
		info.setBounds(0, (int)(height*0.26), (int)(width), (int)(height*0.35));          
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setFont(exitButton.getFont().deriveFont(Font.PLAIN, 30));
		cp.add(info);
		
		//Label: Information for user to start Minecraft Version Modinstaller
		startinfo.setBounds(0, (int)(height*0.35), (int)(width), (int)(height*0.4));                 
		startinfo.setHorizontalAlignment(SwingConstants.CENTER);
		startinfo.setFont(startinfo.getFont().deriveFont(Font.PLAIN, 18));
		startinfo.setText(Read.getTextwith("InstallGUI", "t1")+Start.mcVersion+")");
		startinfo.setVisible(false);
		cp.add(startinfo);
		
		//Button: Back to Menu
		backButton.setBounds(rand, height-40-rand, 200, 40);	
		backButton.setText(Read.getTextwith("InstallGUI", "t2"));
		backButton.setIcon(new ImageIcon(this.getClass().getResource("src/back.png")));		
		backButton.setFont(startMCButton.getFont().deriveFont(Font.BOLD, 18));	
		backButton.addMouseListener(this);
		backButton.setCursor(c);		
		cp.add(backButton);
		
		//Button: Start Minecraft
		startMCButton.setBounds((int)(width-220-rand), height-40-rand, 220, 40);
		startMCButton.setIcon(new ImageIcon(this.getClass().getResource("src/start.png")));		
		startMCButton.setText(Read.getTextwith("InstallGUI", "t3"));
		startMCButton.setHorizontalTextPosition(SwingConstants.LEFT);
		startMCButton.setFont(startMCButton.getFont().deriveFont(Font.BOLD, 18));
		startMCButton.setHorizontalAlignment(SwingConstants.RIGHT);	
		startMCButton.setVerticalAlignment(SwingConstants.CENTER);
		startMCButton.addMouseListener(this);	
		startMCButton.setCursor(c);	
		startMCButton.setEnabled(false);
		cp.add(startMCButton);
		
		setVisible(true);		
		
		//Picture Bar
		String[] Bilder = {"src/facebook.png", "src/google.png", "src/twitter.png", "src/proposal.png", "src/support.png"};
		int abstand = 20;
		int lange = 70;
		socialIcons = new JLabel[Bilder.length];
		int anz = Bilder.length;
		
		for (int i=0; i<anz; i++)
		{
			socialIcons[i] = new JLabel();
			socialIcons[i].setIcon(new ImageIcon(this.getClass().getResource(Bilder[i])));
			socialIcons[i].setBounds((width-anz*(lange+abstand)+abstand)/2 + i*(lange+abstand), (int)(height*0.62), lange, lange);
			socialIcons[i].setCursor(c);
			socialIcons[i].setVisible(false);
			socialIcons[i].addMouseListener(this);
			cp.add(socialIcons[i]);
		}
	}
	
	public void mouseClicked(MouseEvent e) 
	{
		Object s = e.getSource();
		if(s==socialIcons[0])
			OperatingSystem.openLink(Read.getTextwith("InstallGUI", "facebook"));
		else if(s==socialIcons[1])
			OperatingSystem.openLink("https://plus.google.com/+MinecraftinstallerDeMod");
		else if(s==socialIcons[2])
			OperatingSystem.openLink("https://twitter.com/Modinstaller");
		else if(s==socialIcons[3])
			OperatingSystem.openLink(Read.getTextwith("installer", "website")+"/proposal.php");
		else if(s==socialIcons[4])
			OperatingSystem.openLink(Read.getTextwith("installer", "website")+"/faq.php");
		else if(s==banner)
		{
			OperatingSystem.openLink("http://minecraft-installer.de/api/getAds.php?channel=installlarge&type=url");
		}
		else if(s==startMCButton)
		{
			new MCLauncher();	
			dispose();
		}
		else if(s==exitButton)
		{     
			if(!installcomplete)
			{
	            int reply = JOptionPane.showConfirmDialog(null, Read.getTextwith("InstallGUI", "t4"), Read.getTextwith("InstallGUI", "t4h"), 
	            		JOptionPane.YES_NO_OPTION);
	            if (reply == JOptionPane.YES_OPTION) 
	            	 System.exit(0);
			}
			else
			{
				System.exit(0);
			}
		}
		else if(s==minButton)
		{
			setState(ICONIFIED);
		}
		else if(s==backButton)
		{
			boolean back = false;
			if(!installcomplete)
			{
				int reply = JOptionPane.showConfirmDialog(null, Read.getTextwith("InstallGUI", "t5"), Read.getTextwith("InstallGUI", "t5h"), 
						JOptionPane.YES_NO_OPTION);
	            if (reply == JOptionPane.YES_OPTION)
	            	back = true;
			}
			else
			{
				back = true;
			}
			if(back)
			{
				new Menu();
				dispose();
			}
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}
