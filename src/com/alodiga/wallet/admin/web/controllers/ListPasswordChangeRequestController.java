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
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.PasswordChangeRequest;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.SimpleDateFormat;
import org.zkoss.zul.Textbox;

public class ListPasswordChangeRequestController extends GenericAbstractListController<PasswordChangeRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtRequestNumber;
    private PersonEJB personEJB = null;
    private List<User> userList = null;
    private List<PasswordChangeRequest> passwordChangeRequestList = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_PASSWORD_CHANGE_REQUEST));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_PASSWORD_CHANGE_REQUEST);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_PASSWORD_CHANGE_REQUEST);
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
            adminPage = "adminPasswordChangeRequest.zul";
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            getData();
            loadList(passwordChangeRequestList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        passwordChangeRequestList = new ArrayList<PasswordChangeRequest>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            passwordChangeRequestList = personEJB.getPasswordChangeRequest(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
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
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.passwordChange.list"));
            AccessControl.saveAction(Permission.LIST_PASSWORD_CHANGE_REQUEST, "Se descargo listado de Cambio de Contraseña en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void loadList(List<PasswordChangeRequest> list) {
        String indApproved = null;
        Listitem item = null;
        try {
            lbxRecords.getItems().clear();
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (PasswordChangeRequest passwordChangeRequest : list) {
                    item = new Listitem();
                    item.setValue(passwordChangeRequest);
                    String pattern = "dd-MM-yyyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(passwordChangeRequest.getRequestNumber()));
                    item.appendChild(new Listcell(simpleDateFormat.format(passwordChangeRequest.getRequestDate())));
                    item.appendChild(new Listcell(passwordChangeRequest.getUserId().getLogin()));
                    item.appendChild(new Listcell(passwordChangeRequest.getUserId().getFirstName() + " " + passwordChangeRequest.getUserId().getLastName()));
                    if (passwordChangeRequest.getIndApproved() != null) {
                        if (passwordChangeRequest.getIndApproved() == true) {
                            indApproved = Labels.getLabel("sp.common.yes");
                        } else {
                            indApproved = Labels.getLabel("sp.common.no");
                        }
                    }
                    item.appendChild(new Listcell(indApproved));
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, passwordChangeRequest, Permission.VIEW_PASSWORD_CHANGE_REQUEST) : new Listcell());
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
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
    public List<PasswordChangeRequest> getFilteredList(String filter) {
        List<PasswordChangeRequest> passwordChangeaux = new ArrayList<PasswordChangeRequest>();
        try {
            if (filter != null && !filter.equals("")) {
                passwordChangeaux = personEJB.getSearchPasswordChangeRequest(filter);
            } else {
                return passwordChangeRequestList;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return passwordChangeaux;
    }

    @Override
    public void onClick$btnClear() throws InterruptedException {
        txtRequestNumber.setText("");
    }

    @Override
    public void onClick$btnSearch() throws InterruptedException {
         try {
            loadList(getFilteredList(txtRequestNumber.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }

}
