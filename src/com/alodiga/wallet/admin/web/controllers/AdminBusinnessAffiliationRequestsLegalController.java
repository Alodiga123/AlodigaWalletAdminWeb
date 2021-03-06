package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.enumeraciones.StatusRequestE;
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

public class AdminBusinnessAffiliationRequestsLegalController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private Label lblCountryName;
    private Label lblIdentificationType;
    private Label lblIdentificationNumber;
    private Label lblTradeName;
    private Label lblBusinessName;
    private Label lblBusinessCategory;
    private Label lblRegistryNumber;
    private Label lblRegistrationDate;
    private Label lblPayedCapital;
    private Label lblPhoneNumber;
    private Label lblEmail;
    private Label lblWebSite;
    private PersonEJB personEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private List<PhonePerson> phonePersonList = null;
    private AffiliationRequest businessAffiliationRequestParam;
    public static AffiliationRequest businessAffiliationRequetsParent = null;
    private Tab tabBusinessAffiliationRequests;
    private Tab tabAddress;
    private Tab tabLegalRepresentative;
    private Tab tabCollectionAffiliationRequests;
    private Tab tabOfac;
    private Tab tabReview;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            businessAffiliationRequestParam = null;
        } else {
            businessAffiliationRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (AffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
            businessAffiliationRequetsParent = businessAffiliationRequestParam;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.businessAffiliationRequests.edit"));
                activateTabCollectionAffiliationRequests();
                activateTabOfac();
                activateTabReview();
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.businessAffiliationRequests.view"));
                activateTabCollectionAffiliationRequests();
                activateTabOfac();
                activateTabReview();
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
        return this.businessAffiliationRequetsParent;
    }

    public void setProductParent(AffiliationRequest businessAffiliationRequets) {
        this.businessAffiliationRequetsParent = businessAffiliationRequets;
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

            businessAffiliationRequetsParent = businessAffiliationRequest;
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
            lblIdentificationType.setValue(person.getLegalPerson().getDocumentsPersonTypeId().getDescription());
            lblIdentificationNumber.setValue(person.getLegalPerson().getIdentificationNumber());
            lblTradeName.setValue(person.getLegalPerson().getTradeName());
            lblBusinessName.setValue(person.getLegalPerson().getBusinessName());
            lblBusinessCategory.setValue(person.getLegalPerson().getBusinessCategoryId().getDescription());
            lblRegistryNumber.setValue(person.getLegalPerson().getRegisterNumber());
            lblRegistrationDate.setValue(simpleDateFormat.format(person.getLegalPerson().getDateInscriptionRegister()));
            lblPayedCapital.setValue(String.valueOf(person.getLegalPerson().getPayedCapital()));            
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
    
    public void activateTabOfac(){
         if((businessAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.APLINE.getStatusRequestCode())) || 
            (businessAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.RELINE.getStatusRequestCode()))){
            tabOfac.setDisabled(false);
         } else {
            tabOfac.setDisabled(true);
         }
  
    }
    
    public void activateTabCollectionAffiliationRequests(){
         if((businessAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.APLINE.getStatusRequestCode()))) {
            tabCollectionAffiliationRequests.setDisabled(false);
         } else {
            tabCollectionAffiliationRequests.setDisabled(true); 
         }
  
    }
    
    public void activateTabReview(){
         if((businessAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.RECCOM.getStatusRequestCode())) ||
            (businessAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.RELINE.getStatusRequestCode())) ||
            (businessAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.RECINC.getStatusRequestCode()))) {
            tabReview.setDisabled(false);
         } else {
            tabReview.setDisabled(true);
         }  
    }
}
