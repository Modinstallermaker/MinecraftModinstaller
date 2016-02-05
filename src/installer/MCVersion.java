package installer;

public class MCVersion 
{
	private int ID, SumAll, SumForge, stripMeta;
	private String Version;
	
	public int getID() {
		return ID;
	}	
	public int getSumAll() {
		return SumAll;
	}	
	public int getSumForge() {
		return SumForge;
	}	
	public String getVersion() {
		return Version;
	}
	public void setVersion(String Version) {
		this.Version = Version;
	}	
	public int getStripMeta(){
		return stripMeta;
	}
}
