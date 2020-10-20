package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.BusinessEJB;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
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
            } else if(manualWithdrawalApproval.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.PORNEG.getTransactionSourceCode())) {
                //Obtiene el negocio de origen de BusinessPortal relacionado con la Transacción
                Business businessSource = businessEJB.getBusinessById(manualWithdrawalApproval.getTransactionId().getBusinessId().longValue());
                lblUserSource.setValue(businessSource.getDisplayName());
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
        List<StatusTransactionApproveRequest> statusApproved = new ArrayList<StatusTransactionApproveRequest>();
        List<StatusTransactionApproveRequest> statusRejected = new ArrayList<StatusTransactionApproveRequest>();
        StatusTransactionApproveRequest status = null;
        TransactionApproveRequest manualWithdrawalApproval = null;
        BankOperation bankOperation = null;

        boolean indApprovedRequest;
        try {
            if (_manualWithdrawalApproval != null) {
                manualWithdrawalApproval = _manualWithdrawalApproval;
                bankOperation = manualWithdrawalApproval.getBankOperationId();
            } else {//New DocumentsPersonType
                manualWithdrawalApproval = new TransactionApproveRequest();
            }

            if (rApprovedYes.isChecked()) {
                indApprovedRequest = true;

                //Se cambia el estado para aprobada
                EJBRequest statusA = new EJBRequest();
                Map params = new HashMap();
                params = new HashMap();
                params.put(QueryConstants.PARAM_CODE, Constants.STATUS_TRANSACTIONS_APPR);
                statusA.setParams(params);
                statusApproved = utilsEJB.getStatusTransactionApproveRequestPending(statusA);

                if (statusApproved != null) {
                    for (StatusTransactionApproveRequest s : statusApproved) {
                        status = s;
                    }
                }
            } else {
                indApprovedRequest = false;

                //Se cambia el estatus a rechazada
                EJBRequest statusR = new EJBRequest();
                Map params = new HashMap();
                params = new HashMap();
                params.put(QueryConstants.PARAM_CODE, Constants.STATUS_TRANSACTIONS_REJE);
                statusR.setParams(params);
                statusRejected = utilsEJB.getStatusTransactionApproveRequestPending(statusR);

                if (statusRejected != null) {
                    for (StatusTransactionApproveRequest s : statusRejected) {
                        status = s;
                    }
                }
            }

            manualWithdrawalApproval.setUnifiedRegistryUserId(bankOperation.getUserSourceId());
            manualWithdrawalApproval.setStatusTransactionApproveRequestId(status);
            manualWithdrawalApproval.setUpdateDate(new Timestamp(new Date().getTime()));
            manualWithdrawalApproval.setApprovedRequestDate(dtbApprovedRequestDate.getValue());
            manualWithdrawalApproval.setIndApproveRequest(indApprovedRequest);
            manualWithdrawalApproval.setObservations(txtObservations.getText());
            manualWithdrawalApproval.setUserApprovedRequestId(user);
            manualWithdrawalApproval = utilsEJB.saveTransactionApproveRequest(manualWithdrawalApproval);
            manualWithdrawalApprovalParam = manualWithdrawalApproval;
            if(indApprovedRequest == true){
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
