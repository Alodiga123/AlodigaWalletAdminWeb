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

public class AdminUsersAffiliationRequestsController extends GenericAbstractAdminController {

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
    private AffiliationRequest userAffiliationRequestParam;
    public static AffiliationRequest userAffiliationRequestParent = null;
    private Integer eventType;
    private Tab tabUserAffiliationRequests;
    private Tab tabOfac;
    private Tab tabReview;
    private Tab tabCollectionAffiliationRequests;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            userAffiliationRequestParam = null;
        } else {
            userAffiliationRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (AffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
            userAffiliationRequestParent = userAffiliationRequestParam;
        }

        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.userAffiliationRequests.edit"));
                activateTabCollectionAffiliationRequests();
                activateTabOfac();
                activateTabReview();
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.userAffiliationRequests.view"));
                activateTabCollectionAffiliationRequests();
                activateTabOfac();
                activateTabReview();
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.userAffiliationRequests.add"));
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

    public AffiliationRequest getUserAffiliationRequets() {
        return this.userAffiliationRequestParent;
    }

    public void setProductParent(AffiliationRequest userAffiliationRequest) {
        this.userAffiliationRequestParent = userAffiliationRequest;
    }

    public Integer getEventType() {
        return this.eventType;
    }

    public void clearFields() {

    }

    private void loadFields(AffiliationRequest userAffiliationRequest) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            lblRequestNumber.setValue(userAffiliationRequest.getNumberRequest());
            lblRequestDate.setValue(simpleDateFormat.format(userAffiliationRequest.getDateRequest()));
            lblStatusRequest.setValue(userAffiliationRequest.getStatusRequestId().getDescription());
            userAffiliationRequestParent = userAffiliationRequest;
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void loadFieldRequest(Person person) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            if(person.getCountryId().getName() != null){
                lblCountryName.setValue(person.getCountryId().getName());
            }
            
            lblIdentificationType.setValue(person.getNaturalPerson().getDocumentsPersonTypeId().getDescription());
            lblIdentificationNumber.setValue(person.getNaturalPerson().getIdentificationNumber());
            
            if(person.getNaturalPerson().getDueDateDocumentIdentification() != null){
               lblExpirationDater.setValue(simpleDateFormat.format(person.getNaturalPerson().getDueDateDocumentIdentification())); 
            }
            
            if(person.getNaturalPerson().getIdentificactionNumberOld() != null){
               lblIdentificationNumberOld.setValue(person.getNaturalPerson().getIdentificactionNumberOld());
            }
            
            if(person.getNaturalPerson().getFirstName() != null){
               lblFullName.setValue(person.getNaturalPerson().getFirstName()); 
            }
            
            if(person.getNaturalPerson().getLastName() != null){
               lblFullLastName.setValue(person.getNaturalPerson().getLastName()); 
            }
            
            if(person.getNaturalPerson().getMarriedLastName() != null){
               lblMarriedLastName.setValue(person.getNaturalPerson().getMarriedLastName());  
            }
            
            if(person.getNaturalPerson().getPlaceBirth() != null){
               lblBirthPlace.setValue(person.getNaturalPerson().getPlaceBirth()); 
            }
            
            if(person.getNaturalPerson().getDateBirth() != null){
               lblBirthday.setValue(simpleDateFormat.format(person.getNaturalPerson().getDateBirth())); 
            }
            
            if(person.getNaturalPerson().getGender() != null){
               lblGender.setValue(person.getNaturalPerson().getGender());  
            }
            
            if(person.getNaturalPerson().getCivilStatusId() != null){
                lblCivilState.setValue(person.getNaturalPerson().getCivilStatusId().getDescription());
            }
            
            if(person.getNaturalPerson().getProfessionId() != null){
               lblProfession.setValue(person.getNaturalPerson().getProfessionId().getName());  
            }
            
            if(person.getEmail() != null){
               lblEmail.setValue(person.getEmail()); 
            }
           
            if(person.getWebSite() != null){
              lblWebSite.setValue(person.getWebSite());  
            }
            
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, person.getId());
            request.setParams(params);
            phonePersonList = personEJB.getPhoneByPerson(request);
            if (phonePersonList != null) {
                for (PhonePerson p : phonePersonList) {
                        lblPhoneType.setValue(p.getPhoneTypeId().getDescription());
                        lblPhoneNumber.setValue(p.getNumberPhone());
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
                loadFields(userAffiliationRequestParam);
                loadFieldRequest(userAffiliationRequestParam.getUserRegisterUnifiedId());
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(userAffiliationRequestParam);
                loadFieldRequest(userAffiliationRequestParam.getUserRegisterUnifiedId());
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
    
    public void activateTabOfac(){
         if((userAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.APLINE.getStatusRequestCode())) || 
            (userAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.RELINE.getStatusRequestCode()))){
            tabOfac.setDisabled(false);
         } else {
            tabOfac.setDisabled(true);
         }
  
    }
    
    public void activateTabCollectionAffiliationRequests(){
         if((userAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.APLINE.getStatusRequestCode()))) {
            tabCollectionAffiliationRequests.setDisabled(false);
         } else {
            tabCollectionAffiliationRequests.setDisabled(true); 
         }
  
    }
    
    public void activateTabReview(){
         if((userAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.RECCOM.getStatusRequestCode())) ||
            (userAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.RELINE.getStatusRequestCode())) ||
            (userAffiliationRequestParam.getStatusRequestId().getCode().equals(StatusRequestE.RECINC.getStatusRequestCode()))) {
            tabReview.setDisabled(false);
         } else {
            tabReview.setDisabled(true);
         }
  
    }
}
