package com.alodiga.wallet.admin.web.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import com.alodiga.businessportal.ws.BPBusinessWSProxy;
import com.alodiga.businessportal.ws.BpBusiness;
import com.alodiga.businessportal.ws.BusinessSearchType;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractController;
import com.alodiga.wallet.admin.web.generic.controllers.GenericSPController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.PreferenceClassification;
import com.alodiga.wallet.common.model.PreferenceControl;
import com.alodiga.wallet.common.model.PreferenceField;
import com.alodiga.wallet.common.model.PreferenceFieldEnum;
import com.alodiga.wallet.common.model.PreferenceValue;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.model.Provider;
import com.alodiga.wallet.common.model.TransactionType;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class AdminSpecificsSettingsController extends GenericAbstractController implements GenericSPController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtTimeoutInactiveSession;
    private Textbox txtMaxWrongNumberIntentLogin;
    private Textbox txtMaxTransactionAmountLimit;
    private Textbox txtMaxTransactionAmountDailyLimit;
    private Textbox txtMaxTransactionAmountMonthLimit;
    private Textbox txtMaxTransactionAmountYearLimit;
    private Textbox txtMaxTransactionQuantityDailyLimit;
    private Textbox txtMaxTransactionQuantityMonthLimit;
    private Textbox txtMaxTransactionQuantityYearLimit;
    private Label lblTimeoutInactiveSession;
    private Label lblMaxWrongNumberIntentLogin;
    private Label lblEnabledTransaction;
    private Label lblMaxTransactionAmountLimit;
    private Label lblMaxTransactionAmountDailyLimit;
    private Label lblMaxTransactionAmountMonthLimit;
    private Label lblMaxTransactionAmountYearLimit;
    private Label lblMaxTransactionQuantityDailyLimit;
    private Label lblMaxTransactionQuantityMonthLimit;
    private Label lblMaxTransactionQuantityYearLimit;
    private Label lblDefaultSMSProvider;
    private Checkbox cbxTimeoutInactiveSession;
    private Checkbox cbxMaxWrongNumberIntentLogin;
    private Checkbox cbxEnabledTransaction;
    private Checkbox cbxMaxTransactionAmountLimit;
    private Checkbox cbxMaxTransactionAmountDailyLimit;
    private Checkbox cbxMaxTransactionAmountMonthLimit;
    private Checkbox cbxMaxTransactionAmountYearLimit;
    private Checkbox cbxMaxTransactionQuantityDailyLimit;
    private Checkbox cbxMaxTransactionQuantityMonthLimit;
    private Checkbox cbxMaxTransactionQuantityYearLimit;
    private Checkbox cbxDefaultSMSProvider;
    private Long languageId;
    
    private Button btnSave;
    private Button btnSearch;
    private Combobox cmbClassification;
    private Combobox cmbDefaultSMSProvider;
    private Checkbox chbEnableTransaction;
    private Long TIMEOUT_INACTIVE_SESSION_ID;
    private Long MAX_TRANSACTION_AMOUNT_LIMIT_ID;
    private Long MAX_WRONG_NUMBER_INTENT_LOGIN_ID;
    private Long MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID;
    private Long MAX_TRANSACTION_AMOUNT_MONTH_LIMIT_ID;
    private Long MAX_TRANSACTION_AMOUNT_YEAR_LIMIT_ID;
    private Long DISABLED_TRANSACTION_ID;
    private Long DEFAULT_SMS_PROVIDER_ID;
    private Long MAX_TRANSACTION_QUANTITY_DAILY_LIMIT_ID;
    private Long MAX_TRANSACTION_QUANTITY_MONTH_LIMIT_ID;
    private Long MAX_TRANSACTION_QUANTITY_YEAR_LIMIT_ID;

    private PreferencesEJB preferencesEJB = null;
    private ProductEJB productEJB = null;
    List<PreferenceValue> preferenceValues = null;
    private User user=null;
    private PreferenceValue preferenceValueParam;
    private Combobox cmbProduct;
    private Combobox cmbTransactionType;
    private Textbox txtBussiness;
    private Textbox txtEmailBussiness;


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
        	languageId = AccessControl.getLanguage();
        	user = AccessControl.loadCurrentUser();
        	loadData();
			onChange$cmbClassification();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void blockFields() {
    	cmbClassification.setDisabled(true);
    	cmbProduct.setDisabled(true);
    	cmbTransactionType.setDisabled(true);
        txtTimeoutInactiveSession.setReadonly(true);
        txtMaxWrongNumberIntentLogin.setReadonly(true);
        txtMaxTransactionAmountLimit.setReadonly(true);
        txtMaxTransactionAmountDailyLimit.setReadonly(true);
        txtMaxTransactionAmountMonthLimit.setReadonly(true);
        txtMaxTransactionAmountYearLimit.setReadonly(true);
        cmbDefaultSMSProvider.setReadonly(true);
        txtMaxTransactionQuantityDailyLimit.setReadonly(true);
        txtMaxTransactionQuantityMonthLimit.setReadonly(true);
        txtMaxTransactionQuantityYearLimit.setReadonly(true);
        txtBussiness.setReadonly(true);
        cbxTimeoutInactiveSession.setDisabled(true);
        cbxMaxWrongNumberIntentLogin.setDisabled(true);
        cbxEnabledTransaction.setDisabled(true);
        cbxMaxTransactionAmountLimit.setDisabled(true);
        cbxMaxTransactionAmountDailyLimit.setDisabled(true);
        cbxMaxTransactionAmountMonthLimit.setDisabled(true);
        cbxMaxTransactionAmountYearLimit.setDisabled(true);
        cbxMaxTransactionQuantityDailyLimit.setDisabled(true);
        cbxMaxTransactionQuantityMonthLimit.setDisabled(true);
        cbxMaxTransactionQuantityYearLimit.setDisabled(true);
        cbxDefaultSMSProvider.setDisabled(true);
        btnSave.setVisible(false);
    	txtEmailBussiness.setVisible(false);
    	btnSearch.setVisible(false);
    }

    private void loadPreferenceClassifications(PreferenceClassification preferenceClassification) {
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
                if (preferenceClassification != null && preferenceClassification.getId().equals(e.getId())) {
                	cmbClassification.setSelectedItem(cmbItem);
                } else {
                	cmbClassification.setSelectedIndex(0);
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }

    }

	private void loadProviders(Long providerId) {
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
    	if (cmbClassification.getSelectedIndex() == -1) {
    		cmbClassification.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        }else if (cmbProduct.getSelectedIndex() == -1) {
        	cmbProduct.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (cmbTransactionType.getSelectedIndex() == -1) {
        	cmbTransactionType.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        }  else if (txtBussiness.getText().isEmpty()) {
        	txtBussiness.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        }  else if (txtTimeoutInactiveSession.getText().isEmpty()) {
            txtTimeoutInactiveSession.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxWrongNumberIntentLogin.getText().isEmpty()) {
            txtMaxWrongNumberIntentLogin.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxTransactionAmountLimit.getText().isEmpty()) {
            txtMaxTransactionAmountLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxTransactionAmountDailyLimit.getText().isEmpty()) {
            txtMaxTransactionAmountDailyLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxTransactionAmountMonthLimit.getText().isEmpty()) {
        	txtMaxTransactionAmountMonthLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxTransactionAmountYearLimit.getText().isEmpty()) {
        	txtMaxTransactionAmountYearLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxTransactionQuantityDailyLimit.getText().isEmpty()) {
            txtMaxTransactionQuantityDailyLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxTransactionQuantityMonthLimit.getText().isEmpty()) {
        	txtMaxTransactionQuantityMonthLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxTransactionQuantityYearLimit.getText().isEmpty()) {
        	txtMaxTransactionQuantityYearLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        }else if (cmbDefaultSMSProvider.getSelectedIndex() == -1) {
            cmbDefaultSMSProvider.setFocus(true);
            this.showMessage("sp.error.smsprovider.notSelected", true, null);
        } else {
            return true;
        }
        return false;
    }

    public void onChange$cmbClassification() throws InterruptedException {
        this.clearMessage();
        PreferenceClassification preferenceClassification = null;
        if (cmbClassification.getSelectedItem() != null && eventType==WebConstants.EVENT_ADD) {
        	preferenceClassification = (PreferenceClassification) cmbClassification.getSelectedItem().getValue();
            loadPreferences(preferenceClassification.getId());
        }else if (cmbClassification.getSelectedItem() != null && (eventType==WebConstants.EVENT_EDIT || eventType==WebConstants.EVENT_VIEW)) {
        	preferenceClassification = (PreferenceClassification) cmbClassification.getSelectedItem().getValue();
        	loadPreferencesByParam(preferenceValueParam);
        }
    }


    private void loadPreferences(Long classificationId) {
        try {
            setData();

            List<PreferenceField> fields = preferencesEJB.getPreferenceFields(request);
            preferenceValues = new ArrayList<PreferenceValue>();
            for (PreferenceField field : fields) {
                
                PreferenceValue pValue = preferencesEJB.loadActivePreferenceValuesByClassificationIdAndFieldId(classificationId, field.getId());

                if (field.getId().equals(MAX_WRONG_NUMBER_INTENT_LOGIN_ID)) {
                	lblMaxWrongNumberIntentLogin.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtMaxWrongNumberIntentLogin.setText(pValue.getValue());
                   	cbxMaxWrongNumberIntentLogin.setChecked(pValue.isEnabled());
                    preferenceValues.add(createPreferencenValue(pValue));
                }
                if (field.getId().equals(TIMEOUT_INACTIVE_SESSION_ID)) {
                	lblTimeoutInactiveSession.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtTimeoutInactiveSession.setText(pValue.getValue());
                	cbxTimeoutInactiveSession.setChecked(pValue.isEnabled());
                    preferenceValues.add(createPreferencenValue(pValue));
                }
                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_LIMIT_ID)) {
                	lblMaxTransactionAmountLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountLimit.setText(pValue.getValue());
                	cbxMaxTransactionAmountLimit.setChecked(pValue.isEnabled());
                	preferenceValues.add(createPreferencenValue(pValue));
                }
                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID)) {
                	lblMaxTransactionAmountDailyLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountDailyLimit.setText(pValue.getValue());
                   	cbxMaxTransactionAmountDailyLimit.setChecked(pValue.isEnabled());
                    preferenceValues.add(createPreferencenValue(pValue));
                }
                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_MONTH_LIMIT_ID)) {
                	lblMaxTransactionAmountMonthLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountMonthLimit.setText(pValue.getValue());
                	cbxMaxTransactionAmountMonthLimit.setChecked(pValue.isEnabled());
                	preferenceValues.add(createPreferencenValue(pValue));
                }

                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_YEAR_LIMIT_ID)) {
                	lblMaxTransactionAmountYearLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountYearLimit.setText(pValue.getValue());
                	cbxMaxTransactionAmountYearLimit.setChecked(pValue.isEnabled());
                    preferenceValues.add(createPreferencenValue(pValue));
                }
                if (field.getId().equals(DISABLED_TRANSACTION_ID)) {
                	lblEnabledTransaction.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	boolean checked = Integer.parseInt(pValue.getValue()) == 1 ? true : false;
                	chbEnableTransaction.setChecked(checked);
                	cbxEnabledTransaction.setChecked(pValue.isEnabled());
                	preferenceValues.add(createPreferencenValue(pValue));
                }
                if (field.getId().equals(DEFAULT_SMS_PROVIDER_ID)) {
                	lblDefaultSMSProvider.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    loadProviders(Long.parseLong(pValue.getValue()));
                    cbxDefaultSMSProvider.setChecked(pValue.isEnabled());
                    preferenceValues.add(createPreferencenValue(pValue));
                }
                if (field.getId().equals(MAX_TRANSACTION_QUANTITY_DAILY_LIMIT_ID)) {
                	lblMaxTransactionQuantityDailyLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionQuantityDailyLimit.setText(pValue.getValue());
                	cbxMaxTransactionQuantityDailyLimit.setChecked(pValue.isEnabled());
                	preferenceValues.add(createPreferencenValue(pValue));
                }
                if (field.getId().equals(MAX_TRANSACTION_QUANTITY_MONTH_LIMIT_ID)) {
                	lblMaxTransactionQuantityMonthLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtMaxTransactionQuantityMonthLimit.setText(pValue.getValue());
                    cbxMaxTransactionQuantityMonthLimit.setChecked(pValue.isEnabled());
                    preferenceValues.add(createPreferencenValue(pValue));
                }
                if (field.getId().equals(MAX_TRANSACTION_QUANTITY_YEAR_LIMIT_ID)) {
                	lblMaxTransactionQuantityYearLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtMaxTransactionQuantityYearLimit.setText(pValue.getValue());
                    cbxMaxTransactionQuantityYearLimit.setChecked(pValue.isEnabled());
                    preferenceValues.add(createPreferencenValue(pValue));
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }

    }
    
    private void loadPreferencesByParam(PreferenceValue preferenceValue) {
        try {
            setData();

            preferenceValues = preferencesEJB.getPreferenceValuesByParam(preferenceValue.getPreferenceClassficationId().getId(),
            preferenceValue.getProductId().getId(), preferenceValue.getTransactionTypeId().getId(), preferenceValue.getBussinessId());
            for (PreferenceValue pValue : preferenceValues) {
                
      
                if (pValue.getPreferenceFieldId().getId().equals(MAX_WRONG_NUMBER_INTENT_LOGIN_ID)) {
                	lblMaxWrongNumberIntentLogin.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtMaxWrongNumberIntentLogin.setText(pValue.getValue());
                	cbxMaxWrongNumberIntentLogin.setChecked(pValue.isEnabled());
                }
                if (pValue.getPreferenceFieldId().getId().equals(TIMEOUT_INACTIVE_SESSION_ID)) {
                	lblTimeoutInactiveSession.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtTimeoutInactiveSession.setText(pValue.getValue());
                	cbxTimeoutInactiveSession.setChecked(pValue.isEnabled());
                }
                if (pValue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_LIMIT_ID)) {
                	lblMaxTransactionAmountLimit.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountLimit.setText(pValue.getValue());
                	cbxMaxTransactionAmountLimit.setChecked(pValue.isEnabled());
                }
                if (pValue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID)) {
                	lblMaxTransactionAmountDailyLimit.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountDailyLimit.setText(pValue.getValue());
                	cbxMaxTransactionAmountDailyLimit.setChecked(pValue.isEnabled());
                }
                if (pValue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_MONTH_LIMIT_ID)) {
                	lblMaxTransactionAmountMonthLimit.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountMonthLimit.setText(pValue.getValue());
                	cbxMaxTransactionAmountMonthLimit.setChecked(pValue.isEnabled());
                }

                if (pValue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_YEAR_LIMIT_ID)) {
                	lblMaxTransactionAmountYearLimit.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountYearLimit.setText(pValue.getValue());
                	cbxMaxTransactionAmountYearLimit.setChecked(pValue.isEnabled());
                }
                if (pValue.getPreferenceFieldId().getId().equals(DISABLED_TRANSACTION_ID)) {
                	lblEnabledTransaction.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	boolean checked = Integer.parseInt(pValue.getValue()) == 1 ? true : false;
                	chbEnableTransaction.setChecked(checked);
                	cbxEnabledTransaction.setChecked(pValue.isEnabled());
                }
                if (pValue.getPreferenceFieldId().getId().equals(DEFAULT_SMS_PROVIDER_ID)) {
                	lblDefaultSMSProvider.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    loadProviders(Long.parseLong(pValue.getValue()));
                	cbxDefaultSMSProvider.setChecked(pValue.isEnabled());
                }
                if (pValue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_QUANTITY_DAILY_LIMIT_ID)) {
                	lblMaxTransactionQuantityDailyLimit.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionQuantityDailyLimit.setText(pValue.getValue());
                	cbxMaxTransactionQuantityDailyLimit.setChecked(pValue.isEnabled());
                }
                if (pValue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_QUANTITY_MONTH_LIMIT_ID)) {
                	lblMaxTransactionQuantityMonthLimit.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtMaxTransactionQuantityMonthLimit.setText(pValue.getValue());
                	cbxMaxTransactionQuantityMonthLimit.setChecked(pValue.isEnabled());
                }
                if (pValue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_QUANTITY_YEAR_LIMIT_ID)) {
                	lblMaxTransactionQuantityYearLimit.setValue(pValue.getPreferenceFieldId().getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtMaxTransactionQuantityYearLimit.setText(pValue.getValue());
                	cbxMaxTransactionQuantityYearLimit.setChecked(pValue.isEnabled());
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void setData() {
        MAX_TRANSACTION_AMOUNT_LIMIT_ID = PreferenceFieldEnum.MAX_TRANSACTION_AMOUNT_LIMIT_ID.getId();
        MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID = PreferenceFieldEnum.MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID.getId();
        MAX_TRANSACTION_AMOUNT_MONTH_LIMIT_ID = PreferenceFieldEnum.MAX_TRANSACTION_AMOUNT_MONTH_LIMIT_ID.getId();
        MAX_TRANSACTION_AMOUNT_YEAR_LIMIT_ID = PreferenceFieldEnum.MAX_TRANSACTION_AMOUNT_YEAR_LIMIT_ID.getId();
        MAX_WRONG_NUMBER_INTENT_LOGIN_ID = PreferenceFieldEnum.MAX_WRONG_LOGIN_INTENT_NUMBER_ID.getId();
        TIMEOUT_INACTIVE_SESSION_ID = PreferenceFieldEnum.TIMEOUT_INACTIVE_SESSION_ID.getId();
        DISABLED_TRANSACTION_ID = PreferenceFieldEnum.DISABLED_TRANSACTION_ID.getId();
        DEFAULT_SMS_PROVIDER_ID = PreferenceFieldEnum.DEFAULT_SMS_PROVIDER_ID.getId();
        MAX_TRANSACTION_QUANTITY_DAILY_LIMIT_ID = PreferenceFieldEnum.MAX_TRANSACTION_QUANTITY_DAILY_LIMIT_ID.getId();
        MAX_TRANSACTION_QUANTITY_MONTH_LIMIT_ID = PreferenceFieldEnum.MAX_TRANSACTION_QUANTITY_MONTH_LIMIT_ID.getId();
        MAX_TRANSACTION_QUANTITY_YEAR_LIMIT_ID = PreferenceFieldEnum.MAX_TRANSACTION_QUANTITY_YEAR_LIMIT_ID.getId();
    }

    private void savePreferenceValues() {
        try {
        	boolean save= true;
            int enableTransaction = chbEnableTransaction.isChecked() ? 1 : 0;
            List<PreferenceValue> preferenceValueSave = new ArrayList<PreferenceValue>();
            List<PreferenceControl>  preferenceControls = new ArrayList<PreferenceControl>();
            for(PreferenceValue pvalue: preferenceValues) {
            	if (pvalue.getPreferenceFieldId().getId().equals(TIMEOUT_INACTIVE_SESSION_ID) && (!pvalue.getValue().equals(txtTimeoutInactiveSession.getText()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxTimeoutInactiveSession.isChecked())) {
            		if (Integer.parseInt(txtTimeoutInactiveSession.getText())>Integer.parseInt(pvalue.getPreferenceValueParentId().getValue())) {
            			txtTimeoutInactiveSession.setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);        
            			break;
            		}else {
            			preferenceControls.add(createPreferencenControl(pvalue));
            			preferenceValueSave.add(updatePreferencenValue(pvalue,txtTimeoutInactiveSession.getText(), cbxTimeoutInactiveSession.isChecked()));
            		}
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_WRONG_NUMBER_INTENT_LOGIN_ID) && (!pvalue.getValue().equals(txtMaxWrongNumberIntentLogin.getText()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxMaxWrongNumberIntentLogin.isChecked())) {
            		if (Integer.parseInt(txtMaxWrongNumberIntentLogin.getText())>Integer.parseInt(pvalue.getPreferenceValueParentId().getValue())) {
            			txtMaxWrongNumberIntentLogin.setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);    
            			break;
            		}else {
            			preferenceControls.add(createPreferencenControl(pvalue));
            			preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxWrongNumberIntentLogin.getText(), cbxMaxWrongNumberIntentLogin.isChecked()));	
            		}
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(DISABLED_TRANSACTION_ID) && (!pvalue.getValue().equals("" +enableTransaction) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxEnabledTransaction.isChecked())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,"" +enableTransaction, cbxEnabledTransaction.isChecked()));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_LIMIT_ID) && (!pvalue.getValue().equals(txtMaxTransactionAmountLimit.getText()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxMaxTransactionAmountLimit.isChecked())) {
            		if (Long.parseLong(txtMaxTransactionAmountLimit.getText())>Long.parseLong(pvalue.getPreferenceValueParentId().getValue())) {
            			txtMaxTransactionAmountLimit.setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);   
            			break;
            		}else {
            			preferenceControls.add(createPreferencenControl(pvalue));
                    	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionAmountLimit.getText(), cbxMaxTransactionAmountLimit.isChecked()));
            		}
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID) && (!pvalue.getValue().equals(txtMaxTransactionAmountDailyLimit.getText()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxMaxTransactionAmountDailyLimit.isChecked())) {
            		if (Long.parseLong(txtMaxTransactionAmountDailyLimit.getText())>Long.parseLong(pvalue.getPreferenceValueParentId().getValue())) {
            			txtMaxTransactionAmountDailyLimit.setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);      
            			break;
            		}else {
                		preferenceControls.add(createPreferencenControl(pvalue));
                    	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionAmountDailyLimit.getText(), cbxMaxTransactionAmountDailyLimit.isChecked()));
                 	}
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_MONTH_LIMIT_ID) && (!pvalue.getValue().equals(txtMaxTransactionAmountMonthLimit.getText()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxMaxTransactionAmountMonthLimit.isChecked())){
            		if (Long.parseLong(txtMaxTransactionAmountMonthLimit.getText())>Long.parseLong(pvalue.getPreferenceValueParentId().getValue())) {
            			txtMaxTransactionAmountMonthLimit.setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);     
            			break;
            		}else {
            			preferenceControls.add(createPreferencenControl(pvalue));
                    	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionAmountMonthLimit.getText(), cbxMaxTransactionAmountMonthLimit.isChecked()));
            		}
              	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_YEAR_LIMIT_ID) && (!pvalue.getValue().equals(txtMaxTransactionAmountYearLimit.getText()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxMaxTransactionAmountYearLimit.isChecked())) {
            		if (Long.parseLong(txtMaxTransactionAmountYearLimit.getText())>Long.parseLong(pvalue.getPreferenceValueParentId().getValue())) {
            			txtMaxTransactionAmountYearLimit.setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);   
            			break;
            		}else {
            			preferenceControls.add(createPreferencenControl(pvalue));
                    	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionAmountYearLimit.getText(), cbxMaxTransactionAmountYearLimit.isChecked()));
            		}
               	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_QUANTITY_DAILY_LIMIT_ID) && (!pvalue.getValue().equals(txtMaxTransactionQuantityDailyLimit.getText()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxMaxTransactionQuantityDailyLimit.isChecked())) {
            		if (Integer.parseInt(txtMaxTransactionQuantityDailyLimit.getText())>Integer.parseInt(pvalue.getPreferenceValueParentId().getValue())) {
            			txtMaxTransactionQuantityDailyLimit.setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);        
            			break;
            		}else {
            			preferenceControls.add(createPreferencenControl(pvalue));
                    	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionQuantityDailyLimit.getText(), cbxMaxTransactionQuantityDailyLimit.isChecked()));
            		}
               	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_QUANTITY_MONTH_LIMIT_ID) && (!pvalue.getValue().equals(txtMaxTransactionQuantityMonthLimit.getText()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxMaxTransactionQuantityMonthLimit.isChecked())) {
            		if (Integer.parseInt(txtMaxTransactionQuantityMonthLimit.getText())>Integer.parseInt(pvalue.getPreferenceValueParentId().getValue())) {
            			txtMaxTransactionQuantityMonthLimit.setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);       
            			break;
            		}else {
            			preferenceControls.add(createPreferencenControl(pvalue));
                    	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionQuantityMonthLimit.getText(), cbxMaxTransactionQuantityMonthLimit.isChecked()));
            		}
               	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_QUANTITY_YEAR_LIMIT_ID) && (!pvalue.getValue().equals(txtMaxTransactionQuantityYearLimit.getText()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxMaxTransactionQuantityYearLimit.isChecked())) {
            		if (Integer.parseInt(txtMaxTransactionQuantityYearLimit.getText())>Integer.parseInt(pvalue.getPreferenceValueParentId().getValue())) {
            			txtMaxTransactionQuantityYearLimit.setFocus(true);
            			save= false;
            			this.showMessage("sp.error.field.wrongValue", true, null);   
            			break;
            		}else {
                  		preferenceControls.add(createPreferencenControl(pvalue));
                    	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionQuantityYearLimit.getText(), cbxMaxTransactionQuantityYearLimit.isChecked()));
           		}
              	}
            	Provider provider = (Provider) cmbDefaultSMSProvider.getSelectedItem().getValue();
            	if (pvalue.getPreferenceFieldId().getId().equals(DEFAULT_SMS_PROVIDER_ID) && (!pvalue.getValue().equals(provider.getId().toString()) || eventType==WebConstants.EVENT_ADD || pvalue.isEnabled()!=cbxDefaultSMSProvider.isChecked())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,provider.getId().toString(), cbxDefaultSMSProvider.isChecked()));
            	}    
            }
            if (save) {
            	preferencesEJB.savePreferenceValues(preferenceValueSave,preferenceControls);
//            PreferenceManager preferenceManager = PreferenceManager.getInstance();
//            preferenceManager.refresh();
            this.showMessage("sp.common.save.success", false, null);
            }   	
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
    
    private PreferenceValue updatePreferencenValue(PreferenceValue preferenceValue,String value, boolean enabled) {
    	preferenceValue.setValue(value);
    	preferenceValue.setUpdateDate(new Timestamp(new Date().getTime()));
    	preferenceValue.setEnabled(enabled);
    	if(eventType==WebConstants.EVENT_ADD) {
    		preferenceValue.setPreferenceClassficationId((PreferenceClassification)cmbClassification.getSelectedItem().getValue());
    		preferenceValue.setProductId((Product)cmbProduct.getSelectedItem().getValue());
    		preferenceValue.setTransactionTypeId((TransactionType)cmbTransactionType.getSelectedItem().getValue());
    		preferenceValue.setBussinessId(Long.parseLong(txtBussiness.getText()));
    	}
    	return preferenceValue;
    }
    
    private PreferenceValue createPreferencenValue(PreferenceValue value) {
    	PreferenceValue preferenceValue = new PreferenceValue();
    	preferenceValue.setCreateDate(new Timestamp(new Date().getTime()));
    	preferenceValue.setPreferenceFieldId(value.getPreferenceFieldId());
    	preferenceValue.setPreferenceValueParentId(value);
    	preferenceValue.setValue(value.getValue());
    	return preferenceValue;
    }

    public void onClick$btnSave() {
		if (validateEmpty()) {
			switch (eventType) {
			case WebConstants.EVENT_ADD:
				try {
					if (preferencesEJB.validatePreferencesValues(
							((PreferenceClassification) cmbClassification.getSelectedItem().getValue()).getId(),
							((Product) cmbProduct.getSelectedItem().getValue()).getId(),
							((TransactionType) cmbTransactionType.getSelectedItem().getValue()).getId(),
							Long.parseLong(txtBussiness.getText())))
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
            	loadPreferenceClassifications(preferenceValueParam.getPreferenceClassficationId());
            	loadProduct(preferenceValueParam.getProductId());
            	loadTransactionType(preferenceValueParam.getTransactionTypeId());
            	txtBussiness.setText(preferenceValueParam.getBussinessId().toString());
            	cmbClassification.setDisabled(true);
            	cmbProduct.setDisabled(true);
            	cmbTransactionType.setDisabled(true);
            	txtBussiness.setDisabled(true);
            	txtEmailBussiness.setVisible(false);
            	btnSearch.setVisible(false);
                break;
            case WebConstants.EVENT_VIEW:
            	loadPreferenceClassifications(preferenceValueParam.getPreferenceClassficationId());
            	loadProduct(preferenceValueParam.getProductId());
            	loadTransactionType(preferenceValueParam.getTransactionTypeId());
            	txtBussiness.setText(preferenceValueParam.getBussinessId().toString());
            	blockFields();
                break;
            case WebConstants.EVENT_ADD:
            	loadPreferenceClassifications(null);
            	loadProduct(null);
            	loadTransactionType(null);
                break;
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

    public void onClick$btnSearch() {
    	BPBusinessWSProxy proxy = new BPBusinessWSProxy();
        try {
        	BpBusiness bpBussiness = proxy.getBusiness(BusinessSearchType.EMAIL, txtEmailBussiness.getText());
        		txtBussiness.setText(bpBussiness.getId().toString());    
        } catch (Exception e) {
        	this.showMessage("sp.specific.preference.error.search", true, null);  
        }
    }

}
