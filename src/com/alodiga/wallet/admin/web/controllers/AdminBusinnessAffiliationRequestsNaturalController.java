package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.PhonePerson;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Toolbarbutton;

public class AdminBusinnessAffiliationRequestsNaturalController extends GenericAbstractAdminController {

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
    private Label lblMarriedLastName;
    private Label lblBirthPlace;
    private Label lblProfession;
    private Label lblPhoneType;
    private Label lblPhoneNumber;
    private Label lblBirthday;
    private Label lblGender;
    private Label lblCivilState;
    private Label lblEmail;
    private Label lblWebSite;
    private PersonEJB personEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private List<PhonePerson> phonePersonList = null;
    private AffiliationRequest businessAffiliationRequestParam;
    public static AffiliationRequest businessAffiliationRequestParent = null;
    private Integer eventType;
    private Tab tabBusinessAffiliationRequests;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            businessAffiliationRequestParam = null;
        } else {
            businessAffiliationRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (AffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
            businessAffiliationRequestParent = businessAffiliationRequestParam;
        }

        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.businessAffiliationRequests.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.businessAffiliationRequests.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.businessAffiliationRequests.add"));
                break;
            default:
                break;
        }
        try {
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public AffiliationRequest getBusinessAffiliationRequets() {
        return this.businessAffiliationRequestParent;
    }

    public void setProductParent(AffiliationRequest businessAffiliationRequest) {
        this.businessAffiliationRequestParent = businessAffiliationRequest;
    }

    public Integer getEventType() {
        return this.eventType;
    }

    public void clearFields() {

    }

    private void loadFields(AffiliationRequest businessAffiliationRequest) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            lblRequestNumber.setValue(businessAffiliationRequest.getNumberRequest());
            lblRequestDate.setValue(simpleDateFormat.format(businessAffiliationRequest.getDateRequest()));
            lblStatusRequest.setValue(businessAffiliationRequest.getStatusRequestId().getDescription());

            businessAffiliationRequestParent = businessAffiliationRequest;
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void loadFieldRequest(Person person) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            lblCountryName.setValue(person.getCountryId().getName());
            lblIdentificationType.setValue(person.getNaturalPerson().getDocumentsPersonTypeId().getDescription());
            lblIdentificationNumber.setValue(person.getNaturalPerson().getIdentificationNumber());
            lblExpirationDater.setValue(simpleDateFormat.format(person.getNaturalPerson().getDueDateDocumentIdentification()));
            lblIdentificationNumberOld.setValue(person.getNaturalPerson().getIdentificactionNumberOld());
            lblFullName.setValue(person.getNaturalPerson().getFirstName());
            lblFullLastName.setValue(person.getNaturalPerson().getLastName());
            lblMarriedLastName.setValue(person.getNaturalPerson().getMarriedLastName());
            lblBirthPlace.setValue(person.getNaturalPerson().getPlaceBirth());
            lblBirthday.setValue(simpleDateFormat.format(person.getNaturalPerson().getDateBirth()));
            lblGender.setValue(person.getNaturalPerson().getGender());
            lblCivilState.setValue(person.getNaturalPerson().getCivilStatusId().getDescription());
            lblProfession.setValue(person.getNaturalPerson().getProfessionId().getName());          
            lblEmail.setValue(person.getEmail());
            lblWebSite.setValue(person.getWebSite());
            
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, person.getId());
            request.setParams(params);
            phonePersonList = personEJB.getPhoneByPerson(request);
            if (phonePersonList != null) {
                for (PhonePerson p : phonePersonList) {
                    if (p.getPhoneTypeId().getId() == Constants.PHONE_TYPE_ROOM) {
                        lblPhoneType.setValue(p.getPhoneTypeId().getDescription());
                        lblPhoneNumber.setValue(p.getNumberPhone());
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
