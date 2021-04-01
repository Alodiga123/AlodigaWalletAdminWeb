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
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.enumeraciones.PersonClassificationE;
import com.alodiga.wallet.common.enumeraciones.StatusApplicantE;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.LegalPerson;
import com.alodiga.wallet.common.model.LegalRepresentative;
import com.alodiga.wallet.common.model.NaturalPerson;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.PersonClassification;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.ReviewOfac;
import com.alodiga.wallet.common.model.StatusApplicant;
import com.alodiga.wallet.common.model.StatusRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import com.alodiga.ws.remittance.services.WSOFACMethodProxy;
import com.alodiga.ws.remittance.services.WsExcludeListResponse;
import com.alodiga.ws.remittance.services.WsLoginResponse;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

public class ListAplicantOFACController extends GenericAbstractListController<Person> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Combobox cmbStatus;
    private Textbox txtNumber;
    private UserEJB userEJB = null;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<Person> personList = null;
    private LegalPerson legalPersonParam;
    private List<LegalPerson> legalPerson;
    private User currentUser;
    private Profile currentProfile;
    private AdminBusinnessAffiliationRequestsLegalController legalRequest;
    private AdminBusinnessAffiliationRequestsNaturalController naturalRequest;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        startListener();
    }

    @Override
    public void checkPermissions() {
        try {
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_APLICANT_OFAC);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_APLICANT_OFAC);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateApplicantOFAC", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {
            public void onEvent(Event evt) {
                getData();
                loadList(personList);
            }
        });
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminAplicantOFAC.zul";
            getData();
            loadList(personList);
            loadStatusApplicant();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<Person> list) {
        String applicantNameLegal = "";
        String tipo = "";
        NumberFormat formatoPorcentaje = NumberFormat.getPercentInstance();
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (Person aplicant : list) {
                    item = new Listitem();
                    item.setValue(aplicant);   
                        if (aplicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEBUAP.getPersonClassificationCode())) {
                                item.appendChild(new Listcell(aplicant.getAffiliationRequest()!=null?aplicant.getAffiliationRequest().getNumberRequest():""));
                                applicantNameLegal = aplicant.getLegalPerson()!=null?aplicant.getLegalPerson().getBusinessName():"";
                                item.appendChild(new Listcell(applicantNameLegal));
                                item.appendChild(new Listcell(Labels.getLabel("wallet.common.legalApplicant")));
                                item.appendChild(new Listcell(aplicant.getLegalPerson()!=null?aplicant.getLegalPerson().getStatusApplicantId().getDescription():""));
                                if(aplicant.getLegalPerson()!=null?aplicant.getLegalPerson().getPersonId().getReviewOfac() != null:false){
                                    item.appendChild(new Listcell(formatoPorcentaje.format(aplicant.getLegalPerson().getPersonId().getReviewOfac().getResultReview())));
                                } else {
                                    item.appendChild(new Listcell(""));
                                }
                                item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                                item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());  
                        } else if (aplicant.getPersonClassificationId().getCode().equals(PersonClassificationE.NABUAP.getPersonClassificationCode())){
                                item.appendChild(new Listcell(aplicant.getAffiliationRequest()!=null?aplicant.getAffiliationRequest().getNumberRequest():""));
                                StringBuilder applicarBusinessNatural = new StringBuilder(aplicant.getNaturalPerson()!=null?aplicant.getNaturalPerson().getFirstName():"");
                                applicarBusinessNatural.append(" ");
                                applicarBusinessNatural.append(aplicant.getNaturalPerson()!=null?aplicant.getNaturalPerson().getLastName():"");
                                item.appendChild(new Listcell(applicarBusinessNatural.toString()));
                                item.appendChild(new Listcell(Labels.getLabel("wallet.common.naturalApplicant")));
                                item.appendChild(new Listcell(aplicant.getNaturalPerson()!=null?aplicant.getNaturalPerson().getStatusApplicantId().getDescription():""));
                                if(aplicant.getNaturalPerson()!=null?aplicant.getNaturalPerson().getPersonId().getReviewOfac() != null:false){
                                    item.appendChild(new Listcell(formatoPorcentaje.format(aplicant.getNaturalPerson().getPersonId().getReviewOfac().getResultReview())));
                                } else {
                                    item.appendChild(new Listcell(""));
                                }
                                item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                                item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());
                        } else if (aplicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEGREP.getPersonClassificationCode())){
                                item.appendChild(new Listcell(aplicant.getAffiliationRequest()!=null?aplicant.getAffiliationRequest().getNumberRequest():""));
                                StringBuilder applicantRepresentative = new StringBuilder(aplicant.getLegalRepresentative()!=null?aplicant.getLegalRepresentative().getFirstNames():"");
                                applicantRepresentative.append(" ");
                                applicantRepresentative.append(aplicant.getLegalRepresentative()!=null?aplicant.getLegalRepresentative().getLastNames():"");
                                item.appendChild(new Listcell(applicantRepresentative.toString()));
                                item.appendChild(new Listcell(Labels.getLabel("wallet.common.legalRepresentative")));
                                item.appendChild(new Listcell(aplicant.getLegalRepresentative()!=null?aplicant.getLegalRepresentative().getStatusApplicantId().getDescription():""));
                                if(aplicant.getLegalRepresentative()!=null?aplicant.getLegalRepresentative().getPersonId().getReviewOfac() != null:false){
                                    item.appendChild(new Listcell(formatoPorcentaje.format(aplicant.getLegalRepresentative().getPersonId().getReviewOfac().getResultReview())));
                                } else {
                                    item.appendChild(new Listcell(""));
                                }
                                item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                                item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());
                        } 
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

    public void getData() {
        personList = new ArrayList<Person>();
        List<LegalPerson> legalPerson = new ArrayList<LegalPerson>();
        List<AffiliationRequest> affiliationRequest = new ArrayList<AffiliationRequest>();
        Long legalPersonId = 0L;
        EJBRequest request = new EJBRequest();
        Map<String, Object> params = new HashMap<String, Object>();
        Long affiliationRequestId = 0L;
        try {
            request.setFirst(0);
            request.setLimit(null);
            personList = personEJB.getPersonBusinessApplicant(request);
            for (Person p: personList) {
                if (p.getAffiliationRequest() == null) {
                    params = new HashMap<String, Object>();
                    params.put(QueryConstants.PARAM_LEGAL_REPRESENTATIVE_ID , p.getLegalRepresentative().getId());
                    request.setParams(params);
                    legalPerson = personEJB.getLegalPersonByLegalRepresentative(request);
                    for (LegalPerson lg: legalPerson) {
                        legalPersonId = lg.getPersonId().getId();
                    }
                    params = new HashMap<String, Object>();                    
                    params.put(QueryConstants.PARAM_LEGAL_PERSON_ID, legalPersonId);
                    request.setParams(params);
                    affiliationRequest = utilsEJB.getAffiliationRequestByLegalPerson(request);
                    for (AffiliationRequest ar: affiliationRequest) {
                        p.setAffiliationRequest(ar);
                        personEJB.savePerson(p);
                        affiliationRequestId = ar.getId();
                    }
                } else {                   
                    affiliationRequestId = p.getAffiliationRequest().getId(); 
                }
                Long haveReviewOFAC = utilsEJB.haveReviewOFACByPerson(p.getId());
                   if (haveReviewOFAC > 0) {
                       request = new EJBRequest();
                       params = new HashMap();
                       params.put(Constants.PERSON_KEY, p.getId());
                       params.put(Constants.AFFILIATION_REQUEST_KEY, affiliationRequestId);
                       request.setParams(params);
                       List<ReviewOfac> reviewOFAC = utilsEJB.getReviewOfacByRequest(request);
                       for (ReviewOfac r: reviewOFAC) {
                            p.setReviewOfac(r);
                        }
                   }
            }
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
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

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("wallet.businessAffiliationRequests.ofac.list"));
            AccessControl.saveAction(Permission.LIST_APLICANT_OFAC, "Se descargo listado OFAC en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtNumber.setText("");
    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
            loadList(getFilteredList(txtNumber.getText()));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnReviewOFAC() {
        int indBlackList = 0;
        String lastName = "";
        String firstName = "";
        String businessName = "";
        float ofacPercentege = 0.1F;
        NaturalPerson naturalPerson = new NaturalPerson();
        LegalPerson legalPerson = new LegalPerson();
        LegalRepresentative legalRepresentative = new LegalRepresentative();
        AffiliationRequest affiliationRequest = new AffiliationRequest();
        WSOFACMethodProxy ofac = new WSOFACMethodProxy();
        try {
            WsLoginResponse loginResponse = new WsLoginResponse();
            loginResponse = ofac.loginWS("alodiga", "d6f80e647631bb4522392aff53370502");
            WsExcludeListResponse ofacResponse = new WsExcludeListResponse();
            for (Object o: lbxRecords.getSelectedItems()) {
                Listitem item = (Listitem) o;
                Person applicant = (Person) item.getValue();
                if (applicant.getPersonTypeId().getIndNaturalPerson() == false) { //Solicitante Jurídico
                    if(applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEBUAP.getPersonClassificationCode())){
                        if (applicant.getLegalPerson().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){
                            affiliationRequest = applicant.getLegalPerson().getPersonId().getAffiliationRequest();
                            businessName = applicant.getLegalPerson().getBusinessName();
                        }
                    }
                    if (!applicant.getLegalPerson().getStatusApplicantId().getId().equals(Constants.STATUS_APPLICANT_BLACK_LIST) || !applicant.getLegalPerson().getStatusApplicantId().getId().equals(Constants.STATUS_APPLICANT_BLACK_LIST_OK)) {
	                    ofacResponse = ofac.queryOFACLegalPersonList(loginResponse.getToken(), businessName, ofacPercentege);
	                    saveReviewOfac(applicant, ofacResponse, affiliationRequest);
                    } 
                } else { //Solicitante Natural (negocio o representante legal)                    
                    if (applicant.getLegalRepresentative() != null && applicant.getLegalRepresentative().getPersonId().getPersonClassificationId().getCode().equals(PersonClassificationE.LEGREP.getPersonClassificationCode())){
                        if (getLegalPersonParam(applicant.getLegalRepresentative()) != null) {
                            legalPerson = legalPersonParam;
                            if (applicant.getLegalRepresentative().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())) {
                                affiliationRequest = legalPerson.getLegalRepresentativeId().getPersonId().getAffiliationRequest();
                                lastName = legalPerson.getLegalRepresentativeId().getLastNames();
                                firstName = legalPerson.getLegalRepresentativeId().getFirstNames();
                                ofacResponse = ofac.queryOFACList(loginResponse.getToken(),lastName, firstName, null, null, null, null, ofacPercentege);
                                saveReviewOfac(applicant, ofacResponse, affiliationRequest);
                            }   
                        }                                             
                    } 
                    if ( applicant.getNaturalPerson()!=null && applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.NABUAP.getPersonClassificationCode())){
                        if (applicant.getNaturalPerson().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){    
                            affiliationRequest = applicant.getNaturalPerson().getPersonId().getAffiliationRequest();
                            lastName = applicant.getNaturalPerson().getLastName();
                            firstName = applicant.getNaturalPerson().getFirstName();
                            ofacResponse = ofac.queryOFACList(loginResponse.getToken(),lastName, firstName, null, null, null, null, ofacPercentege);
                            saveReviewOfac(applicant, ofacResponse, affiliationRequest);
                        }                        
                    }
                }

                //Actualizar el estatus del solicitante si tiene coincidencia con lista OFAC
                if (applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEBUAP.getPersonClassificationCode())){
                    if (applicant.getLegalPerson().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){
                        if (Double.parseDouble(ofacResponse.getPercentMatch()) >= 0.75) {
                            applicant.getLegalPerson().setStatusApplicantId(getStatusApplicant(applicant.getLegalPerson().getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST));
                            indBlackList = 1;
                        } else {
                            applicant.getLegalPerson().setStatusApplicantId(getStatusApplicant(legalPerson.getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                        }
                        legalPerson = personEJB.saveLegalPerson(applicant.getLegalPerson());
                        int indBlackListLegalPerson= 0;
                        Person person = personEJB.searchPersonByLegalPersonId(applicant.getLegalPerson().getId());
    	                if (person!=null && !person.getLegalRepresentative().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())) {
                            if (person.getLegalRepresentative().getStatusApplicantId().getCode().equals(StatusApplicantE.LISNEG.getStatusApplicantCode())) {
                                indBlackListLegalPerson = 1;
                            }		 
                            if (indBlackList == 0 && indBlackListLegalPerson ==0) {
                                affiliationRequest.setStatusRequestId(getStatusAffiliationRequest(affiliationRequest.getStatusRequestId(), Constants.STATUS_REQUEST_BLACK_LIST_OK));
                            } else {
                                affiliationRequest.setStatusRequestId(getStatusAffiliationRequest(affiliationRequest.getStatusRequestId(), Constants.STATUS_REQUEST_REJECTED_BLACK_LIST));
                            }
                            affiliationRequest = utilsEJB.saveAffiliationRequest(affiliationRequest);
    	                } 
                    } 
                } else if (applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.NABUAP.getPersonClassificationCode())){
                     if (applicant.getNaturalPerson().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){    
                        if (Double.parseDouble(ofacResponse.getPercentMatch()) >= 0.75) {
                            applicant.getNaturalPerson().setStatusApplicantId(getStatusApplicant(applicant.getNaturalPerson().getStatusApplicantId(),Constants.STATUS_APPLICANT_BLACK_LIST ));
                            indBlackList = 1;
                        } else {
                            applicant.getNaturalPerson().setStatusApplicantId(getStatusApplicant(applicant.getNaturalPerson().getStatusApplicantId() ,Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                        }
                        naturalPerson = personEJB.saveNaturalPerson(applicant.getNaturalPerson());       
                        if (indBlackList == 1) {
                            affiliationRequest.setStatusRequestId(getStatusAffiliationRequest(affiliationRequest.getStatusRequestId(), Constants.STATUS_REQUEST_BLACK_LIST_OK));
                        } else {
                            affiliationRequest.setStatusRequestId(getStatusAffiliationRequest(affiliationRequest.getStatusRequestId(), Constants.STATUS_REQUEST_REJECTED_BLACK_LIST));
                        }
                        affiliationRequest = utilsEJB.saveAffiliationRequest(affiliationRequest);
                     }
                }else if (applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEGREP.getPersonClassificationCode())){
                    if (applicant.getLegalRepresentative().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){
                        if (Double.parseDouble(ofacResponse.getPercentMatch()) >= 0.75) {
                            applicant.getLegalRepresentative().setStatusApplicantId(getStatusApplicant(applicant.getLegalRepresentative().getStatusApplicantId(),Constants.STATUS_APPLICANT_BLACK_LIST));
                            indBlackList = 1;
                        } else {
                            applicant.getLegalRepresentative().setStatusApplicantId(getStatusApplicant(applicant.getLegalRepresentative().getStatusApplicantId() ,Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                        }
                        legalRepresentative = personEJB.saveLegalRepresentative(applicant.getLegalRepresentative());                       
                    }
                    EJBRequest request = new EJBRequest();
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put(QueryConstants.PARAM_LEGAL_REPRESENTATIVE_ID , applicant.getLegalRepresentative().getId());
                    request.setParams(params);
                    List<LegalPerson> legalPersons = personEJB.getLegalPersonByLegalRepresentative(request);
                    int indBlackListLegalPerson= 0;
	                if (legalPersons !=null && !legalPersons.get(0).getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())) {
                            if (legalPersons.get(0).getStatusApplicantId().getCode().equals(StatusApplicantE.LISNEG.getStatusApplicantCode())) {
                                indBlackListLegalPerson = 1;
                            }		 
                            if (indBlackList == 0 && indBlackListLegalPerson ==0) {
                                affiliationRequest.setStatusRequestId(getStatusAffiliationRequest(affiliationRequest.getStatusRequestId(), Constants.STATUS_REQUEST_BLACK_LIST_OK));
                            } else {
                                affiliationRequest.setStatusRequestId(getStatusAffiliationRequest(affiliationRequest.getStatusRequestId(), Constants.STATUS_REQUEST_REJECTED_BLACK_LIST));
                            }
                            affiliationRequest = utilsEJB.saveAffiliationRequest(affiliationRequest);
	                }
	           }	
            }
            getData();
            loadList(personList);
            this.showMessage("wallet.msj.finishReviewOFAC", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public LegalPerson getLegalPersonParam(LegalRepresentative legalRepresentative) {
        legalPersonParam = null;
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_LEGAL_REPRESENTATIVE_ID, legalRepresentative.getId());
            request1.setParams(params);
            legalPerson = personEJB.getLegalPersonByLegalRepresentative(request1);
            for (LegalPerson r : legalPerson) {
                legalPersonParam = r;
            }
        } catch (Exception ex) {
        }
        return legalPersonParam;
    }

    public void saveReviewOfac(Person applicant, WsExcludeListResponse ofacResponse, AffiliationRequest affiliationRequest) {
        ReviewOfac reviewOFAC = new ReviewOfac();
        try {
            //Guardar el resultado de revisión en lista OFAC para cada solicitante
            reviewOFAC.setPersonId(applicant);
            reviewOFAC.setResultReview(Float.parseFloat(ofacResponse.getPercentMatch()));
            reviewOFAC.setAffiliationRequestId(affiliationRequest);
            reviewOFAC.setUserReviewId(currentUser);
            reviewOFAC.setCreateDate(new Timestamp(new Date().getTime()));

            reviewOFAC = utilsEJB.saveReviewOfac(reviewOFAC);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public StatusApplicant getStatusApplicant(StatusApplicant statusAplicant, int statusRequestId) {
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(statusRequestId);
            statusAplicant = personEJB.loadStatusApplicant(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusAplicant;
    }

    public StatusRequest getStatusAffiliationRequest(StatusRequest statusRequest, int statusRequestId) {
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(statusRequestId);
            statusRequest = utilsEJB.loadStatusRequest(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusRequest;
    }
    
    private void loadStatusApplicant() {
        try {
           cmbStatus.getItems().clear();
            EJBRequest request = new EJBRequest();
            List<StatusApplicant> status = personEJB.getStatusApplicant(request);
            Comboitem item = new Comboitem();
            item.setLabel(Labels.getLabel("wallet.common.all"));
            item.setParent(cmbStatus);
            cmbStatus.setSelectedItem(item);
            for (int i = 0; i < status.size(); i++) {
                item = new Comboitem();
                item.setValue(status.get(i));
                item.setLabel(status.get(i).getDescription());
                item.setParent(cmbStatus);
            }
        } catch (Exception ex) {
            this.showError(ex);
        }
    }
    
    @Override
    public List<Person> getFilteredList(String filter) {
        List<Person> persons = new ArrayList<Person>();
        try{
            if(filter != null && !filter.equals(" ")){
               EJBRequest request = new EJBRequest();
               Map<String, Object> params = new HashMap<String, Object>();
               if (cmbStatus.getSelectedItem() != null && cmbStatus.getSelectedIndex() != 0) {
                   params.put(QueryConstants.PARAM_STATUS_APPLICANT_ID, ((StatusApplicant) cmbStatus.getSelectedItem().getValue()).getId());
               }
               if(!txtNumber.getText().equals("")){
                 params.put(QueryConstants.PARAM_NUMBER_REQUEST, txtNumber.getText());  
               }
               request.setParams(params);
               persons = personEJB.searchBusinessApplicantByStatusApplicantAndNumber(request);
               
            } else {
                return personList;
            }
        } catch (Exception ex ){
            showError(ex);
        }
        return persons;
    }
}
