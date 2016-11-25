package edu.uclm.esi.iso2.Radar.domain;

import java.util.List;

import edu.uclm.esi.iso2.multas.dao.GeneralDao;
import edu.uclm.esi.iso2.multas.domain.Vehicle;

public class Radar extends Thread {

	private boolean estado;
	
	public Radar(String msg){
		super(msg);
	}
	
	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	
	public void run(){
		setEstado(true);
		while(isEstado()){

			}
			
	}

	public void parar(){
		setEstado(false);;
	}
	
}
