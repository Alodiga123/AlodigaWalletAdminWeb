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
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.enumeraciones.StatusTransactionApproveRequestE;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Product;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;

public class ListManualWithdrawalApprovalController extends GenericAbstractListController<TransactionApproveRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Datebox dtbBeginningDate;
    private Datebox dtbEndingDate;
    private Textbox txtRequestNumber;
    private Combobox cmbStatus; 
    private Combobox cmbProduct; 
    private Listbox lbxRecords;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
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
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
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
        loadStatus();
	loadProducts();
        try {
            EJBRequest status = new EJBRequest();
            Map params = new HashMap();
            params = new HashMap();
            params.put(QueryConstants.PARAM_REQUEST_NUMBER, Constants.REQUEST_NUMBER_MANUAL_WITHDRAWAL);
            status.setParams(params);
            manualWithdrawalApproval = productEJB.getTransactionApproveRequestByStatus(status);
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
        cmbStatus.setSelectedIndex(0);
        cmbProduct.setSelectedIndex(0);
        txtRequestNumber.setText("");;
        Date date = new Date();
        dtbBeginningDate.setValue(date);
        dtbEndingDate.setValue(date);
    }

   
    public void onClick$btnSearchh() throws InterruptedException {
        try {
              clearMessage();
              EJBRequest _request = new EJBRequest();
              Map<String, Object> params = new HashMap<String, Object>();
              params.put(QueryConstants.PARAM_BEGINNING_DATE, dtbBeginningDate.getValue());
              params.put(QueryConstants.PARAM_ENDING_DATE, dtbEndingDate.getValue());
              if (dtbEndingDate.getValue().getTime() >= dtbBeginningDate.getValue().getTime()) {
                  if (cmbStatus.getSelectedItem() != null && cmbStatus.getSelectedIndex() != 0) {
                      params.put(QueryConstants.PARAM_STATUS_TRANSACTION_APPROVE_REQUEST_ID, ((StatusTransactionApproveRequest) cmbStatus.getSelectedItem().getValue()).getId());
                  }               
                  if (cmbProduct.getSelectedItem() != null && cmbProduct.getSelectedIndex() != 0) {
                      params.put(QueryConstants.PARAM_PRODUCT_ID, ((Product) cmbProduct.getSelectedItem().getValue()).getId());
                  }
                  if (!txtRequestNumber.getText().equals("")) {
                      params.put(QueryConstants.PARAM_REQUEST_NUMBER, txtRequestNumber.getText());
                  }
                  _request.setParams(params);
                  _request.setParam(true);
                  loadList(productEJB.searchTransactionApproveRequestByParamsMWAR(_request));
              } else  {
                  this.showMessage("sp.error.date.invalid", true, null);
              }         
        } catch (EmptyListException ex) {
            showEmptyList();
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
    
    private void loadStatus() {

        try {
            cmbStatus.getItems().clear();
            EJBRequest request = new EJBRequest();
            List<StatusTransactionApproveRequest> transactionApproveRequests = productEJB.getStatusTransactionApproveRequests(request);
            Comboitem item = new Comboitem();
            item.setLabel(Labels.getLabel("sp.common.all"));
            item.setParent(cmbStatus);
            cmbStatus.setSelectedItem(item);
            for (int i = 0; i < transactionApproveRequests.size(); i++) {
                item = new Comboitem();
                item.setValue(transactionApproveRequests.get(i));
                item.setLabel(transactionApproveRequests.get(i).getDescription());
                item.setParent(cmbStatus);
            }
        } catch (Exception ex) {
            this.showError(ex);
        }

    }
    
    
    private void loadProducts() {

        try {
        	cmbProduct.getItems().clear();
        	EJBRequest request = new EJBRequest();
            List<Product> products = productEJB.getProducts(request);
            Comboitem item = new Comboitem();
            item.setLabel(Labels.getLabel("sp.common.all"));
            item.setParent(cmbProduct);
            cmbProduct.setSelectedItem(item);
            for (int i = 0; i < products.size(); i++) {
                item = new Comboitem();
                item.setValue(products.get(i));
                item.setLabel(products.get(i).getName());
                item.setParent(cmbProduct);
            }
        } catch (Exception ex) {
            this.showError(ex);
        }

    }

    @Override
    public void onClick$btnSearch() throws InterruptedException {
        try {
              EJBRequest _request = new EJBRequest();
              Map<String, Object> params = new HashMap<String, Object>();
              params.put(QueryConstants.PARAM_BEGINNING_DATE, dtbBeginningDate.getValue());
              params.put(QueryConstants.PARAM_ENDING_DATE, dtbEndingDate.getValue());
              if (dtbEndingDate.getValue().getTime() >= dtbBeginningDate.getValue().getTime()) {
                  if (cmbStatus.getSelectedItem() != null && cmbStatus.getSelectedIndex() != 0) {
                      params.put(QueryConstants.PARAM_STATUS_TRANSACTION_APPROVE_REQUEST_ID, ((StatusTransactionApproveRequest) cmbStatus.getSelectedItem().getValue()).getId());
                  }               
                  if (cmbProduct.getSelectedItem() != null && cmbProduct.getSelectedIndex() != 0) {
                      params.put(QueryConstants.PARAM_PRODUCT_ID, ((Product) cmbProduct.getSelectedItem().getValue()).getId());
                  }
                  if (!txtRequestNumber.getText().equals("")) {
                      params.put(QueryConstants.PARAM_REQUEST_NUMBER, txtRequestNumber.getText());
                  }
                  _request.setParams(params);
                  _request.setParam(true);
                  loadList(productEJB.searchTransactionApproveRequestByParamsMWAR(_request));
              } else  {
                  this.showMessage("sp.error.date.invalid", true, null);
              }         
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (Exception ex) {
            showError(ex);
        }
    }
}
