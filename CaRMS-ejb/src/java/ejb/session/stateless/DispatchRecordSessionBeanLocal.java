/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DispatchRecord;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.DispatchRecordNotFoundException;
import util.exception.DriverNotWorkingInSameOutletException;
import util.exception.EmployeeNotFoundException;
import util.exception.RentalReservationNotFoundException;

/**
 *
 * @author Nelson Choo
 */
@Local
public interface DispatchRecordSessionBeanLocal {

    public Long createNewDispatchRecord(Long reservationId, Date transitDate) throws RentalReservationNotFoundException;

    public List<DispatchRecord> retrieveDispatchRecordsByOutletId(Date date, Long outletId);

    public DispatchRecord retrieveDispatchRecordById(Long dispatchRecordId) throws DispatchRecordNotFoundException;

    public void assignDriver(Long driverId, Long dispatchRecordId) throws DriverNotWorkingInSameOutletException, DispatchRecordNotFoundException, EmployeeNotFoundException;

    public void updateTransitAsCompleted(Long dispatchRecordId) throws DispatchRecordNotFoundException;
    
}
