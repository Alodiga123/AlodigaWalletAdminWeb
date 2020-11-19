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
    }

    @Override
    public void checkPermissions() {
        try {
//            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_APLICANT_OFAC));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_APLICANT_OFAC);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_APLICANT_OFAC);
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
                                item.appendChild(new Listcell(aplicant.getAffiliationRequest().getNumberRequest()));
                                applicantNameLegal = aplicant.getLegalPerson().getBusinessName();
                                item.appendChild(new Listcell(applicantNameLegal));
                                item.appendChild(new Listcell(aplicant.getLegalPerson().getIdentificationNumber()));
                                item.appendChild(new Listcell(Labels.getLabel("sp.common.legalApplicant")));
                                item.appendChild(new Listcell(aplicant.getLegalPerson().getStatusApplicantId().getDescription()));
                                if(aplicant.getLegalPerson().getPersonId().getReviewOfac() != null){
                                    item.appendChild(new Listcell(formatoPorcentaje.format(aplicant.getLegalPerson().getPersonId().getReviewOfac().getResultReview())));
                                } else {
                                    item.appendChild(new Listcell(""));
                                }
                                item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                                item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());  
                        } else if (aplicant.getPersonClassificationId().getCode().equals(PersonClassificationE.NABUAP.getPersonClassificationCode())){
                                item.appendChild(new Listcell(aplicant.getAffiliationRequest().getNumberRequest()));
                                StringBuilder applicarBusinessNatural = new StringBuilder(aplicant.getNaturalPerson().getFirstName());
                                applicarBusinessNatural.append(" ");
                                applicarBusinessNatural.append(aplicant.getNaturalPerson().getLastName());
                                item.appendChild(new Listcell(applicarBusinessNatural.toString()));
                                item.appendChild(new Listcell(aplicant.getNaturalPerson().getIdentificationNumber()));
                                item.appendChild(new Listcell(Labels.getLabel("sp.common.naturalApplicant")));
                                item.appendChild(new Listcell(aplicant.getNaturalPerson().getStatusApplicantId().getDescription()));
                                if(aplicant.getNaturalPerson().getPersonId().getReviewOfac() != null){
                                    item.appendChild(new Listcell(formatoPorcentaje.format(aplicant.getNaturalPerson().getPersonId().getReviewOfac().getResultReview())));
                                } else {
                                    item.appendChild(new Listcell(""));
                                }
                                item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                                item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());
                        } else if (aplicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEGREP.getPersonClassificationCode())){
                                item.appendChild(new Listcell(aplicant.getAffiliationRequest().getNumberRequest()));
                                StringBuilder applicantRepresentative = new StringBuilder(aplicant.getLegalRepresentative().getFirstNames());
                                applicantRepresentative.append(" ");
                                applicantRepresentative.append(aplicant.getLegalRepresentative().getLastNames());
                                item.appendChild(new Listcell(applicantRepresentative.toString()));
                                item.appendChild(new Listcell(aplicant.getLegalRepresentative().getIdentificationNumber()));
                                item.appendChild(new Listcell(Labels.getLabel("sp.common.legalRepresentative")));
                                item.appendChild(new Listcell(aplicant.getLegalRepresentative().getStatusApplicantId().getDescription()));
                                if(aplicant.getLegalRepresentative().getPersonId().getReviewOfac() != null){
                                    item.appendChild(new Listcell(formatoPorcentaje.format(aplicant.getLegalRepresentative().getPersonId().getReviewOfac().getResultReview())));
                                } else {
                                    item.appendChild(new Listcell(""));
                                }
                                item.appendChild(new Listcell(aplicant.getLegalRepresentative().getStatusApplicantId().getDescription()));
                                item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                                item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());
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

    public void getData() {
        personList = new ArrayList<Person>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            personList = personEJB.getPersonBusinessApplicant(request);
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
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.businessAffiliationRequests.ofac.list"));
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
        AffiliationRequest affiliatinRequest = new AffiliationRequest();
        WSOFACMethodProxy ofac = new WSOFACMethodProxy();
        try {
            WsLoginResponse loginResponse = new WsLoginResponse();
            loginResponse = ofac.loginWS("alodiga", "d6f80e647631bb4522392aff53370502");
            WsExcludeListResponse ofacResponse = new WsExcludeListResponse();
            for (Person applicant : personList) {
                 if (applicant.getPersonTypeId().getIndNaturalPerson() == false) {
                   if(applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEBUAP.getPersonClassificationCode())){
                            if (applicant.getLegalPerson().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){
                                
                                affiliatinRequest = applicant.getLegalPerson().getPersonId().getAffiliationRequest();
                                businessName = applicant.getLegalPerson().getBusinessName();
                            }
                    } else if (applicant.getLegalRepresentative().getPersonId().getPersonClassificationId().getCode().equals(PersonClassificationE.LEGREP.getPersonClassificationCode())){
                            if (applicant.getLegalRepresentative().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){
                                if (getLegalPersonParam(applicant.getLegalRepresentative()) != null) {
                                legalPerson = legalPersonParam;
                                }
                                affiliatinRequest = legalPerson.getLegalRepresentativeId().getPersonId().getAffiliationRequest();
                                lastName = legalPerson.getLegalRepresentativeId().getLastNames();
                                firstName = legalPerson.getLegalRepresentativeId().getFirstNames();
                            }
                    } 
                         
                 } else {
                     if (applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.NABUAP.getPersonClassificationCode())){
                        if (applicant.getNaturalPerson().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){    
                            affiliatinRequest = applicant.getNaturalPerson().getPersonId().getAffiliationRequest();
                            lastName = applicant.getNaturalPerson().getLastName();
                            firstName = applicant.getNaturalPerson().getFirstName();
                        }            
                     }
                 }
                
                if (applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEBUAP.getPersonClassificationCode())){
                    if( businessName != null && !businessName.equals("")){
                       ofacResponse = ofac.queryOFACLegalPersonList(loginResponse.getToken(), businessName, ofacPercentege); 
                       //Se guarda el registro de la revision OFAC
                       saveReviewOfac(applicant, ofacResponse, affiliatinRequest);
                    }
                }  else {
                    if (!lastName.equals("") && !firstName.equals("") && lastName != null && firstName != null) {
                        ofacResponse = ofac.queryOFACList(loginResponse.getToken(),lastName, firstName, null, null, null, null, ofacPercentege);
                        //Se guarda el registro de la revision OFAC
                        saveReviewOfac(applicant, ofacResponse, affiliatinRequest);
                    }
                }
                
                
                //Actualizar el estatus del solicitante si tiene coincidencia con lista OFAC
                if (applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEBUAP.getPersonClassificationCode())){
                    if (applicant.getLegalPerson().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){
                        if (Double.parseDouble(ofacResponse.getPercentMatch()) <= 0.75) {
                            legalPerson.setStatusApplicantId(getStatusApplicant(legalPerson.getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST));
                            indBlackList = 1;
                        } else {
                            legalPerson.setStatusApplicantId(getStatusApplicant(legalPerson.getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                        }
                        legalPerson = personEJB.saveLegalPerson(legalPerson);
                        
                            //Si algunos solicitante(s) coincide(n) con la Lista OFAC se actualiza estatus de la solicitud
                        if (ofacResponse != null) {
                            if (indBlackList == 1) {
                                affiliatinRequest.setStatusRequestId(getStatusAffiliationRequest(affiliatinRequest.getStatusRequestId(), Constants.STATUS_REQUEST_PENDING));
                            } else {
                                affiliatinRequest.setStatusRequestId(getStatusAffiliationRequest(affiliatinRequest.getStatusRequestId(), Constants.STATUS_REQUEST_BLACK_LIST_OK));
                            }
                            affiliatinRequest = utilsEJB.saveAffiliationRequest(affiliatinRequest);

                            getData();
                            loadList(personList);
                            this.showMessage("sp.common.finishReviewOFAC", false, null);
                        } 
                    } 
                }
                
                if (applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.NABUAP.getPersonClassificationCode())){
                     if (applicant.getNaturalPerson().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){    
                        if (Double.parseDouble(ofacResponse.getPercentMatch()) <= 0.75) {
                            naturalPerson.setStatusApplicantId(getStatusApplicant(applicant.getNaturalPerson().getStatusApplicantId(),Constants.STATUS_APPLICANT_BLACK_LIST ));
                            indBlackList = 1;
                        } else {
                            naturalPerson.setStatusApplicantId(getStatusApplicant(applicant.getNaturalPerson().getStatusApplicantId() ,Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                        }
                        naturalPerson = personEJB.saveNaturalPerson(naturalPerson);
                        
                            //Si algunos solicitante(s) coincide(n) con la Lista OFAC se actualiza estatus de la solicitud
                        if (ofacResponse != null) {
                            if (indBlackList == 1) {
                                affiliatinRequest.setStatusRequestId(getStatusAffiliationRequest(affiliatinRequest.getStatusRequestId(), Constants.STATUS_REQUEST_PENDING));
                            } else {
                                affiliatinRequest.setStatusRequestId(getStatusAffiliationRequest(affiliatinRequest.getStatusRequestId(), Constants.STATUS_REQUEST_BLACK_LIST_OK));
                            }
                            affiliatinRequest = utilsEJB.saveAffiliationRequest(affiliatinRequest);

                            getData();
                            loadList(personList);
                            this.showMessage("sp.common.finishReviewOFAC", false, null);
                        } 
                     }
                }
                if (applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEGREP.getPersonClassificationCode())){
                    if (applicant.getLegalRepresentative().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){
                        if (Double.parseDouble(ofacResponse.getPercentMatch()) <= 0.75) {
                            legalRepresentative.setStatusApplicantId(getStatusApplicant(applicant.getLegalRepresentative().getStatusApplicantId(),Constants.STATUS_APPLICANT_BLACK_LIST ));
                            indBlackList = 1;
                        } else {
                            legalRepresentative.setStatusApplicantId(getStatusApplicant(applicant.getLegalRepresentative().getStatusApplicantId() ,Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                        }
                        legalRepresentative = personEJB.saveLegalRepresentative(legalRepresentative);
                        
                            //Si algunos solicitante(s) coincide(n) con la Lista OFAC se actualiza estatus de la solicitud
                        if (ofacResponse != null) {
                            if (indBlackList == 1) {
                                affiliatinRequest.setStatusRequestId(getStatusAffiliationRequest(affiliatinRequest.getStatusRequestId(), Constants.STATUS_REQUEST_PENDING));
                            } else {
                                affiliatinRequest.setStatusRequestId(getStatusAffiliationRequest(affiliatinRequest.getStatusRequestId(), Constants.STATUS_REQUEST_BLACK_LIST_OK));
                            }
                            affiliatinRequest = utilsEJB.saveAffiliationRequest(affiliatinRequest);

                            getData();
                            loadList(personList);
                            this.showMessage("sp.common.finishReviewOFAC", false, null);
                        } 
                    }
                }
                
                
                    
                
                
            }
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
            item.setLabel(Labels.getLabel("sp.common.all"));
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
