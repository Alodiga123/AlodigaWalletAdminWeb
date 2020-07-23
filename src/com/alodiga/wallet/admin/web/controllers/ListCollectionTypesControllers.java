package com.alodiga.wallet.admin.web.controllers;

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
import com.alodiga.wallet.common.model.CollectionType;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListCollectionTypesControllers extends GenericAbstractListController<CollectionType> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private List<CollectionType> collectionType = null;
    private User currentUser;
    private Profile currentProfile;

    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            //currentUser = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            currentUser = AccessControl.loadCurrentUser();
            currentProfile = currentUser.getCurrentProfile();
            adminPage = "adminCollectionTypes.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            checkPermissions();
            getData();
            loadDataList(collectionType);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
   public void getData() {
    collectionType = new ArrayList<CollectionType>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            collectionType = utilsEJB.getCollectionType(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
            showError(ex);
        }
    }


    @Override
    public void checkPermissions() {
        try {
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_TYPE_OF_COLLECTIONS));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_TYPE_OF_COLLECTIONS);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_TYPE_OF_COLLECTIONS);
        } catch (Exception ex) {
            showError(ex);
        }

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
            StringBuilder file = new StringBuilder(Labels.getLabel("cms.menu.collectionsType.list"));
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
            loadDataList(getFilterList(txtName.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public List<CollectionType> getFilterList(String filter) {
        List<CollectionType> collectionTypeList = new ArrayList<CollectionType>();
        try {
            if (filter != null && !filter.equals("")) {
                collectionTypeList = utilsEJB.getSearchCollectionType(filter);
            } else {
                return collectionType;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return collectionTypeList;
    }
   
    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void loadDataList(List<CollectionType> list) {
          try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (CollectionType collectionType : list) {
                    item = new Listitem();
                    item.setValue(collectionType);
                    item.appendChild(new Listcell(collectionType.getCountryId().getName()));
                    item.appendChild(new Listcell(collectionType.getDescription()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, collectionType, Permission.EDIT_TYPE_OF_COLLECTIONS) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, collectionType, Permission.VIEW_TYPE_OF_COLLECTIONS) : new Listcell());
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
    public List<CollectionType> getFilteredList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadList(List<CollectionType> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
