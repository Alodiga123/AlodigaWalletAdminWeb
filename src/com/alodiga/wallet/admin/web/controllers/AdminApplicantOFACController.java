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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class AdminApplicantOFACController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private Label lblName;
    private Label lblDocumentType;
    private Label lblIdentificationNumber;
    private Label lblPercentageMatch;
    private Label lblStatus;
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
        AdminBusinnessAffiliationRequestsNaturalController adminRequestN = new AdminBusinnessAffiliationRequestsNaturalController();
        if (adminRequestN.getBusinessAffiliationRequets() != null) {
            afilationRequest = adminRequestN.getBusinessAffiliationRequets();
        }
//        if (eventType == WebConstants.EVENT_ADD) {
//            businessAffiliationRequestParam = null;
//        } else {
//            businessAffiliationRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BusinessAffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
//        }
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

    private void loadFields(BusinessAffiliationRequest businessAffiliationRequest) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            lblRequestNumber.setValue(businessAffiliationRequest.getNumberRequest());
            lblRequestDate.setValue(simpleDateFormat.format(businessAffiliationRequest.getDateRequest()));
            lblStatusRequest.setValue(businessAffiliationRequest.getStatusBusinessAffiliationRequestId().getDescription());

            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
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
            lblStatus.setValue(applicant.getStatusApplicantId().getDescription());

            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
    }

    public void onClick$btnSave() {

    }

    public void onClick$btnBack() {
        winAdminApplicantOFAC.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(afilationRequest.getBusinessPersonId().getNaturalPerson());
                loadFields(afilationRequest);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(afilationRequest.getBusinessPersonId().getNaturalPerson());
                blockFields();
                break;
        }
    }
}
