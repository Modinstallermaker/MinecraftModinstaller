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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
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
 * Mod, Minecraft and Forge installation
 * 
 * @version 5.0
 * @author Dirk Lippke
 */

public class Install extends InstallGUI
{
	private static final long serialVersionUID = 1L;
	private String mineord = Start.mineord, stamm = Start.stamm, mcVersion = Start.mcVersion;	
	private boolean online = Start.online;
	private double mainVal=0.00;
	private File sport = new File(stamm, "Modinstaller");
	private File modsport = new File(mineord, "versions/Modinstaller");
	private ArrayList<Modinfo> mods;
	private boolean isModloader; 
	private static FileInputStream fis;

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
	
	/**
	 * Main installation thread
	 */
	public void installation()
	{
		new Thread() 
		{	
			@Override
			public void run() 
			{
				try 
				{
					mainState(mainVal += 1);	//1				
										
					//Setting restore point
					mainBarInf.setText(Read.getTextwith("Install", "main1"));
					try
					{	
						detBarInf.setText(Read.getTextwith("Install", "def1"));
						stateIcon.setIcon(new ImageIcon(getClass().getResource("src/restore2.png")));
						del(new File(sport, "Backup"));
						copy(modsport, new File(sport, "Backup"));
						mainState(mainVal += 2.0D);
						copy(new File(mineord, "mods"), new File(sport, "Backup/mods"));
				    }
					catch (Exception e)
					{
						mainState(mainVal += 2);	//3
						detBarInf.setText("Errorocde: S3x0a: " + String.valueOf(e));
						errors += getError(e) + " Errorcode: S3x0a\n\n";
					}
					
					mainState(mainVal += 2); //5	
					
					//Delete old files
					detBarInf.setText(Read.getTextwith("Install", "def2"));		
					  OP.del(new File(Install.this.sport, "Result"));
			          OP.del(new File(Install.this.sport, "Original"));
			          OP.del(new File(Install.this.mineord, "mods"));
			          OP.del(new File(Install.this.mineord, "coremods"));
			          OP.del(new File(Install.this.mineord, "config"));
			          OP.del(Install.this.modsport);
					
					mainState(mainVal += 1);	//6
					
					//Create new folders
					detBarInf.setText(Read.getTextwith("Install", "def3"));
					OP.makedirs(new File(Install.this.sport, "Result"));
			        OP.makedirs(new File(Install.this.sport, "Backup"));
			        OP.makedirs(Install.this.modsport);
					File mcVersionFolder = new File(mineord + "versions/"+mcVersion);
					mcVersionFolder.mkdirs();
					
					mainState(mainVal += 1); //7											
					
					//Installing new Minecraft version
					if(online)
					{	
						mainBarInf.setText(Read.getTextwith("Install", "main2"));
						
						//Downloading JSON file											
						File jsonFile = new File(mineord, "versions/"+mcVersion+"/"+mcVersion+".json");	
						if(!jsonFile.exists())
						{
							detBarInf.setText(Read.getTextwith("Install", "def4"));
							new Downloader("https://s3.amazonaws.com/Minecraft.Download/versions/"+mcVersion+"/"+mcVersion+".json", jsonFile).run();
						}
						
						//Downloading Minecraft JAR						
						File jarFile = new File(mineord, "versions/"+mcVersion+"/"+mcVersion+".jar");
						if(!jarFile.exists())
						{
							stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
							detBarInf.setText(Read.getTextwith("Install", "def5"));	
							
							Downloader dow = new Downloader("https://s3.amazonaws.com/Minecraft.Download/versions/"+mcVersion+"/"+mcVersion+".jar", jarFile);
							Thread t = new Thread(new Downloadstate(dow));
							t.start();							
							dow.run();	
							t.interrupt();							
						}						
					}
					
					mainState(mainVal += 2); //9
					
					//Copy and edit new Minecraft version in Modinstaller folder
					detBarInf.setText(Read.getTextwith("Install", "def6"));	
					copy(mcVersionFolder, modsport);
					
					//Rename JAR and JSON into "Modinstaller"
					File newJson = new File(modsport + "Modinstaller.json");
					rename(new File(modsport, mcVersion+".jar"), new File(modsport, "Modinstaller.jar"));
					rename(new File(modsport, mcVersion+".json"), newJson);
					
					mainState(mainVal += 1); //10	
					
					//Install required libraries for Minecraft
					mainBarInf.setText(Read.getTextwith("Install", "main3")); 					
					if(newJson.exists())
						installLibraries(newJson);	//+15= 25								
					
					//Extract Minecraft JAR, if Modinstaller is in modloader mode
					if(isModloader)
					{	
						mainBarInf.setText(Read.getTextwith("Install", "main4")); 
						detBarInf.setText("");
						stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/extract.png")));
						new Extract(new File(modsport, "Modinstaller.jar"), new File(sport + "Result/"));  
						mainState(mainVal += 10); //+10 =20 (Modloader has only one library JSON file)
					}
					
					//Document all installed mods
					writeLog();
				} 
				catch (Exception ex) 
				{
					detBarInf.setText("Errorcode: S3x01: " + String.valueOf(ex));
					errors += getError(ex) + " Errorcode: S3x01\n\n";
				}
				
				//Downloads all selected mod files
				if (online)
				{
					mainBarInf.setText(Read.getTextwith("Install", "main5"));
					downloadMods();
				}					
				
				//Install manual imported mods
				mainBarInf.setText(Read.getTextwith("Install", "main6"));
				importMods();
				
				mainState(mainVal += 10); 
				
				stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
				detBarInf.setText("");
				
				//If modloader mode, compress new mods into Minecraft JAR
				if(isModloader)
				{	
					mainBarInf.setText(Read.getTextwith("Install", "main7"));
					stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/compress.png")));
					new Compress(new File(sport, "Result/"), new File(modsport, "Modinstaller.jar"));
					
					mainState(mainVal += 5); //+5 =20, da keine 2 Libraries
				}			
				
				mainBarInf.setText(Read.getTextwith("Install", "main8"));
				
				//Set Minecraft JSON files
				detBarInf.setText(Read.getTextwith("Install", "def7"));
				setProfiles();				
				
				mainState(mainVal += 2);
				
				//Modify Minecraft Serverlist
				modifyServerlist();
				
				mainState(mainVal += 2);
				
				//Copy sound files
				detBarInf.setText(Read.getTextwith("Install", "def8"));
				File sound = new File(mineord, "assets/indexes/"+mcVersion+".json");
				File soundc = new File(mineord, "assets/indexes/Modinstaller.json");
				if(sound.exists())
				{
					try 
					{
						copy(sound, soundc);
					} 
					catch (Exception e) 
					{	
						detBarInf.setText("Errorocde: S3xSS: " + String.valueOf(e));
						errors += getError(e) + " Errorcode: S3xSS\n\n";
					}
				}
				
				mainState(mainVal += 1);
				
				startMCButton.setEnabled(true);
				
				if (!errors.equals("")) //show all Errors
				{
					new Error(errors);
					info.setText(Read.getTextwith("Install", "head2"));
					detBarInf.setText(Read.getTextwith("Install", "error"));				
				} 
				else  //show picture gallery
				{
					detBar.setVisible(false);
					mainBar.setVisible(false);
					banner.setVisible(false);
					detBarInf.setVisible(false);
					mainBarInf.setVisible(false);
					stateIcon.setVisible(false);
					
					for(int i=0; i<socialIcons.length; i++)
						socialIcons[i].setVisible(true);	
					startinfo.setVisible(true);
					stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/play.png")));							
					info.setText(Read.getTextwith("Install", "head1"));	
					Start.mol.askUser(true);
				}
			}
		}.start();
	}
	
