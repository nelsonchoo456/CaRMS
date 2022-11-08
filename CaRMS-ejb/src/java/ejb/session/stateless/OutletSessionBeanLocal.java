/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Outlet;
import javax.ejb.Local;
import util.exception.OutletNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Local
public interface OutletSessionBeanLocal {

    public Long createOutlet(Outlet outlet);

    public Outlet retrieveOutletById(Long outletId) throws OutletNotFoundException;

    public Outlet retrieveOutletByName(String outletName) throws OutletNotFoundException;
    
}
