package com.alodiga.wallet.admin.web.controllers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
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
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;

public class ListManualRechargeController extends GenericAbstractListController<TransactionApproveRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private ProductEJB productEJB = null;
    private List<TransactionApproveRequest> approveRequests = null;
    private Datebox dtbBeginningDate;
    private Datebox dtbEndingDate;
    private User currentUser;
    private Profile currentProfile;
    private Combobox cmbStatus; 
    private Combobox cmbProduct; 
    private Textbox txtRequestNumber;
    private Label lblInfo;

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
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_MANUAL_RECHARGUES_APPROVAL);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_MANUAL_RECHARGUES_APPROVAL);        
        } catch (Exception ex) {
            showError(ex);
        }

    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminManualRecharge.zul";
            getData();
            loadList(approveRequests);
        } catch (Exception ex) {
            showError(ex);
        }
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
    
   
    
    public void clearFields() {
        lblInfo.setValue("");
        lblInfo.setVisible(false);
    }


    public void onClick$btnSearch()  {
        try {

        	  clearFields();
              clearMessage();
              EJBRequest _request = new EJBRequest();
              Map<String, Object> params = new HashMap<String, Object>();
              params.put(QueryConstants.PARAM_BEGINNING_DATE, dtbBeginningDate.getValue());
              params.put(QueryConstants.PARAM_ENDING_DATE, dtbEndingDate.getValue());
              if (dtbEndingDate.getValue().getTime() >= dtbBeginningDate.getValue().getTime()) {
                  if (cmbStatus.getSelectedItem() != null && cmbStatus.getSelectedIndex() != 0) {
                      params.put(QueryConstants.PARAM_STATUS_TRANSACTION_APPOVED_REQUEST, ((StatusTransactionApproveRequest) cmbStatus.getSelectedItem().getValue()).getId());
                  }               
                  if (cmbProduct.getSelectedItem() != null && cmbProduct.getSelectedIndex() != 0) {
                      params.put(QueryConstants.PARAM_PRODUCT_ID, ((Product) cmbProduct.getSelectedItem().getValue()).getId());
                  }
                  if (!txtRequestNumber.getText().equals("")) {
                      params.put(QueryConstants.PARAM_REQUEST_NUMBER, txtRequestNumber.getText());
                  }
                  _request.setParams(params);
                  _request.setParam(true);
                  loadList(productEJB.getTransactionApproveRequestByParams(_request));
              } else  {
                  this.showMessage("sp.error.date.invalid", true, null);
              }         
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (Exception ex) {
            showError(ex);
        }
    }


    public List<TransactionApproveRequest> getFilteredList(String filter) {
        List<TransactionApproveRequest> list = new ArrayList<TransactionApproveRequest>();
        if (approveRequests != null) {
            for (Iterator<TransactionApproveRequest> i = approveRequests.iterator(); i.hasNext();) {
            	TransactionApproveRequest tmp = i.next();
                String field = tmp.getProductId().getName().toLowerCase();
                if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                    list.add(tmp);
                }
            }
        }
        return list;
    }

    public void loadList(List<TransactionApproveRequest> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);

                for (TransactionApproveRequest approveRequest : list) {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                    item = new Listitem();
                    item.setValue(approveRequest);

                    item.appendChild(new Listcell(approveRequest.getRequestNumber()));
                    	item.appendChild(new Listcell(simpleDateFormat.format(approveRequest.getCreateDate())));
                    	item.appendChild(new Listcell(approveRequest.getProductId().getName()));
                    	item.appendChild(new Listcell(String.valueOf(approveRequest.getTransactionId().getAmount())));
                    	item.appendChild(new Listcell(approveRequest.getStatusTransactionApproveRequestId().getDescription()));
                    	item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, approveRequest,Permission.EDIT_MANUAL_RECHARGUES_APPROVAL) : new Listcell());
                        item.appendChild(permissionRead ? new ListcellViewButton(adminPage, approveRequest,Permission.VIEW_MANUAL_RECHARGUES_APPROVAL) : new Listcell());
                    	item.setParent(lbxRecords);
                    	
                    
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
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

    public void getData() {
		dtbBeginningDate.setFormat("yyyy/MM/dd");
		dtbBeginningDate.setValue(new Timestamp(new java.util.Date().getTime()));
	    dtbEndingDate.setFormat("yyyy/MM/dd");
	    dtbEndingDate.setValue(new Timestamp(new java.util.Date().getTime()));
	    loadStatus();
		loadProducts();
		approveRequests = new ArrayList<TransactionApproveRequest>();
		try {
			 EJBRequest _request = new EJBRequest();
             Map<String, Object> params = new HashMap<String, Object>();
             params.put(QueryConstants.PARAM_BEGINNING_DATE, dtbBeginningDate.getValue());
             params.put(QueryConstants.PARAM_ENDING_DATE, dtbBeginningDate.getValue()); 
             _request.setParams(params);
             _request.setParam(true);
             approveRequests = productEJB.getTransactionApproveRequestByParams(_request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    private void showEmptyList() {
    	lbxRecords.getItems().clear();
        Listitem item = new Listitem();
        item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.tab.list.bank.operation"));
            AccessControl.saveAction(Permission.LIST_OPERATION_BANK, "Se descargo listado de Operaciones Bankarias en formato excel");
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
        clearFields();
    }


    @Override
    public void onClick$btnAdd() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
