/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Override
    public Long createNewPartner(Partner partner)
    {
        em.persist(partner);
        em.flush();
        return partner.getPartnerId();
    }
    
    @Override
    public Long partnerLogin(String partnerName, String password) throws InvalidLoginCredentialException 
    {
        try {
            Query query = em.createQuery("SELECT p FROM Partner p WHERE p.name = :inPartnerName");
            query.setParameter("inPartnerName", partnerName);
            Partner partner = (Partner) query.getSingleResult();

            if (partner.getPassword().equals(password)) {
                return partner.getPartnerId();
            } else {
                throw new InvalidLoginCredentialException("Invalid login credential");
            }
        } catch (NoResultException ex) {
            throw new InvalidLoginCredentialException("Invalid login credential");
        }
    }
    
    @Override
    public Partner retrievePartnerByPartnerId(Long partnerId) throws PartnerNotFoundException {
        Partner partner = em.find(Partner.class, partnerId);

        if (partner != null) {
            return partner;
        } else {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " does not exist!");
        }
    }

}
