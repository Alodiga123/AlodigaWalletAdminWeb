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
import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

public class ListCommissionByProductController extends GenericAbstractListController<Commission> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UtilsEJB utilsEJB = null;
    private List<Commission> commissions = null;
    private AdminProductController adminProduct = null;
    private User currentUser;
    private Profile currentProfile;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        adminProduct = new AdminProductController();
        eventType = adminProduct.getEventType();
        super.doAfterCompose(comp);
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
//        EventQueue que = EventQueues.lookup("updateCommission", EventQueues.APPLICATION, true);
//        que.subscribe(new EventListener() {
//
//            public void onEvent(Event evt) {
//                getData();
//                loadList(commissions);
//            }
//        });
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "/adminCommissionByProduct.zul";
            startListener();
            getData();
            loadList(commissions);
        } catch (Exception ex) {
            showError(ex);
        }
    }

//    public List<Commission> getFilteredList(String Commission) {
//        List<Commission> list = new ArrayList<ExchangeRate>();
//        if (commissions != null) {
//            for (Iterator<Commission> i = commissions.iterator(); i.hasNext();) {
//                Commission tmp = i.next();
//                String field = tmp.getProductId().getName().toLowerCase();
//                if (field.indexOf(filter.trim().toLowerCase()) >= 0) {
//                    list.add(tmp);
//                }
//            }
//        }
//        return list;
//    }
//    public void onClick$btnAdd() throws InterruptedException {
//        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
//        Sessions.getCurrent().removeAttribute("object");
//        Executions.getCurrent().sendRedirect(adminPage);
//    }
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
                    item.appendChild(new Listcell(commission.getProductId().getName()));
                    item.appendChild(new Listcell(commission.getTransactionTypeId().getValue()));
                    if (commission.getIsPercentCommision() == 1) {
                        comission = Labels.getLabel("sp.common.yes");
                    } else {
                        comission = Labels.getLabel("sp.common.no");
                    }
                    item.appendChild(new Listcell(comission));
                    item.appendChild(new Listcell(value));
                    item.appendChild(createButtonEditModal(commission));
                    item.appendChild(createButtonViewModal(commission));
//                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, commission, Permission.EDIT_COMMISSION) : new Listcell());
//                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, commission, Permission.VIEW_COMMISSION) : new Listcell());
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
//        commissions = new ArrayList<Commission>();
//        try {
//            

        Product product = null;
        commissions = new ArrayList<Commission>();

        try {
            
//            request.setFirst(0);
//            request.setLimit(null);
//            commissions = utilsEJB.getCommission(request);

            AdminProductController adminProduct = new AdminProductController();
            if (adminProduct.getProduct()!= null) {
                product = adminProduct.getProduct();
            }
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.COMMISSION_KEY, product.getId());
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
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.commissions.list"));
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

    @Override
    public List<Commission> getFilteredList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
