package com.alodiga.wallet.admin.web.controllers;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.List;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.BusinessCategory;
import com.alodiga.wallet.common.model.BusinessSubCategory;
import com.alodiga.wallet.common.utils.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AdminBusinnesSubCategoryController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbBusinnessCategory;
    private Label lblBusinnessCategory;
    private Textbox txtDescription;
    private Textbox txtMccCode;
    private UtilsEJB utilsEJB = null;
    private Button btnSave;
    private BusinessSubCategory businessSubCategoryParam;
    private Integer eventType;
    public Window winAdminBusinnesSubCategory;
    private BusinessCategory businessCategory;
    List<BusinessSubCategory> businessSubCategoryList = new ArrayList<BusinessSubCategory>();
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            businessSubCategoryParam = null;
        } else {
            businessSubCategoryParam = (BusinessSubCategory) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtDescription.setRawValue(null);
        txtMccCode.setRawValue(null);
    }

    private void loadFields(BusinessSubCategory businessSubCategory) {
        try {

            AdminBusinnessCategoryController adminBusinness = new AdminBusinnessCategoryController();
            businessCategory = adminBusinness.getBusinessCategory();

            if (adminBusinness.getBusinessCategory() != null) {
                lblBusinnessCategory.setValue(businessCategory.getDescription());
            } else {
                lblBusinnessCategory.setValue(businessSubCategory.getBusinessCategoryId().getDescription());
            }
            lblBusinnessCategory.setValue(businessCategory.getDescription());
            txtDescription.setValue(businessSubCategory.getDescription());
            txtMccCode.setValue(businessSubCategory.getMccCode());

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFieldCategory() {
        try {
            AdminBusinnessCategoryController adminBusinness = new AdminBusinnessCategoryController();
            businessCategory = adminBusinness.getBusinessCategory();
            
            lblBusinnessCategory.setValue(businessCategory.getDescription());
            
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtDescription.setDisabled(true);
        txtMccCode.setDisabled(true);

        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("sp.error.businessCategory.description", true, null);
        } else if (txtMccCode.getText().isEmpty()) {
            txtMccCode.setFocus(true);
            this.showMessage("sp.error.businessCategory.mccCode", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public boolean validateCodeMMC(){
        try{
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PARAM_MCC_CODE, txtMccCode.getValue());
            request1.setParams(params);
            businessSubCategoryList = utilsEJB.getBusinessSubCategoryValidateCodeMCC(request1);
        } catch (Exception ex) {
            showError(ex);
        }   if (businessSubCategoryList.size() > 0) {
                this.showMessage("sp.crud.businnesCategory.code.existBD", true, null);
                txtMccCode.setFocus(true);
                return false;
            }
         return true;   
    }

    private void saveBusinessSubCategory(BusinessSubCategory _businessSubCategory) {
        BusinessSubCategory businessSubCategory = null;

        try {
            if (_businessSubCategory != null) {
                businessSubCategory = _businessSubCategory;
            } else {//New country
                businessSubCategory = new BusinessSubCategory();
            }

            businessSubCategory.setDescription(txtDescription.getText());
            businessSubCategory.setMccCode(txtMccCode.getText());
            businessSubCategory.setBusinessCategoryId(businessCategory);
            businessSubCategory = utilsEJB.saveBusinessSubCategory(businessSubCategory);
            businessSubCategoryParam = businessSubCategory;
            this.showMessage("sp.common.save.success", false, null);

            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
            } else {
                btnSave.setVisible(true);
            }
            EventQueues.lookup("updateBusinessSubCategory", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    if(validateCodeMMC()){
                     saveBusinessSubCategory(businessSubCategoryParam);
                    }                    
                    break;
                case WebConstants.EVENT_EDIT:
                    saveBusinessSubCategory(businessSubCategoryParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void onClick$btnBack() {
        winAdminBusinnesSubCategory.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(businessSubCategoryParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(businessSubCategoryParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadFieldCategory();
                break;
            default:
                break;
        }
    }

    private void loadCmbBusinnesCategory(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<BusinessCategory> businessCategories;
        try {
            businessCategories = utilsEJB.getBusinessCategory(request1);
            loadGenericCombobox(businessCategories, cmbBusinnessCategory, "description", evenInteger, Long.valueOf(businessSubCategoryParam != null ? businessSubCategoryParam.getBusinessCategoryId().getId() : 0));
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
