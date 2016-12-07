package edu.uclm.esi.iso2.Interfaz.presentacion;

import java.awt.EventQueue;

import javax.persistence.NoResultException;
import javax.swing.*;

import edu.uclm.esi.iso2.Radar.domain.Radar;
import edu.uclm.esi.iso2.multas.domain.Driver;
import edu.uclm.esi.iso2.multas.domain.Inquiry;
import edu.uclm.esi.iso2.multas.domain.Manager;

import javax.swing.JButton;
import javax.swing.plaf.synth.SynthSpinnerUI;

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

		Object[] listadoIdExpediente = new Object[listaExpedientes.size()];
		for (int i = 0; i < listadoIdExpediente.length; i++) {
			listadoIdExpediente[i] = listaExpedientes.get(i).getId();
		}
		Object seleccion = JOptionPane.showInputDialog(frame, "Seleccione el identificador del expediente",
				"Selección expediente", JOptionPane.QUESTION_MESSAGE, null, // null
																			// para
																			// icono
																			// defecto
				listadoIdExpediente, 1);
		if (seleccion != null) {
			String dni = JOptionPane.showInputDialog(frame, "indique el dni del conductor",
					JOptionPane.QUESTION_MESSAGE); 
			try {
				if (dni.length() < 7) {
					for (int i = 0; i <6 && dni.length()<6; i++) {
						dni = "0" + dni;
					}
					dni = "5"+ dni;
				}				
				Manager.get().identifyDriver(Integer.parseInt(seleccion.toString()), dni);
			} catch (NoResultException e1) {
				JOptionPane.showMessageDialog(null, "El dni indicado: " + dni + " no corresponde con un dni de un conductor", // Mensaje
						"Error", // Título
						JOptionPane.ERROR_MESSAGE); // Tipo de mensaje

			}
		}

		// Manager.get().identifyDriver(1, listaConductores.get(1).getDni());
		// iniciarRadar();
	}
	
	public void pagarSancion(){
		String idSancion = JOptionPane.showInputDialog(frame, "indique el id de la sanción",
				JOptionPane.QUESTION_MESSAGE); 
		
		try{
			int identificadorSancion=Integer.parseInt(idSancion);
			Manager.get().pay(identificadorSancion);
		}
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
	}
	
	public void cambiarPropietario(){
		String vehiculo = JOptionPane.showInputDialog(frame, "Indique la licencia del vehículo que desea cambiar de propietario",
				JOptionPane.QUESTION_MESSAGE); 
		String nuevoPropietario=null;
		
		if(vehiculo!=null){
			nuevoPropietario = JOptionPane.showInputDialog(frame, "Indique el dni del nuevo propietario",
					JOptionPane.QUESTION_MESSAGE); 
		}
		 
		
		try{
			if(vehiculo!=null && nuevoPropietario!=null){
				Manager.get().changeOwner(vehiculo, nuevoPropietario);
			}
		}
		catch(NoResultException e1){
			JOptionPane.showMessageDialog(null, "La licencia del vehiculo indicado: "+vehiculo+" o el dni del nuevo propietario: "+nuevoPropietario+" no se encuentran en nuestra base de datos", // Mensaje
					"Error", // Título
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
