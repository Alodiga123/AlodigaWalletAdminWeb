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
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ListUsersController extends GenericAbstractListController<User> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private Textbox txtLogin;
    private Textbox txtProfile;
    private UserEJB userEJB = null;
    private List<User> users = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_USER));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_USER);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_USER);
            permissionChangeStatus = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.CHANGE_USER_STATUS);
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
            adminPage = "adminUser.zul";
            userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
            //loadPermission(new Profile());
            //startListener();
            getData();
            loadList(users);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private Listcell initEnabledButton(final Boolean enabled, final Listitem listItem) {

        Listcell cell = new Listcell();
        cell.setValue("");
        final ChangeStatusButton button = new ChangeStatusButton(enabled);
        button.setTooltiptext(Labels.getLabel("wallet.actions.changeStatus"));
        button.setClass("open orange");
        button.addEventListener("onClick", new EventListener() {

            public void onEvent(Event event) throws Exception {
                changeStatus(button, listItem);
            }
        });

        button.setParent(cell);
        return cell;
    }

    public List<User> getFilteredList(String filter) {
        List<User> list= new ArrayList<User>();
		if (users != null) {
			for (Iterator<User> i = users.iterator(); i.hasNext();) {
				User tmp = i.next();
				String field = tmp.getFirstName().toLowerCase();
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

    private void changeStatus(ChangeStatusButton button, Listitem listItem) {
        try {
            User user = (User) listItem.getValue();
            
            button.changeImageStatus(user.getEnabled());
            user.setEnabled(!user.getEnabled());
            listItem.setValue(user);
            //request.setAuditData(AccessControl.getCurrentAudit());
            request.setParam(user);
            userEJB.saveUser(request);
            AccessControl.saveAction(Permission.CHANGE_USER_STATUS, "changeStatus user = "+user.getId() +" to status = "+ !user.getEnabled());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void loadList(List<User> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (User user : list) {

                    item = new Listitem();
                    item.setValue(user);
                    item.appendChild(new Listcell(user.getFirstName() + " " + user.getLastName()));
                    item.appendChild(new Listcell(user.getLogin()));
                    item.appendChild(new Listcell(user.getEmail()));
                    item.appendChild(new Listcell(user.getPhoneNumber()));
                    if(user.getCurrentProfile() != null){
                     item.appendChild(new Listcell(user.getCurrentProfile().getProfileDataByLanguageId(languageId).getAlias()));   
                    } else {
                        item.appendChild(new Listcell(""));
                    }
                    
                    item.appendChild(permissionChangeStatus ? initEnabledButton(user.getEnabled(), item) : new Listcell());
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, user,Permission.EDIT_USER) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, user,Permission.VIEW_USER) : new Listcell());
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
        users = new ArrayList<User>();
        try {
            request.setFirst(0);
            request.setLimit(null);
//            request.setAuditData(AccessControl.getCurrentAudit());
            users = userEJB.getUsers(request);
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
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("wallet.crud.user.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_PRODUCTS, "Se descargo listado de productos en stock formato excel");
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
}
