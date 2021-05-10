package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.model.BankOperation;
import com.alodiga.wallet.common.enumeraciones.TransactionSourceE;
import com.ericsson.alodiga.ws.APIRegistroUnificadoProxy;
import com.ericsson.alodiga.ws.RespuestaUsuario;
import org.zkoss.util.resource.Labels;

public class AdminBankOperationsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblNumber;
    private Label lblUser;
    private Label lblTransaction;
    private Label lblStatus;
    private Label lblType;
    private Label lblMode;
    private Label lblBank;
    private Label lblProduct;
    private Label lblAmount;
    private Label lblDate;
    private Label lblComissions;
    private Label lblPayType;
    private Label lblTransactionNumber;
    private Label lblTransactionDate;
    private Label lblTransactionType;
    private Label lblUserDestination;
    private BankOperation bankOperationParam;
    private Integer eventType;
    public Window winAdminBankOperations;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_VIEW) {
        	bankOperationParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BankOperation) Sessions.getCurrent().getAttribute("object") : null;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();      
        try {
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {

    }

    private void loadFields(BankOperation bankOperation) {
        try {
            APIRegistroUnificadoProxy apiRegistroUnificado = new APIRegistroUnificadoProxy();
            RespuestaUsuario responseUser = new RespuestaUsuario();
            
            //Usuario Origen
            responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",String.valueOf(bankOperation.getUserSourceId()));
            String userNameSource = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
            //Usuario Destino
            responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",String.valueOf(bankOperation.getTransactionId().getUserDestinationId()));
            String userNameDestination = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
            
            //Se obtienen los usuarios del Registro Unificado
            if(bankOperation.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.APPBIL.getTransactionSourceCode())){
                lblUser.setValue(userNameSource);
                lblUserDestination.setValue(userNameDestination);
            } else if(bankOperation.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.PORNEG.getTransactionSourceCode())){
            //Se obtienen de BusinessEJB los negocios
            }
            
            lblAmount.setValue(String.valueOf(bankOperation.getTransactionId().getAmount()));
            lblDate.setValue(bankOperation.getTransactionId().getCreationDate().toString());
            lblBank.setValue(bankOperation.getBankId().getName());
            lblType.setValue(bankOperation.getBankOperationTypeId().getName());
            lblMode.setValue(bankOperation.getBankOperationModeId().getName());
            lblProduct.setValue(bankOperation.getProductId().getName());
            lblNumber.setValue(bankOperation.getBankOperationNumber());
            lblStatus.setValue(bankOperation.getTransactionId().getTransactionStatus());
            lblComissions.setValue(String.valueOf(bankOperation.getCommisionId().getValue()));
            if (bankOperation.getPaymentTypeId() != null){
                lblPayType.setValue(bankOperation.getPaymentTypeId().getName()); 
            } else {
                lblPayType.setValue(Labels.getLabel("wallet.common.emptySymbol"));
            }
            lblTransactionNumber.setValue(bankOperation.getTransactionId().getTransactionNumber());
            lblTransactionDate.setValue(bankOperation.getTransactionId().getCreationDate().toString());
            lblTransactionType.setValue(bankOperation.getTransactionId().getTransactionTypeId().getValue());
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
                loadFields(bankOperationParam);
                blockFields();
                break;         
            default:
                break;
        }
    }

    public void onClick$btnBack() {
    	winAdminBankOperations.detach();
    }
 
}
