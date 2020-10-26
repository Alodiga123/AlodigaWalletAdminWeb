package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.BusinessEJB;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.enumeraciones.StatusTransactionApproveRequestE;
import com.alodiga.wallet.common.enumeraciones.TransactionSourceE;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.BankOperation;
import com.alodiga.wallet.common.model.CommissionItem;
import com.alodiga.wallet.common.model.StatusTransactionApproveRequest;
import com.alodiga.wallet.common.model.TransactionApproveRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import com.ericsson.alodiga.ws.APIRegistroUnificadoProxy;
import com.ericsson.alodiga.ws.RespuestaUsuario;
import com.portal.business.commons.models.Business;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminManualWithdrawalApprovalController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private Label lblProduct;
    private Label lblTransactionConcept;
    private Label lblTransactionDate;
    private Label lblBank;
    private Label lblBankOperation;
    private Label lblUserSource;
    private Label lblUser;
    private Label lblTransactionNumber;
    private Label lblBankOperationDate;
    private Label lblTelephone;
    private Label lblEmail;
    private Label lblUserDocumentType;
    private Label lblUserDocumentNumber;
    private Datebox dtbApprovedRequestDate;
    private Textbox txtObservations;
    private Doublebox dblAmount;
    private Doublebox dblCommision;
    private Float totalAmount = 0F;
    private Label dblBankOperationAmount;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private BusinessEJB businessEJB = null;
    private ProductEJB productEJB = null;
    private TransactionApproveRequest manualWithdrawalApprovalParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    private User user = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Sessions.getCurrent();
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            manualWithdrawalApprovalParam = null;
        } else {
            manualWithdrawalApprovalParam = (TransactionApproveRequest) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
        initView(eventType, "crud.manualWithdrawalApproval");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.manualWithdrawalApproval.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.manualWithdrawalApproval.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.manualWithdrawalApproval.add"));
                break;
            default:
                break;
        }
        try {
            user = AccessControl.loadCurrentUser();
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            businessEJB = (BusinessEJB) EJBServiceLocator.getInstance().get(EjbConstants.BUSINESS_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            dtbApprovedRequestDate.setValue(new Timestamp(new java.util.Date().getTime()));
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        dtbApprovedRequestDate.setRawValue(null);
        txtObservations.setRawValue(null);
    }

    private void loadFields(TransactionApproveRequest manualWithdrawalApproval) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            if(manualWithdrawalApproval.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.APPBIL.getTransactionSourceCode())){
               //Obtiene el usuario de origen de Registro Unificado relacionado con la Transacción
                APIRegistroUnificadoProxy apiRegistroUnificado = new APIRegistroUnificadoProxy();
                RespuestaUsuario responseUser = new RespuestaUsuario();
                responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",String.valueOf(manualWithdrawalApproval.getUnifiedRegistryUserId()));
                String userNameSource = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
                lblUserSource.setValue(userNameSource);
                lblTelephone.setValue(responseUser.getDatosRespuesta().getTelefonoResidencial());
                lblEmail.setValue(responseUser.getDatosRespuesta().getEmail());
                lblUserDocumentType.setValue(responseUser.getDatosRespuesta().getTipoDocumento().getNombre());
                lblUserDocumentNumber.setValue(responseUser.getDatosRespuesta().getNumeroDocumento());
            
            } else if(manualWithdrawalApproval.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.PORNEG.getTransactionSourceCode())) {
                //Obtiene el negocio de origen de BusinessPortal relacionado con la Transacción
                Business businessSource = businessEJB.getBusinessById(manualWithdrawalApproval.getTransactionId().getBusinessId().longValue());
                lblUserSource.setValue(businessSource.getDisplayName());
                lblTelephone.setValue(businessSource.getPhoneNumber());
                lblEmail.setValue(businessSource.getEmail());
            }
            if (manualWithdrawalApproval.getRequestNumber() != null) {
                lblRequestNumber.setValue(manualWithdrawalApproval.getRequestNumber());
            }
            if (manualWithdrawalApproval.getRequestDate() != null) {
                lblRequestDate.setValue(simpleDateFormat.format(manualWithdrawalApproval.getRequestDate()));
            }
            lblStatusRequest.setValue(manualWithdrawalApproval.getStatusTransactionApproveRequestId().getDescription());
            lblProduct.setValue(manualWithdrawalApproval.getProductId().getName());
            if (manualWithdrawalApproval.getTransactionId().getConcept() != null) {
                lblTransactionConcept.setValue(manualWithdrawalApproval.getTransactionId().getConcept());
            }
            lblTransactionDate.setValue(simpleDateFormat.format(manualWithdrawalApproval.getTransactionId().getCreationDate()));
            if (manualWithdrawalApproval.getTransactionId().getCreationDate() != null) {
                lblRequestDate.setValue(simpleDateFormat.format(manualWithdrawalApproval.getTransactionId().getCreationDate()));
            }
            if (manualWithdrawalApproval.getApprovedRequestDate() != null) {
                dtbApprovedRequestDate.setValue(manualWithdrawalApproval.getApprovedRequestDate());
            }
            if (manualWithdrawalApproval.getIndApproveRequest() != null) {
                if (manualWithdrawalApproval.getIndApproveRequest() == true) {
                    rApprovedYes.setChecked(true);
                } else {
                    rApprovedNo.setChecked(true);
                }
            }
            lblUser.setValue(user.getFirstName() + " " + user.getLastName());
            if (manualWithdrawalApproval.getObservations() != null) {
                txtObservations.setValue(manualWithdrawalApproval.getObservations());
            }

            lblBank.setValue(manualWithdrawalApproval.getBankOperationId().getBankId().getName());
            lblBankOperation.setValue(manualWithdrawalApproval.getBankOperationId().getBankOperationNumber());
            
            if (manualWithdrawalApproval.getTransactionId().getAmount() != 0) {
                dblAmount.setValue(manualWithdrawalApproval.getTransactionId().getAmount());
                totalAmount = manualWithdrawalApproval.getTransactionId().getAmount();
            }
            lblTransactionNumber.setValue(manualWithdrawalApproval.getTransactionId().getTransactionNumber());
            lblBankOperationDate.setValue(simpleDateFormat.format(manualWithdrawalApproval.getBankOperationId().getBankOperationDate()));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        dtbApprovedRequestDate.setDisabled(true);
        rApprovedYes.setDisabled(true);
        rApprovedNo.setDisabled(true);
        txtObservations.setDisabled(true);
        dblAmount.setDisabled(true);
        dblCommision.setDisabled(true);

        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (dtbApprovedRequestDate.getText().isEmpty()) {
            dtbApprovedRequestDate.setFocus(true);
            this.showMessage("sp.error.date.approvedRequestDate", true, null);
        } else if ((!rApprovedYes.isChecked()) && (!rApprovedNo.isChecked())) {
            this.showMessage("sp.error.indApproveRequest", true, null);
        } else if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("sp.error.observations", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    private void saveManualWithdrawalApproval(TransactionApproveRequest _manualWithdrawalApproval) {
        StatusTransactionApproveRequest statusTransactionApproveRequest;
        EJBRequest request = new EJBRequest();
        HashMap params = new HashMap();
        TransactionApproveRequest manualWithdrawalApproval = null;
        BankOperation bankOperation = null;
        boolean indApproved;
        
        try {
            if (_manualWithdrawalApproval != null) {
                manualWithdrawalApproval = _manualWithdrawalApproval;
                bankOperation = manualWithdrawalApproval.getBankOperationId();
            } else {
                manualWithdrawalApproval = new TransactionApproveRequest();
            }
            
            if (rApprovedYes.isChecked()) {
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

            //Se actualiza el estatus de la solicitud de aprobación y el saldo del usuario en la billetera
            manualWithdrawalApproval.setUnifiedRegistryUserId(bankOperation.getUserSourceId());
            manualWithdrawalApproval.setStatusTransactionApproveRequestId(statusTransactionApproveRequest);
            manualWithdrawalApproval.setUpdateDate(new Timestamp(new Date().getTime()));
            manualWithdrawalApproval.setApprovedRequestDate(dtbApprovedRequestDate.getValue());
            manualWithdrawalApproval.setIndApproveRequest(indApproved);
            manualWithdrawalApproval.setObservations(txtObservations.getText());
            manualWithdrawalApproval.setUserApprovedRequestId(user);
            manualWithdrawalApproval = productEJB.saveTransactionApproveRequest(manualWithdrawalApproval);
            manualWithdrawalApproval = productEJB.updateTransactionApproveRequest(manualWithdrawalApproval);
            manualWithdrawalApprovalParam = manualWithdrawalApproval;
            
            if(indApproved == true){
                 this.showMessage("sp.crud.manualWithdrawalApprova.recharge.saveApproved", false, null);  
            } else {
                 this.showMessage("sp.crud.manualWithdrawalApprova.recharge.saveRejected", false, null);  
            }

            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
            } else {
                btnSave.setVisible(true);
            }
        } catch (Exception ex) {
            this.showMessage("sp.msj.errorSave", true, null);
        }
    }

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveManualWithdrawalApproval(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveManualWithdrawalApproval(manualWithdrawalApprovalParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(manualWithdrawalApprovalParam);
                dblAmount.setDisabled(true);
                dblCommision.setDisabled(true);
                dtbApprovedRequestDate.setDisabled(true);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(manualWithdrawalApprovalParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
