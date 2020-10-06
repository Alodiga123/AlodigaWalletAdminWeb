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
import com.alodiga.wallet.common.model.Commission;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.StatusCard;
import com.alodiga.wallet.common.model.StatusCardHasFinalState;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.model.Employee;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.PhonePerson;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

public class ListEmployeePhoneController extends GenericAbstractListController<PhonePerson> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private User currentUser;
    private Profile currentProfile;
    private List<PhonePerson> phonePersonList = null;
    private Person person = null;
    private Tab tabEmployeePhone;

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
            showError(ex);
        }
    }
    
    public void startListener() {
        EventQueue que = EventQueues.lookup("updatePhonePerson", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {

            public void onEvent(Event evt) {
                getData();
                loadList(phonePersonList);
            }
        });
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminEmployeePhone.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            startListener();
            getData();
            if(phonePersonList != null){
              loadList(phonePersonList);  
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public List<PhonePerson> getFilteredList(String filter) {
        List<PhonePerson> auxList = new ArrayList<PhonePerson>();
        for (Iterator<PhonePerson> i = phonePersonList.iterator(); i.hasNext();) {
            PhonePerson tmp = i.next();
            String field = tmp.getNumberPhone();
            if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                auxList.add(tmp);
            }
        }
        return auxList;
    }

    public void onClick$btnAdd() throws InterruptedException {
            Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
            Map<String, Object> paramsPass = new HashMap<String, Object>();
            paramsPass.put("object", phonePersonList);
            final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
            window.doModal();
    }

    public void loadList(List<PhonePerson> list) {
        String indMainPhone = null;
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (PhonePerson phonePerson : list) {
                    item = new Listitem();
                    item.setValue(phonePerson);
                    item.appendChild(new Listcell(phonePerson.getCountryCode()));
                    item.appendChild(new Listcell(phonePerson.getAreaCode()));
                    item.appendChild(new Listcell(phonePerson.getNumberPhone()));
                    item.appendChild(new Listcell(phonePerson.getExtensionPhoneNumber()));
                    if (phonePerson.getIndMainPhone() != null) {
                        if (phonePerson.getIndMainPhone() == true) {
                            indMainPhone = "Si";
                        } else {
                            indMainPhone = "No";
                        }
                        item.appendChild(new Listcell(indMainPhone));
                    } else {
                        item.appendChild(new Listcell("No"));
                    }                    
                    item.appendChild(new Listcell(phonePerson.getPhoneTypeId().getDescription()));
                    item.appendChild(createButtonEditModal(phonePerson));
                    item.appendChild(createButtonViewModal(phonePerson));
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(true);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
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

    public Listcell createButtonEditModal(final Object obg) {
        Listcell listcellEditModal = new Listcell();
        try {
            Button button = new Button();
            button.setImage("/images/icon-edit.png");
            button.setTooltiptext(Labels.getLabel("sp.common.actions.edit"));
            button.setClass("open orange");
            button.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(Event arg0) throws Exception {
                    Sessions.getCurrent().setAttribute("object", obg);
                    Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_EDIT);
                    Map<String, Object> paramsPass = new HashMap<String, Object>();
                    paramsPass.put("object", obg);
                    final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
                    window.doModal();
                }

            });
            button.setParent(listcellEditModal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listcellEditModal;
    }

    public Listcell createButtonViewModal(final Object obg) {
        Listcell listcellViewModal = new Listcell();
        try {
            Button button = new Button();
            button.setImage("/images/icon-invoice.png");
            button.setTooltiptext(Labels.getLabel("sp.common.actions.view"));
            button.setClass("open orange");
            button.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(Event arg0) throws Exception {
                    Sessions.getCurrent().setAttribute("object", obg);
                    Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_VIEW);
                    Map<String, Object> paramsPass = new HashMap<String, Object>();
                    paramsPass.put("object", obg);
                    final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
                    window.doModal();
                }

            });
            button.setParent(listcellViewModal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listcellViewModal;
    }

    public void getData() {
       Employee employee = null;
       try {
           //Empleado Principal
            AdminEmployeeController adminEmployee = new AdminEmployeeController();
            if (adminEmployee.getEmployeeParent().getPersonId() != null) {
                employee = adminEmployee.getEmployeeParent();
            }
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, employee.getPersonId().getId());
            request.setParams(params);
            phonePersonList = personEJB.getPhoneByPerson(request);
            
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        } finally {
            showEmptyList();
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
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.crud.employeePhone.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_EMPLOYEE, "Se descargo listado de telefonos de empleado en stock formato excel");
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