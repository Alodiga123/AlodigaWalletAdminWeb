package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.components.ChangeStatusButton;
import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.PDFUtil;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.PreferenceField;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListPreferenceController extends GenericAbstractListController<PreferenceField> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtAlias;
    private PreferencesEJB preferencesEJB = null;
    private List<PreferenceField> preferenceField = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_PREFERENCE));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.EDIT_PREFERENCE);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.VIEW_PREFERENCE);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminPreference.zul";
            preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
            startListener();
            getData();
            loadList(preferenceField);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    public void startListener() {
    }

    public List<PreferenceField> getFilteredList(String filter) {
        List<PreferenceField> preferenceListaux = new ArrayList<PreferenceField>();
        try {
            if (filter != null && !filter.equals("")) {
                preferenceListaux = preferencesEJB.searchPreferenceField(filter);
            } else {
                return preferenceField;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return preferenceListaux;
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);

    }

    public void loadList(List<PreferenceField> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (PreferenceField preference : list) {

	                    item = new Listitem();
	                    item.setValue(preference);
	                    item.appendChild(new Listcell(preference.getName()));
                            item.appendChild(new Listcell(preference.getPreferenceId().getName()));
                            item.appendChild(new Listcell(preference.getPreferenceTypeId().getType()));
                            item.appendChild(new Listcell((preference.getEnabled()==1? Labels.getLabel("wallet.common.yes"):Labels.getLabel("wallet.common.no"))));
	                    item.appendChild(new ListcellEditButton(adminPage, preference));
	                    item.appendChild(new ListcellViewButton(adminPage, preference));
	                    item.setParent(lbxRecords);
	                }
	            } else {
	                btnDownload.setVisible(false);
	                item = new Listitem();
	                item.appendChild(new Listcell(Labels.getLabel("msj.error.empty.list")));
	                item.appendChild(new Listcell());
	                item.appendChild(new Listcell());
	                item.appendChild(new Listcell());
	                item.setParent(lbxRecords);
	            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        preferenceField  = new ArrayList<PreferenceField>();
        EJBRequest request1 = new EJBRequest();
        try {
            preferenceField  = preferencesEJB.getPreferenceFields(request1);
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
        item.appendChild(new Listcell(Labels.getLabel("msj.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }


    
public void onClick$btnDownload() throws InterruptedException {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("wallet.crud.preferences.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_PREFERENCE, "Se descargo listado de preferencias en stock formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

public void onClick$btnExportPdf() throws InterruptedException {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("wallet.crud.product.list"));
            file.append("_");
            file.append(date);
            StringBuilder productTitle = new StringBuilder(Labels.getLabel("wallet.common.product"));
            PDFUtil.exportPdf(file.toString(), productTitle.toString(), lbxRecords, 2);            
        } catch (Exception ex) {
            showError(ex);
        }
    }



    public void onClick$btnClear() throws InterruptedException {
        txtAlias.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadList(getFilteredList(txtAlias.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }
}
