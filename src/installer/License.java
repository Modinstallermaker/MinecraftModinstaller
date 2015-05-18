package installer;

import static installer.OP.optionReader;
import static installer.OP.optionWriter;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class License extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private JButton zur = new JButton();
	private JButton wei = new JButton();
	private JCheckBox check = new JCheckBox();
	private JLabel head = new JLabel();	
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);
	private String Lizenztext = "";
	private JEditorPane tp = new JEditorPane();
	private Scanner scan;
	private int hoehe = 345, breite=550;
	private Modinfo[] Mod, Downloadlist;

	public License(Modinfo[] Mod, Modinfo[] Downloadlist) 
	{
		this.Mod=Mod;
		this.Downloadlist=Downloadlist;
		
		setUndecorated(true);			
		setSize(breite, hoehe);
		JPanel cp = new GraphicsPanel(false, "src/bild.png");
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));

		
		setTitle(Read.getTextwith("installer", "name"));		
		setLocationRelativeTo(null);	
		setResizable(false);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
		
		cp.setLayout(null);
		add(cp);
		
		String lang = optionReader("language");
		scan = new Scanner(getClass().getResourceAsStream("src/lizenz_"+lang+".txt"), "ISO-8859-15");

		while (scan.hasNextLine()) 
		{
			Lizenztext += scan.nextLine();
		}
		
		tp.setEditable(false);
		tp.setContentType("text/html");
		tp.setEditorKit(new HTMLEditorKit()); 
		tp.setText("<html><body>" +Lizenztext	+ "</body></html>");
	
		JScrollPane ScrollPane2 = new JScrollPane(tp);
		ScrollPane2.setBounds(25, 50, 500, 200);
		cp.add(ScrollPane2);

		tp.setCaretPosition(0);

		zur.setCursor(c);
		zur.setBackground(null);
		zur.setBounds(10, 295, 110, 35);
		zur.setText(Read.getTextwith("lizenz", "text1"));
		zur.setMargin(new Insets(2, 2, 2, 2));
		zur.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});
		cp.add(zur);
		wei.setCursor(c);
		wei.setBackground(null);
		wei.setBounds(410, 295, 130, 35);
		wei.setText(Read.getTextwith("lizenz", "text2"));
		wei.setMargin(new Insets(2, 2, 2, 2));
		wei.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				wei_ActionPerformed(evt);
			}
		});
		wei.setEnabled(false);
		cp.add(wei);
		check.setBounds(55, 260, 400, 25);
		check.setText(Read.getTextwith("lizenz", "text3"));
		check.setOpaque(false);
		check.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				check_ItemStateChanged(evt);
			}
		});
		cp.add(check);
		head.setBounds(24, 15, 243, 25);
		head.setFont(new Font("Dialog", Font.BOLD, 16));
		head.setText(Read.getTextwith("lizenz", "text4"));
		cp.add(head);

		setVisible(true);
	}

	// Anfang Methoden
	

	public void wei_ActionPerformed(ActionEvent evt) 
	{
		Date zeitstempel = new Date();
		
		optionWriter("lizenz", String.valueOf(zeitstempel));				
		
		dispose();
		new Menu(Mod, Downloadlist);
	}

	public void check_ItemStateChanged(ItemEvent evt) 
	{
		if (evt.getStateChange() == 1) 
		{
			wei.setEnabled(true);
		} 
		else 
		{
			wei.setEnabled(false);
		}
	}
}