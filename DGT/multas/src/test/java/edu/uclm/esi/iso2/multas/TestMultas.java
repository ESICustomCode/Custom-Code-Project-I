
package edu.uclm.esi.iso2.multas;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uclm.esi.iso2.multas.dao.DriverDao;
import edu.uclm.esi.iso2.multas.dao.GeneralDao;
import edu.uclm.esi.iso2.multas.dao.OwnerDao;
import edu.uclm.esi.iso2.multas.dao.VehicleDao;
import edu.uclm.esi.iso2.multas.domain.Driver;
import edu.uclm.esi.iso2.multas.domain.Inquiry;
import edu.uclm.esi.iso2.multas.domain.Manager;
import edu.uclm.esi.iso2.multas.domain.Owner;
import edu.uclm.esi.iso2.multas.domain.Sanction;
import edu.uclm.esi.iso2.multas.domain.SanctionHolder;
import edu.uclm.esi.iso2.multas.domain.Vehicle;

public class TestMultas {
	private static GeneralDao dao;
	private static Manager m;
	private static DriverDao driverDao;
	private static OwnerDao ownerDao;
	private static VehicleDao vehicleDao;
	
	@BeforeClass
	public static void inicializarYCrearConductor() {
		dao = new GeneralDao();
		driverDao = new DriverDao();
		m = Manager.get();
		ownerDao=new OwnerDao();
		vehicleDao=new VehicleDao();
		
		Driver conductorNuevo = new Driver("699999999", "Jose", "Torralbo", "X");
		conductorNuevo.setPoints(12);
		driverDao.insert(conductorNuevo);
		
	}

	@Before
	public void setUp() {
		Driver actualizarPuntosCondutor = driverDao.findByDni("699999999");
		actualizarPuntosCondutor.setPoints(12);
		driverDao.update(actualizarPuntosCondutor);
	}

	@AfterClass
	public static void borrarDatosInsertados() {
		Driver borrarConductor = driverDao.findByDni("699999999");

		List<Sanction> listadoSanciones = m.findAll(Sanction.class);
		int sizeListaSanciones = listadoSanciones.size();

		ArrayList<Sanction> listadoSancionesConDNIEspecifico = new ArrayList<>();

		for (int i = 0; i < sizeListaSanciones; i++) {
			Sanction sancion = listadoSanciones.get(i);
			if (sancion.getSanctionHolder().getDni().equals("699999999"))
				listadoSancionesConDNIEspecifico.add(sancion);
		}
		int sizeListaExpedientesAnadidos = listadoSancionesConDNIEspecifico.size();

		List<Inquiry> listadoExpedientes = m.findAll(Inquiry.class);
		int sizeListaExpedientes = listadoExpedientes.size();

		for (int i = 0; i < sizeListaExpedientesAnadidos; i++) {
			List<Inquiry> lista = dao.findInquirySantion(listadoSancionesConDNIEspecifico.get(i).getId());
			
				lista.get(0).setSanction(listadoSancionesConDNIEspecifico.get(i));
				m.delete(lista.get(0));
		}
		SanctionHolder usuario = (SanctionHolder) dao.findById(SanctionHolder.class, borrarConductor.getId());
		dao.delete(usuario);
		
	}

	@Test
	public void testDatosRelacionInquirySanctionCorrectos() {
		int idExpediente = m.openInquiry("0001", 31, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		m.pay(multa.getId());
		Inquiry expediente = (Inquiry) dao.findById(Inquiry.class, idExpediente);
		Sanction multaAlmacenada = (Sanction) dao.findById(Sanction.class, multa.getId());

		assertNotNull(expediente.getSanction());
		assertEquals(multa.getId(), multaAlmacenada.getId());
		assertNull(multa.getDateOfPayment());
		assertNotNull(multaAlmacenada.getDateOfPayment());
		
	}

	@Test
	public void testCambiarPropietario(){
		
		Owner propietarioNuevoConCoche=new Owner("999900000","Jose","Torralbo","X");
		Owner propietarioSinCoche=new Owner("777700000","Jose","Torralbo","X");
		Vehicle vehiculoNuevo=new Vehicle("8000");
		vehiculoNuevo.setOwner(propietarioNuevoConCoche);
		
		ownerDao.insert(propietarioNuevoConCoche);
		ownerDao.insert(propietarioSinCoche);
		vehicleDao.insert(vehiculoNuevo);
		
		try{
			Owner propietarioVehiculo=ownerDao.findByDni("999900000");
			Vehicle vehiculoPropietario=vehicleDao.findByLicense("8000");
			
			
			m.changeOwner("8000", "777700000");
			Owner propietarioVehiculoNuevo=ownerDao.findByDni("777700000");
			Vehicle vehiculoPropietarioModificado=vehicleDao.findByLicense("8000");
			
			
			assertEquals(vehiculoPropietario.getOwner().getId(),propietarioVehiculo.getId());
			assertEquals(vehiculoPropietarioModificado.getOwner().getId(),propietarioVehiculoNuevo.getId());
			
			Vehicle vehiculoAnadido=vehicleDao.findByLicense("8000");
			SanctionHolder usuarioVehiculoInicial=(SanctionHolder) dao.findById(SanctionHolder.class,ownerDao.findByDni("999900000").getId());
			vehicleDao.delete(vehiculoAnadido);
			dao.delete(usuarioVehiculoInicial);
		}
		catch(Exception e){
			fail("No se esperaba ninguna excepcion");
		}
	}

	@Test
	public void test30_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 30, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}
	
