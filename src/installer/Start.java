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
	private String versionExtension, modinstallerVersion;
	private ArrayList<String> offlineList = new ArrayList<String>();
	private int versuch = 0;	
	private int heightFrame =300, widthFrame=500;
	private Modinfo[] modtexts = null, moddownloads = null;
	
	public static String mcVersion="", webplace, mineord, stamm, lang ="n/a";
	public static ArrayList<String> sentImportedModInfo = new ArrayList<String>();
	public static String[] mcVersionen;	
	public static MCVersion[] allMCVersions, forgeMCVersions;	
	public static MinecraftOpenListener mol;
	public static boolean online = false;
	
	/**
	 * Setting up design of Minecraft Modinstaller
	 */	
	public static void main(String[] args) 
	{
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
			del(new File(stamm + "Modinstaller"));
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
		
		makedirs(new File(stamm + "Modinstaller"));	
		
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
				del(new File(stamm+"Modinstaller/zusatz.txt"));
				del(new File(stamm+"Modinstaller/Importn/"));
				del(new File(stamm + "Modinstaller/modlist.txt"));				
								
				searchMCVersions();
		
				if(!checkInstallerUpdate())
					offline();
				
				mol = new MinecraftOpenListener(); // Check if Minecraft is open 
				if(online)
				{
					makeShortcuts();
					downloadReqInfo();
				}
				askMCVersion();
			}
		}.start();
	}
	
	/**
	 * If the client pc is offline, configure Modinstaller and ask for Minecraft Version	
	 */
	//TODO: This method has to be integrated in the MCVersion JFrame 
	private void offline()
	{
		online=false;
		versionExtension = "Offline";								
		mcVersionen = offlineList.toArray( new String[]{} );
		int selected2 = JOptionPane.showOptionDialog(null, Read.getTextwith("Start", "modver") + " (Offline)", Read.getTextwith("Start", "modverh"),
				JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null, mcVersionen, mcVersionen[mcVersionen.length-1]);
		if(selected2 !=-1)
		{
			mcVersion = mcVersionen[selected2];
		}
		else
		{
			mcVersion = mcVersionen[mcVersionen.length-1];
		}
	}
	
	/**
	 * Creates shortcuts for Minecraft Modinstaller on desktop and in start menu with a VB-Script
	 */
	private void makeShortcuts()
	{
		try
		{	
			String str = System.getProperty("os.name").toLowerCase(); // Ordner Appdata den Betriebssystemen anpassen
			File installer = new File(stamm+"Modinstaller/MCModinstaller.exe");
			 
			if (str.contains("win") && !installer.exists())
			{	
				String downlaodexestr = "http://www.minecraft-installer.de//Dateien/Programme/MC%20Modinstaller%20"+modinstallerVersion+".exe";
				new Downloader(downlaodexestr, installer).run();				
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
		
		 if (str.contains("win"))
		 {
			 mineord = System.getenv("APPDATA").replace("\\", "/") + "/.minecraft/";
			 stamm = System.getenv("APPDATA").replace("\\", "/")+"/";			 
		 }
		 else if (str.contains("mac")) 
		 {
			 mineord = System.getProperty("user.home").replace("\\", "/") + "/Library/Application Support/minecraft/";
			 stamm =  System.getProperty("user.home").replace("\\", "/") + "/Library/Application Support/";
		 }
		 else 
		 {
			mineord = System.getProperty("user.home").replace("\\", "/") + "/.minecraft/";
		    stamm = System.getProperty("user.home").replace("\\", "/")+"/";
		 }	
	}	  
	
	/**
	 * Checks if the the client uses the latest Modinstaller verison.
	 * If not a dialog is show that informs the user about the new Installer features.
	 * @return true: if the Modinstaller can establish an Internet connection
	 */
	private boolean checkInstallerUpdate()  // Update testen
	{		
		prog.setText(Read.getTextwith("Start", "prog4"));
		try 
		{			
			File updatetxt = new File(stamm + "Modinstaller/update.txt");
			String quellenurl = "http://www.minecraft-installer.de//request.php?target=update&lang="+Read.getTextwith("installer", "lang");
			new Downloader(quellenurl , updatetxt).run(); // update_de.txt herunterladen
			if(updatetxt.exists())
			{
				String[] cont = Textreader(updatetxt);
				
				boolean newVersAvail = false;				
				String newVers ="";
							
				try
				{						
					String currVers = modinstallerVersion;
					newVers = cont[0];
					String s1 = normalisedVersion(currVers);
			        String s2 = normalisedVersion(newVers);
			        int cmp = s1.compareTo(s2); //Vergleich beider Modinstaller Versionen
			        if(cmp<0)
			        	newVersAvail = true;
				}
				catch (Exception e)
				{
					String body = "Text=" + String.valueOf(e) + "; Errorcode: S1x04a&MCVers=" + mcVersion + "&InstallerVers=" + 
							Read.getTextwith("installer", "version") + "&OP=" + System.getProperty("os.name").toString() + "; " + 
							System.getProperty("os.version").toString() + "; " + System.getProperty("os.arch").toString()+ "&EMail=unkn";
					new Postrequest("http://www.minecraft-installer.de/api/errorreceiver.php", body);
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
			online=true;	
			del(updatetxt);
		}		 
		catch (Exception ex) 
		{
			if(versuch<2)
			{
				versuch++;				
				return checkInstallerUpdate();				
			}
			try 
			{
				String body = "Text=" + String.valueOf(ex) + "; Errorcode: S1x04&MCVers=" + mcVersion + "&InstallerVers=" +
						Read.getTextwith("installer", "version") + "&OP=" + System.getProperty("os.name").toString() + "; " + 
						System.getProperty("os.version").toString() + "; " + System.getProperty("os.arch").toString()+ "&EMail=unkn";
				new Postrequest("http://www.minecraft-installer.de/error.php", body);
			} 
			catch (Exception e) {}

			Object[] options2 = {Read.getTextwith("Start", "inter1"), Read.getTextwith("Start", "inter2"), Read.getTextwith("Start", "inter3")};
			int selected2 = JOptionPane.showOptionDialog(null, Read.getTextwith("Start", "inter4")+ex.toString(), 
					Read.getTextwith("Start", "inter4h"), JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null, options2, options2[0]);
			switch(selected2)
			{
				case 0: OperatingSystem.openLink(Read.getTextwith("Start", "intercon"));//TODO: check intercon
						break;
				case 2: System.exit(0);
			}
			online = false;
		}
		return online;		
	}
	
	/**
	 * Lists all installed client Minecraft Versions
	 */	
	private void searchMCVersions()
	{		
		File file = new File(mineord + "versions");
		if (file.exists()) 
		{
			File[] li = file.listFiles();
			for (int i=0; i<li.length; i++)
			{
				File jarfile = new File(li[i].getAbsolutePath()+"/"+li[i].getName()+".jar");
				File jsonfile = new File(li[i].getAbsolutePath()+"/"+li[i].getName()+".json");
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
				File texte = new File(stamm+"Modinstaller/modtexts.json"); 
				new Downloader("http://www.minecraft-installer.de//api/mods2.php", texte).run(); //all mod texts
				
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
				File downloadt = new File(stamm+"Modinstaller/downloadtexts.json");
				new Downloader("http://www.minecraft-installer.de//api/offer3.php", downloadt).run();  //All mod downloads
				
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
	    		File mcversions = new File(Start.stamm+"Modinstaller/mcversions.json"); 
	    		new Downloader("http://www.minecraft-installer.de//api/mcversions.php", mcversions).run(); //MC versions + number of mods
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
	    		File backgr = new File(Start.stamm+"Modinstaller/modinstallerbg.png"); //Background picture
	    		new Downloader("http://www.minecraft-installer.de/Dateien/modinstallerbg.png", backgr).run();	    		
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
		
    	String lizenz = optionReader("lizenz");
		
		if(lizenz.equals("n/a")||lizenz.equals("false"))
			new License(modtexts, moddownloads, offlineList);
		else
			new MCVersions(modtexts, moddownloads, offlineList);	
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
