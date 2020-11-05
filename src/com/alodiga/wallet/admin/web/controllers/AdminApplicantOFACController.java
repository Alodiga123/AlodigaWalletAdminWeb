package com.alodiga.wallet.admin.web.controllers;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.NaturalPerson;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.ReviewOfac;
import com.alodiga.wallet.common.model.StatusApplicant;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.ws.remittance.services.WSOFACMethod;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
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
    private Combobox cmbStatusApplicant;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private NaturalPerson naturalPersonParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminApplicantOFAC;
    public String indGender = null;
    private Long optionMenu;
    private AffiliationRequest afilationRequest;
    private Person personParam;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        AdminBusinnessAffiliationRequestsNaturalController adminRequestN = new AdminBusinnessAffiliationRequestsNaturalController();
        AdminUsersAffiliationRequestsController adminRequestUser = new AdminUsersAffiliationRequestsController();
        if (adminRequestN.getBusinessAffiliationRequets() != null) {
            afilationRequest = adminRequestN.getBusinessAffiliationRequets();
        }
        
        if (adminRequestUser.getUserAffiliationRequets() != null){
            afilationRequest = adminRequestUser.getUserAffiliationRequets();
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

    private void loadFields(NaturalPerson applicant) {
        btnSave.setVisible(false);
        Float percentageMatchApplicant = 0.00F;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            AdminBusinnessAffiliationRequestsNaturalController adminRequestN = new AdminBusinnessAffiliationRequestsNaturalController();
            AdminUsersAffiliationRequestsController adminRequestUser = new AdminUsersAffiliationRequestsController();
        
            if (adminRequestN.getBusinessAffiliationRequets() != null) {
                afilationRequest = adminRequestN.getBusinessAffiliationRequets();
            }
            
            if (adminRequestUser.getUserAffiliationRequets() != null){
                afilationRequest = adminRequestUser.getUserAffiliationRequets();
            }
            
            lblRequestNumber.setValue(afilationRequest.getNumberRequest());
            lblRequestDate.setValue(simpleDateFormat.format(afilationRequest.getDateRequest()));
            lblStatusRequest.setValue(afilationRequest.getStatusRequestId().getDescription());
            
            StringBuilder builder = new StringBuilder(applicant.getFirstName());
            builder.append(" ");
            builder.append(applicant.getLastName());
            
            lblName.setValue(builder.toString());
            lblDocumentType.setValue(applicant.getDocumentsPersonTypeId().getDescription());
            lblIdentificationNumber.setValue(applicant.getIdentificationNumber());
            lblStatus.setValue(applicant.getStatusApplicantId().getDescription());
            percentageMatchApplicant = getReviewOFAC(applicant).getResultReview()*100;
            lblPercentageMatch.setValue(percentageMatchApplicant.toString());

        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    public ReviewOfac getReviewOFAC(NaturalPerson applicant) {
        ReviewOfac reviewOFAC = null;
        try {
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, applicant.getPersonId().getId());
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
                if(afilationRequest.getBusinessPersonId() != null){
                    loadFields(afilationRequest.getBusinessPersonId().getNaturalPerson());
                } else if(afilationRequest.getUserRegisterUnifiedId() != null){
                    loadFields(afilationRequest.getUserRegisterUnifiedId().getNaturalPerson());
                }
                break;
            case WebConstants.EVENT_VIEW:
                if(afilationRequest.getBusinessPersonId() != null){
                    loadFields(afilationRequest.getBusinessPersonId().getNaturalPerson());
                    blockFields();
                } else if(afilationRequest.getUserRegisterUnifiedId() != null){
                    loadFields(afilationRequest.getUserRegisterUnifiedId().getNaturalPerson());
                    blockFields();
                }

                break;
        }
    }
 
}
