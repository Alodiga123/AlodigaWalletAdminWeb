package com.alodiga.wallet.admin.web.controllers;

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
import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.CalendarDays;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.zkoss.zul.Textbox;

public class ListOfHolidaysController extends GenericAbstractListController<CalendarDays> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UserEJB userEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<CalendarDays> calendarDays = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_OF_HOLIDAYS));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_OF_HOLIDAYS);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_OF_HOLIDAYS);
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
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminOfHolidays.zul";
            getData();
            loadList(calendarDays);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public List<CalendarDays> getFilteredList(String filter) {
        List<CalendarDays> auxList = new ArrayList<CalendarDays>();
        try {
            if (filter != null && !filter.equals("")) {
                auxList = utilsEJB.searchCalendarDays(filter);
            } else {
                return  calendarDays;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return auxList;
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<CalendarDays> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);

                for (CalendarDays calendarDays : list) {
                    item = new Listitem();
                    item.setValue(calendarDays);
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(calendarDays.getCountryId().getName()));
                    item.appendChild(new Listcell(simpleDateFormat.format(calendarDays.getHolidayDate())));
                    item.appendChild(new Listcell(calendarDays.getDescription()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, calendarDays, Permission.EDIT_OF_HOLIDAYS) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, calendarDays, Permission.VIEW_OF_HOLIDAYS) : new Listcell());
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
        calendarDays = new ArrayList<CalendarDays>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            calendarDays = utilsEJB.getCalendarDays(request);
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

    public void onClick$btnDownload() throws InterruptedException {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.crud.calendarDays.list"));
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
}
