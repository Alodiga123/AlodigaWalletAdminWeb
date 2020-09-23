package com.alodiga.wallet.admin.web.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.AccessControlEJB;
import com.alodiga.wallet.common.ejb.AuditoryEJB;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Language;
import com.alodiga.wallet.common.model.BusinessType;
import com.alodiga.wallet.common.model.BusinessServiceType;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zul.ListModel;

public class AdminBusinessTypeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtCode;
    private Textbox txtDescription;
    private Listbox lbxRecords;
    private BusinessType businessTypeParam;
    private User user;
    private AccessControlEJB accessEjb = null;
    private UtilsEJB utilsEJB = null;
    private Button btnSave;
    private AuditoryEJB auditoryEJB;
    private String ipAddress;
    List<BusinessServiceType> businessServiceList = new ArrayList<BusinessServiceType>();
    List<BusinessServiceType> businessServiceTypeByBusinessTypeList = new ArrayList<BusinessServiceType>(); 
    Integer businessTypeId;
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        businessTypeParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BusinessType) Sessions.getCurrent().getAttribute("object") : null;
        if (businessTypeParam != null) {
            businessTypeId = businessTypeParam.getId();
        }        
        user = AccessControl.loadCurrentUser();
        initialize();
        initView(eventType, "sp.crud.businesstype");
    }

    @Override
    public void initView(int eventType, String adminView) {
        super.initView(eventType, "sp.crud.businesstype");
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
        	ipAddress = Executions.getCurrent().getRemoteAddr();
        	auditoryEJB = (AuditoryEJB) EJBServiceLocator.getInstance().get(EjbConstants.AUDITORY_EJB);
                accessEjb = (AccessControlEJB) EJBServiceLocator.getInstance().get(EjbConstants.ACCESS_CONTROL_EJB);
                utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtCode.setRawValue(null);
        txtDescription.setRawValue(null);
    }

    private void loadFields(BusinessType businessType) {
        txtCode.setText(businessType.getCode());
        txtDescription.setText(businessType.getDescription());
    }

    public void blockFields() {
        txtCode.setReadonly(true);
        txtDescription.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtCode.getText().isEmpty()) {
            txtCode.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void loadBussinessService() {
        Listcell tmpCell = new Listcell();
        try {
            request.setFirst(0);
            request.setFirst(null);            
            businessServiceList = utilsEJB.getBusinessServiceType(request);
            lbxRecords.getItems().clear();
            if (businessServiceList != null && !businessServiceList.isEmpty()) {
                for (BusinessServiceType businessServices : businessServiceList) {
                        Listitem item = new Listitem(); 
                        item.setValue(businessServices);
                        tmpCell = new Listcell();
                        Checkbox chkRequired = new Checkbox();
                        chkRequired.setParent(tmpCell);
                        if (eventType != WebConstants.EVENT_ADD && businessServices.getBusinessTypeId() != null) {
                            if (businessServices.getBusinessTypeId().getId().intValue() == businessTypeId.intValue()) {
                                chkRequired.setChecked(true);
                                item.appendChild(tmpCell);
                            } else {
                                item.appendChild(tmpCell);
                            }
                        } else {
                            item.appendChild(tmpCell);
                        }                       
                        if (eventType == WebConstants.EVENT_ADD) {
                            item.appendChild(tmpCell);
                        }
                        item.appendChild(new Listcell(businessServices.getCode()));
                        item.appendChild(new Listcell(businessServices.getDescription()));
                        item.setParent(lbxRecords);
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveBusinessType(BusinessType _businessType) {
        businessServiceList = lbxRecords.getItems();
        int countRecords = businessServiceList.size();
        
        try{
            BusinessType businessType = null;
            if (_businessType != null) {
                businessType = _businessType;
            } else {
                businessType = new BusinessType();
            }
            //Guardar BusinessType
            businessType.setCode(txtCode.getText());
            businessType.setDescription(txtDescription.getText());
            businessType = utilsEJB.saveBusinessType(businessType);
            businessTypeParam = businessType;
            
            //BusinessServiceType  
            for (int i = 0; i < countRecords; i++) {
                List<Listcell> listCells = ((Listitem) lbxRecords.getItems().get(i)).getChildren();
                for (Listcell l : listCells) {
                    for (Object cell : ((Listcell) l).getChildren()) {
                        if (cell instanceof Checkbox) {
                            Checkbox myCheckbox = (Checkbox) cell;
                            if (myCheckbox.isChecked()) {
                                BusinessServiceType businessServiceType = new BusinessServiceType();
                                businessServiceType = (BusinessServiceType) ((Listitem) lbxRecords.getItems().get(i)).getValue();
                                businessServiceType.setBusinessTypeId(businessType);
                                if (businessServiceType.getBusinessTypeId() == null) {
                                    businessServiceType.setBusinessTypeId(businessType);
                                }
                                businessServiceType = utilsEJB.saveBusinessServiceType(businessServiceType);
                            }
                        }
                    }
                }
            }
            this.showMessage("sp.common.save.success", false, null);        
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveBusinessType(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveBusinessType(businessTypeParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(businessTypeParam);
                loadBussinessService();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(businessTypeParam);
                loadBussinessService();
                break;
            case WebConstants.EVENT_ADD:
                loadBussinessService();
                break;
            default:
                break;
        }
    }
}
