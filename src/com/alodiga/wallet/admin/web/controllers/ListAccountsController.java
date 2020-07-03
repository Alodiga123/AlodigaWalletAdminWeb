package com.alodiga.wallet.admin.web.controllers;
//package com.alodiga.services.provider.web.controllers;
//
//import com.alodiga.services.provider.commons.ejbs.UserEJB;
//import com.alodiga.services.provider.commons.ejbs.UtilsEJB;
//import com.alodiga.services.provider.commons.exceptions.EmptyListException;
//import com.alodiga.services.provider.commons.exceptions.GeneralException;
//import com.alodiga.services.provider.commons.exceptions.NullParameterException;
//import com.alodiga.services.provider.commons.managers.PermissionManager;
//import com.alodiga.services.provider.commons.models.Account;
//import com.alodiga.services.provider.commons.models.Enterprise;
//import com.alodiga.services.provider.commons.models.Permission;
//import com.alodiga.services.provider.commons.models.Profile;
//import com.alodiga.services.provider.commons.models.User;
//import com.alodiga.services.provider.commons.utils.EJBServiceLocator;
//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.services.provider.web.components.ChangeStatusButton;
//import com.alodiga.services.provider.web.components.ListcellEditButton;
//import com.alodiga.services.provider.web.components.ListcellInvoiceButton;
//import com.alodiga.services.provider.web.components.ListcellViewButton;
//import com.alodiga.services.provider.web.generic.controllers.GenericAbstractListController;
//import com.alodiga.services.provider.web.utils.AccessControl;
//import com.alodiga.services.provider.web.utils.Utils;
//import com.alodiga.services.provider.web.utils.WebConstants;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import org.zkoss.util.resource.Labels;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zk.ui.event.Event;
//import org.zkoss.zk.ui.event.EventListener;
//import org.zkoss.zul.Button;
//import org.zkoss.zul.Combobox;
//import org.zkoss.zul.Comboitem;
//import org.zkoss.zul.Listbox;
//import org.zkoss.zul.Listcell;
//import org.zkoss.zul.Listitem;
//import org.zkoss.zul.Textbox;
//
//public class ListAccountsController extends GenericAbstractListController<Account> {
//
//    private static final long serialVersionUID = -9145887024839938515L;
//    private Listbox lbxRecords;
//    private Textbox txtName;
//    private Textbox txtLogin;
//    private Textbox txtEmail;
//    private Combobox cmbLevels;
//    private Combobox cmbEnterprises;
//    private Combobox cmbStatus;
//    private UtilsEJB utilsEJB = null;
//    private UserEJB userEJB = null;
//    private List<Account> accounts = null;
//    private User currentUser;
//    private Profile currentProfile;
//    private boolean sendDistributorData = false;
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//        initialize();
//    }
//
//    @Override
//    public void checkPermissions() {
//        try {
//
//            permissionAdd = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_ACCOUNT);
//            btnAdd.setVisible(permissionAdd);
//            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_ACCOUNT);
//            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_ACCOUNT);
//            permissionChangeStatus = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.CHANGE_ACCOUNT_STATUS);
//            sendDistributorData = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.SEND_ACCOUNT_DATA);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//
//    }
//
//    @Override
//    public void initialize() {
//        super.initialize();
//        try {
//            adminPage = "adminAccount.zul";
//            adminInvoices ="listInvoiceForAccount.zul";
//            currentUser = AccessControl.loadCurrentUser();
//            currentProfile = currentUser.getCurrentProfile(Enterprise.ALODIGA_USA);
//            checkPermissions();
//            userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
//            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
//            loadEnterprises();
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    private Listcell initEnabledButton(final Boolean enabled, final Listitem listItem) {
//
//        Listcell cell = new Listcell();
//        cell.setValue("");
//        final ChangeStatusButton button = new ChangeStatusButton(enabled);
//        button.setTooltiptext(Labels.getLabel("sp.common.actions.changeStatus"));
//        button.setClass("open orange");
//        button.addEventListener("onClick", new EventListener() {
//
//            @Override
//            public void onEvent(Event event) throws Exception {
//                changeStatus(button, listItem);
//            }
//        });
//
//        button.setParent(cell);
//        return cell;
//    }
//
//    private Listcell initForwardDataButton(final Account account) {
//
//        Listcell cell = new Listcell();
//        cell.setValue("");
//        final Button button = new Button();
//        button.setImage("/images/icon_envelope.png");
//        button.setTooltiptext(Labels.getLabel("sp.common.actions.forward"));
//        button.setClass("open orange");
//        button.addEventListener("onClick", new EventListener() {
//
//            @Override
//            public void onEvent(Event event) throws Exception {
//                AccessControl.generateNewPassword(null, account, true);
//            }
//        });
//
//        button.setParent(cell);
//        return cell;
//    }
//
//    @Override
//    public List<Account> getFilteredList(String filter) {
//        List<Account> listAux = new ArrayList<Account>();
//        for (Iterator<Account> i = accounts.iterator(); i.hasNext();) {
//            Account tmp = i.next();
//            String field = tmp.getFullName();
//            if (field.toLowerCase().indexOf(filter.trim().toLowerCase()) >= 0) {
//                listAux.add(tmp);
//            }
//        }
//        return listAux;
//    }
//
//    @Override
//    public void onClick$btnAdd() throws InterruptedException {
//        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
//        Sessions.getCurrent().removeAttribute("object");
//        Executions.getCurrent().sendRedirect(adminPage);
//    }
//
//    private void changeStatus(ChangeStatusButton button, Listitem listItem) {
//        try {
//            Account account = (Account) listItem.getValue();
//            button.changeImageStatus(account.getEnabled());
//            account.setEnabled(!account.getEnabled());
//            listItem.setValue(account);
//            request.setParam(account);
////            userEJB.saveAccount(request);
//            AccessControl.saveAction(Permission.CHANGE_ACCOUNT_STATUS, "changeStatus account = " + account.getId() + " to status = " + !account.getEnabled());
//
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void loadList(List<Account> list) {
//        try {
//            lbxRecords.getItems().clear();
//            Listitem item = null;
//            if (list != null && !list.isEmpty()) {
//                btnDownload.setVisible(true);
//                for (Account account : list) {
//
//                    item = new Listitem();
//                    item.setValue(account);
//                    item.appendChild(new Listcell(account.getFullName()));
//                    item.appendChild(new Listcell(account.getLogin()));
//                    item.appendChild(new Listcell(account.getEmail()));
//                    item.appendChild(permissionChangeStatus ? initEnabledButton(account.getEnabled(), item) : new Listcell());
//                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, account,Permission.EDIT_ACCOUNT) : new Listcell());
//                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, account,Permission.VIEW_ACCOUNT) : new Listcell());
//                    item.appendChild(sendDistributorData ? initForwardDataButton(account) : new Listcell());
//                    if(!account.getIsPrePaid()){
//                    item.appendChild(permissionEdit ? new ListcellInvoiceButton(adminInvoices, account,Permission.READ_INVOICE) : new Listcell());
//                    }
//                    item.setParent(lbxRecords);
//                }
//            } else {
//                btnDownload.setVisible(false);
//                item = new Listitem();
//                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
//                item.appendChild(new Listcell());
//                item.appendChild(new Listcell());
//                item.appendChild(new Listcell());
//                item.setParent(lbxRecords);
//            }
//
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void getData() {
//        accounts = new ArrayList<Account>();
////        try {
////            request.setFirst(0);
////            request.setLimit(null);
//////            request.setAuditData(AccessControl.getCurrentAudit());
//////            accounts = userEJB.getAccounts(request);
////        } catch (NullParameterException ex) {
////            showError(ex);
////        } catch (EmptyListException ex) {
////            //lblInfo.setValue(Labels.getLabel("error.empty.list"));
////        } catch (GeneralException ex) {
////            showError(ex);
////        }
//    }
//
//    public void onClick$btnDownload() throws InterruptedException {
//        try {
//            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.account.list"));
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void onClick$btnClear() throws InterruptedException {
//        txtName.setText("");
//        txtLogin.setText("");
//        txtEmail.setText("");
//        cmbLevels.setSelectedIndex(0);
//        lbxRecords.getItems().clear();
//    }
//
//    public void onClick$btnSearch() throws InterruptedException {
//        try {
//            String email = !txtEmail.getText().isEmpty() ? txtEmail.getText() : null;
//            String login = !txtLogin.getText().isEmpty() ? txtLogin.getText() : null;
//            String fullName = !txtName.getText().isEmpty() ? txtName.getText() : null;
//            Long enterpriseId = ((Enterprise) cmbEnterprises.getSelectedItem().getValue()).getId();
//            String enabled = (String) cmbStatus.getSelectedItem().getValue();
//
////            loadList(userEJB.searchAccounts(enterpriseId, login, fullName, email,enabled));
//        } catch (Exception ex) {
//            //showError(ex);
//            ex.printStackTrace();
//            System.out.println("sp.error.SearchOption : "+ex+ "  ");
//            this.showMessage("sp.error.SearchOption", true, null);
//
//           
//        }
//    }
//
//    
//    private void loadEnterprises() {
//        List<Enterprise> enterprises = null;
//        try {
//            cmbEnterprises.getItems().clear();
//            enterprises = utilsEJB.getEnterprises();
//            for (Enterprise e : enterprises) {
//                Comboitem cmbItem = new Comboitem();
//                cmbItem.setLabel(e.getName());
//                cmbItem.setValue(e);
//                cmbItem.setParent(cmbEnterprises);
//
//            }
//            cmbEnterprises.setSelectedIndex(0);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//
//    }
//
//
//    @Override
//    public void startListener() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//
//}
