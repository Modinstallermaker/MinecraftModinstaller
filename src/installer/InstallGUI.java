package installer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
	JLabel uberschrift = new JLabel();
	JLabel info = new JLabel();
	JLabel iconf = new JLabel();
	JButton back = new JButton();
	JLabel beenden = new JLabel();
	JLabel start = new JLabel();
	JLabel banner = new JLabel();
	JLabel[] social = new JLabel[3];	
	JLabel stat = new JLabel();	
	JPanel cp;
	static JProgressBar bar = new JProgressBar();
	Cursor c = new Cursor(Cursor.HAND_CURSOR);	
	int breite = 600, hoehe=350, rand = 20;
	private String webplace = Start.webplace, stamm = Start.stamm;

	public void GUI()
	{		
		setUndecorated(true);			
		setSize(breite, hoehe);
		setTitle(Read.getTextwith("installer", "name"));		
		setLocationRelativeTo(null);	
		setResizable(false);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
		
		cp = new GraphicsPanel(false, "src/bild.png");
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.setLayout(null);
		add(cp);
		
		uberschrift.setBounds(0, 20, (int)(breite), (int)(hoehe*0.1));                              //Überschrift
		uberschrift.setText(Read.getTextwith("installer", "name"));
		uberschrift.setHorizontalAlignment(SwingConstants.CENTER);
		uberschrift.setVerticalAlignment(SwingConstants.CENTER);
		uberschrift.setFont(Start.lcd.deriveFont(Font.PLAIN,40));
		cp.add(uberschrift);
		
		banner.setBackground(null);
		banner.setForeground(null);
		banner.setIcon(new ImageIcon(this.getClass().getResource("src/banner_gross.png")));
		banner.setBounds(0, (int)(hoehe*0.12), (int)(breite), (int)(hoehe*0.3));
		banner.setCursor(c);
		banner.setHorizontalAlignment(SwingConstants.CENTER);
		banner.addMouseListener(this);
		cp.add(banner);
		
		iconf.setBackground(null);
		iconf.setForeground(null);
		iconf.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
		iconf.setBounds(15, 150, 80, 80);
		cp.add(iconf);		

		bar.setBounds(100, 180, 425, 33);
		cp.add(bar);
		
		stat.setBounds(105, 160, 425, 17);
		cp.add(stat);
		
		info.setBounds(0, 70, (int)(breite), (int)(hoehe*0.2));                             //Info
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setFont(Start.lcd.deriveFont(Font.PLAIN,30));
		cp.add(info);
		
		beenden.setBounds(rand, hoehe-40-rand, 150, 40); //Beenden	
		beenden.setText(Read.getTextwith("seite3", "text2"));		
		beenden.setIcon(new ImageIcon(this.getClass().getResource("src/power.png")));			
		beenden.setFont(beenden.getFont().deriveFont(Font.BOLD, 16));	
		beenden.addMouseListener(this);
		beenden.setCursor(c);		
		cp.add(beenden);
		
		start.setBounds((int)(breite-220-rand), hoehe-40-rand, 220, 40);
		start.setIcon(new ImageIcon(this.getClass().getResource("src/start.png")));		
		start.setText(Read.getTextwith("seite3", "text3"));
		start.setHorizontalTextPosition(SwingConstants.LEFT);
		start.setFont(start.getFont().deriveFont(Font.BOLD, 18));
		start.setHorizontalAlignment(SwingConstants.RIGHT);	
		start.setVerticalAlignment(SwingConstants.CENTER);
		start.addMouseListener(this);	
		start.setCursor(c);	
		start.setEnabled(false);
		cp.add(start);
		
		setVisible(true);
		
		String[] Bilder = {"src/facebook.png", "src/google.png", "src/mail.png"};
		int abstand = 30;
		int lange = 70;
		int anz = social.length;
		
		for (int i=0; i<anz; i++)
		{
			social[i] = new JLabel();
			social[i].setIcon(new ImageIcon(this.getClass().getResource(Bilder[i])));
			social[i].setBounds((breite-anz*(lange+abstand)+abstand)/2 + i*(lange+abstand), 155, lange, lange);
			social[i].setCursor(c);
			social[i].setVisible(false);
			social[i].addMouseListener(this);
			cp.add(social[i]);	
		}	
	}
	public void mouseClicked(MouseEvent e) 
	{
		Object s = e.getSource();
		if(s==social[0])
			new Browser("https://www.facebook.com/mcmodinstaller");
		else if(s==social[1])
			new Browser("https://plus.google.com/+MinecraftinstallerDeMod");
		else if(s==social[2])
			new Browser("http://www.minecraft-installer.de/faq.php");
		else if(s==banner)
			new Browser("http://server.nitrado.net/deu/gameserver-mieten?pk_campaign=MinecraftInstaller");
		else if(s==start)
			new MCLauncher(webplace, stamm);	
		else if(s==beenden)
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
