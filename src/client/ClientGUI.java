package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


@SuppressWarnings("serial")
public class ClientGUI extends JFrame{

	private JFrame frame;
	private JTextField messageField;
	private static JTextArea textArea = new JTextArea();
	private Client client;
	public boolean running;
	private int posX, posY;
	static DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");  
	static LocalDateTime now = LocalDateTime.now();
	


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	
	public ClientGUI() {
	       String name = "";
	       while (name.isEmpty()) {
	    	   name = JOptionPane.showInputDialog("Enter Name");
	       }
	       client = new Client(name, "localhost" ,57419);
	       initialize();
	}
	
	

	
	private void initialize() {
		frame = new JFrame();
		frame.setForeground(Color.BLACK);
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setResizable(false);
		frame.setTitle("Chat Room\r\n");
		frame.setBounds(100, 100, 691, 526);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		running = true;
		frame.getContentPane().setLayout(null);
		textArea.setFont(new Font("Century Gothic", Font.PLAIN, 13));
		textArea.setBackground(Color.GRAY);
		textArea.setForeground(Color.BLACK);
		textArea.setSelectedTextColor(Color.WHITE);
		textArea.setEditable(false);
		
		JScrollPane ChatBox = new JScrollPane(textArea);
		ChatBox.setBorder(new LineBorder(Color.BLACK, 1, true));
		ChatBox.setBackground(SystemColor.controlDkShadow);
		ChatBox.setBounds(10, 27, 668, 459);
		frame.getContentPane().add(ChatBox);
		
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.setBounds(10, 486, 668, 40);
		frame.getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		messageField = new JTextField();
		messageField.setDisabledTextColor(Color.WHITE);
		messageField.setPreferredSize(new Dimension(7, 25));
		messageField.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		messageField.setBorder(new LineBorder(Color.BLACK, 1, true));
		messageField.setBackground(Color.GRAY);
		messageField.setForeground(Color.black);
		panel.add(messageField);
		messageField.setColumns(45);
		
		JButton btnSend = new JButton("Send");
		btnSend.setFont(new Font("Century Gothic", Font.BOLD, 12));
		btnSend.setPreferredSize(new Dimension(56, 25));
		btnSend.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		btnSend.setBackground(Color.LIGHT_GRAY);
		btnSend.addActionListener(e -> {
			if (!messageField.getText().equals("")) {
			client.send(messageField.getText());
			messageField.setText("");
			}
			
			
		});
		
		panel.add(btnSend);
		
		frame.getRootPane().setDefaultButton(btnSend);
		
		JPanel DragPanel = new JPanel();
		DragPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				posX = e.getX();
				posY = e.getY();
			}
		});
		DragPanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(frame.getX() + e.getX() - posX, frame.getY() + e.getY() - posY );
			}
		});
		DragPanel.setBackground(Color.DARK_GRAY);
		DragPanel.setBounds(0, 0, 691, 29);
		frame.getContentPane().add(DragPanel);
		DragPanel.setLayout(null);
		
		JButton btnX = new JButton("X");
		btnX.setFocusable(false);
		btnX.setForeground(Color.LIGHT_GRAY);
		btnX.setFont(new Font("Century Gothic", Font.BOLD, 22));
		btnX.setContentAreaFilled(false);
		btnX.setBorder(null);
		btnX.setBounds(645, 3, 42, 23);
		btnX.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				running = false;
				System.out.print("Thread closed");
			}
			
		});
		DragPanel.add(btnX);
		
		
		frame.setLocationRelativeTo(null);

	}
	
	
	public static void printToConsole(String message) {
		textArea.setText(textArea.getText()+time.format(now)+": "+message+"\n");
	}
}