package installer;

import static installer.OP.Textreaders;
import static installer.OP.copy;
import static installer.OP.del;
import static installer.OP.makedirs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

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

public class Import
{
	private String stamm = Start.stamm;	
	private boolean Modloader =Menu.isModloader;
	private File sport = new File(stamm+"Modinstaller/Import/");
	private File sportn = new File(stamm+"Modinstaller/Importn/");
	private File extr = new File(stamm+"Modinstaller/Importc/");
	private File extr2 = new File(stamm+"Modinstaller/Importc2/");
	File nameu=null;
	private String modName="", modVersion="undefined", mcVersion="undefined", description="", authors="", website="", credits="", requiredMods="none", modLogo="";
	private JTable table;
	private MenuGUI men;
	
	public Import(File datei, MenuGUI men)
	{	
		this.men=men;
		del(extr);
		del(extr2);		
		makedirs(sportn);
		makedirs(extr);	
			
		
		if(!Modloader) //Forge
		{	
			try 
			{
				sucher(datei);					
			} 
			catch (Exception e) {}
			
			if(modName.equals(""))
			{
				if(datei.isFile()) //Datei --> alles vor der Endung --> Titel --> Datei in mods Ordner
				{
					modName=datei.getName().substring(0, datei.getName().lastIndexOf("."));					
				}
				else //Ordner --> Ordnername --> Titel --> Ordner in mods Ordner
				{
					modName=datei.getName();
				}
				JOptionPane.showMessageDialog(null,  Read.getTextwith("modimport", "nomodinfo"));
				File importf = new File(sport+"/"+ modName +".jar");
				try {
					copy(datei, importf);
				} catch (Exception e) {}
				updateList();
			}	
		}
		else //Modloader
		{
			String name = datei.getName();
			File modspo = new File(sport.getAbsolutePath()+"/"+name);
			modspo.mkdirs();
			if(datei.isFile()) //Modloader Datei
			{
				String Dateiendung = datei.getName().substring(datei.getName().lastIndexOf("."));				
				if(Dateiendung.equals(".jar")||Dateiendung.equals(".zip")) //Modloader ZIP oder JAR Datei --> Extrahieren und Kopieren in Minecraft.jar
				{
					try {
						new Extract(datei, modspo);
					} catch (Exception e) {					
						e.printStackTrace();
					}
				}
				else //Modloader Datei unbekannt --> Kopieren in Minecraft.JAR
				{
					try {
						copy(datei, new File(modspo.getAbsolutePath()+"/"+name));
					} catch (Exception e) {					
						e.printStackTrace();
					}
				}
			}
			else //Modloader Ordner --> Kopieren in Minecraft.JAR
			{
				try {
					copy(datei, modspo);
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}
			setListEntry(name);	
		}			
	}
	
	public void sucher(File datei)
	{			
		del(extr);
		if(datei.isFile()) //Wenn Datei
		{	
			String Dateiendung = datei.getName().substring(datei.getName().lastIndexOf("."));
			if(Dateiendung.equals(".jar")||Dateiendung.equals(".zip")) //Wenn JAR oder ZIP Datei
			{
				try 
				{
					extr.mkdirs();
					new Extract(datei, extr);					
					searchInfo(extr);
					try {
						File[] jars = searchFile(extr, ".jar");
						for (int j=0; j<jars.length; j++)
						{
							
							del(extr2);
							new Extract(jars[j], extr2);
							searchInfo(extr2);
						}
					}
					catch (Exception e){}
				
					try {
						File[] zips = searchFile(extr, ".zip");
						for (int z=0; z<zips.length; z++)
						{
							del(extr2);
							new Extract(zips[z], extr2);
							searchInfo(extr2);
						}
					}
					catch (Exception e){}
				}
				catch (Exception e){}
			} 
		}
		else //Wenn Ordner
		{
			File[] jars = searchFile(datei, ".jar"); //In Ordner JAR Datei
			for (int j=0; j<jars.length; j++)
				sucher(jars[j]);
			File[] zips = searchFile(datei, ".zip"); //In Ordner ZIP Datei
			for (int z=0; z<zips.length; z++)
				sucher(zips[z]);		
			searchInfo(datei);
		}
	}
	
	private void searchInfo(File datei)
	{
		for(File file : datei.listFiles()) //In Ordner Infodatei suchen
		{
			if(file.isFile())
			{
				if(file.getName().equals("mcmod.info"))
				{
					String modname = updateCat(file);
					File path = new File(sportn.getAbsolutePath()+"/"+modname);
					path.mkdirs();
					try {
						copy(datei, path);
					} catch (Exception e) {				
						e.printStackTrace();
					}
					make();
					new Compress(path, new File(sport.getAbsolutePath()+"/"+modname+".jar"));
					updateList();
				}
			}
			else
			{
				searchInfo(file);
			}
		}	
	}
	
	public String updateCat(File modinfo)
	{				
		Gson gson = new Gson(); 
        try 
        {          
            String jsontext = Textreaders(modinfo);
            JsonArray jsona1=null;
            JsonObject jsono1=null;
            try
            {
	            jsona1 = gson.fromJson(jsontext, JsonArray.class);
	            jsono1 = gson.fromJson(jsona1.get(0), JsonObject.class); 
            }
            catch(Exception e)
            {
            	JsonObject jsonoa = gson.fromJson(jsontext, JsonObject.class);
            	jsono1 = jsonoa.get("modList").getAsJsonArray().get(0).getAsJsonObject();            	
            }          
       
        	try{
        		modName = jsono1.get("name").getAsString();
        	}
        	catch (Exception e){						
			}
        	try{
        		modVersion = jsono1.get("version").getAsString();
        	}
        	catch (Exception e){						
			}
        	try{
        		mcVersion = jsono1.get("mcversion").getAsString();
        		if(!mcVersion.equals("")&&!mcVersion.contains(Start.mcVersion))
        			JOptionPane.showMessageDialog(null, 
        					Read.getTextwith("modimport", "mcVersionT1")+mcVersion+
        					Read.getTextwith("modimport", "mcVersionT2")+Start.mcVersion+
        					Read.getTextwith("modimport", "mcVersionT3")+mcVersion+
        					Read.getTextwith("modimport", "mcVersionT4"), 
        					Read.getTextwith("modimport", "mcVersionTh"), JOptionPane.WARNING_MESSAGE); 	        		
        	}
        	catch (Exception e){						
			}
        	try{
        		description = stringBreak(jsono1.get("description").getAsString(), 58);	        		
        	}
        	catch (Exception e){						
			}
        	try{	
        		String neu ="";
    			JsonArray Autorena = jsono1.get("authorList").getAsJsonArray();
        		for (int i=0; i<Autorena.size(); i++)
        			neu += Autorena.get(i).getAsString() +", ";
        		if(neu.length()>0)
        			authors = neu.substring(0, neu.length()-2);
        	}
        	catch (Exception e){
        		try{
        			String neu ="";
        			JsonArray Autorena = jsono1.get("authors").getAsJsonArray();
	        		for (int i=0; i<Autorena.size(); i++)
	        			neu += Autorena.get(i).getAsString() +", ";
	        		if(neu.length()>0)
	        			authors = neu.substring(0, neu.length()-2);
	        	}
	        	catch (Exception e2){		        		
	        	}
			}
        	try{
        		website = jsono1.get("url").getAsString();
        	}
        	catch (Exception e){						
			}
        	try{
        		credits = stringBreak(jsono1.get("credits").getAsString(), 58);
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
        			requiredMods = neu.substring(0, neu.length()-2);
        			if((!Start.sent.contains(modName))&&(!requiredMods.equals("mod_MinecraftForge")))
        				JOptionPane.showMessageDialog(null, Read.getTextwith("modimport", "requiredModsText")+requiredMods, 
        					Read.getTextwith("modimport", "requiredModsTexth"), JOptionPane.INFORMATION_MESSAGE);
        		}
        	}
        	catch (Exception e){						
			}
        	try{
        		modLogo = jsono1.get("logoFile").getAsString().replace("\\", "/");
        	}
        	catch (Exception e){
			}	        	
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        } 
        return modName;
	}	
	
