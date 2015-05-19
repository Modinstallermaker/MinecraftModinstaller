package installer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import layout.TableLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static installer.OP.*;

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
	private String stamm = Start.stamm;	
	private boolean Modloader =Menu.Modloader;
	private File sport = new File(stamm+"Modinstaller/Import/");
	private File extr = new File(stamm+"Modinstaller/Importc/");
	private File extr2 = new File(stamm+"Modinstaller/Importc2/");
	private String modName="", modVersion="", mcVersion="", description="", authors="", website="", credits="", requiredMods="", modLogo="";
	private JTable table;
	
	public Import(File datei)
	{	
		System.gc();
		del(extr);
		del(extr2);
		makedirs(sport);
		makedirs(extr);		
		
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
						copy(datei, sport);
					} catch (Exception e) {					
						e.printStackTrace();
					}
				}
			}
			else //Modloader Ordner importiert
			{
				try {
					copy(datei, sport);
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}
		}		
		if(updateCat())
		{			
			File importf = new File(stamm+"Modinstaller/Import/"+ modName +".jar");
			new Compress(extr, importf);
			Menu.nextButton.setEnabled(true);
			make();
		}
		else
		{
			if(datei.isFile())
				modName=datei.getName().substring(0, datei.getName().lastIndexOf("."));
			else
				modName=datei.getName();
			File importf = new File(stamm+"Modinstaller/Import/"+ modName +".jar");
			try {
				copy(datei, importf);
			} catch (Exception e) {}
		}
		Menu.rightListModel.addElement("+ " +modName);
		System.gc();
	}
	
	public void sucher(File datei)
	{	
		del(extr);
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
								del(extr2);
								new Extract(jars[j], extr2);
							}
						}
						catch (Exception e){}
					
						try {
							File[] zips = searchFile(extr, ".zip");
							for (int z=0; z<zips.length; z++)
							{
								del(extr2);
								new Extract(zips[z], extr2);
							}
						}
						catch (Exception e){}
						copy(extr2, extr);
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
	            String jsontext = Textreaders(modinfo[0]);
	    		
	            JsonArray jsona1 = gson.fromJson(jsontext, JsonArray.class);
	            JsonObject jsono1 = gson.fromJson(jsona1.get(0), JsonObject.class); 
	       
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
	        		description = stringBreak(jsono1.get("description").getAsString(), 60);	        		
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
	        		credits = jsono1.get("credits").getAsString();
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
	        			JOptionPane.showMessageDialog(null, Read.getTextwith("modimport", "requiredModsText")+requiredMods, Read.getTextwith("modimport", "requiredModsTexth"), JOptionPane.INFORMATION_MESSAGE);
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
	        return true;
		}
		else
			return false;
	}
	
	private String stringBreak(String str, int maxLength)
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

	public Import(String Modname)
	{
		File Mod = new File(stamm+"Modinstaller/Import/"+Modname+".jar");
		try {
			del(extr);
			new Extract(Mod, extr);
		} catch (Exception e) {	}
		if(updateCat())		
			make();
	}	

	public void make()
	{
		setTitle(modName);				
		setResizable(true);	
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());	
		 double size[][] = {{TableLayout.FILL}, // Columns
		            {120, TableLayout.FILL, 40}}; // Rows

		setLayout(new TableLayout(size));
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
				add(bild,  "0,0");
			}			
		}
		if(text)
		{
			JLabel head = new JLabel(modName);
			head.setPreferredSize(new Dimension(300, 50));
			head.setFont(Start.lcd.deriveFont(Font.BOLD,30));
			head.setHorizontalAlignment(SwingConstants.CENTER);
			add(head, "0,0");
		}
		
		DefaultTableModel model = new DefaultTableModel();       
		model.setColumnIdentifiers(new Object[] {"", ""});
	    model.insertRow(0, new Object[] {Read.getTextwith("modimport", "modVersion"), modVersion}); //Modversion
        model.insertRow(1, new Object[] {Read.getTextwith("modimport", "mcVersion"), mcVersion}); //Für Minecraft
        model.insertRow(2, new Object[] {Read.getTextwith("modimport", "description"), "<html><body>"+description+"</body></html>"}); //Beschreibung     
        model.insertRow(3, new Object[] {Read.getTextwith("modimport", "requiredMods"), requiredMods}); //Benötigte Mods
        model.insertRow(4, new Object[] {Read.getTextwith("modimport", "authors"), authors}); //Autoren
        model.insertRow(5, new Object[] {Read.getTextwith("modimport", "website"), website}); //Webseite
        model.insertRow(6, new Object[] {Read.getTextwith("modimport", "credits"), credits}); //Webseite        
		
        table = new JTable(model){           
			/**
			 * 
			 */
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
        add(new JScrollPane(table),  "0,1");   
        
        JButton close = new JButton(Read.getTextwith("modimport", "exit"));
        close.addActionListener(new ActionListener() { 
  	      public void actionPerformed(ActionEvent evt) { 
  	        dispose();
  	      }
  	    });
        add(close, "0,2");
        setSize(550, 435);
        setLocationRelativeTo(null);
        setVisible(true); 
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
