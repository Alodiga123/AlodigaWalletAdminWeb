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
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;
import org.zkoss.zul.Button;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Bank;
import com.alodiga.wallet.common.model.BankOperation;
import com.alodiga.wallet.common.model.BankOperationMode;
import com.alodiga.wallet.common.model.BankOperationType;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;

public class ListBankOperationController extends GenericAbstractListController<BankOperation> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private List<BankOperation> bankOperations = null;
    private Datebox dtbBeginningDate;
    private Datebox dtbEndingDate;
    private User currentUser;
    private Profile currentProfile;
    private Combobox cmbOperationType; 
    private Combobox cmbOperationMode; 
    private Combobox cmbProduct; 
    private Combobox cmbBank;
    private Bank bankParam;
    private Label lblInfo;
    private Label lblBankName;
    private Bank bank;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        bankParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Bank) Sessions.getCurrent().getAttribute("object") : null;
        initialize();
        getBank();
    }

    @Override
    public void startListener() {
    }

    @Override
    public void checkPermissions() {
        try {
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_OPERATION_BANK);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_OPERATION_BANK);        
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
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminBankOperations.zul";
            getData();
            loadList(bankOperations);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    private void loadOperationTypes() {

        try {
            cmbOperationType.getItems().clear();
            EJBRequest request = new EJBRequest();
            List<BankOperationType> operationTypes = utilsEJB.getBankOperationTypes(request);
            Comboitem item = new Comboitem();
            item.setLabel(Labels.getLabel("sp.common.all"));
            item.setParent(cmbOperationType);
            cmbOperationType.setSelectedItem(item);
            for (int i = 0; i < operationTypes.size(); i++) {
                item = new Comboitem();
                item.setValue(operationTypes.get(i));
                item.setLabel(operationTypes.get(i).getName());
                item.setParent(cmbOperationType);
            }
        } catch (Exception ex) {
            this.showError(ex);
        }

    }
    
    private void loadOperationModes() {

        try {
            cmbOperationMode.getItems().clear();
            EJBRequest request = new EJBRequest();
            List<BankOperationMode> operationModes = utilsEJB.getBankOperationModes(request);
            Comboitem item = new Comboitem();
            item.setLabel(Labels.getLabel("sp.common.all"));
            item.setParent(cmbOperationMode);
            cmbOperationMode.setSelectedItem(item);
            for (int i = 0; i < operationModes.size(); i++) {
                item = new Comboitem();
                item.setValue(operationModes.get(i));
                item.setLabel(operationModes.get(i).getName());
                item.setParent(cmbOperationMode);
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
    
    private void loadBanks(Bank bank) {

        try {
            cmbBank.getItems().clear();
            Comboitem item = new Comboitem();
            item.setValue(bank.getId());
            item.setLabel(bank.getName());
            item.setParent(cmbBank);
            cmbBank.setSelectedItem(item);           
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
              params.put(QueryConstants.PARAM_BANK_ID, bankParam.getId());
              if (dtbEndingDate.getValue().getTime() >= dtbBeginningDate.getValue().getTime()) {
                  if (cmbOperationType.getSelectedItem() != null && cmbOperationType.getSelectedIndex() != 0) {
                      params.put(QueryConstants.PARAM_BANK_OPERATION_TYPE_ID, ((BankOperationType) cmbOperationType.getSelectedItem().getValue()).getId());
                  }
                  if (cmbOperationMode.getSelectedItem() != null && cmbOperationMode.getSelectedIndex() != 0) {
                      params.put(QueryConstants.PARAM_BANK_OPERATION_MODE_ID, ((BankOperationMode) cmbOperationMode.getSelectedItem().getValue()).getId());
                  }
                  if (cmbProduct.getSelectedItem() != null && cmbProduct.getSelectedIndex() != 0) {
                      params.put(QueryConstants.PARAM_PRODUCT_ID, ((Product) cmbProduct.getSelectedItem().getValue()).getId());
                  }
                  _request.setParams(params);
                  _request.setParam(true);
                  loadList(utilsEJB.getBankOperationsByParams(_request));
              } else  {
                  this.showMessage("sp.error.date.invalid", true, null);
              }         
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (Exception ex) {
            showError(ex);
        }
    }


    public List<BankOperation> getFilteredList(String filter) {
        List<BankOperation> list = new ArrayList<BankOperation>();
        if (bankOperations != null) {
            for (Iterator<BankOperation> i = bankOperations.iterator(); i.hasNext();) {
            	BankOperation tmp = i.next();
                String field = tmp.getProductId().getName().toLowerCase();
                if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                    list.add(tmp);
                }
            }
        }
        return list;
    }

    public void loadList(List<BankOperation> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);

                for (BankOperation bankOperation : list) {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                    item = new Listitem();
                    item.setValue(bankOperation);
                    item.appendChild(new Listcell(bankOperation.getBankOperationNumber()));
                    item.appendChild(new Listcell(simpleDateFormat.format(bankOperation.getTransactionId().getCreationDate())));
                    item.appendChild(new Listcell(bankOperation.getBankOperationTypeId().getName()));
                    item.appendChild(new Listcell(bankOperation.getProductId().getName()));
                    item.appendChild(new Listcell(String.valueOf(bankOperation.getBankOperationAmount())));
                    item.appendChild(new Listcell(String.valueOf(bankOperation.getCommisionId().getValue())));
                    item.appendChild(new Listcell(bankOperation.getBankOperationModeId().getName()));
                    item.appendChild(createButtonViewModal(bankOperation));
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
		dtbBeginningDate.setFormat("yyyy/MM/dd");
		dtbBeginningDate.setValue(new Timestamp(new java.util.Date().getTime()));
	    dtbEndingDate.setFormat("yyyy/MM/dd");
	    dtbEndingDate.setValue(new Timestamp(new java.util.Date().getTime()));
		loadOperationModes();
		loadOperationTypes();
		loadProducts();
		loadBanks(bankParam);
		bankOperations = new ArrayList<BankOperation>();
		try {
			 EJBRequest _request = new EJBRequest();
             Map<String, Object> params = new HashMap<String, Object>();
             params.put(QueryConstants.PARAM_BEGINNING_DATE, dtbBeginningDate.getValue());
             params.put(QueryConstants.PARAM_ENDING_DATE, dtbBeginningDate.getValue()); 
             params.put(QueryConstants.PARAM_BANK_ID,bankParam.getId());
             _request.setParams(params);
             _request.setParam(true);
            bankOperations = utilsEJB.getBankOperationsByParams(_request);
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
    
    public void getBank(){
        AdminBankController adminBank = new AdminBankController();
        if(adminBank.getBankParent().getId() != null){
            bank = adminBank.getBankParent();
            lblBankName.setValue(bank.getName()); 
        }
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
        cmbOperationType.setSelectedIndex(0);
        cmbOperationMode.setSelectedIndex(0);
        cmbProduct.setSelectedIndex(0);
        Date date = new Date();
        dtbBeginningDate.setValue(date);
        dtbEndingDate.setValue(date);
        clearFields();
    }


    @Override
    public void onClick$btnAdd() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
