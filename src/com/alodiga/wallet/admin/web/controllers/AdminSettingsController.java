package com.alodiga.wallet.admin.web.controllers;

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
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.model.Enterprise;
import com.alodiga.wallet.model.PreferenceField;
import com.alodiga.wallet.model.PreferenceValue;

public class AdminSettingsController extends GenericAbstractController implements GenericSPController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtTimeoutInactiveSession;
    private Textbox txtMaxWrongNumberIntentLogin;
    private Textbox txtMaxTransactionAmountLimit;
    private Textbox txtMaxTransactionAmountDailyLimit;
    private Textbox txtMaxTransactionNumberPerAccount;
    private Textbox txtMaxPromotionalTransactionLimit;
//    private ProductEJB productEJB = null;
//    private List<Period> periods = new ArrayList<Period>();
    private Combobox cmbPeriods;
    private Button btnSave;
    private Combobox cmbEnterprises;
    private Combobox cmbCycles;
    private Combobox cmbDefaultSMSProvider;
    private Checkbox chbEnableTransaction;
    private Label lblCurrency;
    private Textbox txtPointExchange;
    private Textbox txtRoyaltyPercent;
    private Combobox cmbClosurePeriod;
    private Long TIMEOUT_INACTIVE_SESSION_ID;
    private Long MAX_TRANSACTION_AMOUNT_LIMIT_ID;
    private Long MAX_WRONG_NUMBER_INTENT_LOGIN_ID;
    private Long MAX_TRANSACTION_NUMBER_PER_ACCOUNT_ID;
    private Long MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID;
    private Long DISABLED_TRANSACTION_ID;
    private Long PERIOD;
    private Long MAX_PROMOTIONAL_TRANSACTION_LIMIT;
    private Long DEFAULT_SMS_PROVIDER;
    private Long POINT_EXCHANGE;
    private Long ROYALTY_PERCENT;
    private Long INACTIVITY_HOLDBACK_PAYMENT;
    private Long CYCLES;


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();

    }

    @Override
    public void initialize() {
        try {
            loadEnterprises();
            onChange$cmbEnterprises();
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
        cmbPeriods.setReadonly(true);
        txtMaxTransactionAmountLimit.setReadonly(true);
        txtMaxTransactionAmountDailyLimit.setReadonly(true);
        txtMaxTransactionNumberPerAccount.setReadonly(true);
        txtMaxPromotionalTransactionLimit.setReadonly(true);
        cmbDefaultSMSProvider.setReadonly(true);
        txtPointExchange.setReadonly(true);
//        cmbCycles.setReadonly(true);
        btnSave.setVisible(false);
    }

    

    private void loadEnterprises() {
        List<Enterprise> enterprises = null;
        try {
            cmbEnterprises.getItems().clear();
//            enterprises = (List<Enterprise>) utilsEJB.getEnterprises();
            for (Enterprise e : enterprises) {
                Comboitem cmbItem = new Comboitem();
                cmbItem.setLabel(e.getName());
                cmbItem.setValue(e);
                cmbItem.setParent(cmbEnterprises);
            }
            cmbEnterprises.setSelectedIndex(0);
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
        } else if (cmbPeriods.getSelectedIndex() == -1) {
            cmbPeriods.setFocus(true);
            this.showMessage("sp.error.period.notSelected", true, null);
        } else if (txtMaxTransactionAmountLimit.getText().isEmpty()) {
            txtMaxTransactionAmountLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxTransactionAmountDailyLimit.getText().isEmpty()) {
            txtMaxTransactionAmountDailyLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxTransactionNumberPerAccount.getText().isEmpty()) {
            txtMaxTransactionNumberPerAccount.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaxPromotionalTransactionLimit.getText().isEmpty()) {
            txtMaxPromotionalTransactionLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (cmbDefaultSMSProvider.getSelectedIndex() == -1) {
            cmbDefaultSMSProvider.setFocus(true);
            this.showMessage("sp.error.smsprovider.notSelected", true, null);
        } else if (txtPointExchange.getText().isEmpty()) {
            txtPointExchange.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        }   else {
            return true;
        }
        return false;
    }

    public void onChange$cmbEnterprises() throws InterruptedException {
        this.clearMessage();
        Enterprise enterprise = null;
        if (cmbEnterprises.getSelectedItem() != null) {
            enterprise = (Enterprise) cmbEnterprises.getSelectedItem().getValue();
            lblCurrency.setValue(enterprise.getCurrencyId().getSymbol());
            loadPreferences(enterprise.getId());
        }
    }


    private void loadPreferences(Long enterpriseId) {
        try {
            setData();

//            List<PreferenceField> fields = preferencesEJB.getPreferenceFields(request);
//            for (PreferenceField field : fields) {
//                
//                PreferenceValue pValue = preferencesEJB.loadActivePreferenceValuesByEnterpriseIdAndFieldId(enterpriseId, field.getId());
//
//                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_LIMIT_ID)) {
//                    txtMaxTransactionAmountLimit.setText(pValue.getValue());
//                }
//                if (field.getId().equals(MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID)) {
//                    txtMaxTransactionAmountDailyLimit.setText(pValue.getValue());
//                }
//                if (field.getId().equals(MAX_WRONG_NUMBER_INTENT_LOGIN_ID)) {
//                    txtMaxWrongNumberIntentLogin.setText(pValue.getValue());
//                }
//                if (field.getId().equals(TIMEOUT_INACTIVE_SESSION_ID)) {
//                    txtTimeoutInactiveSession.setText(pValue.getValue());
//                }
//                if (field.getId().equals(PERIOD)) {
//                    loadPeriods(Long.parseLong(pValue.getValue()));
//                    cmbPeriods.setDisabled(true);
//                }
//
//                if (field.getId().equals(MAX_TRANSACTION_NUMBER_PER_ACCOUNT_ID)) {
//                    txtMaxTransactionNumberPerAccount.setText(pValue.getValue());
//                }
//
//                if (field.getId().equals(DISABLED_TRANSACTION_ID)) {
//                    boolean checked = Integer.parseInt(pValue.getValue()) == 1 ? true : false;
//                    chbEnableTransaction.setChecked(checked);
//                }
//                if (field.getId().equals(MAX_PROMOTIONAL_TRANSACTION_LIMIT)) {
//                    txtMaxPromotionalTransactionLimit.setText(pValue.getValue());
//                }
//                if (field.getId().equals(DEFAULT_SMS_PROVIDER)) {
//                    loadProviders(Long.parseLong(pValue.getValue()));
//                }
//                if (field.getId().equals(POINT_EXCHANGE)) {
//                    txtPointExchange.setText(pValue.getValue());
//                }
//            }
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void setData() {
//        MAX_TRANSACTION_AMOUNT_LIMIT_ID = PreferenceFieldEnum.MAX_TRANSACTION_AMOUNT_LIMIT.getId();
//        MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID = PreferenceFieldEnum.MAX_TRANSACTION_AMOUNT_DAILY_LIMIT.getId();
//        MAX_TRANSACTION_NUMBER_PER_ACCOUNT_ID = PreferenceFieldEnum.MAX_TRANSACTION_LIMIT_PER_ACCOUNT.getId();
//        MAX_WRONG_NUMBER_INTENT_LOGIN_ID = PreferenceFieldEnum.MAX_WRONG_LOGIN_INTENT_NUMBER.getId();
//        TIMEOUT_INACTIVE_SESSION_ID = PreferenceFieldEnum.TIMEOUT_INACTIVE_SESSION.getId();
//        PERIOD = PreferenceFieldEnum.PERIOD.getId();
////        CYCLES = PreferenceFieldEnum.CYCLES.getId();
//        DISABLED_TRANSACTION_ID = PreferenceFieldEnum.DISABLED_TRANSACTION.getId();
//        MAX_PROMOTIONAL_TRANSACTION_LIMIT = PreferenceFieldEnum.MAX_PROMOTION_TRANSACTION_DAILY_LIMIT.getId();
//        DEFAULT_SMS_PROVIDER = PreferenceFieldEnum.DEFAULT_SMS_PROVIDER.getId();
//        POINT_EXCHANGE = PreferenceFieldEnum.POINT_EXCHANGE.getId();
    }

    private void savePreferenceValues() {
        try {
            Enterprise enterprise = (Enterprise) cmbEnterprises.getSelectedItem().getValue();
            int enableTransaction = chbEnableTransaction.isChecked() ? 1 : 0;
//            EJBRequest _request = new EJBRequest();
//            List<PreferenceValue> preferenceValues = new ArrayList<PreferenceValue>();
//            preferenceValues.add(createPreferenceValue(new PreferenceField(MAX_TRANSACTION_AMOUNT_LIMIT_ID), txtMaxTransactionAmountLimit.getText(), enterprise));
//            preferenceValues.add(createPreferenceValue(new PreferenceField(MAX_TRANSACTION_NUMBER_PER_ACCOUNT_ID), txtMaxTransactionNumberPerAccount.getText(), enterprise));
//            preferenceValues.add(createPreferenceValue(new PreferenceField(MAX_WRONG_NUMBER_INTENT_LOGIN_ID), txtMaxWrongNumberIntentLogin.getText(), enterprise));
//            preferenceValues.add(createPreferenceValue(new PreferenceField(TIMEOUT_INACTIVE_SESSION_ID), txtTimeoutInactiveSession.getText(), enterprise));
//            preferenceValues.add(createPreferenceValue(new PreferenceField(PERIOD), ((Period) cmbPeriods.getSelectedItem().getValue()).getId().toString(), enterprise));
////            preferenceValues.add(createPreferenceValue(new PreferenceField(CYCLES), cmbCycles.getValue(),enterprise));
//            preferenceValues.add(createPreferenceValue(new PreferenceField(DISABLED_TRANSACTION_ID), "" + enableTransaction, enterprise));
//            preferenceValues.add(createPreferenceValue(new PreferenceField(MAX_PROMOTIONAL_TRANSACTION_LIMIT), txtMaxPromotionalTransactionLimit.getText(), enterprise));
//            preferenceValues.add(createPreferenceValue(new PreferenceField(MAX_TRANSACTION_AMOUNT_DAILY_LIMIT_ID), txtMaxTransactionAmountDailyLimit.getText(), enterprise));
//            Provider provider = (Provider) cmbDefaultSMSProvider.getSelectedItem().getValue();
//            preferenceValues.add(createPreferenceValue(new PreferenceField(DEFAULT_SMS_PROVIDER), provider.getId().toString(), enterprise));
//            preferenceValues.add(createPreferenceValue(new PreferenceField(POINT_EXCHANGE), txtPointExchange.getText(), enterprise));
//            _request.setParam(preferenceValues);
//            preferencesEJB.savePreferenceValues(_request);
//            PreferenceManager preferenceManager = PreferenceManager.getInstance();
//            preferenceManager.refresh();
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private PreferenceValue createPreferenceValue(PreferenceField field, String value, Enterprise enterprise) {
        PreferenceValue pv = new PreferenceValue();
        pv.setPreferenceFieldId(field);
        pv.setValue(value);
        pv.setEnterpriseId(enterprise);
        return pv;
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            savePreferenceValues();
        }

    }

//    @Override
//    public void loadPermission(AbstractSPEntity clazz) throws GeneralException {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
}
