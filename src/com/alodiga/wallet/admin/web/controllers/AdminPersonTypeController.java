package com.alodiga.wallet.admin.web.controllers;


import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.AccessControlEJB;
import com.alodiga.wallet.common.ejb.AuditoryEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.CollectionType;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.OriginApplication;
import com.alodiga.wallet.common.model.PersonType;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminPersonTypeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private Combobox cmbCountry;
    private Combobox cmbOriginApplication;
    private UtilsEJB utilsEJB = null;
    private Radio rIsNaturalPersonYes;
    private Radio rIsNaturalPersonNo;
    private PersonType personTypeParam;
    private Toolbarbutton tbbTitle;
    private Button btnSave;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
         eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            personTypeParam = null;
        } else {
            personTypeParam = (Sessions.getCurrent().getAttribute("object") != null) ? (PersonType) Sessions.getCurrent().getAttribute("object") : null;
        }
        initialize();
        
    }
    
    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.personType.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.personType.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.personType.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
        }
    }
    
    public void clearFields() {
        
    }
    
    private void loadFields(PersonType personType) {
        try {
            txtName.setText(personType.getDescription());
            if (personType.getIndNaturalPerson() == true) {
                rIsNaturalPersonYes.setChecked(true);
            } else {
                rIsNaturalPersonNo.setChecked(true);
            }
            cmbCountry.setReadonly(true);
            cmbOriginApplication.setReadonly(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        btnSave.setVisible(false);
        rIsNaturalPersonYes.setDisabled(true);
        rIsNaturalPersonNo.setDisabled(true);
    }
    
    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.person.type.empty", true, null);
        } else if (cmbCountry.getSelectedItem() == null) {
            cmbCountry.setFocus(true);
            this.showMessage("sp.error.country.notSelected", true, null);
        } else if (cmbOriginApplication.getSelectedItem() == null) {
            cmbOriginApplication.setFocus(true);
            this.showMessage("sp.error.OriginApplication.noSelected", true, null);
        } else if ((!rIsNaturalPersonYes.isChecked()) && (!rIsNaturalPersonNo.isChecked())) {
            this.showMessage("sp.error.isNaturalPerson.noSelected", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void savePersonType(PersonType _personType) throws RegisterNotFoundException, NullParameterException, GeneralException {
        boolean indIsNaturalPerson;
        try {
            PersonType personType = null;
            if (_personType != null) {
                personType = _personType;
            } else {//New personType
                personType = new PersonType();
            }

            if (rIsNaturalPersonYes.isChecked()) {
                indIsNaturalPerson = true;
            } else {
                indIsNaturalPerson = false;
            }
            personType.setDescription(txtName.getText());
            personType.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            personType.setOriginApplicationId((OriginApplication) cmbOriginApplication.getSelectedItem().getValue());
            personType.setIndNaturalPerson(indIsNaturalPerson);
            personType = utilsEJB.savePersonType(personType);
            personTypeParam = personType;
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

    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    savePersonType(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    savePersonType(personTypeParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(personTypeParam);
                loadCmbCountry(eventType);
                loadCmbOriginApplication(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(personTypeParam);
                blockFields();
                loadCmbCountry(eventType);
                loadCmbOriginApplication(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbOriginApplication(eventType);
                break;
            default:
                break;
        }
    }
    
    private void loadCmbCountry(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Country> countryList;
        try {
            countryList = utilsEJB.getCountries(request1);
            loadGenericCombobox(countryList, cmbCountry, "name", evenInteger, Long.valueOf(personTypeParam != null ? personTypeParam.getCountryId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
    
    private void loadCmbOriginApplication(Integer evenInteger) {
        //cmbOriginAplication
        EJBRequest request1 = new EJBRequest();
        List<OriginApplication> originApplicationList;

        try {
            originApplicationList = utilsEJB.getOriginApplications(request1);
            loadGenericCombobox(originApplicationList, cmbOriginApplication, "name", evenInteger, Long.valueOf(personTypeParam != null ? personTypeParam.getOriginApplicationId().getId() : 0));
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
