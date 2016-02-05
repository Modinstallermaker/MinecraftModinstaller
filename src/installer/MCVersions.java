package installer;

import java.awt.Cursor;
import java.awt.event.ItemEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Dirk
 */
public class MCVersions extends javax.swing.JFrame implements MouseWheelListener 
{
	private static final long serialVersionUID = 1L;
	
	private boolean onlyForge=true;
	private int pos = 0;
	private String[] MCVersionStr = new String[3];
	private boolean[] isInstalled = new boolean[3];
	private Modinfo[] modlist, downloadlist;
	private ArrayList<String> offlineList;
	private Cursor curs1 = new Cursor(Cursor.HAND_CURSOR);
	private Menu menu =null;
	
	public MCVersions(Modinfo[] modlist, Modinfo[] downloadlist, ArrayList<String> offlineList) 
    {
    	this.modlist = modlist;
    	this.downloadlist=downloadlist;
    	this.offlineList=offlineList;
        initComponents();
        fillComponents();
    }
    
    public MCVersions(Modinfo[] modlist, Modinfo[] downloadlist, ArrayList<String> offlineList, Menu menu) {
		this.menu = menu;
		this.modlist = modlist;
    	this.downloadlist=downloadlist;
    	this.offlineList=offlineList;
    	menu.setEnabled(false);
        initComponents();
        fillComponents();
	}
                     
    private void fillComponents() 
    {    	
		int verg= 2;
		int posp = pos;
		MCVersion[] MCVersionsx = Start.allMCVersions;
		if(onlyForge)
			MCVersionsx = Start.forgeMCVersions;
		
		boolean inside = false;
		if(pos==0)
			text_rightb.setEnabled(false);
		else
			text_rightb.setEnabled(true);
		
		if(MCVersionsx == null || MCVersionsx.length<1 || !Start.online) //Offline
		{
			if(offlineList.size()==0)
			{
				setAlwaysOnTop(false);
				JOptionPane.showMessageDialog(null, "No Minecraft Version is found. Please install at least one Minecraft Version manually.");
				System.exit(0);
			}
			else
			{
				MCVersionsx = new MCVersion[offlineList.size()];
			}
		}
		
		if(MCVersionsx.length==1)
		{
			verg = 1;
			text[0].setEnabled(false);
			text[2].setEnabled(false);
		}
		else if(MCVersionsx.length==2)
		{
			text[2].setEnabled(false);
		}
		
		for (int i=MCVersionsx.length-1; i>=0; i--)
		{		
			if(MCVersionsx[i]==null)
			{
				MCVersionsx[i] = new MCVersion();
				MCVersionsx[i].setVersion(offlineList.get(i));
				forge_only.setVisible(false);
			}
			if(posp>0)
			{
				posp--;
				continue;
			}
			if(verg<0)
				break;			
					
			String mcVersion = MCVersionsx[i].getVersion();
			String installt = Read.getTextwith("MCVersions", "t2");
			if(offlineList.contains(mcVersion))
			{
				installt = Read.getTextwith("MCVersions", "t3");
				isInstalled[verg] = true;		
			}
			else
			{
				isInstalled[verg] = false;				
			}
			MCVersionStr[verg] = mcVersion;
			
			int sum = MCVersionsx[i].getSumAll();
			String sp = Read.getTextwith("MCVersions", "t4a");
			if(sum>1)
				sp+=Read.getTextwith("MCVersions", "t4b");
			
			String quan = String.valueOf(sum);		
			String ut = installt+" "+Read.getTextwith("MCVersions", "t1");
			if(!Start.online)
			{
				quan = "Offline";
				ut = "Import mods";
			}
			
			String stext = "<html><center><b><span style='font-size:16px'>"+mcVersion+
					"</span></b><br> <br> <b><span style='font-size:12px'>"+quan+
					"</b></b><br>"+sp+"<br><br><i>"+ut+"</i></center></html>";
			
			if(verg==1) //Mitte
			{
				stext = "<html><center><b><span style='font-size:22px'>"+mcVersion+
						"</span></b><br> <br> <b><span style='font-size:18px'>"+quan+
						"</b></b><br>"+sp+"<br> <br><i><span style='font-size:10px'>"+
						ut+"</span></i></center></html>";				
			}
			if(verg==0 && i==0)
				inside=true;
			
			text[verg].setText(stext);
			verg--;
		}
		if(inside || MCVersionsx.length<4)
			text_leftb.setEnabled(false);
		else
			text_leftb.setEnabled(true);
	}

