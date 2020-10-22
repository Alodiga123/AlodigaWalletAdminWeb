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
import com.alodiga.wallet.common.enumeraciones.StatusTransactionApproveRequestE;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.BusinessEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.StatusTransactionApproveRequest;
import com.alodiga.wallet.common.model.TransactionApproveRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.ericsson.alodiga.ws.APIRegistroUnificadoProxy;
import com.ericsson.alodiga.ws.RespuestaUsuario;
import com.portal.business.commons.models.Business;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
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
    public static TransactionApproveRequest transactionApproveRequestParam;

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
    
    public TransactionApproveRequest getTransactionAprroveRequest() {
        return transactionApproveRequestParam;
    }

    public void clearFields() {
    	txtObservation.setReadonly(true);
    }

    private void loadFields(TransactionApproveRequest transactionApproveRequest) {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            if(transactionApproveRequest.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.APPBIL.getTransactionSourceCode())){
                if (transactionApproveRequest.getTransactionId().getUserSourceId() != null) {
                    //Obtiene el usuario de origen de Registro Unificado relacionado con la Transacción
                    APIRegistroUnificadoProxy apiRegistroUnificado = new APIRegistroUnificadoProxy();
                    RespuestaUsuario responseUser = new RespuestaUsuario();
                    responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",String.valueOf(transactionApproveRequest.getUnifiedRegistryUserId()));
                    String userNameSource = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
                    lblUserName.setValue(userNameSource);
                    lblTelephone.setValue(responseUser.getDatosRespuesta().getMovil());
                    lblEmail.setValue(responseUser.getDatosRespuesta().getEmail());
                }
                if (transactionApproveRequest.getTransactionId().getBusinessId() != null) {
                    //Obtiene el negocio de origen de BusinessPortal relacionado con la Transacción
                    Business businessSource = businessEJB.getBusinessById(transactionApproveRequest.getTransactionId().getBusinessId().longValue());
                    lblUserName.setValue(businessSource.getDisplayName());
                    lblTelephone.setValue(businessSource.getPhoneNumber());
                    lblEmail.setValue(businessSource.getEmail());
                }                
            } else if(transactionApproveRequest.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.PORNEG.getTransactionSourceCode())){
                //Obtiene el negocio de origen de BusinessPortal relacionado con la Transacción
                Business businessSource = businessEJB.getBusinessById(transactionApproveRequest.getTransactionId().getBusinessId().longValue());
                lblUserName.setValue(businessSource.getDisplayName());
                lblTelephone.setValue(businessSource.getPhoneNumber());
                lblEmail.setValue(businessSource.getEmail());
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
        StatusTransactionApproveRequest statusTransactionApproveRequest;
        EJBRequest request = new EJBRequest();
        HashMap params = new HashMap();
        
        try{              
            if (rIsApprovedYes.isChecked()) {
                indApproved = true;              
                params.put(Constants.PARAM_CODE, StatusTransactionApproveRequestE.APROBA.getStatusTransactionApproveRequestCode() );
                request.setParams(params);
                //Obtener el estatus "Transacción Aprobada"
                statusTransactionApproveRequest = productEJB.loadStatusTransactionApproveRequestbyCode(request); 
            } else {
                indApproved = false;
                params.put(Constants.PARAM_CODE, StatusTransactionApproveRequestE.RECHAZ.getStatusTransactionApproveRequestCode());
                request.setParams(params);
                //Obtener el estatus "Transacción Rechazada"
                statusTransactionApproveRequest = productEJB.loadStatusTransactionApproveRequestbyCode(request);               
            }      

            //Se crea el objeto manualRechargeApproval
            manualRechargeApproval.setStatusTransactionApproveRequestId(statusTransactionApproveRequest);
            manualRechargeApproval.setUpdateDate(new Date());
            manualRechargeApproval.setApprovedRequestDate(new Date());
            manualRechargeApproval.setUserApprovedRequestId(user);
            manualRechargeApproval.setIndApproveRequest(indApproved);
            manualRechargeApproval.setObservations(txtObservation.getText());
            manualRechargeApproval = productEJB.updateTransactionApproveRequest(manualRechargeApproval);
            transactionApproveRequestParam = manualRechargeApproval;

            if(indApproved == true){
              this.showMessage("sp.crud.manual.recharge.saveApproved", false, null);  
            } else {
              this.showMessage("sp.crud.manual.recharge.saveRejected", false, null);  
            }
            btnSave.setVisible(false);
        } catch (Exception ex) {
            this.showMessage("sp.msj.errorSave", true, null);
        }
    }
 
}
