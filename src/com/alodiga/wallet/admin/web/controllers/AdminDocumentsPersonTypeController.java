package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.DocumentsPersonType;
import com.alodiga.wallet.common.model.OriginApplication;
import com.alodiga.wallet.common.model.PersonType;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public class AdminDocumentsPersonTypeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbCountry;
    private Combobox cmbPersonType;
    private Combobox cmbOriginApplication;
    private Textbox txtDocumentPerson;
    private Textbox txtIdentityCode;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private DocumentsPersonType documentsPersonTypeParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Sessions.getCurrent();
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            documentsPersonTypeParam = null;
        } else {
            documentsPersonTypeParam = (DocumentsPersonType) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
        initView(eventType, "crud.documentsPersonType");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.documentPersonType.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.documentPersonType.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.documentPersonType.add"));
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
        txtDocumentPerson.setRawValue(null);
        txtIdentityCode.setRawValue(null);
    }

    private void loadFields(DocumentsPersonType documentsPersonType) {
        try {
            txtDocumentPerson.setText(documentsPersonType.getDescription());
            if (documentsPersonType.getCodeIdentification() != null) {
                txtIdentityCode.setText(documentsPersonType.getCodeIdentification());
            }
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

    public void blockFields() {
        txtDocumentPerson.setReadonly(true);
        txtIdentityCode.setReadonly(true);
        
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (cmbCountry.getSelectedItem() == null) {
            cmbCountry.setFocus(true);
            this.showMessage("msj.error.countryNotSelected", true, null);
        }else if (cmbPersonType.getSelectedItem() == null) {
            cmbPersonType.setFocus(true);
            this.showMessage("msj.error.countryNotSelected", true, null);
        } else if (txtDocumentPerson.getText().isEmpty()) {
            txtDocumentPerson.setFocus(true);
            this.showMessage("msj.error.personType.notSelected", true, null);
        } else if (txtIdentityCode.getText().isEmpty()) {
            txtIdentityCode.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveDocumentsPersonType(DocumentsPersonType _documentsPersonType) {
        try {
            DocumentsPersonType documentsPersonType = null;
            if (_documentsPersonType != null) {
                documentsPersonType = _documentsPersonType;
            } else {
                documentsPersonType = new DocumentsPersonType();
            }
            documentsPersonType.setDescription(txtDocumentPerson.getText());
            documentsPersonType.setCodeIdentification(txtIdentityCode.getText());
            documentsPersonType.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            documentsPersonType = personEJB.saveDocumentsPersonType(documentsPersonType);
            documentsPersonTypeParam = documentsPersonType;
            this.showMessage("wallet.msj.save.success", false, null);
            
            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
            } else {
                btnSave.setVisible(true);
            }
        } catch (Exception ex) {
            showError(ex);
            this.showMessage("msj.error.errorSave", true, null);
        }
    }
    
    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveDocumentsPersonType(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveDocumentsPersonType(documentsPersonTypeParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(documentsPersonTypeParam);
                loadCmbCountry(eventType);
                loadCmbOriginApplication(eventType);
                onChange$cmbOriginApplication();

                break;
            case WebConstants.EVENT_VIEW:
                loadFields(documentsPersonTypeParam);
                blockFields();
                loadCmbCountry(eventType);
                loadCmbOriginApplication(eventType);
                onChange$cmbOriginApplication();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbOriginApplication(eventType);
                onChange$cmbCountry();
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(documentsPersonTypeParam != null ? documentsPersonTypeParam.getPersonTypeId().getCountryId().getId() : 0));
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
            loadGenericCombobox(originAplication, cmbOriginApplication, "name", evenInteger, Long.valueOf(documentsPersonTypeParam != null ? documentsPersonTypeParam .getPersonTypeId().getOriginApplicationId().getId() : 0));
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

    private void loadCmbPersonType(Integer evenInteger, int countryId , int originApplicationId) {
        EJBRequest request1 = new EJBRequest();
        cmbPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PARAM_ORIGIN_APPLICATION_ID, originApplicationId);
        request1.setParams(params);
        List<PersonType> personTypes = null;
        try {
            personTypes = utilsEJB.getPersonTypeByCountryByOriginApplication(request1);
            loadGenericCombobox(personTypes, cmbPersonType, "description", evenInteger, Long.valueOf(documentsPersonTypeParam != null ? documentsPersonTypeParam.getPersonTypeId().getId() : 0));
        } catch (EmptyListException ex) {
//            showError(ex);
            this.showMessage("msj.error.PersonTypeNull", true, null);
//            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        } finally {
            if (personTypes == null) {
                this.showMessage("msj.error.PersonTypeNull", true, null);
            }
        }
    }

}
