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
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.BusinessCategory;
import com.alodiga.wallet.common.model.BusinessSubCategory;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ListBusinnesSubCategoryController extends GenericAbstractListController<BusinessSubCategory> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<BusinessSubCategory> businessSubCategoryList = null;
    private User currentUser;
    private Profile currentProfile;
    private BusinessCategory businessCategoryParam;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        if (eventType == WebConstants.EVENT_ADD) {
            businessCategoryParam = null;
        } else {
            businessCategoryParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BusinessCategory) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
    }

    @Override
    public void checkPermissions() {
        try {
            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_BUSINESS_SUB_CATEGORY));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_BUSINESS_SUB_CATEGORY);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_BUSINESS_SUB_CATEGORY);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateBusinessSubCategory", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {

            public void onEvent(Event evt) {
                getData();
                loadDataList(businessSubCategoryList);
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
            adminPage = "adminBusinnessSubCategory.zul";
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            startListener();
            getData();
            loadDataList(businessSubCategoryList);
        } catch (Exception ex) {
//            showError(ex);
            showEmptyList();
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
        try {
            Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
            Map<String, Object> paramsPass = new HashMap<String, Object>();
            paramsPass.put("object", businessSubCategoryList);
            final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
            window.doModal();
        } catch (Exception ex) {
            this.showMessage("msj.error.general", true, ex);
        }
    }

    public void getData() {
        businessSubCategoryList = new ArrayList<BusinessSubCategory>();
        try {
            if (businessCategoryParam != null) {
                EJBRequest request1 = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.BUSINESS_CATEGORY_ID, businessCategoryParam.getId());
                request1.setParams(params);
                businessSubCategoryList = utilsEJB.getBusinessSubCategoryByCategory(request1);
            } else {
                showEmptyList();
            }

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
        item.appendChild(new Listcell(Labels.getLabel("msj.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    public void loadDataList(List<BusinessSubCategory> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (BusinessSubCategory businessSubCategory : list) {
                    item = new Listitem();
                    item.setValue(businessSubCategoryList);
                    item.appendChild(new Listcell(businessSubCategory.getMccCode()));
                    item.appendChild(new Listcell(businessSubCategory.getDescription()));
                    item.appendChild(new Listcell(businessSubCategory.getBusinessCategoryId().getDescription()));
                    item.appendChild(createButtonEditModal(businessSubCategory));
                    item.appendChild(createButtonViewModal(businessSubCategory));
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("msj.error.empty.list")));
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
            button.setTooltiptext(Labels.getLabel("wallet.actions.edit"));
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
            button.setTooltiptext(Labels.getLabel("wallet.actions.view"));
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

    public void onClick$btnDownload() throws InterruptedException {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("wallet.crud.businnesSubCategory.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_BUSINESS_SUB_CATEGORY, "Se descargo listado de sub categorías de comercio en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadDataList(getFilteredList(txtName.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @Override
    public List<BusinessSubCategory> getFilteredList(String filter) {
        List<BusinessSubCategory> businessSubCategoriesaux = new ArrayList<BusinessSubCategory>();
        try {
            if (filter != null && !filter.equals("")) {
                businessSubCategoriesaux = utilsEJB.getSearchBusinessSubCategory(filter);
            } else {
                return businessSubCategoryList;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return businessSubCategoriesaux;
    }

    @Override
    public void loadList(List<BusinessSubCategory> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
