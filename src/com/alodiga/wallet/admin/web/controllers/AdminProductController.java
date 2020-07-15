package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.AccessControlEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.Bank;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.Enterprise;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public class AdminProductController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
//    private static final long serialVersionUID = -9145887024839938157L;
    private Textbox txtName;
    private Textbox txtCodeSwift;
    private Textbox txtAba;
    private Combobox cmbEnterprise;
    private Combobox cmbCountry;
    private UtilsEJB utilsEJB = null;
    private AccessControlEJB accessEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private Bank bankParam;
    private Integer eventType;
    private boolean editingPassword = false;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            bankParam = null;
        } else {
            bankParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Bank) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.user");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.bank.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.bank.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.bank.add"));
                break;
            default:
                break;
        }
        try {
            accessEJB = (AccessControlEJB) EJBServiceLocator.getInstance().get(EjbConstants.ACCESS_CONTROL_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtName.setRawValue(null);
    }

    private void loadFields(Bank bank) {
        try {
            txtName.setText(bank.getName());
//            txtCodeSwift.setText(bank.getCodeSwift());
            txtAba.setText(bank.getAba());
            
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
//        txtCodeSwift.setReadonly(true);
        txtAba.setReadonly(true);
        cmbCountry.setReadonly(true);
        cmbEnterprise.setReadonly(true);
        
        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
//        } else if (txtCodeSwift.getText().isEmpty()) {
//            txtCodeSwift.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void saveBank(Bank _bank) {
        try {
            Bank bank = null;

            if (_bank != null) {
                bank = _bank;
            } else {//New country
                bank = new Bank();
            }

            bank.setName(txtName.getText());
            bank.setEnterpriseId((Enterprise) cmbEnterprise.getSelectedItem().getValue());
            bank.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            bank.setAba(txtAba.getText());
//            bank.setCodeSwift(txtCodeSwift.getText());
            bank = utilsEJB.saveBank(bank);
            bankParam = bank;
            eventType = WebConstants.EVENT_EDIT;
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
                    saveBank(bankParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveBank(bankParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(bankParam);
                loadCmbCountry(eventType);
                loadCmbEnterprise(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(bankParam);
                blockFields();
                loadCmbCountry(eventType);
                loadCmbEnterprise(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbEnterprise(eventType);
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(bankParam != null ? bankParam.getCountryId().getId() : 0));
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

    private void loadCmbEnterprise(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Enterprise> enterprise;
        try {
            enterprise = utilsEJB.getEnterprises(request1);
            loadGenericCombobox(enterprise, cmbEnterprise, "name", evenInteger, Long.valueOf(bankParam != null ? bankParam.getEnterpriseId().getId() : 0));
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
}
