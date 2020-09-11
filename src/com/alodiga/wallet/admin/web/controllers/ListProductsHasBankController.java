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
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.BankHasProduct;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

public class ListProductsHasBankController extends GenericAbstractListController<BankHasProduct> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtAlias;
    private ProductEJB productEJB = null;
    private List<BankHasProduct> bankHasProductList = null;
    private User currentUser;
    private Profile currentProfile;
    private Product product;
    private Product productParam;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if (eventType == WebConstants.EVENT_ADD) {
            productParam = null;
        } else {
            productParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Product) Sessions.getCurrent().getAttribute("object") : null;
        }
        initialize();
    }

    @Override
    public void checkPermissions() {
        try {
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_PRODUCT));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.EDIT_PRODUCT);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.VIEW_PRODUCT);
            permissionChangeStatus = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.CHANGE_PRODUCT_STATUS);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void startListener() {
        EventQueue que = EventQueues.lookup("updateProductHasBank", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {

            public void onEvent(Event evt) {
                getData();
                loadList(bankHasProductList);
            }
        });
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "/adminAddProductHasBank.zul";
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            startListener();
            getData();
            if (bankHasProductList != null) {
                loadList(bankHasProductList);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private Listcell initEnabledButton(final Boolean enabled, final Listitem listItem) {

        Listcell cell = new Listcell();
        cell.setValue("");
        final ChangeStatusButton button = new ChangeStatusButton(enabled);
        button.setTooltiptext(Labels.getLabel("sp.common.actions.changeStatus"));
        button.setClass("open orange");
        button.addEventListener("onClick", new EventListener() {

            public void onEvent(Event event) throws Exception {
                changeStatus(button, listItem);
            }
        });

        button.setParent(cell);
        return cell;
    }

    public List<BankHasProduct> getFilteredList(String filter) {
        List<BankHasProduct> auxList = new ArrayList<BankHasProduct>();
        for (Iterator<BankHasProduct> i = bankHasProductList.iterator(); i.hasNext();) {
            BankHasProduct tmp = i.next();
            String field = tmp.getProductId().getName().toLowerCase();
            if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                auxList.add(tmp);
            }
        }
        return auxList;
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Map<String, Object> paramsPass = new HashMap<String, Object>();
        paramsPass.put("object", bankHasProductList);
        final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
        window.doModal();

    }

    private void changeStatus(ChangeStatusButton button, Listitem listItem) {
        try {
            Product product = (Product) listItem.getValue();
            button.changeImageStatus(product.getEnabled());
            product.setEnabled(!product.getEnabled());
            listItem.setValue(product);
            request.setParam(product);
            productEJB.saveProduct(request);
            AccessControl.saveAction(Permission.CHANGE_PRODUCT_STATUS, "changeStatus product = " + product.getId() + " to status = " + !product.getEnabled());

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void loadList(List<BankHasProduct> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (BankHasProduct bankHasProduct : list) {

	                    item = new Listitem();
	                    item.setValue(bankHasProduct);
                            item.appendChild(new Listcell(bankHasProduct.getBankId().getName()));
	                    item.appendChild(createButtonEditModal(bankHasProduct));
                            item.appendChild(createButtonViewModal(bankHasProduct));
                            item.setParent(lbxRecords);
	                }
	            } else {
	                btnDownload.setVisible(false);
	                item = new Listitem();
	                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
	                item.appendChild(new Listcell());
	                item.setParent(lbxRecords);
	            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        bankHasProductList = new ArrayList<BankHasProduct>();
        try {
            if (eventType != WebConstants.EVENT_ADD) {
                    EJBRequest request1 = new EJBRequest();
                    Map params = new HashMap();
                    params.put(Constants.PRODUCT_KEY, productParam.getId());
                    request1.setParams(params);
                    bankHasProductList = productEJB.getBankHasProduct(request1);
                } else {
                    bankHasProductList = null;
                }       
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        } finally {
            showEmptyList();
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
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.provider.list"));
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
    
    public Listcell createButtonEditModal(final Object obg) {
        Listcell listcellEditModal = new Listcell();
        try {
            Button button = new Button();
            button.setImage("/images/icon-edit.png");
            button.setTooltiptext(Labels.getLabel("sp.common.actions.edit"));
            button.setClass("open orange");
            button.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(Event arg0) throws Exception {
                    Sessions.getCurrent().setAttribute("object", obg);
                    Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_EDIT);
                    Map<String, Object> paramsPass = new HashMap<String, Object>();
                    paramsPass.put("object", obg);
                    final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
                    window.doModal();
                }

            });
            button.setParent(listcellEditModal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listcellEditModal;
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
