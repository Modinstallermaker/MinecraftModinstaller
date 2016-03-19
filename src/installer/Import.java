package installer;

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
import javax.swing.JOptionPane;
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
	private File isport = new File(Start.sport, "Import");
	private File isportn = new File(Start.sport, "Importn");
	private File isportc = new File(Start.sport, "Importc");
	private File isportc2 = new File(Start.sport, "Importc2");
	private File isportinfo = new File(Start.sport, "ImportInfo");
	
	private String modName = "", modVersion = "", mcVersion = "", description = "", authors = "", website = "", credits = "", requiredMods = "", modLogo = "";
	private MenuGUI men;

	public Import(File impFile, MenuGUI men) 
	{		
		this.men = men;
		setImport();
		OP.del(isportc);
		OP.del(isportc2);
		OP.makedirs(isportn);
		OP.makedirs(isportc);

		if (!Menu.isModloader) // Forge
		{
			try 
			{
				sucher(impFile);
			} 
			catch (Exception e) {}

			if (modName.equals("")) 
			{
				description = Read.getTextwith("Import", "nomodinfo"); //keine Modinfos vorhanden
				website = Read.getTextwith("installer", "website")+"faq.php?id=nomodinfo";
				
				if (impFile.isFile()) // Datei --> alles vor der Endung --> Titel --> Datei in mods Ordner
				{
					modName = impFile.getName().substring(0, impFile.getName().lastIndexOf("."));
				} 
				else // Ordner --> Ordnername --> Titel --> Ordner in mods Ordner
				{
					modName = impFile.getName();
				}				
				File importf = new File(isport, modName + ".jar");
				try 
				{
					OP.copy(impFile, importf);
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
			String impFileName = impFile.getName();
			this.modName = impFileName.substring(0, impFileName.lastIndexOf('.'));
			File modspo = new File(isport, modName);
			modspo.mkdirs();
			if (impFile.isFile()) // Modloader Datei
			{
				String Dateiendung = impFile.getName().substring(impFile.getName().lastIndexOf("."));
				if (Dateiendung.equals(".jar") || Dateiendung.equals(".zip"))
				// Modloader ZIP oder JAR Datei --> Extrahieren und Kopieren in Minecraft.jar
				{
					try 
					{
						new Extract(impFile, modspo);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				} 
				else // Modloader Datei unbekannt --> Kopieren in Minecraft.JAR
				{
					try 
					{
						OP.copy(impFile, new File(modspo, impFileName));
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
					OP.copy(impFile, modspo);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if (isport.exists()) // rechte Liste im Modinstaller aktualisieren
		{
			File[] imports = isport.listFiles();
			for (File modi : imports) 
			{
				if (!Menu.isModloader)
				{
					if (modi.isFile())
					{
						String name = modi.getName().substring(0, modi.getName().lastIndexOf("."));
						setListEntry(name);			
					}
				}
				else if (modi.isDirectory())
				{
					String name = modi.getName();
					setListEntry(name);			
				}					
			}		
		}
		make(new File(isport, modName + ".jar"));		
	}
	
	private void setImport()
	{
		men.modinstWebLnk.setVisible(false);
		men.modVersionL.setVisible(true);
		men.topIcon.setVisible(false);
		men.videoButton.setVisible(false);
		for (JLabel ic : men.ratIcons)
        	ic.setVisible(false);
	}

	public void sucher(File datei)
	{
		OP.del(isportc);
		if (datei.isFile()) // Wenn Datei
		{
			String Dateiendung = datei.getName().substring(datei.getName().lastIndexOf("."));
			if (Dateiendung.equals(".jar") || Dateiendung.equals(".zip")) // Wenn JAR oder ZIP Datei
			{
				if(Dateiendung.equals(".jar"))
				{
					if (getModinfoStringFromJAR(datei)) // Wenn Jar Datei direkt .modinfo enthält
					{
						File importf = new File(isport, modName + ".jar");
						try 
						{
							OP.copy(datei, importf);
						} 
						catch (Exception e) {}				
					}
					else
					{
						isportc.mkdirs();
						try {
							new Extract(datei, isportc);
							searchInfoFile(isportc, datei);
						} catch (Exception e) {							
							e.printStackTrace();
						}
						
					}
				} 
				else 
				{
					try 
					{
						isportc.mkdirs();
						new Extract(datei, isportc);
						searchInfoFile(isportc, null);
						try 
						{
							File[] jars = searchFile(isportc, ".jar");
							for (int j = 0; j < jars.length; j++) 
							{
								OP.del(isportc2);
								new Extract(jars[j], isportc2);
								searchInfoFile(isportc2, null);
							}
						} 
						catch (Exception e) {}

						try 
						{
							File[] zips = searchFile(isportc, ".zip");
							for (int z = 0; z < zips.length; z++) 
							{
								OP.del(isportc2);
								new Extract(zips[z], isportc2);
								searchInfoFile(isportc2, null);
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
			searchInfoFile(datei, null);
		}	
	}

	private void searchInfoFile(File datei, File jarfile) 
	{
		for (File file : datei.listFiles()) // In Ordner Infodatei suchen
		{
			if (file.isFile()) 
			{
				if (file.getName().endsWith(".info")&&!file.getName().startsWith("dependencies")) 
				{
					getModinfoStringFromFile(file, jarfile);
					File path = new File(isportn, modName);
					path.mkdirs();
					try 
					{
						OP.copy(datei, path);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					new Compress(path, new File(isport, modName + ".jar"));
				}
			} 
			else 
			{
				searchInfoFile(file, jarfile);
			}
		}
	}

	public boolean getModinfoStringFromFile(File modinfo, File jarfile) 
	{
		try 
		{
			String cont = OP.Textreaders(modinfo);
			if (!cont.equals(""))
			{
				return readModinfo(cont, jarfile);
			}				
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean getModinfoStringFromJAR(File jarfile) 
	{
		StringBuilder builder = new StringBuilder();
		
		String inputFile = "jar:file:/" + jarfile.getAbsolutePath() + "!/mcmod.info";	
		InputStream in = null;
		try 
		{
			URL inputURL = new URL(inputFile);

			JarURLConnection conn = (JarURLConnection) inputURL.openConnection();
			in = conn.getInputStream();

			int ch;
			while ((ch = in.read()) != -1) {
				builder.append((char) ch);
			}
		} 
		catch (Exception e) 
		{
		}
		finally
		{
			if(in!=null)
			{
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String cont = builder.toString();
		if (!cont.equals(""))
		{					
			return readModinfo(cont, jarfile);
		}
		return false;
	}

	public boolean readModinfo(String jsontext, File jarfile) 
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
				
				isportinfo.mkdirs();
				OP.Textwriter(new File(isportinfo, modName+".json"), jsontext, false);
			} catch (Exception e) {
			}
			try {
				modVersion = jsono1.get("version").getAsString();
			} catch (Exception e) {
			}
			try {
				modLogo = jsono1.get("logoFile").getAsString().replace("\\","/");
				if(modLogo.startsWith("/"))
					modLogo = modLogo.substring(1, modLogo.length());
				File modpicfile = new File(isportinfo, modName+".png");
				if(!modpicfile.exists()&&jarfile.exists())
				{
					modpicfile.createNewFile();
					InputStream in = null;
					BufferedImage bi = null;
					try 
					{
						String inputFile = "jar:file:/" + jarfile.getAbsolutePath() + "!/"+modLogo;			
						URL inputURL = new URL(inputFile);
						JarURLConnection conn = (JarURLConnection) inputURL.openConnection();
						in = conn.getInputStream();						
						bi = ImageIO.read(in);	
						ImageIO.write(bi, "png", modpicfile);	
						men.picture.setIcon(new ImageIcon(new ImageScaler().scaleImage(bi, new Dimension(400, 225))));					    	
					} 
					catch (Exception e){
						e.printStackTrace();
					}
					finally
					{
						if(in!=null)
							in.close();	
						if(bi!=null)
						{
							bi.flush();
							bi.flush();
						}
					}					
				}
				if(modpicfile.exists()&&modpicfile.length()>10)
				{
					BufferedImage bi = null;
					try {
						bi = ImageIO.read(modpicfile);
						men.picture.setIcon(new ImageIcon(new ImageScaler().scaleImage(bi, new Dimension(400, 225))));
					} catch (Exception e) {
						e.printStackTrace();
					}
					finally
					{
						if(bi !=null)
						{
							bi.flush();
							bi.flush();
						}
					}
				}
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
			return true;	
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;			
		}		
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
	    if (!Menu.isModloader) //Forge
	    {
	      File jarfile = new File(this.isport, modName + ".jar");
	      File modjsonfile = new File(isportinfo, this.modName+".json");     
	      if (jarfile.exists())
	      {
	        if (modjsonfile.exists()) 	        
	        {
	        	try {
					readModinfo(OP.Textreaders(modjsonfile), jarfile);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        else
	        {
	        	description = Read.getTextwith("Import", "nomodinfo");
				website = Read.getTextwith("installer", "website")+"faq.php?id=nomodinfo";	
	        }
	        make(jarfile);
	      }	      
	    }
	    else //Modloader
	    {
	      for (File f : isport.listFiles())
	      {
	        if (f.isDirectory()) {
	          make(f);
	        }
	      }
	    }	
	}

	public void make(File jarfile) {
		
		if (!modLogo.equals("")) 
		{
			JLabel bild = new JLabel();
			bild.setHorizontalAlignment(SwingConstants.CENTER);	
		}	
		File modpicfile = new File(isportinfo, this.modName+".png");		
		if(modpicfile.length()<10)
		{			
			men.picture.setIcon(new ImageIcon(this.getClass().getResource("src/mods.png")));
		}
		men.website = this.website;
		men.modNameLabel.setText(this.modName);
		if (!Menu.isModloader)
		{
			men.sizeLabel.setText(new OP().getSizeAsString(jarfile.length()));
			if(!modVersion.equals(""))
				men.modVersionL.setText("Mod v. " + this.modVersion);
			else
				men.modVersionL.setText("Forge Mod");
		}
		else
		{
			description = Read.getTextwith("Menu", "importm");
			men.website = Read.getTextwith("installer", "website") + "faq.php?id=importm";
			men.modVersionL.setText("Modloader Mod");
		}
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
