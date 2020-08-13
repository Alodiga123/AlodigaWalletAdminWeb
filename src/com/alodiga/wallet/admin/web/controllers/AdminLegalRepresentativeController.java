package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.BusinessAffiliationRequest;
import com.alodiga.wallet.common.model.LegalRepresentative;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.PhonePerson;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;

public class AdminLegalRepresentativeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private Label lblCountryName;
    private Label lblIdentificationType;
    private Label lblIdentificationNumber;
    private Label lblExpirationDater;
    private Label lblIdentificationNumberOld;
    private Label lblFullName;
    private Label lblFullLastName;
    private Label lblBirthPlace;
    private Label lblBirthday;
    private Label lblAge;
    private Label lblGender;
    private Label lblCivilState;
    private Label lblPhoneType;
    private Label lblPhoneNumber;
    private Label lblEmail;
    private PersonEJB personEJB = null;
    private Button btnSave;
    private List<PhonePerson> phonePersonList = null;
    private BusinessAffiliationRequest businessAffiliationRequestParam;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            businessAffiliationRequestParam = null;
        } else {
            businessAffiliationRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BusinessAffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
//        initView(eventType, "crud.businessAffiliationRequets");
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

    public Integer getEventType() {
        return this.eventType;
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
    
    
    private void loadFieldRequest(Person person) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        LegalRepresentative legalRepresentative = new LegalRepresentative();
        legalRepresentative = person.getLegalPerson().getLegalRepresentativeId();
        try {
            lblCountryName.setValue(person.getCountryId().getName());
            lblIdentificationType.setValue(legalRepresentative.getDocumentsPersonTypeId().getDescription());
            lblIdentificationNumber.setValue(legalRepresentative.getIdentificationNumber());
            lblExpirationDater.setValue(simpleDateFormat.format(legalRepresentative.getDueDateDocumentIdentification()));
            lblIdentificationNumberOld.setValue(legalRepresentative.getIdentificationNumberOld());
            lblFullName.setValue(legalRepresentative.getFirstNames());
            lblFullLastName.setValue(legalRepresentative.getLastNames());
            lblBirthPlace.setValue(legalRepresentative.getPlaceBirth());
            lblBirthday.setValue(simpleDateFormat.format(legalRepresentative.getDateBirth()));
            lblAge.setValue(String.valueOf(legalRepresentative.getAge()));
            lblGender.setValue(legalRepresentative.getGender());
            lblCivilState.setValue(legalRepresentative.getCivilStatusId().getDescription());            
            lblEmail.setValue(person.getEmail());
            
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, legalRepresentative.getPersonId().getId());
            request.setParams(params);
            phonePersonList = personEJB.getPhoneByPerson(request);
            if (phonePersonList != null) {
                for (PhonePerson p : phonePersonList) {
                    if (p.getPhoneTypeId().getId() == Constants.PHONE_TYPE_ROOM) {
                        lblPhoneType.setValue(p.getPhoneTypeId().getDescription());
                        lblPhoneNumber.setValue(p.getCountryCode() + " " + p.getAreaCode() + " " + p.getNumberPhone());
                    }
                }
            }
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                break;
            case WebConstants.EVENT_EDIT:
                break;
            default:
                break;
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(businessAffiliationRequestParam);
                loadFieldRequest(businessAffiliationRequestParam.getBusinessPersonId());
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(businessAffiliationRequestParam);
                loadFieldRequest(businessAffiliationRequestParam.getBusinessPersonId());
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
