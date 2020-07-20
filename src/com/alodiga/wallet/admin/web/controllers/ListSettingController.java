package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

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
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.PreferenceValue;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class ListSettingController extends GenericAbstractListController<PreferenceValue> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtBussinessId;
    private PreferencesEJB preferencesEJB = null;
    private List<PreferenceValue> preferenceValues = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_PREFERENCES));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_PREFERENCES);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_PREFERENCES);
            permissionChangeStatus = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.CHANGE_PREFERENCES_STATUS);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void startListener() {

    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminSpecificsSettings.zul";
            getData();
            loadList(preferenceValues);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public List<PreferenceValue> getFilteredList(String filter) {
        List<PreferenceValue> list = new ArrayList<PreferenceValue>();
        if (preferenceValues != null) {
            for (Iterator<PreferenceValue> i = preferenceValues.iterator(); i.hasNext();) {
            	PreferenceValue tmp = i.next();
                String field = tmp.getBussinessId().toString().toLowerCase();
                if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                    list.add(tmp);
                }
            }
        }
        return list;
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<PreferenceValue> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                
                for (PreferenceValue preferenceValue : list) {
                    item = new Listitem();
                    item.setValue(preferenceValue);
                    item.appendChild(new Listcell(preferenceValue.getPreferenceClassficationId().getName()));
                    item.appendChild(new Listcell(preferenceValue.getProductId().getName()));
                    item.appendChild(new Listcell(preferenceValue.getTransactionTypeId().getValue()));
                    item.appendChild(new Listcell(preferenceValue.getBussinessId().toString()));
//                    item.appendChild(permissionChangeStatus ? initEnabledButton(preferenceValue.isEnabled(), item) : new Listcell());
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, preferenceValue, Permission.EDIT_PREFERENCES) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, preferenceValue, Permission.VIEW_PREFERENCES) : new Listcell());
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
//                item.appendChild(new Listcell());
                item.setParent(lbxRecords);
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

//    private Listcell initEnabledButton(final Boolean enabled, final Listitem listItem) {
//
//        Listcell cell = new Listcell();
//        cell.setValue("");
//        final ChangeStatusButton button = new ChangeStatusButton(enabled);
//        button.setTooltiptext(Labels.getLabel("sp.common.actions.changeStatus"));
//        button.setClass("open orange");
//        button.addEventListener("onClick", new EventListener() {
//
//            public void onEvent(Event event) throws Exception {
//                changeStatus(button, listItem);
//            }
//        });
//
//        button.setParent(cell);
//        return cell;
//    }
//    
//    private void changeStatus(ChangeStatusButton button, Listitem listItem) {
//        try {
//            PreferenceValue preferenceValue = (PreferenceValue) listItem.getValue();
//            
//            
//            preferencesEJB.EnabledPreferencesValues(preferenceValue.getPreferenceClassficationId().getId(), preferenceValue.getBussinessId(), !preferenceValue.isEnabled());
//            button.changeImageStatus(preferenceValue.isEnabled());
//            preferenceValue.setEnabled(!preferenceValue.isEnabled());
//            listItem.setValue(preferenceValue);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
    public void getData() {
    	preferenceValues = new ArrayList<PreferenceValue>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            preferenceValues = preferencesEJB.getPreferenceValuesGroupByBussinessId(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    private void showEmptyList() {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.specific.preference.list"));
            AccessControl.saveAction(Permission.LIST_PREFERENCES, "Se descargo listado de preferencias especificas en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
        	  loadList(getFilteredList(txtBussinessId.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }
}
