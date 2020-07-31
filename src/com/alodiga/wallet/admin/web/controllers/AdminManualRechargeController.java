package com.alodiga.wallet.admin.web.controllers;

import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.alodiga.businessportal.ws.BPBusinessWSProxy;
import com.alodiga.businessportal.ws.BpBusiness;
import com.alodiga.businessportal.ws.BusinessSearchType;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.model.TransactionApproveRequest;
import com.alodiga.wallet.common.model.User;

public class AdminManualRechargeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblRequestStatus;
    private Label lblProduct;
    private Label lblConcept;
    private Label lblTransactionNumber;
    private Label lblTransactionDate;
    private Label lblAmount;
    private Label lblBank;
    private Label lblBankOperationNumber;
    private Label lblBankOperationDate;
    private Label lblApprovalUser;
    private Label lblApprovalDate;
    private Label lblUserName;
    private Label lblTelephone;
    private Label lblEmail;
    private Checkbox chbApprovalIndicator;
    private TransactionApproveRequest transactionApproveRequest;
    private User user=null;
    private Textbox txtObservation;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        transactionApproveRequest = (Sessions.getCurrent().getAttribute("object") != null) ? (TransactionApproveRequest) Sessions.getCurrent().getAttribute("object") : null;
        initialize();
        initView(eventType, "sp.crud.manual.recharge");
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
			user = AccessControl.loadCurrentUser();
			loadData();
		} catch (Exception e) {

		}

    }

    public void clearFields() {
    	chbApprovalIndicator.setDisabled(true);
    	txtObservation.setReadonly(true);
    }

    private void loadFields(TransactionApproveRequest transactionApproveRequest) {
        try {
        	lblRequestNumber.setValue(transactionApproveRequest.getRequestNumber());
        	lblRequestDate.setValue(transactionApproveRequest.getCreateDate().toString());
        	lblRequestStatus.setValue(transactionApproveRequest.getStatusTransactionApproveRequestId().getDescription());
        	lblProduct.setValue(transactionApproveRequest.getTransactionId().getProductId().getName());
        	lblConcept.setValue(transactionApproveRequest.getTransactionId().getConcept());
        	lblTransactionNumber.setValue(transactionApproveRequest.getTransactionId().getTransactionNumber());
        	lblTransactionDate.setValue(transactionApproveRequest.getTransactionId().getCreationDate().toString());
            lblAmount.setValue(String.valueOf(transactionApproveRequest.getTransactionId().getAmount()));
            lblBank.setValue(transactionApproveRequest.getBankOperationId().getBankId().getName());
            lblBankOperationNumber.setValue(transactionApproveRequest.getBankOperationId().getBankOperationNumber());
            lblBankOperationDate.setValue(transactionApproveRequest.getBankOperationId().getBankOperationDate().toString());
            lblApprovalUser.setValue(user.getFirstName()+" "+user.getLastName());
            Date date = new Date();
            lblApprovalDate.setValue(date.toString());
            chbApprovalIndicator.setChecked(transactionApproveRequest.getIndApproveRequest());
            txtObservation.setValue(transactionApproveRequest.getObservations());
            BPBusinessWSProxy proxy = new BPBusinessWSProxy();
            try {
            	BpBusiness bpBussiness = proxy.getBusiness(BusinessSearchType.ID, String.valueOf(transactionApproveRequest.getUnifiedRegistryUserId()));
            	lblUserName.setValue(bpBussiness.getName()); 
            	lblTelephone.setValue(bpBussiness.getPhoneNumber()); 
            	lblEmail.setValue(bpBussiness.getEmail()); 
            } catch (Exception e) {
            	this.showMessage("sp.specific.preference.error.search", true, null);  
            }
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
                loadFields(transactionApproveRequest);
                blockFields();
                break;  
            case WebConstants.EVENT_EDIT:
                loadFields(transactionApproveRequest);
                break;  
            default:
                break;
        }
    }
    
    public void onClick$btnSave() {
			switch (eventType) {
			case WebConstants.EVENT_EDIT:
				saveTransactionApproveRequest();
				break;
			default:
				break;
			}
	}

   private void saveTransactionApproveRequest() {
   }
 
}
