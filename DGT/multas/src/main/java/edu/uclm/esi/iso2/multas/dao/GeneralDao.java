package edu.uclm.esi.iso2.multas.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

import java.util.List;

public class GeneralDao<T> {
    protected Session session;
    protected Transaction transaction;

    public GeneralDao() {
    	// Si quitamos el comentario a la línea siguiente, la base de datos se vaciará.
        // HibernateFactory.buildIfNeeded();
    }

    protected void startOperation() throws HibernateException {
        session = HibernateFactory.openSession();
        transaction = session.beginTransaction();
    }

    public void insert(T object) throws HibernateException {
        try {
            startOperation();
            session.save(object);
            transaction.commit();
        } catch(HibernateException e) {
        	throw e;
        } finally {
            HibernateFactory.close(session);
        }
    }
    
    public void update(T object) throws HibernateException {
        try {
            startOperation();
            session.update(object);
            transaction.commit();
        } catch(HibernateException e) {
        	throw e;
        } finally {
            HibernateFactory.close(session);
        }
    }

    public void delete(T obj) throws HibernateException {
        try {
            startOperation();
            session.delete(obj);
            transaction.commit();
        } catch(HibernateException e) {
        	throw e;
        } finally {
            HibernateFactory.close(session);
        }
    }
    
	public T findById(Class clazz, Integer id) throws HibernateException {
        T obj = null;
        try {
            startOperation();
            obj = (T) session.get(clazz, id);
            transaction.commit();
        } 
        catch(HibernateException e) {
        	throw e;
        } finally {
            HibernateFactory.close(session);
        }
        return obj;
    }

    public List<T> findAll(Class clazz) throws HibernateException {
        List<T> objects = null;
        try {
            startOperation();
            Query query = session.createQuery("from " + clazz.getName());
            objects = query.list();
            transaction.commit();
        } catch(HibernateException e) {
        	throw e;
        } finally {
            HibernateFactory.close(session);
        }
        return objects;
    }
}