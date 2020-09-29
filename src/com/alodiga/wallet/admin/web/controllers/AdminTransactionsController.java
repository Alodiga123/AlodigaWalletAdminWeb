package com.alodiga.wallet.admin.web.controllers;

import java.util.List;

import com.ericsson.alodiga.ws.APIRegistroUnificadoProxy;
import com.ericsson.alodiga.ws.RespuestaUsuario;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.model.CommissionItem;
import com.alodiga.wallet.common.model.Transaction;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class AdminTransactionsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblUserSource;
    private Label lblUserDestination;
    private Label lblProduct;
    private Label lblPaymentInfo;
    private Label lblTransactionType;
    private Label lblTransactionSource;
    private Label lblTransactionDate;
    private Label lblAmount;
    private Label lblStatus;
    private Label lblTotalTax;
    private Label lblTotalAmount;
    private Label lblPromotionAmount;
    private Label lblTotalAlopointsUsed;
    private Label lblTopUpDescription;
    private Label lblBillPaymentDescription;
    private Label lblExternal;
    private Label lblAdditional;
    private Label lblAdditional2;
    private Label lblClose;
    private Label lblConcept;
    private Label lblAmountComission;
    private Label lblComissionValue;
    private Checkbox chbPercentComission;
    private Grid grdCommision;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private Transaction transactionParam;
    private Integer eventType;
    private UtilsEJB utilsEJB = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            transactionParam = null;
        } else {
            transactionParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Transaction) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.transaction");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.transaction.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.transaction.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.exchangeRate.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void buscarUsuario() {

    }

    private void loadFields(Transaction transaction) {
        try {
            APIRegistroUnificadoProxy apiRegistroUnificado = new APIRegistroUnificadoProxy();
            RespuestaUsuario responseUser = new RespuestaUsuario();
            responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",String.valueOf(transaction.getUserSourceId()));
            String userName = responseUser.getDatosRespuesta().getNombre();
            lblUserSource.setValue(transaction.getUserSourceId().toString());
            if (transaction.getUserDestinationId() != null) {
                lblUserDestination.setValue(transaction.getUserDestinationId().toString());
            } else {
                lblUserDestination.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getProductId().getName() != null) {
                lblProduct.setValue(transaction.getProductId().getName());
            } else {
                lblProduct.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getPaymentInfoId() != null) {
                lblPaymentInfo.setValue(transaction.getPaymentInfoId().getCreditCardName());
            } else {
                lblPaymentInfo.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getTransactionTypeId() != null) {
                lblTransactionType.setValue(transaction.getTransactionTypeId().getValue());
            } else {
                lblTransactionType.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getTransactionSourceId() != null) {
                lblTransactionSource.setValue(transaction.getTransactionSourceId().getName());
            } else {
                lblTransactionSource.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            lblTransactionDate.setValue(transaction.getCreationDate().toString());
            lblAmount.setValue(String.valueOf(transaction.getAmount()));
            lblStatus.setValue(transaction.getTransactionStatus());
            if (transaction.getTotalTax() != null) {
                lblTotalTax.setValue(transaction.getTotalTax().toString());
            } else {
                lblTotalTax.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (String.valueOf(transaction.getTotalAmount()) != null) {
                lblTotalAmount.setValue(String.valueOf(transaction.getTotalAmount()));
            } else {
                lblTotalAmount.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getPromotionAmount() != null) {
                lblPromotionAmount.setValue(transaction.getPromotionAmount().toString());
            } else {
                lblPromotionAmount.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getTotalAlopointsUsed() != null) {
                lblTotalAlopointsUsed.setValue(transaction.getTotalAlopointsUsed().toString());
            } else {
                lblTotalAlopointsUsed.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getTopUpDescription() != null) {
                lblTopUpDescription.setValue(transaction.getTopUpDescription());
            } else {
                lblTopUpDescription.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getBillPaymentDescription() != null) {
                lblBillPaymentDescription.setValue(transaction.getBillPaymentDescription());
            } else {
                lblBillPaymentDescription.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getExternalId() != null) {
                lblExternal.setValue(transaction.getExternalId());
            } else {
                lblExternal.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getAdditional() != null) {
                lblAdditional.setValue(transaction.getAdditional());
            } else {
                lblAdditional.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getAdditional2() != null) {
                lblAdditional2.setValue(transaction.getAdditional2());
            } else {
                lblAdditional2.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getConcept() != null) {
                lblConcept.setValue(transaction.getConcept());
            } else {
                lblConcept.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getCloseId() != null) {
                lblClose.setValue(transaction.getCloseId().getId().toString());
            } else {
                lblClose.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            try {
                List<CommissionItem> items = utilsEJB.getCommissionItems(transaction.getId());
                if (!items.isEmpty()) {
                    grdCommision.setVisible(true);
                    for (CommissionItem c : items) {
                        lblAmountComission.setValue(String.valueOf(c.getAmount()));
                        lblComissionValue.setValue(String.valueOf(c.getCommissionId().getValue()));
                        chbPercentComission.setChecked(c.getCommissionId().getIsPercentCommision() != 0);
                        chbPercentComission.setDisabled(true);
                    }
                }
            } catch (Exception e) {
                showError(e);
            }
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                break;
            case WebConstants.EVENT_EDIT:
                break;
            default:
                break;
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(transactionParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(transactionParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadFields(transactionParam);
                break;
            default:
                break;
        }
    }
}
