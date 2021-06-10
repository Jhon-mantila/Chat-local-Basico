import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;

public class Servidor {

	public static void main(String[] args) {
		// TODO Apéndice de método generado automáticamente
		
		MarcoServidor miServidor = new MarcoServidor();
		
		miServidor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

class MarcoServidor extends JFrame implements Runnable{
	
	private JTextArea textarea;
	
	public MarcoServidor() {
		
		setBounds(1200, 300, 280,350);
		
		JPanel milamina = new JPanel();
		
		milamina.setLayout(new BorderLayout());
		
		textarea = new JTextArea();
		
		milamina.add(textarea, BorderLayout.CENTER);
		
		add(milamina);
		
		setVisible(true);
		
		Thread mihilo = new Thread(this);
		
		mihilo.start();
	}

	@Override
	public void run() {
		// TODO Apéndice de método generado automáticamente
		//System.out.println("Hilo en ejecución");
		
		String ip, nick, mensaje;
		
		ArrayList<String> listaIp = new ArrayList<String>();
		
		EnvioPaquete paquete_recibido;
		
		try {
			ServerSocket servidor = new ServerSocket(9999);
			
			while(true) {
				
			
				Socket misocket = servidor.accept();
				

				
				ObjectInputStream flujo_recibido = new ObjectInputStream(misocket.getInputStream());
				
				paquete_recibido = (EnvioPaquete) flujo_recibido.readObject();
				
				
				nick = paquete_recibido.getNick();
				
				ip = paquete_recibido.getIp();
				
				mensaje = paquete_recibido.getMensaje();
				
				
				/*DataInputStream flujo_entrada = new DataInputStream(misocket.getInputStream());
				
				String mensaje_texto = flujo_entrada.readUTF();
				
				textarea.append("\n" + mensaje_texto);*/
				
				if (!mensaje.equals("-Online")) {
				
				textarea.append("\n" + nick + " : " + mensaje + " para: " + ip);
				
				
				Socket socketDestinatario = new Socket(ip,9090);
				
				ObjectOutputStream paquete_reenvio = new ObjectOutputStream(socketDestinatario.getOutputStream());
				
				paquete_reenvio.writeObject(paquete_recibido);
				
				paquete_reenvio.close();
				
				socketDestinatario.close();
				
				misocket.close(); 
				
				}else{
					
					//**************DETECTA ONLINE LOS CONECTADOS*********************
					
					InetAddress localizacion = misocket.getInetAddress();
					
					String ipRemota = localizacion.getHostAddress();
					
					//System.out.println("Online :"+ipRemota);
					
					listaIp.add(ipRemota);
					
					paquete_recibido.setIps(listaIp);
					
					for (String z: listaIp) {
						
						//System.out.println("Array: " + z);
						
						Socket socketDestinatario = new Socket(z,9090);
						
						ObjectOutputStream paquete_reenvio = new ObjectOutputStream(socketDestinatario.getOutputStream());
						
						paquete_reenvio.writeObject(paquete_recibido);
						
						paquete_reenvio.close();
						
						socketDestinatario.close();
						
						misocket.close(); 
					}
										
					//****************************************************************
				}
			
			}
			
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();	
		}
		
		
	}
	
}