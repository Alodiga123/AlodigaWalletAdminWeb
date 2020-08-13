package com.alodiga.wallet.admin.web.controllers;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.BusinessAffiliationRequest;
import com.alodiga.wallet.common.model.NaturalPerson;
import com.alodiga.wallet.common.utils.Constants;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class AdminApplicantOFACController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Label lblName;
    private Label lblDocumentType;
    private Label lblIdentificationNumber;
    private Label lblPercentageMatch;
    private PersonEJB personEJB = null;
    private NaturalPerson naturalPersonParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminApplicantOFAC;
    public String indGender = null;
    private Long optionMenu;
    private BusinessAffiliationRequest afilationRequest;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            naturalPersonParam = null;
        } else {
            naturalPersonParam = (NaturalPerson) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
    }

    private void loadFields(NaturalPerson applicant) {
//        ReviewOFAC reviewOFAC = null;
        Float percentageMatchApplicant = 0.00F;
        try {
            AdminBusinnessAffiliationRequestsNaturalController adminRequestN = new AdminBusinnessAffiliationRequestsNaturalController();
            if (adminRequestN.getBusinessAffiliationRequets() != null) {
                afilationRequest = adminRequestN.getBusinessAffiliationRequets();
            }

            StringBuilder builder = new StringBuilder(applicant.getFirstName());
            builder.append(" ");
            builder.append(applicant.getLastName());
            lblName.setValue(builder.toString());
            lblDocumentType.setValue(applicant.getDocumentsPersonTypeId().getDescription());
            lblIdentificationNumber.setValue(applicant.getIdentificationNumber());
//            EJBRequest request = new EJBRequest();
//            Map params = new HashMap();
//            params.put(Constants.PERSON_KEY, applicant.getPersonId().getId());
//            params.put(Constants.BUSINESS_AFFILIATION_REQUEST_KEY, afilationRequest.getId());
//            request.setParams(params);
//            List<ReviewOFAC> reviewOFACList = requestEJB.getReviewOFACByApplicantByRequest(request);
//            for (ReviewOFAC r : reviewOFACList) {
//                reviewOFAC = r;
//            }
//            percentageMatchApplicant = Float.parseFloat(reviewOFAC.getResultReview()) * 100;
            lblPercentageMatch.setValue(percentageMatchApplicant.toString());
            
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
    }

//    private void saveApplicantOFAC(ApplicantNaturalPerson _applicantNaturalPerson) {
//        try {
//            ApplicantNaturalPerson applicantNaturalPerson = _applicantNaturalPerson;
//
//            //Guarda el cambio de status en el solicitante
//            applicantNaturalPerson.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
//            applicantNaturalPerson = personEJB.saveApplicantNaturalPerson(applicantNaturalPerson);
//            this.showMessage("sp.common.save.success", false, null);
//            EventQueues.lookup("updateApplicantOFAC", EventQueues.APPLICATION, true).publish(new Event(""));
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }

    public void onClick$btnSave() {
//        switch (eventType) {
//            case WebConstants.EVENT_EDIT:
//                saveApplicantOFAC(applicantNaturalPersonParam);
//                break;
//            default:
//                break;
//        }
    }

    public void onClick$btnBack() {
        winAdminApplicantOFAC.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(naturalPersonParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(naturalPersonParam);
                blockFields();
                break;
        }
    }

//    private void loadCmbStatusApplicant(Integer evenInteger) {
//        EJBRequest request1 = new EJBRequest();
//        List<StatusApplicant> statusApplicantList;
//
//        try {
//            statusApplicantList = requestEJB.getStatusApplicant(request1);
//            loadGenericCombobox(statusApplicantList, cmbStatusApplicant, "description", evenInteger, Long.valueOf(applicantNaturalPersonParam != null ? applicantNaturalPersonParam.getStatusApplicantId().getId() : 0));
//        } catch (EmptyListException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        } catch (GeneralException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        } catch (NullParameterException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        }
//    }
}
