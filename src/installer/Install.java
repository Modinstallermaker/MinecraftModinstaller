package installer;

import static installer.OP.Textreaders;
import static installer.OP.Textwriters;
import static installer.OP.copy;
import static installer.OP.del;
import static installer.OP.getError;
import static installer.OP.makedirs;
import static installer.OP.optionReader;
import static installer.OP.optionWriter;
import static installer.OP.rename;
import static installer.OP.unpackLibrary;
import static installer.OP.checksumValid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ImageIcon;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Install extends InstallGUI
{
	private static final long serialVersionUID = 1L;
	private String mineord = Start.mineord, stamm = Start.stamm, mcVersion = Start.mcVersion;	
	private boolean online = Start.online;
	private double value = 0.00;
	private String modsport = mineord + "versions/Modinstaller/";
	private String sport = stamm + "Modinstaller/";	
	private ArrayList<Modinfo> mods;
	private boolean isModloader; 

    private static final String PACK_NAME = ".pack.xz";
	
	public static String errors="";
	private RandomAccessFile f;
	
	public Install(final ArrayList<Modinfo> mods, final boolean isModloader) 
	{
		this.mods=mods;
		this.isModloader=isModloader;
		GUI();
		installation();
	}
	
	public void installation()
	{
		new Thread() 
		{	
			@Override
			public void run() 
			{
				try 
				{
					status(value += 5);					
					
					try
					{	
						stat.setText(Read.getTextwith("seite3", "prog3")); //Wiederherstellungspunkt
						stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/restore2.png")));		
						del(new File(sport + "Backup"));					
						copy(new File(modsport), new File(sport + "Backup"));	
						copy(new File(mineord +"mods"), new File(sport + "Backup/mods"));	
					}
					catch (Exception e)
					{
						stat.setText("Errorocde: S3x0a: " + String.valueOf(e));
						errors += getError(e) + " Errorcode: S3x0a\n\n";
					}
					
					status(value += 5);
					
					stat.setText(Read.getTextwith("seite3", "prog1"));	//Löschen								
					del(new File(sport + "Result"));
					del(new File(sport + "Original"));					
					del(new File(mineord +"mods"));
					del(new File(mineord +"coremods"));
					del(new File(mineord +"config"));	
					del(new File(modsport));
					
					stat.setText(Read.getTextwith("seite3", "prog2"));            //Anlegen
					makedirs(new File(sport + "Result"));
					makedirs(new File(sport + "Backup"));	
					makedirs(new File(modsport));
					
					status(value += 5);
					
					File mcVersionFolder = new File(mineord + "versions/"+mcVersion);
					mcVersionFolder.mkdirs();
					
					if(online)
					{							
						File jsonFile = new File(mineord + "versions/"+mcVersion+"/"+mcVersion+".json");	
						if(!jsonFile.exists())							
							new Downloader("https://s3.amazonaws.com/Minecraft.Download/versions/"+mcVersion+"/"+mcVersion+".json", jsonFile).run();
											
						File jarFile = new File(mineord + "versions/"+mcVersion+"/"+mcVersion+".jar");
						if(!jarFile.exists())
						{
							stat.setText(Read.getTextwith("seite3", "installmc"));
							stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
							
							Downloader dow = new Downloader("https://s3.amazonaws.com/Minecraft.Download/versions/"+mcVersion+"/"+mcVersion+".jar", jarFile);
							Thread t = new Thread(new Downloadstate(dow, Install.this));
							t.start();							
							dow.run();	
							t.interrupt();							
						}
					}
					
					copy(mcVersionFolder, new File(modsport)); //von Versions Ordner in Modinstaller Ordner kopieren
					
					File newJson = new File(modsport + "Modinstaller.json");
					rename(new File(modsport + mcVersion+".jar"), new File(modsport + "Modinstaller.jar")); //Umbenennen in Modinstaller
					rename(new File(modsport + mcVersion+".json"), newJson);
					
					if(newJson.exists())
						installLibraries(newJson);	
					
					status(value += 5);					
					
					if(isModloader)  // Entpacken	
					{
						
						stat.setText(Read.getTextwith("seite3", "extra"));
						stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
						new Extract(new File(modsport+"Modinstaller.jar"), new File(sport + "Result/"));  
					}
					status(value += 5);
					
					writeLog();
					
					status(value += 5);		
				} 
				catch (Exception ex) 
				{
					stat.setText("Errorcode: S3x01: " + String.valueOf(ex));
					errors += getError(ex) + " Errorcode: S3x01\n\n";
				}
				
				if (online)																//Dateien herunterladen
				{			
					try 
					{	
						makedirs(new File(sport + "Mods")); // Ordner anlegen	
						
						for (Modinfo mod : mods) 
						{
							String infotext = Read.getTextwith("seite3", "prog8a") + mod.getName() + "</b>"+Read.getTextwith("seite3", "prog8b");
							stat.setText(infotext);
							stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
							
							String DownloadURL = "http://www.minecraft-installer.de/api/download3.php?id="+mod.getID(); //Downloadlink für ZIP Datei
											
							File ZIPFile= new File(sport + "Mods/"+ mod.getID()+".zip");
							File ZIPExtract = new File(sport + "Mods/"+ mod.getID()+"/");
														
							try
							{	
								if(ZIPFile.length() != mod.getSize()) // Nur herunterladen, wenn nicht auf PC verfügbar
								{
									del(ZIPFile);
									del(ZIPExtract);
									
									Downloader dow = new Downloader(DownloadURL, ZIPFile);									
									Thread t = new Thread(new Downloadstate(dow, Install.this));
									t.start();
									dow.run();	
									t.interrupt();									
								}
								
								status(value=90);								
								stat.setText(Read.getTextwith("seite3", "extra2")+mod.getName()+"..."); //Heruntergeladene ZIP Datei entpacken
								stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));	
								status(value+=2);
								
								new Extract(ZIPFile, ZIPExtract); 
								
								status(value=95);
								
								if(isModloader) //Modloader
									copy(ZIPExtract, new File(sport + "Result")); //in JAR Kompressionsordner
								else  //Forge
									copy(ZIPExtract, new File(mineord)); //in .mincraft Ordner
								
								status(value=100);
							}
							catch (Exception ex)
							{
								stat.setText("Errorocde: S3x04: " + String.valueOf(ex));
								errors += "Mod: "+mod.getName() + " " + mcVersion +"\nSource: "+DownloadURL+"\nFrom: "+ZIPFile.toString()+
										"\nTo: "+ZIPExtract.toString()+"\nException:\n"+ getError(ex) + "\nErrorcode: S3x04\n\n";
							}	
						} 	
						
						status(value=0);
						
						if (!isModloader) //Forge Modus
						{	
							forgeInstallation();
							extraInstallation();
						}					
					}					 
					catch (Exception ex) 
					{
						stat.setText("Errorocde: S3x04: " + String.valueOf(ex));
						errors += getError(ex) + " Errorcode: S3x04\n\n";
					}
			    }
							
				importMods();
				
				stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
				
				if(isModloader)     //Dateien in Minecraft JAR bei Modloader Modus komprimieren
				{					
					stat.setText(Read.getTextwith("seite3", "prog12"));	
					stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Komprimieren.png")));
					status(value += 5);
					new Compress(new File(sport + "Result/"), new File(modsport +"Modinstaller.jar"));  // Komprimieren
					status(value += 5);
				}			
				
				setProfiles();
				modifyServerlist();
				
				File sound = new File(mineord + "assets/indexes/"+mcVersion+".json");    //Sounddateien kopieren
				File soundc = new File(mineord + "assets/indexes/Modinstaller.json");
				if(sound.exists())
				{
					try 
					{
						copy(sound, soundc);
					} 
					catch (Exception e) 
					{	
						stat.setText("Errorocde: S3xSS: " + String.valueOf(e));
						errors += getError(e) + " Errorcode: S3xSS\n\n";
					}
				}

				startMCButton.setEnabled(true);
				
				if (!errors.equals("")) // alle Fehler anzeigen
				{
					new Error(errors);
					stat.setText(Read.getTextwith("seite3", "error2"));				
				} 
				else 
				{
					bar.setVisible(false);
					banner.setVisible(false);
					for(int i=0; i<socialIcons.length; i++)
						socialIcons[i].setVisible(true);
					stateIcon.setVisible(false);
					stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/play.png")));
					stat.setText("");
					startinfo.setVisible(true);
					info.setText(Read.getTextwith("seite3", "prog13"));	
					Start.mol.askUser(true);
				}
			}
		}.start();
	}
	
	private String getNativeString(JsonObject jo)
	{
		String add=null;
		OperatingSystem os = OperatingSystem.getCurrentPlatform();		
		try
		{		
			if (jo.has("natives")) 
			{
				JsonObject nat = jo.get("natives").getAsJsonObject();
								
				if (os == OperatingSystem.WINDOWS && nat.has("windows"))
					add = nat.get("windows").getAsString();
				else if (os == OperatingSystem.OSX && nat.has("osx"))
					add = nat.get("osx").getAsString();
				else if (os == OperatingSystem.LINUX && nat.has("linux"))
					add = nat.get("linux").getAsString();
				
				String arch = "";
			    if (os == OperatingSystem.WINDOWS)
			    {
					String parch = System.getenv("PROCESSOR_ARCHITECTURE");
					String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
					arch = parch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64") ? "64" : "32";
			    }
			    else 
			    {
			    	String parch = System.getProperty("os.arch");	
					if(parch.contains("64"))
						arch ="64";
					else
						arch="32";
			    }
				if(add!=null)
					add = add.replace("${arch}", arch);
			}
			if (jo.has("rules")) 
			{
				JsonArray ja = jo.get("rules").getAsJsonArray();
				for(JsonElement el : ja)
				{
					JsonObject obj = el.getAsJsonObject();
					if(obj.has("action"))
					{
						String action = obj.get("action").getAsString();
						if(action.equals("allow"))
						{
							if(obj.has("os"))
							{
								JsonObject oso = obj.get("os").getAsJsonObject();
								if(oso.has("name"))
								{
									String name = oso.get("name").getAsString();
									if (os == OperatingSystem.WINDOWS )
										if(!name.equals("windows"))
											add = "false";
									else if (os == OperatingSystem.OSX)
										if(!name.equals("osx"))
										add = "false";
									else if (os == OperatingSystem.LINUX)
										if(!name.equals("linux"))
											add = "false";
								}
							}
						}
						else if(action.equals("disallow"))
						{
							if(obj.has("os"))
							{
								JsonObject oso = obj.get("os").getAsJsonObject();
								if(oso.has("name"))
								{
									String name = oso.get("name").getAsString();
									if (os == OperatingSystem.WINDOWS)
										if(name.equals("windows"))
											add = "false";
									else if (os == OperatingSystem.OSX)
										if(name.equals("osx"))
										add = "false";
									else if (os == OperatingSystem.LINUX)
										if(name.equals("linux"))
											add = "false";
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			stat.setText("Errorcode: S3xna: "+ String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xna\n\n";
		}
		return add;
	}		
	
	private void forgeInstallation()
	{
		stat.setText(Read.getTextwith("seite3", "forge"));  
		stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));	
		
		new File(sport + "Forge/").mkdirs();
		
		File jsonFile = new File(sport+"Forge/"+mcVersion+".json");	
		del(jsonFile);
		new Downloader("http://files.minecraft-mods.de/installer/MCForge/"+mcVersion+".json", jsonFile).run();
		if(jsonFile.exists())
			installLibraries(jsonFile);
		
		File jsonFile2 = new File(mineord + "versions/Modinstaller/Modinstaller.json");	
		try 
		{
			copy(jsonFile, jsonFile2);
		} 
		catch (Exception e) 
		{			
			stat.setText("Errorcode: S3xfo: "+ String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xfo\n\n";
		}		
	}
	
	private void installLibraries(File jsonFile)
	{
		Gson gson = new Gson(); 
		String content ="";
		try 
		{
			content = Textreaders(jsonFile);
		} 
		catch (Exception e) 
		{
			stat.setText("Errorcode: S3xli: "+ String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xli\n\n";
		}
		
		JsonObject main = gson.fromJson(content, JsonObject.class);
		
		String forgeID ="forge";
		try
		{
			String idtext = main.get("id").getAsString().substring(6);
			int pos=0;
			for(int z=0; z<idtext.length(); z++)
				if(Character.isDigit(idtext.charAt(z)))
					pos= z;
			forgeID = idtext.substring(pos);
		}
		catch (Exception e)
		{
			forgeID ="forge";
			stat.setText("Errorcode: S3xID: "+ String.valueOf(e));
			errors +=  getError(e) + " Errorcode: S3xID\n\n";
		}
			
		main.addProperty("id", "Modinstaller");
				
		if(main.has("libraries") && online)
		{
			JsonArray arr = main.get("libraries").getAsJsonArray();
			
			loop1 : for(JsonElement obj : arr)
			{
				JsonObject jo = obj.getAsJsonObject();				
				if(jo.has("name"))
				{
					List<String> checksums = new ArrayList<String>();
					if(jo.has("checksums"))
					{
						JsonArray ja = jo.get("checksums").getAsJsonArray();
						for (JsonElement e : ja)
							checksums.add(e.getAsString());
					}
					
					String con = jo.get("name").getAsString();					
					String add = getNativeString(jo);
					
					Artifact artifact = null;
					if(add!=null)
					{
						if(add.equals("false"))
							continue loop1;
						
						artifact = new Artifact(con+":"+add);
					}
					else						
						artifact = new Artifact(con);
					
					String libURL = "https://libraries.minecraft.net/";
					if(jo.has("url"))
						libURL = jo.get("url").getAsString();
					libURL += artifact.getPath();
					
					System.out.println(forgeID);
					if(libURL.contains(forgeID))
						 libURL = "http://files.minecraft-mods.de/installer/MCForge/"+mcVersion+".jar";
					
					File libPath = artifact.getLocalPath(new File(mineord+"libraries/"));
					if(!libPath.exists())
					{
						 stat.setText("Downloading library: "+artifact.getDescriptor());
						 stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
						 Thread t = null;
						 try
						 {							
							 Downloader dow = new Downloader(libURL, libPath);
							 t = new Thread(new Downloadstate(dow, Install.this));
							 t.start();
							 dow.run();
							 t.interrupt();								 
						 }
						 catch (Exception e)
						 {	
							 t.interrupt();	
							 Thread t2 = null;
							 try
							 {			
								 File packFile = new File(libPath.getParentFile(), libPath.getName() + PACK_NAME);	
								 Downloader dow2 = new Downloader(libURL + PACK_NAME, packFile);
								 t2 = new Thread(new Downloadstate(dow2, Install.this));
								 t2.start();
								 dow2.run();
								 t2.interrupt();	
								 
								 stat.setText("Exracting library: "+artifact.getDescriptor());
								 stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
								 f = new RandomAccessFile(packFile, "r");
								 byte[] b = new byte[(int)f.length()];
								 f.read(b);
								 f.close();
								 unpackLibrary(libPath, b);
								 del(packFile);
								 checksumValid(libPath, checksums);
							 }									 
							 catch (Exception e2)
							 {
								 t2.interrupt();
								 stat.setText("Errorcode: S3xli: "+ String.valueOf(e2));
								 errors += con +": "+"\n" +getError(e2) + " Errorcode: S3xli2\n\n";
							 }
						 }
					}
				}
			}
		}
		
		try 
		{
			Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			Textwriters(jsonFile, gson2.toJson(main), false);
		} 
		catch (IOException e) 
		{
			stat.setText("Errorocde: S3xJS: " + String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xJS\n\n";
		}
	}
	
	private void extraInstallation()
	{		
		try 
		{		
			String sourceurl = "http://files.minecraft-mods.de/installer/Extra/downloadsrc.txt";
			File fileurl = new File(sport + "downloadsrc.txt");
			
			new Downloader(sourceurl, fileurl).run();
			
			new OP();
			String cont = OP.Textreaders(fileurl);
			String[] sp1 = cont.split(";;;;");
			for(String s1:  sp1)
			{
				String[] sp2 = s1.split(";;");
				if(sp2[0].equals(mcVersion)||sp2[0].equals("all"))
				{
					for (int i=1; i<sp2.length; i++)
					{
						File exra = new File(sport + "Extra.zip"); 				
						Downloader dowf = new Downloader(sp2[i], exra);						
						try 
						{
							if(!dowf.isDownloadSizeEqual())  //Extra Datei herunterladen
							{				
								dowf.run();
							}			
							try
							{
								new Extract(exra, new File(mineord));
							}
							catch(Exception ex)
							{	
								ex.printStackTrace();
							}
						} 
						catch (Exception ex) 
						{
							ex.printStackTrace();
						}
					}
				}
			}				
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}	
	}
	
	private void writeLog()
	{
		try
		{
			String mode="Forge";
			if(isModloader==true) mode="Modloader";
		
			optionWriter("slastmc", optionReader("lastmc"));
			optionWriter("lastmc", mcVersion);					
			optionWriter("slastmode", optionReader("lastmode"));
			optionWriter("lastmode", mode);	
			optionWriter("changed", "true");	
								
			if(mods.size()!=0)
			{
				optionWriter("slastmods", optionReader("lastmods"));
				String modn="";
				for (Modinfo mod : mods)
				{
					modn+=mod.getID()+";;";
				}
				if(modn.endsWith(";;"))
					modn = modn.substring(0, modn.length()-2);
				optionWriter("lastmods", modn);
			}
			else
			{
				optionWriter("slastmods", "n/a");
				optionWriter("lastmods", "n/a");
			}						
		}
		catch (Exception e)	
		{
			stat.setText("Errorocde: S3x00: " + String.valueOf(e));
			errors += getError(e) + " Errorcode: S3x00\n\n";
		}
	}
	
	private void importMods()
	{
		File impf = new File(stamm + "Modinstaller/Import/");
		if (impf.exists()) 
		{
			stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/import.png")));  // Importiertes kopieren
			if (isModloader) 
			{
				try 
				{
					for (File modim : impf.listFiles())
						copy(modim, new File(sport + "Result/"));
				} 
				catch (Exception e) 
				{
					stat.setText("Errorocde: S3x05: " + String.valueOf(e));
					errors += getError(e) + " Errorcode: S3x05\n\n";
				}
			} 
			else 
			{
				try 
				{
					copy(impf, new File(mineord + "mods/"));
				} 
				catch (Exception e) 
				{
					stat.setText("Errorocde: S3x06: " + String.valueOf(e));
					errors += getError(e) + " Errorcode: S3x06\n\n";
				}
			}
			File impo = new File(stamm + "Modinstaller/Importo/");
			del(impo);
			try 
			{
				rename(impf, impo);
			} 
			catch (Exception e) {}
		}
	}
	
	private void setProfiles() //Minecraft Launcher: JSON Datei präparieren: Profil Modinstaller einstellen	
	{
		File profiles = new File(mineord + "launcher_profiles.json"); 	
		boolean emty = false;
		if(!profiles.exists())
		{	
			emty=true;
			try 
			{
				profiles.createNewFile();				
			} 
			catch (IOException e) 
			{
				stat.setText("Errorocde: S3xPR: " + String.valueOf(e));
				errors += getError(e) + " Errorcode: S3xPR\n\n";
			}
		}
		
		Gson gson = new Gson(); 
	          
    	JsonObject jfile = null;
    	JsonObject jprofiles = null;
    	
    	if(emty||profiles.length()<10)
    	{
    		 jfile = new JsonObject();
    		 jprofiles = new JsonObject();
    	}
    	else
    	{
    		try 
    	    {
    			String jsontext = Textreaders(profiles);
	    		jfile = gson.fromJson(jsontext, JsonObject.class); 	  
	    		if(jfile.has("profiles"))
	    			jprofiles = jfile.get("profiles").getAsJsonObject();	
	    		else
	    			jprofiles = new JsonObject();
    	    }
		    catch (Exception e)
		    {
				jfile = new JsonObject();
				jprofiles = new JsonObject();
				stat.setText("Errorocde: S3xPRO1: " + String.valueOf(e));
				errors += getError(e) + " Errorcode: S3xPR01\n\n";
		    }	
    	}
    	
    	JsonObject sub = new JsonObject();
        sub.addProperty("name", "Modinstaller");
        sub.addProperty("lastVersionId", "Modinstaller");	
        
    	jprofiles.add("Modinstaller", sub);
    	
        jfile.add("profiles", jprofiles);	
        
        jfile.addProperty("selectedProfile", "Modinstaller");
        try 
        {
        	Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			Textwriters(profiles, gson2.toJson(jfile), false);
		} 
        catch (IOException e) 
        {
			stat.setText("Errorocde: S3xPRO2: " + String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xPR02\n\n";
		}	
	}
	
	private void modifyServerlist()  //Serverliste modifizieren
	{
		File jsonfile = new File(stamm+"Modinstaller/serverlist.json");
		try 
		{				
			new Downloader("http://www.minecraft-installer.de/api/serverlist.json", jsonfile).run();
		} 
		catch (Exception e) 
		{				
			e.printStackTrace();
		} 
		if(jsonfile.exists()&& jsonfile.length()>2)
		{
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
		        JsonArray jsona1 = gson.fromJson(jsontext, JsonArray.class);
		        
		        loopx: for(int i=0; i<jsona1.size(); i++)
		        {
		        	String Servername="", Serverip="", Servericon="";  
			        JsonObject jsono3 = jsona1.get(i).getAsJsonObject();		      
			        if(jsono3.has("name"))
			        	Servername = jsono3.get("name").getAsString();
			        if(jsono3.has("ip"))
			        	Serverip = jsono3.get("ip").getAsString();
			        if(jsono3.has("icon"))
			        	Servericon = jsono3.get("icon").getAsString();	       	
					
			        CompoundMap serverentry = new CompoundMap();
					serverentry.put(new StringTag("name", Servername));				
					serverentry.put(new StringTag("ip", Serverip));	
					serverentry.put(new StringTag("icon", Servericon));	
					
					for(int j=0; j <oldserverentries.size(); j++)
						if(oldserverentries.get(j).getValue().get("ip").getValue().equals(Serverip))
							continue loopx;
					
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
				
				del(jsonfile);
			} 
			catch (IOException e1) 
			{			
				e1.printStackTrace();				
			}	
		}		
	}

	public void status(double zahl) // Statusbar einstellen
	{
		bar.setValue((int) zahl);
		bar.setStringPainted(true);
	}
}
