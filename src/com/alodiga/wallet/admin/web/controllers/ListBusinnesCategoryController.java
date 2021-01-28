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
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.BusinessCategory;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.zkoss.zul.Textbox;

public class ListBusinnesCategoryController extends GenericAbstractListController<BusinessCategory> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<BusinessCategory> businessCategoryList = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_BUSINESS_CATEGORY));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_BUSINESS_CATEGORY);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_BUSINESS_CATEGORY);
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
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            checkPermissions();
            adminPage = "TabBusinessCategory.zul";
            getData();
            loadDataList(businessCategoryList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void getData() {
        businessCategoryList = new ArrayList<BusinessCategory>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            businessCategoryList = utilsEJB.getBusinessCategory(request);
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
        item.appendChild(new Listcell(Labels.getLabel("msj.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void loadDataList(List<BusinessCategory> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (BusinessCategory businessCategory : list) {
                    item = new Listitem();
                    item.setValue(businessCategoryList);
                    item.appendChild(new Listcell(businessCategory.getDescription()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, businessCategory, Permission.EDIT_BUSINESS_CATEGORY) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, businessCategory, Permission.VIEW_BUSINESS_CATEGORY) : new Listcell());
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

   
    public void onClick$btnDownload() throws InterruptedException {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("wallet.crud.businnesCategory.list.excel"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_BUSINESS_CATEGORY, "Se descargo listado de categorías de comercio en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public List<BusinessCategory> getFilteredList(String filter) {
        List<BusinessCategory> businessCategoriesaux = new ArrayList<BusinessCategory>();
        try {
            if (filter != null && !filter.equals("")) {
                businessCategoriesaux = utilsEJB.getSearchBusinessCategory(filter);
            } else {
                return businessCategoryList;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return businessCategoriesaux;
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadDataList(getFilteredList(txtName.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    @Override
    public void loadList(List<BusinessCategory> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
