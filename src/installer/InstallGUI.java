package installer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
	public JLabel stat = new JLabel();
	public JLabel startinfo = new JLabel();	
	public static JProgressBar bar = new JProgressBar();
	
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
		
		headlineLabel.setBounds(0, 20, (int)(breite), (int)(hoehe*0.1));                              //Überschrift
		headlineLabel.setText(Read.getTextwith("installer", "name"));
		headlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
		headlineLabel.setVerticalAlignment(SwingConstants.CENTER);
		headlineLabel.setFont(headlineLabel.getFont().deriveFont(Font.BOLD,45));
		cp.add(headlineLabel);
		
		banner.setBackground(null);
		banner.setForeground(null);
		banner.setIcon(new ImageIcon(this.getClass().getResource("src/banner_gross.png")));
		banner.setBounds(0, (int)(hoehe*0.12), (int)(breite), (int)(hoehe*0.3));
		banner.setCursor(c);
		banner.setHorizontalAlignment(SwingConstants.CENTER);
		banner.addMouseListener(this);
		cp.add(banner);
		
		stateIcon.setBackground(null);
		stateIcon.setForeground(null);
		stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
		stateIcon.setBounds(15, 150, 80, 80);
		cp.add(stateIcon);		

		bar.setBounds(100, 180, 425, 33);
		cp.add(bar);
		
		stat.setBounds(105, 160, 425, 17);
		cp.add(stat);
		
		info.setBounds(0, 70, (int)(breite), (int)(hoehe*0.2));                             //Info
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setFont(exitButton.getFont().deriveFont(Font.PLAIN, 30));
		cp.add(info);
		
		startinfo.setBounds(0, (int)(hoehe*0.31), (int)(breite), (int)(hoehe*0.2));                             //Info
		startinfo.setHorizontalAlignment(SwingConstants.CENTER);
		startinfo.setText(Read.getTextwith("seite3", "startinfo")+Start.mcVersion+")");
		startinfo.setFont(exitButton.getFont().deriveFont(Font.PLAIN, 16));
		startinfo.setVisible(false);
		cp.add(startinfo);
		
		exitButton.setBounds(rand, hoehe-40-rand, 150, 40); //exitButton	
		exitButton.setText(Read.getTextwith("seite3", "text2"));		
		exitButton.setIcon(new ImageIcon(this.getClass().getResource("src/power.png")));			
		exitButton.setFont(exitButton.getFont().deriveFont(Font.BOLD, 16));	
		exitButton.addMouseListener(this);
		exitButton.setCursor(c);		
		cp.add(exitButton);
		
		startMCButton.setBounds((int)(breite-220-rand), hoehe-40-rand, 220, 40);
		startMCButton.setIcon(new ImageIcon(this.getClass().getResource("src/start.png")));		
		startMCButton.setText(Read.getTextwith("seite3", "text3"));
		startMCButton.setHorizontalTextPosition(SwingConstants.LEFT);
		startMCButton.setFont(startMCButton.getFont().deriveFont(Font.BOLD, 18));
		startMCButton.setHorizontalAlignment(SwingConstants.RIGHT);	
		startMCButton.setVerticalAlignment(SwingConstants.CENTER);
		startMCButton.addMouseListener(this);	
		startMCButton.setCursor(c);	
		startMCButton.setEnabled(false);
		cp.add(startMCButton);
		
		setVisible(true);
		
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
			new Browser(Read.getTextwith("seite3", "facebook"));
		else if(s==socialIcons[1])
			new Browser("https://plus.google.com/+MinecraftinstallerDeMod");
		else if(s==socialIcons[2])
			new Browser("https://twitter.com/Modinstaller");
		else if(s==socialIcons[3])
			new Browser(Read.getTextwith("installer", "website")+"/proposal.php");
		else if(s==socialIcons[4])
			new Browser(Read.getTextwith("installer", "website")+"/faq.php");
		else if(s==banner)
			new Browser("http://server.nitrado.net/deu/gameserver-mieten?pk_campaign=MinecraftInstaller");
		else if(s==startMCButton)
			new MCLauncher();	
		else if(s==exitButton)
			System.exit(0);
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
