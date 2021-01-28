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
import com.alodiga.wallet.common.model.DocumentsPersonType;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import org.zkoss.zul.Textbox;

public class ListDocumentsPersonTypeController extends GenericAbstractListController<DocumentsPersonType> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<DocumentsPersonType> documentsPersonType = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_DOCUMENTS_PERSON_TYPE));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_DOCUMENTS_PERSON_TYPE);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_DOCUMENTS_PERSON_TYPE);
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
            adminPage = "adminDocumentsPersonType.zul";
            getData();
            loadDataList(documentsPersonType);
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
        documentsPersonType = new ArrayList<DocumentsPersonType>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            documentsPersonType = personEJB.getDocumentsPersonType(request);
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

    public void loadDataList(List<DocumentsPersonType> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (DocumentsPersonType documentsPersonType : list) {
                    item = new Listitem();
                    item.setValue(documentsPersonType);
                    item.appendChild(new Listcell(documentsPersonType.getPersonTypeId().getCountryId().getName()));
                    item.appendChild(new Listcell(documentsPersonType.getPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(documentsPersonType.getDescription()));
                    item.appendChild(new Listcell(documentsPersonType.getPersonTypeId().getOriginApplicationId().getName()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, documentsPersonType, Permission.EDIT_DOCUMENTS_PERSON_TYPE) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, documentsPersonType, Permission.VIEW_DOCUMENTS_PERSON_TYPE) : new Listcell());
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
            Utils.exportExcel(lbxRecords, Labels.getLabel("wallet.crud.documentPersonType.list"));
            AccessControl.saveAction(Permission.LIST_DOCUMENTS_PERSON_TYPE, "Se descargo listado de Documentos por tipo de Persona en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
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
    public List<DocumentsPersonType> getFilteredList(String filter) {
        List<DocumentsPersonType> documentsPersonTypeaux = new ArrayList<DocumentsPersonType>();
        try {
            if (filter != null && !filter.equals("")) {
                documentsPersonTypeaux = personEJB.searchDocumentsPersonTypeByCountry(filter);
            } else {
                return documentsPersonType;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return documentsPersonTypeaux;
    }
    @Override
    public void loadList(List<DocumentsPersonType> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
