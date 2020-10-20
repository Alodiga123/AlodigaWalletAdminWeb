package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.Commission;
import com.alodiga.wallet.common.model.ExchangeRate;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.TransactionType;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminCommissionController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label testPorcent;
    private Combobox cmbProduct;
    private Combobox cmbTrasactionType;
    private Radio rPercentCommisionYes;
    private Radio rPercentCommisionNo;
    private Radio rIsApplicationYes;
    private Radio rIsApplicationNo;
    private Doublebox dblValue;
    public Short percentCommision = 0;
    private Datebox dtbBeginningDate;
    private Datebox dtbEndingDate;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private ExchangeRate exchangeRateParam;
    private Commission commissionParam;
    private Integer eventType;
    List<Commission> comissionList = new ArrayList<Commission>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            commissionParam = null;
        } else {
            commissionParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Commission) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.commission");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.commission.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.commission.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.commission.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        dblValue.setRawValue(null);
        dtbBeginningDate.setRawValue(null);
        dtbEndingDate.setRawValue(null);
    }
    
    public void onClick$rPercentCommisionYes(){
         if (rPercentCommisionYes.isChecked()) {
            testPorcent.setValue(Labels.getLabel("sp.tab.comission.porcentcomission"));  
        } 
    }
    
    public void onClick$rPercentCommisionNo(){
        if(rPercentCommisionNo.isChecked()) {
            testPorcent.setValue(Labels.getLabel("sp.crud.commission.value")); 
         }
    }

    private void loadFields(Commission commission) {
        try {
            dtbBeginningDate.setValue(commission.getBeginningDate());
            dtbEndingDate.setValue(commission.getEndingDate());
            dblValue.setValue(commission.getValue());

            if (commission.getIsPercentCommision() == 1) {
                rPercentCommisionYes.setChecked(true);
            } else {
                rPercentCommisionNo.setChecked(true);
            }
            
            if (commission.getIndApplicationCommission() == 1) {
                rIsApplicationYes.setChecked(true);
            } else {
                rIsApplicationNo.setChecked(true);
            }

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        dtbBeginningDate.setReadonly(true);
        dtbEndingDate.setReadonly(true);
        rPercentCommisionYes.setDisabled(true);
        rPercentCommisionNo.setDisabled(true);
        rIsApplicationYes.setDisabled(true);
        rIsApplicationNo.setDisabled(true);
        dblValue.setReadonly(true);
        cmbProduct.setReadonly(true);
        cmbTrasactionType.setReadonly(true);

        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (cmbProduct.getSelectedItem() == null) {
            cmbProduct.setFocus(true);
            this.showMessage("sp.error.products.notSelected", true, null);
        } else if (cmbTrasactionType.getSelectedItem() == null) {
            cmbTrasactionType.setFocus(true);
            this.showMessage("sp.error.trasacctionType.notSelected", true, null);
        } else if ((!rPercentCommisionYes.isChecked()) && (!rPercentCommisionNo.isChecked())) {
            this.showMessage("sp.error.percentCommision", true, null);
        } else if (dblValue.getText().isEmpty()) {
            dblValue.setFocus(true);
            this.showMessage("sp.error.value", true, null);
        } else if (dtbBeginningDate.getText().isEmpty()) {
            dtbBeginningDate.setFocus(true);
            this.showMessage("sp.error.date.beginningDate", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public boolean validateComissionByProduct(){
        comissionList.clear();
        TransactionType transactionTypee =(TransactionType) cmbTrasactionType.getSelectedItem().getValue();
        Product product = (Product) cmbProduct.getSelectedItem().getValue();
        Integer application;
        
        if (rIsApplicationYes.isChecked()) {
                application = 1;
        } else {
                application = 2;
        }
        
        try {
            //Valida que la comision del producto no exista
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PRODUCT_KEY, product.getId());
            params.put(Constants.TRANSACTION_TYPE_KEY, transactionTypee.getId());
            params.put(Constants.APPLICATION_COMISSION, application);
            request1.setParams(params);
            comissionList = utilsEJB.getCommissionByProductAndTranssactionType(request1);
        } catch (Exception ex) {
            showError(ex);
        } if (comissionList.size() > 0) {
                this.showMessage("sp.tab.commission.error.commissionByProductAndType", true, null);
                dblValue.setFocus(true);
                return false;
        } else if((new Timestamp(new Date().getTime())).compareTo((dtbBeginningDate.getValue())) > 0){
            this.showMessage("sp.tab.commission.error.todayComprareToBeginningDate", true, null);
            return false;
        }
        return true;
    }

    private void saveBank(Commission _commission) {
        try {
            Commission commission = null;
            Integer application;
            if (_commission != null) {
                commission = _commission;
            } else {//New country
                commission = new Commission();
            }

            if (rPercentCommisionYes.isChecked()) {
                percentCommision = 1;
            } else {
                percentCommision = 0;
            }
            
            if (rIsApplicationYes.isChecked()) {
                application = 1;
            } else {
                application = 2;
            }

            commission.setProductId((Product) cmbProduct.getSelectedItem().getValue());
            commission.setTransactionTypeId((TransactionType) cmbTrasactionType.getSelectedItem().getValue());
            commission.setIndApplicationCommission(application);
            commission.setIsPercentCommision(percentCommision);
            commission.setValue(dblValue.getValue().floatValue());
            commission.setBeginningDate(dtbBeginningDate.getValue());
            commission.setEndingDate(dtbEndingDate.getValue());
            commission = utilsEJB.saveCommission(commission);
            commissionParam = commission;
            eventType = WebConstants.EVENT_EDIT;
            this.showMessage("sp.common.save.success", false, null);

            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
            } else {
                btnSave.setVisible(true);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    if(validateComissionByProduct()){
                        saveBank(commissionParam);
                    }              
                    break;
                case WebConstants.EVENT_EDIT:
                    saveBank(commissionParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(commissionParam);
                loadCmbProduct(eventType);
                loadCmbTrasactionType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(commissionParam);
                blockFields();
                loadCmbProduct(eventType);
                loadCmbTrasactionType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbProduct(eventType);
                loadCmbTrasactionType(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbProduct(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Product> products;
        try {
            products = productEJB.getProducts(request1);
            loadGenericCombobox(products, cmbProduct, "name", evenInteger, Long.valueOf(commissionParam != null ? commissionParam.getProductId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    private void loadCmbTrasactionType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<TransactionType> transactionTypes;
        try {
            transactionTypes = utilsEJB.getTransactionType(request1);
            loadGenericCombobox(transactionTypes, cmbTrasactionType, "value", evenInteger, Long.valueOf(commissionParam != null ? commissionParam.getTransactionTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }
}
