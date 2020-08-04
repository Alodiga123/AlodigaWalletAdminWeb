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
import com.alodiga.wallet.common.model.PersonType;
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
import org.zkoss.zul.Toolbarbutton;

public class AdminCollectionsRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbCountry;
    private Combobox cmbPersonType;
    private Combobox cmbCollectionType;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
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
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
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
    }

    public void onChange$cmbCountry() {
        this.clearMessage();
        cmbCollectionType.setVisible(true);
        cmbPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbCollectionType(eventType, Integer.parseInt(country.getId().toString()));
        loadCmbPersonType(eventType, Integer.parseInt(country.getId().toString()));
    }

    public void onChange$cmbCollectionType() {
        this.clearMessage();
    }
    public void onChange$cmbPersonType() {
        this.clearMessage();
    }
    
    public void blockFields() {
        btnSave.setVisible(false);
    }

    private void saveCollectionsRequest(CollectionsRequest _collectionsRequest) {
        CollectionsRequest collectionsRequest = null;

        try {
            if (_collectionsRequest != null) {
                collectionsRequest = _collectionsRequest;
            } else {//New collectionsRequest
                collectionsRequest = new CollectionsRequest();
            }

            collectionsRequest.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            collectionsRequest.setCollectionTypeId((CollectionType) cmbCollectionType.getSelectedItem().getValue());

            if (validate(collectionsRequest)) {
                collectionsRequest = utilsEJB.saveCollectionsRequest(collectionsRequest);
                collectionsRequestParam = collectionsRequest;
                this.showMessage("sp.common.save.success", false, null);

                eventType = WebConstants.EVENT_EDIT;

                if (eventType == WebConstants.EVENT_ADD) {
                    btnSave.setVisible(false);
                } else {
                    btnSave.setVisible(true);
                }
            } else {
                this.showMessage("sp.crud.product.exist", true, null);
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
        } else if (cmbCollectionType.getText().isEmpty()) {
            cmbCollectionType.setFocus(true);
            this.showMessage("sp.error.collectionsType.notSelected", true, null);
        } else if (cmbPersonType.getSelectedItem() == null) {
            cmbPersonType.setFocus(true);
            this.showMessage("sp.error.personType.notSelected", true, null);
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
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(collectionsRequestParam);
                blockFields();
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
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

//    private void loadCmbPersonType(Integer evenInteger, int countryId) {
//        EJBRequest request1 = new EJBRequest();
//        cmbPersonType.getItems().clear();
//        Map params = new HashMap();
//        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
//        if (evenInteger == WebConstants.EVENT_ADD) {
//            params.put(QueryConstants.PARAM_ORIGIN_APPLICATION_ID, Constants.ORIGIN_APPLICATION_WALLET_ADMIN_WEB_ID);
//        } else {
//            params.put(QueryConstants.PARAM_ORIGIN_APPLICATION_ID, collectionsRequestParam.getPersonTypeId().getOriginApplicationId().getId());
//        }
//        request1.setParams(params);
//        List<PersonType> personTypes = null;
//        try {
//            personTypes = personEJB.getPersonTypeByCountry(request1);
//            loadGenericCombobox(personTypes, cmbPersonType, "description", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getPersonTypeId().getId() : 0));
//        } catch (EmptyListException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        } catch (GeneralException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        } catch (NullParameterException ex) {
//            showError(ex);
//            ex.printStackTrace();
//        } finally {
//            if (personTypes == null) {
//                this.showMessage("cms.msj.PersonTypeNull", false, null);
//            }
//        }
//    }
    private void loadCmbPersonType(Integer evenInteger, int countryId) {
        EJBRequest request1 = new EJBRequest();
        cmbPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        request1.setParams(params);
        List<PersonType> personTypes = null;
        try {
            personTypes = personEJB.getPersonTypeByCountry(request1);
            loadGenericCombobox(personTypes, cmbPersonType, "description", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getPersonTypeId().getId() : 0));
        } catch (EmptyListException ex) {
//            showError(ex);
            this.showMessage("sp.msj.PersonTypeNull", true, null);
//            ex.printStackTrace();
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

    private void loadCmbCollectionType(Integer evenInteger, int countryId) {
        EJBRequest request = new EJBRequest();
        cmbCollectionType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        request.setParams(params);
        List<CollectionType> collectionTypes;
        try {
            collectionTypes = utilsEJB.getCollectionTypeByCountry(request);
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
        }
    }
}
