package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Commission;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class ListCommissionByProductController extends GenericAbstractListController<Commission> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Label lblProduct;
    private UtilsEJB utilsEJB = null;
    private List<Commission> commissions = null;
    private User currentUser;
    private Profile currentProfile;
    private Product productParam;
    private Product products = null;

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
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_COMMISSION));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_COMMISSION);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_COMMISSION);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateCommission", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {

            public void onEvent(Event evt) {
                getData();
                loadList(commissions);
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
            adminPage = "/adminCommissionByProduct.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            startListener();
            getData();
            loadList(commissions);
            getProduct();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void getProduct(){
        AdminProductController adminProduct = new AdminProductController();
        if(adminProduct.getProductParent().getId() != null){
            products = adminProduct.getProductParent();
        }
            String product =  products.getName();
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.product.title"));
            file.append(":");
            file.append(" ");
            file.append(product);
            lblProduct.setValue(file.toString());
    }

    public List<Commission> getFilteredList(String filter) {
        List<Commission> auxList = new ArrayList<Commission>();
        for (Iterator<Commission> i = commissions.iterator(); i.hasNext();) {
            Commission tmp = i.next();
            String field = tmp.getProductId().getName().toLowerCase();
            if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
                auxList.add(tmp);
            }
        }
        return auxList;
    }

    public void onClick$btnAdd() throws InterruptedException {
        try {
            Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
            Map<String, Object> paramsPass = new HashMap<String, Object>();
            paramsPass.put("object", commissions);
            final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
            window.doModal();
        } catch (Exception ex) {
            this.showMessage("sp.error.general", true, ex);
        }
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<Commission> list) {
        Locale locale = new Locale("es", "ES");
        String value = "";
        String comission = "";
        String apply= "";
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (Commission commission : list) {
                    item = new Listitem();
                    item.setValue(commission);
                    value = numberFormat.format(commission.getValue());
                    item.appendChild(new Listcell(commission.getTransactionTypeId().getValue()));
                   if (commission.getIndApplicationCommission() == 1){
                        apply = Labels.getLabel("sp.crud.commission.discount.amount");
                    } else {
                        apply = Labels.getLabel("sp.crud.commission.additional.charge");
                    }
                    
                    if (commission.getIsPercentCommision() == 1) {
                        comission = Labels.getLabel("sp.common.yes");
                    } else {
                        comission = Labels.getLabel("sp.common.no");
                    }
                    item.appendChild(new Listcell(apply));
                    item.appendChild(new Listcell(comission));
                    item.appendChild(new Listcell(value));
                    item.appendChild(createButtonEditModal(commission));
                    item.appendChild(createButtonViewModal(commission));
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

    public void getData() {
        Product product = null;
        commissions = new ArrayList<Commission>();
        try {
            
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PRODUCT_KEY, productParam.getId());
            request1.setParams(params);
            commissions = utilsEJB.getCommissionByProduct(request1);
                      
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
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.crud.commissions.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_COMMISSION, "Se descargo listado de comisiones en formato excel");
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
}