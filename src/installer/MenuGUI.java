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

public class MenuGUI extends JFrame implements ActionListener, MouseListener, ChangeListener
{
	private static final long serialVersionUID = 1L;
	
	JList<String> jList1 = new JList<String>();
	JScrollPane jList1ScrollPane = new JScrollPane(jList1);
	DefaultListModel<String> jList1Model = new DefaultListModel<String>();
	
	JList<String> jList1b = new JList<String>();
	JScrollPane jList1bScrollPane = new JScrollPane(jList1b);
	DefaultListModel<String> jList1bModel = new DefaultListModel<String>();
	
	JList<String> jList2 = new JList<String>();	
	JScrollPane jList2ScrollPane = new JScrollPane(jList2);
	static DefaultListModel<String> jList2Model = new DefaultListModel<String>();
	
	DefaultListModel<String> jListModel = null;
	JList<String> jList;	
	JTabbedPane tabbedPane = new JTabbedPane();
		
	JEditorPane pane;  
	private JScrollPane scroller;
	
	JLabel uberschrift = new JLabel();
	JLabel versionstext = new JLabel();
	JLabel modtext = new JLabel();	
	JLabel[] bew = new JLabel[5];	
	JLabel banner = new JLabel();
	JLabel bild = new JLabel();
	JLabel listright = new JLabel();
	JLabel pfeilrechts = new JLabel();
	JLabel pfeillinks = new JLabel();
	JLabel importbutton = new JLabel();
	JLabel restore = new JLabel();
	JLabel hilfe = new JLabel();
	JLabel link = new JLabel();
	JLabel quelle = new JLabel();
	JLabel beenden = new JLabel();
	JLabel maximize = new JLabel();
	JLabel minimize = new JLabel();
	static JLabel weiter = new JLabel();		
	JComboBox<String> ChVers;
	JPanel cp;
	
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);		
	private String stamm = Start.stamm, Version = Start.Version;	
	public static int zahl;

	static boolean Modloader=true;
	
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
		
		cp = new GraphicsPanel(false, "src/bild.png");		
		cp.setBackground(Color.decode("#CEE3F6"));
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.setLayout(null);
		add(cp);
		
		int rand = 20;
		int bildx = 400;
		int bildy = 225;
		int uber = (int)(hoehe*0.09);
		int listeya = rand+uber;
		int listenb = (int)(breite/2-bildx+4*rand);
		int listenh = hoehe-listeya-rand;
		int mittexa= rand+listenb+20;
		int modtexty = rand+uber;	
		int infol = modtexty+2*rand;
		int bildya = infol+(int)(2.5*rand);		
		int textya = bildy+bildya+20;		
		int texth = listeya+listenh-textya;
		int liste2h= (int)(listenh*0.55);
	
		uberschrift.setBounds(0, 0, (int)(breite), (int)(hoehe*0.1));                              //Überschrift
		uberschrift.setText(Read.getTextwith("installer", "name"));
		uberschrift.setHorizontalAlignment(SwingConstants.CENTER);
		uberschrift.setVerticalAlignment(SwingConstants.CENTER);
		uberschrift.setFont(Start.lcd.deriveFont(Font.BOLD,48));
		cp.add(uberschrift);
	
		if(Start.Versionen!=null&&Start.Versionen.length>0)  //MC Version ändern
		{
			ChVers = new JComboBox<String>(Start.Versionen);
			for (int ka =0; ka<Start.Versionen.length; ka++)	
			{
				if(Start.Versionen[ka].equals(Version))
					ChVers.setSelectedIndex(ka);
			}
			ChVers.setBounds(rand+listenb-70, (int)(hoehe*0.05), 70, 25);
			ChVers.addActionListener(this);			
			if(Start.Versionen.length==1) ChVers.setEnabled(false);
			cp.add(ChVers);		
		
			versionstext.setBounds((int)(rand+listenb-70-110-5), (int)(hoehe*0.05), 110, 25);
			versionstext.setText("Minecraft");
			versionstext.setVerticalAlignment(SwingConstants.CENTER);
			versionstext.setHorizontalAlignment(SwingConstants.RIGHT);
			cp.add(versionstext);
		}
		
		jList1Model.addElement(Read.getTextwith("seite2", "wait2")); //Liste1 Modloader
		jList1.setModel(jList1Model);		
		jList1.setCellRenderer(new CellRenderer());				
		jList1.addMouseListener(this);
				
		jList1bModel.addElement(Read.getTextwith("seite2", "wait2")); //Liste1 Forge
		jList1b.setModel(jList1bModel);
		jList1b.setCellRenderer(new CellRenderer());				
		jList1b.addMouseListener(this);
		
		jList1ScrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		jList1bScrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		
		tabbedPane.addTab( "Modloader", jList1ScrollPane);
		tabbedPane.addTab( "Forge", jList1bScrollPane);
		tabbedPane.setEnabled(false);
		tabbedPane.addChangeListener(this);
		tabbedPane.setBounds(rand, listeya, listenb, listenh);							
	    cp.add(tabbedPane);        
		
		modtext.setBounds(mittexa, modtexty, bildx, 30);           //Modname
		modtext.setText(Read.getTextwith("seite2", "text7"));		
		modtext.setHorizontalAlignment(SwingConstants.CENTER);
		modtext.setFont(new Font("Dialog", Font.BOLD, 25));
		cp.add(modtext);
		
		link.setBounds(mittexa+bildx-80, infol+5, 40, 40); // Link zu Modinstallerweb	
		link.setIcon(new ImageIcon(this.getClass().getResource("src/infokl.png")));
		link.addMouseListener(this); 
		link.setToolTipText(Read.getTextwith("seite2", "webi"));
		link.setCursor(c);
		cp.add(link);
		
		quelle.setBounds(mittexa+bildx-40, infol+5, 40, 40); // Link zum Entwickler	
		quelle.setIcon(new ImageIcon(this.getClass().getResource("src/quelle.png")));
		quelle.addMouseListener(this); 
		quelle.setToolTipText(Read.getTextwith("seite2", "dev"));
		quelle.setCursor(c);
		cp.add(quelle);
		
		for (int i=0; i<5; i++) //Sterne für Bewertung
		{
			bew[i] = new JLabel();
			bew[i].setBounds(mittexa+10+i*25, infol, 40, 40);
			bew[i].setCursor(c);	
			bew[i].setIcon(new ImageIcon(this.getClass().getResource("src/star0.png")));
			bew[i].addMouseListener(this);
			cp.add(bew[i]);
		}
		
		bild.setBounds(mittexa, bildya, bildx, bildy); 
		bild.setHorizontalAlignment(SwingConstants.CENTER);
		bild.setVerticalAlignment(SwingConstants.CENTER);   
		bild.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		bild.addMouseListener(this);
		bild.setCursor(c);    	
		bild.setIcon(new ImageIcon(this.getClass().getResource("src/warten.gif")));	
		bild.setToolTipText(Read.getTextwith("seite2", "pici"));
		cp.add(bild);
		
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
	    scroller.setBounds(mittexa, textya, bildx, texth);	
	    cp.add(scroller);
	    
		pfeilrechts.setBounds(mittexa+bildx+2*rand, 300, 100, 83); // Pfeil nach rechts		
		pfeilrechts.setIcon(new ImageIcon(this.getClass().getResource("src/hinzufügen.png")));		
		pfeilrechts.setToolTipText(Read.getTextwith("seite2", "text1"));
		pfeilrechts.addMouseListener(this);
		pfeilrechts.setCursor(c);
		cp.add(pfeilrechts);
	
		pfeillinks.setBounds(mittexa+bildx+rand, 390, 100, 83); // Pfeil nach links		
		pfeillinks.setIcon(new ImageIcon(this.getClass().getResource("src/löschen.png")));	
		pfeillinks.setToolTipText(Read.getTextwith("seite2", "text2"));	
		pfeillinks.addMouseListener(this);
		pfeillinks.setCursor(c);
		cp.add(pfeillinks);	
		
		restore.setBounds(breite-rand-listenb+10, (int)(hoehe*0.15), 180, 40); // Restore druchführen	
		restore.setText(Read.getTextwith("seite2", "text5"));	
		restore.setFont(restore.getFont().deriveFont(Font.BOLD));
		restore.setIcon(new ImageIcon(this.getClass().getResource("src/restore.png")));		
		restore.addMouseListener(this); 
		restore.setCursor(c);			
		File backupfile = new File(stamm +"Modinstaller/Backup/");
		if (!backupfile.exists()) // überprüfen, ob Restore möglich ist		
		{
			restore.setEnabled(false);
		}
		//cp.add(restore);
		
		importbutton.setText(Read.getTextwith("seite2", "text3"));	
		importbutton.setFont(importbutton.getFont().deriveFont(Font.BOLD));
		importbutton.setIcon(new ImageIcon(this.getClass().getResource("src/importkl.png")));	
		importbutton.addMouseListener(this); 
		importbutton.setCursor(c);	
		importbutton.setIcon(new ImageIcon(this.getClass().getResource("src/importkl.png")));	
		importbutton.setBounds(breite-rand-listenb+10, (int)(hoehe*0.1), listenb+20, 120); 
		cp.add(BorderLayout.CENTER, importbutton);
		
	    DragDropListener myDragDropListener = new DragDropListener();	    
	    new DropTarget(importbutton, myDragDropListener);	   
		
		listright.setBounds(breite-rand-listenb, (int)(hoehe*0.315), listenb, 20); //Liste2 Überschrift
		listright.setHorizontalAlignment(SwingConstants.CENTER);
		listright.setText(Read.getTextwith("seite2", "modi"));		
		cp.add(listright);
		
		jList2Model.addElement("");    // Liste2
		jList2.setModel(jList2Model);   
		jList2.setCellRenderer(new CellRenderer());  
		jList2.addMouseListener(this);
		jList2ScrollPane.setBounds(breite-rand-listenb,  (int)(hoehe*0.35), listenb, liste2h);
		jList2ScrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.add(jList2ScrollPane);
		
		hilfe.setBounds(2, 5, 50, 50); // FAQ anzeigen			
		hilfe.setIcon(new ImageIcon(this.getClass().getResource("src/help.png")));
		hilfe.setToolTipText(Read.getTextwith("seite2", "text6"));	
		hilfe.addMouseListener(this); 		
		cp.add(hilfe);
	    
		minimize.setBounds(breite-(35+35+35+3+3)-3, 3, 35, 27); //Minimieren		
		minimize.setIcon(new ImageIcon(this.getClass().getResource("src/mini.png")));			
		minimize.addMouseListener(this);
		cp.add(minimize);
		
		maximize.setBounds(breite-(35+35+3)-3, 3, 35, 27); //Maximieren
		maximize.setIcon(new ImageIcon(this.getClass().getResource("src/maxi.png")));			
		maximize.addMouseListener(this);
		maximize.setEnabled(false);
		cp.add(maximize);
		
		beenden.setBounds(breite-35-3, 3, 35, 27); //Beenden		
		beenden.setIcon(new ImageIcon(this.getClass().getResource("src/closeme.png")));			
		beenden.addMouseListener(this);
		cp.add(beenden);
	
		weiter.setBounds((int)(breite-200-rand), hoehe-70-rand, 200, 70); // Installieren		
		weiter.setText(Read.getTextwith("seite2", "text10"));
		weiter.setFont(weiter.getFont().deriveFont((float) 15));
		weiter.addMouseListener(this);
		weiter.setCursor(c);		
		weiter.setHorizontalTextPosition(SwingConstants.LEFT);
		weiter.setFont(weiter.getFont().deriveFont(Font.BOLD, 20));
		weiter.setHorizontalAlignment(SwingConstants.RIGHT);	
		weiter.setVerticalAlignment(SwingConstants.CENTER);
		weiter.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));	
		weiter.setEnabled(false);
		cp.add(weiter);	
	    
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
}