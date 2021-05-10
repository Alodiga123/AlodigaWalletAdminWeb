package com.alodiga.wallet.admin.web.controllers;

import java.text.SimpleDateFormat;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.enumeraciones.RequestTypeE;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.RequestHasCollectionRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

public class ListCollectionsAffiliationRequestController extends GenericAbstractListController<RequestHasCollectionRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UtilsEJB utilsEJB = null;
    private List<RequestHasCollectionRequest> collectionsRequests = null;
    private AffiliationRequest affiliationRequestParam;
    private User currentUser;
    private Profile currentProfile;
    private Textbox txtName;
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        affiliationRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (AffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
        initialize();
    }
    
    @Override
    public void checkPermissions() {
        try {
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_COLLECTION_AFFILIATION_REQUEST);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_COLLECTION_AFFILIATION_REQUEST);
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
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminCollectionsAffiliationRequest.zul";
            startListener();
            getData();
            loadList(collectionsRequests);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        collectionsRequests = new ArrayList<RequestHasCollectionRequest>();
        try {
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(EjbConstants.PARAM_AFFILIATION_REQUEST, affiliationRequestParam.getId());
            request.setParams(params);
            collectionsRequests = utilsEJB.getRequestsHasCollectionsRequestByBusinessAffiliationRequest(request);
                
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
    
    
    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            StringBuilder file = new StringBuilder(Labels.getLabel("wallet.collectionsRequest.list"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
        } catch (Exception ex) {
            showError(ex);
        } 
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadList(getFilteredList(txtName.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateCollectionsAffiliationRequest", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {

            public void onEvent(Event evt) {
                getData();
                loadList(collectionsRequests);
            }
        });
    }

    public void loadList(List<RequestHasCollectionRequest> list) {
        String indAprroved = "";
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (RequestHasCollectionRequest collectionsRequest : list) {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    
                    item = new Listitem();
                    item.setValue(collectionsRequest);
                    item.appendChild(new Listcell(collectionsRequest.getCollectionsRequestId().getCollectionTypeId().getCountryId().getName()));
                    item.appendChild(new Listcell(collectionsRequest.getCollectionsRequestId().getCollectionTypeId().getDescription()));
                    if(collectionsRequest.getIndApproved() != null){
                       if (collectionsRequest.getIndApproved() == true) {
                            indAprroved = Labels.getLabel("wallet.common.approveds");
                        } else {
                            indAprroved = Labels.getLabel("wallet.common.rejecteds");
                        } 
                    } else {
                            indAprroved = Labels.getLabel("wallet.common.pending");
                    }
                    item.appendChild(new Listcell(indAprroved));
                    if (collectionsRequest.getUpdateDate() != null){
                      item.appendChild(new Listcell(simpleDateFormat.format(collectionsRequest.getUpdateDate())));  
                    } else {
                      item.appendChild(new Listcell(""));  
                    }
                    
                    item.appendChild(permissionEdit ?createButtonEditModal(collectionsRequest) : new Listcell());
                    item.appendChild(permissionRead ?createButtonViewModal(collectionsRequest) : new Listcell());
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


    @Override
    public List<RequestHasCollectionRequest> getFilteredList(String filter) {
        List<RequestHasCollectionRequest> requestHasCollectionlist = new ArrayList<RequestHasCollectionRequest>();
        try {
            if(filter != null && !filter.equals(" ")){
                EJBRequest request = new EJBRequest();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(QueryConstants.PARAM_AFFILIATION_REQUEST_ID , affiliationRequestParam.getId());
                params.put(QueryConstants.PARAM_NAME, txtName.getText());
                request.setParams(params);
                requestHasCollectionlist = utilsEJB.searchRequestHasCollectionRequest(request);
             } else {
                return collectionsRequests;
            }
                  
        } catch (Exception ex ){
            showError(ex);
        }
        return requestHasCollectionlist;
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
}
