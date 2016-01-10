package installer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class MenuGUI extends JFrame implements ActionListener, MouseListener, ChangeListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	
	JTabbedPane tabbedPane = new JTabbedPane();
	JComboBox<String> mcVersDrop;
	JPanel cp;
	
	JList<String> leftListM = new JList<String>();
	JScrollPane  leftListMSP = new JScrollPane(leftListM);
	DefaultListModel<String> leftListMModel = new DefaultListModel<String>();
	
	JList<String> leftListF = new JList<String>();
	JScrollPane leftListFSP = new JScrollPane(leftListF);
	DefaultListModel<String> leftListFModel = new DefaultListModel<String>();
	
	JList<String> rightList = new JList<String>();	
	JScrollPane rightListSP = new JScrollPane(rightList);
	DefaultListModel<String> rightListModel = new DefaultListModel<String>();
	
	DragDropListener myDragDropListener = new DragDropListener(this);	
			
	JEditorPane modDescPane;  
	JScrollPane scroller;
	
	JLabel headerLabel = new JLabel();
	JLabel mcVersLabel = new JLabel();
	JLabel modNameLabel = new JLabel();	
	JLabel[] ratIcons = new JLabel[5];		
	JLabel picture = new JLabel();
	JLabel rightListHeadl = new JLabel();
	JLabel selectArrow = new JLabel();
	JLabel removeArrow = new JLabel();
	JLabel importButton = new JLabel();
	JLabel restoreButton = new JLabel();
	JLabel sizeLabel = new JLabel();
	JLabel helpButton = new JLabel();
	JLabel modinstWebLnk = new JLabel();
	JLabel topIcon = new JLabel();
	JLabel videoButton = new JLabel();
	JLabel devWebLnk = new JLabel();
	JLabel exitButton = new JLabel();
	JLabel maxButton = new JLabel();
	JLabel minButton = new JLabel();
	JLabel modVersionL = new JLabel();
	HintTextField searchInput = new HintTextField(Read.getTextwith("seite2", "search"));
	JLabel nextButton = new JLabel();
	
	JProgressBar bar = new JProgressBar();
	
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);		
	private String stamm = Start.stamm;		
	private int hoehe =600, breite=1024;	
	String hyperlink = Read.getTextwith("installer", "website"), website=Read.getTextwith("installer", "website");
	
	public void GUI() 
	{
		setUndecorated(true);
		setSize(breite, hoehe);
		setTitle(Read.getTextwith("installer", "name"));		
		setLocationRelativeTo(null);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
		
		final Point point = new Point(0,0);
		addMouseListener(new MouseAdapter() 
		{  			
			public void mousePressed(MouseEvent e) 
			{  
				 if(!e.isMetaDown())
				 {  					
					 point.x = e.getX();  
					 point.y = e.getY();  
					 setCursor(new Cursor(Cursor.MOVE_CURSOR));
				 } 
			} 			
			public void mouseReleased(MouseEvent e) 
			{		
				 setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});  
		addMouseMotionListener(new MouseMotionAdapter() 
		{  
			 public void mouseDragged(MouseEvent e) 
			 {  				 
				 if(!e.isMetaDown())
				 {  
					 
					 Point p = getLocation();  
					 setLocation(p.x + e.getX() - point.x,  
					 p.y + e.getY() - point.y); 					
				 } 				
			 } 			
		});
		
		cp = new GraphicsPanel(false);
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.setLayout(null);
		add(cp);
		
		int rand = 20;
		int picturex = 400;
		int picturey = 225;
		int uber = (int)(hoehe*0.09);
		int listeya = rand+uber;
		int listenb = (int)(breite/2-picturex+4*rand);
		int listenh = hoehe-listeya-rand;
		int mittexa= rand+listenb+20;
		int modtexty = rand+uber;	
		int infol = modtexty+2*rand;
		int pictureya = infol+(int)(2.5*rand);		
		int textya = picturey+pictureya+20;		
		int texth = listeya+listenh-textya;
		int liste2h= (int)(listenh*0.54);
	
		headerLabel.setBounds(0, 0, (int)(breite), (int)(hoehe*0.1));                              //Überschrift
		headerLabel.setText(Read.getTextwith("installer", "name"));
		headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		headerLabel.setVerticalAlignment(SwingConstants.CENTER);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD,45));
		cp.add(headerLabel);
		
		mcVersLabel.setBounds((int)(rand), (int)(hoehe*0.05), listenb, 40); //Minecraft Version auswählen
		mcVersLabel.setText("Minecraft ["+Start.mcVersion+"]");
		mcVersLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mcVersLabel.addMouseListener(this);
		mcVersLabel.setCursor(c);	
		mcVersLabel.setFont(mcVersLabel.getFont().deriveFont(Font.BOLD,14));
		cp.add(mcVersLabel);
		
		leftListMModel.addElement(Read.getTextwith("seite2", "wait2")); //Liste1 Modloader
		leftListM.setModel(leftListMModel);		
		leftListM.setCellRenderer(new CellRenderer());				
		leftListM.addMouseListener(this);
				
		leftListFModel.addElement(Read.getTextwith("seite2", "wait2")); //Liste1 Forge
		leftListF.setModel(leftListFModel);
		leftListF.setCellRenderer(new CellRenderer());				
		leftListF.addMouseListener(this);
		
		leftListMSP.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		leftListFSP.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		
		tabbedPane.addTab("Modloader", leftListMSP);
		tabbedPane.addTab("Forge", leftListFSP);
		tabbedPane.setEnabled(false);
		tabbedPane.addChangeListener(this);
		tabbedPane.setBounds(rand, listeya, listenb, listenh-30);							
	    cp.add(tabbedPane);   
	    
	    searchInput.setBounds(rand, listeya+listenh-25, listenb, 25);	//Suchfeld
	    searchInput.addKeyListener(this);
	    searchInput.addMouseListener(this);
	    searchInput.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
	    searchInput.setBorder(BorderFactory.createCompoundBorder(searchInput.getBorder(), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
	    cp.add(searchInput);
	    
		modNameLabel.setBounds(mittexa, modtexty, picturex, 30);           //Modname
		modNameLabel.setText(Read.getTextwith("seite2", "text7"));
		modNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		modNameLabel.setFont(new Font("Dialog", Font.BOLD, 25));
		cp.add(modNameLabel);
		
		modVersionL.setBounds(mittexa+10, infol+5, 160, 40); //Importierte Mods: Modversion
		modVersionL.setFont(new Font("Dialog", Font.PLAIN, 18));
		modVersionL.setVisible(false);
		cp.add(modVersionL);
		
		for (int i=0; i<5; i++) //Sterne für Bewertung
		{
			ratIcons[i] = new JLabel();
			ratIcons[i].setBounds(mittexa+10+i*25, infol, 40, 40);
			ratIcons[i].setCursor(c);	
			ratIcons[i].setIcon(new ImageIcon(this.getClass().getResource("src/star0.png")));
			ratIcons[i].addMouseListener(this);
			cp.add(ratIcons[i]);
		}
		
		topIcon.setBounds(mittexa+picturex-250, infol-1, 50, 50); // TOP oder NEW Bild	
		topIcon.setIcon(new ImageIcon(this.getClass().getResource("src/top.png")));
		topIcon.setVisible(false);
		cp.add(topIcon);
		
		sizeLabel.setBounds(mittexa+picturex-160-50-10, infol+5, 40+50, 40); // Downloadgröße		
		sizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		sizeLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		sizeLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
		cp.add(sizeLabel);
		
		videoButton.setBounds(mittexa+picturex-120, infol+5, 40, 40); // Link zu Video	
		videoButton.setIcon(new ImageIcon(this.getClass().getResource("src/video.png")));
		videoButton.addMouseListener(this); 
		videoButton.setToolTipText(Read.getTextwith("seite2", "video"));
		videoButton.setCursor(c);
		videoButton.setVisible(false);
		cp.add(videoButton);	
		
		modinstWebLnk.setBounds(mittexa+picturex-80, infol+5, 40, 40); // Link zu Modinstallerweb	
		modinstWebLnk.setIcon(new ImageIcon(this.getClass().getResource("src/infokl.png")));
		modinstWebLnk.addMouseListener(this); 
		modinstWebLnk.setToolTipText(Read.getTextwith("seite2", "webi"));
		modinstWebLnk.setCursor(c);
		cp.add(modinstWebLnk);		
		
		devWebLnk.setBounds(mittexa+picturex-40, infol+5, 40, 40); // Link zum Entwickler	
		devWebLnk.setIcon(new ImageIcon(this.getClass().getResource("src/quelle.png")));
		devWebLnk.addMouseListener(this); 
		devWebLnk.setToolTipText(Read.getTextwith("seite2", "dev"));
		devWebLnk.setCursor(c);
		cp.add(devWebLnk);		
		
		picture.setBounds(mittexa, pictureya, picturex, picturey); //Modbild
		picture.setHorizontalAlignment(SwingConstants.CENTER);
		picture.setVerticalAlignment(SwingConstants.CENTER);   
		picture.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		picture.addMouseListener(this);
		picture.setCursor(c);    	
		picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif")));	
		picture.setToolTipText(Read.getTextwith("seite2", "pici"));
		cp.add(picture);
		
		new DropTarget(picture, myDragDropListener);
		
		HTMLEditorKit kit = new HTMLEditorKit();		
			
		modDescPane = new JEditorPane(); //Beschreibungsfenster		
		modDescPane.setEditable(false);
	    modDescPane.setContentType("text/html");	
	    Document doc = kit.createDefaultDocument();
	    modDescPane.setDocument(doc);	  
	    modDescPane.setText(Read.getTextwith("seite2", "wait"));	  	  
	    
	    StyleSheet ss = kit.getStyleSheet();
		try 
		{
			ss.importStyleSheet(new URL("http://www.minecraft-installer.de/sub/installerstyle.css"));
		} 
		catch (MalformedURLException e1) 
		{			
		}		
		kit.setStyleSheet(ss);
	    
	    scroller = new JScrollPane(modDescPane);
	    scroller.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
	    scroller.setBounds(mittexa, textya, picturex, texth);	
	    cp.add(scroller);
	    
	    new DropTarget(modDescPane, myDragDropListener);
	    
		selectArrow.setBounds(mittexa+picturex+2*rand, 250, 100, 83); // Pfeil nach rechts		
		selectArrow.setIcon(new ImageIcon(this.getClass().getResource("src/arrowSe.png")));		
		selectArrow.setToolTipText(Read.getTextwith("seite2", "text1"));
		selectArrow.addMouseListener(this);
		selectArrow.setCursor(c);
		cp.add(selectArrow);
	
		removeArrow.setBounds(mittexa+picturex+rand, 360, 100, 83); // Pfeil nach links		
		removeArrow.setIcon(new ImageIcon(this.getClass().getResource("src/arrowRe.png")));	
		removeArrow.setToolTipText(Read.getTextwith("seite2", "text2"));	
		removeArrow.addMouseListener(this);
		removeArrow.setCursor(c);
		cp.add(removeArrow);	
		
		restoreButton.setBounds(breite-rand-listenb+10, (int)(hoehe*0.1), 180, 40); // Restore druchführen	
		restoreButton.setText(Read.getTextwith("seite2", "text5"));	
		restoreButton.setFont(restoreButton.getFont().deriveFont(Font.BOLD));
		restoreButton.setIcon(new ImageIcon(this.getClass().getResource("src/restore.png")));		
		restoreButton.addMouseListener(this); 
		restoreButton.setCursor(c);			
		File backupfile = new File(stamm +"Modinstaller/Backup/");
		if (!backupfile.exists()) // überprüfen, ob Restore möglich ist		
		{
			restoreButton.setEnabled(false);
		}
		cp.add(restoreButton);
		
		importButton.setText(Read.getTextwith("seite2", "text3"));	
		importButton.setFont(importButton.getFont().deriveFont(Font.BOLD));
		importButton.setIcon(new ImageIcon(this.getClass().getResource("src/importkl.png")));	
		importButton.addMouseListener(this); 
		importButton.setCursor(c);	
		importButton.setIcon(new ImageIcon(this.getClass().getResource("src/importkl.png")));	
		importButton.setBounds(breite-rand-listenb+10, (int)(hoehe*0.2), listenb+20, 50); 
		cp.add(BorderLayout.CENTER, importButton);
		new DropTarget(importButton, myDragDropListener);	   
		
		rightListHeadl.setBounds(breite-rand-listenb, (int)(hoehe*0.315), listenb, 20); //Liste2 Überschrift
		rightListHeadl.setHorizontalAlignment(SwingConstants.CENTER);
		rightListHeadl.setText(Read.getTextwith("seite2", "modi"));		
		cp.add(rightListHeadl);
		
		rightListModel.addElement("");    // Liste2
		rightList.setModel(rightListModel);   
		rightList.setCellRenderer(new CellRenderer());  
		rightList.addMouseListener(this);
		rightListSP.setBounds(breite-rand-listenb,  (int)(hoehe*0.35), listenb, liste2h);
		rightListSP.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.add(rightListSP);
		
	
		bar.setBounds(breite-rand-listenb, (int)(hoehe*0.35)+liste2h-1, listenb, 15);
		bar.setOpaque(false);
		bar.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		bar.setUI(new GradientPalletProgressBarUI());
		cp.add(bar);
		
		new DropTarget(rightList, myDragDropListener);
		
		helpButton.setBounds(2, 5, 50, 50); // FAQ anzeigen			
		helpButton.setIcon(new ImageIcon(this.getClass().getResource("src/help.png")));
		helpButton.setToolTipText(Read.getTextwith("seite2", "text6"));	
		helpButton.addMouseListener(this); 		
		cp.add(helpButton);
	    
		minButton.setBounds(breite-(35+35+3+3)-3, 3, 35, 27); //Minimieren		
		minButton.setIcon(new ImageIcon(this.getClass().getResource("src/mini.png")));			
		minButton.addMouseListener(this);
		cp.add(minButton);
		
		maxButton.setBounds(breite-(35+35+3)-3, 3, 35, 27); //Maximieren
		maxButton.setIcon(new ImageIcon(this.getClass().getResource("src/maxi.png")));			
		maxButton.addMouseListener(this);
		maxButton.setEnabled(false);
		//cp.add(maxButton);
		
		exitButton.setBounds(breite-35-3, 3, 35, 27); //Beenden		
		exitButton.setIcon(new ImageIcon(this.getClass().getResource("src/closeme.png")));			
		exitButton.addMouseListener(this);
		cp.add(exitButton);
	
		nextButton.setBounds((int)(breite-250-rand), hoehe-70-rand, 250, 70); // Installieren		
		nextButton.setText(Read.getTextwith("seite2", "text10"));
		nextButton.setFont(nextButton.getFont().deriveFont((float) 15));
		nextButton.addMouseListener(this);
		nextButton.setCursor(c);		
		nextButton.setHorizontalTextPosition(SwingConstants.LEFT);
		nextButton.setFont(nextButton.getFont().deriveFont(Font.BOLD, 20));
		nextButton.setHorizontalAlignment(SwingConstants.RIGHT);	
		nextButton.setVerticalAlignment(SwingConstants.CENTER);
		nextButton.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));	
		nextButton.setEnabled(false);
		cp.add(nextButton);	
	    
	    setVisible(true);
	}
	private static class CellRenderer extends DefaultListCellRenderer //Auswahlliste verschönern
	{   
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	    {  
           super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );  
           list.setFixedCellHeight(25);     
           setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));           
           if(isSelected) setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));          
           if(cellHasFocus) setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
           return (this);  
        }
    }
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	@Override
	public void stateChanged(ChangeEvent e) {
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyPressed(KeyEvent e) {
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
}