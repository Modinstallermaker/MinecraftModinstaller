package installer;

import static installer.OP.Textreader;
import static installer.OP.Textreaders;
import static installer.OP.del;
import static installer.OP.getError;
import static installer.OP.makedirs;
import static installer.OP.optionReader;
import static installer.OP.optionWriter;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * Modinstaller start window
 * 
 * @version 5.0
 * @author Dirk Lippke
 */

public class Start extends JFrame
{
	private static final long serialVersionUID = 6893761562923644768L;
	private JLabel modinstallerVersionLabel = new JLabel();
	private JLabel prog = new JLabel();	
	private JLabel logo = new JLabel();	
	private JPanel cp;		
	private String versionExtension;	
	private int tryno = 0;	
	private int heightFrame =300, widthFrame=500;
	
	public static ArrayList<String> offlineList = new ArrayList<String>();
	public static Modinfo[] modtexts = null, moddownloads = null;	
	public static String mcVersion="", webplace, lang ="en", modinstallerVersion="";
	public static File mineord, sport;
	public static ArrayList<String> sentImportedModInfo = new ArrayList<String>();
	public static String[] mcVersionen;	
	public static MCVersion[] allMCVersions, forgeMCVersions;
	public static boolean online = false;
	
	/**
	 * Setting up design of Minecraft Modinstaller
	 */	
	public static void main(String[] args) 
	{
		System.setProperty("java.net.preferIPv4Stack" , "true");
		try 
	    {	
			Color red = Color.decode("#9C2717");
			Color white = Color.decode("#FFFfff");			
			Color darkwhite = Color.decode("#eFe9d9");
			
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");			 
			UIManager.put("nimbusBase", white);
			UIManager.put("text", red);		    
			UIManager.put("info", white);	
			UIManager.put("nimbusSelectionBackground", red);
			UIManager.put("nimbusSelectedText", white);
			UIManager.put("nimbusFocus", darkwhite);
			UIManager.put("nimbusLightBackground", white);			
			UIManager.put("control", white);
			UIManager.getLookAndFeelDefaults().put("List[Selected].textBackground", red);
			UIManager.getLookAndFeelDefaults().put("List[Selected].textForeground", white);
	    } 
	    catch (Exception e) 
	    {
	      try
	      {
	    	  UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
	      }
	      catch (Exception e2) {}
	    }
		new Start();			
	}	
	
