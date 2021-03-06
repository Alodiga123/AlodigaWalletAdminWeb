package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.BusinessServiceType;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminBusinessServiceTypeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtCode;
    private Textbox txtDescription;
    private UtilsEJB utilsEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private BusinessServiceType businessServiceParam;
    private Integer eventType;
    List<BusinessServiceType> businessServiceTypeList = new ArrayList<BusinessServiceType>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            businessServiceParam = null;
        } else {
            businessServiceParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BusinessServiceType) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.business.service.type");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.business.service.type.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.business.service.type.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.business.service.type.add"));
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

    public void clearFields() {
        txtDescription.setRawValue(null);
        txtCode.setRawValue(null);

    }

    private void loadFields(BusinessServiceType businessServiceType) {
        try {
            txtCode.setText(businessServiceType.getCode());
            txtDescription.setText(businessServiceType.getDescription());
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtCode.setReadonly(true);
        txtDescription.setReadonly(true);
        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (txtCode.getText().isEmpty()) {
            txtCode.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
        } else if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public boolean validateCodeBusiness(){
        businessServiceTypeList.clear();
        try{
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PARAM_CODE, txtCode.getValue());
            request1.setParams(params);
            businessServiceTypeList = utilsEJB.getBusinessServiceTypeValidateCode(request1);
        } catch (Exception ex) {
            showError(ex);
        } if(businessServiceTypeList.size() > 0){
                this.showMessage("msj.error.businnesCategory.code.existBD", true, null);
                txtCode.setFocus(true);
                return false;
            }
        return true;
    }

    private void saveBusinessService(BusinessServiceType _businessServiceType) {
        try {
            BusinessServiceType businessServiceType = null;

            if (_businessServiceType != null) {
                businessServiceType = _businessServiceType;
            } else {//New country
                businessServiceType = new BusinessServiceType();
            }

            businessServiceType.setCode(txtCode.getText());
            businessServiceType.setDescription(txtDescription.getText());
            businessServiceType = utilsEJB.saveBusinessServiceType(businessServiceType);
            businessServiceParam = businessServiceType;
            this.showMessage("wallet.msj.save.success", false, null);

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
                    if(validateCodeBusiness()){
                       saveBusinessService(businessServiceParam); 
                    }
                    break;
                case WebConstants.EVENT_EDIT:
                    saveBusinessService(businessServiceParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(businessServiceParam);;
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(businessServiceParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
