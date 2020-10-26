package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.BusinessEJB;
import com.alodiga.wallet.common.enumeraciones.TransactionSourceE;
import com.alodiga.wallet.common.enumeraciones.TransactionTypeE;
import com.alodiga.wallet.common.model.BankOperation;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.ericsson.alodiga.ws.APIRegistroUnificadoProxy;
import com.ericsson.alodiga.ws.RespuestaUsuario;
import com.portal.business.commons.models.Business;
import java.util.List;
import org.zkoss.zul.Group;
import org.zkoss.zul.Row;

public class AdminBankOperationController extends GenericAbstractAdminController {

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
    private Label lblTransactionNumber;
    private Label lblTransactionType;
    private Label lblBankDate;
    private Label lblBankAccount;
    private Label lblBankAmount;
    private Label lblUserName;
    private Label lblUserDocumentType;
    private Label lblUserDocumentNumber;
    private Label lblUserPhone;
    private Label lblUserEmail;
    private Label lblUserDestinationName;
    private Label lblUserDestinationDocumentType;
    private Label lblUserDestinationDocumentNumber;
    private Label lblUserDestinationPhone;
    private Label lblUserDestinationEmail;
    private Row rowTitleNameDestinationTypeNumber;
    private Row rowNameDestinationTypeNumber;
    private Row rowTitlePhoneEmail;
    private Row rowPhoneEmail;
    private Group UserDestination;
    private Toolbarbutton tbbTitle;
    private BankOperation bankOperationParam;
    private Integer eventType;
    private BusinessEJB businessEJB = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_VIEW) {
        	bankOperationParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BankOperation) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.bank.operaton");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.bank.operaton.view"));
                break;           
            default:
                break;
        } try{
            businessEJB = (BusinessEJB) EJBServiceLocator.getInstance().get(EjbConstants.BUSINESS_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
        loadData();

    }

    public void clearFields() {

    }

    private void loadFields(BankOperation bankOperation) {
        try {
            UserDestination.setVisible(false);
            
            //Transacción
            lblTransactionNumber.setValue(bankOperation.getTransactionId().getTransactionNumber());
            lblDate.setValue(bankOperation.getTransactionId().getCreationDate().toString());
            lblProduct.setValue(bankOperation.getProductId().getName());
            
            lblAmount.setValue(String.valueOf(bankOperation.getTransactionId().getAmount()));
            lblTransactionType.setValue(bankOperation.getTransactionId().getTransactionTypeId().getValue());
            lblStatus.setValue(bankOperation.getTransactionId().getTransactionStatus());
            
            
            //Bank operation
            lblNumber.setValue(bankOperation.getBankOperationNumber());
            
            if(bankOperation.getBankOperationDate() != null){
              lblBankDate.setValue(bankOperation.getBankOperationDate().toString());  
            } 
            lblBank.setValue(bankOperation.getBankId().getName());
            if(bankOperation.getAccountBankId() != null){
              lblBankAccount.setValue(bankOperation.getAccountBankId().getAccountNumber());  
            }
            if(bankOperation.getBankOperationAmount() < 0){
              lblBankAmount.setValue(String.valueOf(bankOperation.getBankOperationAmount()));  
            }
            lblMode.setValue(bankOperation.getBankOperationModeId().getName());
            
            // //Obtiene los usuarios de Origen y Destino de Registro Unificado 
            
            if(bankOperation.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.APPBIL.getTransactionSourceCode())){
                APIRegistroUnificadoProxy apiRegistroUnificado = new APIRegistroUnificadoProxy();
                RespuestaUsuario responseUser = new RespuestaUsuario();
                responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",String.valueOf(bankOperation.getUserSourceId()));
                String userNameSource = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
                lblUserName.setValue(userNameSource);
                lblUserDocumentType.setValue(responseUser.getDatosRespuesta().getTipoDocumento().getNombre());
                lblUserDocumentNumber.setValue(responseUser.getDatosRespuesta().getNumeroDocumento());
                lblUserPhone.setValue(responseUser.getDatosRespuesta().getTelefonoResidencial());
                lblUserEmail.setValue(responseUser.getDatosRespuesta().getEmail());
                
                //Verificar que tipo de transaccion es para traer el usuario destino
                if((!bankOperation.getTransactionId().getTransactionTypeId().getCode().equals(TransactionTypeE.MANREC .getTransactionTypeCode()) 
                    || 
                    (!bankOperation.getTransactionId().getTransactionTypeId().getCode().equals(TransactionTypeE.WITMAN.getTransactionTypeCode())
                )))
                {
                    UserDestination.setVisible(true);
                    rowTitleNameDestinationTypeNumber.setVisible(true);
                    rowNameDestinationTypeNumber.setVisible(true);
                    rowTitlePhoneEmail.setVisible(true);
                    rowPhoneEmail.setVisible(true);
                    responseUser = apiRegistroUnificado.getUsuarioporId("usuarioWS","passwordWS",bankOperation.getTransactionId().getUserDestinationId().toString());
                    String userNameDestination = responseUser.getDatosRespuesta().getNombre() + " " + responseUser.getDatosRespuesta().getApellido();
                    lblUserDestinationName.setValue(userNameDestination);
                    lblUserDestinationDocumentType.setValue(responseUser.getDatosRespuesta().getTipoDocumento().getNombre());
                    lblUserDestinationDocumentNumber.setValue(responseUser.getDatosRespuesta().getNumeroDocumento());
                    lblUserDestinationPhone.setValue(responseUser.getDatosRespuesta().getMovil());
                    lblUserDestinationEmail.setValue(responseUser.getDatosRespuesta().getEmail());
                }
                // Obtiene los business de Origen y Destino de BusinessPortal 
            } else if(bankOperation.getTransactionId().getTransactionSourceId().getCode().equals(TransactionSourceE.PORNEG.getTransactionSourceCode())){
                List<Business> businessList = businessEJB.getAll();
                Business businessSource = businessEJB.getBusinessById(bankOperation.getBusinessId().longValue());
                lblUserName.setValue(businessSource.getName());
                // lblUserDocumentType.setValue();
                lblUserDocumentNumber.setValue(businessSource.getIdentification());
                lblUserPhone.setValue(businessSource.getPhoneNumber());
                lblUserEmail.setValue(businessSource.getEmail());
                
                //Verificar que tipo de transaccion es para traer el usuario destino
                if((!bankOperation.getTransactionId().getTransactionTypeId().getCode().equals(TransactionTypeE.MANREC .getTransactionTypeCode()) 
                    || 
                    (!bankOperation.getTransactionId().getTransactionTypeId().getCode().equals(TransactionTypeE.WITMAN.getTransactionTypeCode())
                )))
                {   
                    UserDestination.setVisible(true);
                    rowTitleNameDestinationTypeNumber.setVisible(true);
                    rowNameDestinationTypeNumber.setVisible(true);
                    rowTitlePhoneEmail.setVisible(true);
                    rowPhoneEmail.setVisible(true);
                    Business businessDestination = businessEJB.getBusinessById(bankOperation.getTransactionId().getBusinessDestinationId().longValue());
                    lblUserDestinationName.setValue(businessDestination.getName());
                    // lblUserDestinationDocumentType.setValue();
                    lblUserDestinationDocumentNumber.setValue(businessDestination.getIdentification());
                    lblUserDestinationPhone.setValue(businessDestination.getPhoneNumber());
                    lblUserDestinationEmail.setValue(businessDestination.getEmail());
                }
            
            }
  
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

 
}
