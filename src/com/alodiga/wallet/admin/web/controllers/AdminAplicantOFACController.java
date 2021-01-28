package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.enumeraciones.StatusApplicantE;
import com.alodiga.wallet.common.enumeraciones.StatusRequestE;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.LegalPerson;
import com.alodiga.wallet.common.model.LegalRepresentative;
import com.alodiga.wallet.common.model.NaturalPerson;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.ReviewOfac;
import com.alodiga.wallet.common.model.StatusApplicant;
import com.alodiga.wallet.common.model.StatusRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminAplicantOFACController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblName;
    private Label lblDocumentType;
    private Label lblIdentificationNumber;
    private Label lblUser;
    private Textbox txtObservations;
    private Label lblPercentageMatch;
    private Combobox cmbStatusApplicant;
    private User user = null;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private ReviewOfac reviewOfacParam;
    private LegalPerson legalPersonParam;
    private AffiliationRequest affiliationRequestParam;
    private List<ReviewOfac> reviewOfac;
    private List<LegalPerson> legalPerson;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private Person personParam;
    private Integer eventType;
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            personParam = null;
        } else {
            personParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Person) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.aplicantOFAC");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.ofac.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.ofac.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.ofac.add"));
                break;
            default:
                break;
        }
        try {
            user = AccessControl.loadCurrentUser();
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public LegalPerson getLegalPersonParam() {
        legalPersonParam = null;
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_LEGAL_REPRESENTATIVE_ID, personParam.getLegalRepresentative().getId());
            request1.setParams(params);
            legalPerson = personEJB.getLegalPersonByLegalRepresentative(request1);
            for (LegalPerson r : legalPerson) {
                legalPersonParam = r;
            }
        } catch (Exception ex) {
        }
        return legalPersonParam;
    }

    public ReviewOfac getReviewOfacParam() {
        reviewOfacParam = null;
        try {
           
            affiliationRequestParam = personParam.getAffiliationRequest();
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_PERSON_ID, personParam.getId());
            params.put(QueryConstants.PARAM_AFFILIATION_REQUEST_ID, affiliationRequestParam.getId());
            request1.setParams(params);
            reviewOfac = utilsEJB.getReviewOfacByRequest(request1);
            for (ReviewOfac r : reviewOfac) {
                reviewOfacParam = r;
            }
            
        } catch (Exception ex) {
        }
        return reviewOfacParam;
    }

    public void clearFields() {
    }

    private void loadFieldP(Person person) {
        Float percentageMatchApplicant = 0.00F;
        try {
            if (person.getPersonTypeId().getIndNaturalPerson() == true) {
                lblName.setValue(person.getNaturalPerson().getFirstName() + " " + person.getNaturalPerson().getLastName());
                lblDocumentType.setValue(person.getNaturalPerson().getDocumentsPersonTypeId().getDescription());
                lblIdentificationNumber.setValue(person.getNaturalPerson().getIdentificationNumber());
            } else {
                if (person.getPersonClassificationId().getId() == 2) {
                    lblName.setValue(person.getLegalPerson().getBusinessName());
                    lblDocumentType.setValue(person.getLegalPerson().getDocumentsPersonTypeId().getDescription());
                    lblIdentificationNumber.setValue(person.getLegalPerson().getIdentificationNumber());
                } else if (person.getPersonClassificationId().getId() == 3) {
                    if (getLegalPersonParam() != null) {
                        affiliationRequestParam = legalPersonParam.getPersonId().getAffiliationRequest();
                    }
                    lblName.setValue(person.getLegalRepresentative().getFirstNames() + " " + person.getLegalRepresentative().getLastNames());
                    lblDocumentType.setValue(person.getLegalRepresentative().getDocumentsPersonTypeId().getDescription());
                    lblIdentificationNumber.setValue(person.getLegalRepresentative().getIdentificationNumber());
                } 
            }

            lblPercentageMatch.setValue(percentageMatchApplicant.toString());

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFieldU(User user) {
        lblUser.setValue(user.getFirstName() + " " + user.getLastName());
    }

    private void loadFieldR(ReviewOfac reviewOfac) {
        if (reviewOfac != null) {
            lblPercentageMatch.setValue(String.valueOf(reviewOfac.getResultReview()));
            lblUser.setValue(reviewOfac.getUserReviewId().getFirstName() + " " + reviewOfac.getUserReviewId().getLastName());
            txtObservations.setValue(reviewOfac.getObservations());
        }
    }

    public void blockFields() {
        cmbStatusApplicant.setDisabled(true);

        btnSave.setVisible(false);
    }

    private void saveReviewOfac(ReviewOfac _reviewOfac) {
        ReviewOfac reviewOfac = null;
        Person person = null;
        NaturalPerson naturalPerson = null;
        LegalPerson legalPerson = null;
        LegalRepresentative legalRepresentative = null;
        AffiliationRequest affiliationRequest = affiliationRequestParam;
        Integer statusRequestId = StatusRequestE.APLINE.getId();
        try {
            if (_reviewOfac != null) {
                reviewOfac = _reviewOfac;
            } else {//New country
                reviewOfac = new ReviewOfac();
            }

            person = personParam;

            reviewOfac.setPersonId(personParam);
            reviewOfac.setAffiliationRequestId(affiliationRequestParam);
            reviewOfac.setObservations(txtObservations.getText());
            reviewOfac.setUserReviewId(user);
            if (_reviewOfac != null) {
                reviewOfac.setUpdateDate(new Timestamp(new Date().getTime()));
            } else {
                reviewOfac.setCreateDate(new Timestamp(new Date().getTime()));
            }
            reviewOfac = utilsEJB.saveReviewOfac(reviewOfac);

            if (personParam.getPersonTypeId().getIndNaturalPerson() == true) {
                naturalPerson = personParam.getNaturalPerson();
                naturalPerson.setUpdateDate(new Timestamp(new Date().getTime()));
                naturalPerson.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
                naturalPerson = personEJB.saveNaturalPerson(naturalPerson);
            } else {
                if (personParam.getPersonClassificationId().getId() != 3) {
                    legalPerson = personParam.getLegalPerson();
                    legalPerson.setUpdateDate(new Timestamp(new Date().getTime()));
                    legalPerson.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
                    legalPerson = personEJB.saveLegalPerson(legalPerson);
                } else {
                    legalRepresentative = personParam.getLegalRepresentative();
                    legalRepresentative.setUpdateDate(new Timestamp(new Date().getTime()));
                    legalRepresentative.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
                    legalRepresentative = personEJB.saveLegalRepresentative(legalRepresentative);
                }
            }
            
            if(activateTabApplicationReview() == 0){
               affiliationRequest.setStatusRequestId(getStatusRequest(affiliationRequest, statusRequestId));
               affiliationRequest = utilsEJB.saveAffiliationRequest(affiliationRequest);
            }

            this.showMessage("wallet.msj.save.success", false, null);
            EventQueues.lookup("updateApplicantOFAC", EventQueues.APPLICATION, true).publish(new Event(""));

            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
            } else {
                btnSave.setVisible(true);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public int activateTabApplicationReview(){
        String statusApplicantBlackList = StatusApplicantE.LISNEG.getStatusApplicantCode();
        int indBlackList = 0;
        
        if(personParam.getNaturalPerson().getStatusApplicantId().getCode().equals(statusApplicantBlackList)){
            indBlackList = 1;
        }
        
        return indBlackList;
    }
    
    public StatusRequest getStatusRequest(AffiliationRequest affiliationRequest, int statusRequestId){
        StatusRequest statusRequest = affiliationRequest.getStatusRequestId();
        try{
            EJBRequest request = new EJBRequest();
            request.setParam(statusRequestId);
            statusRequest = utilsEJB.loadStatusRequest(request);
        } catch (Exception ex) {
            showError(ex);
        }
        
        return statusRequest;
    }
    
    public void onClick$btnCancel() {
        clearFields();
    }

    public Boolean validateEmpty() {
        if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("msj.error.observations", true, null);
        } else {
            return true;
        }
        return false;
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveReviewOfac(reviewOfacParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveReviewOfac(reviewOfacParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFieldP(personParam);
                loadFieldU(user);
                if (getReviewOfacParam() != null) {
                    loadFieldR(reviewOfacParam);
                }
                loadCmbStatusApplicant(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFieldP(personParam);
                loadFieldU(user);
                if (getReviewOfacParam() != null) {
                    loadFieldR(reviewOfacParam);
                }
                loadCmbStatusApplicant(eventType);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbStatusApplicant(eventType);
                blockFields();
                break;
            default:
                break;
        }
    }

    private void loadCmbStatusApplicant(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<StatusApplicant> statusApplicants;
        try {
            statusApplicants = personEJB.getStatusApplicant(request1);
            if (personParam.getPersonTypeId().getIndNaturalPerson() == true) {
                loadGenericCombobox(statusApplicants, cmbStatusApplicant, "description", evenInteger, Long.valueOf(personParam != null ? personParam.getNaturalPerson().getStatusApplicantId().getId() : 0));
            } else if (personParam.getPersonClassificationId().getId() == 2) {
                loadGenericCombobox(statusApplicants, cmbStatusApplicant, "description", evenInteger, Long.valueOf(personParam != null ? personParam.getLegalPerson().getStatusApplicantId().getId() : 0));
            } else if (personParam.getPersonClassificationId().getId() == 3) {
                loadGenericCombobox(statusApplicants, cmbStatusApplicant, "description", evenInteger, Long.valueOf(personParam != null ? personParam.getLegalRepresentative().getStatusApplicantId().getId() : 0));
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
