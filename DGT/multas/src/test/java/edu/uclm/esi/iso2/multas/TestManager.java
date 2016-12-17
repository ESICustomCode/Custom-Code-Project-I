/*
package edu.uclm.esi.iso2.multas;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mysql.jdbc.Driver;
import com.sun.javafx.image.impl.General;

import edu.uclm.esi.iso2.multas.dao.GeneralDao;
import edu.uclm.esi.iso2.multas.domain.Manager;
import edu.uclm.esi.iso2.multas.domain.Sanction;

public class TestManager {

	@Test
	public void test140_120() {
		
		Manager m=Manager.get();
		int idExpediente=m.openInquiry("0001", 140, "La ronda", 120);
		Sanction multa=m.identifyDriver(idExpediente, "5000002");
		m.pay(multa.getId());
		assertNotNull(multa.getDateOfPayment());
		assertTrue(multa.getAmount()==100);
		assertTrue(multa.getPoints()==0);
	}
	
	@Test
	public void test160_120(){

		Manager m=Manager.get();
		int idExpediente=m.openInquiry("0001", 160, "La ronda", 120);
		Sanction multa=m.identifyDriver(idExpediente, "5000002");
		m.pay(multa.getId());
		assertNotNull(multa.getDateOfPayment());
		assertTrue(multa.getAmount()==300);
		assertTrue(multa.getPoints()==2);
		
	}

}
*/