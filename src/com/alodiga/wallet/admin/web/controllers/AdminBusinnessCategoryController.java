package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
//import com.alodiga.wallet.common.ejb.AccessControlEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.model.BusinessCategory;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public class AdminBusinnessCategoryController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtDescription;
    private Textbox txtMccCode;
    private UtilsEJB utilsEJB = null;
//    private AccessControlEJB accessEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private BusinessCategory businessCategoryParam;
    public static BusinessCategory businessCategoryParent = null;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            businessCategoryParam = null;
        } else {
            businessCategoryParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BusinessCategory) Sessions.getCurrent().getAttribute("object") : null;
            businessCategoryParent = businessCategoryParam;
        }

        initialize();
        initView(eventType, "crud.businessCategory");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.businnesCategory.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.businnesCategory.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.businnesCategory.add"));
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

    public BusinessCategory getBusinessCategory() {
        return this.businessCategoryParent;
    }

    public void setProductParent(BusinessCategory businessCategory) {
        this.businessCategoryParent = businessCategory;
    }

    public Integer getEventType() {
        return this.eventType;
    }

    public void clearFields() {
        txtDescription.setRawValue(null);
        txtMccCode.setRawValue(null);
    }

    private void loadFields(BusinessCategory businessCategory) {
        try {
            txtDescription.setText(businessCategory.getDescription());
            txtMccCode.setText(businessCategory.getMccCode());

            businessCategoryParent = businessCategory;
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtDescription.setReadonly(true);
        txtMccCode.setReadonly(true);

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

    private void saveBusinessCategory(BusinessCategory _businessCategory) {
        try {
            BusinessCategory businessCategory = null;

            if (_businessCategory != null) {
                businessCategory = _businessCategory;
            } else {
                businessCategory = new BusinessCategory();
            }

            businessCategory.setDescription(txtDescription.getText());
            businessCategory.setMccCode(txtMccCode.getText());
            businessCategory = utilsEJB.saveBusinessCategory(businessCategory);
            businessCategoryParam = businessCategory;
            businessCategoryParent = businessCategory;

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
                    saveBusinessCategory(businessCategoryParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveBusinessCategory(businessCategoryParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(businessCategoryParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(businessCategoryParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
