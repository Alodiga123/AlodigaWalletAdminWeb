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
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.zkoss.zul.Textbox;

public class ListUsersAffiliationRequestsController extends GenericAbstractListController<AffiliationRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtNumber;
    private UtilsEJB utilsEJB = null;
    private List<AffiliationRequest> userAffiliationRequestList = null;
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
//            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_BUSINESS_AFFILIATION_REQUESTS));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_BUSINESS_AFFILIATION_REQUESTS);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_BUSINESS_AFFILIATION_REQUESTS);
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
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            checkPermissions();
            adminPage = "TabUserAffiliationRequests.zul";
            getData();
            loadDataList(userAffiliationRequestList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
    }

    public void getData() {
        userAffiliationRequestList = new ArrayList<AffiliationRequest>();
        try {
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params = new HashMap();
            params.put(QueryConstants.PARAM_REQUEST_TYPE, RequestTypeE.SORUBI.getId());
            request.setParams(params);
            userAffiliationRequestList = utilsEJB.getAffiliationRequestByRequestByType(request);
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

    public void loadDataList(List<AffiliationRequest> list) {
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String applicantNameLegal = "";
        String tipo = "";
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (AffiliationRequest usersAffiliationRequest : list) {
                    item = new Listitem();
                    item.setValue(usersAffiliationRequest);
                    item.appendChild(new Listcell(usersAffiliationRequest.getNumberRequest()));
                    item.appendChild(new Listcell(simpleDateFormat.format(usersAffiliationRequest.getDateRequest())));
                    StringBuilder applicantNameNatural = new StringBuilder(usersAffiliationRequest.getUserRegisterUnifiedId().getNaturalPerson().getFirstName());
                    applicantNameNatural.append(" ");
                    applicantNameNatural.append(usersAffiliationRequest.getUserRegisterUnifiedId().getNaturalPerson().getLastName());
                    item.appendChild(new Listcell(applicantNameNatural.toString()));
                    item.appendChild(new Listcell(usersAffiliationRequest.getStatusRequestId().getDescription()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, usersAffiliationRequest, Permission.EDIT_USERS_AFFILIATION_REQUESTS) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, usersAffiliationRequest, Permission.VIEW_USERS_AFFILIATION_REQUESTS) : new Listcell());
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
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.userAffiliationRequests.list.download"));
            file.append("_");
            file.append(date);
            Utils.exportExcel(lbxRecords, file.toString());
            AccessControl.saveAction(Permission.LIST_BUSINESS_AFFILIATION_REQUESTS, "Se descargo listado de países en stock formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtNumber.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadDataList(getFilteredList(txtNumber.getText()));
        } catch (Exception ex) {
            showError(ex);
        }         
    }
    
    public List<AffiliationRequest> getFilteredList(String filter) {
        List<AffiliationRequest> afiliationRequestaux = new ArrayList<AffiliationRequest>();
        try {
            if (filter != null && !filter.equals("")) {
                EJBRequest _request = new EJBRequest();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(QueryConstants.PARAM_REQUEST_TYPE, RequestTypeE.SORUBI.getId());
                params.put(QueryConstants.PARAM_NUMBER_REQUEST, txtNumber.getText());
                _request.setParams(params);
                afiliationRequestaux = utilsEJB.searchAffiliationRequestByParams(_request); 
            } else {
                return userAffiliationRequestList;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return afiliationRequestaux;
    }  

    @Override
    public void loadList(List<AffiliationRequest> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
