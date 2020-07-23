package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.model.BankOperation;

public class AdminBankOperationsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblNumber;
    private Label lblUser;
    private Label lblTransaction;
    private Label lblStatus;
    private Label lblType;
    private Label lblMode;
    private Label lblBank;
    private Label lblProduct;
    private Label lblAmount;
    private Label lblDate;
    private BankOperation bankOperationParam;
    private Integer eventType;
    public Window winAdminBankOperations;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_VIEW) {
        	bankOperationParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BankOperation) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();      
        try {
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {

    }

    private void loadFields(BankOperation bankOperation) {
        try {
            lblAmount.setValue(String.valueOf(bankOperation.getTransactionId().getAmount()));
            lblDate.setValue(bankOperation.getTransactionId().getCreationDate().toString());
            lblBank.setValue(bankOperation.getBankId().getName());
            lblType.setValue(bankOperation.getBankOperationTypeId().getName());
            lblMode.setValue(bankOperation.getBankOperationModeId().getName());
            lblProduct.setValue(bankOperation.getProductId().getName());
            lblTransaction.setValue(bankOperation.getTransactionId().getId().toString());
            lblNumber.setValue(bankOperation.getBankOperationNumber());
            lblUser.setValue(bankOperation.getUserSourceId().toString());
            lblStatus.setValue(bankOperation.getTransactionId().getTransactionStatus());
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    
    public void blockFields() {
    	
    }

    public void onClick$btnCancel() {
        clearFields();
    }

   

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_VIEW:
                loadFields(bankOperationParam);
                blockFields();
                break;         
            default:
                break;
        }
    }

    public void onClick$btnBack() {
    	winAdminBankOperations.detach();
    }
 
}
