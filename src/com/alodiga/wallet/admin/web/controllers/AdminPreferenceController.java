package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.Language;
import com.alodiga.wallet.common.model.Preference;
import com.alodiga.wallet.common.model.PreferenceField;
import com.alodiga.wallet.common.model.PreferenceFieldData;
import com.alodiga.wallet.common.model.PreferenceType;
import com.alodiga.wallet.common.model.PreferenceValue;
import com.alodiga.wallet.common.enumeraciones.PersonClassificationE;
import com.alodiga.wallet.common.model.PersonClassification;
import com.alodiga.wallet.common.model.PreferenceClassification;
import com.alodiga.wallet.common.utils.Constants;

import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminPreferenceController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtPreference;
    private Textbox txtCode;
    private Textbox txtDescription;
    private Textbox txtDescriptionEnglish;
    private Combobox cmbTypePreference;
    private Combobox cmbTypeData;
    private Radio rEnabledYes;
    private Radio rEnabledNo;
    private UtilsEJB utilsEJB = null;
    private PreferencesEJB preferencesEJB = null;
    private PersonEJB personEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private PreferenceField preferenceFieldParam;
    private Integer eventType;
    private List<PreferenceFieldData> preferenceDataList = null;
    List<PreferenceField> preferenceFieldList = new ArrayList<PreferenceField>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            preferenceFieldParam = null;
        } else {
            preferenceFieldParam = (Sessions.getCurrent().getAttribute("object") != null) ? (PreferenceField) Sessions.getCurrent().getAttribute("object") : null;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.preferences.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.preferences.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.preferences.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void clearFields() {
        txtPreference.setRawValue(null);
    }

    private void loadFields(PreferenceField preferenceField) {
        try {
            txtPreference.setText(preferenceField.getName());
            txtCode.setText(preferenceField.getCode());  
            btnSave.setVisible(true);
            if (preferenceField.getEnabled() == 1) {
                rEnabledYes.setChecked(true);
            } else {
                rEnabledNo.setChecked(true);
            }
                EJBRequest request = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.PREFERENCE_FIELD_KEY, preferenceField.getId());
                request.setParams(params);
                preferenceDataList = preferencesEJB.getPreferenceFieldDataByPreference(request);

                if (preferenceDataList != null) {
                    for (PreferenceFieldData p : preferenceDataList) {
                        if (p.getLanguage().getId() == Constants.SPANISH) {
                            txtDescription.setText(p.getDescription());
                        }
                        if (p.getLanguage().getId() == Constants.ENGLISH) {
                            txtDescriptionEnglish.setText(p.getDescription());
                        }
                    }
                }
                
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
       txtPreference.setReadonly(true);
       txtCode.setReadonly(true);
       txtDescription.setReadonly(true);
       txtDescriptionEnglish.setReadonly(true);
       btnSave.setVisible(false);
       rEnabledYes.setDisabled(true);
       rEnabledNo.setDisabled(true);
    }

    public boolean validateEmpty() {
        if (txtPreference.getText().isEmpty()) {
            txtPreference.setFocus(true);
            this.showMessage("sp.error.preferences.description", true, null);
        } else if (cmbTypePreference.getSelectedItem() == null) {
            cmbTypePreference.setFocus(true);
            this.showMessage("sp.error.preferences.preferenceType", true, null);
        }  else if (cmbTypeData.getSelectedItem() == null) {
            cmbTypeData.setFocus(true);
            this.showMessage("sp.error.preferences.typeData", true, null);
        } else {
            return true;
        }
        return false;

    }
    
    public boolean validatePreferenceCode() {
//        PreferenceField preferenceField = null;
//        preferenceFieldList.clear();
//        try{
//            EJBRequest request = new EJBRequest();
//            Map params = new HashMap();
//            params.put(Constants.PARAM_CODE, txtCode.getText());
//            request.setParams(params);
//            preferenceFieldList = preferencesEJB.getPreferenceFieldsByCode(request);
//        } catch (Exception ex) {
//            showError(ex);
//        } if(preferenceFieldList.size() > 0){
//            this.showMessage("sp.error.preferences.code", true, null);
//                txtCode.setFocus(true);
//                return false;
//        }
        return true;
    }
    

    private void savePreferenceField(PreferenceField _preferenceField) {
        PreferenceField preference = null;
        PreferenceFieldData preferenceDataES = null;
        PreferenceFieldData preferenceDataEN = null;
        PreferenceClassification classificationClient = null;
        PreferenceClassification classificationBussines = null;
        PreferenceValue preferenceValueClient = null;
        PreferenceValue preferenceValueBusiness = null;
        Language languageES = null;
        Language languageEN = null;
        short indEnable = 1;
        
        try {            
            //Se obtienen los lenguajes Español e Ingles
            EJBRequest request4 = new EJBRequest();
            request4.setParam(Constants.SPANISH_LANGUAGE);
            languageEN = utilsEJB.loadLanguage(request4);

            request4 = new EJBRequest();
            request4.setParam(Constants.ENGLISH_LANGUAGE);
            languageES = utilsEJB.loadLanguage(request4);
            
            //Se obtienen las clasificaciones para el Preference value
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.CLIENT_CLASSIFICATION);
            classificationClient = preferencesEJB.loadPreferenceClassification(request1);
            
            request1 = new EJBRequest();
            request1.setParam(Constants.BUSINESS_CLASSIFICATION);
            classificationBussines = preferencesEJB.loadPreferenceClassification(request1);

            if (_preferenceField != null) {
                preference = _preferenceField;                
            } else {
                preference = new PreferenceField();                
            }
            
            if (rEnabledYes.isChecked()) {
                indEnable = 1;
            } else {
                indEnable = 0;
            }
            
            //Guardar Preference Field
            preference.setName(txtPreference.getText());
            preference.setDescription(txtDescription.getText());
            preference.setCode(txtCode.getText());
            preference.setPreferenceId((Preference) cmbTypePreference.getSelectedItem().getValue());
            preference.setPreferenceTypeId((PreferenceType) cmbTypeData.getSelectedItem().getValue());
            preference.setEnabled(indEnable);
            request.setParam(preference);
            preference = preferencesEJB.savePreferenceField(request);
            
            //Se obtienen las Descripciones de las preferencias
            if (eventType == WebConstants.EVENT_EDIT) {
                request = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.PREFERENCE_FIELD_KEY, preference.getId());
                request.setParams(params);
                preferenceDataList = preferencesEJB.getPreferenceFieldDataByPreference(request);

                if (preferenceDataList != null) {
                    for (PreferenceFieldData p : preferenceDataList) {
                        if (p.getLanguage().getId() == Constants.SPANISH) {
                            preferenceDataES = p;
                        }
                        if (p.getLanguage().getId() == Constants.ENGLISH) {
                            preferenceDataEN = p;
                        }
                    }
                    
                    //Guardar descripcion en español en PreferenceFieldData
                    preferenceDataES.setPreferenceField(preference);
                    preferenceDataES.setLanguage(languageES);
                    preferenceDataES.setDescription(txtDescription.getText());
                    preferenceDataES = preferencesEJB.savePreferenceFieldData(preferenceDataES);
                    //Guardar descripcion en ingles en PreferenceFieldData
                    preferenceDataEN.setPreferenceField(preference);
                    preferenceDataEN.setLanguage(languageEN);
                    preferenceDataEN.setDescription(txtDescriptionEnglish.getText());
                    preferenceDataEN = preferencesEJB.savePreferenceFieldData(preferenceDataEN);
                }
            } else if (eventType == WebConstants.EVENT_ADD) {
                //Guardar descripcion en español en PreferenceFieldData
                preferenceDataES = new PreferenceFieldData();
                preferenceDataES.setPreferenceField(preference);
                preferenceDataES.setLanguage(languageES);
                preferenceDataES.setDescription(txtDescription.getText());
                preferenceDataES = preferencesEJB.savePreferenceFieldData(preferenceDataES);
                //Guardar descripcion en ingles en PreferenceFieldData
                preferenceDataEN = new PreferenceFieldData();
                preferenceDataEN.setPreferenceField(preference);
                preferenceDataEN.setLanguage(languageEN);
                preferenceDataEN.setDescription(txtDescriptionEnglish.getText());
                preferenceDataEN = preferencesEJB.savePreferenceFieldData(preferenceDataEN);
            } 
            
            if (eventType == WebConstants.EVENT_ADD){
                //Guardar Preference Value Cliente
                preferenceValueClient = new PreferenceValue();
                preferenceValueClient.setPreferenceFieldId(preference);
                preferenceValueClient.setPreferenceClassficationId(classificationClient);
                preferenceValueClient.setEnabled(true);
                preferenceValueClient.setCreateDate(new Timestamp(new Date().getTime()));
                preferenceValueClient = preferencesEJB.savePreferenceValue(preferenceValueClient);

                //Guardar Preference Value Business
                preferenceValueBusiness = new PreferenceValue();
                preferenceValueBusiness.setPreferenceFieldId(preference);
                preferenceValueBusiness.setPreferenceClassficationId(classificationBussines);
                preferenceValueBusiness.setEnabled(true);
                preferenceValueBusiness.setCreateDate(new Timestamp(new Date().getTime()));
                preferenceValueBusiness = preferencesEJB.savePreferenceValue(preferenceValueBusiness);
            }
            
            this.showMessage("sp.common.save.success", false, null);
            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
            } else {
                btnSave.setVisible(true);
            }
           
        } catch (Exception ex) {
            showError(ex);
        } 
    }

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    if(validatePreferenceCode()){
                      savePreferenceField(null);  
                    }
                    break;
                case WebConstants.EVENT_EDIT:
                    savePreferenceField(preferenceFieldParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(preferenceFieldParam);
                loadCmbTypePreference(eventType);
                loadCmbTypeData(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(preferenceFieldParam);
                blockFields();
                loadCmbTypePreference(eventType);
                loadCmbTypeData(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbTypePreference(eventType);
                loadCmbTypeData(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbTypePreference(Integer evenInteger){
        //cmbTypePreference
        EJBRequest request1 = new EJBRequest();
        List<Preference> preference;
        try {
            preference = preferencesEJB.getPreference(request1);
            loadGenericCombobox(preference, cmbTypePreference, "name", evenInteger, Long.valueOf(preferenceFieldParam != null ? preferenceFieldParam.getPreferenceId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (RegisterNotFoundException ex) {
        }
    }

    private void loadCmbTypeData(Integer evenInteger) {
        //cmbTypeData
        EJBRequest request1 = new EJBRequest();
        List<PreferenceType> preferenceTypes;
        try {
            preferenceTypes = preferencesEJB.getPreferenceTypes(request1);
            loadGenericCombobox(preferenceTypes, cmbTypeData, "type", evenInteger, Long.valueOf(preferenceFieldParam != null ? preferenceFieldParam.getPreferenceTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (RegisterNotFoundException ex) {
        }
    }
}
