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
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.enumeraciones.PersonClassificationE;
import com.alodiga.wallet.common.enumeraciones.RequestTypeE;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.LegalPerson;
import com.alodiga.wallet.common.model.LegalRepresentative;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.RequestHasCollectionRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.text.NumberFormat;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

public class ListApplicantOFACBusinessLegalController extends GenericAbstractListController<Person> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private List<Person> applicantList = null;
    private AffiliationRequest affiliationRequestParam;
    private List<LegalPerson> legalPersonList = null;
    private List<LegalRepresentative> legalRepresentativeList = null;
    private User currentUser;
    private Profile currentProfile;
    private Textbox txtName;
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        AdminBusinnessAffiliationRequestsLegalController adminRequestN = new AdminBusinnessAffiliationRequestsLegalController();
        if (adminRequestN.getBusinessAffiliationRequets().getBusinessPersonId().getLegalPerson() != null) {
            affiliationRequestParam = adminRequestN.getBusinessAffiliationRequets();
        }
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
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB); 
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminApplicantOFACBusinessLegal.zul";
            startListener();
            getData();
            loadList(applicantList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        applicantList = new ArrayList<Person>();
        Person person = null;
        legalPersonList = new ArrayList<LegalPerson>();
        legalRepresentativeList = new ArrayList<LegalRepresentative>();
        LegalRepresentative legalRepre = null;
        try {   
                AdminBusinnessAffiliationRequestsLegalController adminRequestN = new AdminBusinnessAffiliationRequestsLegalController();
                if (adminRequestN.getBusinessAffiliationRequets().getBusinessPersonId().getLegalPerson() != null) {
                    affiliationRequestParam = adminRequestN.getBusinessAffiliationRequets();
                }
               
                EJBRequest request = new EJBRequest();
                Map params = new HashMap();
                params.put(EjbConstants.PARAM_PERSON_ID, affiliationRequestParam.getBusinessPersonId().getId());
                request.setParams(params);
                legalPersonList = personEJB.getLegalPersonByPerson(request);
                for (LegalPerson legal : legalPersonList){
                    applicantList.add(legal.getPersonId());
                    request = new EJBRequest();
                    request.setParam(legal.getLegalRepresentativeId().getId());
                    legalRepre = personEJB.loadLegalRepresentative(request);
                    applicantList.add(legalRepre.getPersonId());
                }
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        } catch (Exception ex) {
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
            StringBuilder file = new StringBuilder(Labels.getLabel("sp.menu.collectionsRequest.list"));
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
                loadList(applicantList);
            }
        });
    }

    public void loadList(List<Person> list) {
        NumberFormat formatoPorcentaje = NumberFormat.getPercentInstance(); 
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (Person applicant : list) {
                    item = new Listitem();
                    item.setValue(applicant);
                    if (applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEBUAP.getPersonClassificationCode())){
                       item.appendChild(new Listcell(applicant.getLegalPerson().getBusinessName())); 
                       item.appendChild(new Listcell(applicant.getLegalPerson().getIdentificationNumber()));
                       item.appendChild(new Listcell("Solicitante Principal"));
                       item.appendChild(new Listcell(applicant.getLegalPerson().getStatusApplicantId().getDescription()));
                       if(applicant.getLegalPerson().getPersonId().getReviewOfac() != null){
                          item.appendChild(new Listcell(formatoPorcentaje.format(applicant.getReviewOfac().getResultReview()))); 
                       } else {
                           item.appendChild(new Listcell(""));
                       }
                       item.appendChild(permissionEdit ?createButtonEditModal(affiliationRequestParam) : new Listcell());
                       item.appendChild(permissionRead ?createButtonViewModal(affiliationRequestParam) : new Listcell());
                    } else if(applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEGREP.getPersonClassificationCode())){
                       StringBuilder names = new StringBuilder(applicant.getLegalRepresentative().getFirstNames());
                       names.append(" ");
                       names.append(applicant.getLegalRepresentative().getLastNames());
                       item.appendChild(new Listcell(names.toString()));
                       item.appendChild(new Listcell(applicant.getLegalRepresentative().getIdentificationNumber()));
                       item.appendChild(new Listcell("Representante Legal"));
                       item.appendChild(new Listcell(applicant.getLegalRepresentative().getStatusApplicantId().getDescription()));
                       if(applicant.getLegalRepresentative().getPersonId().getReviewOfac() != null){
                          item.appendChild(new Listcell(formatoPorcentaje.format(applicant.getReviewOfac().getResultReview()))); 
                       } else {
                           item.appendChild(new Listcell(""));
                       }
                       item.appendChild(permissionEdit ?createButtonEditModal(affiliationRequestParam) : new Listcell());
                       item.appendChild(permissionRead ?createButtonViewModal(affiliationRequestParam) : new Listcell());
                    }
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


    @Override
    public List<Person> getFilteredList(String filter) {
        List<Person> personlist = new ArrayList<Person>();
        try {
            if(filter != null && !filter.equals(" ")){
                EJBRequest request = new EJBRequest();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(QueryConstants.PARAM_PERSON_ID , affiliationRequestParam.getBusinessPersonId().getId());
                params.put(QueryConstants.PARAM_AFFILIATION_REQUEST_ID , affiliationRequestParam.getId());
                params.put(QueryConstants.PARAM_NAME, txtName.getText());
                request.setParams(params);
                personlist = personEJB.searchPersonByLegalPersonAndLegalRepresentative(request);
             } else {
                return applicantList;
            }
                  
        } catch (Exception ex ){
            showError(ex);
        }
        return personlist;
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
