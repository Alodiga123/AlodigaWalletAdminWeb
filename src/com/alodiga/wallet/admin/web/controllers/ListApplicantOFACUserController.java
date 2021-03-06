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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

public class ListApplicantOFACUserController extends GenericAbstractListController<Person> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Combobox cmbStatus;
    private Textbox txtNumber;
    private UserEJB userEJB = null;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<Person> personList = null;
    private List<NaturalPerson> naturalPersonList = null;
    private User currentUser;
    private Profile currentProfile;
    private AdminUsersAffiliationRequestsController naturalRequest;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
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
            adminPage = "adminAplicantOFACUser.zul";
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
        NumberFormat formatoPorcentaje = NumberFormat.getPercentInstance();
        String requestNumber = ""; 
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);
                for (Person aplicant : list) {
                    item = new Listitem();
                    item.setValue(aplicant);
                    if (aplicant.getPersonTypeId().getIndNaturalPerson() == true) {
                            item.appendChild(new Listcell(aplicant.getAffiliationRequest().getNumberRequest()));                           
                            StringBuilder applicantNameNatural = new StringBuilder(aplicant.getNaturalPerson().getFirstName());
                            applicantNameNatural.append(" ");
                            applicantNameNatural.append(aplicant.getNaturalPerson().getLastName());
                            item.appendChild(new Listcell(applicantNameNatural.toString()));
                            if (aplicant.getNaturalPerson().getIdentificationNumber() != null) {
                                item.appendChild(new Listcell(aplicant.getNaturalPerson().getIdentificationNumber()));
                            }                            
                            item.appendChild(new Listcell(aplicant.getNaturalPerson().getStatusApplicantId().getDescription()));
                            if(aplicant.getNaturalPerson().getPersonId().getReviewOfac() != null){
                                item.appendChild(new Listcell(formatoPorcentaje.format(aplicant.getNaturalPerson().getPersonId().getReviewOfac().getResultReview())));
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
        naturalPersonList = new ArrayList<NaturalPerson>();
        List<AffiliationRequest> affiliationRequest = new ArrayList<AffiliationRequest>();
        Long affiliationRequestId = 0L;
        try {
            request.setFirst(0);
            request.setLimit(null);
            personList = personEJB.getPersonRegisterUnified(request);
            for (Person p: personList) {
               EJBRequest request = new EJBRequest();
               Map<String, Object> params = new HashMap<String, Object>();
               params.put(QueryConstants.PARAM_USER_REGISTER_ID , p.getId());
               request.setParams(params);
               affiliationRequest = utilsEJB.getAffiliationRequestByPerson(request);
               for(AffiliationRequest af : affiliationRequest){
                    p.setAffiliationRequest(af);
                    personEJB.savePerson(p);
                    affiliationRequestId = af.getId();
               }
               if (p.getNaturalPerson() == null) {
                   request = new EJBRequest();
                   params = new HashMap<String, Object>();
                   params.put(QueryConstants.PARAM_PERSON_ID, p.getId());
                   request.setParams(params);
                   naturalPersonList = personEJB.getNaturalPersonByPerson(request);
                   for (NaturalPerson np: naturalPersonList) {
                       p.setNaturalPerson(np);
                   }
               }
               if (p.getReviewOfac() == null) {
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
            }
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            Logger.getLogger(ListApplicantOFACUserController.class.getName()).log(Level.SEVERE, null, ex);
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
        float ofacPercentege = 0.1F;
        NaturalPerson naturalPerson = new NaturalPerson();
        AffiliationRequest affiliationRequest = new AffiliationRequest();
        WSOFACMethodProxy ofac = new WSOFACMethodProxy();
        try {
            WsLoginResponse loginResponse = new WsLoginResponse();
            loginResponse = ofac.loginWS("alodiga", "d6f80e647631bb4522392aff53370502");
            WsExcludeListResponse ofacResponse = new WsExcludeListResponse();
            for (Object o: lbxRecords.getSelectedItems()) {
                Listitem item = (Listitem) o;
                Person applicant = (Person) item.getValue();
                if (applicant.getPersonTypeId().getIndNaturalPerson() == true) {
                    if (applicant.getNaturalPerson().getStatusApplicantId().getCode().equals(StatusApplicantE.ACTIVO.getStatusApplicantCode())){
                      affiliationRequest = applicant.getAffiliationRequest();
                      naturalPerson = applicant.getNaturalPerson();
                      lastName = applicant.getNaturalPerson().getLastName();
                      firstName = applicant.getNaturalPerson().getFirstName();  
                    }
                } 
                if (!naturalPerson.getStatusApplicantId().getId().equals(Constants.STATUS_APPLICANT_BLACK_LIST) || !naturalPerson.getStatusApplicantId().getId().equals(Constants.STATUS_APPLICANT_BLACK_LIST_OK)) {               	
                    if (lastName != null && firstName != null) {
                        ofacResponse = ofac.queryOFACList(loginResponse.getToken(), lastName, firstName, null, null, null, null, ofacPercentege);

                        //Se guarda el registro de la revision OFAC
                        saveReviewOfac(applicant, ofacResponse, affiliationRequest);

                        //Actualizar el estatus del solicitante si tiene coincidencia con lista OFAC
                        if (applicant.getPersonTypeId().getIndNaturalPerson() == true) {
                            if (Double.parseDouble(ofacResponse.getPercentMatch()) <= 0.75) {
                                naturalPerson.setStatusApplicantId(getStatusApplicant(applicant.getNaturalPerson().getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                            } else {
                                naturalPerson.setStatusApplicantId(getStatusApplicant(applicant.getNaturalPerson().getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST));
                                indBlackList = 1;
                            }
                            naturalPerson = personEJB.saveNaturalPerson(naturalPerson);
                        } 
                    }
                    //Si algunos solicitante(s) coincide(n) con la Lista OFAC se actualiza estatus de la solicitud
                    if (ofacResponse != null) {
                        if (indBlackList == 1) {
                            affiliationRequest.setStatusRequestId(getStatusAffiliationRequest(affiliationRequest.getStatusRequestId(), Constants.STATUS_REQUEST_REJECTED_BLACK_LIST));
                        } else {
                            affiliationRequest.setStatusRequestId(getStatusAffiliationRequest(affiliationRequest.getStatusRequestId(), Constants.STATUS_REQUEST_BLACK_LIST_OK));
                        }
                        affiliationRequest = utilsEJB.saveAffiliationRequest(affiliationRequest);

                        getData();
                        loadList(personList);
	                    this.showMessage("wallet.msj.finishReviewOFAC", false, null);
	                }
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
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
               persons = personEJB.searchRegisterUnifiedByStatusApplicantAndNumber(request);
               
            } else {
                return personList;
            }
        } catch (Exception ex ){
            showError(ex);
        }
        return persons;
    }
    
   
}