	/**
	 * Downloads all selected Mods from the Internet
	 */
	private void downloadMods()
	{
		try 
		{				
			makedirs(new File(sport, "Mods"));
			
			int i = 1;
			double add = 50.0/(double)mods.size();
			for (Modinfo mod : mods) 
			{
				detBarInf.setText(Read.getTextwith("Install", "dow1a") + mod.getName() + "</b>"+Read.getTextwith("Install", "dow1b"));
				mainBarInf.setText(Read.getTextwith("Install", "main5") + "(" + i + "/" + this.mods.size() + ").");
				stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
				i++;
				String DownloadURL = "http://www.minecraft-installer.de/api/download3.php?id="+mod.getID(); //Downloadlink für ZIP Datei
								
				File ZIPFile= new File(sport, "Mods/"+ mod.getID()+".zip");				
				File ZIPExtract = new File(mineord);				
				if(isModloader)
					 ZIPExtract = new File(sport, "Result");
											
				try
				{	
					//Download mod only if the mod is not available on the client
					if(ZIPFile.length() != mod.getSize())
					{
						Downloader dow = new Downloader(DownloadURL, ZIPFile);									
						Thread t = new Thread(new Downloadstate(dow));
						t.start();
						dow.run();	
						t.interrupt();									
					}
					
					mainState(mainVal += add*0.8);	
					
					//Extract downloaded ZIP file
					detBarInf.setText(Read.getTextwith("Install", "dow2")+mod.getName());
					stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/extract.png")));													
					new Extract(ZIPFile, ZIPExtract); 
					
					mainState(mainVal += add*0.2);
				}
				catch (Exception ex)
				{
					detBarInf.setText("Errorocde: S3x04: " + String.valueOf(ex));
					errors += "Mod: "+mod.getName() + " " + mcVersion +"\nSource: "+DownloadURL+"\nFrom: "+ZIPFile.toString()+
							"\nTo: "+ZIPExtract.toString()+"\nException:\n"+ getError(ex) + "\nErrorcode: S3x04\n\n";
				}	
			} 							
			//75%
			if (!isModloader) //Forge mode
			{	
				//Install sepcial forge mods and libraries
				forgeInstallation(); //10
				mainState(mainVal += 9);
				
				//Install special Modinstaller Mods
				extraInstallation();
				mainState(mainVal += 1);
			}							
		}					 
		catch (Exception ex) 
		{
			detBarInf.setText("Errorocde: S3x04: " + String.valueOf(ex));
			errors += getError(ex) + " Errorcode: S3x04\n\n";
		}
	}
	
