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
import com.alodiga.wallet.admin.web.utils.PDFUtil;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.TransactionType;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListTransactionTypeController extends GenericAbstractListController<TransactionType> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtAlias;
    private PreferencesEJB preferencesEJB = null;
    private List<TransactionType> transactionType = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_TRANSACTION_TYPE));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.EDIT_TRANSACTION_TYPE);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.VIEW_TRANSACTION_TYPE);
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
            checkPermissions();
            adminPage = "adminTransactionType.zul";
            preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
            startListener();
            getData();
            loadList(transactionType);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    public void startListener() {
    }

    public List<TransactionType> getFilteredList(String filter) {
        List<TransactionType> transactionTypeaux = new ArrayList<TransactionType>();
        try {
            if (filter != null && !filter.equals("")) {
                transactionTypeaux = preferencesEJB.searchTransactionType(filter);
            } else {
                return transactionType;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return transactionTypeaux;
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);

    }

    public void loadList(List<TransactionType> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (TransactionType transaction : list) {

	                    item = new Listitem();
	                    item.setValue(transaction);
	                    item.appendChild(new Listcell(transaction.getCode()));
                            item.appendChild(new Listcell(transaction.getValue()));
                            item.appendChild(new Listcell(transaction.getDescription()));
	                    item.appendChild(new ListcellEditButton(adminPage, transaction));
	                    item.appendChild(new ListcellViewButton(adminPage, transaction));
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
        transactionType  = new ArrayList<TransactionType>();
        EJBRequest request1 = new EJBRequest();
        try {
            transactionType   = preferencesEJB.getTransactionTypes(request1);
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
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.crud.transactionType.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_PREFERENCE, "Se descargo listado de Tipo de Transacción en stock formato excel");
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
}
