package com.alodiga.wallet.admin.web.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractController;
import com.alodiga.wallet.admin.web.generic.controllers.GenericSPController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PreferenceManager;
import com.alodiga.wallet.common.model.PreferenceClassification;
import com.alodiga.wallet.common.model.PreferenceControl;
import com.alodiga.wallet.common.model.PreferenceField;
import com.alodiga.wallet.common.model.PreferenceFieldData;
import com.alodiga.wallet.common.model.PreferenceFieldEnum;
import com.alodiga.wallet.common.model.PreferenceTypeValuesEnum;
import com.alodiga.wallet.common.model.PreferenceValue;
import com.alodiga.wallet.common.model.Provider;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class AdminSettingsController extends GenericAbstractController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Long languageId;   
    private Button btnSave;
    private Combobox cmbClassification;
    private Long DEFAULT_SMS_PROVIDER_ID  = PreferenceFieldEnum.DEFAULT_SMS_PROVIDER_ID.getId();
    private PreferencesEJB preferencesEJB = null;
    private ProductEJB productEJB = null;
    List<PreferenceValue> preferenceValues = null;
    private User user=null;
    private Rows rowsGrid;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();

    }

    @Override
    public void initialize() {
        try {
            preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            languageId = AccessControl.getLanguage();
            user = AccessControl.loadCurrentUser();
            loadPreferenceClassifications();
            onChange$cmbClassification();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadPreferenceClassifications() {
        List<PreferenceClassification> classifications = null;
        try {
        	cmbClassification.getItems().clear();
        	request = new EJBRequest();
        	classifications = (List<PreferenceClassification>) preferencesEJB.getPreferenceClassifications(request);
            for (PreferenceClassification e : classifications) {
                Comboitem cmbItem = new Comboitem();
                cmbItem.setLabel(e.getName());
                cmbItem.setValue(e);
                cmbItem.setParent(cmbClassification);
            }
            cmbClassification.setSelectedIndex(0);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    private void loadProviders(Long providerId,Combobox cmbDefaultSMSProvider) {
        try {
            List<Provider> providers = new ArrayList<Provider>();
            EJBRequest r = new EJBRequest();
            r.setLimit(1000);
            providers = productEJB.getSMSProviders(r);
            cmbDefaultSMSProvider.getItems().clear();
            for (Provider provider : providers) {
                Comboitem item = new Comboitem();
                item.setValue(provider);
                item.setLabel(provider.getName());
                item.setParent(cmbDefaultSMSProvider);
                if (providerId.equals(provider.getId())) {
                        cmbDefaultSMSProvider.setSelectedItem(item);
                }
            }
        } catch (Exception ex) {
                showError(ex);
        }
    }
    
    public boolean validateEmpty() {
        List<Component> childrens = rowsGrid.getChildren();
        int i=0;
        boolean valid = false;
        for(Component row: childrens) {
            Row r = (Row) childrens.get(i++);
            List<Component> children = r.getChildren();
            if (children.get(1) instanceof Combobox) {   		
                if (((Combobox) children.get(1)).getSelectedIndex() == -1 ) {
                        ((Combobox) children.get(1)).setFocus(true);
                        this.showMessage("sp.error.smsprovider.notSelected", true, null);
                        valid = false;
                        break;
                }		
            }else if (children.get(1) instanceof Textbox) {
                if (((Textbox)children.get(1)).getText().isEmpty()) {
                        ((Textbox)children.get(1)).setFocus(true);
                        this.showMessage("sp.error.field.cannotNull", true, null); 
                        valid = false;
                        break;
                }    
            } else {
                valid = true;
            }
        }
        return valid;
    }

    public void onChange$cmbClassification() throws InterruptedException {
        this.clearMessage();
        PreferenceClassification preferenceClassification = null;
        if (cmbClassification.getSelectedItem() != null) {
            preferenceClassification = (PreferenceClassification) cmbClassification.getSelectedItem().getValue();
            loadPreferences(preferenceClassification.getId());
        }
    }

    private void loadPreferences(Long classificationId) {
        try {
            rowsGrid.getChildren().clear();
            EJBRequest request1 = new EJBRequest();
            List<PreferenceField> fields = preferencesEJB.getPreferenceFields(request1);
            preferenceValues = new ArrayList<PreferenceValue>();
            for (PreferenceField field : fields) {
            	PreferenceValue pValue = null;
            	try {
            		pValue = preferencesEJB.loadActivePreferenceValuesByClassificationIdAndFieldId(classificationId, field.getId());
            	} catch (Exception e) {
					pValue = null;
				}
            	if (pValue != null) {
	                Row row = new Row();
	                row.setId(pValue.getId().toString());
	                Label label = new Label();
	                try {
                    	label.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    } catch (Exception e) {
                    	label.setValue(getPreferenceDataLabel(field.getId()));
                    }
	                label.setParent(row);
	                if (field.getId().equals(DEFAULT_SMS_PROVIDER_ID)) {
	                	Combobox cmbbox = new Combobox();
	                	loadProviders(Long.parseLong(pValue.getValue()),cmbbox);
	                	cmbbox.setParent(row);
	                	Datebox beginninDate = new Datebox();
	                	beginninDate.setFormat("dd-MM-yyyy");
	                	beginninDate.setValue(pValue.getBeginningDate());
	                	beginninDate.setParent(row);
	                	Hlayout hlayout = new Hlayout();
	                	Checkbox endCheck = new Checkbox();
	                	endCheck.addEventListener("onClick", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                              changeEndinningDate(hlayout);
                            }
                        });
	                	endCheck.setChecked(pValue.getEndingDate()!=null?true:false);
	                	endCheck.setParent(hlayout);
	                	Datebox endinngDate = new Datebox();
	                	endinngDate.setFormat("dd-MM-yyyy");
	                	endinngDate.setValue(pValue.getEndingDate()!=null?pValue.getEndingDate():new Timestamp(new Date().getTime()));
	                	if (endCheck.isChecked()) {
	                		endinngDate.setVisible(true);
	                	}else{
	                		endinngDate.setVisible(false);
	                	}	                	
	                	endinngDate.setParent(hlayout);	  
	                	hlayout.setParent(row);
	                	Label labelType = new Label();
	                	labelType.setValue(field.getPreferenceId().getName());
	                	labelType.setParent(row);
	                	Checkbox percentage = new Checkbox();
                                if (pValue.getIsPercentage() != null) {
                                    percentage.setChecked(pValue.getIsPercentage());
                                }	                	
	                	percentage.setParent(row);
	                	Checkbox chb = new Checkbox();
	                	chb.setChecked(pValue.isEnabled());
	                	chb.setParent(row);
	                	preferenceValues.add(pValue);
	                } else if (field.getPreferenceTypeId().getId().equals(PreferenceTypeValuesEnum.BOOLEAN.getValue())) {
	                	Checkbox chbValue = new Checkbox();
	                	boolean checked = Integer.parseInt(pValue.getValue()!=null?pValue.getValue():"0") == 1 ? true : false;
	                	chbValue.setChecked(checked);
	                	chbValue.setParent(row);
	                	Datebox beginninDate = new Datebox();
	                	beginninDate.setFormat("dd-MM-yyyy");
	                	beginninDate.setValue(pValue.getBeginningDate());
	                	beginninDate.setParent(row);
	                	Hlayout hlayout = new Hlayout();
	                	Checkbox endCheck = new Checkbox();
	                	endCheck.addEventListener("onClick", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                              changeEndinningDate(hlayout);
                            }
                        });
	                	endCheck.setChecked(pValue.getEndingDate()!=null?true:false);
	                	endCheck.setParent(hlayout);
	                	Datebox endinngDate = new Datebox();
	                	endinngDate.setFormat("dd-MM-yyyy");
	                	endinngDate.setValue(pValue.getEndingDate()!=null?pValue.getEndingDate():new Timestamp(new Date().getTime()));
	                	if (endCheck.isChecked()) {
	                		endinngDate.setVisible(true);
	                	}else{
	                		endinngDate.setVisible(false);
	                	}	                	
	                	endinngDate.setParent(hlayout);	  
	                	hlayout.setParent(row);
	                	Label labelType = new Label();
	                	labelType.setValue(field.getPreferenceId().getName());
	                	labelType.setParent(row);
	                	Checkbox percentage = new Checkbox();
                                if (pValue.getIsPercentage() != null) {
                                    percentage.setChecked(pValue.getIsPercentage());
                                }	                	
	                	percentage.setParent(row);
	                	Checkbox chb = new Checkbox();
	                	chb.setChecked(pValue.isEnabled());
	                	chb.setParent(row);
	                	preferenceValues.add(pValue);
	                }  else {
	                	Textbox txtValue = new Textbox();
	                	txtValue.setText(pValue.getValue()!=null?pValue.getValue():"");
	                	txtValue.setParent(row);
	                	Datebox beginninDate = new Datebox();
	                	beginninDate.setFormat("dd-MM-yyyy");
	                	beginninDate.setValue(pValue.getBeginningDate());
	                	beginninDate.setParent(row);
	                	Hlayout hlayout = new Hlayout();
	                	Checkbox endCheck = new Checkbox();
	                	endCheck.addEventListener("onClick", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                              changeEndinningDate(hlayout);
                            }
                        });
	                	endCheck.setChecked(pValue.getEndingDate()!=null?true:false);
	                	endCheck.setParent(hlayout);
	                	Datebox endinngDate = new Datebox();
	                	endinngDate.setFormat("dd-MM-yyyy");
	                	endinngDate.setValue(pValue.getEndingDate()!=null?pValue.getEndingDate():new Timestamp(new Date().getTime()));
	                	if (endCheck.isChecked()) {
	                		endinngDate.setVisible(true);
	                	}else{
	                		endinngDate.setVisible(false);
	                	}	                	
	                	endinngDate.setParent(hlayout);	                		
	                	hlayout.setParent(row);
	                	Label labelType = new Label();
	                	labelType.setValue(field.getPreferenceId().getName());
	                	labelType.setParent(row);
	                	Checkbox percentage = new Checkbox();
                                if (pValue.getIsPercentage() != null) {
                                    percentage.setChecked(pValue.getIsPercentage());
                                }	                	
	                	percentage.setParent(row);
	                	Checkbox chb = new Checkbox();
	                	chb.setChecked(pValue.isEnabled());
	                	chb.setParent(row);
	                	preferenceValues.add(pValue);
	                }
	                row.setParent(rowsGrid);   
            	}

            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void changeEndinningDate(Hlayout hlayout) {
     	List<Component> children = hlayout.getChildren();
     	if (((Checkbox)children.get(0)).isChecked()){
	     	((Datebox)children.get(1)).setVisible(true);
     	}else {
     		((Datebox)children.get(1)).setVisible(false);
     	}
    }

    private void savePreferenceValues() {
        try {
            List<PreferenceValue> preferenceValueSave = new ArrayList<PreferenceValue>();
            List<PreferenceControl>  preferenceControls = new ArrayList<PreferenceControl>();
            List<Component> children = rowsGrid.getChildren();
            int i=0;
            for(PreferenceValue pvalue: preferenceValues) {
            	preferenceControls.add(createPreferencenControl(pvalue));
            	Row r = (Row) children.get(i++);
            	preferenceValueSave.add(updatePreferencenValue(pvalue,r));         	      
            }
            preferencesEJB.savePreferenceValues(preferenceValueSave,preferenceControls);
            PreferenceManager preferenceManager = PreferenceManager.getInstance();
            preferenceManager.refresh();
            this.showMessage("sp.common.save.success", false, null);
        } catch (GeneralException ex) {
            //showError(ex);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private PreferenceControl createPreferencenControl(PreferenceValue preferenceValue) {
    	PreferenceControl preferenceControl = new PreferenceControl();
    	preferenceControl.setCreationDate(new Date());
    	preferenceControl.setParamOld(preferenceValue.getValue());
    	preferenceControl.setPreferenceValueId(preferenceValue);
    	preferenceControl.setUserId(user.getId());
    	return preferenceControl;
    }
    
    private PreferenceValue updatePreferencenValue(PreferenceValue preferenceValue,Row r) throws GeneralException {
    	List<Component> children = r.getChildren();
    	String value = "";
    	if (children.get(1) instanceof Combobox) {
            Provider provider = (Provider)((Combobox) children.get(1)).getSelectedItem().getValue();
            value = provider.getId().toString();
    	}else if (children.get(1) instanceof Textbox)
            value = ((Textbox)children.get(1)).getText();
    	else if (children.get(1) instanceof Checkbox)	
            value = ((Checkbox)children.get(1)).isChecked()?"1":"0";
    	preferenceValue.setValue(value);
    	preferenceValue.setBeginningDate(((Datebox)children.get(2)).getValue());
    	if (children.get(3) instanceof Hlayout) {
    		List<Component> childrenHlayout = ((Hlayout)children.get(3)).getChildren();
    		if (((Checkbox)childrenHlayout.get(0)).isChecked()) {
    			if (((Datebox)childrenHlayout.get(1)).getValue().getTime() >= ((Datebox)children.get(2)).getValue().getTime()) {
    				preferenceValue.setEndingDate(((Datebox)childrenHlayout.get(1)).getValue()); 			
    			} else {
    				this.showMessage("sp.error.date.invalid", true, null);
    				throw new GeneralException("Error Invalid Dates");
    			}
    		}else
    			preferenceValue.setEndingDate(null);
    	}
    	preferenceValue.setIsPercentage(((Checkbox)children.get(5)).isChecked());
    	preferenceValue.setEnabled(((Checkbox)children.get(6)).isChecked());
    	preferenceValue.setUpdateDate(new Timestamp(new Date().getTime()));
    	
    	
    	return preferenceValue;
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            savePreferenceValues();
        }

    }

    @SuppressWarnings("unchecked")
    public String getPreferenceDataLabel(Long preferenceFieldId) {
    	EJBRequest request = new EJBRequest();
        Map params = new HashMap();
        params.put(Constants.PREFERENCE_FIELD_KEY, preferenceFieldId);
        request.setParams(params);
        try {
        	List<PreferenceFieldData> preferenceDataList = preferencesEJB.getPreferenceFieldDataByPreference(request);
        	if (preferenceDataList != null) {
        		for (PreferenceFieldData p : preferenceDataList) {
        			if (p.getLanguage().getId().equals(languageId)) {
        				return p.getDescription();
        			}
        		}
        	}
        }  catch (Exception ex) {      	
        }
        return null;
    }
}
