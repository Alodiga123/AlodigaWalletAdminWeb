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
import com.alodiga.wallet.common.enumeraciones.TransactionSourceE;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.BusinessEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.model.CommissionItem;
import com.alodiga.wallet.common.model.Transaction;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.rmi.RemoteException;
import com.portal.business.commons.models.Business;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.zkoss.zul.Radio;

public class AdminTransactionsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblUserSource;
    private Label lblUserDestination;
    private Label lblProduct;
    private Label lblTransactionType;
    private Label lblTransactionSource;
    private Label lblTransactionDate;
    private Label lblAmount;
    private Label lblStatus;
    private Label lblConcept;
    private Label lblAmountComission;
    private Label lblComissionValue;
    private Label lblEndDate;
    private Radio rIsCloseYes;
    private Radio rIsCloseNo;
    private Checkbox chbPercentComission;
    private Grid grdCommision;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private Transaction transactionParam;
    private Integer eventType;
    private UtilsEJB utilsEJB = null;
    private BusinessEJB businessEJB = null;

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
            businessEJB = (BusinessEJB) EJBServiceLocator.getInstance().get(EjbConstants.BUSINESS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFields(Transaction transaction) {
        try {
            //Formato de fecha
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            if (transaction.getTransactionSourceId().getCode().equals(TransactionSourceE.APPBIL.getTransactionSourceCode())){
                //Obtiene los usuarios de Origen y Destino de Registro Unificado relacionados con la Transacción
                APIRegistroUnificadoProxy apiRegistroUnificado = new APIRegistroUnificadoProxy();
                RespuestaUsuario responseUser = new RespuestaUsuario();
                responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",String.valueOf(transaction.getUserSourceId()));
                String userNameSource = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
                responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",transaction.getUserDestinationId().toString());
                String userNameDestination = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
                lblUserSource.setValue(userNameSource);
                lblUserDestination.setValue(userNameDestination);
            } else if (transaction.getTransactionSourceId().getCode().equals(TransactionSourceE.PORNEG.getTransactionSourceCode())){
                //Obtiene los usuarios de Origen y Destino de BusinessPortal relacionados con la Transacción
                List<Business> businessList = businessEJB.getAll();
                Business businessSource = businessEJB.getBusinessById(transaction.getBusinessId());
                Business businessDestination = businessEJB.getBusinessById(transaction.getBusinessDestinationId());
                lblUserSource.setValue(businessSource.getDisplayName());
                lblUserDestination.setValue(businessDestination.getDisplayName());
            }    
            if (transaction.getProductId().getName() != null) {
                lblProduct.setValue(transaction.getProductId().getName());
            } else {
                lblProduct.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getTransactionTypeId() != null) {
                lblTransactionType.setValue(transaction.getTransactionTypeId().getDescription());
            } else {
                lblTransactionType.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getTransactionSourceId() != null) {
                lblTransactionSource.setValue(transaction.getTransactionSourceId().getName());
            } else {
                lblTransactionSource.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            lblTransactionDate.setValue(simpleDateFormat.format(transaction.getCreationDate()));
            lblAmount.setValue(String.valueOf(transaction.getAmount()));
            lblStatus.setValue(transaction.getTransactionStatus());
            if (transaction.getConcept() != null) {
                lblConcept.setValue(transaction.getConcept());
            } else {
                lblConcept.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getDailyClosingId() != null){
                lblEndDate.setValue(transaction.getDailyClosingId().getClosingDate().toString());
            } else {
                lblEndDate.setValue(Labels.getLabel("sp.crud.transaction.empty"));
            }
            if (transaction.getIndClosed() == true) {
                rIsCloseYes.setChecked(true);
            } else {
                rIsCloseNo.setChecked(true);
            } 
            
            //Se obtiene la comisión de la transacción                    
            List<CommissionItem> items = utilsEJB.getCommissionItems(transaction.getId());
            if (!items.isEmpty()) {
                for (CommissionItem c : items) {
                    lblAmountComission.setValue(String.valueOf(c.getAmount()));
                }
            }
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
        rIsCloseYes.setDisabled(true);
        rIsCloseNo.setDisabled(true);
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
