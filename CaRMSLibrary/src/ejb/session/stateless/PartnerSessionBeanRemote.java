/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import javax.ejb.Remote;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Remote
public interface PartnerSessionBeanRemote {
    
    public Long createNewPartner(Partner partner);
    
    public Long partnerLogin(String partnerName, String password) throws InvalidLoginCredentialException;
    
    public Partner retrievePartnerByPartnerId(Long partnerId) throws PartnerNotFoundException;
}
