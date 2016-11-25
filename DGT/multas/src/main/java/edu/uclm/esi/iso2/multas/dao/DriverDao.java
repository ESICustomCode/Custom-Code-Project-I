package edu.uclm.esi.iso2.multas.dao;

import javax.persistence.Query;

import org.hibernate.HibernateException;

import edu.uclm.esi.iso2.multas.domain.Driver;

public class DriverDao extends GeneralDao<Driver> {
	public DriverDao() {
		super();
	}
	
	public Driver findByDni(String dni) throws HibernateException {
		Driver driver = null;
        try {
            startOperation();
            
            Query query= session.createQuery("from Driver where dni=?");
            query.setParameter(0, dni);
            
            driver = (Driver) query.getSingleResult();
            transaction.commit();
        } catch (HibernateException e) {
            throw e;
        } finally {
            HibernateFactory.close(session);
        }
        return driver;
	}
}
