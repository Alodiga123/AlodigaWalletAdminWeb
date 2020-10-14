package com.alodiga.wallet.admin.web.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractController;
import com.alodiga.wallet.admin.web.generic.controllers.GenericSPController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PreferenceManager;
import com.alodiga.wallet.common.model.PreferenceClassification;
import com.alodiga.wallet.common.model.PreferenceControl;
import com.alodiga.wallet.common.model.PreferenceField;
import com.alodiga.wallet.common.model.PreferenceFieldEnum;
import com.alodiga.wallet.common.model.PreferenceValue;
import com.alodiga.wallet.common.model.Provider;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class AdminSettingsController extends GenericAbstractController implements GenericSPController {

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
    private Long languageId;
    
    private Button btnSave;
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
            if (eventType != null && eventType == WebConstants.EVENT_VIEW) {
                blockFields();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void blockFields() {
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
        btnSave.setVisible(false);
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
        if (txtTimeoutInactiveSession.getText().isEmpty()) {
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
        if (cmbClassification.getSelectedItem() != null) {
        	preferenceClassification = (PreferenceClassification) cmbClassification.getSelectedItem().getValue();
            loadPreferences(preferenceClassification.getId());
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
                    preferenceValues.add(pValue);
                }
                if (field.getId().equals(TIMEOUT_INACTIVE_SESSION_ID)) {
                	lblTimeoutInactiveSession.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtTimeoutInactiveSession.setText(pValue.getValue());
                    preferenceValues.add(pValue);
                }
                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_LIMIT_ID)) {
                	lblMaxTransactionAmountLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountLimit.setText(pValue.getValue());
                	preferenceValues.add(pValue);
                }
                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID)) {
                	lblMaxTransactionAmountDailyLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountDailyLimit.setText(pValue.getValue());
                }
                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_MONTH_LIMIT_ID)) {
                	lblMaxTransactionAmountMonthLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountMonthLimit.setText(pValue.getValue());
                	preferenceValues.add(pValue);
                }

                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_YEAR_LIMIT_ID)) {
                	lblMaxTransactionAmountYearLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionAmountYearLimit.setText(pValue.getValue());
                	preferenceValues.add(pValue);
                }
                if (field.getId().equals(DISABLED_TRANSACTION_ID)) {
                	lblEnabledTransaction.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	boolean checked = Integer.parseInt(pValue.getValue()) == 1 ? true : false;
                	chbEnableTransaction.setChecked(checked);
                	preferenceValues.add(pValue);
                }
                if (field.getId().equals(DEFAULT_SMS_PROVIDER_ID)) {
                	lblDefaultSMSProvider.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    loadProviders(Long.parseLong(pValue.getValue()));
                    preferenceValues.add(pValue);
                }
                if (field.getId().equals(MAX_TRANSACTION_QUANTITY_DAILY_LIMIT_ID)) {
                	lblMaxTransactionQuantityDailyLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                	txtMaxTransactionQuantityDailyLimit.setText(pValue.getValue());
                	preferenceValues.add(pValue);
                }
                if (field.getId().equals(MAX_TRANSACTION_QUANTITY_MONTH_LIMIT_ID)) {
                	lblMaxTransactionQuantityMonthLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtMaxTransactionQuantityMonthLimit.setText(pValue.getValue());
                    preferenceValues.add(pValue);
                }
                if (field.getId().equals(MAX_TRANSACTION_QUANTITY_YEAR_LIMIT_ID)) {
                	lblMaxTransactionQuantityYearLimit.setValue(field.getPreferenceFieldDataByLanguageId(languageId).getDescription());
                    txtMaxTransactionQuantityYearLimit.setText(pValue.getValue());
                    preferenceValues.add(pValue);
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
            int enableTransaction = chbEnableTransaction.isChecked() ? 1 : 0;
            List<PreferenceValue> preferenceValueSave = new ArrayList<PreferenceValue>();
            List<PreferenceControl>  preferenceControls = new ArrayList<PreferenceControl>();
            for(PreferenceValue pvalue: preferenceValues) {
            	if (pvalue.getPreferenceFieldId().getId().equals(TIMEOUT_INACTIVE_SESSION_ID) && !pvalue.getValue().equals(txtTimeoutInactiveSession.getText())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,txtTimeoutInactiveSession.getText()));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_WRONG_NUMBER_INTENT_LOGIN_ID) && !pvalue.getValue().equals(txtMaxWrongNumberIntentLogin.getText())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxWrongNumberIntentLogin.getText()));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(DISABLED_TRANSACTION_ID) && !pvalue.getValue().equals("" +enableTransaction)) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,"" +enableTransaction));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_LIMIT_ID) && !pvalue.getValue().equals(txtMaxTransactionAmountLimit.getText())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionAmountLimit.getText()));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID) && !pvalue.getValue().equals(txtMaxTransactionAmountDailyLimit.getText())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionAmountDailyLimit.getText()));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_MONTH_LIMIT_ID) && !pvalue.getValue().equals(txtMaxTransactionAmountMonthLimit.getText())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionAmountMonthLimit.getText()));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_AMOUNT_YEAR_LIMIT_ID) && !pvalue.getValue().equals(txtMaxTransactionAmountYearLimit.getText())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionAmountYearLimit.getText()));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_QUANTITY_DAILY_LIMIT_ID) && !pvalue.getValue().equals(txtMaxTransactionQuantityDailyLimit.getText())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionQuantityDailyLimit.getText()));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_QUANTITY_MONTH_LIMIT_ID) && !pvalue.getValue().equals(txtMaxTransactionQuantityMonthLimit.getText())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionQuantityMonthLimit.getText()));
            	}
            	if (pvalue.getPreferenceFieldId().getId().equals(MAX_TRANSACTION_QUANTITY_YEAR_LIMIT_ID) && !pvalue.getValue().equals(txtMaxTransactionQuantityYearLimit.getText())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,txtMaxTransactionQuantityYearLimit.getText()));
            	}
            	Provider provider = (Provider) cmbDefaultSMSProvider.getSelectedItem().getValue();
            	if (pvalue.getPreferenceFieldId().getId().equals(DEFAULT_SMS_PROVIDER_ID) && !pvalue.getValue().equals(provider.getId().toString())) {
            		preferenceControls.add(createPreferencenControl(pvalue));
                	preferenceValueSave.add(updatePreferencenValue(pvalue,provider.getId().toString()));
            	}    
          
            }
            preferencesEJB.savePreferenceValues(preferenceValueSave,preferenceControls);
//            PreferenceManager preferenceManager = PreferenceManager.getInstance();
//            preferenceManager.refresh();
            this.showMessage("sp.common.save.success", false, null);
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
    
    private PreferenceValue updatePreferencenValue(PreferenceValue preferenceValue,String value) {
    	preferenceValue.setValue(value);
    	preferenceValue.setUpdateDate(new Timestamp(new Date().getTime()));
    	return preferenceValue;
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            savePreferenceValues();
        }

    }

}
