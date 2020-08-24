package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.AccessControlEJB;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Category;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.Employee;
import com.alodiga.wallet.common.model.Enterprise;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.PhonePerson;
import com.alodiga.wallet.common.model.PhoneType;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class AdminEmployeePhoneController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtPhone;
    private Textbox txtCodeCountry;
    private Textbox txtAreaCode;
    private Textbox txtPhoneExtension;
    private Combobox cmbCountry; 
    private Combobox cmbPhoneType;
    private Radio rIsPrincipalNumberYes;
    private Radio rIsPrincipalNumberNo;
    private PhonePerson phonePersonParam;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private AccessControlEJB accessEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private Integer eventType;
    private Profile currentProfile;
    public Window winAdminPhoneEmployee;
   List<PhonePerson> phonePersonList = new ArrayList<PhonePerson>();
   
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            phonePersonParam = null;
        } else {
            phonePersonParam = (Sessions.getCurrent().getAttribute("object") != null) ? (PhonePerson) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
        }
    }
    
    public void onChange$cmbCountry() {
        this.clearMessage();        
        txtCodeCountry.setVisible(true);
        txtCodeCountry.setValue("");        
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        txtCodeCountry.setValue(country.getCode());
    }
    
    
    public void clearFields() {
        txtPhone.setRawValue(null);
    }
    
    private void loadFields(PhonePerson phonePerson) {
            txtPhone.setText(phonePerson.getNumberPhone());
            txtCodeCountry.setText(phonePerson.getCountryCode());
            txtAreaCode.setText(phonePerson.getAreaCode());
            txtPhoneExtension.setText(phonePerson.getExtensionPhoneNumber()); 
            if (phonePerson.getIndMainPhone() == true) {
                rIsPrincipalNumberYes.setChecked(true);
            } else {
                rIsPrincipalNumberNo.setChecked(true);
            }
            btnSave.setVisible(true);
    }

    public void blockFields() {;
        txtCodeCountry.setReadonly(true);
        txtPhone.setReadonly(true);
        txtAreaCode.setReadonly(true);
        txtPhoneExtension.setReadonly(true);
        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (cmbCountry.getSelectedItem()  == null) {
            cmbCountry.setFocus(true);
            this.showMessage("sp.error.countryNotSelected", true, null);     
        } else if (txtCodeCountry.getText().isEmpty()) {
            txtCodeCountry.setFocus(true);
            this.showMessage("sp.error.employee.areaCountry", true, null);
        } else if (txtAreaCode.getText().isEmpty()) {
            txtAreaCode.setFocus(true);
            this.showMessage("sp.error.employee.areaCode", true, null);
        } else if (txtPhone.getText().isEmpty()) {
            txtPhone.setFocus(true);
            this.showMessage("sp.error.field.phoneNumber", true, null);
        } else if (txtPhoneExtension.getText().isEmpty()) {
            txtPhoneExtension.setFocus(true);
            this.showMessage("sp.error.employee.extensionPhone", true, null);
        } else if (cmbPhoneType.getSelectedItem() == null) {
            cmbPhoneType.setFocus(true);
            this.showMessage("sp.error.phoneType.notSelected", true, null);
        }  else {
            return true;
        }
        return false;

    }
    
    public boolean validatePhone() {
        Employee employee = null;
        try {    
            //Empleado Principal
            AdminEmployeeController adminEmployee = new AdminEmployeeController();
            if (adminEmployee.getEmployeeParent().getPersonId() != null) {
                employee = adminEmployee.getEmployeeParent();
            }
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, employee.getPersonId().getId());
            request.setParams(params);
            phonePersonList = personEJB.getValidateMainPhone(request);
            if ((phonePersonList != null) && (rIsPrincipalNumberYes.isChecked())){
                this.showMessage("sp.error.employee.PhoneMainYes", true, null);
                txtPhone.setFocus(true);
                return false;
            }
        } catch (Exception ex) {
            showError(ex);
        }        
        return true;
    }

    private void savePhone(PhonePerson _phonePerson) {
        Employee employee = null;
        boolean indPrincipalPhone  = true;
        try {
            PhonePerson phonePerson = null;

            if (_phonePerson != null) {

           phonePerson = _phonePerson;
            } else {
                phonePerson = new PhonePerson();
            }
            
            if (rIsPrincipalNumberYes.isChecked()) {
                indPrincipalPhone = true;
            } else {
                indPrincipalPhone = false;
            }
            //Obtener Person
             AdminEmployeeController adminEmployee = new AdminEmployeeController();
            if (adminEmployee.getEmployeeParent().getPersonId().getId() != null) {
                employee = adminEmployee.getEmployeeParent();
            }
            
            //Guardar telefono
            phonePerson.setPersonId(employee.getPersonId());
            phonePerson.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            phonePerson.setCountryCode(txtCodeCountry.getText());
            phonePerson.setAreaCode(txtAreaCode.getText());
            phonePerson.setNumberPhone(txtPhone.getText());
            phonePerson.setExtensionPhoneNumber(txtPhoneExtension.getText());
            phonePerson.setIndMainPhone(indPrincipalPhone);
            phonePerson.setPhoneTypeId((PhoneType) cmbPhoneType.getSelectedItem().getValue());
            phonePerson.setCreateDate(new Timestamp(new Date().getTime()));
            phonePerson = personEJB.savePhonePerson(phonePerson);
            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
            
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
                     if (validatePhone()){
                        savePhone(null);   
                    }        
                    break;
                case WebConstants.EVENT_EDIT:
                    savePhone(phonePersonParam);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onClick$btnBack() {
        winAdminPhoneEmployee.detach();
    }
  
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(phonePersonParam);
                loadcmbPhoneType(eventType);
                loadCmbCountry(eventType);
                txtCodeCountry.setReadonly(true);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(phonePersonParam);
                txtPhone.setReadonly(true);
                blockFields();
                loadCmbCountry(eventType);
                loadcmbPhoneType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadcmbPhoneType(eventType);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                txtCodeCountry.setReadonly(true);
                break;
            default:
                break;
        }
    }
    
    private void loadcmbPhoneType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<PhoneType> phoneTypes;
        try {
            phoneTypes = personEJB.getPhoneType(request1);
            loadGenericCombobox(phoneTypes,cmbPhoneType, "description",evenInteger,Long.valueOf(phonePersonParam != null? phonePersonParam.getPhoneTypeId().getId() : 0) );
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
    
    private void loadCmbCountry(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Country> countryList;
        try {
            countryList = utilsEJB.getCountries(request1);
            loadGenericCombobox(countryList, cmbCountry, "name", evenInteger, Long.valueOf(phonePersonParam != null ? phonePersonParam.getPersonId().getCountryId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    } 

}
