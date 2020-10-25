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
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.PermissionHasProfile;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.ProfileData;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class AdminProfileController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtAlias;
    private Textbox txtAliasSpanish;
    private Textbox txtAliasEnglish;
    private Textbox txtDescriptionSpanish;
    private Textbox txtDescriptionEnglish;
    private Checkbox cbxEnabled;
    private Listbox lbxPermissions;
    private AccessControlEJB accessEjb = null;
    private Profile profileParam;
    Profile parentProfile = null;
    private Button btnSave;
    private User user;
    private AuditoryEJB auditoryEJB;
    private String ipAddress;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        profileParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Profile) Sessions.getCurrent().getAttribute("object") : null;
        user = AccessControl.loadCurrentUser();
        initialize();
        initView(eventType, "sp.crud.profile");
    }

    @Override
    public void initView(int eventType, String adminView) {
        super.initView(eventType, "sp.crud.profile");
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
        	ipAddress = Executions.getCurrent().getRemoteAddr();
        	auditoryEJB = (AuditoryEJB) EJBServiceLocator.getInstance().get(EjbConstants.AUDITORY_EJB);
                accessEjb = (AccessControlEJB) EJBServiceLocator.getInstance().get(EjbConstants.ACCESS_CONTROL_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtAlias.setRawValue(null);
        txtAliasSpanish.setRawValue(null);
        txtAliasEnglish.setRawValue(null);
        txtDescriptionSpanish.setRawValue(null);
        txtDescriptionEnglish.setRawValue(null);
        cbxEnabled.setChecked(true);
    }

    private void loadFields(Profile profile) {
        txtAlias.setText(profile.getName());
        txtAliasEnglish.setText(profile.getProfileData().get(0).getAlias());
        txtAliasSpanish.setText(profile.getProfileData().get(1).getAlias());
        txtDescriptionEnglish.setText(profile.getProfileData().get(0).getDescription());
        txtDescriptionSpanish.setText(profile.getProfileData().get(1).getDescription());
        cbxEnabled.setChecked(profile.getEnabled());
    }

    public void blockFields() {
        txtAlias.setReadonly(true);
        txtAliasSpanish.setReadonly(true);
        txtAliasEnglish.setReadonly(true);
        txtDescriptionSpanish.setReadonly(true);
        txtDescriptionEnglish.setReadonly(true);
        cbxEnabled.setDisabled(true);
        lbxPermissions.setDisabled(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtAlias.getText().isEmpty()) {
            txtAlias.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtAliasEnglish.getText().isEmpty()) {
            txtAliasEnglish.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtAliasSpanish.getText().isEmpty()) {
            txtAliasSpanish.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (lbxPermissions.getSelectedCount() == 0) {
            this.showMessage("sp.error.permission.notSelected", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void loadPermissionsList(Boolean isAdd) {
        List<Permission> permissions = new ArrayList<Permission>();
        try {
            request.setFirst(0);
            request.setFirst(null);
            permissions = accessEjb.getPermissions(request);
            lbxPermissions.getItems().clear();
            if (permissions != null && !permissions.isEmpty()) {
                for (Permission permission : permissions) {
                    if (permission.getEnabled()) {
                        Listitem item = new Listitem();
                        if (!isAdd) {
                            List<PermissionHasProfile> permissionsProfile = profileParam.getPermissionHasProfiles();
                            for (int y = 0; y < permissionsProfile.size(); y++) {
                                Permission p = permissionsProfile.get(y).getPermission();
                                if (p.getId().equals(permission.getId())) {
                                    item.setSelected(true);                                  
                                }
                            }
                        }                        
                        item.setValue(permission);
                        item.appendChild(new Listcell());
                        item.appendChild(new Listcell(permission.getPermissionGroup().getPermissionGroupDataByLanguageId(languageId) != null ? permission.getPermissionGroup().getPermissionGroupDataByLanguageId(languageId).getAlias() : permission.getName()));
                        item.appendChild(new Listcell(permission.getPermissionDataByLanguageId(languageId) != null ? permission.getPermissionDataByLanguageId(languageId).getAlias() : permission.getName()));
                        item.setParent(lbxPermissions);
                    }

                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveProfile(Profile _profile) {
        Profile rolOld = _profile;
        Profile profile = new Profile();
        try {
            profile.setName(txtAlias.getText());
            profile.setEnabled(cbxEnabled.isChecked());

            List<PermissionHasProfile> permissionHasProfiles = new ArrayList<PermissionHasProfile>();
            Set items = lbxPermissions.getSelectedItems();
            List al = new ArrayList(items);
            for (Iterator it = al.iterator(); it.hasNext();) {
                PermissionHasProfile permissionHasProfile = new PermissionHasProfile();
                Listitem li = (Listitem) it.next();
                Permission permission = (Permission) li.getValue();
                permissionHasProfile.setPermission(permission);
                permissionHasProfile.setProfile(profile);
                permissionHasProfiles.add(permissionHasProfile);
            }
            profile.setPermissionHasProfiles(permissionHasProfiles);
            if (_profile != null) {
                profile.setId(_profile.getId());
                List<ProfileData> profileDatas = profileParam.getProfileData();
                if (profileDatas != null) {
                    profileDatas.get(0).setAlias(txtAliasEnglish.getText());
                    profileDatas.get(0).setDescription(txtDescriptionEnglish.getText());
                    profileDatas.get(1).setAlias(txtAliasSpanish.getText());
                    profileDatas.get(1).setDescription(txtDescriptionSpanish.getText());
                    profile.setProfileData(profileDatas);
                }
                accessEjb.deletePermissionHasProfile(_profile.getId());
            } else {
                List<ProfileData> pds = new ArrayList<ProfileData>();
                ProfileData profileDataEnglish = new ProfileData();
                ProfileData profileDataSpanish = new ProfileData();
                Language languageEnglish = new Language();
                Language languageSpanish = new Language();
                languageEnglish.setId(Language.ENGLISH);
                languageSpanish.setId(Language.SPANISH);
                profileDataEnglish.setLanguage(languageEnglish);
                profileDataEnglish.setAlias(txtAliasEnglish.getText());
                profileDataEnglish.setDescription(txtDescriptionEnglish.getText());
                profileDataEnglish.setProfile(profile);
                profileDataSpanish.setLanguage(languageSpanish);
                profileDataSpanish.setAlias(txtAliasSpanish.getText());
                profileDataSpanish.setDescription(txtDescriptionSpanish.getText());
                profileDataSpanish.setProfile(profile);
                pds.add(profileDataEnglish);
                pds.add(profileDataSpanish);
                profile.setProfileData(pds);
            }
            request.setParam(profile);
            profile = accessEjb.saveProfile(request);
            profileParam = profile;
            eventType = WebConstants.EVENT_EDIT;
            this.showMessage("sp.common.save.success", false, null);
            try {
                PermissionManager.refresh();
            } catch (Exception ex) {
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveProfile(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveProfile(profileParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(profileParam);
                loadPermissionsList(false);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(profileParam);
                blockFields();
                loadPermissionsList(false);
                break;
            case WebConstants.EVENT_ADD:
                loadPermissionsList(true);
                break;
            default:
                break;
        }
    }
    
}