	/**
	 * Design JFrame for startup screen
	 */
	public Start()
	{	
		getMCDir();		
		
		if(!optionReader("language").equals("n/a"))
			lang = optionReader("language");
		else
		{
			if(System.getProperty("user.language").startsWith("de")) //Set up language
				lang="de";
			else
				lang="en";
		}
		
		modinstallerVersion = Read.getTextwith("installer", "version");
		versionExtension = Read.getTextwith("installer", "zusatz");
		webplace = Read.getTextwith("installer", "webplace");		
		
		if(!optionReader("modinstaller").equals(modinstallerVersion))
		{
			del(sport);
			del(new File(System.getProperty("user.home") + "/Desktop/MC Modinstaller 4.1.lnk"));
			del(new File(System.getProperty("user.home") + "/Microsoft/Windows/Start Menu/Programs/MC Modinstaller 4.1.lnk"));	
			del(new File(System.getProperty("user.home") + "/Desktop/MC Modinstaller 4.2.lnk"));
			del(new File(System.getProperty("user.home") + "/Microsoft/Windows/Start Menu/Programs/MC Modinstaller 4.2.lnk"));	
			del(new File(System.getProperty("user.home") + "/Desktop/MC Modinstaller 4.3.lnk"));
			del(new File(System.getProperty("user.home") + "/Microsoft/Windows/Start Menu/Programs/MC Modinstaller 4.3.lnk"));	
			del(new File(System.getProperty("user.home") + "/Desktop/MC Modinstaller 4.4.lnk"));
			del(new File(System.getProperty("user.home") + "/Microsoft/Windows/Start Menu/Programs/MC Modinstaller 4.4.lnk"));
			del(new File(System.getProperty("user.home") + "/Desktop/MC Modinstaller 4.5.lnk"));
			del(new File(System.getProperty("user.home") + "/Microsoft/Windows/Start Menu/Programs/MC Modinstaller 4.5.lnk"));
		}
		
		makedirs(sport);	
		
		optionWriter("language", lang);
		optionWriter("modinstaller", modinstallerVersion);
				
		setSize(widthFrame, heightFrame);
		setUndecorated(true);
		setTitle(Read.getTextwith("installer", "name"));
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
				
		cp = new JPanel();
		cp.setBackground(Color.white);
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.setLayout(null);			
		add(cp);
		
		modinstallerVersionLabel.setBounds(widthFrame-150-15, 15, 150, 20);
		modinstallerVersionLabel.setText("Version " + modinstallerVersion + " " + versionExtension);
		modinstallerVersionLabel.setFont(new Font("Arial", Font.PLAIN, 14));	
		modinstallerVersionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		cp.add(modinstallerVersionLabel);
		
		logo.setBounds(0, 0, widthFrame, heightFrame-50);
		logo.setIcon(new ImageIcon(this.getClass().getResource("src/logok.png")));
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		cp.add(logo);
		
		prog.setBounds(220, 250, 350, 20);
		prog.setText(Read.getTextwith("Start", "prog1"));
		prog.setFont(new Font("Dialog", Font.BOLD, 16));		
		cp.add(prog);
		
		setVisible(true);	
		
		new Thread() 
		{
			public void run() 
			{					
				del(new File(sport, "zusatz.txt"));
			    del(new File(sport, "Importn"));
			    del(new File(sport, "modlist.txt"));				
								
				searchMCVersions();
				
				Start.online = Start.this.checkInstallerUpdate();
		        if (Start.online)
		        {
		          makeShortcuts();
		          downloadReqInfo();
		        }
		        askMCVersion();
			}
		}.start();
	}	
	
	
	/**
	 * Creates shortcuts for Minecraft Modinstaller on desktop and in start menu with a VB-Script
	 */
	private void makeShortcuts()
	{
		try
		{	
			String str = System.getProperty("os.name").toLowerCase(); // Ordner Appdata den Betriebssystemen anpassen
			File installerf = new File(sport, "MCModinstaller.exe");
			 
			if (str.contains("win") && (!installerf.exists()||installerf.length()<1024*100))
			{	
				if(!installerf.exists()||installerf.length()<1024*100)
				{
					String downlaodexestr = "http://www.minecraft-installer.de/Dateien/Programme/MC%20Modinstaller%20"+modinstallerVersion+".exe";
					new Downloader(downlaodexestr, installerf).run();
				}
				java.io.InputStream inputStream = this.getClass().getResourceAsStream("src/links.vbs");
	
			    File tempOutputFile = File.createTempFile("links", ".vbs"); 
			    tempOutputFile.deleteOnExit();
	
			    FileOutputStream out = new FileOutputStream( tempOutputFile );
	
			    byte buffer[] = new byte[1024];
			    int len;
			    while( ( len = inputStream.read( buffer ) ) > 0 ) 
			    {
			      out.write( buffer, 0, len );
			    }
	
			    out.close();
			    inputStream.close();
	
			    Desktop.getDesktop().open(tempOutputFile);								
			}
		}
		catch (Exception ex){	
			new Error(getError(ex));
		}	
	}	
	
	/**
	 * Gets Minecraft and Modinstaller folder
	 */
	private void getMCDir()
	{	
		String str = System.getProperty("os.name").toLowerCase(); // Ordner Appdata den Betriebssystemen anpassen
		File stamm;
		 if (str.contains("win"))
		 {
			 mineord = new File(System.getenv("APPDATA").replace("\\", File.separator), ".minecraft");
			 stamm = new File(System.getenv("APPDATA").replace("\\", File.separator));			 
		 }
		 else if (str.contains("mac")) 
		 {
			 mineord = new File(System.getProperty("user.home").replace("\\", File.separator), "Library/Application Support/minecraft");
			 stamm =  new File(System.getProperty("user.home").replace("\\", File.separator), "Library/Application Support");
		 }
		 else 
		 {
			mineord = new File(System.getProperty("user.home").replace("\\", File.separator), ".minecraft");
		    stamm = new File(System.getProperty("user.home").replace("\\", File.separator));
		 }	
		 sport = new File(stamm, "Modinstaller");
	}	  
	
