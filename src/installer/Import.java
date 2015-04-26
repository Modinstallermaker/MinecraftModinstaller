package installer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import layout.TableLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Import extends JFrame 
{
	private static final long serialVersionUID = 1L;
	
	private boolean Modloader =Menu.Modloader;
	private String stamm = Start.stamm;	
	private File sport = new File(stamm+"Modinstaller/Import/");
	private File extr = new File(stamm+"Modinstaller/Importc/");
	private File extr2 = new File(stamm+"Modinstaller/Importc2/");
	private String Modname="nicht angegeben", Modversion="unbekannt", MCVersion="unbekannt", Beschreibung="nicht vorhanden", Autoren="", Webseite="", Credits="", Benoetigt="keine", Logo="";
	
	public Import(File datei)
	{	
		System.gc();
		new OP().del(extr);
		new OP().del(extr2);
		new OP().makedirs(sport);
		new OP().makedirs(extr);
		
		
		if(!Modloader)
		{	
			try 
			{
				sucher(datei);					
			} 
			catch (Exception e) {}
		}
		else
		{
			if(datei.isFile()) //Modloader Datei importiert
			{
				String Dateiendung = datei.getName().substring(datei.getName().lastIndexOf("."));
				if(Dateiendung.equals(".jar")||Dateiendung.equals(".zip"))
				{
					try {
						new Extract(datei, sport);
					} catch (Exception e) {					
						e.printStackTrace();
					}
				}
				else
				{
					try {
						new OP().copy(datei, sport);
					} catch (Exception e) {					
						e.printStackTrace();
					}
				}
			}
			else //Modloader Ordner importiert
			{
				try {
					new OP().copy(datei, sport);
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}
		}		
		if(updateCat())
		{			
			File importf = new File(stamm+"Modinstaller/Import/"+ Modname +".jar");
			new Compress(extr, importf);
			Menu.weiter.setEnabled(true);
			make();
		}
		else
		{
			if(datei.isFile())
				Modname=datei.getName().substring(0, datei.getName().lastIndexOf("."));
			else
				Modname=datei.getName();
			File importf = new File(stamm+"Modinstaller/Import/"+ Modname +".jar");
			try {
				new OP().copy(datei, importf);
			} catch (Exception e) {}
		}
		Menu.jList2Model.addElement("+ " +Modname);
		System.gc();
	}
	
	public void sucher(File datei)
	{	
		new OP().del(extr);
		if(datei.isFile())
		{	
			String Dateiendung = datei.getName().substring(datei.getName().lastIndexOf("."));
			if(Dateiendung.equals(".jar")||Dateiendung.equals(".zip"))
			{
				try 
				{
					new Extract(datei, extr);
					if(searchFile(extr, ".info").length==0)
					{						
						try {
							File[] jars = searchFile(extr, ".jar");
							for (int j=0; j<jars.length; j++)
							{
								new OP().del(extr2);
								new Extract(jars[j], extr2);
							}
						}
						catch (Exception e){}
					
						try {
							File[] zips = searchFile(extr, ".zip");
							for (int z=0; z<zips.length; z++)
							{
								new OP().del(extr2);
								new Extract(zips[z], extr2);
							}
						}
						catch (Exception e){}
						new OP().copy(extr2, extr);
					}				
				} catch (Exception e) {}
			}
		}
		else
		{
			File[] jars = searchFile(datei, ".jar");
			for (int j=0; j<jars.length; j++)
				sucher(jars[j]);
			File[] zips = searchFile(datei, ".zip");
			for (int z=0; z<zips.length; z++)
				sucher(zips[z]);
		}
	}
	
	public boolean updateCat()
	{	
		File[] modinfo =  searchFile(extr, ".info");
		if(modinfo.length>0)
		{				
			Gson gson = new Gson(); 
	        try 
	        {          
	            String jsontext = new OP().Textreaders(modinfo[0]);
	    		
	            JsonArray jsona1 = gson.fromJson(jsontext, JsonArray.class);
	            JsonObject jsono1 = gson.fromJson(jsona1.get(0), JsonObject.class); 
	       
	        	try{
	        		Modname = jsono1.get("name").getAsString();
	        	}
	        	catch (Exception e){						
				}
	        	try{
	        		Modversion = jsono1.get("version").getAsString();
	        	}
	        	catch (Exception e){						
				}
	        	try{
	        		MCVersion = jsono1.get("mcversion").getAsString();
	        		if(!MCVersion.equals("unbekannt")&&!MCVersion.contains(Start.Version))
	        			JOptionPane.showMessageDialog(null, 
	        					  "Die vom Modentwickler angebene Minecraft Version ("+MCVersion+") "
	        					+ "stimmt nicht mit Deiner im Modinstaller ausgewählten Minecraft Version ("+Start.Version+") überein.\n"
	        					+ "Entweder wird Minecraft beim Starten abstürzen oder die Mod funktioniert dennoch (der Entwicker hat evtl. falsche Angaben gemacht)...\n\n"	        							
	        					+ "Wenn Du sicher sein möchtest, dass die Mod funktioniert, dann wähle zuerst im Modinstaller \"Minecraft "+MCVersion+"\" aus "
	        					+ "und importiere die Mod dann.", "Mod funktioniert evtl. nicht", JOptionPane.WARNING_MESSAGE); 	        		
	        	}
	        	catch (Exception e){						
				}
	        	try{
	        		Beschreibung = jsono1.get("description").getAsString();
	        	}
	        	catch (Exception e){						
				}
	        	try{	
	        		String neu ="";
        			JsonArray Autorena = jsono1.get("authorList").getAsJsonArray();
	        		for (int i=0; i<Autorena.size(); i++)
	        			neu += Autorena.get(i).getAsString() +", ";
	        		if(neu.length()>0)
	        		Autoren = neu.substring(0, neu.length()-2);
	        	}
	        	catch (Exception e){
	        		try{
	        			String neu ="";
	        			JsonArray Autorena = jsono1.get("authors").getAsJsonArray();
		        		for (int i=0; i<Autorena.size(); i++)
		        			neu += Autorena.get(i).getAsString() +", ";
		        		if(neu.length()>0)
		        		Autoren = neu.substring(0, neu.length()-2);
		        	}
		        	catch (Exception e2){
	        		
		        	}
				}
	        	try{
	        		Webseite = jsono1.get("url").getAsString();
	        	}
	        	catch (Exception e){						
				}
	        	try{
	        		Credits = jsono1.get("credits").getAsString();
	        	}
	        	catch (Exception e){						
				}
	        	try{
	        		String neu ="";
	        		JsonArray Benoetigta = jsono1.get("dependencies").getAsJsonArray();
	        		for (int i=0; i<Benoetigta.size(); i++)
	        			neu += Benoetigta.get(i).getAsString() +", ";
	        		if(neu.length()>0)
	        		{
	        			Benoetigt = neu.substring(0, neu.length()-2);
	        			JOptionPane.showMessageDialog(null, "Bitte denke daran, folgende für die Mod benötigte Mods mit zu importieren:\n\n"+Benoetigt, "Mod benötigt weitere Mods", JOptionPane.INFORMATION_MESSAGE);
	        		}
	        	}
	        	catch (Exception e){						
				}
	        	try{
	        		Logo = jsono1.get("logoFile").getAsString().replace("\\", "/");
	        	}
	        	catch (Exception e){
				}	        	
	        } 
	        catch (Exception e) 
	        { 
	            e.printStackTrace(); 
	        } 
	        return true;
		}
		else
			return false;
	}
	
	public File[] searchFile(File ordner, String suche)
	{		
		FileFinder ff = new FileFinder();
        ff.sucheDatei(suche, ordner, null);
        File[] fs = new File[ff.findings.size()];
        for (int i=0; i<ff.findings.size(); i++)
        {
        	fs[i]=ff.findings.get(i);
        }
        return fs;
	}

	public Import(String Modname)
	{
		File Mod = new File(stamm+"Modinstaller/Import/"+Modname+".jar");
		try {
			new OP().del(extr);
			new Extract(Mod, extr);
		} catch (Exception e) {	}
		if(updateCat())		
			make();
	}	

	public void make()
	{
		setTitle(Modname);
		
		setLocationRelativeTo(null);
		setResizable(true);	
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());	
		 double size[][] = {{TableLayout.FILL}, // Columns
		            {120, TableLayout.FILL, 40}}; // Rows

		setLayout(new TableLayout(size));
		boolean text = true;
		if(!Logo.equals(""))
		{
			JLabel bild = new JLabel();		
			bild.setHorizontalAlignment(SwingConstants.CENTER);
			String pfad = extr.toString().replace("\\", "/")+"/"+Logo;
			File f = new File(pfad);
			if(!f.exists())
			{
				if(Logo.startsWith("mods"))
					Logo = Logo.substring(5);
					pfad = extr.toString().replace("\\", "/")+"/assets/"+Logo;
					f = new File(pfad);				
			}
			if(f.exists())
			{
				bild.setIcon(new ImageIcon(f.getAbsolutePath()));
				text =false;
				add(bild,  "0,0");
			}			
		}
		if(text)
		{
			JLabel head = new JLabel(Modname);
			head.setPreferredSize(new Dimension(300, 50));
			head.setFont(Start.lcd.deriveFont(Font.BOLD,30));
			head.setHorizontalAlignment(SwingConstants.CENTER);
			add(head, "0,0");
		}
		
		DefaultTableModel model = new DefaultTableModel();       
		model.setColumnIdentifiers(new Object[] {"Beschreibung", "Wert"});
		
        JTable table = new JTable(model);
       
        model.insertRow(0, new Object[] {"Modversion",Modversion}); //Modversion
        model.insertRow(1, new Object[] {"für Minecraft",MCVersion}); //Für Minecraft
        model.insertRow(2, new Object[] {"Beschreibung", Beschreibung}); //Beschreibung     
        model.insertRow(3, new Object[] {"Benötigte Mods",Benoetigt}); //Benötigte Mods
        model.insertRow(4, new Object[] {"Modautoren", Autoren}); //Autoren
        model.insertRow(5, new Object[] {"Entwicklerwebseite", Webseite}); //Webseite
        model.insertRow(6, new Object[] {"Danksagung", Credits}); //Webseite        
        
        table.setRowHeight(30);
        add(new JScrollPane(table),  "0,1");   
        
        JButton close = new JButton("Schließen");
        close.addActionListener(new ActionListener() { 
  	      public void actionPerformed(ActionEvent evt) { 
  	        dispose();
  	      }
  	    });
        add(close, "0,2");
        setSize(550, 450);
        setVisible(true); 
	}
}
