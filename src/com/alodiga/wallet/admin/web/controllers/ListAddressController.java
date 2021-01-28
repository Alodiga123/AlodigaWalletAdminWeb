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
import com.alodiga.wallet.common.enumeraciones.RequestTypeE;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Address;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.PersonHasAddress;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ListAddressController extends GenericAbstractListController<PersonHasAddress> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<PersonHasAddress> personHasAddressList = null;
    private User currentUser;
    private Profile currentProfile;
    private Integer eventType;
    private AffiliationRequest affiliationRequetsParam;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            affiliationRequetsParam = null;
        } else {
            affiliationRequetsParam = (Sessions.getCurrent().getAttribute("object") != null) ? (AffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
        }
        initialize();
    }

    @Override
    public void checkPermissions() {
        try {
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_ADDRESS);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_ADDRESS);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateAddress", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {
            public void onEvent(Event evt) {
                getData();
                loadList(personHasAddressList);
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
            adminPage = "adminAddress.zul";
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            startListener();
            getData();
            loadList(personHasAddressList);
        } catch (Exception ex) {
            showEmptyList();
        }
    }

    public void getData() {
        personHasAddressList = new ArrayList<PersonHasAddress>();
        Address address = null;
        Person person = null;
        try {
            
            if(affiliationRequetsParam.getRequestTypeId().getCode().equals(RequestTypeE.SOAFNE.getRequestTypeCode())){
                person = affiliationRequetsParam.getBusinessPersonId();
                EJBRequest request1 = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.PERSON_KEY, person.getId());
                request1.setParams(params);
                personHasAddressList = personEJB.getPersonHasAddressesByPerson(request1);
            } else if(affiliationRequetsParam.getRequestTypeId().getCode().equals(RequestTypeE.SORUBI.getRequestTypeCode())){
                person = affiliationRequetsParam.getUserRegisterUnifiedId();
                EJBRequest request1 = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.PERSON_KEY, person.getId());
                request1.setParams(params);
                personHasAddressList = personEJB.getPersonHasAddressesByPerson(request1);
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

    public void loadList(List<PersonHasAddress> list) {
        String indAddressDelivery = "";
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (PersonHasAddress personHasAddress : list) {
                    item = new Listitem();
                    item.setValue(personHasAddress);
                    item.appendChild(new Listcell(personHasAddress.getAddressId().getCountryId().getName()));
                    item.appendChild(new Listcell(personHasAddress.getAddressId().getCityId().getName()));

                    if(personHasAddress.getAddressId().getAddressTypeId() == null){
                        item.appendChild(new Listcell(Labels.getLabel("wallet.common.notSpecified")));
                    } else {
                        item.appendChild(new Listcell(personHasAddress.getAddressId().getAddressTypeId().getDescription()));
                    }
                    
                    if (personHasAddress.getAddressId().getIndMainAddress() == true) {
                        indAddressDelivery = "Yes";
                    } else {
                        indAddressDelivery = "No";
                    }
                    
                    item.appendChild(new Listcell(indAddressDelivery));
                    item.appendChild(new Listcell(personHasAddress.getAddressId().getZipCode()));
                    item.appendChild(createButtonEditModal(personHasAddress));
                    item.appendChild(createButtonViewModal(personHasAddress));
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
            Utils.exportExcel(lbxRecords, Labels.getLabel("wallet.crud.address.list"));
            AccessControl.saveAction(Permission.LIST_ADDRESS, "Se descargo la lista de direcciones en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
//            loadDataList(getFilterList(txtName.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @Override
    public List<PersonHasAddress> getFilteredList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onClick$btnAdd() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
