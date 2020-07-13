package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.model.Currency;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public class AdminCurrencyController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtNameMoney;
    private Textbox txtSymbolMoney;
    private UtilsEJB utilsEJB = null;
    private Currency currencyParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            currencyParam = null;
        } else {
            currencyParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Currency) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.user");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.currency.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.currency.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.currency.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtNameMoney.setRawValue(null);
        txtSymbolMoney.setRawValue(null);
    }

    private void loadFields(Currency currency) {
        try {
            txtNameMoney.setText(currency.getName());
            txtSymbolMoney.setText(currency.getSymbol());

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtNameMoney.setReadonly(true);
        txtSymbolMoney.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtNameMoney.getText().isEmpty()) {
            txtNameMoney.setFocus(true);
            this.showMessage("sp.error.currency", true, null);
        } else if (txtSymbolMoney.getText().isEmpty()) {
            txtSymbolMoney.setFocus(true);
            this.showMessage("sp.error.symbolCurrency", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveCurrency(Currency currency_) {
        try {
            Currency currency = null;

            if (currency_ != null) {
                currency = currency_;
            } else {//New requestType
                currency = new Currency();
            }

            currency.setName(txtNameMoney.getText());
            currency.setSymbol(txtSymbolMoney.getText());
            currency = utilsEJB.saveCurrency(currency);
            currencyParam = currency;
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

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveCurrency(currencyParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCurrency(currencyParam);
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(currencyParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(currencyParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
