package edu.uclm.esi.iso2.Interfaz.presentacion;

import java.awt.EventQueue;

import javax.persistence.NoResultException;
import javax.swing.*;

import edu.uclm.esi.iso2.Radar.domain.Radar;
import edu.uclm.esi.iso2.multas.domain.Driver;
import edu.uclm.esi.iso2.multas.domain.Inquiry;
import edu.uclm.esi.iso2.multas.domain.Manager;
import edu.uclm.esi.iso2.multas.domain.Sanction;
import edu.uclm.esi.iso2.multas.domain.SanctionHolder;
import edu.uclm.esi.iso2.multas.domain.Vehicle;

import javax.swing.JButton;
import javax.swing.plaf.synth.SynthSpinnerUI;

import com.sun.corba.se.spi.oa.OADefault;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class Interfaz {

	private JFrame frame;
	private Radar radar;
	private List<Inquiry> listaExpedientes;
	private JButton btnIniciar;
	private JButton btnApagar;
	private JButton btnSancionarConductor;
	private JButton btnPagarSancin;
	private JButton btnCambiarPropietario;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interfaz window = new Interfaz();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interfaz() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 292, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		btnSancionarConductor = new JButton("Sancionar Conductor");
		btnSancionarConductor.addActionListener(new BtnSancionarConductorActionListener());
		GridBagConstraints gbc_btnSancionarConductor = new GridBagConstraints();
		gbc_btnSancionarConductor.fill = GridBagConstraints.BOTH;
		gbc_btnSancionarConductor.insets = new Insets(0, 0, 5, 5);
		gbc_btnSancionarConductor.gridx = 1;
		gbc_btnSancionarConductor.gridy = 0;
		frame.getContentPane().add(btnSancionarConductor, gbc_btnSancionarConductor);
		
		btnIniciar = new JButton("Iniciar");
		btnIniciar.addActionListener(new BtnIniciarActionListener());
		GridBagConstraints gbc_btnIniciar = new GridBagConstraints();
		gbc_btnIniciar.gridheight = 3;
		gbc_btnIniciar.fill = GridBagConstraints.BOTH;
		gbc_btnIniciar.insets = new Insets(0, 0, 0, 5);
		gbc_btnIniciar.gridx = 0;
		gbc_btnIniciar.gridy = 0;
		frame.getContentPane().add(btnIniciar, gbc_btnIniciar);
		
		btnPagarSancin = new JButton("Pagar Sanción");
		btnPagarSancin.addActionListener(new BtnPagarSancinActionListener());
		GridBagConstraints gbc_btnPagarSancin = new GridBagConstraints();
		gbc_btnPagarSancin.fill = GridBagConstraints.BOTH;
		gbc_btnPagarSancin.insets = new Insets(0, 0, 5, 5);
		gbc_btnPagarSancin.gridx = 1;
		gbc_btnPagarSancin.gridy = 1;
		frame.getContentPane().add(btnPagarSancin, gbc_btnPagarSancin);
		
		btnApagar = new JButton("Apagar");
		btnApagar.setEnabled(false);
		btnApagar.addActionListener(new BtnPararActionListener());
		GridBagConstraints gbc_btnParar = new GridBagConstraints();
		gbc_btnParar.fill = GridBagConstraints.BOTH;
		gbc_btnParar.gridheight = 3;
		gbc_btnParar.gridx = 2;
		gbc_btnParar.gridy = 0;
		frame.getContentPane().add(btnApagar, gbc_btnParar);
		
		btnCambiarPropietario = new JButton("Cambiar Propietario");
		btnCambiarPropietario.addActionListener(new BtnCambiarPropietarioActionListener());
		GridBagConstraints gbc_btnCambiarPropietario = new GridBagConstraints();
		gbc_btnCambiarPropietario.fill = GridBagConstraints.BOTH;
		gbc_btnCambiarPropietario.insets = new Insets(0, 0, 0, 5);
		gbc_btnCambiarPropietario.gridx = 1;
		gbc_btnCambiarPropietario.gridy = 2;
		frame.getContentPane().add(btnCambiarPropietario, gbc_btnCambiarPropietario);

	}
	
	
	private class BtnIniciarActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			iniciarRadar();
		}
	}
	private class BtnPararActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			pararRadar();
		}
	}
	private class BtnSancionarConductorActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			sancionConductor();
		}
	}
	
	private class BtnPagarSancinActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			pagarSancion();
		}
	}
	private class BtnCambiarPropietarioActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cambiarPropietario();
		}
	}
	
	public void iniciarRadar() {
		radar = new Radar("Radar 1");
		radar.start();
		btnIniciar.setEnabled(false);
		btnApagar.setEnabled(true);
	}

	public void pararRadar() {
		radar.parar();
		btnIniciar.setEnabled(true);
		btnApagar.setEnabled(false);
	}
	
	public void sancionConductor() {
		// iniciarRadar();
		// pararRadar();

		listaExpedientes = new Radar("expedientes").listaExpedientes();
		int numeroExpedientes=listaExpedientes.size();
		int numeroSancionesValidas=0;
		for(int i=0;i<numeroExpedientes;i++){
			if(listaExpedientes.get(i).getSanction()==null)
				numeroSancionesValidas++;
		}
		Object[] listadoIdExpediente = new Object[numeroSancionesValidas];
		
		int posicion=0;
		for (int i = 0; i < numeroExpedientes; i++) {
			if(listaExpedientes.get(i).getSanction()==null)
				listadoIdExpediente[posicion++] = listaExpedientes.get(i).getId();
		}
		Object seleccion = JOptionPane.showInputDialog(frame, "Seleccione el identificador del expediente",
				"Selección expediente", JOptionPane.QUESTION_MESSAGE, null, 
				listadoIdExpediente, 1);
		
		if (seleccion != null) {
			
			List<Driver> listadoConductores=Manager.get().findAll(Driver.class);
			
			int cantidadConductores=listadoConductores.size();
			Object[] listadoDNIConductores=new Object[cantidadConductores];
			
			for(int j=0;j<cantidadConductores;j++ ){
				listadoDNIConductores[j]=listadoConductores.get(j).getDni();
			}
			
			Object dni = JOptionPane.showInputDialog(frame, "Seleccione el dni del conductor",
					"Selección dni conductor", JOptionPane.QUESTION_MESSAGE, null, 
					listadoDNIConductores, 1);
			//String dni = JOptionPane.showInputDialog(frame, "indique el dni del conductor",JOptionPane.QUESTION_MESSAGE); 
			//try {
				if(dni!=null){
					Manager.get().identifyDriver(Integer.parseInt(seleccion.toString()), dni.toString());
				}
			//}
			/*catch (NoResultException e1) {
				JOptionPane.showMessageDialog(null, "El dni indicado: " + dni + " no corresponde con un dni de un conductor", // Mensaje
						"Error", // Título
						JOptionPane.ERROR_MESSAGE); // Tipo de mensaje

			}
			*/
		}

		// Manager.get().identifyDriver(1, listaConductores.get(1).getDni());
		// iniciarRadar();
	}
	
	public void pagarSancion(){
		List<Sanction> listadoSanciones=Manager.get().findAll(Sanction.class);
		
		int cantidadSanciones=listadoSanciones.size();
		int sancionesNoPagadas=0;
		for(int i=0;i<cantidadSanciones;i++){
			if(listadoSanciones.get(i).getDateOfPayment()==null)
				sancionesNoPagadas++;
		}
		
		
		Object[] listadoIDSanciones=new Object[sancionesNoPagadas];
		
		int posicion=0;
		for(int j=0;j<cantidadSanciones;j++ ){
			if(listadoSanciones.get(j).getDateOfPayment()==null)
				listadoIDSanciones[posicion++]=listadoSanciones.get(j).getId();
		}
		
		Object idSancion = JOptionPane.showInputDialog(frame, "Seleccione el id de la sanción",
				"Selección identificación sanción", JOptionPane.QUESTION_MESSAGE, null, 
				listadoIDSanciones, 1);
		
		//String idSancion = JOptionPane.showInputDialog(frame, "indique el id de la sanción",JOptionPane.QUESTION_MESSAGE); 
		
		//try{
		if(idSancion!=null){
			int identificadorSancion=Integer.parseInt(idSancion.toString());
			Manager.get().pay(identificadorSancion);
		}
		
		//}
		/*
		catch(NoResultException e1){
			JOptionPane.showMessageDialog(null, "El id indicado: " + idSancion + " no corresponde con ninguna sanción del sistema", // Mensaje
					"Error", // Título
					JOptionPane.ERROR_MESSAGE); // Tipo de mensaje
		}
		catch(NumberFormatException e2){
			JOptionPane.showMessageDialog(null, "El id indicado: " + idSancion + " debe de ser de tipo numérico", // Mensaje
					"Error", // Título
					JOptionPane.ERROR_MESSAGE); // Tipo de mensaje
		}
		*/
	}
	
	public void cambiarPropietario(){
		
		List<Vehicle> listadoLicencias=Manager.get().findAll(Vehicle.class);
		int cantidadLicencias=listadoLicencias.size();
		Object[] listadoLicenciasVehiculo=new Object[cantidadLicencias];
		
		for(int i=0;i<cantidadLicencias;i++){
			listadoLicenciasVehiculo[i]=listadoLicencias.get(i).getLicense();
		}
				
		Object licenciaVehiculo = JOptionPane.showInputDialog(frame, "Seleccione la licencia del vehículo",
				"Selección licencia vehículo", JOptionPane.QUESTION_MESSAGE, null, 
				listadoLicenciasVehiculo, 1);
		
		//String vehiculo = JOptionPane.showInputDialog(frame, "Indique la licencia del vehículo que desea cambiar de propietario",JOptionPane.QUESTION_MESSAGE); 
		
		
		if(licenciaVehiculo!=null){
			List<SanctionHolder> listadoPersonas=Manager.get().findAll(SanctionHolder.class);
			int cantidadPersonas=listadoPersonas.size();
			Object[] listadoDNIPersonas=new Object[cantidadPersonas];
			for(int i=0;i<cantidadPersonas;i++){
				listadoDNIPersonas[i]=listadoPersonas.get(i).getDni();
			}
			
			Object nuevoPropietario = JOptionPane.showInputDialog(frame, "Seleccione el dni del nuevo propietario",
					"Selección dni nuevo propietario", JOptionPane.QUESTION_MESSAGE, null, 
					listadoDNIPersonas, 1);
			
			//nuevoPropietario = JOptionPane.showInputDialog(frame, "Indique el dni del nuevo propietario",JOptionPane.QUESTION_MESSAGE); 
			if(nuevoPropietario!=null)
				Manager.get().changeOwner(licenciaVehiculo.toString(), nuevoPropietario.toString());
		}
		 
		/*
		try{
			if(licenciaVehiculo!=null && nuevoPropietario!=null){
				Manager.get().changeOwner(licenciaVehiculo.toString(), nuevoPropietario);
			}
		}
		*/
		/*catch(NoResultException e1){
			JOptionPane.showMessageDialog(null, "La licencia del vehiculo indicado: "+vehiculo+" o el dni del nuevo propietario: "+nuevoPropietario+" no se encuentran en nuestra base de datos", // Mensaje
					"Error", // Título
					JOptionPane.ERROR_MESSAGE);
		}
		*/
	}
}
