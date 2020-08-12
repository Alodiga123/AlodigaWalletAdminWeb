package com.alodiga.wallet.admin.web.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.RequestHasCollectionRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class ListCollectionsAffiliationRequestController extends GenericAbstractListController<RequestHasCollectionRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UtilsEJB utilsEJB = null;
    List<RequestHasCollectionRequest> collectionsRequests = null;
    private User currentUser;
    private Profile currentProfile;
    private Textbox txtName;
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }
    
    @Override
    public void checkPermissions() {
        try {
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_COLLECTION_AFFILIATION_REQUEST);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_COLLECTION_AFFILIATION_REQUEST);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminRequestCollections.zul";
            getData();
            loadList(collectionsRequests);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        collectionsRequests = new ArrayList<RequestHasCollectionRequest>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            collectionsRequests = utilsEJB.getRequestsHasCollectionsRequest(request);
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
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.menu.collectionsRequest.list"));
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
            loadList(getFilteredList(txtName.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void startListener() {
    }

    public void loadList(List<RequestHasCollectionRequest> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (RequestHasCollectionRequest collectionsRequest : list) {
                    item = new Listitem();
                    item.setValue(collectionsRequest);
                    item.appendChild(new Listcell(collectionsRequest.getCollectionsRequestId().getCollectionTypeId().getCountryId().getName()));
                    item.appendChild(new Listcell(collectionsRequest.getCollectionsRequestId().getCollectionTypeId().getDescription()));
                    item.appendChild(new Listcell(collectionsRequest.getIndApproved()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, collectionsRequest, Permission.EDIT_COLLECTIONS_REQUEST) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, collectionsRequest, Permission.VIEW_COLLECTIONS_REQUEST) : new Listcell());
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
    public List<RequestHasCollectionRequest> getFilteredList(String filter) {
        List<RequestHasCollectionRequest> list = new ArrayList<RequestHasCollectionRequest>();
        if (collectionsRequests != null) {
            for (Iterator<RequestHasCollectionRequest> i = collectionsRequests.iterator(); i.hasNext();) {
            	RequestHasCollectionRequest tmp = i.next();
                String field = tmp.getCollectionsRequestId().getCollectionTypeId().getDescription().toLowerCase();
                if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                    list.add(tmp);
                }
            }
        }
        return list;
    }

}
