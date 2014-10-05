package installer;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;


/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Start extends JFrame 
{
	private static final long serialVersionUID = 6893761562923644768L;

	private JLabel programmtext = new JLabel();
	private JLabel prog = new JLabel();	
	private JPanel cp;	
	
	public static String Version;
	public static String Programmnummer;
	public static String Zusatz;
	public static String webplace;
	public static String mineord;
	public static String stamm = null;
	public static boolean online = false;
	public static Font lcd = new Font("Dialog",Font.PLAIN,20);
	public static String[] Versionen; 
	List<String> OnlineList = new ArrayList<String>();
	List<String> OfflineList = new ArrayList<String>();
	List<String> AvialableList;
	

	public Start()
	{		
		minecraftSuchen();
		File logf = new File(stamm + "/Modinstaller/log.log"); //Neuinstallation
		if(logf.exists())
			new OP().del(new File(stamm + "/Modinstaller"));
		new OP().makedirs(new File(stamm + "/Modinstaller"));
		File configf = new File(stamm + "/Modinstaller/config.txt");
		if(!configf.exists())
		{
			String[] text = {"loadtexts:true", "design:default", "lizenz:false"};
			try 
			{
				new OP().Textwriter(configf, text, false);
			} 
			catch (IOException e1) 
			{						
				e1.printStackTrace();
			}
		}	
		String lang ="en";
		try 
		{
			if(new OP().optionReader("language").equals("n/a"))
			  {
				Object[] options2 = {"Deutsch (German)", "English (Englisch)"};			
				int selected2 = JOptionPane.showOptionDialog(null, "Welche Sprache sprichst Du?\nWhat language do you speak?", "Spache/Language?", JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null,	options2, options2[0]);
				switch (selected2)
				{
				case 0: lang="de";
				}
				new OP().optionWriter("language", lang);
			  }
		}
		catch (IOException e1) 
		{			
			e1.printStackTrace();
		}
		 
		Programmnummer = Read.getTextwith("installer", "version");
		Zusatz = Read.getTextwith("installer", "zusatz");
		webplace = Read.getTextwith("installer", "webplace");
				
		setSize(499, 290);
		setUndecorated(true);
		setTitle("Minecraft Modinstaller 4");
		setLocationRelativeTo(null);
		setResizable(false);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
		
		cp = new GraphicsPanel(false, "src/mm.png");
		cp.setLayout(null);			
		add(cp);
		
		programmtext.setBounds(150, 15, 100, 20);
		programmtext.setText("Version " + Programmnummer + " " + Zusatz);
		programmtext.setFont(new Font("Arial", Font.PLAIN, 14));
		programmtext.setForeground(Color.decode("#6666ff"));
		cp.add(programmtext);
		
		prog.setBounds(160, 240, 350, 20);
		prog.setText(Read.getTextwith("seite1", "prog1"));
		prog.setFont(new Font("Dialog", Font.BOLD, 16));
		prog.setForeground(Color.decode("#e0dbc7"));
		cp.add(prog);
		
		setVisible(true);
		
		new Thread() 
		{			
			@Override
			public void run() 
			{					
				try 
				{
					lcd = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResource("src/Font.TTF").openStream());
				} catch (FontFormatException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}	
				
				Serverlist();
				
				new OP().makedirs(new File(stamm+"/Modinstaller/Texte"));    
				new OP().del(new File(stamm+"/Modinstaller/zusatz.txt"));
				new OP().del(new File(stamm+"/Modinstaller/Import"));
				new OP().del(new File(stamm + "/Modinstaller/modlist.txt"));
				
				versionenSuchen();
				
				if(OfflineList.size()==0)  //keine Dateien im Ordner versions
				{
					
					File vers = new File(mineord+"/versions");
					vers.mkdirs();					
					
					try 
					{							
						JOptionPane.showMessageDialog(null, Read.getTextwith("OP", "error")+":\n\n"+ mineord + "/versions", Read.getTextwith("OP", "errorh"), JOptionPane.ERROR_MESSAGE);
						Desktop.getDesktop().open(vers);							
					} 
					catch (IOException e) 
					{							
						e.printStackTrace();
					}
					System.exit(0);
				}
				
				if(updateHerunterladen())  //Online
				{
					Online();
				}				
				else  
				{				
					Offline();
				}				
				
				aktualisieren();				
				hauptmStarten();
			}
		}.start();
	}
	
	public void Online()
	{
		File versionendat = new File(stamm+"/Modinstaller/versionen.txt");
		try 
		{
			new Download().downloadFile(webplace+"quellen.txt", new FileOutputStream(versionendat));
			OnlineList = new OP().Textreadera(versionendat);
			
			AvialableList = new LinkedList<String>(OnlineList);					
			AvialableList.retainAll(OfflineList);						
			Collections.sort(AvialableList);
			
			Versionen = AvialableList.toArray( new String[]{} );
			
			LinkedList<String> SelectionList = (LinkedList<String>) AvialableList;
			SelectionList.add("Offline");	
			
			String[] Angebot = SelectionList.toArray( new String[]{} );
			
			if(Angebot.length==1) //nur Offline
			{
				Offline();
			}
			else
			{
				int selected = JOptionPane.showOptionDialog(null, Read.getTextwith("OP", "modver"), Read.getTextwith("OP", "modverh"), JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null, Angebot, Angebot[Angebot.length-1]);
				if(selected !=-1)
				{
					if(selected==Angebot.length-1)  //Offline ausgewählt
					{
						Offline();
					}
					else  //Online ausgewählt
					{
						Version = Angebot[selected];
					}
				}						
				else //Online nichts ausgewählt
				{
					Version = Angebot[Angebot.length-2];							
				}
			}
			
		} 
		catch (Exception e) 
		{						
			Offline();
		} 
	}
	
	public void Offline()
	{
		online=false;
		Zusatz = "Offline";								
		Versionen = OfflineList.toArray( new String[]{} );
		int selected2 = JOptionPane.showOptionDialog(null, Read.getTextwith("OP", "modver") + " (Offline)", Read.getTextwith("OP", "modverh"), JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null,	Versionen, Versionen[Versionen.length-1]);
		if(selected2 !=-1)
		{
			Version = Versionen[selected2];
		}
		else
		{
			Version = Versionen[Versionen.length-1];
		}
	}
	
	
	public void minecraftSuchen()
	{	
		String str = System.getProperty("os.name").toLowerCase(); // Ordner Appdata den Betriebssystemen anpassen
		
		 if (str.contains("win"))
		 {
			 mineord = System.getenv("APPDATA").replace("\\", "/") + "/.minecraft";
			 stamm = System.getenv("APPDATA").replace("\\", "/");
		 }
		 else if (str.contains("mac")) 
		 {
			 mineord = System.getProperty("user.home").replace("\\", "/") + "/Library/Application Support/minecraft";
			 stamm =  System.getProperty("user.home").replace("\\", "/") + "/Library/Application Support";
		 }
		 else if (str.contains("solaris")) 
		 {
			 mineord = System.getProperty("user.home").replace("\\", "/") + "/.minecraft";
			 stamm = System.getProperty("user.home").replace("\\", "/");
		 }
		 else if (str.contains("sunos")) 
		 {
			 mineord = System.getProperty("user.home").replace("\\", "/") + "/.minecraft";
			 stamm = System.getProperty("user.home").replace("\\", "/");
		 }
		 else if (str.contains("linux"))
		 {
			 mineord = System.getProperty("user.home").replace("\\", "/") + "/.minecraft";
			 stamm = System.getProperty("user.home").replace("\\", "/");
		 }
		 else if (str.contains("unix")) 
		 {
			 mineord = System.getProperty("user.home").replace("\\", "/") + "/.minecraft";
			 stamm = System.getProperty("user.home").replace("\\", "/");
		 }
		 else 
		 {
			mineord = System.getProperty("user.home").replace("\\", "/") + "/.minecraft";
		    stamm = System.getProperty("user.home").replace("\\", "/");
		 }		
			
		if(!new File(mineord).exists())
		{
			 JOptionPane.showMessageDialog(null, Read.getTextwith("seite1", "error4"), Read.getTextwith("seite1", "error4h"), JOptionPane.ERROR_MESSAGE);
			 JFileChooser fc = new JFileChooser(); 
			 
			 fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
			 int returnVal = fc.showOpenDialog(null);
			 if (returnVal != JFileChooser.APPROVE_OPTION) 
			 { 
				 System.exit(0);
			 } 
			 else 
			 { 
				 mineord = String.valueOf(fc.getSelectedFile()).replace("\\", "/");
			 }
		}		
	}
	
	public boolean updateHerunterladen()
	{		
		prog.setText(Read.getTextwith("seite1", "prog4"));
    	try 
    	{
			Thread.sleep(100);
		} 
    	catch (InterruptedException e1) 
    	{			
			e1.printStackTrace();
		}
		try // Update testen
		{
			File updatetxt = new File(stamm + "/Modinstaller/update.txt");		
			new Download().downloadFile("http://www.minecraft-installer.de/request.php?target=update&lang="+Read.getTextwith("installer", "lang"), new FileOutputStream(updatetxt)); // update_de.txt herunterladen
			BufferedReader in2 = new BufferedReader(new FileReader(stamm + "/Modinstaller/update.txt")); // Datei einlesen
			String zeile3 = null;
			int zahl = 0;
			
			String meld = "";
			String textz = "";
			boolean antw = false;
			while ((zeile3 = in2.readLine()) != null) // Datei durchk�mmen
			{
				zahl++;
				if (zahl == 1) 
				{					
					String[] Nrneu = zeile3.split("\\.");
					String[] Nralt = Programmnummer.split("\\.");	

					if (Integer.parseInt(Nrneu[0])>Integer.parseInt(Nralt[0])) 
					{						
						antw = true;	
					}
					else if(Integer.parseInt(Nrneu[0])==Integer.parseInt(Nralt[0]))
					{
						if (Integer.parseInt(Nrneu[1])>Integer.parseInt(Nralt[1])) 
						{
							antw = true;						
						}
						else if(Integer.parseInt(Nrneu[1])==Integer.parseInt(Nralt[1]))
						{
							if(Nrneu.length==3)
							{
								if(Nralt.length==3)
								{
									if (Integer.parseInt(Nrneu[2])>Integer.parseInt(Nralt[2])) 
									{
										antw = true;					
									}	
								}
								else
								{	
									if (Integer.parseInt(Nrneu[2])>0) 
									{
										antw = true;						
									}
								}								
							}	
						}
					}
					meld = zeile3;
				} 
				else // alle anderen Zeilen in text speichern
				{
					textz += zeile3;
				}
			}
			in2.close();
			if (antw == true) // Wenn Programmnummer nicht identisch ist
			{
				prog.setText(Read.getTextwith("seite1", "prog5"));
				int eingabe = JOptionPane.showConfirmDialog(null,"<html><body><span style=\"font-weight:bold\">"+Read.getTextwith("seite1", "update1")+ meld+ Read.getTextwith("seite1", "update2")+ textz+ Read.getTextwith("seite1", "update3"), Read.getTextwith("seite1", "update1") + meld, JOptionPane.YES_NO_OPTION);
				if (eingabe == 0) 
				{
					new Browser("http://www.minecraft-installer.de");
				} // end of if
			} // end of if
			else
			{
				prog.setText(Read.getTextwith("seite1", "prog6"));
		    	
			}
			online=true;
			
		}		 
		catch (Exception ex) 
		{
			try 
			{
				String body = "Text=" + String.valueOf(ex) + "; Errorcode: S1x04&MCVers=" + Version + "&InstallerVers=" + Read.getTextwith("installer", "version") + "&OP=" + System.getProperty("os.name").toString() + "; " + System.getProperty("os.version").toString() + "; " + System.getProperty("os.arch").toString()+ "&EMail=unkn";
				new Download().post("http://www.minecraft-installer.de/error.php", body);
			} 
			catch (Exception e) 
			{					
				e.printStackTrace();
			}

			Object[] options2 = {Read.getTextwith("seite1", "inter1"), Read.getTextwith("seite1", "inter2"), Read.getTextwith("seite1", "inter3")};
			int selected2 = JOptionPane.showOptionDialog(null, Read.getTextwith("seite1", "inter4")+ex.toString(), Read.getTextwith("seite1", "inter4h"), JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null, options2, options2[0]);
			switch(selected2)
			{
				case 0: new Browser("http://www.minecraft-installer.de/verbindung.htm");
						break;
				case 2: System.exit(0);
			}
			online = false;
		}
		return online;
	}
	
	public void versionenSuchen()
	{				
		File file = new File(mineord + "/versions");
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
				}
			}			
		}	
	}
	
	public void aktualisieren()
	{
		try 												// Wenn Minecraft aktueller
		{ 
			prog.setText(Read.getTextwith("seite1", "prog7"));
			
			String lastmc = new OP().optionReader("lastmc");
			
			if (!lastmc.equals("n/a")&&!lastmc.equals(Version))
			{				
				prog.setText(Read.getTextwith("seite1", "prog8"));
				
				new OP().del(new File(stamm + "/Modinstaller/Mods"));				
				new OP().del(new File(stamm + "/Modinstaller/Original"));	
				new OP().del(new File(stamm + "/Modinstaller/Mods/forge.zip"));	
				new OP().del(new File(stamm + "/Modinstaller/Mods/Forge"));	
			}
			else
			{
				prog.setText(Read.getTextwith("seite1", "prog9"));						
			}
							
		} 
		catch (Exception ex) 
		{
			new Error(String.valueOf(ex) +"\n\nErrorcode: S1x03", Version);
		}		
	}
	
	public void hauptmStarten()
	{
		prog.setText(Read.getTextwith("seite1", "prog12"));
		File texte = new File(stamm+"/Modinstaller/Texte.zip");
	    
		if(online)
	    try 
	    {
	    	
			new Download().downloadFile("http://www.minecraft-installer.de/Dateien/zipper.php?lang="+new OP().optionReader("language"), new FileOutputStream(texte));
			new Extrahieren(texte, new File(stamm+"/Modinstaller/"));
		} 
	    catch (Exception e1) 
		{				
			e1.printStackTrace();
		} 
		    
	    try 
	    {
			Thread.sleep(100);
			dispose();
		} 
	    catch (InterruptedException e1) 
	    {				
			e1.printStackTrace();
		} 
	   
	    try 
	    {
			if(new OP().optionReader("lizenz").equals("false"))
			{
				new Lizenz();
			}
			else
			{
				new Menu();
			}
		} 
	    catch (IOException e) 
	    {				
			e.printStackTrace();
		}		 	
	}
	
	public void Serverlist()  //Serverliste modifizieren
	{
		try
		  {
			if(new OP().optionReader("servermod").equals("n/a"))  
				try 
				{
					List<CompoundTag> list3 = new ArrayList<CompoundTag>();	  
					File sd = new File(mineord+"/servers.dat");
					if(sd.exists())
					{
						NBTInputStream fd = new NBTInputStream(new FileInputStream(mineord+"/servers.dat"), false);	
						
						CompoundTag master = (CompoundTag) fd.readTag();
						fd.close();
						
						CompoundMap map = master.getValue();		
						@SuppressWarnings("rawtypes")
						Set s=map.entrySet();
					    @SuppressWarnings("rawtypes")
						Iterator it=s.iterator();
					    
						@SuppressWarnings("rawtypes")
						Map.Entry m =(Map.Entry)it.next();
					    @SuppressWarnings("unchecked")
						ListTag<CompoundTag> value= (ListTag<CompoundTag>) m.getValue();   
					    list3 = value.getValue();
					}
				  			
					CompoundMap map21 = new CompoundMap();				
					CompoundMap map3 = new CompoundMap();
					
					map21.put(new StringTag("icon", "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAbjUlEQVR42uVa129j95klEGdGjSLFJomS2HsRiyg2UZREUV2iehlN0TTNeNwycS/ZxGnetE1zerK72HjXWATwgx8WfvCLIQcB/GL4xXnwP2LAgP3tOR/JiYF9oYNkHScPFxTJy3t/3/nOd8753RmDiBj+kY//84Fl1GywuS2GoaDdMBiwGaxjZv17ND5ksONzm3vAMOi33Tv43XDIYXD4rAa7x6Ln87X9HV8HRkz62XDIbrD7LPzbhvOfw/X/yNeR6KBtJDZ07xq8R/tweK16X72uW39rGMK6eAz6bPe+t7UO/j3otxqcYYfBiloGnCY9Z8DZ/ykD4NaFm3DuXW927L3YbEDSy1GZ3EpKoh56zzXuvIvfmWzuvzMA+Ll5uN+IRdwJFj3vTqDgqUs5mb1ekPlbJVn5wozU75+S9EpUvNnRdx1+6x1cw2hzfcYBcPj0+158d4rfvoPiZXI7qcWu3J2VtcfmZP3xmux9ZVl2v7wkSw9PS/EgLdFZv4wlh9+xey2nVpe51+H5jAHQ/P1A13DQfuLLud7Orsdl6uKETKPrtdOSLKPjm8/UZeOJeVkFEI0n5xUEHptPzStAhT0AMRMQV8r5Nu5xAjZ0sZi/bQBwEyz0PM4/Ho0NvZWYD0vlOCdLD06D5lVZe3RWjv55Xa58f1v2v74q+19dkQO87vzTkmw+XZftLy3K/vP4/CurCsz05ZwUdlNCrRiNDb6Fax/j2udbWvI3BEDAzsXcN+iz7oG6f4hM+2RiIy71W2XZQmHsbOOpOug+J1d/vCu3f3NBDr+xKgdfW5HL39+S4+80FIhdnLf9pSXZfm5R1jEeK4/MyNoX52T+tKzX43V5fYfXsge23Tfk/7QBaN78c+h+w593vUERKx5kZPpKXup3Kujsolz+l025+Yt9uYDOr35xVovbenZB6rfLygoWvdui/8Vvb8rF727JHpix9/yKsuTwG2uy9UzzfDIivzsuyYWweLKjbwD4Bu//qQDAeQcIa+6U8/XEfEimjidk+eEqOj2vRe59dRXFb+nBzhf30xKvBSWOc+NzQYmim/G5gGSgD/mdcSnspGCH4zgvI9WTvOrChW9tyOEL6woQ3y8+WFHnmLtZlAo0JV4LiXvc+TqKW2s6xl8bAAQV/o2bLbiSztcSWED5KKvCVsOiVu7OyKXvbcqDL12Su7+7qp3jOb5Jl4wvRSCCk7IIZqw8UtWOUhvWHqvp/JPy1ZNJdDglE42EZFZjMnOtoLpA3Tj+9gbGZEXdgu+pHwSc54envNCIodew5gXr2IDB7vorAMBXzF8NHX81VPJ8lNtMyDIKoYBxfveeX5YrP9qRh//7ijzw24syczWv4jVztaDdazxZhwjWZOmhKry/rP6/hI6uP1GTBhyBzKEt8nrUhH3oA4ElENNXcjoiHKdrL+7KtZ/uq5Zc/O6mbIFts9eLklmLSaDo+cgZGXwVDarBQv8yAJDquEhlJDb4Sno19iEXv/BARWl+8sPmIrhYzviVH2zLrd8cydyNosD+9HX3y8tyBBqzk/ztzEkBVK7iN6ty9K11nfcddJUAts8jK0j/EwDKkEQbLe6nwKg6mNCQ6wDg1q+O5OYvD+UY9ydovM8qBHMS4xSu+D50p0deGYkMVixIoH8eAJgpKHsRF3oZF/yAAkdVZyf2IVC7X1mSHSx28+l5FLAoF0H96z/fl+s/29NuFDDzSw9NyzKODYSdRYC29FBFjlEYF376q0O5/IMtpf7inSlkg6qKJim+ArEku1Zhm1UwafrKpJQvZHX2CRiBIhNu/+sFZcLB19cA/AW5/W/H0sB6yBzmjlDZ+wEyw8tIoUW755MAMGJOA7GXMLvvU7yY0DZb6LMAihrneOOJOVV0qvWVH+7I1RfJiIZkNxIyjxnfby128+kFZcyNnx/IA/9xETZ4JNd/sqt7gGDBLTEEnmQ9LLmtBNxjSkWP16WmLEAzahgXFrWK92TFyQ+3lSG8Nt1iE1qz+uicMoBrY96ooVmT2+M6ht6JsffB5pcGhvvTHQFgGjS+iXmHwKQ1kLDrFLeHX74iD/3nZaUp5/oAFqWWpRbWkEuwMAIwC/Gax4KVLXCDNSyOGsBxofeTyheweCa+BtLg9rNN36fnUyhLh1kVupu/PMCMLwCEKdWMxpM17fj96PQpWHRBAWjG6A2sh4XzYLwm6NSURTCQYwE9kB5z95sdMsD0ez+UWwUMAWYbiyDS19C16z/duydqZAQVnAWShnx/pLa1pAJHlSaAyw/NYJZndSSoC2QTu0zPv/GzfYShbRS8odfgtacvww1gi+z46a8PwZx9BZOsW4aIUnQvfmdTz+V4Nde2Jzd5HsUY4eru767pejk62GWK1TUgvebu33cEAHz+jKmLorWEIqqYQS6eC2dS4+Kv/2RfHv6vK7q4oxc2dEEMMlwAAaOqc47ZPdKanaAt7j6/pJ2nfbJQWh9BIX1J87kbFMlp7XgBnavfX8Z4bTet72tNG+R1uBa+clQ4XoffXIMQbzS/e5r7jJoUDzNa/HDQIdAzMVp7zzoCABZyFqn4JL+X0kWVL0xoUCkfZZRqFLtTqDBnmkJESl790a52iTc+wmJOoAnc7Cy0iicT2C0WwM5m1+IyvhCBYMZ1VhcfmNbzeQ4ZRcD52+aorepoEWy6DN2GbPnTsaoA0E3IWm6iAtQWBC8yGZlFkFbFaO/rDADsx8+iVb+k12NSovpiplk8RSu3mYSV5RXp4281lL6XkPbaosSDWvHU/9xWcA5fWEPXmfGb3eJ8EgxlFDpFOyRb2rGYdKaQccdIsPe+ilH5XgMA72g+ULtEsRRjfcVnvDdZw4jsy42haLdEsP4wWEwgPJlRGfaCAcauzgAYDNnP4rNByeY8UmokwYKizMKOpgBGAX7MjpVAYY7GGharSa21EI2uX17Sg9ZI1dbPsVgeTTAWVdy0UIwY9YKsIEDtPcC1F/fk1q+PdLN04TsbsMxtLZ6MoCCTfcwei7BWWrQfuYPFh0peQV7R5BkousWfd4ubDLD0ifH8uQ4Z4DSdxco+yXodMjk+qg8ppuGrVcwsNzpTcIfyWgK2lVTP5xwzzR290KQk2cGu0iFUGAEAR4SCx0i7pbu9moYcFsRxufPvx8qk9ubo6o/xGUaLewkCTBYcgC3MHgSFYHBjhFSqXQ6XvYjEPqU/4zEjuDc7SgsUV2JYnNY+Mfec7wwAa2/XWchll0IyIoUJv2SwFWWxBKKCrle2UlIMDEtpPiKTey1GQHDmb0+hi4ty57eX5LFXb6hWcAQufHtDQw+7SipTrCimLIjvqSE3f3Ggxa23ssUhlR7gcTdINm0/W9fcMXstL+OLYd0aI6AJR5WbK27ISHu+TyRHxA/hcxOA1Ih4QH/PoEmsxu7OABge6D0Lj9plqjItM5WSTBa8kqNYAdkCCi2tJ6ToHZKp2bBMQ3QolgxMTG1VMIRzzoi69WzTn8mAPRTLwo6+ua7dpUXxlR3Wv1/c0SRIfeC2WUFozTpFjpunJIqkqJHW3ADx4NzzIAAJhKnETFACdpMEPDbxhQYl4LSIz2ES76C5cwD8zoGzqdiYzBazMlvKS943KIXoiOSngxpxi6B/yT8kZQIAQczjs4ntpH5XuZiTMoLMJF0DW2TSncrN6MpkyGI52/RtFkg9oH5c+t6WrKF4PgRpbqdXdBSWkUIpvJxnD7rpzwMAUN4HajNFNgGIgBURSWH2k9WAJNx2ibtsEkDRgcEB8eH1EwHgGTKfzSRdspD2yHohLEtZv5SCwzLpQ9FgQQZIZ8bsUgIAfLpbwCjkVmK6t+co1G6WNIAwgZEd9HLa4za7+g0+J9hWK6O1HXxM8K7jlXpwgu8aOJ9WSXELoOPBfHPOg5h5L0EAEzgCMdA/XPEz90twfETCKHwCzRn3OCTlHZTYKOjfYoDd1NMZACNW49lkaFiqCZdslUJydTEl1xbSslUG0rgh9toyYu6TcBQ6cICRyLilCMCYFegUi63oquwAABPoYH47pQGIXs0Irc8CuaFqPQ/krN8P2+SWl9dkgRSxQN4jQVA5FBxUXw/C4gLhIQmh++70qGDPIpZhk1jGzGLp78K6eiUDAAohp8ylvJKAlgWGBwDIoIzZ+jsDwGXvP8uA9mkgWAcLrtTH5QubeXnmoCJRn0P8WFAa30cxX+kIQIiNSik5JtOgPB1hHgzgZmgOIPBJkY7F0YQ6CD/ne250DqEHVPQdCCKf+/Fcili766rq7OywWUK4b3guIOHwsPjs/WIfGxCzwygOU4+Meqx8eiwe95AEPS5JA4BieESm4i5dJ9kwAQaPdgpAzGU/m097ZRJKP4vOXl1Iyc3ljDzcyEtwaECyuOjqZEBKuEkK85bHaBQAQHEvrVvXWWxbWSijNLelcwCkgc0JR4OPtJgAU0tRWeIDFTCC52QhrLRU0poKzyQanYGqQ+iCzgEJ+uwKgAev5u7zgj2+jPhBb1u/eMYs4h4dkIAf54fDkgUAE4Ehfc20AEjgGDT3dQ5AoxiGBnhlGTZ4qZaUE4BwYS4h4RGLlCIjsjMVkZWcXyrRUamER6WQGJPsZkLyKKy6Epca8j2fAfCoXStKA75fAyjFg5Rul9PQjAKoPo7zqeoEJFlvPi+MYwurNsfiOf9gWgiF+xBozKC5rb9bRiND4vLbxQ02uG3Nw+8akeBYs3AeOXSdQCTRpCi0wWHucC/AWZlJuGUx45NVFLmObu9XorJZgs2MWWUh5VE9WMr6ZBY6sYLXhQmf5OoRyVeDEnGYZRRjwijK7tcv5GRq3CXlDTgFAChAF7jbo2AyxYVQaPvg/p2FU+kDRY8CEBq16Bxbes+LzdgtfoyEB6xg8RhXCJxJQmiMB3/78XcGjMxDA2bGPQoCGfCJAKAIkjo1XKBRCMlWMSRrAGIa1piK+mVpMiI7AGA26cY4DMk8AOH3ZMZEFLHT1CvBrAtd9EsMQjk9F5EizsuWfJJpxHVPQXHkPiO92gQgCJujijPO+tTrXWpzYYIwagV9e7X48IhV4mM28cLrCUAY3yVRIItkp7mGbEsDqlhfMTIqefwdx3cdAwDLOCviQnO4wGE1Lo/tlOQ6RqAUdkq+WJblOjI8Ol6FyHAxBKEaH5MybpSE6rqsRslgJNJgQ9JjlyIWRJYUS354dVSy3FRhBJgk08sRiWDWQwEHQsuAeLFx8SecEnBZJQSvjxAcFOkAqCGMAlU9gvcsPghWsNh80KnN2kCz1vPBj9F/GEI4JgWAEAJw1v5Og9DwwNnMuFvmcdFdzPoTe1PypaOqzvwMwtFyDQkRF+ZBpHOYsxxRB+2iWBwXmsJCU75hyaXiUo57ZTMf0HHJQSw5KjlkCabJTB60XwpLyO+QkAvJDZ3yM8BgjEIxXA+qj/WID+IbxfgFhsziovCBAQSfdCcTt0sRZWs97bunAe2D62EWsHQahGiDTIJ1iCCL3gMID65PykLGLyuTftksR3X+yZA5uMQRWHI4HZcqfsNZpQeXlYpOdL0s08WcbAAAjkqeHo2OlVMumYGAZnPYuoL+sVhA4rEoChxAl20yB/AzsGEWOoZuJ9wO7foY2MV7TMF66VT0/BmsYzkX0N9UAHK78Il28bgGQQSLOtcAUmijEFRUV+EEexDBAopaxY02EY52Kk0XIAsuziXlmcNp2GRBUggcUzo+LtWMXCIkUxPjKqaH1Rg6FVb7nMY5m7g+I7YX3YlHwjIeBzMAzgLElxpDGx5lwcwb0CSXzYg5d2ixdKElrIuRl1SvonjOfImgtuyPrxyZKJhCERwc+IQA7E1HZBeF0urW0MFciLPmhj368B1YAB1gkbTLG4sZeWSzoOpLPWBO4Ll0DY4IQTyaics1pEr+ZkoBCOnYsEvpIMAKNYWLzFtE/Oa1RrCPp4ePgw08j+mUbrQ8EZAyrkPwCM4sACgAWI4ED4qhRnjqktsG3bCJreMobGkCwOKJdB4Ic6HM1rWWNrCYeaREUp0soFXugCVEvc2Kq4jPiwCLlF8BAA0IFJmkgonfrQPUWqpJ2zzux99OA4CZ1pHFfZ12qL7LITEAGUURBQhxCaI2jd9Q4anuJW7c1PKcyoY6GMQcswWgygmvjEcCGAFE5r6uTjXAdEb0CcAhCqXvc65pMxSyGRzboGg941Vn4HcsdAG6MO51aISmeJ6uZOX2Sg5Fx2QZ3xEk2iXB4HvuMxZwDRZNG4uBqmRAFRmkDNYlwn5xuz1gwJDu8Pg9m0CgSHUCQLFm8Yy9DD0EaBPFrwFsXrs8CavNZMULbTH3nuvcBUi1dczoAeaWVGWnKDJcOLvH+WTXcq3ImW/ZDoHj3HE8+Nsr8ykVySXQmmFqD6CuQUgPp2Py+E4ZnQophUOwQNobC8sFmmwI+5EBvC4dDYodAaDn0w1SAJr3qqBw+nzG36Q+mUsGEJgqdKgE/UkCSCdGqa/r8x1uh5EDqrod9mKxAaVuJeYCGxJyupSFoodAayQsdIUxtQjUebCLtCSKGIVyBb/dKodVTHmtC7Nx3VnyPZ3lWQgn2THeCjLsLsFgIdPMGOg8Kd4ukAAwCNEpyLJ23ufftGNlEUAki9qjNAnwokiSTouxcxtsi6CK2QSjsF9vtDMVldPlrFzCfC+CXozKXBRFkpZIFnD3SC04QrHsAsMJBZEAkPIEgc5CVt1azaom8DqcaxZehitQALcBHMGIAYBJAhBoAkBF5zj8Ke871Q5Z7BDSIjvddgCOBllCRqWROTreDmsUJrJAlKJHS+Ji1rHoXSx4H/SlrV2tpyBkIbVDnkd94O6RGYEiWaZXp9ya0uqMywBiA2yiHXIcuM2mxlCwqOCTKIYjQfHivbApU4YRgBTuz0dbsZbtpVtawPPncG8vAhK2u/rgg86QwtrpErWWo0zSUVBXxwDwJqQ1L35tKY3ZHNGdIW+4D9o+tDEptyByXPweAFnR7XHTAgkExY4s4kgwIBGgdQUgIJfnkzoK3GZTTNl9FkMQ6O3s3CwACzpB92GLdt3j6FdLpE4wpjNxapFgH4NSBMDQKvk5z0m3xonjSBCoHR3vBUZtzRGgBZKi3A5TUadwMxVGFPzUfkW9n4um4vJIIr/TISh6pDiFioDUU15lBVnDkVppZYLrAJYFJ1uUbs90vrWRYX73DzV3fYyyLKqArXcK5zAgDQ4YtetRV3NM2k7RHg8CSyC5F+A5HQOA3H1G+m4DgAuzCaUtPZs0ZgjhfN9andANEsWH4DQQj9NhL9zCq5unR7dLGlrU7yeDChYfq20WQ5oLqBt0AgLAgjnLmZagcYbZAG5x+TCE0ZcCuQgQGdEz+G5sxKkHn/7waQ8TotqkpymiPI92yO7z+rFPAgDm6YxBg8LFwtkxUnoNEXRrKi7HtXG5BBo/iDFg4OHcN8pxKZanpFbMyANrExqNOR5cGDXgJsTzyb2KfHGrKEv0fgDMa5KiVYyHakBoWMKY+Sg6TwDo6QxDCd1YDSndOZo5nBsNQyOC3nv5gdTnKzWCIDJLkJV0AwJABthNHY9A/+8ncfOZVuih5zPD07qW8xDAQjPT3wYLHkW3SXN2vpyfkPlyDuKWkNsAYRWd1/QGK6J43lnPyeO7JdmFG1ALOEoEgMXTz8m69haXv6XeMOw088WIPo3iXGcBTszTdgWrUr+CNfCVI8PfU1DbupL1D6tOIAd09s/jQwN9b/JBA0MOc3vWP6hzzWIbpSgEL6JsYAEsirZWxeL5fGBxIghWJJD501IHZWlBFXzH0eHn18GEnbmcit8NaMAyWEDaEgCmOXa0nfcprPx8NulRSmvK9DUfddPv2XUq/iQA4fU0E7QskOdS+HjQGUw956X73H1vdgpAGsdL2Ba/T/toFMIqhvev5XQHx9Gg0jPu8lnhycK4/k1qryIBbqB7tLl2VCbdSW+y4ATAHO7vye76klycbWpAG4Bqaytbjo7B233NMIPxoc8TAKbRbCsQxUFpMoLPAqlDTKk8hw9AWHwbAIqn1dj9fs/5+14CAOlOATAMmvsMSE7FEVv/y6DhBww1XCw7xq1ulhsjLPAAu0LaGj19Q/XCp0+L1sEQagPDEQHgohmIaH/7jWXZ3VwDmAG1SQqcMoAC16Kt7gk+dsyheN3dARzG5Uyr29QFPhsY99jV6/k5d48hsAO0/2Cgr+vlvq5zRRRv4NExADyAnMHe38PNUQXC+Api6YdEeBZgcDyo5MvoMnWCj6KoCwRlEt8RGL7nOWQLN0yMwPR+zvZqKaHiyt9SxTnj2Vae598Ffa4/pmmyOR5jCgIzAgWabCGgHD+CQO/XB6Po+Ki9/0NsfV8x93ZVjN3nDX3d5wx/NgAOUy8BMAxbwIqBvhpu8CqA+CgNxKfSEZkvpnQGWSgfiBxjv8B9wulyUyAZgHhwPOgoFFKOSz3l0m12BV2nC1C02FF2VzvMPT26XcCRDTSf8pIJ1BQWy/jMYEYwuF/g0+FBc+9H6PirKLxm6u0y4NXQ3/MXAsBpNbZGo5fHwrC597WwDxZTLEk+Bn9GUrwDjeAOj1pxYykjjzTy+uCDSbCB4pvb4QBSZB4ZIqPMKLf29lRwiho7zsKVDUFE4OCo5oJ72YD/2jtoUncgMKQ61iYDvV2vGbvPLbBYc6v4vwoAPBAoDJa+LoPdYl4bc7leD40NqlOQkpfnx/XJ0B42ThRM6kE7Gl/D3uE6whC/Z054fLesIYkaEIL90TLbtqdFB0YkHWg+MQq1wlAY59DqgsgKLtDdbu59HUWusUhj6/h/AcBq7DLYLGaDzW7/nM3U28D+4Y0klJd+vIEZZ+FMiBdrCWyeoiqOt2CBLPzmSkbt85nDiu4r2H3GYA06rUjM7TDzA5WcHQ+OwPoiIfGNDsnwQC//pfcN0L1h6un6HIs0floA2B0OAwDgufcNWfr2IJZ/SLXoWta9Q0B3k9wLUBS5naYT0DlOAQSFkd4/jeSmowB7y7d2hUyCnO/AsFl8ToeMutxiNRv/YOz6/F5/9/n7Pl7kpw6Arb+bIkl9OD9sMR5j7/0WqUsq8/8aMMfz/xvwiRID0Qlsk2GJ1sbo2n6ay3/e4nv6ehpCGIF9chdo7e99y2QyH/f1dp9nkQg2hr85ANrfN3/b02Xr7zlxWoxvt5/11bNeHQs+Ub7ILTEskbpRbGV8PuKmENLL+RjbjQSHe73d333upK+nq6vfZDIYe7sNnwkAULz+3tLX3YvzT/3OgXeo2nzUxmcMG8WgPh1ahTtwu9p81mfTTMAYjt+8gxk/hZ31amEs5rMIgB6mHo6GEWy4E3Ba3o3q012HxmNSn7GVyo6xYXp719rfcwfiZmwX8ZkHAKmsmSGaGmFymHruOi197/EZHq6tdmbt734Pi71rMfaYBvCbjxfxdwPAveviXAQX/OT8c1jkH3E8h0XajD3n8F0P9yCfLgD/aMf/AhkBq7pbpUIjAAAAAElFTkSuQmCC"));
					map21.put(new StringTag("name", "Block City (Empfohlener Server)"));
					map21.put(new StringTag("ip", "join.block-city.de"));	
					
					List<CompoundTag> list2 = new ArrayList<CompoundTag>();		
					list2.add(new CompoundTag("", map21));				
					list2.addAll(list3);
	
					map3.put(new ListTag<CompoundTag>("servers", CompoundTag.class, list2));
					
					NBTOutputStream os = new NBTOutputStream(new FileOutputStream(mineord+"/servers.dat"), false);			
					CompoundTag exit = new CompoundTag("", map3);
					os.writeTag(exit);
					os.close();
					
					new OP().optionWriter("servermod", "true");
				} 
				catch (IOException e1) 
				{			
					e1.printStackTrace();
				}
		} 
		catch (IOException e1) 
		{			
			e1.printStackTrace();
		}	
	}
		
	
	public void beenden_ActionPerformed(ActionEvent evt) 
	{
		System.exit(0);
	}
	

	public static void main(String[] args) 
	{	
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		
		try 
	    {	
			Color red = Color.decode("#9C2717");
			Color white = Color.decode("#E0D4BA");
			//Color white = Color.decode("#FFF9E9");
			
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");			 
			UIManager.put("nimbusBase", white);
			UIManager.put("text", red);		    
			UIManager.put("nimbusSelectionBackground", red);
			UIManager.put("nimbusSelectedText", white);
			UIManager.put("nimbusFocus", white);
			UIManager.put("nimbusLightBackground", Color.decode("#FFF9E9"));
			UIManager.put("control", white);		
	    } 
	    catch (Exception e) 
	    {
	      try
	      {
	    	  UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
	      }
	      catch (Exception e2)
	      {}
	    }
		new Start();			
	}	
}
