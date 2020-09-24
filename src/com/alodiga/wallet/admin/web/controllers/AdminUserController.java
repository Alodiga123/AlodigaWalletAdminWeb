package com.alodiga.wallet.admin.web.controllers;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.AccessControlEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.ComercialAgency;
import com.alodiga.wallet.common.model.Employee;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.PersonClassification;
import com.alodiga.wallet.common.model.PhonePerson;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.model.UserHasProfile;
import com.alodiga.wallet.common.enumeraciones.PersonClassificationE;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.Encoder;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.util.Date;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;

public class AdminUserController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtLogin;
    private Textbox txtPassword;
    private Label lblPosition;
    private Label lblUserExtAlodiga;
    private Label lblCountry;
    private Label lblIdentificactionType;
    private Label lblIdentificationNumber;
    private Label lblComercialAgency;
    private Label lblEmailEmployee;
    private Label lblAuthorizeExtAlodiga; 
    private Label lblCityEmployee;
    private Checkbox rEnabledYes;
    private Checkbox rEnabledNo;
    private Combobox cmbEmployee;
    private Combobox cmbAuthorizeEmployee;
    private Combobox cmbProfiles;
    private Button btnSave;
    private AccessControlEJB accessEJB = null;
    private UserEJB userEJB = null;
    private PersonEJB personEJB = null;
    private User userParam;
    private boolean editingPassword = false;
    private List<PhonePerson> phonePersonUserList = null;
    Employee employee = null;
    List<User> userEmployeeList = new ArrayList<User>();
    List<User> userLoginList = new ArrayList<User>();
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        userParam = (Sessions.getCurrent().getAttribute("object") != null) ? (User) Sessions.getCurrent().getAttribute("object") : null;
        initialize();
        initView(eventType, "sp.crud.user");
    }

    @Override
    public void initialize() {
        try {
            super.initialize();
            accessEJB = (AccessControlEJB) EJBServiceLocator.getInstance().get(EjbConstants.ACCESS_CONTROL_EJB);
            userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        loadProfiles(true);
    }

    private void loadFields(User user) {
        PhonePerson phonePersonUser = null;
        List<PhonePerson> phonePersonEmployeeAuthorizeList = null;
        PhonePerson phonePersonEmployeeAuthorize = null;
        
        try{
            txtLogin.setText(user.getLogin());
            txtPassword.setText(user.getPassword());
            lblPosition.setValue(user.getEmployeeId().getEmployedPositionId().getName());
            lblIdentificactionType.setValue(user.getEmployeeId().getDocumentsPersonTypeId().getDescription());
            lblIdentificationNumber.setValue(String.valueOf(user.getEmployeeId().getIdentificationNumber()));
            lblEmailEmployee.setValue(user.getEmployeeId().getPersonId().getEmail());
            lblCountry.setValue(user.getEmployeeId().getPersonId().getCountryId().getName());
            cmbEmployee.setDisabled(true);
            cmbAuthorizeEmployee.setDisabled(true);
            btnSave.setVisible(true);
            
            if (user.getEnabled() == true) {
                rEnabledYes.setChecked(true);
            } else {
                rEnabledNo.setChecked(true);
            }
            
            if (user.getEmployeeId().getComercialAgencyId().getCityId() != null) {
                lblCityEmployee.setValue(user.getEmployeeId().getComercialAgencyId().getCityId().getName());
            } else {
                EJBRequest request = new EJBRequest();
                request.setParam(user.getEmployeeId().getComercialAgencyId().getId());
                ComercialAgency comercialAgency = personEJB.loadComercialAgency(request);
                user.getEmployeeId().getComercialAgencyId().setCityId(comercialAgency.getCityId());
                lblCityEmployee.setValue(user.getEmployeeId().getComercialAgencyId().getCityId().getName());
            } 
            if (user.getEmployeeId() != null) {
                EJBRequest request = new EJBRequest();
                HashMap params = new HashMap();
                params.put(Constants.PERSON_KEY, user.getEmployeeId().getPersonId().getId());
                request.setParams(params);
                phonePersonUserList = personEJB.getPhoneByPerson(request);
                for (PhonePerson phoneUser : phonePersonUserList) {
                    phonePersonUser = phoneUser;
                }
                lblUserExtAlodiga.setValue(phonePersonUser.getExtensionPhoneNumber());
            } 
            
            if (user.getAuthorizedEmployeeId() != null) {
                EJBRequest request = new EJBRequest();
                HashMap params = new HashMap();
                params.put(Constants.PERSON_KEY, user.getAuthorizedEmployeeId().getPersonId().getId());
                request.setParams(params);
                phonePersonEmployeeAuthorizeList = personEJB.getPhoneByPerson(request);
                for (PhonePerson phoneEmployeeAuthorize : phonePersonEmployeeAuthorizeList) {
                    phonePersonEmployeeAuthorize = phoneEmployeeAuthorize;
                }
                lblAuthorizeExtAlodiga.setValue(phonePersonEmployeeAuthorize.getExtensionPhoneNumber());
            } 
    
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        cmbEmployee.setDisabled(true);
        cmbAuthorizeEmployee.setDisabled(true);
        txtLogin.setReadonly(true);
        txtPassword.setReadonly(true);
        rEnabledYes.setDisabled(true);
        rEnabledNo.setDisabled(true);
        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (cmbEmployee.getSelectedItem() == null) {
            cmbEmployee.setFocus(true);
            this.showMessage("sp.error.employee.noSelected", true, null);
        } else if (cmbAuthorizeEmployee.getSelectedItem() == null) {
            cmbAuthorizeEmployee.setFocus(true);
            this.showMessage("sp.error.authorizeEmployee.noSelected", true, null);
        } else if (cmbProfiles.getSelectedItem() == null) {
            cmbProfiles.setFocus(true);
            this.showMessage("sp.error.profile.notSelected", true, null);
        } else if (txtLogin.getText().isEmpty()) {
            txtLogin.setFocus(true);
            this.showMessage("sp.error.login.empty", true, null);
        } else if (txtPassword.getText().isEmpty()) {
            txtPassword.setFocus(true);
            this.showMessage("sp.error.password.empty", true, null);
        } else if ((!rEnabledYes.isChecked()) && (!rEnabledNo.isChecked())) {
            this.showMessage("sp.error.field.enabled", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public Boolean validateUserAndEmployee(){
        userEmployeeList.clear();
        userLoginList.clear();
        try {
            //Valida si el empleado ya tiene usuario
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, employee.getId());
            request1.setParams(params);
            userEmployeeList = userEJB.getValidateEmployee(request1);
        } catch (Exception ex) {
            showError(ex);
        } finally {
            if (userEmployeeList.size() > 0) {
                this.showMessage("sp.error.field.employeeExistInBD", true, null);
                cmbEmployee.setFocus(true);
                return false;
            }
            try {
                //Valida si el login ingresado ya existe en BD
                EJBRequest request1 = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.PARAM_LOGIN, txtLogin.getValue());
                request1.setParams(params);
                userLoginList = userEJB.getUserByLogin(request1);
            } catch (Exception ex) {
                showError(ex);
            } finally {
                if (userLoginList.size() > 0) {
                    this.showMessage("sp.error.field.loginExistInBD", true, null);
                    txtLogin.setFocus(true);
                    return false;
                }
            }
        }       
        return true;
    }
    
    public void onChange$cmbEmployee() {
    PhonePerson phonePersonEmployee = null;
    try {
        employee = (Employee) cmbEmployee.getSelectedItem().getValue();
        lblCountry.setValue(employee.getPersonId().getCountryId().getName());
        lblPosition.setValue(employee.getEmployedPositionId().getName());
        lblComercialAgency.setValue(employee.getComercialAgencyId().getName());
        lblCityEmployee.setValue(employee.getComercialAgencyId().getCityId().getName());
        lblIdentificactionType.setValue(employee.getDocumentsPersonTypeId().getDescription()); 
        lblIdentificationNumber.setValue(String.valueOf(employee.getIdentificationNumber()));
        lblEmailEmployee.setValue(employee.getPersonId().getEmail());
        if (employee.getPersonId().getPhonePerson() != null) {
            lblUserExtAlodiga.setValue(employee.getPersonId().getPhonePerson().getNumberPhone());
        } else {
                    EJBRequest request = new EJBRequest();
                    HashMap params = new HashMap();
                    params.put(Constants.PERSON_KEY, employee.getPersonId().getId());
                    request.setParams(params);
                    phonePersonUserList = personEJB.getPhoneByPerson(request);
                    for (PhonePerson phoneUser : phonePersonUserList) {
                        phonePersonEmployee = phoneUser;
                    }
                    lblUserExtAlodiga.setValue(phonePersonEmployee.getNumberPhone());
                }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void onChange$cmbAuthorizeEmployee() {
    this.clearMessage();
    Employee employeeAuthorize = (Employee) cmbAuthorizeEmployee.getSelectedItem().getValue();
    String nameEmployeeAuthorize = employeeAuthorize.getFirstNames() + " " + employeeAuthorize.getLastNames();
    String nameEmployee = employee.getFirstNames() + " " + employee.getLastNames();
    if (nameEmployeeAuthorize.equals(nameEmployee)) {
            cmbAuthorizeEmployee.setValue("");
            lblAuthorizeExtAlodiga.setValue("");
            this.showMessage("sp.msj.error.EmployeeAuthorizeNotEqualToEmployeeUser", true, null);
            cmbAuthorizeEmployee.setFocus(true);           
        }  else {
            if (employeeAuthorize.getPersonId().getPhonePerson() != null) {
                lblAuthorizeExtAlodiga.setValue(employeeAuthorize.getPersonId().getPhonePerson().getNumberPhone());
            } else {
                lblAuthorizeExtAlodiga.setValue("");
            }
        } 
    }

   
    private void saveUser(User _user) {
        boolean indEnabled = true;
        boolean received = true;
        Person person = null;
//        String statusUser = PersonClassificationE.USER.getPersonClassificationCode();
        try {
            User user = null;
            
            if (_user != null) {
                user = _user;
                person = user.getPersonId();
            } else {
                user = new User();
                person = new Person();
            }

            if (rEnabledYes.isChecked()) {
                indEnabled = true;
            } else {
                indEnabled = false;
            }
            //Obtener la clasificacion del Empleado / Usuario
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_USER);
            PersonClassification personClassification = personEJB.loadPersonClassification(request1);
                        
            //Guardar la persona
            person.setCountryId(employee.getPersonId().getCountryId());
            person.setPersonTypeId(employee.getDocumentsPersonTypeId().getPersonTypeId());
            person.setEmail(lblEmailEmployee.getValue().toString());
            if (eventType == WebConstants.EVENT_ADD) {
                person.setCreateDate(new Timestamp(new Date().getTime()));
            } else {
                person.setUpdateDate(new Timestamp(new Date().getTime()));
            }
            person.setPersonClassificationId(personClassification);
            person = personEJB.savePerson(person);
            
            //Guarda el Usuario
            user.setLogin(txtLogin.getText());
            user.setPassword(Encoder.MD5(txtPassword.getText()));
            user.setPersonId(person);
            user.setDocumentsPersonTypeId(employee.getDocumentsPersonTypeId());
            user.setIdentificationNumber(lblIdentificationNumber.getValue());
            user.setFirstName(employee.getFirstNames());
            user.setLastName(employee.getLastNames());
            user.setEmail(lblEmailEmployee.getValue().toString());
            user.setPhoneNumber(lblUserExtAlodiga.getValue());
            user.setReceiveTopUpNotification(received);
            user.setEmployeeId((Employee) cmbEmployee.getSelectedItem().getValue());
            user.setAuthorizedEmployeeId((Employee) cmbAuthorizeEmployee.getSelectedItem().getValue());
            user.setEnabled(indEnabled);
            user.setCreationDate(new Date());
            
            //Guardar en UserHasProfile
            List<UserHasProfile> uhphes = new ArrayList<UserHasProfile>();
            UserHasProfile uhphe = new UserHasProfile();
            uhphe.setUser(user);
            uhphe.setProfile((Profile) cmbProfiles.getSelectedItem().getValue());
            uhphe.setBeginningDate(new Timestamp(new java.util.Date().getTime()));
            uhphes.add(uhphe);
            
            user.setUserHasProfile(uhphes);
            if (user != null && user.getId() != null) {//Is update
                user.setId(user.getId());
                if (!editingPassword) {
                    user.setPassword(userParam.getPassword());
                }
                List<UserHasProfile> auxUhphes = new ArrayList<UserHasProfile>();
                List<UserHasProfile> activeUhphes = new ArrayList<UserHasProfile>();
                auxUhphes = userParam.getUserHasProfile();

                for (int i = 0; i < auxUhphes.size(); i++) {
                    if (auxUhphes.get(i).getEndingDate() == null) {
                        activeUhphes.add(auxUhphes.get(i));
                    }
                }
                
                if(indEnabled != true){
                 for (int i = 0; i < activeUhphes.size(); i++) {
                    activeUhphes.get(i).setEndingDate(new Timestamp(new java.util.Date().getTime()));
                }   
                }
                
                user.getUserHasProfile().addAll(activeUhphes);

            }
            request.setParam(user);
            userParam = userEJB.saveUser(request);
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
                    if (validateUserAndEmployee()) {
                        saveUser(null);
                    } 
                    break;
                case WebConstants.EVENT_EDIT:
                    saveUser(userParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(userParam);
                loadProfiles(false);
                txtLogin.setReadonly(true);
                txtPassword.setReadonly(true);
                loadCmbEmployee(eventType);
                loadCmbAuthorizeEmployee(eventType);
                onChange$cmbEmployee();
                onChange$cmbAuthorizeEmployee();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(userParam);
                loadCmbEmployee(eventType);
                loadCmbAuthorizeEmployee(eventType);
                blockFields();
                onChange$cmbEmployee();
                onChange$cmbAuthorizeEmployee();
                loadProfiles(false);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbEmployee(eventType);
                loadCmbAuthorizeEmployee(eventType);
                loadProfiles(true);
                break;
            default:
                break;
        }
    }
    
    private void loadCmbEmployee(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Employee> employeeList;
        String nameEmployee = "";
        try {
            employeeList = personEJB.getEmployee(request1);
            for (int i = 0; i < employeeList.size(); i++) {
                Comboitem item = new Comboitem();
                item.setValue(employeeList.get(i));
                nameEmployee = employeeList.get(i).getFirstNames() + " " + employeeList.get(i).getLastNames();
                item.setLabel(nameEmployee);
                item.setParent(cmbEmployee);
                if (eventType != 1) {
                    if (employeeList.get(i).getId().equals(userParam.getEmployeeId().getId())) {
                        cmbEmployee.setSelectedItem(item);
                    }
                }
            }
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
    
    private void loadProfiles(Boolean isAdd) {

        List<Profile> profiles = new ArrayList<Profile>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            profiles = accessEJB.getProfiles(request);
            cmbProfiles.getItems().clear();
            for (int i = 0; i < profiles.size(); i++) {
                Comboitem item = new Comboitem();
                item.setValue(profiles.get(i));
                item.setLabel(profiles.get(i).getProfileDataByLanguageId(languageId).getAlias());
                item.setParent(cmbProfiles);
                if (!isAdd) {
                    List<UserHasProfile> uhphes = userParam.getUserHasProfile();
                    for (int y = 0; y < uhphes.size(); y++) {
                        Profile p = uhphes.get(y).getProfile();
                        if (p.getId().equals(profiles.get(i).getId()) && uhphes.get(y).getEndingDate() != null) {
                            cmbProfiles.setSelectedIndex(i);
                        }
                        
                    }
                }
            }
        } catch (Exception ex) {
            this.showError(ex);
        }
    }
    
//    private void loadCmbAuthorizeEmployee(Integer evenInteger) {
//        EJBRequest request1 = new EJBRequest();
//        List<Employee> employeeList;
//        try {
//            employeeList = personEJB.getEmployee(request1);
//            loadGenericCombobox(employeeList, cmbAuthorizeEmployee, "firstNames", evenInteger, Long.valueOf(userParam != null ? userParam.getAuthorizedEmployeeId().getId() : 0));
//        } catch (EmptyListException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        } catch (GeneralException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        } catch (NullParameterException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        }
//    }
    
    private void loadCmbAuthorizeEmployee(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Employee> employeeList;
        String nameEmployee = "";
        try {
            employeeList = personEJB.getEmployee(request1);
            for (int i = 0; i < employeeList.size(); i++) {
                Comboitem item = new Comboitem();
                item.setValue(employeeList.get(i));
                nameEmployee = employeeList.get(i).getFirstNames() + " " + employeeList.get(i).getLastNames();
                item.setLabel(nameEmployee);
                item.setParent(cmbAuthorizeEmployee);
                if (eventType != WebConstants.EVENT_ADD) {
                    if (employeeList.get(i).getId().equals(userParam.getAuthorizedEmployeeId().getId())) {
                        cmbAuthorizeEmployee.setSelectedItem(item);
                    }
                }
            }
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
    

}
