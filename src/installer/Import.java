package installer;

import static installer.OP.copy;
import static installer.OP.del;
import static installer.OP.makedirs;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

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
	private boolean Modloader = Menu.isModloader;
	private File sport = new File(stamm + "Modinstaller/Import/");
	private File sportn = new File(stamm + "Modinstaller/Importn/");
	private File extr = new File(stamm + "Modinstaller/Importc/");
	private File extr2 = new File(stamm + "Modinstaller/Importc2/");
	private String modName = "", modVersion = "",
			mcVersion = "", description = "", authors = "",
			website = "", credits = "", requiredMods = "", modLogo = "";
	private MenuGUI men;

	public Import(File datei, MenuGUI men) 
	{		
		this.men = men;
		setImport();
		del(extr);
		del(extr2);
		makedirs(sportn);
		makedirs(extr);

		if (!Modloader) // Forge
		{
			try 
			{
				sucher(datei);
			} 
			catch (Exception e) {}

			if (modName.equals("")) 
			{
				description = Read.getTextwith("Import", "nomodinfo"); //keine Modinfos vorhanden
				website = Read.getTextwith("installer", "website")+"faq.php?id=nomodinfo";
				
				if (datei.isFile()) // Datei --> alles vor der Endung --> Titel --> Datei in mods Ordner
				{
					modName = datei.getName().substring(0, datei.getName().lastIndexOf("."));
				} 
				else // Ordner --> Ordnername --> Titel --> Ordner in mods Ordner
				{
					modName = datei.getName();
				}				
				File importf = new File(sport + "/" + modName + ".jar");
				try 
				{
					copy(datei, importf);
				}
				catch (Exception e) {}
			}
			else //Modinfos an Datenbank
			{
				try 
				{
					if (!Start.sentImportedModInfo.contains(modName)) 
					{
						String body = "Name=" + modName.replace("\'", "`") + "&"
								+ "MCVersion=" + mcVersion.replace("\'", "`") + "&"
								+ "ModVersion="	+ URLEncoder.encode(modVersion.replace("\'", "`"), "UTF-8")
								+ "&" + "Requires="	+ URLEncoder.encode(requiredMods.replace("\'", "`"),"UTF-8")
								+ "&" + "Description=" + description.replace("\'", "`")
								+ "&" + "Web=" + URLEncoder.encode(website.replace("\'", "`"), "UTF-8");
						Start.sentImportedModInfo.add(modName);
						new Postrequest("http://www.minecraft-installer.de/api/imports.php", body);
					}
				} 
				catch (Exception e) {}
			}
		} 
		else // Modloader
		{
			String name = datei.getName();
			File modspo = new File(sport.getAbsolutePath() + "/" + name);
			modspo.mkdirs();
			if (datei.isFile()) // Modloader Datei
			{
				String Dateiendung = datei.getName().substring(datei.getName().lastIndexOf("."));
				if (Dateiendung.equals(".jar") || Dateiendung.equals(".zip"))
				// Modloader ZIP oder JAR Datei --> Extrahieren und Kopieren in Minecraft.jar
				{
					try 
					{
						new Extract(datei, modspo);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				} 
				else // Modloader Datei unbekannt --> Kopieren in Minecraft.JAR
				{
					try 
					{
						copy(datei, new File(modspo.getAbsolutePath() + "/" + name));
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			} 
			else // Modloader Ordner --> Kopieren in Minecraft.JAR
			{
				try 
				{
					copy(datei, modspo);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			setListEntry(name);	
			modName = name;
			description = Read.getTextwith("seite2", "importm"); //Modloader Mod Info Text
			website = Read.getTextwith("installer", "website")+"faq.php?id=importm";
		}
		
		if (sport.exists()) // rechte Liste im Modinstaller aktualisieren
		{
			File[] imports = sport.listFiles();
			for (File modi : imports) 
			{
				setListEntry(modi.getName().substring(0, modi.getName().lastIndexOf(".")));				
			}		
		}
		make(new File(sport + "/" + modName + ".jar"));		
	}
	
	private void setImport()
	{
		men.modinstWebLnk.setVisible(false);
		men.modVersionL.setVisible(true);
		men.topIcon.setVisible(false);
		for (JLabel ic : men.ratIcons)
        	ic.setVisible(false);
	}

	public void sucher(File datei)
	{
		del(extr);
		if (datei.isFile()) // Wenn Datei
		{
			String Dateiendung = datei.getName().substring(datei.getName().lastIndexOf("."));
			if (Dateiendung.equals(".jar") || Dateiendung.equals(".zip")) // Wenn JAR oder ZIP Datei
			{
				if(Dateiendung.equals(".jar"))
				{
					String info = getModinfoStringFromJAR(datei); // Wenn Jar Datei direkt .modinfo enthält
					if (!info.equals("")) 
					{
						this.modName = updateCat(info);
						File importf = new File(sport + "/" + modName + ".jar");
						try 
						{
							copy(datei, importf);
						} 
						catch (Exception e) {}				
					}
				} 
				else 
				{
					try 
					{
						extr.mkdirs();
						new Extract(datei, extr);
						searchInfo(extr);
						try 
						{
							File[] jars = searchFile(extr, ".jar");
							for (int j = 0; j < jars.length; j++) 
							{
								del(extr2);
								new Extract(jars[j], extr2);
								searchInfo(extr2);
							}
						} 
						catch (Exception e) {}

						try 
						{
							File[] zips = searchFile(extr, ".zip");
							for (int z = 0; z < zips.length; z++) 
							{
								del(extr2);
								new Extract(zips[z], extr2);
								searchInfo(extr2);
							}
						} 
						catch (Exception e) {}
					} 
					catch (Exception e) {}
				}
			}
		} 
		else // Wenn Ordner
		{
			File[] jars = searchFile(datei, ".jar"); // In Ordner JAR Datei
			for (int j = 0; j < jars.length; j++)
				sucher(jars[j]);
			File[] zips = searchFile(datei, ".zip"); // In Ordner ZIP Datei
			for (int z = 0; z < zips.length; z++)
				sucher(zips[z]);
			searchInfo(datei);
		}	
	}

	private void searchInfo(File datei) 
	{
		for (File file : datei.listFiles()) // In Ordner Infodatei suchen
		{
			if (file.isFile()) 
			{
				if (file.getName().equals("mcmod.info")) 
				{
					String modname = updateCat(getModinfoStringFromFile(file));
					File path = new File(sportn.getAbsolutePath() + "/" + modname);
					path.mkdirs();
					try 
					{
						copy(datei, path);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					new Compress(path, new File(sport.getAbsolutePath() + "/" + modname + ".jar"));
				}
			} 
			else 
			{
				searchInfo(file);
			}
		}
	}

	public String getModinfoStringFromFile(File modinfo) 
	{
		new OP();
		try 
		{
			return OP.Textreaders(modinfo);
		} 
		catch (IOException e) {}
		return "";
	}

	public String getModinfoStringFromJAR(File jarfile) 
	{
		StringBuilder builder = new StringBuilder();
		InputStream in = null;
		String inputFile = "jar:file:/" + jarfile.getAbsolutePath() + "!/mcmod.info";
		if (inputFile.startsWith("jar:")) 
		{
			URL inputURL;
			try 
			{
				inputURL = new URL(inputFile);

				JarURLConnection conn = (JarURLConnection) inputURL.openConnection();
				in = conn.getInputStream();

				int ch;
				while ((ch = in.read()) != -1) {
					builder.append((char) ch);
				}
				in.close();
				return builder.toString();
			} 
			catch (Exception e) 
			{
				return "";
			}
		}
		else
			return "";
	}

	public String updateCat(String jsontext) 
	{
		Gson gson = new Gson();
		try 
		{
			JsonArray jsona1 = null;
			JsonObject jsono1 = null;
			try {
				jsona1 = gson.fromJson(jsontext, JsonArray.class);
				jsono1 = gson.fromJson(jsona1.get(0), JsonObject.class);
			} catch (Exception e) {
				JsonObject jsonoa = gson.fromJson(jsontext, JsonObject.class);
				jsono1 = jsonoa.get("modList").getAsJsonArray().get(0).getAsJsonObject();
			}

			try {
				modName = jsono1.get("name").getAsString();
			} catch (Exception e) {
			}
			try {
				modVersion = jsono1.get("version").getAsString();
			} catch (Exception e) {
			}
			try {
				modLogo = jsono1.get("logoFile").getAsString().replace("\\","/");
			} catch (Exception e) {
			}	
			try {
				mcVersion = jsono1.get("mcversion").getAsString();				
			} catch (Exception e) {
			}
			try {
				description = jsono1.get("description").getAsString();
			} catch (Exception e) {
			}
			try {
				String neu = "";
				JsonArray Autorena = jsono1.get("authorList").getAsJsonArray();
				for (int i = 0; i < Autorena.size(); i++)
					neu += Autorena.get(i).getAsString() + ", ";
				if (neu.length() > 0)
					authors = neu.substring(0, neu.length() - 2);
			} catch (Exception e) {
				try {
					String neu = "";
					JsonArray Autorena = jsono1.get("authors").getAsJsonArray();
					for (int i = 0; i < Autorena.size(); i++)
						neu += Autorena.get(i).getAsString() + ", ";
					if (neu.length() > 0)
						authors = neu.substring(0, neu.length() - 2);
				} catch (Exception e2) {
				}
			}
			try {
				website = jsono1.get("url").getAsString();
			} catch (Exception e) {
			}
			try {
				credits = jsono1.get("credits").getAsString();
			} catch (Exception e) {
			}
			try {
				String neu = "";
				JsonArray Benoetigta = jsono1.get("dependencies").getAsJsonArray();
				for (int i = 0; i < Benoetigta.size(); i++)
					neu += Benoetigta.get(i).getAsString() + ", ";
				if (neu.length() > 0) {
					requiredMods = neu.substring(0, neu.length() - 2);					
				}
			} catch (Exception e) {
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modName;
	}

	private void setListEntry(String name) {
		if (!men.rightListModel.contains("+ " + name)) {
			men.nextButton.setEnabled(true);
			men.rightListModel.addElement("+ " + name);
			men.rightList.setSelectedIndex(men.rightListModel.size()-1);
		}
	}

	public File[] searchFile(File ordner, String suche) {
		FileFinder ff = new FileFinder();
		ff.sucheDatei(suche, ordner, null);
		File[] fs = new File[ff.findings.size()];
		for (int i = 0; i < ff.findings.size(); i++) {
			fs[i] = ff.findings.get(i);
		}
		return fs;
	}

	public Import(String modName, MenuGUI men) {		
		this.modName = modName;
		this.men = men;
		setImport();
		File jarfile = new File(sport.getAbsolutePath() + "/" + modName + ".jar");
		if (jarfile.exists()) {
			String cont = getModinfoStringFromJAR(jarfile);
			if (!cont.equals(""))
				updateCat(cont);
			make(jarfile);
		}		
	}

	public void make(File jarfile) {
		boolean picfound = false;
		
		if (!modLogo.equals("")) 
		{
			JLabel bild = new JLabel();
			bild.setHorizontalAlignment(SwingConstants.CENTER);			
			InputStream in = null;
			try 
			{
				String inputFile = "jar:file:/" + jarfile.getAbsolutePath().replace("\\", "/") + "!/"+modLogo;			
				URL inputURL = new URL(inputFile);
				JarURLConnection conn = (JarURLConnection) inputURL.openConnection();
				in = conn.getInputStream();						
				BufferedImage bi = ImageIO.read(in);
				men.picture.setIcon(new ImageIcon(new ImageScaler().scaleImage(bi, new Dimension(390, 215))));
				in.close();	
				picfound = true;
			} 
			catch (Exception e){		
			}
		}
		
		if(!picfound)
		{
			men.picture.setIcon(new ImageIcon(this.getClass().getResource("src/mods.png")));
		}
		
		men.sizeLabel.setText(new OP().getSizeAsString(jarfile.length()));
		men.website = website;
		
		men.modNameLabel.setText(modName);
		men.modVersionL.setText("Mod v. "+modVersion);
		String text = "<html><body>";
		if (!mcVersion.equals("")&& !mcVersion.contains(Start.mcVersion))
		{
			text += Read.getTextwith("Import", "warning1")+mcVersion+"!</b><br><br>";
		}
		text += description + "<br><br>";
		if(!requiredMods.equals(""))
			text+="<b>"+Read.getTextwith("Import", "requiredMods")+"</b>: "+requiredMods+"<br><br>";
		if(!authors.equals(""))
			text+="<b>"+Read.getTextwith("Import", "authors")+"</b>: "+authors+"<br><br>";
		if(!credits.equals(""))
			text+="<b>"+Read.getTextwith("Import", "credits")+"</b>: "+credits+"<br><br>";
		text = text.substring(0, text.length()-8);
		
		men.modDescPane.setText(text);	
	}	
}
