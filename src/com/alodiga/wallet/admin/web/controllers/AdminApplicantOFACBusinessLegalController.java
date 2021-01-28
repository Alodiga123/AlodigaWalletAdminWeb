package com.alodiga.wallet.admin.web.controllers;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.enumeraciones.PersonClassificationE;
import com.alodiga.wallet.common.enumeraciones.StatusApplicantE;
import com.alodiga.wallet.common.enumeraciones.StatusRequestE;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.LegalPerson;
import com.alodiga.wallet.common.model.LegalRepresentative;
import com.alodiga.wallet.common.model.NaturalPerson;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.ReviewOfac;
import com.alodiga.wallet.common.model.StatusApplicant;
import com.alodiga.wallet.common.model.StatusRequest;
import com.alodiga.wallet.common.utils.Constants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class AdminApplicantOFACBusinessLegalController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private Label lblName;
    private Label lblDocumentType;
    private Label lblIdentificationNumber;
    private Label lblPercentageMatch;
    private Combobox cmbStatusApplicant;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private NaturalPerson naturalPersonParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminApplicantOFACBusinessLegal;
    public String indGender = null;
    private Long optionMenu;
    private AffiliationRequest afilationRequest;
    private Person personParam;
    private Person applicantParam;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                applicantParam = (Person) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                applicantParam = (Person) Sessions.getCurrent().getAttribute("object");
                break;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
    }

    private void loadFields(Person applicant) {
        Float percentageMatchApplicant = 0.00F;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            AdminBusinnessAffiliationRequestsLegalController adminRequestN = new AdminBusinnessAffiliationRequestsLegalController();
        
            if (adminRequestN.getBusinessAffiliationRequets().getBusinessPersonId().getLegalPerson() != null) {
                afilationRequest = adminRequestN.getBusinessAffiliationRequets();
            }
            
            lblRequestNumber.setValue(afilationRequest.getNumberRequest());
            lblRequestDate.setValue(simpleDateFormat.format(afilationRequest.getDateRequest()));
            lblStatusRequest.setValue(afilationRequest.getStatusRequestId().getDescription());
            
            if(applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEBUAP.getPersonClassificationCode())){
                lblName.setValue(applicant.getLegalPerson().getBusinessName()); 
                lblDocumentType.setValue(applicant.getLegalPerson().getDocumentsPersonTypeId().getDescription());
                lblIdentificationNumber.setValue(applicant.getLegalPerson().getIdentificationNumber());
                percentageMatchApplicant = getReviewOFAC(applicant.getLegalPerson().getPersonId()).getResultReview()*100;
                lblPercentageMatch.setValue(percentageMatchApplicant.toString());
            } else if(applicant.getPersonClassificationId().getCode().equals(PersonClassificationE.LEGREP.getPersonClassificationCode())) {
                StringBuilder builder = new StringBuilder(applicant.getLegalRepresentative().getFirstNames());
                builder.append(" ");
                builder.append(applicant.getLegalRepresentative().getLastNames());
                lblName.setValue(builder.toString());
                lblDocumentType.setValue(applicant.getLegalRepresentative().getDocumentsPersonTypeId().getDescription());
                lblIdentificationNumber.setValue(applicant.getLegalRepresentative().getIdentificationNumber());
                percentageMatchApplicant = getReviewOFAC(applicant.getLegalRepresentative().getPersonId()).getResultReview()*100;
                lblPercentageMatch.setValue(percentageMatchApplicant.toString());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    public ReviewOfac getReviewOFAC(Person applicant) {
        ReviewOfac reviewOFAC = null;
        try {
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, applicant.getId());
            params.put(Constants.AFFILIATION_REQUEST_KEY, afilationRequest.getId());
            request.setParams(params);
            List<ReviewOfac> reviewOFACList = utilsEJB.getReviewOfacByRequest(request);
            for (ReviewOfac r: reviewOFACList) {
                reviewOFAC = r;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return reviewOFAC;
    }

    private void saveStatus(AffiliationRequest _affiliation){
       AffiliationRequest affiliation = null;
       Person person = null;
       LegalPerson legalPerson = null;
       LegalRepresentative legalRepresentative = null;
       StatusRequest statusRequest = null;
       try {
            if (_affiliation != null) {
                affiliation  = _affiliation;
            } else {
                affiliation  = new AffiliationRequest();
            }
            StatusApplicant status = (StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue();
                
            
            if (afilationRequest.getBusinessPersonId().getLegalPerson() != null) {
                //Modificar el Estatus del solicitante
                legalPerson = afilationRequest.getBusinessPersonId().getLegalPerson();
                legalPerson.setUpdateDate(new Timestamp(new Date().getTime()));
                legalPerson.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
                legalPerson = personEJB.saveLegalPerson(legalPerson);
                
                if(status.getCode().equals(StatusApplicantE.LISNOK.getStatusApplicantCode())){
                    // Obtener el estatus de la solicitud 
                    EJBRequest request = new EJBRequest();
                    request.setParam(StatusRequestE.APLINE.getId());
                    statusRequest = utilsEJB.loadStatusRequest(request);
                    
                    // Modificar el Estatus de la solicitud 
                    affiliation.setStatusRequestId(statusRequest);
                    affiliation.setUpdateDate(new Timestamp(new Date().getTime()));
                    affiliation = utilsEJB.saveAffiliationRequest(affiliation);
                
                } else if(status.getCode().equals(StatusApplicantE.LISNEG.getStatusApplicantCode())){
                    // Obtener el estatus de la solicitud 
                    EJBRequest request = new EJBRequest();
                    request.setParam(StatusRequestE.RELINE.getId());
                    statusRequest = utilsEJB.loadStatusRequest(request);
                    
                    // Modificar el Estatus de la solicitud 
                    affiliation.setStatusRequestId(statusRequest);
                    affiliation.setUpdateDate(new Timestamp(new Date().getTime()));
                    affiliation = utilsEJB.saveAffiliationRequest(affiliation);
                }
            } else if(afilationRequest.getBusinessPersonId().getLegalRepresentative() != null){
                //Modificar el Estatus del solicitante
                legalRepresentative = afilationRequest.getBusinessPersonId().getLegalRepresentative();
                legalRepresentative.setUpdateDate(new Timestamp(new Date().getTime()));
                legalRepresentative.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
                legalRepresentative = personEJB.saveLegalRepresentative(legalRepresentative);
                
                if(status.getCode().equals(StatusApplicantE.LISNOK.getStatusApplicantCode())){
                    // Obtener el estatus de la solicitud 
                    EJBRequest request = new EJBRequest();
                    request.setParam(StatusRequestE.APLINE.getId());
                    statusRequest = utilsEJB.loadStatusRequest(request);
                    
                    // Modificar el Estatus de la solicitud 
                    affiliation.setStatusRequestId(statusRequest);
                    affiliation.setUpdateDate(new Timestamp(new Date().getTime()));
                    affiliation = utilsEJB.saveAffiliationRequest(affiliation);
                
                } else if(status.getCode().equals(StatusApplicantE.LISNEG.getStatusApplicantCode())){
                    // Obtener el estatus de la solicitud 
                    EJBRequest request = new EJBRequest();
                    request.setParam(StatusRequestE.RELINE.getId());
                    statusRequest = utilsEJB.loadStatusRequest(request);
                    
                    // Modificar el Estatus de la solicitud 
                    affiliation.setStatusRequestId(statusRequest);
                    affiliation.setUpdateDate(new Timestamp(new Date().getTime()));
                    affiliation = utilsEJB.saveAffiliationRequest(affiliation);
                }
            }
            this.showMessage("wallet.msj.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }    
    }    
    
    public void blockFields() {
    }
    
    public Boolean validateEmpty(){
        return true;
    }
    
    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveStatus(afilationRequest);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveStatus(afilationRequest);
                    break;
                default:
                    break;
            }
        }
    }

    public void onClick$btnBack() {
        winAdminApplicantOFACBusinessLegal.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                    loadFields(applicantParam);
                    loadCmbStatusApplicant(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                    loadFields(applicantParam);
                    blockFields();
                    loadCmbStatusApplicant(eventType);
                break;
        }
    }
    
    private void loadCmbStatusApplicant(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<StatusApplicant> statusApplicants;
        try {
            statusApplicants = personEJB.getStatusApplicant(request1);
            if(afilationRequest.getBusinessPersonId() != null){
                if(afilationRequest.getBusinessPersonId().getLegalPerson() != null){
                    loadGenericCombobox(statusApplicants, cmbStatusApplicant, "description", evenInteger, Long.valueOf(afilationRequest != null ? afilationRequest.getBusinessPersonId().getLegalPerson().getStatusApplicantId().getId() : 0));
                } 
            } 
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }
 
}
