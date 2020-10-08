package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.enumeraciones.StatusTransactionApproveRequestE;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.StatusTransactionApproveRequest;
import com.alodiga.wallet.common.model.TransactionApproveRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zul.Textbox;

public class ListManualWithdrawalApprovalController extends GenericAbstractListController<TransactionApproveRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private List<TransactionApproveRequest> manualWithdrawalApproval = null;
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
//            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_MANUAL_WITHDRAWAL_APPROVAL));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_MANUAL_WITHDRAWAL_APPROVAL);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_MANUAL_WITHDRAWAL_APPROVAL);
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
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            checkPermissions();
            adminPage = "adminManualWithdrawalApproval.zul";
            getData();
            loadList(manualWithdrawalApproval);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() { 
        manualWithdrawalApproval = new ArrayList<TransactionApproveRequest>();
        try {

            EJBRequest status = new EJBRequest();
            Map params = new HashMap();
            params = new HashMap();
            params.put(QueryConstants.PARAM_REQUEST_NUMBER, Constants.REQUEST_NUMBER_MANUAL_WITHDRAWAL);
            status.setParams(params);
            manualWithdrawalApproval = utilsEJB.getTransactionApproveRequestByStatus(status);
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

    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void loadList(List<TransactionApproveRequest> list) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (TransactionApproveRequest transactionApproveRequest : list) {
                    item = new Listitem();
                    item.setValue(transactionApproveRequest);
                    item.appendChild(new Listcell(transactionApproveRequest.getRequestNumber()));
                    item.appendChild(new Listcell(transactionApproveRequest.getProductId().getName()));
                    item.appendChild(new Listcell(String.valueOf(transactionApproveRequest.getTransactionId().getAmount())));
                    item.appendChild(new Listcell(transactionApproveRequest.getStatusTransactionApproveRequestId().getDescription()));
                    item.appendChild(new Listcell(simpleDateFormat.format(transactionApproveRequest.getRequestDate())));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, transactionApproveRequest, Permission.EDIT_MANUAL_WITHDRAWAL_APPROVAL) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, transactionApproveRequest, Permission.VIEW_MANUAL_WITHDRAWAL_APPROVAL) : new Listcell());
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

    public void onClick$btnDownload() throws InterruptedException {
        try {        
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.crud.manual.recharge.list.download"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_MANUAL_WITHDRAWAL_APPROVAL, "Se descargo listado de Retiro Manual en formato excel");

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
//            loadDataList(getFilterList(txtName.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @Override
    public List<TransactionApproveRequest> getFilteredList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onClick$btnAdd() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
