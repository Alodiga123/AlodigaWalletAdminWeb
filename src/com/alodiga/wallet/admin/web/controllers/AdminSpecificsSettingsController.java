package com.alodiga.wallet.admin.web.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
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
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PreferenceManager;
import com.alodiga.wallet.common.model.PreferenceClassification;
import com.alodiga.wallet.common.model.PreferenceControl;
import com.alodiga.wallet.common.model.PreferenceField;
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

    
    public boolean validateEmpty() {
        if (cmbProduct.getSelectedIndex() == -1) {
        	cmbProduct.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (cmbTransactionType.getSelectedIndex() == -1) {
        	cmbTransactionType.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        }  else if (cmbBusiness.getSelectedIndex() == -1) {
        	cmbBusiness.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
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


    private void loadPreferences(Long classificationId) {
        try {
            setData();

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
	                label.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
	                label.setParent(row);
	                if (field.getId().equals(DEFAULT_SMS_PROVIDER_ID)) {
	                	Combobox cmbbox = new Combobox();
	                	loadProviders(Long.parseLong(pValue.getValue()),cmbbox);
	                	cmbbox.setParent(row);
	                	Checkbox chb = new Checkbox();
	                	chb.setChecked(pValue.isEnabled());
	                	chb.setParent(row);
	                	preferenceValues.add(createPreferencenValue(pValue));
	                } else if (field.getPreferenceTypeId().getId().equals(PreferenceTypeValuesEnum.BOOLEAN.getValue())) {
	                	Checkbox chbValue = new Checkbox();
	                	boolean checked = Integer.parseInt(pValue.getValue()!=null?pValue.getValue():"0") == 1 ? true : false;
	                	chbValue.setChecked(checked);
	                	chbValue.setParent(row);
	                	Checkbox chb = new Checkbox();
	                	chb.setChecked(pValue.isEnabled());
	                	chb.setParent(row);
	                	preferenceValues.add(createPreferencenValue(pValue));
	                }  else {
	                	Textbox txtValue = new Textbox();
	                	txtValue.setText(pValue.getValue()!=null?pValue.getValue():"");
	                	txtValue.setParent(row);
	                	Checkbox chb = new Checkbox();
	                	chb.setChecked(pValue.isEnabled());
	                	chb.setParent(row);
	                	preferenceValues.add(createPreferencenValue(pValue));
	                }
	                row.setParent(rowsGrid);  
              }
            }
        } catch (Exception ex) {
            showError(ex);
        }

    }
    
    private void loadPreferencesByParam(PreferenceValue preferenceValue,Long classificationId) {
        try {
            setData();
                
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
					label.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
					label.setParent(row);
					if (pValue.getPreferenceFieldId().getId().equals(DEFAULT_SMS_PROVIDER_ID)) {
						Combobox cmbbox = new Combobox();
						loadProviders(Long.parseLong(pValue.getValue()), cmbbox);
						cmbbox.setParent(row);
						Checkbox chb = new Checkbox();
						chb.setChecked(pValue.isEnabled());
						chb.setParent(row);
						preferenceValues.add(pValue);
					} else if (pValue.getPreferenceFieldId().getPreferenceTypeId().getId().equals(PreferenceTypeValuesEnum.BOOLEAN.getValue())) {
						Checkbox chbValue = new Checkbox();
						boolean checked = Integer.parseInt(pValue.getValue()!=null?pValue.getValue():"0") == 1 ? true : false;
						chbValue.setChecked(checked);
						chbValue.setParent(row);
						Checkbox chb = new Checkbox();
						chb.setChecked(pValue.isEnabled());
						chb.setParent(row);
						preferenceValues.add(pValue);
					} else {
						Textbox txtValue = new Textbox();
						txtValue.setText(pValue.getValue()!=null?pValue.getValue():"");
						txtValue.setParent(row);
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

    public void setData() {
        DEFAULT_SMS_PROVIDER_ID = PreferenceFieldEnum.DEFAULT_SMS_PROVIDER_ID.getId();
    }

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
            	
            	boolean check = ((Checkbox)children.get(2)).isChecked() ? true : false;
            	
            	if (!pvalue.getValue().equals(value) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=check) {
            		if ((children.get(1) instanceof Textbox) && Long.parseLong(((Textbox)children.get(1)).getText())>Long.parseLong(pvalue.getPreferenceValueParentId()!=null?pvalue.getPreferenceValueParentId().getValue():pvalue.getValue())) {
            			((Textbox)children.get(1)).setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);        
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
	            this.showMessage("sp.common.save.success", false, null);
             } 
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private PreferenceValue updatePreferencenValue(PreferenceValue preferenceValue,Row r) {
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
    	preferenceValue.setEnabled(((Checkbox)children.get(2)).isChecked());
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
							((Business) cmbBusiness.getSelectedItem().getValue()).getId()))
						savePreferenceValues();
					else
						this.showMessage("sp.error.field.exists", true, null);     
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
            	loadPreferencesByParam(preferenceValueParam,preferenceClassification.getId());
            	cmbProduct.setDisabled(true);
            	cmbTransactionType.setDisabled(true);
                break;
            case WebConstants.EVENT_VIEW:
            	loadProduct(preferenceValueParam.getProductId());
            	loadTransactionType(preferenceValueParam.getTransactionTypeId());
            	loadBusiness(preferenceValueParam.getBussinessId()); 
            	loadPreferencesByParam(preferenceValueParam,preferenceClassification.getId());
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
                } else {
                	cmbProduct.setSelectedIndex(0);
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
                } else {
                	cmbTransactionType.setSelectedIndex(0);
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
                } else {
                	cmbBusiness.setSelectedIndex(0);
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

}
