package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.model.Preference;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminPreferenceBasicController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtPreferenceName;
    private Radio rIsEnabledYes;
    private Radio rIsEnabledNo;
    private Textbox txtPreferenceDescription;
    private PreferencesEJB preferencesEJB = null;
    private Preference preferenceParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            preferenceParam = null;
        } else {
            preferenceParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Preference) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.preferenceBasic");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.preferenceBasic.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.preferenceBasic.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.preferenceBasic.add"));
                break;
            default:
                break;
        }
        try {
            preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtPreferenceName.setRawValue(null);
        txtPreferenceDescription.setRawValue(null);
    }

    private void loadFields(Preference preference) {
        try {
            txtPreferenceName.setText(preference.getName());
            txtPreferenceDescription.setText(preference.getDescription());
            if(preference.getEnabled() == true){
                rIsEnabledYes.setChecked(true);
            } else {
                rIsEnabledNo.setChecked(true);
            }

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtPreferenceName.setReadonly(true);
        txtPreferenceDescription.setReadonly(true);
        rIsEnabledYes.setDisabled(true);
        rIsEnabledNo.setDisabled(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtPreferenceName.getText().isEmpty()) {
            txtPreferenceName.setFocus(true);
            this.showMessage("msj.error.preferenceBasic.name", true, null);
        }  else if (txtPreferenceDescription.getText().isEmpty()){
            txtPreferenceDescription.setFocus(true);
            this.showMessage("msj.error.preferenceBasic.description", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void savePreference(Preference preference_) {
        try {
            Preference preference = null;
             boolean indEnable  = true;

            if (preference_ != null) {
                preference = preference_;
            } else {//New requestType
                preference = new Preference();
            }
            
            if(rIsEnabledYes.isChecked()){
                indEnable = true;
            } else {
                indEnable = false;
            }

            preference.setName(txtPreferenceName.getText());
            preference.setDescription(txtPreferenceDescription.getText());
            preference.setEnabled(indEnable);
            preference = preferencesEJB.savePreference(preference);
            preferenceParam = preference;
            this.showMessage("wallet.msj.save.success", false, null);

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
                    savePreference(preferenceParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    savePreference(preferenceParam);
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(preferenceParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(preferenceParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