	@Test
	public void test31_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 31, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test40_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 40, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test50_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 50, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test51_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 51, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test55_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 55, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test60_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 60, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test61_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 61, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test65_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 65, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test70_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 70, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void tes71_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 71, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test75_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 75, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test80_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 80, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test81_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 81, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test100_30() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 100, "La ronda", 30);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test40_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 40, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}
	
	@Test
	public void test41_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 41, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test50_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 50, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test60_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 60, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test61_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 61, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test65_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 65, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test70_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 70, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test71_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 71, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test75_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 75, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test80_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 80, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test81_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 81, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test85_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 85, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test90_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 90, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test91_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 91, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test100_40() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 100, "La ronda", 40);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test50_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 50, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}
	
	@Test
	public void test51_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 51, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test60_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 60, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test70_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 70, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test71_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 71, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test75_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 75, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test80_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 80, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test81_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 81, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test85_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 85, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test90_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 90, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test91_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 91, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test95_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 95, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test100_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 100, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test101_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 101, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test110_50() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 110, "La ronda", 50);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test60_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 60, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}
	
	@Test
	public void test61_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 61, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test75_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 75, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test90_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 90, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test91_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 91, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test100_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 100, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test110_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 110, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test111_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 111, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test115_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 115, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test120_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 120, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test121_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 121, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test125_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 125, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test130_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 130, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test131_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 131, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test140_60() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 140, "La ronda", 60);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test70_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 70, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}
	
	@Test
	public void test71_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 71, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test85_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 85, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test100_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 100, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test101_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 101, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test110_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 110, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test120_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 120, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test121_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 121, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test125_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 125, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test130_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 130, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test131_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 131, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test135_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 135, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test140_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 140, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test141_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 141, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test150_70() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 150, "La ronda", 70);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test80_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 80, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}
	
	@Test
	public void test81_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 81, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test90_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 90, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test110_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 110, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test111_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 111, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test120_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 120, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test130_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 130, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test131_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 131, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test135_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 135, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test140_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 140, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test141_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 141, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test145_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 145, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test150_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 150, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test151_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 151, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test160_80() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 160, "La ronda", 80);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}
	
	@Test
	public void test90_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 90, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test91_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 91, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test105_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 105, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test120_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 120, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test121_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 121, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test130_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 130, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test140_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 140, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test141_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 141, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test145_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 145, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test150_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 150, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test151_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 151, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test155_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 155, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test160_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 160, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test161_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 161, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test170_90() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 170, "La ronda", 90);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}
	
	@Test
	public void test100_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 100, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test101_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 101, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test115_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 115, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test130_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 130, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test131_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 131, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test140_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 140, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test150_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 150, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test151_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 151, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test155_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 155, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test160_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 160, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test161_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 161, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test165_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 165, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test170_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 170, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test171_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 171, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test180_100() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 180, "La ronda", 100);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test110_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 110, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}
	
	@Test
	public void test111_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 111, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test125_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 125, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test140_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 140, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test141_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 141, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test150_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 150, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test160_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 160, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test161_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 161, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test165_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 165, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test170_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 170, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test171_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 171, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test175_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 175, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test180_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 180, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test181_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 181, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test190_110() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 190, "La ronda", 110);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}
	
	@Test
	public void test120_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 120, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");

		m.pay(multa.getId());

		Driver conductorMultado = driverDao.findByDni("699999999");
		assertEquals(multa.getAmount(), 0, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test121_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 121, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test135_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 135, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test150_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 150, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 100, 0);
		assertEquals(multa.getPoints(), 0);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints());
	}

	@Test
	public void test151_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 151, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test160_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 160, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test170_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 170, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 300, 0);
		assertEquals(multa.getPoints(), 2);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 2);
	}

	@Test
	public void test171_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 171, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test175_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 175, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test180_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 180, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 400, 0);
		assertEquals(multa.getPoints(), 4);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 4);
	}

	@Test
	public void test181_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 181, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test185_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 185, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test190_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 190, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 500, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test191_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 191, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

	@Test
	public void test200_120() {
		m = Manager.get();
		int idExpediente = m.openInquiry("0001", 200, "La ronda", 120);
		Sanction multa = m.identifyDriver(idExpediente, "699999999");
		Driver conductor = driverDao.findByDni("699999999");
		m.pay(multa.getId());
		Driver conductorMultado = driverDao.findByDni("699999999");

		assertEquals(multa.getAmount(), 600, 0);
		assertEquals(multa.getPoints(), 6);
		assertEquals(conductor.getPoints(), conductorMultado.getPoints() + 6);
	}

}