	/**
	 * Checks if the the client uses the latest Modinstaller verison.
	 * If not a dialog is show that informs the user about the new Installer features.
	 * @return true: if the Modinstaller can establish an Internet connection
	 */
	private boolean checkInstallerUpdate()
	{		
		prog.setText(Read.getTextwith("Start", "prog4"));
		try 
		{			
			File updatetxt = new File(sport, "update.txt");
			String quellenurl = "https://www.minecraft-installer.de/api/counter.php?target=update&lang=" + lang;
			new Downloader(quellenurl , updatetxt).run();
			if(updatetxt.exists())
			{
				String[] cont = Textreader(updatetxt);
				if(cont.length<0)
				{
					new Downloader(quellenurl , updatetxt).run();
					cont = Textreader(updatetxt);
				}
				if(cont.length>0)
				{
					boolean newVersAvail = false;				
					String newVers ="";
								
					try
					{						
						String currVers = modinstallerVersion;
						newVers = cont[0];
						String s1 = normalisedVersion(currVers);
				        String s2 = normalisedVersion(newVers);
				        int cmp = s1.compareTo(s2);
				        if(cmp<0)
				        	newVersAvail = true;
					}
					catch (Exception e)
					{
						String body = "Text=" + getError(e) + "; Errorcode: S1x04a&MCVers=" + mcVersion + "&InstallerVers=" + 
								Read.getTextwith("installer", "version") + "&OP=" + System.getProperty("os.name").toString() + "; " + 
								System.getProperty("os.version").toString() + "; " + System.getProperty("os.arch").toString()+ "&EMail=unkn";
						new Postrequest("https://www.minecraft-installer.de/api/errorreceiver.php", body);
					}					
									
					if (newVersAvail) // Wenn Programmnummer nicht identisch ist
					{
						prog.setText(Read.getTextwith("Start", "prog5"));
						
						String desc ="";
						for (int i=1; i<cont.length; i++)
						{
							desc+=cont[i];
						}
						int eingabe = JOptionPane.showConfirmDialog(null,
								"<html><body><span style=\"font-weight:bold\">"+Read.getTextwith("Start", "update1")+
								newVers + Read.getTextwith("Start", "update2")+ desc+ Read.getTextwith("Start", "update3"), 
								Read.getTextwith("Start", "update1"), JOptionPane.YES_NO_OPTION);
						if (eingabe == 0) 
						{
							OperatingSystem.openLink(Read.getTextwith("installer", "website"));
						}
					}
					else
					{
						prog.setText(Read.getTextwith("Start", "prog6"));
				    	
					}
				}
				else 
					new IllegalStateException("Update file cannot be downloaded correctly");
			}			
			online=true;	
			del(updatetxt);
		}		 
		catch (Exception ex) 
		{
			if(tryno<2)
			{
				tryno++;				
				return checkInstallerUpdate();				
			}
			 online = false;
		      Object[] options2 = { Read.getTextwith("Start", "inter1"), Read.getTextwith("Start", "inter2"), Read.getTextwith("Start", "inter3") };
		      int selected2 = JOptionPane.showOptionDialog(null, Read.getTextwith("Start", "inter4") + ex.toString(), 
		        Read.getTextwith("Start", "inter4h"), -1, 3, null, options2, options2[0]);
		      switch (selected2)
		      {
		      case 0: 
		        OperatingSystem.openLink(Read.getTextwith("Start", "intercon"));
		        break;
		      case 2: 
		        System.exit(0);
		      }
		      try
		      {
		        String body = "Text=" + OP.getError(ex) + "; Errorcode: S1x04&MCVers=" + mcVersion + "&InstallerVers=" + 
		          Read.getTextwith("installer", "version") + "&OP=" + System.getProperty("os.name").toString() + "; " + 
		          System.getProperty("os.version").toString() + "; " + System.getProperty("os.arch").toString() + "&EMail=unkn";
		        new Postrequest("https://www.minecraft-installer.de/api/error.php", body);
		      }
		      catch (Exception localException1) {}
		}
		return online;		
	}
	