	private void updateList()
	{		
		 if(sport.exists()) //rechte Liste im Modinstaller aktualisieren
		 {
			 File[] imports = sport.listFiles();
			 for(File modi : imports)
			 {
				setListEntry(modi.getName().substring(0, modi.getName().lastIndexOf(".")));				
			 }
		 }				
	}
	private void setListEntry(String name){	
		 if(!men.rightListModel.contains("+ " +name))
		 {					
			men.nextButton.setEnabled(true);
			men.rightListModel.addElement("+ " +name);
		 }
	}
	
	private String stringBreak(String str, int maxLength) //Zeilenumbruch
	{
		List<String> strTmp = null;
		Pattern p = null;
		Matcher m = null;
		
		strTmp = new ArrayList<String>();
		p = Pattern.compile(".{1," + maxLength + "}$|.{1," + (maxLength - 1) + "}(\\.|,| )");		
		m = p.matcher(str);		 
		
		while(m.find()) {
		    strTmp.add(m.group());
		}
		
		String sn ="";		
		for(int i = 0; i < strTmp.size(); i++) {
		    sn+=strTmp.get(i)+"<br>";
		}
		return sn;
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

	public Import(String modname)
	{		
		File modinfo = new File(sportn.getAbsolutePath()+"/"+modname+"/mcmod.info");
		if(modinfo.exists())
		{
			updateCat(modinfo);
			make();
		}
	}		

	public void make()
	{
		final JFrame wi = new JFrame();
		wi.setTitle(modName);				
		wi.setResizable(true);	
		wi.setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());	
		
		JPanel p1 = new JPanel();
		boolean text = true;
		if(!modLogo.equals(""))
		{
			JLabel bild = new JLabel();		
			bild.setHorizontalAlignment(SwingConstants.CENTER);
			String pfad = extr.toString().replace("\\", "/")+"/"+modLogo;
			File f = new File(pfad);
			if(!f.exists())
			{
				if(modLogo.startsWith("mods"))
					modLogo = modLogo.substring(5);
					pfad = extr.toString().replace("\\", "/")+"/assets/"+modLogo;
					f = new File(pfad);				
			}
			if(f.exists())
			{
				bild.setIcon(new ImageIcon(f.getAbsolutePath()));
				text =false;
				p1.add(bild);
			}			
		}
		if(text)
		{
			JLabel head = new JLabel(modName);
			head.setFont(head.getFont().deriveFont(Font.BOLD,35));
			head.setHorizontalAlignment(SwingConstants.CENTER);
			p1.add(head);
		}
		
		DefaultTableModel model = new DefaultTableModel();       
		model.setColumnIdentifiers(new Object[] {"", ""});
	    model.insertRow(0, new Object[] {Read.getTextwith("modimport", "modVersion"), modVersion}); //Modversion
        model.insertRow(1, new Object[] {Read.getTextwith("modimport", "mcVersion"), mcVersion}); //Für Minecraft
        model.insertRow(2, new Object[] {Read.getTextwith("modimport", "description"), "<html><body>"+description+"</body></html>"}); //Beschreibung     
        model.insertRow(3, new Object[] {Read.getTextwith("modimport", "requiredMods"), requiredMods}); //Benötigte Mods
        model.insertRow(4, new Object[] {Read.getTextwith("modimport", "authors"), authors}); //Autoren
        model.insertRow(5, new Object[] {Read.getTextwith("modimport", "website"), website}); //Webseite
        model.insertRow(6, new Object[] {Read.getTextwith("modimport", "credits"), "<html><body>"+credits+"</body></html>"}); //Webseite        
		
        table = new JTable(model){  
			private static final long serialVersionUID = 1L;
			DefaultTableCellRenderer colortext=new DefaultTableCellRenderer();
            {
                colortext.setForeground(Color.decode("#9C2717"));
            }
            @Override
            public TableCellRenderer getCellRenderer(int arg0, int arg1) {
                return colortext;
            }
        };
       
        TableColumn col = table.getColumnModel().getColumn(0);
        col.setPreferredWidth(150);
        TableColumn col2 = table.getColumnModel().getColumn(1);
        col2.setPreferredWidth(400); 
        //table.setRowHeight(30);
        updateRowHeights();
        
        JSplitPane pSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT); 
        pSplit.add(p1, JSplitPane.TOP);
        pSplit.add(new JScrollPane(table), JSplitPane.BOTTOM);
        
