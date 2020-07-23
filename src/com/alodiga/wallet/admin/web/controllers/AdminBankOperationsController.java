package com.alodiga.wallet.admin.web.controllers;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.Bank;
import com.alodiga.wallet.common.model.BankOperation;
import com.alodiga.wallet.common.model.BankOperationMode;
import com.alodiga.wallet.common.model.BankOperationType;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class AdminBankOperationsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtNumber;
    private Textbox txtUser;
    private Textbox txtTransaction;
    private Textbox txtStatus;
    private Combobox cmbType;
    private Combobox cmbMode;
    private Combobox cmbBank;
    private Combobox cmbProduct;
    private Doublebox dblAmount;
    private Datebox dtbDate;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
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
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        dblAmount.setRawValue(null);
        dtbDate.setRawValue(null);
    }

    private void loadFields(BankOperation bankOperation) {
        try {
            dblAmount.setValue(bankOperation.getTransactionId().getAmount());
            dtbDate.setValue(bankOperation.getTransactionId().getCreationDate());
            loadBank(bankOperation.getBankId());
            loadOperationTypes(bankOperation.getBankOperationTypeId());
            loadOperationModes(bankOperation.getBankOperationModeId());
            loadProducts(bankOperation.getProductId());
            txtTransaction.setText(bankOperation.getTransactionId().getId().toString());
            txtNumber.setText(bankOperation.getBankOperationNumber());
            txtUser.setText(bankOperation.getUserSourceId().toString());
            txtStatus.setText(bankOperation.getTransactionId().getTransactionStatus());
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    private void loadBank(Bank bank) {
        try {
        	cmbBank.getItems().clear();
        	EJBRequest request = new EJBRequest();
            List<Bank> banks = utilsEJB.getBank(request);
            for (Bank e : banks) {
                Comboitem cmbItem = new Comboitem();
                cmbItem.setLabel(e.getName());
                cmbItem.setValue(e);
                cmbItem.setParent(cmbBank);
                if (bank != null && bank.getId().equals(e.getId())) {
                	cmbBank.setSelectedItem(cmbItem);
                } 
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    private void loadOperationTypes(BankOperationType bankOperationType) {
        try {
        	cmbType.getItems().clear();
        	EJBRequest request = new EJBRequest();
            List<BankOperationType> operationTypes = utilsEJB.getBankOperationTypes(request);
            for (BankOperationType e : operationTypes) {
                Comboitem cmbItem = new Comboitem();
                cmbItem.setLabel(e.getName());
                cmbItem.setValue(e);
                cmbItem.setParent(cmbType);
                if (bankOperationType != null && bankOperationType.getId().equals(e.getId())) {
                	cmbType.setSelectedItem(cmbItem);
                } 
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    private void loadOperationModes(BankOperationMode operationMode) {
        try {
        	cmbMode.getItems().clear();
        	EJBRequest request = new EJBRequest();
        	 List<BankOperationMode> operationModes = utilsEJB.getBankOperationModes(request);
            for (BankOperationMode e : operationModes) {
                Comboitem cmbItem = new Comboitem();
                cmbItem.setLabel(e.getName());
                cmbItem.setValue(e);
                cmbItem.setParent(cmbMode);
                if (operationMode != null && operationMode.getId().equals(e.getId())) {
                	cmbMode.setSelectedItem(cmbItem);
                } 
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    private void loadProducts(Product product) {
        try {
        	cmbProduct.getItems().clear();
        	EJBRequest request = new EJBRequest();
        	List<Product> products = productEJB.getProducts(request);
            for (Product e : products) {
                Comboitem cmbItem = new Comboitem();
                cmbItem.setLabel(e.getName());
                cmbItem.setValue(e);
                cmbItem.setParent(cmbProduct);
                if (product != null && product.getId().equals(e.getId())) {
                	cmbProduct.setSelectedItem(cmbItem);
                } 
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
    	dblAmount.setDisabled(true);
        dtbDate.setDisabled(true);
        txtNumber.setDisabled(true);
        txtStatus.setDisabled(true);
        txtUser.setDisabled(true);
        txtTransaction.setDisabled(true);
        cmbBank.setReadonly(true);
        cmbMode.setReadonly(true);
        cmbType.setReadonly(true);
        cmbProduct.setReadonly(true);
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
