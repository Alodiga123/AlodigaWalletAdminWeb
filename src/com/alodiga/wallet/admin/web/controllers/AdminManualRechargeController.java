package com.alodiga.wallet.admin.web.controllers;

import java.sql.Timestamp;
import java.util.Date;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.common.enumeraciones.TransactionSourceE;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.BusinessEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.model.TransactionApproveRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.ericsson.alodiga.ws.APIRegistroUnificadoProxy;
import com.ericsson.alodiga.ws.RespuestaUsuario;
import com.portal.business.commons.models.Business;
import java.text.SimpleDateFormat;
import org.zkoss.zul.Radio;

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
    private Datebox dtbApprovedRequestDate;
    private Label lblUserName;
    private Label lblTelephone;
    private Label lblEmail;
    private Radio rIsApprovedYes;
    private Radio rIsApprovedNo;
    private TransactionApproveRequest transactionApproveRequest;
    private User user=null;
    private Textbox txtObservation;
    private ProductEJB productEJB = null;
    private BusinessEJB businessEJB = null;
    private Button btnSave;

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
        	productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
                businessEJB = (BusinessEJB) EJBServiceLocator.getInstance().get(EjbConstants.BUSINESS_EJB);
		user = AccessControl.loadCurrentUser();
			loadData();
		} catch (Exception e) {
        }
    }

    public void clearFields() {
    	txtObservation.setReadonly(true);
    }

    private void loadFields(TransactionApproveRequest transactionApproveRequest) {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            if(transactionApproveRequest.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.APPBIL.getTransactionSourceCode())){
                //Obtiene los usuarios de Origen de Registro Unificado relacionados con la Transacción
                APIRegistroUnificadoProxy apiRegistroUnificado = new APIRegistroUnificadoProxy();
                RespuestaUsuario responseUser = new RespuestaUsuario();
                responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",String.valueOf(transactionApproveRequest.getUnifiedRegistryUserId()));
                String userNameSource = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
                lblUserName.setValue(userNameSource);
            } else if(transactionApproveRequest.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.PORNEG.getTransactionSourceCode())){
                //Obtiene los usuarios de Origen de BusinessPortal relacionados con la Transacción
                Business businessSource = businessEJB.getBusinessById(transactionApproveRequest.getTransactionId().getBusinessId().longValue());
                lblUserName.setValue(businessSource.getDisplayName());
            }
            lblRequestNumber.setValue(transactionApproveRequest.getRequestNumber());
            lblRequestDate.setValue(simpleDateFormat.format(transactionApproveRequest.getCreateDate()));
            lblRequestStatus.setValue(transactionApproveRequest.getStatusTransactionApproveRequestId().getDescription());
            lblProduct.setValue(transactionApproveRequest.getTransactionId().getProductId().getName());
            lblConcept.setValue(transactionApproveRequest.getTransactionId().getConcept());
            lblTransactionNumber.setValue(transactionApproveRequest.getTransactionId().getTransactionNumber());
            lblTransactionDate.setValue(simpleDateFormat.format(transactionApproveRequest.getTransactionId().getCreationDate()));
            lblAmount.setValue(String.valueOf(transactionApproveRequest.getTransactionId().getAmount()));
            lblBank.setValue(transactionApproveRequest.getBankOperationId().getBankId().getName());
            if (transactionApproveRequest.getBankOperationId().getBankOperationNumber() != null) {
                lblBankOperationNumber.setValue(transactionApproveRequest.getBankOperationId().getBankOperationNumber());
            }
            if (transactionApproveRequest.getBankOperationId().getBankOperationDate() != null) {
                lblBankOperationDate.setValue(simpleDateFormat.format(transactionApproveRequest.getBankOperationId().getBankOperationDate()));
            }            
            lblApprovalUser.setValue(user.getFirstName()+" "+user.getLastName());
            dtbApprovedRequestDate.setValue(new Timestamp(new java.util.Date().getTime()));
            if (transactionApproveRequest.getIndApproveRequest() == true) {
                rIsApprovedYes.setChecked(true);
            } else {
                rIsApprovedNo.setChecked(true);
            }
            txtObservation.setValue(transactionApproveRequest.getObservations());
            
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void blockFields() {
    	 dtbApprovedRequestDate.setDisabled(true);
         rIsApprovedYes.setDisabled(true);
         rIsApprovedNo.setDisabled(true);
         txtObservation.setReadonly(true);
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
                dtbApprovedRequestDate.setReadonly(true);
                break;  
            default:
                break;
        }
    }
    
    public void onClick$btnSave() {
        switch (eventType) {
        case WebConstants.EVENT_EDIT:
            saveTransactionApproveRequest(transactionApproveRequest);
        break;
        default:
            break;
        }
    }

   private void saveTransactionApproveRequest(TransactionApproveRequest manualRechargeApproval) {       
        boolean indApproved  = true;
        if (rIsApprovedYes.isChecked()) {
            indApproved = true;
        } else {
            indApproved = false;
        }
        manualRechargeApproval.setUpdateDate(new Date());
        manualRechargeApproval.setApprovedRequestDate(dtbApprovedRequestDate.getValue());
        manualRechargeApproval.setIndApproveRequest(indApproved);
        manualRechargeApproval.setObservations(txtObservation.getText());
        manualRechargeApproval.setUserApprovedRequestId(user);
        try {
            manualRechargeApproval = productEJB.updateTransactionApproveRequest(manualRechargeApproval);
            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
       } catch (Exception ex) {
           this.showMessage("sp.msj.errorSave", true, null);
       }
   }
 
}
