package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.BusinessEJB;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.PreferenceValue;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.TransactionType;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.EjbUtils;
import com.alodiga.wallet.common.utils.QueryConstants;
import com.portal.business.commons.models.Business;

public class ListSettingController extends GenericAbstractListController<PreferenceValue> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Combobox cmbBusiness;
    private PreferencesEJB preferencesEJB = null;
    private BusinessEJB businessEJB;
    private ProductEJB productEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<PreferenceValue> preferenceValues = null;
    private User currentUser;
    private Profile currentProfile;
    private Combobox cmbProduct;
    private Combobox cmbTransactionType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void checkPermissions() {
        try {
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_PREFERENCES));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_PREFERENCES);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_PREFERENCES);
            permissionChangeStatus = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.CHANGE_PREFERENCES_STATUS);
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
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
            businessEJB = (BusinessEJB) EJBServiceLocator.getInstance().get(EjbConstants.BUSINESS_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminSpecificsSettings.zul";
            getData();
//            loadList(preferenceValues);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public List<PreferenceValue> getFilteredList(String filter) {
        List<PreferenceValue> list = new ArrayList<PreferenceValue>();
        if (preferenceValues != null) {
            for (Iterator<PreferenceValue> i = preferenceValues.iterator(); i.hasNext();) {
            	PreferenceValue tmp = i.next();
                String field = tmp.getBussinessId().toString().toLowerCase();
                if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                    list.add(tmp);
                }
            }
        }
        return list;
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<PreferenceValue> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                
                for (PreferenceValue preferenceValue : list) {
                    item = new Listitem();
                    item.setValue(preferenceValue);
                    item.appendChild(new Listcell(preferenceValue.getProductId().getName()));
                    item.appendChild(new Listcell(preferenceValue.getTransactionTypeId().getValue()));
                    
                    item.appendChild(new Listcell(((Business) cmbBusiness.getSelectedItem().getValue()).getName()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, preferenceValue, Permission.EDIT_PREFERENCES) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, preferenceValue, Permission.VIEW_PREFERENCES) : new Listcell());
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }


    public void getData() {
    	loadProducts();
    	loadTransactionType();
    	loadBusiness();
    	preferenceValues = new ArrayList<PreferenceValue>();
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            request.setParams(params);
            preferenceValues = preferencesEJB.getPreferenceValuesGroupByBussinessId(request);
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
        item.setParent(lbxRecords);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, "Listado de Preferencias Especificas_"+EjbUtils.getFormatedDate(new Date(),"dd/MM/yyyy"));
            AccessControl.saveAction(Permission.LIST_PREFERENCES, "Se descargo listado de preferencias especificas en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        
    }

    public void onClick$btnSearch() throws InterruptedException {
          EJBRequest _request = new EJBRequest();
          Map<String, Object> params = new HashMap<String, Object>();
          try {
              if (cmbTransactionType.getSelectedItem() != null && cmbTransactionType.getSelectedIndex() != 0) {
                  params.put(QueryConstants.PARAM_TRANSACTION_TYPE_ID, ((TransactionType) cmbTransactionType.getSelectedItem().getValue()).getId());
              }
              if (cmbProduct.getSelectedItem() != null && cmbProduct.getSelectedIndex() != 0) {
                  params.put(QueryConstants.PARAM_PRODUCT_ID, ((Product) cmbProduct.getSelectedItem().getValue()).getId());
              }
              if(cmbBusiness.getSelectedItem() != null) {
            	  params.put(QueryConstants.PARAM_BUSSINESS_ID, ((Business) cmbBusiness.getSelectedItem().getValue()).getId());
              }
              _request.setParams(params);
              loadList(preferenceValues = preferencesEJB.getPreferenceValuesGroupByBussinessId(_request));        
	    } catch (EmptyListException ex) {
	        showEmptyList();
	    } catch (Exception ex) {
	        showError(ex);
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
    
    private void loadTransactionType() {

        try {
        	cmbTransactionType.getItems().clear();
        	EJBRequest request = new EJBRequest();
            List<TransactionType> transactionTypes = utilsEJB.getTransactionType(request);
            Comboitem item = new Comboitem();
            item.setLabel(Labels.getLabel("sp.common.all"));
            item.setParent(cmbTransactionType);
            cmbTransactionType.setSelectedItem(item);
            for (int i = 0; i < transactionTypes.size(); i++) {
                item = new Comboitem();
                item.setValue(transactionTypes.get(i));
                item.setLabel(transactionTypes.get(i).getValue());
                item.setParent(cmbTransactionType);
            }
        } catch (Exception ex) {
            this.showError(ex);
        }

    }
    
    private void loadBusiness() {

        try {
        	cmbBusiness.getItems().clear();
            List<Business> businesses = businessEJB.getAll();
            for (int i = 0; i < businesses.size(); i++) {
            	Comboitem item = new Comboitem();
                item.setValue(businesses.get(i));
                item.setLabel(businesses.get(i).getName());
                item.setParent(cmbBusiness);
            }
        } catch (Exception ex) {
            this.showError(ex);
        }

    }
}
