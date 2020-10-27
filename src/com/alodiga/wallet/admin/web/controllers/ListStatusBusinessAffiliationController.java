package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.StatusBusinessAffiliationHasFinalState;
import com.alodiga.wallet.common.model.StatusRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class ListStatusBusinessAffiliationController extends GenericAbstractListController<StatusBusinessAffiliationHasFinalState> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Combobox cmbStatus; 
    private UtilsEJB utilsEJB = null;
    private List<StatusBusinessAffiliationHasFinalState> hasFinalStates = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_AFFILIATIONSTATUSPRERELATIONSHIP));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_AFFILIATIONSTATUSPRERELATIONSHIP);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_AFFILIATIONSTATUSPRERELATIONSHIP);
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
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminStatusBusinessAffiliation.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            getData();
            loadCmbStatus();
            loadList(hasFinalStates);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadCmbStatus() {

        try {
        	cmbStatus.getItems().clear();
        	EJBRequest request = new EJBRequest();
            List<StatusRequest> list = utilsEJB.getStatusBusinessAffiliationRequest(request);
            Comboitem item = new Comboitem();
            for (int i = 0; i < list.size(); i++) {
                item = new Comboitem();
                item.setValue(list.get(i));
                item.setLabel(list.get(i).getDescription());
                item.setParent(cmbStatus);
            }
        } catch (Exception ex) {
            this.showError(ex);
        }

    }
    

    public List<StatusBusinessAffiliationHasFinalState> getFilteredList(String filter) {
        List<StatusBusinessAffiliationHasFinalState> list= new ArrayList<StatusBusinessAffiliationHasFinalState>();
		if (hasFinalStates != null) {
			for (Iterator<StatusBusinessAffiliationHasFinalState> i = hasFinalStates.iterator(); i.hasNext();) {
				StatusBusinessAffiliationHasFinalState tmp = i.next();
				String field = tmp.getStatusBusinessAffiliationRequetsId().getDescription().toLowerCase();
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



    public void loadList(List<StatusBusinessAffiliationHasFinalState> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (StatusBusinessAffiliationHasFinalState state : list) {

                    item = new Listitem();
                    item.setValue(state);
                    item.appendChild(new Listcell(state.getStatusBusinessAffiliationRequetsId().getDescription()));
                    item.appendChild(new Listcell(state.getFinalStateId().getDescription()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, state,Permission.EDIT_AFFILIATIONSTATUSPRERELATIONSHIP) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, state,Permission.VIEW_AFFILIATIONSTATUSPRERELATIONSHIP) : new Listcell());
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

    public void getData() {
        try {
            request.setFirst(0);
            request.setLimit(null);
            hasFinalStates = utilsEJB.getStatusBusinessAffiliationHasFinalState(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
            showError(ex);
        }catch (Exception ex) {
           ex.printStackTrace();
        }
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.status.business.affiliation.list"));
            AccessControl.saveAction(Permission.LIST_AFFILIATIONSTATUSPRERELATIONSHIPS, "Se descargo listado de Prerelaciones Estatus Afiliacion formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
    	cmbStatus.setSelectedIndex(0);
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadList(getFilteredList(cmbStatus.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }
}
