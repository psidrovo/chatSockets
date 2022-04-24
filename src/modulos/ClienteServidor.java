package modulos;

import javax.swing.*;

//Class to precise who is connected : Client or Server
public class ClienteServidor {

	
	public static void main(String [] args){
		
		Object[] valoresCmb = { "Servidor","Cliente"};
		String valorInicial = "Servidor";
		
		Object selection = JOptionPane.showInputDialog(null, "Iniciar como: ", "Chat UPS CUENCA", JOptionPane.QUESTION_MESSAGE, null, valoresCmb, valorInicial);
		if(selection.equals("Servidor")){
                   String[] arguments = new String[] {};
			new SincronizadorClienteServidor().main(arguments);
		}else if(selection.equals("Cliente")){
			String IPServer = JOptionPane.showInputDialog("Ingrese la IP del servidor");
                        String[] arguments = new String[] {IPServer};
			new ChatCliente().main(arguments);
		}
		
	}

}
