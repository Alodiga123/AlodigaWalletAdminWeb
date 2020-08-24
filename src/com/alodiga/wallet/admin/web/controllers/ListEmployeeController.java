package com.alodiga.wallet.admin.web.controllers;

import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Employee;
import com.alodiga.wallet.common.model.Enterprise;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
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

public class ListEmployeeController extends GenericAbstractListController<Employee> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private PersonEJB personEJB = null;
    private Textbox txtName;
    private List<Employee> employee = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_STATUS_CARD));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_STATUS_CARD);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_STATUS_CARD);
        } catch (Exception ex) {
        }
    }
    
    public void startListener() {
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "TabEmployee.zul";
            getData();
            loadList(employee);
        } catch (Exception ex) {
        }
    }

    public List<Employee> getFilteredList(String filter) {
        List<Employee> list = new ArrayList<Employee>();
        if (employee != null) {
            for (Iterator<Employee> i = employee.iterator(); i.hasNext();) {
                Employee tmp = i.next();
                String field = tmp.getLastNames().toLowerCase();
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

    public void loadList(List<Employee> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                
                for (Employee employees : list) {
                    item = new Listitem();
                    item.setValue(employees);
                    StringBuilder employeeName = new StringBuilder(employees.getFirstNames());
                    employeeName.append(" ");
                    employeeName.append(employees.getLastNames());
                    item.appendChild(new Listcell(employeeName.toString()));
                    if (String.valueOf(employees.getIdentificationNumber()) !=  null){
                        item.appendChild(new Listcell(String.valueOf(employees.getIdentificationNumber())));
                    } else{
                        item.appendChild(new Listcell(""));
                    }
                    if (employees.getEmployedPositionId() != null) {
                        item.appendChild(new Listcell(employees.getEmployedPositionId().getName()));
                    } else {
                        item.appendChild(new Listcell(""));
                    }
                    if(employees.getComercialAgencyId() != null){
                        item.appendChild(new Listcell(employees.getComercialAgencyId().getName()));
                    }else{
                        item.appendChild(new Listcell(""));
                    }
                    if (employees.getPersonId().getPhonePerson() != null){
                        item.appendChild(new Listcell(employees.getPersonId().getPhonePerson().getNumberPhone()));
                    } else {
                        item.appendChild(new Listcell(""));
                    }
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, employees, Permission.EDIT_EMPLOYEE) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, employees, Permission.VIEW_EMPLOYEE) : new Listcell());
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
        }
    }

    public void getData() {
        employee = new ArrayList<Employee>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            employee = personEJB.getEmployee(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
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

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.employee.list"));
            AccessControl.saveAction(Permission.LIST_BANK, "Se descargo listado de Empleados en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
        } catch (Exception ex) {
            showError(ex);
        }
    }
}
