package edu.uclm.esi.iso2.Interfaz.presentacion;

import java.awt.EventQueue;

import javax.swing.*;

import edu.uclm.esi.iso2.Radar.domain.Radar;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Interfaz {

	private JFrame frame;
	private JButton btnIniciar;
	private Radar radar;
	private JButton btnApagar;
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
	
	public void iniciarRadar(){
		radar=new Radar("Radar 1");
		radar.start();
		btnIniciar.setEnabled(false);
		btnApagar.setEnabled(true);
	}
	
	public void pararRadar(){
		radar.parar();
		btnIniciar.setEnabled(true);
		btnApagar.setEnabled(false);
	}
}
