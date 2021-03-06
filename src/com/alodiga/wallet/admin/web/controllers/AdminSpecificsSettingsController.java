package com.alodiga.wallet.admin.web.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
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
import com.alodiga.wallet.common.ejb.BusinessEJB;
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
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Provider;
import com.alodiga.wallet.common.model.TransactionType;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.portal.business.commons.models.Business;

public class AdminSpecificsSettingsController extends GenericAbstractController implements GenericSPController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Long languageId;
   
    private Button btnSave;
    private Combobox cmbDefaultSMSProvider;
    private Long DEFAULT_SMS_PROVIDER_ID;
    private PreferencesEJB preferencesEJB = null;
    private ProductEJB productEJB = null;
    List<PreferenceValue> preferenceValues = null;
    private User user=null;
    private PreferenceValue preferenceValueParam;
    private Combobox cmbProduct;
    private Combobox cmbTransactionType;
    private Combobox cmbBusiness;
    private BusinessEJB businessEJB;
    private PreferenceClassification preferenceClassification = null;
    private Rows rowsGrid;


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        preferenceValueParam = (Sessions.getCurrent().getAttribute("object") != null) ? (PreferenceValue) Sessions.getCurrent().getAttribute("object") : null;
        initialize();
    }

    @Override
    public void initialize() {
        try {
        	preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
        	productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
        	businessEJB = (BusinessEJB) EJBServiceLocator.getInstance().get(EjbConstants.BUSINESS_EJB);
        	languageId = AccessControl.getLanguage();
        	user = AccessControl.loadCurrentUser();
    		EJBRequest request = new EJBRequest();
    		request.setParam(Constants.PREFERENCE_CLASSIFICATION_BUSINESS);
    		preferenceClassification = preferencesEJB.loadPreferenceClassification(request);
    		loadData();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void blockFields() {
    	cmbProduct.setDisabled(true);
    	cmbTransactionType.setDisabled(true);
    	cmbBusiness.setDisabled(true);
        cmbDefaultSMSProvider.setReadonly(true);
        btnSave.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    public boolean validateEmpty() {
        if (cmbProduct.getSelectedIndex() == -1) {
        	cmbProduct.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
        } else if (cmbTransactionType.getSelectedIndex() == -1) {
        	cmbTransactionType.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
        }  else if (cmbBusiness.getSelectedIndex() == -1) {
        	cmbBusiness.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
        }
        List<Component> childrens = rowsGrid.getChildren();
        int i=0;
        boolean valid = false;
        for(Component row: childrens) {
        	Row r = (Row) childrens.get(i++);
        	List<Component> children = r.getChildren();
        	if (children.get(1) instanceof Combobox) {   		
        		if (((Combobox) children.get(1)).getSelectedIndex() == -1 ) {
        			((Combobox) children.get(1)).setFocus(true);
        			this.showMessage("msj.error.smsprovider.notSelected", true, null);
        			valid = false;
        			break;
        		}		
        	}else if (children.get(1) instanceof Textbox) {
        		if (((Textbox)children.get(1)).getText().isEmpty()) {
        			((Textbox)children.get(1)).setFocus(true);
        			this.showMessage("msj.error.field.cannotNull", true, null); 
        			valid = false;
        			break;

                } 
        	} else {
        		valid = true;
        	}
        }
        return valid;
    }


    private void loadPreferences(Long classificationId) {
        try {
            setData();
            preferenceValues = new ArrayList<PreferenceValue>();
            rowsGrid.getChildren().clear();
            List<PreferenceField> fields = preferencesEJB.getPreferenceFieldsByPreferenceId(Constants.PREFERENCE_TRANSACTION_ID);
            preferenceValues = new ArrayList<PreferenceValue>();
            for (PreferenceField field : fields) {
            	 PreferenceValue pValue = null;
            	try {
                pValue = preferencesEJB.loadActivePreferenceValuesByClassificationIdAndFieldId(classificationId, field.getId());
            	} catch (Exception ex) {
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
	                	cmbbox.addEventListener("onChange", new EventListener() {
                             public void onEvent(Event event) throws Exception {
                                 changeStatus(row);
                             }
                        });
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
 	                	Checkbox percentage = new Checkbox();
                                if (pValue.getIsPercentage() != null) {
                                    percentage.setChecked(pValue.getIsPercentage());
                                }
 	                	percentage.setParent(row);
	                	Checkbox chb = new Checkbox();
	                	chb.setChecked(false);
	                	chb.addEventListener("onClick", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                            	changeSpecific(row);
                            }
                        });
	                	chb.setParent(row);
	                	Checkbox chbEnabled = new Checkbox();
                        chbEnabled.setChecked(false);
                        chb.addEventListener("onClick", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                            	changeEnabled(row);
                            }
                        });
                        chbEnabled.setParent(row);
	                	preferenceValues.add(createPreferencenValue(pValue));
	                } else if (field.getPreferenceTypeId().getId().equals(PreferenceTypeValuesEnum.BOOLEAN.getValue())) {
	                	Checkbox chbValue = new Checkbox();
	                	boolean checked = Integer.parseInt(pValue.getValue()!=null?pValue.getValue():"0") == 1 ? true : false;
	                	chbValue.setChecked(checked);
	                	chbValue.addEventListener("onClick", new EventListener() {
                             public void onEvent(Event event) throws Exception {
                                 changeStatus(row);
                             }
                        });
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
 	                	Checkbox percentage = new Checkbox();
                                if (pValue.getIsPercentage() != null) {
                                    percentage.setChecked(pValue.getIsPercentage());
                                } 	                	
 	                	percentage.setParent(row);
	                	Checkbox chb = new Checkbox();
	                	chb.setChecked(false);
	                	chb.addEventListener("onClick", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                            	changeSpecific(row);
                            }
                        });
	                	chb.setParent(row);
	                	Checkbox chbEnabled = new Checkbox();
                        chbEnabled.setChecked(false);
                        chbEnabled.addEventListener("onClick", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                            	changeEnabled(row);
                            }
                        });
                        chbEnabled.setParent(row);
	                	preferenceValues.add(createPreferencenValue(pValue));
	                }  else {
	                	Textbox txtValue = new Textbox();
	                	txtValue.setText(pValue.getValue()!=null?pValue.getValue():"");
	                	txtValue.addEventListener("onChange", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                                changeStatus(row);
                            }
                        });
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
 	                	Checkbox percentage = new Checkbox();
                                if (pValue.getIsPercentage() != null) {
                                    percentage.setChecked(pValue.getIsPercentage());
                                }
 	                	percentage.setParent(row);
	                	Checkbox chb = new Checkbox();
	                	chb.setChecked(false);
	                	chb.addEventListener("onClick", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                            	changeSpecific(row);
                            }
                        });
	                	chb.setParent(row);
	                	Checkbox chbEnabled = new Checkbox();
                        chbEnabled.setChecked(false);
                        chbEnabled.addEventListener("onClick", new EventListener() {
                            public void onEvent(Event event) throws Exception {
                            	changeEnabled(row);
                            }
                        });
                        chbEnabled.setParent(row);
	                	preferenceValues.add(createPreferencenValue(pValue));
	                }
	                row.setParent(rowsGrid);  
              }
            }
        } catch (Exception ex) {
            showError(ex);
        }

    }
    
    private void loadPreferencesByParam(PreferenceValue preferenceValue,Long classificationId, boolean readOnly) {
        try {
            setData();  
            preferenceValues = new ArrayList<PreferenceValue>();
            rowsGrid.getChildren().clear();
            List<PreferenceField> fields = preferencesEJB.getPreferenceFieldsByPreferenceId(Constants.PREFERENCE_TRANSACTION_ID);
            preferenceValues = new ArrayList<PreferenceValue>();
            PreferenceValue pValue = null;
            for (PreferenceField field : fields) {
                try {
                    pValue = preferencesEJB.getPreferenceValuesByParamAndBussiness(preferenceValue.getPreferenceClassficationId().getId(),
                    preferenceValue.getProductId().getId(), preferenceValue.getTransactionTypeId().getId(),preferenceValue.getBussinessId(), field.getId());
                } catch (Exception ex) {
                    try {
                        pValue = preferencesEJB.loadActivePreferenceValuesByClassificationIdAndFieldId(classificationId,field.getId());
                        pValue = createPreferencenValue(pValue);
                    } catch (Exception e) {
                        pValue = null;
                    }
                }
                if (pValue != null) {
                    Row row = new Row();
                    Label label = new Label();
                    try {
                    	label.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    } catch (Exception e) {
                    	label.setValue(getPreferenceDataLabel(field.getId()));
                    }
                    label.setParent(row);
                    if (pValue.getPreferenceFieldId().getId().equals(DEFAULT_SMS_PROVIDER_ID)) {
                            Combobox cmbbox = new Combobox();
                            loadProviders(Long.parseLong(pValue.getValue()), cmbbox);
                            cmbbox.setReadonly(readOnly);
                            cmbbox.addEventListener("onChange", new EventListener() {

                                public void onEvent(Event event) throws Exception {
                                    changeStatus(row);
                                }
                            });
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
    	                	Checkbox percentage = new Checkbox();
                                if (pValue.getIsPercentage() != null) {
                                    percentage.setChecked(pValue.getIsPercentage());
                                }
    	                	percentage.setParent(row);
                            Checkbox chb = new Checkbox();
                            chb.setChecked(pValue.isEnabled());
                            chb.addEventListener("onClick", new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                	changeSpecific(row);
                                }
                            });
                            chb.setDisabled(readOnly);
                            chb.setParent(row);
                            Checkbox chbEnabled = new Checkbox();
                            chbEnabled.setChecked(pValue.isEnabled());
                            chbEnabled.addEventListener("onClick", new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                	changeEnabled(row);
                                }
                            });
                            chbEnabled.setDisabled(readOnly);
                            chbEnabled.setParent(row);
                            preferenceValues.add(pValue);
                    } else if (pValue.getPreferenceFieldId().getPreferenceTypeId().getId().equals(PreferenceTypeValuesEnum.BOOLEAN.getValue())) {
                            Checkbox chbValue = new Checkbox();
                            boolean checked = Integer.parseInt(pValue.getValue()!=null?pValue.getValue():"0") == 1 ? true : false;
                            chbValue.setChecked(checked);
                            chbValue.setDisabled(readOnly);
                            chbValue.addEventListener("onClick", new EventListener() {
                            	
                            	public void onEvent(Event event) throws Exception {
                            		changeStatus(row);
                            	}
                            });
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
    	                	Checkbox percentage = new Checkbox();
    	                	if (pValue.getIsPercentage() != null) {
                                    percentage.setChecked(pValue.getIsPercentage());
                                }
    	                	percentage.setParent(row);
                            Checkbox chb = new Checkbox();
                            chb.setChecked(pValue.isEnabled());
                            chb.addEventListener("onClick", new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                	changeSpecific(row);
                                }
                            });
                            chb.setDisabled(readOnly);
                            chb.setParent(row);
                            Checkbox chbEnabled = new Checkbox();
                            chbEnabled.setChecked(pValue.isEnabled());
                            chbEnabled.addEventListener("onClick", new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                	changeEnabled(row);
                                }
                            });
                            chbEnabled.setDisabled(readOnly);
                            chbEnabled.setParent(row);
                            preferenceValues.add(pValue);
                    } else {
                            Textbox txtValue = new Textbox();
                            txtValue.setText(pValue.getValue()!=null?pValue.getValue():"");
                            txtValue.setParent(row);
                            txtValue.setReadonly(readOnly);
                            txtValue.addEventListener("onChange", new EventListener() {

                                public void onEvent(Event event) throws Exception {
                                    changeStatus(row);
                                }
                            });
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
    	                	Checkbox percentage = new Checkbox();
    	                	if (pValue.getIsPercentage() != null) {
                                    percentage.setChecked(pValue.getIsPercentage());
                                }
    	                	percentage.setParent(row);
                            Checkbox chb = new Checkbox();
                            chb.setChecked(pValue.isEnabled());
                            chb.addEventListener("onClick", new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                	changeSpecific(row);
                                }
                            });
                            chb.setDisabled(readOnly);
                            chb.setParent(row);
                            Checkbox chbEnabled = new Checkbox();
                            chbEnabled.setChecked(pValue.isEnabled());
                            chbEnabled.addEventListener("onClick", new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                	changeEnabled(row);
                                }
                            });
                            chbEnabled.setDisabled(readOnly);
                            chbEnabled.setParent(row);
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
    
    public void setData() {
        DEFAULT_SMS_PROVIDER_ID = PreferenceFieldEnum.DEFAULT_SMS_PROVIDER_ID.getId();
    }

    @SuppressWarnings("unchecked")
    private void savePreferenceValues() {
        try {
        	 boolean save= true;
        	 List<Component> component = rowsGrid.getChildren();
        	 List<PreferenceValue> preferenceValueSave = new ArrayList<PreferenceValue>();
        	 List<PreferenceControl>  preferenceControls = new ArrayList<PreferenceControl>();
             int i=0;
             for(PreferenceValue pvalue: preferenceValues) {
             	Row r = (Row) component.get(i++);
             	List<Component> children = r.getChildren();
            	String value = "";
            	if (children.get(1) instanceof Combobox) {
            		Provider provider = (Provider)((Combobox) children.get(1)).getSelectedItem().getValue();
            		value = provider.getId().toString();
            	}else if (children.get(1) instanceof Textbox)
            		value = ((Textbox)children.get(1)).getText();
            	else if (children.get(1) instanceof Checkbox)	
            		value = ((Checkbox)children.get(1)).isChecked()?"1":"0";
            	
            	boolean check = ((Checkbox)children.get(5)).isChecked() ? true : false;
            	boolean percentage = ((Checkbox)children.get(4)).isChecked() ? true : false;
            	List<Component> childrenHlayout = ((Hlayout)children.get(3)).getChildren();
            	
            	Date endingDate = null;
            	if (((Checkbox)childrenHlayout.get(0)).isChecked())
            		endingDate = ((Datebox)childrenHlayout.get(1)).getValue();
            	if (!pvalue.getValue().equals(value) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=check || pvalue.getIsPercentage()!=percentage || pvalue.getBeginningDate()!=((Datebox)children.get(2)).getValue() || pvalue.getEndingDate()!=endingDate) {
            		if ((children.get(1) instanceof Textbox) && Long.parseLong(((Textbox)children.get(1)).getText())>Long.parseLong(pvalue.getPreferenceValueParentId()!=null?pvalue.getPreferenceValueParentId().getValue():pvalue.getValue())) {
            			((Textbox)children.get(1)).setFocus(true);
            			save= false;
            			this.showMessage("msj.error.field.wrongValue", true, null);        
            			break;
            		}else {
                		preferenceControls.add(createPreferencenControl(pvalue));
                		preferenceValueSave.add(updatePreferencenValue(pvalue,r));           	
                	}
            	
            	}
             }       
             if (save) {
            	preferencesEJB.savePreferenceValues(preferenceValueSave,preferenceControls);
	            PreferenceManager preferenceManager = PreferenceManager.getInstance();
	            preferenceManager.refresh();
	            this.showMessage("wallet.msj.save.success", false, null);
             } 
        } catch (GeneralException ex) {
            //showError(ex);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private PreferenceValue updatePreferencenValue(PreferenceValue preferenceValue,Row r) throws GeneralException{
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
    				this.showMessage("msj.error.date.invalid", true, null);
    				throw new GeneralException("Error Invalid Dates");
    			}
    		}else
    			preferenceValue.setEndingDate(null);
    	}
    	preferenceValue.setIsPercentage(((Checkbox)children.get(4)).isChecked());
    	preferenceValue.setEnabled(((Checkbox)children.get(6)).isChecked());
    	preferenceValue.setUpdateDate(new Timestamp(new Date().getTime()));
    	if(eventType==WebConstants.EVENT_ADD) {
    		preferenceValue.setPreferenceClassficationId(preferenceClassification);
    		preferenceValue.setProductId((Product)cmbProduct.getSelectedItem().getValue());
    		preferenceValue.setTransactionTypeId((TransactionType)cmbTransactionType.getSelectedItem().getValue());
    		preferenceValue.setBussinessId(((Business)cmbBusiness.getSelectedItem().getValue()).getId());
    	}
    	return preferenceValue;
    }
    
    private PreferenceControl createPreferencenControl(PreferenceValue preferenceValue) {
    	PreferenceControl preferenceControl = new PreferenceControl();
    	preferenceControl.setCreationDate(new Date());
    	preferenceControl.setParamOld(preferenceValue.getValue());
    	preferenceControl.setPreferenceValueId(preferenceValue);
    	preferenceControl.setUserId(user.getId());
    	return preferenceControl;
    }
    
      
    private PreferenceValue createPreferencenValue(PreferenceValue value) {
    	PreferenceValue preferenceValue = new PreferenceValue();
    	preferenceValue.setCreateDate(new Timestamp(new Date().getTime()));
    	preferenceValue.setPreferenceFieldId(value.getPreferenceFieldId());
    	preferenceValue.setPreferenceValueParentId(value);
    	preferenceValue.setValue(value.getValue());
        preferenceValue.setPreferenceClassficationId(preferenceClassification);
        preferenceValue.setProductId((Product)cmbProduct.getSelectedItem().getValue());
        preferenceValue.setTransactionTypeId((TransactionType)cmbTransactionType.getSelectedItem().getValue());
        preferenceValue.setBussinessId(((Business)cmbBusiness.getSelectedItem().getValue()).getId());
    	return preferenceValue;
    }

    public void onClick$btnSave() {
		if (validateEmpty()) {
			switch (eventType) {
			case WebConstants.EVENT_ADD:
				try {
					if (preferencesEJB.validatePreferencesValues(
							preferenceClassification.getId(),
							((Product) cmbProduct.getSelectedItem().getValue()).getId(),
							((TransactionType) cmbTransactionType.getSelectedItem().getValue()).getId(),
							((Business) cmbBusiness.getSelectedItem().getValue()).getId())) {
						savePreferenceValues();
			      	cmbProduct.setDisabled(true);
	            	cmbTransactionType.setDisabled(true);
	            	cmbBusiness.setDisabled(true);
					}else
						this.showMessage("msj.error.field.exists", true, null);     
				}  catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case WebConstants.EVENT_EDIT:
				savePreferenceValues();
				break;
			default:
				break;
			}
		}
    }
    
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
            	loadProduct(preferenceValueParam.getProductId());
            	loadTransactionType(preferenceValueParam.getTransactionTypeId());
            	loadBusiness(preferenceValueParam.getBussinessId());  
            	loadPreferencesByParam(preferenceValueParam,preferenceClassification.getId(), false);
            	cmbProduct.setDisabled(true);
            	cmbTransactionType.setDisabled(true);
            	cmbBusiness.setDisabled(true);
                break;
            case WebConstants.EVENT_VIEW:
            	loadProduct(preferenceValueParam.getProductId());
            	loadTransactionType(preferenceValueParam.getTransactionTypeId());
            	loadBusiness(preferenceValueParam.getBussinessId()); 
            	loadPreferencesByParam(preferenceValueParam,preferenceClassification.getId(), true);
            	blockFields();
                break;
            case WebConstants.EVENT_ADD:
            	loadProduct(null);
            	loadTransactionType(null);
            	loadBusiness(null); 
                loadPreferences(preferenceClassification.getId());            
            default:
                break;
        }
    }
    
    private void loadProduct(Product p) {
        try {
        	cmbProduct.getItems().clear();
        	EJBRequest request2 = new EJBRequest();
            List<Product> products = productEJB.getProducts(request2);
            for (Product product:products) {
                Comboitem cmbItem = new Comboitem();
                cmbItem.setLabel(product.getName());
                cmbItem.setValue(product);
                cmbItem.setParent(cmbProduct);
                if (p != null && p.getId().equals(product.getId())) {
                	cmbProduct.setSelectedItem(cmbItem);
                } 
            }
        }  catch (Exception ex) {
            this.showError(ex);
        }
    }
    
    private void loadTransactionType(TransactionType t) {
        try {
        	cmbTransactionType.getItems().clear();
        	EJBRequest request2 = new EJBRequest();
            List<TransactionType> transactionTypes = preferencesEJB.getTransactionTypes(request2);
            for (TransactionType transactionType:transactionTypes) {
                Comboitem cmbItem = new Comboitem();
                cmbItem.setLabel(transactionType.getValue());
                cmbItem.setValue(transactionType);
                cmbItem.setParent(cmbTransactionType);
                if (t != null && t.getId().equals(transactionType.getId())) {
                	cmbTransactionType.setSelectedItem(cmbItem);
                }
            }
        }  catch (Exception ex) {
            this.showError(ex);
        }

    }
    
    private void loadBusiness(Long businessId) {

        try {
        	cmbBusiness.getItems().clear();
            List<Business> businesses = businessEJB.getAll();
            for (int i = 0; i < businesses.size(); i++) {
            	Comboitem item = new Comboitem();
                item.setValue(businesses.get(i));
                item.setLabel(businesses.get(i).getName());
                item.setParent(cmbBusiness);
                if (businesses.get(i) != null && businesses.get(i).getId().equals(businessId)) {
                	cmbBusiness.setSelectedItem(item);
                } 
            }
        } catch (Exception ex) {
            this.showError(ex);
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
    
    public void onChange$cmbProduct() {
    	clearMessage();
    	btnSave.setVisible(true);
    	if (cmbProduct.getSelectedItem() != null && cmbTransactionType.getSelectedItem() != null && cmbBusiness.getSelectedItem() != null && eventType==WebConstants.EVENT_ADD) 
    		try {
				if (preferencesEJB.validatePreferencesValues(
						preferenceClassification.getId(),
						((Product) cmbProduct.getSelectedItem().getValue()).getId(),
						((TransactionType) cmbTransactionType.getSelectedItem().getValue()).getId(),
						((Business) cmbBusiness.getSelectedItem().getValue()).getId()))
		    		loadPreferences(preferenceClassification.getId());  
				else {
					PreferenceValue pValue = new PreferenceValue();
					pValue.setPreferenceClassficationId(preferenceClassification);
					pValue.setProductId((Product) cmbProduct.getSelectedItem().getValue());
					pValue.setTransactionTypeId((TransactionType) cmbTransactionType.getSelectedItem().getValue());
					pValue.setBussinessId(((Business) cmbBusiness.getSelectedItem().getValue()).getId());
	            	loadPreferencesByParam(pValue,preferenceClassification.getId(), true);
	            	btnSave.setVisible(false);
					this.showMessage("msj.error.field.exists", true, null);   
				}  
			}  catch (Exception e) {
				e.printStackTrace();
			}
    }
    
    public void onChange$cmbTransactionType() {
    	clearMessage();
    	btnSave.setVisible(true);
    	if (cmbProduct.getSelectedItem() != null && cmbTransactionType.getSelectedItem() != null && cmbBusiness.getSelectedItem() != null && eventType==WebConstants.EVENT_ADD) 
    		try {
				if (preferencesEJB.validatePreferencesValues(
						preferenceClassification.getId(),
						((Product) cmbProduct.getSelectedItem().getValue()).getId(),
						((TransactionType) cmbTransactionType.getSelectedItem().getValue()).getId(),
						((Business) cmbBusiness.getSelectedItem().getValue()).getId()))
		    		loadPreferences(preferenceClassification.getId());  
				else {
					PreferenceValue pValue = new PreferenceValue();
					pValue.setPreferenceClassficationId(preferenceClassification);
					pValue.setProductId((Product) cmbProduct.getSelectedItem().getValue());
					pValue.setTransactionTypeId((TransactionType) cmbTransactionType.getSelectedItem().getValue());
					pValue.setBussinessId(((Business) cmbBusiness.getSelectedItem().getValue()).getId());
	            	loadPreferencesByParam(pValue,preferenceClassification.getId(), true);
	            	btnSave.setVisible(false);
					this.showMessage("msj.error.field.exists", true, null);   
				}     
			}  catch (Exception e) {
				e.printStackTrace();
			}  
    }

    public void onChange$cmbBusiness() {
    	clearMessage();
    	btnSave.setVisible(true);
    	if (cmbProduct.getSelectedItem() != null && cmbTransactionType.getSelectedItem() != null && cmbBusiness.getSelectedItem() != null && eventType==WebConstants.EVENT_ADD) 
    		try {
				if (preferencesEJB.validatePreferencesValues(
						preferenceClassification.getId(),
						((Product) cmbProduct.getSelectedItem().getValue()).getId(),
						((TransactionType) cmbTransactionType.getSelectedItem().getValue()).getId(),
						((Business) cmbBusiness.getSelectedItem().getValue()).getId()))
		    		loadPreferences(preferenceClassification.getId());  
				else {
					PreferenceValue pValue = new PreferenceValue();
					pValue.setPreferenceClassficationId(preferenceClassification);
					pValue.setProductId((Product) cmbProduct.getSelectedItem().getValue());
					pValue.setTransactionTypeId((TransactionType) cmbTransactionType.getSelectedItem().getValue());
					pValue.setBussinessId(((Business) cmbBusiness.getSelectedItem().getValue()).getId());
	            	loadPreferencesByParam(pValue,preferenceClassification.getId(), true);
	            	btnSave.setVisible(false);
					this.showMessage("msj.error.field.exists", true, null);   
				}
			}  catch (Exception e) {
				e.printStackTrace();
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
    
    @SuppressWarnings("unchecked")
    private void changeStatus(Row row) {
     	List<Component> children = row.getChildren();
     	((Checkbox)children.get(5)).setChecked(true);
     	((Checkbox)children.get(6)).setChecked(true);
    }
    
    @SuppressWarnings("unchecked")
    private void changeSpecific(Row row) {
     	List<Component> children = row.getChildren();
     	if (((Checkbox)children.get(5)).isChecked()){
	     	((Checkbox)children.get(5)).setChecked(true);
	     	((Checkbox)children.get(6)).setChecked(true);
     	}else {
     		((Checkbox)children.get(5)).setChecked(false);
	     	((Checkbox)children.get(6)).setChecked(false);
     	}
    }
    
    @SuppressWarnings("unchecked")
    private void changeEnabled(Row row) {
     	List<Component> children = row.getChildren();
     	if (((Checkbox)children.get(6)).isChecked()){
	     	((Checkbox)children.get(5)).setChecked(true);
	     	((Checkbox)children.get(6)).setChecked(true);
     	}else {
     		((Checkbox)children.get(5)).setChecked(false);
	     	((Checkbox)children.get(6)).setChecked(false);
     	}
    }
}
