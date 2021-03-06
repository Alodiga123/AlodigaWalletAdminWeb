package com.alodiga.wallet.admin.web.controllers;

import com.alodiga.wallet.admin.web.components.ChangeStatusButton;
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
import com.alodiga.wallet.common.ejb.ReportEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.Report;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Textbox;

public class ListReportsController extends GenericAbstractListController<Report> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UserEJB userEJB = null;
    private UtilsEJB utilsEJB = null;
    private ReportEJB reportEJB = null;
    private List<Report> reports = null;
    private User currentUser;
    private Profile currentProfile;
    private Textbox txtAlias;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void checkPermissions() {
        try {
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_COUNTRY));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_COUNTRY);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_COUNTRY);
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
            reportEJB = (ReportEJB) EJBServiceLocator.getInstance().get(EjbConstants.REPORT_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminReport.zul";
            getData();
            loadList(reports);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<Report> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (Report report : list) {
                    item = new Listitem();
                    item.setValue(report);
                    item.appendChild(new Listcell(report.getName()));
                    item.appendChild(new Listcell(report.getReportTypeId().getName()));
                    item.appendChild(new Listcell((report.getEnabled()==true? Labels.getLabel("wallet.common.yes"):Labels.getLabel("wallet.common.no"))));
                    item.appendChild(permissionChangeStatus ? initEnabledButton(report.getEnabled(), item) : new Listcell());
                    item.appendChild(permissionEdit ? new ListcellViewButton(adminPage, report,Permission.EDIT_REPORTS) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellEditButton(adminPage, report,Permission.VIEW_REPORTS) : new Listcell());
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
        reports = new ArrayList<Report>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            reports = reportEJB.getReport(request);
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

    public void onClick$btnDownload() throws InterruptedException {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("wallet.crud.country.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtAlias.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadList(getFilteredList(txtAlias.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public List<Report> getFilteredList(String filter) {
        List<Report> reportaux = new ArrayList<Report>();
        try {
            if (filter != null && !filter.equals("")) {
                reportaux = reportEJB.searchReport(filter);
            } else {
                return reports;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return reportaux;
    }
        
    private Component initEnabledButton(final boolean enabled, final Listitem item) {
        Listcell cell = new Listcell();
        final ChangeStatusButton button = new ChangeStatusButton(enabled);
        button.addEventListener("onClick", new EventListener() {

            public void onEvent(Event event) throws Exception {
                enableReport(button, item);
            }
        });
        button.setParent(cell);
        return cell;
    }
    
    private void enableReport(ChangeStatusButton button, Listitem item) {
        try {
            Report report = (Report) item.getValue();
            button.changeImageStatus(report.getEnabled());
            report.setEnabled(!report.getEnabled());
            item.setValue(report);
            request.setParam(report);
            reportEJB.enableProduct(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (RegisterNotFoundException e) {
            showError(e);
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

}