	private void initComponents() 
	{
        panel_top = new javax.swing.JPanel();
        question_t = new javax.swing.JLabel();
        pannel_bottom = new javax.swing.JPanel();
        forge_only = new javax.swing.JCheckBox();
        panel_middle = new javax.swing.JPanel();
        panel_rightb = new javax.swing.JPanel();
        text_rightb = new javax.swing.JLabel();
        panel_leftb = new javax.swing.JPanel();
        text_leftb = new javax.swing.JLabel();
        panel_centermenu = new javax.swing.JPanel();
        panel_left = new javax.swing.JPanel();
        panel_right = new javax.swing.JPanel();
        for(int i=0; i<3; i++)
        	text[i] = new javax.swing.JLabel();
        panel_center = new javax.swing.JPanel();     
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(550, 290));
        setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
        
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
				if(menu==null)
					System.exit(0); 
				else
				{
					menu.setEnabled(true);
					menu.setVisible(true);
				}
			}
			
			public void windowClosed(WindowEvent e)
			{
				if(menu!=null)
				{
					menu.setEnabled(true);
					menu.setVisible(true);
				}
			}
		});         

        question_t.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        question_t.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        question_t.setText(Read.getTextwith("MCVersions", "t5"));
        question_t.setToolTipText("");

        javax.swing.GroupLayout panel_topLayout = new javax.swing.GroupLayout(panel_top);
        panel_top.setLayout(panel_topLayout);
        panel_topLayout.setHorizontalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(question_t, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panel_topLayout.setVerticalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(question_t, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
        );

        forge_only.setSelected(true);
        forge_only.setText(Read.getTextwith("MCVersions", "t6"));
        forge_only.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        forge_only.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                forge_onlyStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pannel_bottomLayout = new javax.swing.GroupLayout(pannel_bottom);
        pannel_bottom.setLayout(pannel_bottomLayout);
        pannel_bottomLayout.setHorizontalGroup(
            pannel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(forge_only, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pannel_bottomLayout.setVerticalGroup(
            pannel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(forge_only, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
        );

        panel_middle.setLayout(new java.awt.BorderLayout());

        text_rightb.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        text_rightb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text_rightb.setText(">>");
        text_rightb.addMouseWheelListener(this);
        text_rightb.setCursor(curs1);	
        text_rightb.setEnabled(false);
        text_rightb.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                text_rightbMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_rightbMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_rightbLayout = new javax.swing.GroupLayout(panel_rightb);
        panel_rightb.setLayout(panel_rightbLayout);
        panel_rightbLayout.setHorizontalGroup(
            panel_rightbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_rightb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panel_rightbLayout.setVerticalGroup(
            panel_rightbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_rightb, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_middle.add(panel_rightb, java.awt.BorderLayout.LINE_END);

        text_leftb.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        text_leftb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text_leftb.setText("<<");
        text_leftb.setCursor(curs1);
        text_leftb.addMouseWheelListener(this);
        text_leftb.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                text_leftbMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_leftbMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_leftbLayout = new javax.swing.GroupLayout(panel_leftb);
        panel_leftb.setLayout(panel_leftbLayout);
        panel_leftbLayout.setHorizontalGroup(
            panel_leftbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_leftb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panel_leftbLayout.setVerticalGroup(
            panel_leftbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_leftb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_middle.add(panel_leftb, java.awt.BorderLayout.LINE_START);

        panel_centermenu.setLayout(new java.awt.BorderLayout());

        text[0].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text[0].setCursor(curs1);
        text[0].addMouseWheelListener(this);
        text[0].addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                text_leftMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_leftMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_leftLayout = new javax.swing.GroupLayout(panel_left);
        panel_left.setLayout(panel_leftLayout);
        panel_leftLayout.setHorizontalGroup(
            panel_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text[0], javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panel_leftLayout.setVerticalGroup(
            panel_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text[0], javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_centermenu.add(panel_left, java.awt.BorderLayout.LINE_START);

        text[2].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text[2].setCursor(curs1);	
        text[2].addMouseWheelListener(this);
        text[2].addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                text_rightMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_rightMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_rightLayout = new javax.swing.GroupLayout(panel_right);
        panel_right.setLayout(panel_rightLayout);
        panel_rightLayout.setHorizontalGroup(
            panel_rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text[2], javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panel_rightLayout.setVerticalGroup(
            panel_rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text[2], javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_centermenu.add(panel_right, java.awt.BorderLayout.LINE_END);

        text[1].setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        text[1].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text[1].setCursor(curs1);	
        text[1].addMouseWheelListener(this);
        text[1].addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {            	
                text_centerMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_centerMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_centerLayout = new javax.swing.GroupLayout(panel_center);
        panel_center.setLayout(panel_centerLayout);
        panel_centerLayout.setHorizontalGroup(
            panel_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text[1], javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
        );
        panel_centerLayout.setVerticalGroup(
            panel_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text[1], javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_centermenu.add(panel_center, java.awt.BorderLayout.CENTER);

        panel_middle.add(panel_centermenu, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pannel_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_top, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_middle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_middle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pannel_bottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }                       
	 
	//Versions hover
	private void text_rightMouseEntered(java.awt.event.MouseEvent evt) {
	}   
	
    private void text_centerMouseEntered(java.awt.event.MouseEvent evt) {   
    }                                        

    private void text_leftMouseEntered(java.awt.event.MouseEvent evt) { 
    }                                      

    //Button hover
    private void text_leftbMouseEntered(java.awt.event.MouseEvent evt) {
    }                                       
    
    private void text_rightbMouseEntered(java.awt.event.MouseEvent evt) {                                         
    	
    }

    private void text_leftMouseClicked(java.awt.event.MouseEvent evt) {    
    	if(text[0].isEnabled())
    	{
	    	Start.mcVersion = MCVersionStr[0];
	    	openMenu();
    	}
    }                                      

    private void text_centerMouseClicked(java.awt.event.MouseEvent evt) {   
    	if(text[1].isEnabled())
    	{
	    	Start.mcVersion = MCVersionStr[1];
	    	openMenu();  
    	}
    }                                        

    private void text_rightMouseClicked(java.awt.event.MouseEvent evt) {      
    	if(text[2].isEnabled())
    	{
	    	Start.mcVersion = MCVersionStr[2];    	
	    	openMenu();
    	}
    }
    
    private void openMenu()
    {
    	if(menu!=null)
    	{
    		menu.changeVersion();
    		menu.setEnabled(true);
    	}
    	else
    	{
    		new Menu(modlist, downloadlist, offlineList);    	        	
    	}
    	dispose();
    }

    private void text_leftbMouseClicked(java.awt.event.MouseEvent evt) {
    	if(text_leftb.isEnabled())
    		back();
    }
    
    private void text_rightbMouseClicked(java.awt.event.MouseEvent evt) {   
    	if(text_rightb.isEnabled())
    		next();
    }    
    
    private void next()
    {
    	if(pos>0)
		{
			pos--;
			fillComponents();
		}
    }
    
    private void back()
    {
    	int anz;
    	if(onlyForge)
    		anz=Start.forgeMCVersions.length;
    	else
    		anz = Start.allMCVersions.length;
        if(pos<anz-3)
        {
	    	pos++;
	        fillComponents();
        }
    }
    
    @Override
	public void mouseWheelMoved(MouseWheelEvent evt) 
    {
		int notches = evt.getWheelRotation();
		if (notches < 0) 
			next();	  
		else 
			back();	
	}                               

    private void forge_onlyStateChanged(ItemEvent evt) {    
    	pos=0;
    	onlyForge =!onlyForge;
    	fillComponents();
    }    

    // Variables declaration - do not modify                     
    private javax.swing.JCheckBox forge_only;
    private javax.swing.JPanel panel_center;
    private javax.swing.JPanel panel_centermenu;
    private javax.swing.JPanel panel_left;
    private javax.swing.JPanel panel_leftb;
    private javax.swing.JPanel panel_middle;
    private javax.swing.JPanel panel_right;
    private javax.swing.JPanel panel_rightb;
    private javax.swing.JPanel panel_top;
    private javax.swing.JPanel pannel_bottom;
    private javax.swing.JLabel question_t;
    private javax.swing.JLabel text_leftb;
    private javax.swing.JLabel text[] = new javax.swing.JLabel[3];
    private javax.swing.JLabel text_rightb;
    // End of variables declaration     
}
