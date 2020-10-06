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
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.PersonType;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
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

public class ListPersonTypeController extends GenericAbstractListController<PersonType> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private List<PersonType> personTypeList = null;
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
            currentUser = AccessControl.loadCurrentUser();
            currentProfile = currentUser.getCurrentProfile();
            adminPage = "adminPersonType.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            checkPermissions();
            getData();
            loadList(personTypeList);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
   public void getData() {
        personTypeList = new ArrayList<PersonType>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            personTypeList = utilsEJB.getPersonType(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }
    }
   
    @Override
    public void checkPermissions() {
        try {
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_PERSON_TYPE));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_PERSON_TYPE);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_PERSON_TYPE);
        } catch (Exception ex) {
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
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.crud.person.type.list"));
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
    
    public List<PersonType> getFilteredList(String filter) {
        List<PersonType> personTypeaux = new ArrayList<PersonType>();
        try {
            if (filter != null && !filter.equals("")) {
                personTypeaux = utilsEJB.getSearchPersonTypeByCountry(filter);
            } else {
                return personTypeList;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return personTypeaux;   
    }

    public void loadList(List<PersonType> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (PersonType personType : list) {
                    item = new Listitem();
                    item.setValue(personType);
                    item.appendChild(new Listcell(personType.getDescription()));
                    item.appendChild(new Listcell(personType.getCountryId().getName()));
                    item.appendChild(new Listcell(personType.getOriginApplicationId().getName()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, personType, Permission.EDIT_PERSON_TYPE) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, personType, Permission.VIEW_PERSON_TYPE) : new Listcell());
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
    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
