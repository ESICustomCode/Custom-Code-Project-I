package edu.uclm.esi.iso2.multas.dao;

import org.hibernate.HibernateException;
import javax.persistence.Query;

import edu.uclm.esi.iso2.multas.domain.Vehicle;

public class VehicleDao extends GeneralDao<Vehicle> {
	public VehicleDao() {
		super();
	}
	
	public Vehicle findByLicense(String license) throws HibernateException {
		Vehicle vehicle = null;
        try {
            startOperation();
            Query query=(Query) session.createQuery("from Vehicle where license=?");
            query.setParameter(0, license);
            vehicle = (Vehicle) query.getSingleResult();
            transaction.commit();
        } catch (HibernateException e) {
            throw e;
        } finally {
            HibernateFactory.close(session);
        }
        return vehicle;
	}
}
