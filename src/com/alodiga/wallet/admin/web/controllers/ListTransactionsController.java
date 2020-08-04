package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Enterprise;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.Transaction;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Radio;

public class ListTransactionsController extends GenericAbstractListController<Transaction> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UtilsEJB utilsEJB = null;
    private List<Transaction> transactions = null;
    private Radio rDaysYes;
    private Radio rDaysNo;
    private Datebox dtbBeginningDate;
    private Datebox dtbEndingDate;
    private User currentUser;
    private Profile currentProfile;
    private Button btnViewTransactions;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void startListener() {
    }

    @Override
    public void checkPermissions() {
        try {
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_TRANSACTION);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_TRANSACTION);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminTransactions.zul";
            btnViewTransactions.setVisible(false);
            getData();
            loadList(transactions);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$rDaysYes() {
        dtbEndingDate.setDisabled(true);
        btnViewTransactions.setVisible(true);
//        btnViewTransactions.setDisabled(false);
    }

    public void onClick$rDaysNo() {
        dtbEndingDate.setDisabled(false);
        btnViewTransactions.setVisible(true);
//        btnViewTransactions.setDisabled(false);
    }

    public void onChange$dtbBeginningDate() {
        this.clearMessage();
    }

    public void onChange$dtbEndingDate() {
        this.clearMessage();
    }

    public void onClick$btnViewTransactions() throws InterruptedException, RegisterNotFoundException {
        try {

            if (rDaysYes.isChecked()) {
                if (dtbBeginningDate.getValue() != null) {
                    loadList(utilsEJB.getTransactionByBeginningDate(dtbBeginningDate.getValue()));
                } else {
                    this.showMessage("sp.error.date.notSelected", true, null);
                }
            }
            
            if (rDaysNo.isChecked()) {
                if ((dtbEndingDate.getValue() != null) && (dtbEndingDate.getValue() != null)) {
//                    if (dtbBeginningDate.getValue().getTime() > dtbEndingDate.getValue().getTime()) {
                        loadList(utilsEJB.getTransactionByDates(dtbBeginningDate.getValue(), dtbEndingDate.getValue()));
//                    }
                } else {
                    this.showMessage("sp.error.dateSelectInvalid.Invalid", true, null);
                }
            }
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getDataTransactions() throws RegisterNotFoundException {
        try {
        loadList(utilsEJB.getTransactionByDates(dtbBeginningDate.getValue(), dtbEndingDate.getValue()));
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    public List<Transaction> getFilteredList(String filter) {
        List<Transaction> list = new ArrayList<Transaction>();
        if (transactions != null) {
            for (Iterator<Transaction> i = transactions.iterator(); i.hasNext();) {
                Transaction tmp = i.next();
                String field = tmp.getProductId().getName().toLowerCase();
                if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                    list.add(tmp);
                }
            }
        }
        return list;
    }

    public void loadList(List<Transaction> list) {
        Locale locale = new Locale("es", "ES");
        String totalAmount = "";
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (Transaction transaction : list) {
                    String pattern = "dd-MM-yyyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item = new Listitem();
                    item.setValue(transaction);
                    item.appendChild(new Listcell(transaction.getProductId().getName()));
                    item.appendChild(new Listcell(transaction.getTransactionTypeId().getValue()));
                    item.appendChild(new Listcell(transaction.getTransactionSourceId().getName()));
                    if (transaction.getTotalAmount() != null) {
                        totalAmount = numberFormat.format(transaction.getTotalAmount());
                        item.appendChild(new Listcell(totalAmount));
                    } else {
                        item.appendChild(new Listcell(""));
                    }
                    item.appendChild(new Listcell(transaction.getTransactionStatus()));
                    item.appendChild(new Listcell(simpleDateFormat.format(transaction.getCreationDate())));
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, transaction, Permission.VIEW_TRANSACTION) : new Listcell());
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
        transactions = new ArrayList<Transaction>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            transactions = utilsEJB.getTransaction(request);
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
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.transaction.list"));
            AccessControl.saveAction(Permission.LIST_BANK, "Se descargo listado de Transacciones en formato excel");
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

    @Override
    public void onClick$btnAdd() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
