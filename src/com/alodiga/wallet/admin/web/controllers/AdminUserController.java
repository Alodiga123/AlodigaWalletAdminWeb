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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.AccessControlEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.model.Enterprise;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.model.UserHasProfileHasEnterprise;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.Encoder;
import com.alodiga.wallet.common.utils.QueryConstants;

public class AdminUserController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtLogin;
    private Textbox txtPassword;
    private Textbox txtName;
    private Textbox txtLastName;
    private Textbox txtEmail;
    private Textbox txtPhoneNumber;
    private Checkbox cbxEnabled;
//    private Checkbox cbxReceiveTopNotification;
    private Listbox lbxEnterprises;
    private Combobox cmbProfiles;
    private UtilsEJB utilsEJB = null;
    private AccessControlEJB accessEJB = null;
    private UserEJB userEJB = null;
    private User userParam;
    private boolean editingPassword = false;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        userParam = (Sessions.getCurrent().getAttribute("object") != null) ? (User) Sessions.getCurrent().getAttribute("object") : null;
        initialize();
        initView(eventType, "crud.user");
    }

    @Override
    public void initialize() {
        try {
            super.initialize();
            accessEJB = (AccessControlEJB) EJBServiceLocator.getInstance().get(EjbConstants.ACCESS_CONTROL_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtLogin.setRawValue(null);
        txtPassword.setRawValue(null);
        txtName.setRawValue(null);
        txtLastName.setRawValue(null);
        txtEmail.setRawValue(null);
        txtPhoneNumber.setRawValue(null);
        cbxEnabled.setChecked(true);
//        cbxReceiveTopNotification.setChecked(false);
        loadEnterprises(true);
        loadProfiles(true);
    }

    private void loadFields(User user) {
        try {
            txtLogin.setText(user.getLogin());
            txtPassword.setText(Encoder.MD5(user.getPassword()));
            txtName.setText(user.getFirstName());
            txtLastName.setText(user.getLastName());
            txtEmail.setText(user.getEmail());
            txtPhoneNumber.setText(user.getPhoneNumber());
            cbxEnabled.setChecked(user.getEnabled());
//            cbxReceiveTopNotification.setChecked(user.getReceiveTopUpNotification());

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtLogin.setReadonly(true);
        txtPassword.setReadonly(true);
        txtName.setReadonly(true);
        txtLastName.setReadonly(true);
        txtEmail.setReadonly(true);
        txtPhoneNumber.setReadonly(true);
        cmbProfiles.setReadonly(true);
        cbxEnabled.setDisabled(true);
//        cbxReceiveTopNotification.setDisabled(true);
    }

    public boolean validateEmpty() {

        if (txtLogin.getText().isEmpty()) {
            txtLogin.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtPassword.getText().isEmpty()) {
            txtPassword.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtLastName.getText().isEmpty()) {
            txtLastName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtEmail.getText().isEmpty()) {
            txtEmail.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (cmbProfiles.getSelectedItem() == null) {
            cmbProfiles.setFocus(true);
            this.showMessage("sp.error.profile.notSelected", true, null);
        } else if (lbxEnterprises.getSelectedCount() == 0) {
            lbxEnterprises.setFocus(true);
            this.showMessage("sp.error.enterprise.notSelected", true, null);
        } else if (validateExistingUser(txtLogin.getText(), null) && eventType == WebConstants.EVENT_ADD) {
            txtLogin.setFocus(true);
            this.showMessage("sp.error.login.exist", true, null);
        } else if (validateExistingUser(null, txtEmail.getText()) && eventType == WebConstants.EVENT_ADD) {
            txtEmail.setFocus(true);
            this.showMessage("sp.error.email.exist", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void loadEnterpriseList(List<UserHasProfileHasEnterprise> userHasProfileHasEnterprises) {
        lbxEnterprises.setCheckmark(false);
        for (int i = 0; i < userHasProfileHasEnterprises.size(); i++) {
            Enterprise enterprise = userHasProfileHasEnterprises.get(i).getEnterprise();
            Listitem item = new Listitem();
            item.setValue(enterprise);
            item.appendChild(new Listcell());
            item.appendChild(new Listcell(enterprise.getName()));
            item.setParent(lbxEnterprises);
        }
    }

    private void loadEnterprises(Boolean isAdd) {

        List<Enterprise> enterprises = new ArrayList<Enterprise>();
        try {
            enterprises = utilsEJB.getEnterprises();
            lbxEnterprises.getItems().clear();
            for (int i = 0; i < enterprises.size(); i++) {
                Listitem item = new Listitem();
                if (!isAdd) {
                    List<UserHasProfileHasEnterprise> uhphes = userParam.getUserHasProfileHasEnterprises();
                    for (int y = 0; y < uhphes.size(); y++) {
                        Enterprise e = uhphes.get(y).getEnterprise();
                        if (e.getId().equals(enterprises.get(i).getId()) && uhphes.get(y).getEndingDate() == null) {
                            item.setSelected(true);
                        }
                    }
                }
                item.setValue(enterprises.get(i));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell(enterprises.get(i).getName()));
                item.setParent(lbxEnterprises);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveUser(User _user) {
        User user = new User();
        try {
            user.setLogin(txtLogin.getText());
            user.setPassword(Encoder.MD5(txtPassword.getText()));
            user.setFirstName(txtName.getText());
            user.setLastName(txtLastName.getText());
            user.setEmail(txtEmail.getText());
            user.setPhoneNumber(txtPhoneNumber.getText());
            user.setEnabled(cbxEnabled.isChecked());
            user.setReceiveTopUpNotification(true);
            user.setCreationDate(new Timestamp(new java.util.Date().getTime()));
            List<UserHasProfileHasEnterprise> uhphes = new ArrayList<UserHasProfileHasEnterprise>();
            Profile profile = (Profile) cmbProfiles.getSelectedItem().getValue();
            for (int i = 0; i < lbxEnterprises.getSelectedCount(); i++) {
                UserHasProfileHasEnterprise uhphe = new UserHasProfileHasEnterprise();
                uhphe.setUser(user);
                uhphe.setProfile(profile);
                uhphe.setEnterprise((Enterprise) lbxEnterprises.getItemAtIndex(i).getValue());
                uhphe.setBeginningDate(new Timestamp(new java.util.Date().getTime()));
                uhphes.add(uhphe);
            }

            user.setUserHasProfileHasEnterprises(uhphes);
            if (_user != null && _user.getId() != null) {//Is update
                user.setId(_user.getId());
                if (!editingPassword) {
                    user.setPassword(userParam.getPassword());
                }
                List<UserHasProfileHasEnterprise> auxUhphes = new ArrayList<UserHasProfileHasEnterprise>();
                List<UserHasProfileHasEnterprise> activeUhphes = new ArrayList<UserHasProfileHasEnterprise>();
                auxUhphes = userParam.getUserHasProfileHasEnterprises();

                for (int i = 0; i < auxUhphes.size(); i++) {
                    if (auxUhphes.get(i).getEndingDate() == null) {
                        activeUhphes.add(auxUhphes.get(i));
                    }
                }
                for (int i = 0; i < activeUhphes.size(); i++) {
                    activeUhphes.get(i).setEndingDate(new Timestamp(new java.util.Date().getTime()));
                }
                user.getUserHasProfileHasEnterprises().addAll(activeUhphes);

            }
            request.setParam(user);
            userParam = userEJB.saveUser(request);

//            if (eventType.equals(WebConstants.EVENT_ADD)) {
//                sendRegistrationMail(user, profile, txtPassword.getText());
//            }


            eventType = WebConstants.EVENT_EDIT;
            txtLogin.setReadonly(true);
            txtPassword.setReadonly(true);
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
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
                    List<UserHasProfileHasEnterprise> uhphes = userParam.getUserHasProfileHasEnterprises();
                    for (int y = 0; y < uhphes.size(); y++) {
                        Profile p = uhphes.get(y).getProfile();
                        if (p.getId().equals(profiles.get(i).getId()) && uhphes.get(y).getEndingDate() == null) {
                            cmbProfiles.setSelectedIndex(i);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            this.showError(ex);
        }
    }

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnEditPassword() {
        txtPassword.setReadonly(false);
        txtPassword.setRawValue(null);
        txtPassword.setFocus(true);
        editingPassword = true;
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveUser(userParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveUser(userParam);
                    break;
                default:
                    break;
            }
        }
    }

    private boolean validateExistingUser(String login, String email) {
        boolean valid = false;
        try {
            Map params = new HashMap<String, Object>();
            if (login != null) {
                params.put(QueryConstants.PARAM_LOGIN, login);
            } else if (email != null) {
                params.put(QueryConstants.PARAM_EMAIL, email);
            }
            request.setParams(params);
            valid = userEJB.validateExistingUser(request);
        } catch (Exception ex) {
            this.showError(ex);
        }
        return valid;
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(userParam);
                loadEnterprises(false);
                loadProfiles(false);
                txtLogin.setReadonly(true);
                txtPassword.setReadonly(true);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(userParam);
                loadEnterpriseList(userParam.getUserHasProfileHasEnterprises());
                loadProfiles(false);
                break;
            case WebConstants.EVENT_ADD:
                loadEnterprises(true);
                loadProfiles(true);
                break;
            default:
                break;
        }
    }

//    private void sendRegistrationMail(User user, Profile profile, String password) {
//
//        try {
//            Enterprise enterprise = utilsEJB.loadEnterprise(new EJBRequest(Enterprise.TURBINES));
//          //  Mail mail = CommonMails.getUserRegistrationMail(user, profile, password, enterprise);
//           // utilsEJB.sendMail(mail);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//    }
}
