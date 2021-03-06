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
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.zkoss.zul.Textbox;

public class ListBusinessAffiliationRequestsController extends GenericAbstractListController<AffiliationRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtNumber;
    private UtilsEJB utilsEJB = null;
    private List<AffiliationRequest> businessAffiliationRequestList = null;
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
            getData();
            loadDataList(businessAffiliationRequestList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
    }

    public void getData() {
        businessAffiliationRequestList = new ArrayList<AffiliationRequest>();
        try {
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params = new HashMap();
            params.put(QueryConstants.PARAM_REQUEST_TYPE, RequestTypeE.SOAFNE.getId());
            request.setParams(params);
            businessAffiliationRequestList = utilsEJB.getAffiliationRequestByRequestByType(request);
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
                for (AffiliationRequest businessAffiliationRequest : list) {
                    item = new Listitem();
                    item.setValue(businessAffiliationRequest);
                    item.appendChild(new Listcell(businessAffiliationRequest.getNumberRequest()));
                    item.appendChild(new Listcell(simpleDateFormat.format(businessAffiliationRequest.getDateRequest())));
                    if (businessAffiliationRequest.getBusinessPersonId().getPersonTypeId().getIndNaturalPerson() == true) {
                        tipo = "Persona Natural";
                        item.appendChild(new Listcell(tipo));
                        StringBuilder applicantNameNatural = new StringBuilder(businessAffiliationRequest.getBusinessPersonId().getNaturalPerson().getFirstName());
                        applicantNameNatural.append(" ");
                        applicantNameNatural.append(businessAffiliationRequest.getBusinessPersonId().getNaturalPerson().getLastName());
                        item.appendChild(new Listcell(applicantNameNatural.toString()));
                        adminPage = "TabAffiliationRequestsNatural.zul";
                    } else {
                        tipo = "Persona Juridica";
                        item.appendChild(new Listcell(tipo));
                        applicantNameLegal = businessAffiliationRequest.getBusinessPersonId().getLegalPerson().getBusinessName();
                        item.appendChild(new Listcell(applicantNameLegal));
                        adminPage = "TabAffiliationRequestsLegal.zul";
                    }
                    item.appendChild(new Listcell(businessAffiliationRequest.getStatusRequestId().getDescription()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, businessAffiliationRequest, Permission.EDIT_BUSINESS_AFFILIATION_REQUESTS) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, businessAffiliationRequest, Permission.VIEW_BUSINESS_AFFILIATION_REQUESTS) : new Listcell());
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

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("wallet.crud.businessAffiliationRequests.list"));
            AccessControl.saveAction(Permission.LIST_BUSINESS_AFFILIATION_REQUESTS, "Se descargo listado de Solicitud de Afiliacion en formato excel");
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
                params.put(QueryConstants.PARAM_REQUEST_TYPE, RequestTypeE.SOAFNE.getId());
                params.put(QueryConstants.PARAM_NUMBER_REQUEST, txtNumber.getText());
                _request.setParams(params);
                afiliationRequestaux = utilsEJB.searchAffiliationRequestByParams(_request); 
            } else {
                return businessAffiliationRequestList;
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