	/**
	 * Lists all installed client Minecraft Versions
	 */	
	private void searchMCVersions()
	{		
		File file = new File(mineord, "versions");
		if (file.exists()) 
		{
			File[] li = file.listFiles();
			for (int i=0; i<li.length; i++)
			{
				File jarfile = new File(li[i], li[i].getName()+".jar");
				File jsonfile = new File(li[i], li[i].getName()+".json");
				if(jarfile.exists()&&jsonfile.exists()&&(!li[i].getName().equals("Modinstaller")))
				{					
					offlineList.add(li[i].getName());
				}
			}			
		}
	}
	
	/**
	 * Downloads all mod texts, downloads, Minecraft versions and the background picture for Modinstaller
	 */
	private void downloadReqInfo()
	{			    
		if(online)
		{
			try 
			{
				prog.setText(Read.getTextwith("Start", "prog12"));
				File texte = new File(sport, "modtexts.json"); 
				new Downloader("https://www.minecraft-installer.de/api/mods2.php", texte).run(); //all mod texts
				
				if(texte.exists())
				{
					Gson gson = new Gson();
					String jsontext= Textreaders(texte);
					modtexts = gson.fromJson(jsontext, Modinfo[].class);
					del(texte);
				}
			} 
			catch (Exception e) 
			{				
				new Error(getError(e));
			} 
			
			try 
			{
				prog.setText(Read.getTextwith("Start", "prog13"));
				File downloadt = new File(sport, "downloadtexts.json");
				new Downloader("https://www.minecraft-installer.de/api/offer3.php", downloadt).run();  //All mod downloads
				
				if(downloadt.exists())
				{
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
					String jsontext= Textreaders(downloadt);
			    	moddownloads = gson.fromJson(jsontext, Modinfo[].class);
			    	del(downloadt);
				}
			}
			catch (Exception e)
			{
				new Error(getError(e));
			}			
			
	    	try
	    	{
	    		File mcversions = new File(sport, "mcversions.json"); 
	    		new Downloader("https://www.minecraft-installer.de/api/mcversions.php", mcversions).run(); //MC versions + number of mods
	    		if(mcversions.exists())
	        	{
	    			Gson gson = new Gson();
	    			String jsontext;
	    			try 
	    			{
	    				jsontext = Textreaders(mcversions);
	    				allMCVersions = gson.fromJson(jsontext, MCVersion[].class);			
	    				
	    			}
	    			catch (IOException e) {			
	    				e.printStackTrace();
	    			}
	    			ArrayList<MCVersion> fmv = new ArrayList<MCVersion>();
	    			for (MCVersion allmcv : allMCVersions)
	    			{
	    				if(allmcv.getSumForge()>2)
	    					fmv.add(allmcv);
	    			}
	    			forgeMCVersions = fmv.toArray(new MCVersion[fmv.size()]);
	    			del(mcversions);
	        	}
	    	}
	    	catch (Exception e)
	    	{
	    		new Error(getError(e));	
	    	}
	    	try
	    	{
	    		File backgr = new File(sport, "modinstallerbg.png"); //Background picture
	    		new Downloader("https://www.minecraft-installer.de/Dateien/modinstallerbg.png", backgr).run();	    		
	    	}
	    	catch (Exception e)
	    	{
	    		new Error(getError(e));	
	    	}
		}
	}
	
	/**
	 * Start next JFrame for asking the User to select Minecraft version or agreeing the licensee. 
	 */
	private void askMCVersion()
	{	
		prog.setText(Read.getTextwith("Start", "prog14"));
		
    	String lizenz = optionReader("license");
		
		if(lizenz.equals("n/a")||lizenz.equals("false"))
			new License();
		else
			new MCVersions();	
	    dispose();
	}
	
	/**
	 * Gets String of a program version which can be compared with another program version.
	 * @param version Program version
	 * @return normalised program version
	 */
	private String normalisedVersion(String version) 
	{
		return normalisedVersion(version, ".", 4);
	}

	/**
	 * Gets String of a program version which can be compared with another program version.
	 * @param version Program version
	 * @param sep separator String
	 * @param maxWidth maximal width of the program version
	 * @return
	 */
	private String normalisedVersion(String version, String sep, int maxWidth)
	{
		String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
		StringBuilder sb = new StringBuilder();
		for (String s : split) 
			sb.append(String.format("%" + maxWidth + 's', s));
		
		return sb.toString();		
	}
}
