/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Outlet;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.OutletNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class OutletSessionBean implements OutletSessionBeanRemote, OutletSessionBeanLocal {

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createOutlet(Outlet outlet) 
    {
        em.persist(outlet);
        em.flush();
        
        return outlet.getOutletId();
        
    }
    
    @Override
    public Outlet retrieveOutletById(Long outletId) throws OutletNotFoundException
    {
        Outlet outlet = em.find(Outlet.class, outletId);
        
        if (outlet != null) {
            return outlet;
        } else {
            throw new OutletNotFoundException("Outlet " + outletId + " does not exist.");
        }
    }
    
    @Override
    public Outlet retrieveOutletByName(String outletName) throws OutletNotFoundException
    {
        Query query = em.createQuery("SELECT o FROM Outlet o WHERE o.outletName = :inName");
        query.setParameter("inName", outletName);
        try {
            return (Outlet)query.getSingleResult();
        } 
        catch (NoResultException | NonUniqueResultException ex) 
        {
            throw new OutletNotFoundException("Outlet " + outletName + " does not exist.");
        }
    }
}
