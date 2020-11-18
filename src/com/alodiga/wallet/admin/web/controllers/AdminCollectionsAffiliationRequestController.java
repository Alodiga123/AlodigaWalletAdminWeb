package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.BusinessServiceType;
import com.alodiga.wallet.common.model.CollectionType;
import com.alodiga.wallet.common.model.CollectionsRequest;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.PersonType;
import com.alodiga.wallet.common.model.RequestHasCollectionRequest;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.io.File;
import java.io.FileOutputStream;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.zkoss.image.AImage;
import org.zkoss.image.Image;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

public class AdminCollectionsAffiliationRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private RequestHasCollectionRequest requestHasCollectionsRequestParam;
    private List<RequestHasCollectionRequest> requestHasCollectionsRequestList;
    private UtilsEJB utilsEJB = null;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private Label lblInfo;
    private Label txtCollectionType;
    private Textbox txtObservations;
    private Combobox cmbPersonType;
    private Combobox cmbCountry;
    private Button btnSave;
    private Button btnUpload;
    private Image image;
    private Vbox divPreview;
    String UrlFile = "";
    String format = "";
    private boolean uploaded = false;
    public Window winAdminCollectionsRequestController;
    private AdminUsersAffiliationRequestsController adminUserRequest = null;
    private AffiliationRequest affiliationRequest = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        adminUserRequest = new AdminUsersAffiliationRequestsController();
        if(adminUserRequest.getUserAffiliationRequets() != null){
            affiliationRequest = adminUserRequest.getUserAffiliationRequets();
        }
        
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
            	requestHasCollectionsRequestParam = (RequestHasCollectionRequest) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
            	requestHasCollectionsRequestParam = (RequestHasCollectionRequest) Sessions.getCurrent().getAttribute("object");
                break;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        } 
    }



    public void clearFields() {
    }

    private void loadField(AffiliationRequest affiliationRequest) {
        try {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            if (affiliationRequest.getNumberRequest() != null) {
                lblRequestNumber.setValue(affiliationRequest.getNumberRequest());
                lblRequestDate.setValue(simpleDateFormat.format(affiliationRequest.getDateRequest()));
                lblStatusRequest.setValue(affiliationRequest.getStatusRequestId().getDescription());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFieldC(CollectionsRequest collectionsRequest) {   
        txtCollectionType.setValue(collectionsRequest.getCollectionTypeId().getDescription());
    }

    private void loadFields(RequestHasCollectionRequest requestHasCollectionsRequest) {
        if (requestHasCollectionsRequest != null) {
            try {
            	short approved=1; 
            	if (requestHasCollectionsRequest.getIndApproved()!=null) {
	                if (requestHasCollectionsRequest.getIndApproved()==approved) {
	                    rApprovedYes.setChecked(true);
	                } else {
	                    rApprovedNo.setChecked(true);
	                }
            	}
                txtObservations.setText(requestHasCollectionsRequest.getObservations());
                UrlFile = requestHasCollectionsRequest.getImageFileUrl();
                if (UrlFile!=null) {
	                	
	                AImage image;
	                image = new org.zkoss.image.AImage(requestHasCollectionsRequest.getImageFileUrl());
	                org.zkoss.zul.Image imageFile = new org.zkoss.zul.Image();
	                imageFile.setWidth("250px");
	                imageFile.setContent(image);
	                imageFile.setParent(divPreview);
                }
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    public Boolean validateEmpty() {
        if(txtObservations.getText().isEmpty()){
             this.showMessage("sp.error.renewal.observations",true, null);
        } else {
            return true;
        }
        return false;
    }

  

    public void blockFields() {
        btnSave.setVisible(false);
        btnUpload.setDisabled(true);
    }

    public void onClick$btnBack() {
    	winAdminCollectionsRequestController.detach();
    }

    private void saveRequest(RequestHasCollectionRequest _requestHasCollectionsRequest) {
    	short approved = 1;
        try {
        	RequestHasCollectionRequest requestHasCollectionsRequest = null;

            if (_requestHasCollectionsRequest != null) {
                requestHasCollectionsRequest = _requestHasCollectionsRequest;
            } 
            if (rApprovedYes.isChecked()) {
            	approved = 1;
            } else {
            	approved = 0;
            }

            //Guarda la solicitud en requestHasCollectionsRequest
            requestHasCollectionsRequest.setIndApproved(approved);
            requestHasCollectionsRequest.setObservations(txtObservations.getText());
            requestHasCollectionsRequest.setUpdateDate(new Timestamp(new Date().getTime()));
            requestHasCollectionsRequest.setImageFileUrl(UrlFile);
            requestHasCollectionsRequest = utilsEJB.saveRequestHasCollectionsRequest(requestHasCollectionsRequest);
            
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(EjbConstants.PARAM_AFFILIATION_REQUEST,requestHasCollectionsRequest.getAffiliationRequestId().getId());
            request.setParams(params);
            utilsEJB.updateBusinessAffiliationRequest(request);
           
            
            
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateCollectionsAffiliationRequest", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }
  
    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    saveRequest(requestHasCollectionsRequestParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(requestHasCollectionsRequestParam);
                loadField(requestHasCollectionsRequestParam.getAffiliationRequestId());
                loadFieldC(requestHasCollectionsRequestParam.getCollectionsRequestId());
                loadCmbCountry(eventType);
                loadCmbPersonType(eventType);
                cmbPersonType.setDisabled(true);
                cmbCountry.setDisabled(true);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(requestHasCollectionsRequestParam);
                loadField(requestHasCollectionsRequestParam.getAffiliationRequestId());
                loadFieldC(requestHasCollectionsRequestParam.getCollectionsRequestId());
                blockFields();
                loadCmbCountry(eventType);
                loadCmbPersonType(eventType);         
            default:
                break;
        }
    }
    
    public void onUpload$btnUpload(org.zkoss.zk.ui.event.UploadEvent event) throws Throwable {
        org.zkoss.util.media.Media media = event.getMedia();
        if (media != null) {
            divPreview.getChildren().clear();
            media = event.getMedia();
            File file = new File("/opt/alodiga/proyecto/maw/imagenes/" + media.getName());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(media.getByteData());
            fos.flush();
            fos.close();
            UrlFile = file.getAbsolutePath();
            format = media.getFormat();
            org.zkoss.zul.Image image = new org.zkoss.zul.Image();
            image.setContent((org.zkoss.image.Image) media);
            image.setWidth("250px");
            image.setParent(divPreview);
            uploaded = true;
        } else {
            lblInfo.setValue("Error");
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;
        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(requestHasCollectionsRequestParam.getCollectionsRequestId() != null ? requestHasCollectionsRequestParam.getCollectionsRequestId().getCollectionTypeId().getCountryId().getId() : 0));
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

    private void loadCmbPersonType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        cmbPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, requestHasCollectionsRequestParam.getCollectionsRequestId().getCollectionTypeId().getCountryId().getId());
        params.put(QueryConstants.PARAM_IND_NATURAL_PERSON, requestHasCollectionsRequestParam.getCollectionsRequestId().getPersonTypeId().getIndNaturalPerson());
        params.put(QueryConstants.PARAM_ORIGIN_APPLICATION_ID, requestHasCollectionsRequestParam.getCollectionsRequestId().getPersonTypeId().getOriginApplicationId().getId());
        request1.setParams(params);
        List<PersonType> personTypes;
        try {
            personTypes = utilsEJB.getPersonTypeByCountryByIndNaturalPerson(request1);
            loadGenericCombobox(personTypes, cmbPersonType, "description", evenInteger, Long.valueOf(requestHasCollectionsRequestParam.getCollectionsRequestId() != null ? requestHasCollectionsRequestParam.getCollectionsRequestId().getPersonTypeId().getId() : 0));
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
