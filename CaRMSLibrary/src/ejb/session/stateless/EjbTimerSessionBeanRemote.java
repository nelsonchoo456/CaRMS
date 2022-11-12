/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RentalReservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.NoCarsToAllocateException;
import util.exception.RentalReservationNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Remote
public interface EjbTimerSessionBeanRemote {
    
    public void allocateCarsToCurrentDayReservations(Date date);
}
