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
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ListProductsController extends GenericAbstractListController<Product> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtAlias;
    private ProductEJB productEJB = null;
    private List<Product> products = null;
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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_PRODUCT));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.EDIT_PRODUCT);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.VIEW_PRODUCT);
//            permissionChangeStatus = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(),Permission.CHANGE_PRODUCT_STATUS);
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
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "tabProducts.zul";
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            startListener();
            getData();
            loadList(products);
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

    public List<Product> getFilteredList(String filter) {
        List<Product> auxList = new ArrayList<Product>();
        for (Iterator<Product> i = products.iterator(); i.hasNext();) {
            Product tmp = i.next();
            String field = tmp.getName().toLowerCase();
            if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                auxList.add(tmp);
            }
        }
        return auxList;
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);

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

    public void loadList(List<Product> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (Product product : list) {

	                    item = new Listitem();
	                    item.setValue(product);
	                    item.appendChild(new Listcell(product.getName()));
                            item.appendChild(new Listcell(product.getReferenceCode()));
                            item.appendChild(new Listcell(product.getSymbol()));
                            item.appendChild(new Listcell(product.getCategoryId().getName()));
                            item.appendChild(new Listcell((product.getEnabled()==true? Labels.getLabel("sp.crud.product.yes"):Labels.getLabel("sp.crud.product.no"))));
	                    item.appendChild(new ListcellEditButton(adminPage, product));
	                    item.appendChild(new ListcellViewButton(adminPage, product));
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
        products = new ArrayList<Product>();
        EJBRequest request1 = new EJBRequest();
        try {
            products = productEJB.getProducts(request1);
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
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.crud.product.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_PRODUCT, "Se descargo listado de productos en stock formato excel");
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
