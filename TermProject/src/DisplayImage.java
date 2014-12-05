import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;




public class DisplayImage {
	//private static final int WIDTH = 600;
	//private static final int HEIGHT = 600;
	
	public static void main( String[] args)
	{
		//make the frame
		//JFrame frame = new ImageFrame(WIDTH,HEIGHT);
		final JFrame splash = new JFrame();
		//JPanel panel = new JPanel();
		BufferedImage img = null;
		try {
			//System.out.println(System.getProperty("user.dir"));
		    img = ImageIO.read(new File("src/images/Splash.png"));
		} catch (IOException e) {
		}
		splash.setContentPane(new JScrollPane (new JLabel(new ImageIcon(img))));
		JMenu fileMenu = new JMenu("File");
		JMenuItem make = new JMenuItem("Make simulation Field");
		make.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Object[] options = {"Small (resolution = 1366x768)", "Large (resolution > 1920x1080)"};
				int choice = JOptionPane.showOptionDialog(null,
						"What size simulation would you like?",
						"Size Selection",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,     //do not use a custom Icon
						options,  //the titles of buttons
						options[0]); //default button title
				
				JFrame frame = new ImageFrame(choice);
				splash.setVisible(false);
				//splash.dispatchEvent(new WindowEvent(splash, WindowEvent.WINDOW_CLOSING));
				frame.setVisible(true);
			}
		});
		fileMenu.add(make);
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		splash.setJMenuBar(menuBar);
		splash.pack();
		splash.validate();
		splash.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		splash.setVisible(true);
	}	
}
