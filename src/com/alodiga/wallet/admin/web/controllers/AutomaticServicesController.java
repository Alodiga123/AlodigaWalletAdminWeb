package com.alodiga.wallet.admin.web.controllers;

import java.util.Date;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractController;
import com.alodiga.wallet.common.ejb.TransactionTimerEJB;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class AutomaticServicesController extends GenericAbstractController {

	private static final long serialVersionUID = -9145887024839938515L;
    private Label lblInfo;
    private Label lblDailyClosingDate;
    private Label lblDailyClosingInterval;

    private TransactionTimerEJB transactionTimerEJB = null;


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }


    @Override
    public void initialize() {
        try {
            super.initialize();
            transactionTimerEJB = (TransactionTimerEJB) EJBServiceLocator.getInstance().get(EjbConstants.TRANSACTION_TIMER_EJB);
          

            showPPNExecutionDates();
        } catch (Exception ex) {
            ex.printStackTrace();
            lblInfo.setValue(Labels.getLabel("sp.error.general"));
        }
    }

    private void showPPNExecutionDates() {
        Date date1 = transactionTimerEJB.getNextExecutionDate();
        lblDailyClosingDate.setValue(date1 != null ? date1.toString() : Labels.getLabel("sp.automatic.commission.noDate"));
        Long dailyInterval = transactionTimerEJB.getTimeoutInterval() / 86400000;// 86400000 Milisegundos en un dia
        lblDailyClosingInterval.setValue(dailyInterval.toString());

    }


    public void onClick$btnPPNRestart() {
        try {
        	transactionTimerEJB.restart();
            lblInfo.setValue(Labels.getLabel("sp.automatic.commission.success"));
            showPPNExecutionDates();
        } catch (Exception ex) {
            ex.printStackTrace();
            lblInfo.setValue(Labels.getLabel("sp.error.general"));
        }
    }

    public void onClick$btnPPNStop() {
        try {
        	transactionTimerEJB.stop();
            lblInfo.setValue(Labels.getLabel("sp.automatic.commission.success"));
            showPPNExecutionDates();
        } catch (Exception ex) {
            ex.printStackTrace();
            lblInfo.setValue(Labels.getLabel("sp.error.general"));
        }
    }

    public void onClick$btnPPNTimeout() {
        try {
        	transactionTimerEJB.forceTimeout();
            String response = Labels.getLabel("sp.automatic.commission.timeoutMessage");
            lblInfo.setValue(response);
            showPPNExecutionDates();
        } catch (Exception ex) {
            ex.printStackTrace();
            lblInfo.setValue(Labels.getLabel("sp.error.general"));
        }
    }

    public void onClick$btnPPNNextExecution() {
        try {
            showPPNExecutionDates();
        } catch (Exception ex) {
            ex.printStackTrace();
            lblInfo.setValue(Labels.getLabel("sp.error.general"));
        }
    }
 
}
