import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Cliente {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MarcoCliente mimarco = new MarcoCliente();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}

}

class MarcoCliente extends JFrame{
	

	public MarcoCliente() {
		
		setBounds(600,300,280,350);
		
		LaminaMarcoCliente lamina = new LaminaMarcoCliente();
		
		add(lamina);
		
		setVisible(true);
		
		addWindowListener(new EnvioOnline());
	}
	
}

//********************************Envio de Señal Online**********************************
class EnvioOnline extends WindowAdapter{
	
	public void windowOpened(WindowEvent e) {
		
		try {
			
			Socket misocket = new Socket ("192.168.1.6", 9999);
			
			EnvioPaquete datos = new EnvioPaquete();
			
			datos.setMensaje("-Online");
			
			ObjectOutputStream paqueteDatos = new ObjectOutputStream(misocket.getOutputStream());
			
			paqueteDatos.writeObject(datos);
			
			misocket.close();
			
			
		}catch(Exception ex) {
			
		}
		
	}
	
}

class LaminaMarcoCliente extends JPanel implements Runnable{
	
	private JTextField campo1;
	
	private JComboBox ip;
	
	private JLabel nick;
	
	private JButton miboton1;
	
	private JTextArea campoChat;
	
	
	public LaminaMarcoCliente() {
		
		String n_usuario = JOptionPane.showInputDialog("Nick:");
		
		
		
		JLabel n_nick = new JLabel("Nick");
		
		add(n_nick);
		
		nick = new JLabel();
		
		nick.setText(n_usuario);
		
		add(nick);
		
		JLabel texto = new JLabel("-ONLINE:-");
		
		add(texto);
		
		ip = new JComboBox();
		
		/*ip.addItem("Usuario 1");
		
		ip.addItem("Usuario 3");
		
		ip.addItem("Usuario 2");*/
		
		add(ip);
		
		campoChat = new JTextArea(12,20);
		
		add(campoChat);
		
		campo1 = new JTextField(20);
		
		add(campo1);
		
		miboton1 = new JButton("Enviar");
		
		EnviaTexto mievento = new EnviaTexto();
		
		miboton1.addActionListener(mievento);
		
		
		
		add(miboton1);
		
		
		Thread mihilo = new Thread(this);
		
		mihilo.start();
		
	}
	
	
	private class EnviaTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Apéndice de método generado automáticamente
			
			//System.out.println(campo1.getText());
			
			campoChat.append("\n"+ nick.getText()+ ":" + campo1.getText());			
			try {
				Socket misocket = new Socket("192.168.1.6",9999);
				
				EnvioPaquete paquete = new EnvioPaquete();
				
				paquete.setNick(nick.getText());
				
				paquete.setIp(ip.getSelectedItem().toString());
				
				paquete.setMensaje(campo1.getText());
				
				ObjectOutputStream flujo_paquete = new ObjectOutputStream(misocket.getOutputStream());
				
				flujo_paquete.writeObject(paquete);
				
				campo1.setText("");
							
				misocket.close();
							
				/*DataOutputStream flujo_salida = new DataOutputStream(misocket.getOutputStream());
				
				flujo_salida.writeUTF(campo1.getText());
				
				campo1.setText("");
				
				flujo_salida.close();*/
				
			} catch (UnknownHostException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Bloque catch generado automáticamente
				System.out.println(e.getMessage());
				
				
				
			
			}
			
		}
		
	}


	@Override
	public void run() {
		// TODO Apéndice de método generado automáticamente
		
		
		try {
			ServerSocket servidor_cliente = new ServerSocket(9090);
			
			String nick, mensaje;
			
			EnvioPaquete paquete_recibido;
			

			
			while(true) {
				
				Socket misocket = servidor_cliente.accept();
				
				ObjectInputStream flujo_recibido = new ObjectInputStream(misocket.getInputStream());
				
				paquete_recibido = (EnvioPaquete) flujo_recibido.readObject();
				
				if (!paquete_recibido.getMensaje().equals("-Online")) {
					
					nick = paquete_recibido.getNick();
					
					mensaje = paquete_recibido.getMensaje();
					
					campoChat.append("\n" + nick + " : " + mensaje ); 
					
					flujo_recibido.close();
					
				}else {
					
					//campoChat.append("\n" + paquete_recibido.getIps());
					
					ArrayList<String> IpsMenu = new ArrayList<String>();
					
					IpsMenu = paquete_recibido.getIps();
					
					ip.removeAllItems();
					
					for (String z: IpsMenu) {
						
						ip.addItem(z);
						
					}
					
				}

			}
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Bloque catch generado automáticamente
			System.out.println(e.getMessage());
		}
		
	}
}

class EnvioPaquete implements Serializable {
	
	private String ip, nick, mensaje;
	
	private ArrayList<String> Ips;

	public ArrayList<String> getIps() {
		return Ips;
	}

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
	
}