	/**
	 * Gets the Path of a Minecraft Library from an JsonObject
	 * @param jo The JsonObject that contains the libraries
	 * @return String of the libraries path
	 */
	private String getNativeString(JsonObject jo)
	{
		String add=null;
		boolean take = false;
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
		        for (JsonElement el : ja)
		        {
		          JsonObject obj = el.getAsJsonObject();
		          if (obj.has("action"))
		          {
		            String action = obj.get("action").getAsString();
		            if (action.equals("allow"))
		            {
		              if (obj.has("os"))
		              {
		                JsonObject oso = obj.get("os").getAsJsonObject();
		                if (oso.has("name"))
		                {
		                  take = false;
		                  String name = oso.get("name").getAsString();
		                  if (os == OperatingSystem.WINDOWS)
		                  {
		                    if (name.equals("windows")) {
		                      take = true;
		                    }
		                  }
		                  else if (os == OperatingSystem.OSX)
		                  {
		                    if (name.equals("osx")) {
		                      take = true;
		                    }
		                  }
		                  else if (os == OperatingSystem.LINUX) {
		                    if (name.equals("linux")) {
		                      take = true;
		                    }
		                  }
		                }
		                else
		                {
		                  take = true;
		                }
		              }
		              else
		              {
		                take = true;
		              }
		            }
		            else if (action.equals("disallow")) {
		              if (obj.has("os"))
		              {
		                JsonObject oso = obj.get("os").getAsJsonObject();
		                if (oso.has("name"))
		                {
		                  String name2 = oso.get("name").getAsString();
		                  if (os == OperatingSystem.WINDOWS)
		                  {
		                    if (name2.equals("windows")) {
		                      take = false;
		                    }
		                  }
		                  else if (os == OperatingSystem.OSX)
		                  {
		                    if (name2.equals("osx")) {
		                      take = false;
		                    }
		                  }
		                  else if (os == OperatingSystem.LINUX) {
		                    if (name2.equals("linux")) {
		                      take = false;
		                    }
		                  }
		                }
		              }
		            }
		          }
		        }
		      }
			else
				take = true;
			
