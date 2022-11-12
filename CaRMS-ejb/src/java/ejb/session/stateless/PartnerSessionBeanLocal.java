/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import javax.ejb.Local;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Local
public interface PartnerSessionBeanLocal {

    public Long createNewPartner(Partner partner);

    public Long partnerLogin(String partnerName, String password) throws InvalidLoginCredentialException;

    public Partner retrievePartnerByPartnerId(Long partnerId) throws PartnerNotFoundException;
    
}
