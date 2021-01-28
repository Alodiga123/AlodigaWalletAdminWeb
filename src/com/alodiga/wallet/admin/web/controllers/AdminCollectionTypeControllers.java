package com.alodiga.wallet.admin.web.controllers;


import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.CollectionType;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.OriginApplication;
import com.alodiga.wallet.common.model.PersonType;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminCollectionTypeControllers extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtDescription;
    private Combobox cmbCountry;
    private Combobox cmbOriginApplication;
    private Combobox cmbPersonType;
    private UtilsEJB utilsEJB = null;
    private CollectionType collectionTypeParam;
    private OriginApplication originApplicationParam;
    private Toolbarbutton tbbTitle;
    private Button btnSave;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            collectionTypeParam = null;
        } else {
            collectionTypeParam = (CollectionType) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
        loadData();
    }

    
    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.collectionsType.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.collectionsType.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.collectionsType.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    public void onChange$cmbOriginApplication() {
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        OriginApplication origin = (OriginApplication) cmbOriginApplication.getSelectedItem().getValue();
        loadCmbPersonType(eventType, country.getId().intValue(), origin.getId());
    } 
    
    public void onChange$cmbCountry() {
        if (cmbCountry.getSelectedItem() == null) {
           cmbOriginApplication.setDisabled(true);
           cmbPersonType.setDisabled(true);
        } else {
            cmbOriginApplication.setDisabled(false);
            cmbPersonType.setDisabled(false);
        }           

    }
    
    public void clearFields() {
        txtDescription.setRawValue(null);
    }
    
     public Boolean validateEmpty() {
        
        if (cmbCountry.getSelectedItem() == null) {
            cmbCountry.setFocus(true);
            this.showMessage("sp.error.countryNotSelected", true, null);
            return false;
        }
        
        if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
            return false;
        } 
        
        return true;
    }

    private void loadFields(CollectionType collectionType) {
         try {
            txtDescription.setText(collectionType.getDescription());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtDescription.setReadonly(true);
        btnSave.setVisible(false);
    }

    private void saveCollections(CollectionType collectionType_) {
        try {
            CollectionType collectionType = null;

            if (collectionType_ != null) {
                collectionType = collectionType_;
            } else {//New requestType
                collectionType = new CollectionType();
            }

            //insertando en collectionType
            collectionType.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            collectionType.setDescription(txtDescription.getText());
            collectionType.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            collectionType = utilsEJB.saveCollectionType(collectionType);
            collectionTypeParam = collectionType;
            this.showMessage("wallet.msj.save.success", false, null);
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveCollections(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCollections(collectionTypeParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(collectionTypeParam);
                loadCmbOriginApplication(eventType);
                loadCmbCountry(eventType);
                onChange$cmbOriginApplication();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(collectionTypeParam);
                blockFields();
                loadCmbOriginApplication(eventType);
                loadCmbCountry(eventType);
                onChange$cmbOriginApplication();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbOriginApplication(eventType);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            default:
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;
        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(collectionTypeParam != null ? collectionTypeParam.getCountryId().getId() : 0));
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
    
    private void loadCmbOriginApplication(Integer evenInteger) {
        //cmbOriginAplication
        EJBRequest request1 = new EJBRequest();
        List<OriginApplication> originAplication;
        try {
            originAplication = utilsEJB.getOriginApplications(request1);
            loadGenericCombobox(originAplication, cmbOriginApplication, "name", evenInteger, Long.valueOf(collectionTypeParam != null ? collectionTypeParam.getPersonTypeId().getOriginApplicationId().getId() : 0));
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
       
    
    private void loadCmbPersonType(Integer evenInteger, int countryId, int originApplicationId) {
        cmbPersonType.getItems().clear();        
        EJBRequest request1 = new EJBRequest();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PARAM_ORIGIN_APPLICATION_ID, originApplicationId);
        request1.setParams(params);
        List<PersonType> personType;
        try {
            personType = utilsEJB.getPersonTypeByCountryByOriginApplication(request1);
            loadGenericCombobox(personType, cmbPersonType, "description", evenInteger, Long.valueOf(collectionTypeParam != null ? collectionTypeParam.getPersonTypeId().getId() : 0));
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