			if(!take)
				add = "false";
		}
		catch (Exception e)
		{
			detBarInf.setText("Errorcode: S3xna: "+ String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xna\n\n";
		}
		System.out.println(jo.get("name").getAsString()+": "+add);
		return add;
	}		
	
	/**
	 * Installs Minecraft Forge files and libraries
	 */
	private void forgeInstallation()
	{
		mainBarInf.setText(Read.getTextwith("Install", "forge1"));  		
		
		new File(sport, "Forge/").mkdirs();
		
		//Downloading Forge JSON File
		detBarInf.setText(Read.getTextwith("Install", "forge2"));  
		stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));	
		File jsonFile = new File(sport+"Forge/"+mcVersion+".json");	
		del(jsonFile);
		new Downloader("http://files.minecraft-mods.de/installer/MCForge/versions/"+mcVersion+".json", jsonFile).run();
		
		//Installing Forge libraries
		if(jsonFile.exists())
			installLibraries(jsonFile);
				
		File jsonFile2 = new File(mineord, "versions/Modinstaller/Modinstaller.json");	
		try 
		{
			copy(jsonFile, jsonFile2);
		} 
		catch (Exception e) 
		{			
			detBarInf.setText("Errorcode: S3xfo: "+ String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xfo\n\n";
		}	
		
		//Delete META-INF folder
		detBarInf.setText(Read.getTextwith("Install", "forge3"));  
		for(int i=0; i<Start.allMCVersions.length; i++)
		{
			if(Start.allMCVersions[i].getVersion().equals(mcVersion))
			{
				if(Start.allMCVersions[i].getStripMeta()==1)
				{
					try 
					{
						File jar =new File(modsport, "Modinstaller.jar");
						File resx = new File(sport + "Resultx/");
						del(resx);
						new Extract(jar, resx, 50, 0);						
						del(jar);
						new Compress(resx, jar, 50, 50);
						del(resx);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}	
				}
			}		
		}
	}	
	
	/**
	 * Install required Minecraft libraries from JSON file
	 * @param jsonFile JsonFile with libraries
	 */
	private void installLibraries(File jsonFile)
	{		
		//Read JSON file
		Gson gson = new Gson(); 
		String content ="";
		try 
		{
			content = Textreaders(jsonFile);
		} 
		catch (Exception e) 
		{
			detBarInf.setText("Errorcode: S3xli: "+ String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xli\n\n";
		}
		
		//Set ID to Modinstaller
		JsonObject main = (JsonObject)gson.fromJson(content, JsonObject.class);
		main.addProperty("id", "Modinstaller");
		if (main.has("downloads")) 
		{
			main.remove("downloads");
		}		
		if(main.has("libraries") && online)
		{
			JsonArray arr = main.get("libraries").getAsJsonArray();
			
			double addi = 15.0/(double)arr.size();
			int u=1;
			//List all Libraries
			loop1 : for(JsonElement obj : arr)
			{				
				mainBarInf.setText(Read.getTextwith("Install", "lib1")+u+"/"+arr.size()+")");
				u++;
				
				JsonObject jo = obj.getAsJsonObject();				
				if(jo.has("name"))
				{
					//Calculate checksum
					List<String> checksums = new ArrayList<String>();
					if(jo.has("checksums"))
					{
						JsonArray ja = jo.get("checksums").getAsJsonArray();
						for (JsonElement e : ja)
							checksums.add(e.getAsString());
					}
					
					String con = jo.get("name").getAsString();					
					String add = getNativeString(jo);
					
					//Get Library Path
					Artifact artifact = null;
					if(add!=null)
					{
						if(add.equals("false"))
							continue loop1;
						
						artifact = new Artifact(con+":"+add);
					}
					else						
						artifact = new Artifact(con);
					
					//Generate Library Download URL
					String libURL = "https://libraries.minecraft.net/";
					if(jo.has("url"))
						libURL = jo.get("url").getAsString();
					libURL += artifact.getPath();
					
					File libPath = artifact.getLocalPath(new File(mineord+"libraries/"));
					if(!libPath.exists())
					{
						 detBarInf.setText(Read.getTextwith("Install", "lib2")+artifact.getDescriptor());
						 stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
						 Thread t2 = null;
						 try
						 {		
							 //Download library as packed file
							 File packFile = new File(libPath.getParentFile(), libPath.getName() + PACK_NAME);	
							 Downloader dow2 = new Downloader(libURL + PACK_NAME, packFile);
							 t2 = new Thread(new Downloadstate(dow2));
							 t2.start();
							 dow2.run();
							 t2.interrupt();	
							 
							 //Extract packed library
							 detBarInf.setText(Read.getTextwith("Install", "lib3")+artifact.getDescriptor());
							 stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/extract.png")));
							 f = new RandomAccessFile(packFile, "r");
							 byte[] b = new byte[(int)f.length()];
							 f.read(b);
							 f.close();
							 unpackLibrary(libPath, b);
							 del(packFile);
						 }									 
						 catch (Exception e2) //If no packed file available download unpacked file
						 {
							 t2.interrupt();
							 Thread t = null;							 
							 try
							 {							
								 Downloader dow = new Downloader(libURL, libPath);
								 t = new Thread(new Downloadstate(dow));
								 t.start();
								 dow.run();
								 t.interrupt();								 
							 }
							 catch (Exception e)
							 {	
								 t.interrupt();									 
								 detBarInf.setText("Errorcode: S3xli: "+ String.valueOf(e));
								 errors += con +": "+"\n" +getError(e) + " Errorcode: S3xli2\n\n";
							 }							 
						 }						
					}
					//Generate sha-1 checksums
					if(libPath.exists())
					{
						MessageDigest md;
						try 
						{
							md = MessageDigest.getInstance("SHA1");
						
					        fis = new FileInputStream(libPath);
					        byte[] dataBytes = new byte[1024];
					        
					        int nread = 0; 
					        
					        while ((nread = fis.read(dataBytes)) != -1) 
					        {
					        	md.update(dataBytes, 0, nread);
					        };
					
					        byte[] mdbytes = md.digest();
					       
					        StringBuffer sb = new StringBuffer("");
					        for (int i = 0; i < mdbytes.length; i++) 
					        {
					        	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
					        }
					        File shafile = new File(libPath.getParentFile(), libPath.getName()+".sha");
					        System.out.println(sb.toString());
					        Textwriters(shafile, sb.toString(), false);
						} 
						catch (Exception e) 
						{			
							e.printStackTrace();
						}
					}
					mainState(mainVal += addi);
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
			detBarInf.setText("Errorocde: S3xJS: " + String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xJS\n\n";
		}
	}
	
	/**
	 * Downloads and installs special mods for Minecraft Modinstaller
	 */
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
							if(!dowf.isDownloadSizeEqual())
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
	
	/**
	 * Documents all installed mods in a file
	 */
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
			detBarInf.setText("Errorocde: S3x00: " + String.valueOf(e));
			errors += getError(e) + " Errorcode: S3x00\n\n";
		}
	}
	
	/**
	 * Copies all manual imported mods into the designated folders 
	 */
	private void importMods()
	{
		File impf = new File(sport, "Import");
		if (impf.exists()) 
		{
			stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/import.png")));
			if (isModloader) 
			{
				try 
				{
					for (File modim : impf.listFiles())
						copy(modim, new File(sport, "Result"));
				} 
				catch (Exception e) 
				{
					detBarInf.setText("Errorocde: S3x05: " + String.valueOf(e));
					errors += getError(e) + " Errorcode: S3x05\n\n";
				}
			} 
			else 
			{
				try 
				{
					 for (File modim : impf.listFiles())
						 copy(impf, new File(mineord, "mods/" + modim.getName()));					
				} 
				catch (Exception e) 
				{
					detBarInf.setText("Errorocde: S3x06: " + String.valueOf(e));
					errors += getError(e) + " Errorcode: S3x06\n\n";
				}
			}
			File impo = new File(sport, "Importo");
			del(impo);
			try 
			{
				rename(impf, impo);
			} 
			catch (Exception e) {}
		}
	}
	
	/**
	 * Prepares Minecraft JSON files
	 */
	private void setProfiles()	
	{
		File profiles = new File(mineord, "launcher_profiles.json"); 	
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
				detBarInf.setText("Errorocde: S3xPR: " + String.valueOf(e));
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
				detBarInf.setText("Errorocde: S3xPRO1: " + String.valueOf(e));
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
			detBarInf.setText("Errorocde: S3xPRO2: " + String.valueOf(e));
			errors += getError(e) + " Errorcode: S3xPR02\n\n";
		}	
	}
	
	/**
	 * Modifies Minecraft Serverist servers.dat
	 */
	private void modifyServerlist()
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
					    oldserverentries = value.getValue(); //old serverlist enties			    
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
	
	/**
	 * Sets main state bar
	 * @param number Integer from 0 to 100
	 */
	private void mainState(double number)
	{
		mainBar.setValue((int) number);
	}

	/**
	 * Sets detailed state bar
	 * @param number Interger from 0 to 100
	 */
	public static void detState(double number)
	{
		detBar.setValue((int) number);
	}
}
