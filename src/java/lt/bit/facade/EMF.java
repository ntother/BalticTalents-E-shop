package lt.bit.facade;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

public class EMF {

    private static final String PERSISTENCE_UNIT_NAME = "E-CommercePU";
    private static final Logger LOG = Logger.getLogger(EMF.class.getName());

    protected static EntityManagerFactory emf;

    public static EntityManagerFactory get() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return emf;
    }

    public static EntityManager getEntityManager()
            throws PersistenceException {
        return get().createEntityManager();
    }

    public static void returnEntityManager(EntityManager em) {
        if (em != null) {
            try {
                if (em.isOpen()) {
                    em.close();
                }
            } catch (PersistenceException ex) {
                LOG.log(Level.WARNING, "Failed to close EntityManager.", ex);
            }
        }
    }

    public static Object getTransaction(EntityManager em)
            throws Exception {
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            if (!tx.isActive()) {
                tx.begin();
            }
            return tx;
        } else {
            throw new PersistenceException("Entity manager not provided.");
        }
    }

    public static void commitTransaction(Object tx)
            throws Exception {
        if (tx != null) {
            if (((EntityTransaction) tx).isActive()) {
                ((EntityTransaction) tx).commit();
            }
        }
    }

    public static void rollbackTransaction(Object tx) {
        if (tx != null) {
            if (((EntityTransaction) tx).isActive()) {
                try {
                    ((EntityTransaction) tx).rollback();
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, "Unexpected exception while rolling back transaction", ex);
                }
            }
        }
    }
}
