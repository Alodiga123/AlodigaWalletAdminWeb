package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.CollectionType;
import com.alodiga.wallet.common.model.CollectionsRequest;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.OriginApplication;
import com.alodiga.wallet.common.model.PersonType;
import com.alodiga.wallet.common.model.RequestType;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminCollectionsRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbCountry;
    private Combobox cmbPersonType;
    private Combobox cmbCollectionType;
    private Combobox cmbOriginAplication;
    private Combobox cmbRequestType;
    private Radio isEnabledYes;
    private Radio isEnabledNo;
    private UtilsEJB utilsEJB = null;
    private CollectionsRequest collectionsRequestParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            collectionsRequestParam = null;
        } else {
            collectionsRequestParam = (CollectionsRequest) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.collectionsRequest.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.collectionsRequest.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.collectionsRequest.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        btnSave.setVisible(false);
    }

    private void loadFields(CollectionsRequest collectionsRequest) {
        btnSave.setVisible(true);
        if (collectionsRequest.getEnabled() == true) {
            isEnabledYes.setChecked(true);
        } else {
            isEnabledNo.setChecked(true);
        }
    }

    public void onChange$cmbCountry() {
        if (cmbCountry.getSelectedItem() == null) {
            cmbOriginAplication.setDisabled(true);
            cmbCollectionType.setDisabled(true);
            cmbPersonType.setDisabled(true);
            cmbRequestType.setDisabled(true);
        } else {
            cmbOriginAplication.setDisabled(false);
            cmbCollectionType.setDisabled(false);
            cmbPersonType.setDisabled(false);
            cmbRequestType.setDisabled(false);
        }

    }

    public void onChange$cmbOriginAplication() {

        Long countryId = ((Country) cmbCountry.getSelectedItem().getValue()).getId();
        Integer originApplicationId = ((OriginApplication) cmbOriginAplication.getSelectedItem().getValue()).getId();
        loadCmbPersonType(eventType, countryId, originApplicationId);

    }

    public void onChange$cmbPersonType() {
        Long countryId = ((Country) cmbCountry.getSelectedItem().getValue()).getId();
        Integer personType = ((PersonType) cmbPersonType.getSelectedItem().getValue()).getId();
        loadCmbCollectionType(eventType, countryId, personType);
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

    private void saveCollectionsRequest(CollectionsRequest _collectionsRequest) {
        CollectionsRequest collectionsRequest = null;
        boolean isEnabled  = true;
        
        try {
            if (_collectionsRequest != null) {
                collectionsRequest = _collectionsRequest;
            } else {//New collectionsRequest
                collectionsRequest = new CollectionsRequest();
            }
            
            if (isEnabledYes.isChecked()) {
                isEnabled = true;
            } else {
                isEnabled = false;
            }

            collectionsRequest.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            collectionsRequest.setCollectionTypeId((CollectionType) cmbCollectionType.getSelectedItem().getValue());
            collectionsRequest.setRequestTypeId((RequestType) cmbRequestType.getSelectedItem().getValue());
            collectionsRequest.setEnabled(isEnabled);
            if(eventType == WebConstants.EVENT_ADD){
                if (validate(collectionsRequest)) {
                    collectionsRequest = utilsEJB.saveCollectionsRequest(collectionsRequest);
                    collectionsRequestParam = collectionsRequest;
                    this.showMessage("sp.common.save.success", false, null);
                } else {
                    this.showMessage("sp.crud.product.exist", true, null);
                }
            } else {
                collectionsRequest = utilsEJB.saveCollectionsRequest(collectionsRequest);
                collectionsRequestParam = collectionsRequest;
                this.showMessage("sp.common.save.success", false, null);
            }

            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
            } else {
                btnSave.setVisible(true);
            }

        } catch (NullParameterException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            showError(ex);
        }
    }

    private boolean validate(CollectionsRequest collectionRequest) {
        EJBRequest request1 = new EJBRequest();
        List<CollectionsRequest> list;

        try {
            list = (List<CollectionsRequest>) utilsEJB.getCollectionsRequestByID(collectionRequest);

            if (list.isEmpty()) {
                return true;
            }
        } catch (GeneralException ex) {
            Logger.getLogger(AdminAddProductHasBankController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EmptyListException ex) {
            Logger.getLogger(AdminAddProductHasBankController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminAddProductHasBankController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Boolean validateEmpty() {
        if (cmbCountry.getSelectedItem() == null) {
            cmbCountry.setFocus(true);
            this.showMessage("sp.error.countryNotSelected", true, null);
        } else if (cmbCollectionType.getSelectedItem() == null) {
            cmbCollectionType.setFocus(true);
            this.showMessage("sp.error.collectionsType.notSelected", true, null);
        } else if (cmbPersonType.getSelectedItem() == null) {
            cmbPersonType.setFocus(true);
            this.showMessage("sp.error.personType.notSelected", true, null);
        } else if (cmbRequestType.getSelectedItem() == null) {
            cmbRequestType.setFocus(true);
            this.showMessage("sp.error.requestType.notSelected", true, null);
        } else {
            return true;
        }
        return false;
    }

    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveCollectionsRequest(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCollectionsRequest(collectionsRequestParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(collectionsRequestParam);
                loadCmbOriginAplication(eventType);
                loadCmbCountry(eventType);                
                onChange$cmbOriginAplication();
                onChange$cmbPersonType();
                loadCmbRequestType(eventType);
                cmbCollectionType.setDisabled(true);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(collectionsRequestParam);
                blockFields();
                loadCmbOriginAplication(eventType);
                loadCmbCountry(eventType);                
                onChange$cmbOriginAplication();
                onChange$cmbPersonType();
                loadCmbRequestType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                onChange$cmbCountry();
                loadCmbRequestType(eventType);
                loadCmbOriginAplication(eventType);
                loadCmbCountry(eventType);
                onChange$cmbPersonType();
                break;
            default:
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;

        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getCollectionTypeId().getCountryId().getId() : 0));
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

    private void loadCmbOriginAplication(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<OriginApplication> originApplications;
        try {
            originApplications = utilsEJB.getOriginApplications(request1);
             if (eventType == WebConstants.EVENT_EDIT) {
                 loadGenericCombobox(originApplications, cmbOriginAplication, "name", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getPersonTypeId().getOriginApplicationId().getId() : 0));
             }else if  (eventType == WebConstants.EVENT_ADD){
                 loadGenericCombobox(originApplications, cmbOriginAplication, "name", evenInteger, 3L);
             }else if  (eventType == WebConstants.EVENT_VIEW){
                 loadGenericCombobox(originApplications, cmbOriginAplication, "name", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getPersonTypeId().getOriginApplicationId().getId() : 0));
             }    
            
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            this.showMessage("sp.msj.error.origin.aplication", true, null);
            ex.printStackTrace();
        }
    }

    private void loadCmbPersonType(Integer evenInteger, Long countryId, Integer originAplicationId) {
        EJBRequest request1 = new EJBRequest();
        //cmbPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PARAM_ORIGIN_APPLICATION_ID, originAplicationId);
        request1.setParams(params);
        List<PersonType> personTypes = null;
        try {
            personTypes = utilsEJB.getPersonTypeByCountryByOriginApplication(request1);
            loadGenericCombobox(personTypes, cmbPersonType, "description", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getPersonTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            //this.showMessage("sp.msj.PersonTypeNull", true, null);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        } finally {
            if (personTypes == null) {
                this.showMessage("sp.msj.PersonTypeNull", true, null);
            } 
        }
    }
    
     private void loadCmbRequestType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<RequestType> requestType;

        try {
            requestType = utilsEJB.getRequestType(request1);
            loadGenericCombobox(requestType, cmbRequestType, "description", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getRequestTypeId().getId() : 0));
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

    private void loadCmbCollectionType(Integer evenInteger, Long countryId, Integer personType) {
        EJBRequest request = new EJBRequest();
       // cmbCollectionType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PERSON_TYPE_ID, personType);
        request.setParams(params);
        List<CollectionType> collectionTypes = null;
        try {
            collectionTypes = utilsEJB.getCollectionTypeByCountryByPersonType(request);
            loadGenericCombobox(collectionTypes, cmbCollectionType, "description", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getCollectionTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        } finally {
            if (collectionTypes == null) {
                this.showMessage("sp.msj.PersonTypeNull", true, null);
            }
        }
    }
}
