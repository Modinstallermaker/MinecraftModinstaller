package installer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class InstallGUI extends JFrame implements MouseListener {
	/**
	 * 
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
	private int breite = 600, hoehe=350, rand = 20;
	private JLabel exitButton = new JLabel();

	public void GUI()
	{		
		setUndecorated(true);			
		setSize(breite, hoehe);
		setTitle(Read.getTextwith("installer", "name"));		
		setLocationRelativeTo(null);	
		setResizable(false);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
		
		JPanel cp = new GraphicsPanel(false);
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.setLayout(null);
		add(cp);
		
		headlineLabel.setBounds(0, 20, (int)(breite), 35);                              //Überschrift
		headlineLabel.setText(Read.getTextwith("installer", "name"));
		headlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
		headlineLabel.setVerticalAlignment(SwingConstants.CENTER);
		headlineLabel.setFont(headlineLabel.getFont().deriveFont(Font.BOLD,45));
		cp.add(headlineLabel);		
		
		stateIcon.setBackground(null);
		stateIcon.setForeground(null);
		stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
		stateIcon.setBounds(15, 160, 80, 80);
		cp.add(stateIcon);		

		info.setBounds(0, 70, (int)(breite), (int)(hoehe*0.2));                             //Info
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setFont(exitButton.getFont().deriveFont(Font.PLAIN, 30));
		cp.add(info);
		
		mainBarInf.setBounds(105, 160, 420, 20);        
		cp.add(mainBarInf);
		mainBar.setBounds(100, 180, 425, 20);
		cp.add(mainBar);
				
		detBarInf.setBounds(105, 200, 420, 20);
		cp.add(detBarInf);		
		detBar.setBounds(100, 220, 425, 20);
		cp.add(detBar);
		
		//Information for user to start Minecraft Version Modinstaller
		startinfo.setBounds(0, (int)(hoehe*0.31), (int)(breite), (int)(hoehe*0.2));                             //Info
		startinfo.setHorizontalAlignment(SwingConstants.CENTER);
		startinfo.setText(Read.getTextwith("InstallGUI", "t1")+Start.mcVersion+")");
		startinfo.setFont(exitButton.getFont().deriveFont(Font.PLAIN, 16));
		startinfo.setVisible(false);
		cp.add(startinfo);
		
		 //Exit Button
		exitButton.setBounds(rand, hoehe-40-rand, 150, 40);	
		exitButton.setText(Read.getTextwith("InstallGUI", "t2"));		
		exitButton.setFont(startMCButton.getFont().deriveFont(Font.BOLD, 18));
		exitButton.setIcon(new ImageIcon(this.getClass().getResource("src/power.png")));	
		exitButton.addMouseListener(this);
		exitButton.setCursor(c);		
		cp.add(exitButton);
		
		//Start Minecraft Button
		startMCButton.setBounds((int)(breite-220-rand), hoehe-40-rand, 220, 40);
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
		
		try 
		{
			banner.setIcon(new ImageIcon(ImageIO.read(new URL("http://minecraft-installer.de/api/getAds.php?channel=installlarge&type=img"))));
			
			banner.setBounds(0, 55, (int)(breite), 105);
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
		
		String[] Bilder = {"src/facebook.png", "src/google.png", "src/twitter.png", "src/proposal.png", "src/support.png"};
		int abstand = 20;
		int lange = 70;
		socialIcons = new JLabel[Bilder.length];
		int anz = Bilder.length;
		
		for (int i=0; i<anz; i++)
		{
			socialIcons[i] = new JLabel();
			socialIcons[i].setIcon(new ImageIcon(this.getClass().getResource(Bilder[i])));
			socialIcons[i].setBounds((breite-anz*(lange+abstand)+abstand)/2 + i*(lange+abstand), (int)(hoehe*0.52), lange, lange);
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
			OperatingSystem.openLink("http://minecraft-installer.de/api/getAds.php?channel=installlarge&type=url");
		else if(s==startMCButton)
		{
			new MCLauncher();	
			dispose();
		}
		else if(s==exitButton)
			dispose();
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
