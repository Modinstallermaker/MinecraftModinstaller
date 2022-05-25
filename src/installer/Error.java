package installer;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;


public class Error extends JFrame 
{
	  private static final long serialVersionUID = 1L;	
	  private JLabel head = new JLabel();
	  private JTextArea feld = new JTextArea("");
	  private JScrollPane feldScrollPane = new JScrollPane(feld);
	  private JButton Exit = new JButton();
	  private JButton Send = new JButton();
	  private JButton Forum = new JButton();
	  private JButton Copy = new JButton();
	  private String Version = Start.mcVersion;

	  private Cursor c = new Cursor(Cursor.HAND_CURSOR);
	  
	  public Error(String fehler) 
	  { 		
	    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    int frameWidth = 615; 
	    int frameHeight = 380;
	    setTitle(Read.getTextwith("installer", "name"));
	    setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
	    setSize(frameWidth, frameHeight);
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (d.width - getSize().width) / 2;
	    int y = (d.height - getSize().height) / 2;
	    setLocation(x, y);
	    setResizable(false);
	    
		JPanel cp = new JPanel();	
		cp.setLayout(null);
		add(cp);		
	
	    // Anfang Komponenten	    
	    head.setBounds(0, 0, 615, 55);
	    head.setText(Read.getTextwith("Error", "head"));
	    head.setHorizontalAlignment(SwingConstants.CENTER);
	    head.setFont(new Font("Calibri", Font.BOLD, 28));
	    cp.add(head);
	    
	    feldScrollPane.setBounds(15, 55, 585, 228);
	    feldScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    feld.setEditable(false);
	    feld.setLineWrap(true);
	    feld.setWrapStyleWord(true);
	    feld.setText(fehler);
	    feld.setCaretPosition(0);
	    cp.add(feldScrollPane);
	    
	    Exit.setBounds(480, 305, 120, 35);
	    Exit.setCursor(c);
	    Exit.setText(Read.getTextwith("Error", "exit"));
	    Exit.setMargin(new Insets(2, 2, 2, 2));
	    Exit.addActionListener(new ActionListener() { 
	      public void actionPerformed(ActionEvent evt) { 
	        Exit_ActionPerformed(evt);
	      }
	    });
	    cp.add(Exit);
	    Send.setBounds(285, 300, 120, 35);
	    Send.setText(Read.getTextwith("Error", "call"));
	    Send.setCursor(c);
	    Send.setMargin(new Insets(2, 2, 2, 2));
	    Send.addActionListener(new ActionListener() { 
	      public void actionPerformed(ActionEvent evt) { 
	        Send_ActionPerformed(evt);
	      }
	    });
	    cp.add(Send);
	    Forum.setBounds(15, 300, 120, 35);
	    Forum.setText(Read.getTextwith("Error", "forum"));
	    Forum.setCursor(c);
	    Forum.setMargin(new Insets(2, 2, 2, 2));
	    Forum.addActionListener(new ActionListener() { 
	      public void actionPerformed(ActionEvent evt) { 
	        Forum_ActionPerformed(evt);
	      }
	    });
	    cp.add(Forum);
	    Copy.setBounds(150, 300, 120, 35);
	    Copy.setText(Read.getTextwith("Error", "copy"));
	    Copy.setCursor(c);
	    Copy.setMargin(new Insets(2, 2, 2, 2));
	    Copy.addActionListener(new ActionListener() { 
	      public void actionPerformed(ActionEvent evt) { 
	        Copy_ActionPerformed(evt);
	      }
	    });
	    cp.add(Copy);
	    // Ende Komponenten
	    
	    setVisible(true);
	  }   

	  public void Exit_ActionPerformed(ActionEvent evt) 
	  {
	    dispose();
	  } 
	  
	  public void Send_ActionPerformed(ActionEvent evt) 
	  {	  
		try 
		{
			String EMail ="";
			int eingabe = JOptionPane.showConfirmDialog(null, Read.getTextwith("Error", "email1"), Read.getTextwith("Error", "email1h"), JOptionPane.YES_NO_OPTION);
			if(eingabe==0)
			{
				EMail = JOptionPane.showInputDialog(null,Read.getTextwith("Error", "email2"), Read.getTextwith("Error", "email2h"), JOptionPane.PLAIN_MESSAGE);
			}
			if(!EMail.contains("@")||!EMail.contains("."))
			{
				EMail="";
			}
				
			String body = "Text=" + URLEncoder.encode(feld.getText(), "UTF-8" ) + "&" + "MCVers=" + URLEncoder.encode(Version, "UTF-8" ) + "&" + "InstallerVers=" + URLEncoder.encode( Read.getTextwith("installer", "version"), "UTF-8" ) + "&" + "OP=" + URLEncoder.encode(System.getProperty("os.name").toString() + "; " + System.getProperty("os.version").toString() + "; " + System.getProperty("os.arch").toString(), "UTF-8" )+ "&" + "EMail=" + URLEncoder.encode( EMail, "UTF-8" );
	
			String erg = new Postrequest("https://www.minecraft-installer.de/api/errorreceiver.php", body).toString();
			
			JOptionPane.showMessageDialog(null, erg, "Server response...", JOptionPane.INFORMATION_MESSAGE);
			Send.setEnabled(false);
		} 
		catch (Exception e) 
		{		
			JOptionPane.showMessageDialog(null, e.toString());
		}			
	  } 
	  
	  public void Forum_ActionPerformed(ActionEvent evt) 
	  {
		  OperatingSystem.openLink(Read.getTextwith("installer", "website")+"faq.php");
	  } 
	  
	  public void Copy_ActionPerformed(ActionEvent evt) 
	  {
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(feld.getText()), null);
	  } 	  
}
