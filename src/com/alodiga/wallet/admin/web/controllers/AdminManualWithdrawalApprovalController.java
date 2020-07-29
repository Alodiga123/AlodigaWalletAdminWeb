package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.model.TransactionApproveRequest;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminManualWithdrawalApprovalController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private Label lblProduct;
    private Label lblTransaction;
    private Datebox dtbApprovedRequestDate;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private Textbox txtObservations;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private TransactionApproveRequest manualWithdrawalApprovalParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

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
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
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
            if(manualWithdrawalApproval.getRequestNumber() != null){
                lblRequestNumber.setValue(manualWithdrawalApproval.getRequestNumber());
            }
            if(manualWithdrawalApproval.getRequestDate() != null){
                lblRequestDate.setValue(simpleDateFormat.format(manualWithdrawalApproval.getRequestDate()));
            }
            
            lblStatusRequest.setValue(manualWithdrawalApproval.getStatusTransactionApproveRequestId().getDescription());

            lblProduct.setValue(manualWithdrawalApproval.getProductId().getName());
            if (manualWithdrawalApproval.getTransactionId().getPaymentInfoId() != null) {
                lblTransaction.setValue(manualWithdrawalApproval.getTransactionId().getPaymentInfoId().getCreditCardName());
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
            if (manualWithdrawalApproval.getObservations() != null) {
                txtObservations.setValue(manualWithdrawalApproval.getObservations());
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
        boolean indApprovedRequest;
        try {
            TransactionApproveRequest manualWithdrawalApproval = null;
            if (_manualWithdrawalApproval != null) {
                manualWithdrawalApproval = _manualWithdrawalApproval;
            } else {//New DocumentsPersonType
                manualWithdrawalApproval = new TransactionApproveRequest();
            }

            if (rApprovedYes.isChecked()) {
                indApprovedRequest = true;
            } else {
                indApprovedRequest = false;
            }

            manualWithdrawalApproval.setApprovedRequestDate(dtbApprovedRequestDate.getValue());
            manualWithdrawalApproval.setIndApproveRequest(indApprovedRequest);
            manualWithdrawalApproval.setObservations(txtObservations.getText());
            manualWithdrawalApproval = utilsEJB.saveTransactionApproveRequest(manualWithdrawalApproval);
            manualWithdrawalApprovalParam = manualWithdrawalApproval;
            this.showMessage("sp.common.save.success", false, null);

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
