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
	JComboBox<String> ChVers;
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
			
	JEditorPane pane;  
	JScrollPane scroller;
	
	JLabel headerLabel = new JLabel();
	JLabel mcLabel = new JLabel();
	JLabel modtext = new JLabel();	
	JLabel[] ratIcons = new JLabel[5];		
	JLabel picture = new JLabel();
	JLabel rightListHeadl = new JLabel();
	JLabel selectArrow = new JLabel();
	JLabel removeArrow = new JLabel();
	JLabel importButton = new JLabel();
	JLabel restoreButton = new JLabel();
	JLabel sizeLabel = new JLabel();
	JLabel helpButton = new JLabel();
	JLabel linkButton = new JLabel();
	JLabel specImg = new JLabel();
	JLabel sourceButton = new JLabel();
	JLabel exitButton = new JLabel();
	JLabel maximizeButton = new JLabel();
	JLabel minimizeButton = new JLabel();
	HintTextField search = new HintTextField(Read.getTextwith("seite2", "search"));
	JLabel nextButton = new JLabel();
	
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);		
	private String stamm = Start.stamm, mcVersion = Start.mcVersion;		
	private int hoehe =600, breite=1024;	
	
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
		int liste2h= (int)(listenh*0.55);
	
		headerLabel.setBounds(0, 0, (int)(breite), (int)(hoehe*0.1));                              //Überschrift
		headerLabel.setText(Read.getTextwith("installer", "name"));
		headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		headerLabel.setVerticalAlignment(SwingConstants.CENTER);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD,45));
		cp.add(headerLabel);
	
		if(Start.mcVersionen!=null&&Start.mcVersionen.length>0)  //MC Version ändern
		{
			ChVers = new JComboBox<String>(Start.mcVersionen);
			for (int ka =0; ka<Start.mcVersionen.length; ka++)	
			{
				if(Start.mcVersionen[ka].equals(mcVersion))
					ChVers.setSelectedIndex(ka);
			}
			ChVers.setBounds(rand+listenb-70, (int)(hoehe*0.05), 70, 25);
			ChVers.addActionListener(this);			
			if(Start.mcVersionen.length==1) ChVers.setEnabled(false);
			cp.add(ChVers);		
		
			mcLabel.setBounds((int)(rand+listenb-70-110-5), (int)(hoehe*0.05), 110, 25);
			mcLabel.setText("Minecraft");
			mcLabel.setVerticalAlignment(SwingConstants.CENTER);
			mcLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			cp.add(mcLabel);
		}
		
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
	    
	    search.setBounds(rand, listeya+listenh-25, listenb, 25);	
	    search.addKeyListener(this);
	    search.addMouseListener(this);
	    search.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
	    search.setBorder(BorderFactory.createCompoundBorder(search.getBorder(), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
	    cp.add(search);
	    
		modtext.setBounds(mittexa, modtexty, picturex, 30);           //Modname
		modtext.setText(Read.getTextwith("seite2", "text7"));		
		modtext.setHorizontalAlignment(SwingConstants.CENTER);
		modtext.setFont(new Font("Dialog", Font.BOLD, 25));
		cp.add(modtext);
		
		for (int i=0; i<5; i++) //Sterne für Bewertung
		{
			ratIcons[i] = new JLabel();
			ratIcons[i].setBounds(mittexa+10+i*25, infol, 40, 40);
			ratIcons[i].setCursor(c);	
			ratIcons[i].setIcon(new ImageIcon(this.getClass().getResource("src/star0.png")));
			ratIcons[i].addMouseListener(this);
			cp.add(ratIcons[i]);
		}
		
		specImg.setBounds(mittexa+picturex-250, infol-1, 50, 50); // Link zu Modinstallerweb			
		specImg.setIcon(new ImageIcon(this.getClass().getResource("src/top.png")));
		specImg.setVisible(false);
		cp.add(specImg);
		
		sizeLabel.setBounds(mittexa+picturex-180, infol+5, 90, 40); // Downloadgröße		
		sizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		sizeLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
		cp.add(sizeLabel);
		
		linkButton.setBounds(mittexa+picturex-80, infol+5, 40, 40); // Link zu Modinstallerweb	
		linkButton.setIcon(new ImageIcon(this.getClass().getResource("src/infokl.png")));
		linkButton.addMouseListener(this); 
		linkButton.setToolTipText(Read.getTextwith("seite2", "webi"));
		linkButton.setCursor(c);
		cp.add(linkButton);		
		
		sourceButton.setBounds(mittexa+picturex-40, infol+5, 40, 40); // Link zum Entwickler	
		sourceButton.setIcon(new ImageIcon(this.getClass().getResource("src/quelle.png")));
		sourceButton.addMouseListener(this); 
		sourceButton.setToolTipText(Read.getTextwith("seite2", "dev"));
		sourceButton.setCursor(c);
		cp.add(sourceButton);		
		
		picture.setBounds(mittexa, pictureya, picturex, picturey); 
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
			
		pane = new JEditorPane(); //Beschreibungsfenster		
		pane.setEditable(false);
	    pane.setContentType("text/html");	
	    Document doc = kit.createDefaultDocument();
	    pane.setDocument(doc);	  
	    pane.setText(Read.getTextwith("seite2", "wait"));	  	  
	    
	    StyleSheet ss = kit.getStyleSheet();
		try 
		{
			ss.importStyleSheet(new URL("http://www.minecraft-installer.de/sub/installerstyle.css"));
		} 
		catch (MalformedURLException e1) 
		{			
		}		
		kit.setStyleSheet(ss);
	    
	    scroller = new JScrollPane(pane);
	    scroller.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
	    scroller.setBounds(mittexa, textya, picturex, texth);	
	    cp.add(scroller);
	    
	    new DropTarget(pane, myDragDropListener);
	    
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
		
		new DropTarget(rightList, myDragDropListener);
		
		helpButton.setBounds(2, 5, 50, 50); // FAQ anzeigen			
		helpButton.setIcon(new ImageIcon(this.getClass().getResource("src/help.png")));
		helpButton.setToolTipText(Read.getTextwith("seite2", "text6"));	
		helpButton.addMouseListener(this); 		
		cp.add(helpButton);
	    
		minimizeButton.setBounds(breite-(35+35+3+3)-3, 3, 35, 27); //Minimieren		
		minimizeButton.setIcon(new ImageIcon(this.getClass().getResource("src/mini.png")));			
		minimizeButton.addMouseListener(this);
		cp.add(minimizeButton);
		
		maximizeButton.setBounds(breite-(35+35+3)-3, 3, 35, 27); //Maximieren
		maximizeButton.setIcon(new ImageIcon(this.getClass().getResource("src/maxi.png")));			
		maximizeButton.addMouseListener(this);
		maximizeButton.setEnabled(false);
		//cp.add(maximizeButton);
		
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

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
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