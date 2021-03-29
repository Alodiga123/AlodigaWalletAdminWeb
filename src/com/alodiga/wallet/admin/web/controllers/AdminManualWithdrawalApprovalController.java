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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
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
    private Label lblUserSource;
    private Label lblUser;
    private Label lblTransactionNumber;
    private Textbox txtBankNumber;
    private Datebox dtbBankOperationDate;
    private Label lblTelephone;
    private Label lblEmail;
    private Label lblUserDocumentType;
    private Label lblUserDocumentNumber;
    private Label lblBankAccount;
    private Datebox dtbApprovedRequestDate;
    private Textbox txtObservations;
    private Textbox txtResponsible;
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
    List<CommissionItem> commissionItemList = new ArrayList<CommissionItem>();

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
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.manualWithdrawalApproval.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.manualWithdrawalApproval.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.manualWithdrawalApproval.add"));
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
                lblUserDocumentNumber.setValue(businessSource.getIdentification());
            }            
            
            //Datos de la Solicitud
            if (manualWithdrawalApproval.getRequestNumber() != null) {
                lblRequestNumber.setValue(manualWithdrawalApproval.getRequestNumber());
            }
            if (manualWithdrawalApproval.getRequestDate() != null) {
                lblRequestDate.setValue(simpleDateFormat.format(manualWithdrawalApproval.getRequestDate()));
            }
            lblStatusRequest.setValue(manualWithdrawalApproval.getStatusTransactionApproveRequestId().getDescription());
            
            //Datos de la Transacción
            lblTransactionNumber.setValue(manualWithdrawalApproval.getTransactionId().getTransactionNumber());
            lblTransactionDate.setValue(simpleDateFormat.format(manualWithdrawalApproval.getTransactionId().getCreationDate()));
            lblProduct.setValue(manualWithdrawalApproval.getProductId().getName());
            if (manualWithdrawalApproval.getTransactionId().getAmount() != 0) {
                dblAmount.setValue(manualWithdrawalApproval.getTransactionId().getAmount());
            }
            
            if (manualWithdrawalApproval.getTransactionId().getConcept() != null) {
                lblTransactionConcept.setValue(manualWithdrawalApproval.getTransactionId().getConcept());
            }
            
            //Datos de Aprobación de la Solicitud
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
            
            //Datos de la operación bancaria
            lblBank.setValue(manualWithdrawalApproval.getBankOperationId().getBankId().getName());
            if (manualWithdrawalApproval.getBankOperationId().getAccountBankId().getAccountNumber() != null) {
                lblBankAccount.setValue(manualWithdrawalApproval.getBankOperationId().getAccountBankId().getAccountNumber());
            }            
            if (manualWithdrawalApproval.getBankOperationId() != null) {
                txtBankNumber.setValue(manualWithdrawalApproval.getBankOperationId().getBankOperationNumber());
            }
            if (manualWithdrawalApproval.getBankOperationId().getResponsible() != null) {
                txtResponsible.setValue(manualWithdrawalApproval.getBankOperationId().getResponsible());
            }
            if (manualWithdrawalApproval.getBankOperationId().getBankOperationDate() != null) {
                dtbBankOperationDate.setValue(manualWithdrawalApproval.getBankOperationId().getBankOperationDate());
            }
            
            List<CommissionItem> commissionItems = utilsEJB.getCommissionItems(manualWithdrawalApproval.getTransactionId().getId());
            if(commissionItems  != null){
                for(CommissionItem comission : commissionItems){
                    dblCommision.setValue(comission.getAmount());
                }
            }
               
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
        txtBankNumber.setDisabled(true);
        txtResponsible.setDisabled(true);
        dtbBankOperationDate.setDisabled(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {        
        if (dtbApprovedRequestDate.getText().isEmpty()) {
            dtbApprovedRequestDate.setFocus(true);
            this.showMessage("msj.error.date.approvedRequestDate", true, null);
        } else if ((!rApprovedYes.isChecked()) && (!rApprovedNo.isChecked())) {
            this.showMessage("msj.error.indApproveRequest", true, null);
        } else if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("msj.error.observations", true, null);
        } else if (txtBankNumber.getText().isEmpty() || txtBankNumber.getText() == "") {
            txtBankNumber.setFocus(true);
            this.showMessage("msj.error.date.bankNumberOperation", true, null);
        } else if (txtResponsible.getText().isEmpty()) {
            txtResponsible.setFocus(true);
            this.showMessage("msj.error.date.responsibleBankOperation", true, null);
        } else if (dtbBankOperationDate.getText().isEmpty()) {
            dtbBankOperationDate.setFocus(true);
            this.showMessage("msj.error.error.date.bankOperationDate", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public boolean validateManualWithdrawal() {
        Date currentDate = new Date(Calendar.getInstance().getTimeInMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String toDay = formatter.format(currentDate);
        String dateBankOperation = formatter.format(dtbBankOperationDate.getValue());;
        
        if(!toDay.equals(dateBankOperation)){
            this.showMessage("msj.error.date.dateBankOperation", true, null);            
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
            
            //Se actualiza el responsable, numero y fecha en la operación bancaria
            bankOperation.setBankOperationNumber(txtBankNumber.getValue().toString());
            bankOperation.setResponsible(txtResponsible.getText());
            bankOperation.setBankOperationDate(dtbBankOperationDate.getValue());
            bankOperation.setUpdateDate(new Timestamp(new Date().getTime()));
            bankOperation = utilsEJB.saveBankOperation(bankOperation);
            
            //Se actualiza el estatus de la solicitud de aprobación y el saldo del usuario en la billetera
            manualWithdrawalApproval.setUnifiedRegistryUserId(bankOperation.getUserSourceId());
            manualWithdrawalApproval.setStatusTransactionApproveRequestId(statusTransactionApproveRequest);
            manualWithdrawalApproval.setUpdateDate(new Timestamp(new Date().getTime()));
            manualWithdrawalApproval.setApprovedRequestDate(dtbApprovedRequestDate.getValue());
            manualWithdrawalApproval.setIndApproveRequest(indApproved);
            manualWithdrawalApproval.setObservations(txtObservations.getText());
            manualWithdrawalApproval.setUserApprovedRequestId(user);
            manualWithdrawalApproval = productEJB.updateTransactionApproveRequest(manualWithdrawalApproval);
            manualWithdrawalApprovalParam = manualWithdrawalApproval;
               
            if(indApproved == true){
                 this.showMessage("msj.manualWithdrawalApprova.recharge.saveApproved", false, null);  
            } else {
                 this.showMessage("msj.error.manualWithdrawalApprova.recharge.saveRejected", false, null);  
            }

            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
            } else {
                btnSave.setVisible(true);
            }
        } catch (Exception ex) {
            this.showMessage("msj.error.errorSave", true, null);
        }
    }

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            if (validateManualWithdrawal()) {
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
