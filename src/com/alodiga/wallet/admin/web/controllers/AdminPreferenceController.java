package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hlayout;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
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
    private Checkbox chkCustomer;
    private Checkbox chkBusiness;
    private Row rowCustomer;
    private Row rowCustomerBeginning;
    private Row rowCustomerEnding;
    private Row rowBusiness;
    private Row rowBusinessBeginning;
    private Row rowBusinessEnding;
    private Datebox dtxCustomerBeginning;
    private Datebox dtxCustomerEnding;
    private Datebox dtxBusinessBeginning;
    private Datebox dtxBusinessEnding;
    private Textbox txtValueCustomer;
    private Textbox txtValueBusiness;
    private Checkbox chkCustomerEnding;
    private Checkbox chkBusinessEnding;
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
            dtxCustomerBeginning.setValue(new Timestamp(new Date().getTime()));
            dtxCustomerEnding.setValue(new Timestamp(new Date().getTime()));
            dtxBusinessBeginning.setValue(new Timestamp(new Date().getTime()));
            dtxBusinessEnding.setValue(new Timestamp(new Date().getTime()));          
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
                //Se obtienen las clasificaciones para el Preference value
                EJBRequest request1 = new EJBRequest();
                request1.setParam(Constants.CLIENT_CLASSIFICATION);
                PreferenceClassification classificationClient = preferencesEJB.loadPreferenceClassification(request1);
                
                request1 = new EJBRequest();
                request1.setParam(Constants.BUSINESS_CLASSIFICATION);
                PreferenceClassification classificationBussines = preferencesEJB.loadPreferenceClassification(request1);
                PreferenceValue pValueClient = null;
            	try {
            		pValueClient = preferencesEJB.loadActivePreferenceValuesByClassificationIdAndFieldId(classificationClient.getId(), preferenceField.getId());
            		chkCustomer.setChecked(pValueClient.isEnabled()); 
            		txtValueCustomer.setText(pValueClient.getValue());
        	    	rowCustomer.setVisible(true);
        	    	rowCustomerBeginning.setVisible(true);
        	    	dtxCustomerBeginning.setValue(pValueClient.getBeginningDate());
        	    	if (pValueClient.getEndingDate()!=null) {
        	    		chkCustomerEnding.setChecked(pValueClient.getEndingDate()!=null);
        	    		dtxCustomerEnding.setVisible(true);
        	    		dtxCustomerEnding.setValue(pValueClient.getEndingDate());
        	    	}
        	    	rowCustomerEnding.setVisible(true);
            	} catch (Exception e) {
            		chkCustomer.setChecked(false); 
            		txtValueCustomer.setText("");
        	    	rowCustomer.setVisible(false);
        	    	rowCustomerBeginning.setVisible(false);
        	    	rowCustomerEnding.setVisible(false);
            	}
            	PreferenceValue pValueBusiness = null;
            	try {
            		pValueBusiness = preferencesEJB.loadActivePreferenceValuesByClassificationIdAndFieldId(classificationBussines.getId(), preferenceField.getId());
            		chkBusiness.setChecked(pValueBusiness.isEnabled()); 
            		txtValueBusiness.setText(pValueBusiness.getValue());
        	    	rowBusiness.setVisible(true);
        	    	rowBusinessBeginning.setVisible(true);
        	    	dtxBusinessBeginning.setValue(pValueBusiness.getBeginningDate());
        	    	if (pValueBusiness.getEndingDate()!=null) {
        	    		chkBusinessEnding.setChecked(pValueBusiness.getEndingDate()!=null);
        	    		dtxBusinessEnding.setValue(pValueBusiness.getEndingDate());
        	    		dtxBusinessEnding.setVisible(true);
        	    	}
        	    	rowBusinessEnding.setVisible(true);
            	} catch (Exception e) {
            		chkBusiness.setChecked(false);
            		txtValueBusiness.setText("");
        	    	rowBusiness.setVisible(false);  
        	    	rowBusinessBeginning.setVisible(false);
        	    	rowBusinessEnding.setVisible(false);
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
       txtValueCustomer.setReadonly(true);
       txtValueBusiness.setReadonly(true);
       chkCustomer.setDisabled(true);
       chkBusiness.setDisabled(true);
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
        } else if (!chkCustomer.isChecked() && !chkBusiness.isChecked()) {
        	chkCustomer.setFocus(true);
        	this.showMessage("sp.error.preferences.checked", true, null);
        }else {
            return true;
        }
        return false;

    }
    
    public boolean validatePreferenceCode() {
        PreferenceField preferenceField = null;
        preferenceFieldList.clear();
        try{
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PARAM_CODE, txtCode.getText());
            request.setParams(params);
            preferenceFieldList = preferencesEJB.getPreferenceFieldsByCode(request);
        } catch (Exception ex) {
            showError(ex);
        } if(preferenceFieldList.size() > 0){
            this.showMessage("sp.error.preferences.code", true, null);
                txtCode.setFocus(true);
                return false;
        }
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
    		if (chkBusinessEnding.isChecked() && dtxBusinessBeginning.getValue().getTime() >= dtxBusinessEnding.getValue().getTime()) {
				this.showMessage("sp.error.date.invalid", true, null);
				throw new GeneralException("Error Invalid Dates");
			}
    		if (chkCustomerEnding.isChecked() && dtxCustomerBeginning.getValue().getTime() >= dtxCustomerEnding.getValue().getTime()) {
				this.showMessage("sp.error.date.invalid", true, null);
				throw new GeneralException("Error Invalid Dates");
			}
            //Se obtienen los lenguajes Español e Ingles
            //Ingles
            EJBRequest request4 = new EJBRequest();
            request4.setParam(Constants.SPANISH_LANGUAGE);
            languageES  = utilsEJB.loadLanguage(request4);
            //Español
            request4 = new EJBRequest();
            request4.setParam(Constants.ENGLISH_LANGUAGE);
            languageEN = utilsEJB.loadLanguage(request4);
            
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
                    preferenceDataES.setLanguage(languageEN);
                    preferenceDataES.setDescription(txtDescription.getText());
                    preferenceDataES = preferencesEJB.savePreferenceFieldData(preferenceDataES);
                    //Guardar descripcion en ingles en PreferenceFieldData
                    preferenceDataEN.setPreferenceField(preference);
                    preferenceDataEN.setLanguage(languageES);
                    preferenceDataEN.setDescription(txtDescriptionEnglish.getText());
                    preferenceDataEN = preferencesEJB.savePreferenceFieldData(preferenceDataEN);
                    
                    if (chkCustomer.isChecked()) {
                    	PreferenceValue pValueClient = null;
                    	try {
                    		pValueClient = preferencesEJB.loadActivePreferenceValuesByClassificationIdAndFieldId(classificationClient.getId(), preference.getId());
                    		pValueClient.setValue(txtValueCustomer.getText());
                    		pValueClient.setBeginningDate(dtxCustomerBeginning.getValue());
                    		if (chkCustomerEnding.isChecked())
                    			pValueClient.setEndingDate(dtxCustomerEnding.getValue());
                    		pValueClient.setUpdateDate(new Timestamp(new Date().getTime()));
                    		pValueClient = preferencesEJB.savePreferenceValue(pValueClient); 
                    	} catch (Exception e) {
                    		pValueClient = new PreferenceValue();
                    		pValueClient.setPreferenceFieldId(preference);
                    		pValueClient.setPreferenceClassficationId(classificationClient);
                    		pValueClient.setCreateDate(new Timestamp(new java.util.Date().getTime()));
                    		pValueClient.setEnabled(true);
                    		pValueClient.setCreateDate(new Timestamp(new Date().getTime()));
                    		pValueClient.setValue(txtValueCustomer.getText());
                    		pValueClient.setBeginningDate(dtxCustomerBeginning.getValue());
                    		if (chkCustomerEnding.isChecked())
                    			pValueClient.setEndingDate(dtxCustomerEnding.getValue());
                    		pValueClient = preferencesEJB.savePreferenceValue(pValueClient);               	
        				}
                    }
                    if (chkBusiness.isChecked()) {
                    	PreferenceValue pValueBusiness = null;
                    	try {
                    		pValueBusiness = preferencesEJB.loadActivePreferenceValuesByClassificationIdAndFieldId(classificationBussines.getId(), preference.getId());
                    		pValueBusiness.setValue(txtValueBusiness.getText());
                    		pValueBusiness.setBeginningDate(dtxBusinessBeginning.getValue());
                    		if (chkBusinessEnding.isChecked())
                    			pValueBusiness.setEndingDate(dtxBusinessEnding.getValue());
                    		pValueBusiness.setUpdateDate(new Timestamp(new Date().getTime()));
                    		pValueBusiness = preferencesEJB.savePreferenceValue(pValueBusiness);               	              	
                    	} catch (Exception e) {
                    		pValueBusiness = new PreferenceValue();
                    		pValueBusiness.setPreferenceFieldId(preference);
                    		pValueBusiness.setPreferenceClassficationId(classificationBussines);
                    		pValueBusiness.setCreateDate(new Timestamp(new java.util.Date().getTime()));
                    		pValueBusiness.setEnabled(true);
                    		pValueBusiness.setCreateDate(new Timestamp(new Date().getTime()));
                    		pValueBusiness.setValue(txtValueBusiness.getText());
                    		pValueBusiness.setBeginningDate(dtxBusinessBeginning.getValue());
                    		if (chkBusinessEnding.isChecked())
                    			pValueBusiness.setEndingDate(dtxBusinessEnding.getValue());
                    		pValueBusiness = preferencesEJB.savePreferenceValue(pValueBusiness);               	              	
        				}
                    }
                }
            } else if (eventType == WebConstants.EVENT_ADD) {
                    //Guardar descripcion en español en PreferenceFieldData
                    preferenceDataES = new PreferenceFieldData();
                    preferenceDataES.setPreferenceField(preference);
                    preferenceDataES.setLanguage(languageEN);
                    preferenceDataES.setDescription(txtDescription.getText());
                    preferenceDataES = preferencesEJB.savePreferenceFieldData(preferenceDataES);
                    //Guardar descripcion en ingles en PreferenceFieldData
                    preferenceDataEN = new PreferenceFieldData();
                    preferenceDataEN.setPreferenceField(preference);
                    preferenceDataEN.setLanguage(languageES);
                    preferenceDataEN.setDescription(txtDescriptionEnglish.getText());
                    preferenceDataEN = preferencesEJB.savePreferenceFieldData(preferenceDataEN);
            } 
            
            if (eventType == WebConstants.EVENT_ADD){
                //Guardar Preference Value Cliente
                if (chkCustomer.isChecked()) {
                	preferenceValueClient = new PreferenceValue();
                	preferenceValueClient.setPreferenceFieldId(preference);
                	preferenceValueClient.setPreferenceClassficationId(classificationClient);
                	preferenceValueClient.setCreateDate(new Timestamp(new java.util.Date().getTime()));
                	preferenceValueClient.setEnabled(true);
                	preferenceValueClient.setCreateDate(new Timestamp(new Date().getTime()));
                	preferenceValueClient.setValue(txtValueCustomer.getText());
                	preferenceValueClient.setBeginningDate(dtxCustomerBeginning.getValue());
                	if (chkCustomerEnding.isChecked())
                		preferenceValueClient.setEndingDate(dtxCustomerEnding.getValue());
                	preferenceValueClient = preferencesEJB.savePreferenceValue(preferenceValueClient);               	
                }

                //Guardar Preference Value Business
                if (chkBusiness.isChecked()) {
                	preferenceValueBusiness = new PreferenceValue();
                	preferenceValueBusiness.setPreferenceFieldId(preference);
                	preferenceValueBusiness.setPreferenceClassficationId(classificationBussines);
                	preferenceValueBusiness.setCreateDate(new Timestamp(new java.util.Date().getTime()));
                	preferenceValueBusiness.setEnabled(true);
                	preferenceValueBusiness.setCreateDate(new Timestamp(new Date().getTime()));
                	preferenceValueBusiness.setValue(txtValueBusiness.getText());
                	preferenceValueBusiness.setBeginningDate(dtxBusinessBeginning.getValue());
                	if (chkBusinessEnding.isChecked())
                		preferenceValueBusiness.setEndingDate(dtxBusinessEnding.getValue());
                	preferenceValueBusiness = preferencesEJB.savePreferenceValue(preferenceValueBusiness);               	
                }
            }
            
            this.showMessage("sp.common.save.success", false, null);
            if (eventType == WebConstants.EVENT_ADD) {
                btnSave.setVisible(false);
            } else {
                btnSave.setVisible(true);
            }
        } catch (GeneralException ex) {
 
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
    
    public void onClick$chkCustomer(){
	     if (chkCustomer.isChecked()) {
	    	 txtValueCustomer.setText("");
	    	 rowCustomer.setVisible(true);
	    	 rowCustomerBeginning.setVisible(true);
	    	 rowCustomerEnding.setVisible(true);
	     }else {
	    	 rowCustomer.setVisible(false);
	    	 rowCustomerBeginning.setVisible(false);
	    	 rowCustomerEnding.setVisible(false);
	     }
    }
    
    public void onClick$chkBusiness(){
	     if (chkBusiness.isChecked()) {
	    	 txtValueBusiness.setText("");
	    	 rowBusiness.setVisible(true);
	    	 rowBusinessBeginning.setVisible(true);
	    	 rowBusinessEnding.setVisible(true);
	     }else {
	    	 rowBusiness.setVisible(false);
	    	 rowBusinessBeginning.setVisible(false);
	    	 rowBusinessEnding.setVisible(false);
	     }
   }

    
    public void onClick$chkCustomerEnding() {
     	if (chkCustomerEnding.isChecked()) {
     		dtxCustomerEnding.setVisible(true);
     	}else {
     		dtxCustomerEnding.setVisible(false);
     	}
    }
    
    public void onClick$chkBusinessEnding() {
     	if (chkBusinessEnding.isChecked()) {
     		dtxBusinessEnding.setVisible(true);
     	}else {
     		dtxBusinessEnding.setVisible(false);
     	}
    }
}
