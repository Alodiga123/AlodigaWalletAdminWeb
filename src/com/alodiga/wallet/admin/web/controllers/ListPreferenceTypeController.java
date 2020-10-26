package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.PreferenceType;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zul.Textbox;

public class ListPreferenceTypeController extends GenericAbstractListController<PreferenceType> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private PreferencesEJB preferencesEJB = null;
    private List<PreferenceType> preferenceTypes = null;
    private User currentUser;
    private Profile currentProfile;
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }
    
    @Override
    public void checkPermissions() {
        try {
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_PREFERENCE_TYPE));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_PREFERENCE_TYPE);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_PREFERENCE_TYPE);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminPreferenceType.zul";
            getData();
            loadList(preferenceTypes);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        preferenceTypes = new ArrayList<PreferenceType>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            preferenceTypes = preferencesEJB.getPreferenceTypes(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
        }
    }
    
    private void showEmptyList() {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }
    
    
    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.crud.preferenceType.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
        } catch (Exception ex) {
            showError(ex);
        } 
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadList(getFilterList(txtName.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public List<PreferenceType> getFilterList(String filter) {
        List<PreferenceType> preferenceTypeList = new ArrayList<PreferenceType>();
        try {
            if (filter != null && !filter.equals("")) {
//                preferenceTypeList = preferencesEJB.(filter);
            } else {
                return preferenceTypes;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return preferenceTypeList;
    }

    public void startListener() {
    }

    public void loadList(List<PreferenceType> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (PreferenceType preferenceType : list) {
                    item = new Listitem();
                    item.setValue(preferenceType);
                    item.appendChild(new Listcell(preferenceType.getType()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, preferenceType, Permission.EDIT_PREFERENCE_TYPE) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, preferenceType, Permission.VIEW_PREFERENCE_TYPE) : new Listcell());
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    @Override
    public List<PreferenceType> getFilteredList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