        wi.add(pSplit);
        wi.setSize(550, 435);
        wi.setLocationRelativeTo(null);
        wi.setVisible(true);
        
        try
    	{
    		if(!Start.sent.contains(modName))
    		{
	        	String body = "Name=" + modName.replace("\'", "`") + "&" + 
	        	"MCVersion=" + mcVersion.replace("\'", "`") + "&" + 
	        	"ModVersion=" + URLEncoder.encode(modVersion.replace("\'", "`"), "UTF-8" ) + "&" + 
	        	"Requires=" + URLEncoder.encode(requiredMods.replace("\'", "`"), "UTF-8" )+ "&" + 
	        	"Description=" + description.replace("\'", "`")+ "&" + 
	        	"Web=" + URLEncoder.encode(website.replace("\'", "`"), "UTF-8" );
	        	Start.sent.add(modName);		        	
				new Download().post("http://www.minecraft-installer.de/api/imports.php", body);					
    		}
    	}
    	catch (Exception e){        		
    	}
	}
	
	private void updateRowHeights()
	{
	    try
	    {
	        for (int row = 0; row < table.getRowCount(); row++)
	        {
	            int rowHeight = table.getRowHeight();

	            for (int column = 0; column < table.getColumnCount(); column++)
	            {
	                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
	                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height)+5;
	            }

	            table.setRowHeight(row, rowHeight);
	        }
	    }
	    catch(ClassCastException e) {}
	}
}
