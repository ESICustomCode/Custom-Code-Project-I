package edu.uclm.esi.iso2.Radar.domain;

import java.util.List;
import java.util.Random;

import edu.uclm.esi.iso2.multas.dao.GeneralDao;
import edu.uclm.esi.iso2.multas.domain.SanctionHolder;
import edu.uclm.esi.iso2.multas.domain.Vehicle;

public class Radar extends Thread {

	private boolean estado;
	private List<Vehicle> listaVehiculos;

	public Radar(String msg){
		super(msg);
	}
	
	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	
	public List<Vehicle> getListaVehiculos() {
		return listaVehiculos;
	}

	public void setListaVehiculos(List<Vehicle> listaVehiculos) {
		this.listaVehiculos = listaVehiculos;
	}
	
	public void run(){
		setEstado(true);
		setListaVehiculos(vehiculosAlmacenados());
		String direccion=generarValoresDirecciones(listaInfractores());
		
		int velocidadMaxima=velocidadMaximaVia();
		 
		
		while(isEstado()){	
		
			double velocidad=GenerarVelocidadAleatoria();
			
			if(velocidadMaxima<velocidad){
				Vehicle pillado= GenerarValoresVehiculos();
				System.out.println("Vehiculo: "+ pillado.getLicense()+"\nDirección: "+ direccion +"\nVelocidadMaxima: "+ velocidadMaxima+"\nVelocidad Coche: "+velocidad );
			}
		}
		
	}

	public void parar(){
		setEstado(false);;
	}
	
	public List<Vehicle> vehiculosAlmacenados(){
		List<Vehicle> listaVehiculos=new GeneralDao().findAll(Vehicle.class);
		return listaVehiculos;
	}
	
	public List<SanctionHolder> listaInfractores(){
		List<SanctionHolder> listaInfractores=new GeneralDao().findAll(SanctionHolder.class);
		return listaInfractores;
	}
	
	public Vehicle GenerarValoresVehiculos(){
		Random rd =new Random();
		return getListaVehiculos().get(rd.nextInt(getListaVehiculos().size()));
	}
	
	public String generarValoresDirecciones(List<SanctionHolder> listaInfractores){
		Random rd=new Random();
		return listaInfractores().get(rd.nextInt(listaInfractores.size())).getFullAddress();
	}
	
	public double GenerarVelocidadAleatoria(){
		Random rd=new Random();
		return rd.nextInt(200) + rd.nextDouble();
	}
	
	public int velocidadMaximaVia(){
		Random rd=new Random();
		int [] velocidades={30,40,50,60,70,80,90,100,110,120};
		return velocidades[rd.nextInt(velocidades.length)];
	}
}
