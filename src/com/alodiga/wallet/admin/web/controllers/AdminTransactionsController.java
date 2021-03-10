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
import com.alodiga.wallet.common.enumeraciones.TransactionTypeE;
import com.alodiga.wallet.common.model.CommissionItem;
import com.alodiga.wallet.common.model.Transaction;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.portal.business.commons.models.Business;
import java.text.SimpleDateFormat;
import org.zkoss.zul.Group;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;

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
    private Label lblUserDocumentType;
    private Label lblUserDocumentNumber;
    private Label lblUserPhone;
    private Label lblUserEmail;  
    private Label lblUserDestinationDocumentType;
    private Label lblUserDestinationDocumentNumber;
    private Label lblUserDestinationPhone;
    private Label lblUserDestinationEmail;
    private Radio rIsCloseYes;
    private Radio rIsCloseNo;
    private Checkbox chbPercentComission;
    private Grid grdCommision;
    private Button btnSave;
    private Row rowTitleNameDestinationTypeNumber;
    private Row rowNameDestinationTypeNumber;
    private Row rowTitlePhoneEmail;
    private Row rowPhoneEmail;
    private Group UserDestination;
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
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.transaction.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.transaction.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.exchangeRate.add"));
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
            UserDestination.setVisible(false);
            //Formato de fecha
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            if (transaction.getTransactionSourceId().getCode().equals(TransactionSourceE.APPBIL.getTransactionSourceCode())){
                //Obtiene los usuarios de Origen de Registro Unificado relacionados con la Transacción
                APIRegistroUnificadoProxy apiRegistroUnificado = new APIRegistroUnificadoProxy();
                RespuestaUsuario responseUser = new RespuestaUsuario();
                responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",String.valueOf(transaction.getUserSourceId()));
                String userNameSource = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
                
                //Datos Usuario de Origen
                lblUserSource.setValue(userNameSource);
                lblUserDocumentType.setValue(responseUser.getDatosRespuesta().getTipoDocumento().getNombre());
                lblUserDocumentNumber.setValue(responseUser.getDatosRespuesta().getNumeroDocumento());
                lblUserPhone.setValue(responseUser.getDatosRespuesta().getMovil());
                lblUserEmail.setValue(responseUser.getDatosRespuesta().getEmail());
            
                //Verificar que tipo de transaccion es para traer el usuario destino
                if((!transaction.getTransactionTypeId().getCode().equals(TransactionTypeE.MANREC .getTransactionTypeCode())) 
                    && (!transaction.getTransactionTypeId().getCode().equals(TransactionTypeE.WITMAN.getTransactionTypeCode())
                )){ 
                    //Se activan el grupo y los campos del usuario de destino
                    UserDestination.setVisible(true);
                    rowTitleNameDestinationTypeNumber.setVisible(true);
                    rowNameDestinationTypeNumber.setVisible(true);
                    rowTitlePhoneEmail.setVisible(true);
                    rowPhoneEmail.setVisible(true);
                    
                    //Datos Usuario Destino
                    responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",transaction.getUserDestinationId().toString());
                    String userNameDestination = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
                    lblUserDestination.setValue(userNameDestination);
                    lblUserDestinationDocumentType.setValue(responseUser.getDatosRespuesta().getTipoDocumento().getNombre());
                    lblUserDestinationDocumentNumber.setValue(responseUser.getDatosRespuesta().getNumeroDocumento());
                    lblUserDestinationPhone.setValue(responseUser.getDatosRespuesta().getMovil());
                    lblUserDestinationEmail.setValue(responseUser.getDatosRespuesta().getEmail());
                }

            } else if (transaction.getTransactionSourceId().getCode().equals(TransactionSourceE.PORNEG.getTransactionSourceCode())){
                //Obtiene los usuarios de Origen de BusinessPortal relacionados con la Transacción
                List<Business> businessList = businessEJB.getAll();
                Business businessSource = businessEJB.getBusinessById(transaction.getBusinessId().longValue());
                lblUserSource.setValue(businessSource.getDisplayName());
                
                if((!transaction.getTransactionTypeId().getCode().equals(TransactionTypeE.MANREC .getTransactionTypeCode())) 
                    && (!transaction.getTransactionTypeId().getCode().equals(TransactionTypeE.WITMAN.getTransactionTypeCode()))
                    && (!transaction.getTransactionTypeId().getCode().equals(TransactionTypeE.PROEXC.getTransactionTypeCode()))){ 
                    //Se activan el grupo y los campos del usuario de destino
                    UserDestination.setVisible(true);
                    rowTitleNameDestinationTypeNumber.setVisible(true);
                    rowNameDestinationTypeNumber.setVisible(true);
                    rowTitlePhoneEmail.setVisible(true);
                    rowPhoneEmail.setVisible(true);
                    
                    //Datos Business Destino
                    Business businessDestination = businessEJB.getBusinessById(transaction.getBusinessDestinationId().longValue());   
                    lblUserDestination.setValue(businessDestination.getDisplayName());
                    // lblUserDestinationDocumentType.setValue();
                    lblUserDestinationDocumentNumber.setValue(businessDestination.getIdentification());
                    lblUserDestinationPhone.setValue(businessDestination.getPhoneNumber());
                    lblUserDestinationEmail.setValue(businessDestination.getEmail());
                }
            
            
            }    

            if (transaction.getProductId().getName() != null) {
                lblProduct.setValue(transaction.getProductId().getName());
            } else {
                lblProduct.setValue(Labels.getLabel("wallet.common.emptySymbol"));
            }
            if (transaction.getTransactionTypeId() != null) {
                lblTransactionType.setValue(transaction.getTransactionTypeId().getCode());
            } else {
                lblTransactionType.setValue(Labels.getLabel("wallet.common.emptySymbol"));
            }
            if (transaction.getTransactionSourceId() != null) {
                lblTransactionSource.setValue(transaction.getTransactionSourceId().getName());
            } else {
                lblTransactionSource.setValue(Labels.getLabel("wallet.common.emptySymbol"));
            }
            lblTransactionDate.setValue(simpleDateFormat.format(transaction.getCreationDate()));
            lblAmount.setValue(String.valueOf(transaction.getAmount()));
            lblStatus.setValue(transaction.getTransactionStatus());
            if (transaction.getConcept() != null) {
                lblConcept.setValue(transaction.getConcept());
            } else {
                lblConcept.setValue(Labels.getLabel("wallet.common.emptySymbol"));
            }
            if (transaction.getDailyClosingId() != null){
                lblEndDate.setValue(transaction.getDailyClosingId().getClosingDate().toString());
            } else {
                lblEndDate.setValue(Labels.getLabel("wallet.common.emptySymbol"));
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
