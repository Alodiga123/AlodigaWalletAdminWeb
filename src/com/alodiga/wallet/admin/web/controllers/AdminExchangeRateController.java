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
import com.alodiga.wallet.common.model.ExchangeRate;
import com.alodiga.wallet.common.model.Product;
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
import org.zkoss.zul.Toolbarbutton;

public class AdminExchangeRateController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbProduct;
    private Doublebox dblValue;
    private Datebox dtbBeginningDate;
    private Datebox dtbEndingDate;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private ExchangeRate exchangeRateParam;
    private Integer eventType;
    List <ExchangeRate> exchangeList = new ArrayList<ExchangeRate>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            exchangeRateParam = null;
        } else {
            exchangeRateParam = (Sessions.getCurrent().getAttribute("object") != null) ? (ExchangeRate) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.exchangeRate");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.exchangeRate.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.exchangeRate.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.exchangeRate.add"));
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

    private void loadFields(ExchangeRate exchangeRate) {
        try {
            dblValue.setValue(exchangeRate.getValue());
            dtbBeginningDate.setValue(exchangeRate.getBeginningDate());
            dtbEndingDate.setValue(exchangeRate.getEndingDate());

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        dblValue.setReadonly(true);
        dtbBeginningDate.setReadonly(true);
        dtbEndingDate.setReadonly(true);
        cmbProduct.setReadonly(true);

        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        Date today = new Date();
        exchangeList.clear();
        if (cmbProduct.getSelectedItem() == null) {
            cmbProduct.setFocus(true);
            this.showMessage("sp.error.products.notSelected", true, null);
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

    public boolean validateExchangeRateByProduct(){
        exchangeList.clear();
        Product product = (Product) cmbProduct.getSelectedItem().getValue();
        try{
           EJBRequest request1 = new EJBRequest();
           Map params = new HashMap();
           params.put(Constants.PRODUCT_KEY, product.getId()); 
           request1.setParams(params);
           exchangeList = utilsEJB.getExchangeRateByProductAndEndingDate(request1);
        } catch (Exception ex) {
            showError(ex);
        }   if (exchangeList.size() > 0) {
                this.showMessage("sp.tab.commission.error.commissionByProductAndType", true, null);
                cmbProduct.setFocus(true);
                return false;
            } else if ((new Timestamp(new Date().getTime())).compareTo((dtbBeginningDate.getValue())) > 0){
                this.showMessage("sp.tab.commission.error.todayComprareToBeginningDate", true, null);
                return false;
            }
        return true;
    }
    
    private void saveBank(ExchangeRate _exchangeRate) {
        try {
            ExchangeRate exchangeRate = null;

            if (_exchangeRate != null) {
                exchangeRate = _exchangeRate;
            } else {//New country
                exchangeRate = new ExchangeRate();
            }

            exchangeRate.setProductId((Product) cmbProduct.getSelectedItem().getValue());
            exchangeRate.setValue(dblValue.getValue().floatValue());
            exchangeRate.setBeginningDate(dtbBeginningDate.getValue());
            if (dtbEndingDate.getValue() != null) {
                exchangeRate.setEndingDate(dtbEndingDate.getValue());
            }
            exchangeRate = utilsEJB.saveExchangeRate(exchangeRate);
            exchangeRateParam = exchangeRate;
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
                    if(validateExchangeRateByProduct()){
                      saveBank(exchangeRateParam);  
                    }
                    break;
                case WebConstants.EVENT_EDIT:
                    saveBank(exchangeRateParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(exchangeRateParam);
                loadCmbProduct(eventType);
                cmbProduct.setDisabled(true);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(exchangeRateParam);
                blockFields();
                loadCmbProduct(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbProduct(eventType);
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
            loadGenericCombobox(products, cmbProduct, "name", evenInteger, Long.valueOf(exchangeRateParam != null ? exchangeRateParam.getProductId().getId() : 0));
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
