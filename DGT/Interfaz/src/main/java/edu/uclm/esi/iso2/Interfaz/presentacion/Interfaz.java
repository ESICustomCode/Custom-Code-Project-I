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

public class Interfaz {

	private JFrame frame;
	private JButton btnIniciar;
	private Radar radar;
	private JButton btnApagar;
	private List<Inquiry> listaExpedientes;
	private JButton btnSancionarConductor;

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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		btnIniciar = new JButton("Iniciar");
		btnIniciar.addActionListener(new BtnIniciarActionListener());
		frame.getContentPane().add(btnIniciar, BorderLayout.WEST);

		btnApagar = new JButton("Apagar");
		btnApagar.setEnabled(false);
		btnApagar.addActionListener(new BtnApagarActionListener());
		frame.getContentPane().add(btnApagar, BorderLayout.EAST);

		btnSancionarConductor = new JButton("Sancionar Conductor");
		btnSancionarConductor.addActionListener(new BtnSancionarConductorActionListener());
		frame.getContentPane().add(btnSancionarConductor, BorderLayout.CENTER);

	}

	private class BtnIniciarActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			iniciarRadar();
		}
	}

	private class BtnApagarActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			pararRadar();
		}
	}

	private class BtnSancionarConductorActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			sancionConductor();
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
					JOptionPane.QUESTION_MESSAGE); // el icono sera un
													// iterrogante
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
}
