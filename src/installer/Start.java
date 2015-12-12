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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 
 * Beschreibung
 * 
 * @version 4.3
 * @author Dirk Lippke
 */

public class Start extends JFrame
{
	private static final long serialVersionUID = 6893761562923644768L;
	private JLabel modinstallerVersionLabel = new JLabel();
	private JLabel prog = new JLabel();	
	private JLabel logo = new JLabel();	
	private JPanel cp;		
	private String Zusatz, modinstallerVersion;
	private ArrayList<String> OnlineList = new ArrayList<String>();
	private ArrayList<String> OfflineList = new ArrayList<String>();
	private ArrayList<String> AvialableList = new ArrayList<String>();
	private int versuch = 0;	
	private int hoehe =300, breite=500;
	
	public static String mcVersion=null, webplace, mineord, stamm, lang ="n/a";
	public static ArrayList<String> sent = new ArrayList<String>();
	public static boolean online = false;
	public static String[] mcVersionen;
	Modinfo[] Modlist = null;
	Modinfo[] Downloadlist = null;
	public static String mac ="";
	public static MinecraftOpenListener mol;
	
	public Start()
	{	
		/* MAC Adresse
		try { 
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()); 
            byte[] hwa = ni.getHardwareAddress(); 
           
            for (int i = 0; i < hwa.length; i++) { 
                mac += String.format("%x:", hwa[i]); 
            } 
            if (mac.length() > 0 && !ni.isLoopback()) { 
                System.out.println(mac.toLowerCase().substring(0, mac.length() - 1)); 
            } 
        } catch (Exception e) {} 
		*/
		minecraftDir();
		lang = optionReader("language");
		if(lang.equals("n/a"))
		{
			Object[] options2 = {"Deutsch (German)", "English (Englisch)"};			
			int selected2 = JOptionPane.showOptionDialog(null, "Welche Sprache sprichst Du?\nWhat language do you speak?", "Spache/Language?", 
					JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null,	options2, options2[0]);
			switch (selected2)
			{
				case 0: lang="de"; break;
				case 1: lang="en"; break;
			}			
		}	
		
		modinstallerVersion = Read.getTextwith("installer", "version");
		Zusatz = Read.getTextwith("installer", "zusatz");
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
				
		setSize(breite, hoehe);
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
		
		modinstallerVersionLabel.setBounds(breite-150-15, 15, 150, 20);
		modinstallerVersionLabel.setText("Version " + modinstallerVersion + " " + Zusatz);
		modinstallerVersionLabel.setFont(new Font("Arial", Font.PLAIN, 14));	
		modinstallerVersionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		cp.add(modinstallerVersionLabel);
		
		logo.setBounds(0, 0, breite, hoehe-50);
		logo.setIcon(new ImageIcon(this.getClass().getResource("src/logok.png")));
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		cp.add(logo);
		
		prog.setBounds(220, 250, 350, 20);
		prog.setText(Read.getTextwith("seite1", "prog1"));
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
				
				if(!new File(mineord).exists())
					sucheMineord();
				
				if(!searchMCVersions())
				{
					JOptionPane.showMessageDialog(null, Read.getTextwith("seite1", "version"));
					optionWriter("mcfolder", mineord);
					sucheMineord();
					if(!searchMCVersions())
						System.exit(0);
				}
				
				if(update())
					online();	
				else  		
					offline();
				
				mol = new MinecraftOpenListener(); // Check if Minecraft is open 
				modifyServerlist();				
				removeOldModFiles();	
				shortcuts();
				downloadInfo();
				startMenu();
			}
		}.start();
	}
	
	private void sucheMineord()
	{
		mineord=optionReader("mcfolder");
		if(mineord.contains("-----"))
			mineord=mineord.replace("-----", ":");
		File em = new File(mineord);
		if(mineord.equals("n/a")||(!em.exists()))
		{
			Object[] options2 = {Read.getTextwith("seite1", "mcdir"), Read.getTextwith("seite1", "inter1"), Read.getTextwith("seite1", "inter3")};
			int selected2 = JOptionPane.showOptionDialog(null, Read.getTextwith("seite1", "error4"), Read.getTextwith("seite1", "error4h"), 
					JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE, null, options2, options2[0]);
			switch(selected2)
			{
				case 0:
				{
					JFileChooser fc = new JFileChooser(); 
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
					int returnVal = fc.showOpenDialog(null);
					if (returnVal != JFileChooser.APPROVE_OPTION) 
					{
						 System.exit(0);
					}
					else 
					{
						mineord = String.valueOf(fc.getSelectedFile()).replace("\\", "/")+"/";
						optionWriter("mcfolder", mineord.replace(":", "-----"));
					}
					break;
				}
				case 1: 
				{
					new Browser(Read.getTextwith("installer", "website")+"faq.php?id=installmc");
					System.exit(0);
					break;
				}					
				case 2: System.exit(0);
				default: System.exit(0);
			}
		}			
	}
	
	public void online()
	{		
		prog.setText(Read.getTextwith("seite1", "prog15"));
		try 
		{
			File versionendat = new File(stamm+"Modinstaller/versions.txt");
			new Download().downloadFile(webplace+"versions.txt", new FileOutputStream(versionendat));
			String[] onlVers = Textreader(versionendat);
			String[] offVers = OfflineList.toArray(new String[OfflineList.size()]);			
			int pos=1000, posn=1000;
			
			//new MCVersions().setVisible(true);
			
			for(int i =0; i<onlVers.length; i++)
			{				
				String[] modi = onlVers[i].split(";");
				String onVers = modi[0];				
				OnlineList.add(onVers);
				if(modi.length==1)
					pos = 1000;
				else
					pos = Integer.parseInt(onlVers[i].split(";")[1]);
				
				for(int j=0; j<offVers.length; j++)
				{	
					if(offVers[j].equals(onVers))
					{	
						if(pos<posn)
						{
							posn=pos;							
							mcVersion = onVers;
						}
						AvialableList.add(onVers);
					}				
				}
			}			
			mcVersionen = AvialableList.toArray( new String[]{} );
			
			if(AvialableList.size()==0) //keine MC Version gefunden, für die online Mods verfügbar sind. --> Offline Modus
			{				
				String[] ques = {Read.getTextwith("seite1", "inter1"), Read.getTextwith("seite1", "inter2"), Read.getTextwith("seite1", "inter5")};
				int sel = JOptionPane.showOptionDialog(null, Read.getTextwith("seite1", "lessmc1")+Read.getTextwith("seite1", "lessmc"), Read.getTextwith("seite1", "lessmc1h"), 
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, ques, ques[ques.length-1]);
				switch (sel)
				{
					case 0:
					{
						new Browser(Read.getTextwith("installer", "website")+"faq.php?id=noonlinemods");
						System.exit(0);
						break;
					}
					case 1: default: 
					{
						offline();
						break;
					}
					case 2:
					{
						new MCLauncher();
						System.exit(0);
						break;
					}						
				}		
			}
			else
			{
				if(mcVersion==null)
				{
					String[] ques = {Read.getTextwith("seite1", "inter1"), Read.getTextwith("seite1", "inter6"), Read.getTextwith("seite1", "inter5")};
					int sel = JOptionPane.showOptionDialog(null, Read.getTextwith("seite1", "lessmc2") + Read.getTextwith("seite1", "lessmc"), 
							Read.getTextwith("seite1", "lessmc2h"), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
							null, ques, ques[ques.length-1]);
					switch (sel)
					{
						case 0:
						{
							new Browser(Read.getTextwith("installer", "website")+"faq.php?id=lessmods");
							System.exit(0);
							break;
						}
						case 1: default: 
						{
							int selected = JOptionPane.showOptionDialog(null, Read.getTextwith("OP", "modver"), Read.getTextwith("OP", "modverh"), 
									JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null, offVers, offVers[offVers.length-1]);
							if(selected !=-1)
							{
								mcVersion = offVers[selected];
							}						
							else
							{
								mcVersion = offVers[offVers.length-1];							
							}
							break;
						}
						case 2:
						{
							new MCLauncher();
							System.exit(0);
							break;
						}						
					}		
				}
			}
		}
		catch (Exception e){	
			new Error(getError(e));
		}			
	}
	
	public void offline()
	{
		online=false;
		Zusatz = "Offline";								
		mcVersionen = OfflineList.toArray( new String[]{} );
		int selected2 = JOptionPane.showOptionDialog(null, Read.getTextwith("OP", "modver") + " (Offline)", Read.getTextwith("OP", "modverh"),
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
	
	public void shortcuts()
	{
		try
		{	
			String str = System.getProperty("os.name").toLowerCase(); // Ordner Appdata den Betriebssystemen anpassen
			File installer = new File(stamm+"Modinstaller/MCModinstaller.exe");
			 
			if (str.contains("win") && !installer.exists())
			{	
				new Download().smartDownload("http://www.minecraft-installer.de//Dateien/Programme/MC%20Modinstaller%20"+modinstallerVersion+".exe", installer);				
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
	
	public void minecraftDir()
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

    public static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    public static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }
	
	public boolean update()  // Update testen
	{		
		prog.setText(Read.getTextwith("seite1", "prog4"));
		try 
		{			
			File updatetxt = new File(stamm + "Modinstaller/update.txt");
			String quellenurl = "http://www.minecraft-installer.de/request.php?target=update&lang="+Read.getTextwith("installer", "lang");
			new Download().downloadFile(quellenurl ,new FileOutputStream(updatetxt)); // update_de.txt herunterladen
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
					new Download().post("http://www.minecraft-installer.de/api/errorreceiver.php", body);
				}					
								
				if (newVersAvail) // Wenn Programmnummer nicht identisch ist
				{
					prog.setText(Read.getTextwith("seite1", "prog5"));
					
					String desc ="";
					for (int i=1; i<cont.length; i++)
					{
						desc+=cont[i];
					}
					int eingabe = JOptionPane.showConfirmDialog(null,
							"<html><body><span style=\"font-weight:bold\">"+Read.getTextwith("seite1", "update1")+
							newVers + Read.getTextwith("seite1", "update2")+ desc+ Read.getTextwith("seite1", "update3"), 
							Read.getTextwith("seite1", "update1"), JOptionPane.YES_NO_OPTION);
					if (eingabe == 0) 
					{
						new Browser(Read.getTextwith("installer", "website"));
					}
				}
				else
				{
					prog.setText(Read.getTextwith("seite1", "prog6"));
			    	
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
				return update();				
			}
			try 
			{
				String body = "Text=" + String.valueOf(ex) + "; Errorcode: S1x04&MCVers=" + mcVersion + "&InstallerVers=" +
						Read.getTextwith("installer", "version") + "&OP=" + System.getProperty("os.name").toString() + "; " + 
						System.getProperty("os.version").toString() + "; " + System.getProperty("os.arch").toString()+ "&EMail=unkn";
				new Download().post("http://www.minecraft-installer.de/error.php", body);
			} 
			catch (Exception e) {}

			Object[] options2 = {Read.getTextwith("seite1", "inter1"), Read.getTextwith("seite1", "inter2"), Read.getTextwith("seite1", "inter3")};
			int selected2 = JOptionPane.showOptionDialog(null, Read.getTextwith("seite1", "inter4")+ex.toString(), 
					Read.getTextwith("seite1", "inter4h"), JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null, options2, options2[0]);
			switch(selected2)
			{
				case 0: new Browser(Read.getTextwith("seite1", "intercon"));
						break;
				case 2: System.exit(0);
			}
			online = false;
		}
		return online;		
	}
	
	public boolean searchMCVersions()
	{		
		boolean found=false;
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
					OfflineList.add(li[i].getName());
					found=true;
				}
			}			
		}	
		return found;
	}
	
	public void removeOldModFiles()
	{
		try 												// Wenn Minecraft aktueller
		{ 
			prog.setText(Read.getTextwith("seite1", "prog7"));
			
			String lastmc = optionReader("lastmc");
			
			if (!lastmc.equals("n/a")&&!lastmc.equals(mcVersion))
			{				
				prog.setText(Read.getTextwith("seite1", "prog8"));
				
				del(new File(stamm + "Modinstaller/Mods"));				
				del(new File(stamm + "Modinstaller/Original"));	
				del(new File(stamm + "Modinstaller/Mods/forge.zip"));	
				del(new File(stamm + "Modinstaller/Mods/Forge"));	
			}
			else
			{
				prog.setText(Read.getTextwith("seite1", "prog9"));						
			}
							
		} 
		catch (Exception ex) 
		{
			new Error(String.valueOf(ex) +"\n\nErrorcode: S1x03");
		}		
	}
	
	public void downloadInfo()
	{			    
		if(online)
		{
			try 
			{
				prog.setText(Read.getTextwith("seite1", "prog12"));
				File texte = new File(stamm+"Modinstaller/modtexts.json");
				new Download().downloadFile("http://www.minecraft-installer.de/api/mods2.php", new FileOutputStream(texte));
				
				Gson gson = new Gson();
				String jsontext= Textreaders(texte);
				Modlist = gson.fromJson(jsontext, Modinfo[].class);
				del(texte);
			} 
			catch (Exception e) 
			{				
				new Error(getError(e));
			} 
			
			try 
			{
				prog.setText(Read.getTextwith("seite1", "prog13"));
				File downloadt = new File(stamm+"Modinstaller/downloadtexts.json");
				new Download().downloadFile("http://www.minecraft-installer.de/api/offer3.php", new FileOutputStream(downloadt));
				
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
				String jsontext= Textreaders(downloadt);
		    	Downloadlist = gson.fromJson(jsontext, Modinfo[].class);
		    	del(downloadt);
			}
			catch (Exception e)
			{
				new Error(getError(e));
			}
		}
	}
	
	public void startMenu()
	{	
		prog.setText(Read.getTextwith("seite1", "prog14"));
		
    	String lizenz = optionReader("lizenz");
		
		if(lizenz.equals("n/a")||lizenz.equals("false"))
		{
			new License(Modlist, Downloadlist);
		}
		else
		{
			new Menu(Modlist, Downloadlist);
		}		
	    dispose();
	}
	
	public void modifyServerlist()  //Serverliste modifizieren
	{
		if(optionReader("servermod").equals("n/a"))
		{
			File jsonfile = new File(stamm+"Modinstaller/serverlist.json");
			try {
				new Download().downloadFile("http://www.minecraft-installer.de/api/serverlist.json", new FileOutputStream(jsonfile));
			} catch (Exception e) {				
				e.printStackTrace();
			} 
			try 
			{
				List<CompoundTag> oldserverentries = new ArrayList<CompoundTag>();	  
				File sd = new File(mineord+"servers.dat");				
				if(sd.exists())
				{
					FileInputStream fis = null;
					NBTInputStream nis = null;	
					
					try
					{
						fis = new FileInputStream(mineord+"servers.dat");
						nis = new NBTInputStream(fis, false);						
						
						CompoundTag master = (CompoundTag) nis.readTag();						
						CompoundMap map = master.getValue();
						Set<Entry<String, Tag>> s=map.entrySet();
						Iterator<Entry<String, Tag>> it=s.iterator();
					    
						Map.Entry<String, Tag> m =it.next();
						@SuppressWarnings("unchecked")
						ListTag<CompoundTag> value= (ListTag<CompoundTag>) m.getValue();   
					    oldserverentries = value.getValue(); //Alte Serverlisteinträge
					}
					finally
					{
						if(nis!=null) nis.close();
						if(fis!=null) fis.close();
					}
				}
				else
				{
					 sd.createNewFile();
				}
			  					
				CompoundMap map3 = new CompoundMap();
				ArrayList<CompoundTag> serverentries = new ArrayList<CompoundTag>();
				 
				Gson gson = new Gson(); 
				String jsontext= Textreaders(jsonfile);
				String Servername="", Serverip="", Servericon="";  
		        JsonArray jsona1 = gson.fromJson(jsontext, JsonArray.class);
		        
		        for(int i=0; i<jsona1.size(); i++)
		        {
			        JsonObject jsono3 = jsona1.get(i).getAsJsonObject();		      
			        try
			        {
			        	Servername = jsono3.get("name").getAsString();
			        }
			        catch (Exception e){}
			        try
			        {
			        	Serverip = jsono3.get("ip").getAsString();
			        }
			        catch (Exception e){}
			        try
			        {
			        	Servericon = jsono3.get("icon").getAsString();
			        }
			        catch (Exception e){}		       	
					
			        CompoundMap serverentry = new CompoundMap();
					serverentry.put(new StringTag("name", Servername));				
					serverentry.put(new StringTag("ip", Serverip));	
					serverentry.put(new StringTag("icon", Servericon));					
							
					serverentries.add(new CompoundTag("", serverentry));	
		        }		        
		        serverentries.addAll(oldserverentries);
		        map3.put(new ListTag<CompoundTag>("servers", CompoundTag.class, serverentries));
		       
		        FileOutputStream fos = null;
		        NBTOutputStream nos = null;		        
		        try
		        {		        	
					fos = new FileOutputStream(sd);
					nos = new NBTOutputStream(fos, false);			
					CompoundTag exit = new CompoundTag("", map3);
					nos.writeTag(exit);
				}
		        finally
		        {
		        	if(nos!=null) nos.close();
		        	if(fos!=null) fos.close();
		        }
				
				optionWriter("servermod", "true");
				del(jsonfile);
			} 
			catch (IOException e1) 
			{			
				e1.printStackTrace();				
			}	
		}
	}


	public static void main(String[] args) 
	{		
		System.setProperty("java.net.preferIPv4Stack", "true");
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
}
