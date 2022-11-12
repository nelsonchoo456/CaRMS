/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.DispatchRecordSessionBeanRemote;
import ejb.session.stateless.EjbTimerSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import ejb.session.stateless.RentalReservationSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author Nelson Choo
 */
public class Main {

    @EJB
    private static EjbTimerSessionBeanRemote ejbTimerSessionBean;

    @EJB
    private static RentalReservationSessionBeanRemote rentalReservationSessionBean;

    @EJB
    private static DispatchRecordSessionBeanRemote dispatchRecordSessionBean;

    @EJB
    private static RentalRateSessionBeanRemote rentalRateSessionBean;

    @EJB
    private static ModelSessionBeanRemote modelSessionBean;

    @EJB
    private static CategorySessionBeanRemote categorySessionBean;

    @EJB
    private static CarSessionBeanRemote carSessionBean;

    @EJB
    private static OutletSessionBeanRemote outletSessionBean;

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(outletSessionBean, employeeSessionBean, rentalRateSessionBean, modelSessionBean, categorySessionBean, carSessionBean, dispatchRecordSessionBean, rentalReservationSessionBean, ejbTimerSessionBean);
        
        mainApp.runApp();
    }
    
}
