package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.model.PreferenceType;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public class AdminPreferenceTypeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtPreferenceType;
    private PreferencesEJB preferencesEJB = null;
    private PreferenceType preferenceTypeParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            preferenceTypeParam = null;
        } else {
            preferenceTypeParam = (Sessions.getCurrent().getAttribute("object") != null) ? (PreferenceType) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.preferenceType");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.preferenceType.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.preferenceType.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.preferenceType.add"));
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
        txtPreferenceType.setRawValue(null);
    }

    private void loadFields(PreferenceType preferenceType) {
        try {
            txtPreferenceType.setText(preferenceType.getType());
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtPreferenceType.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtPreferenceType.getText().isEmpty()) {
            txtPreferenceType.setFocus(true);
            this.showMessage("sp.error.preferenceType.type", true, null);
        }  else {
            return true;
        }
        return false;
    }

    private void savePreferenceType(PreferenceType preferenceType_) {
        try {
            PreferenceType preferenceType = null;

            if (preferenceType_ != null) {
                preferenceType = preferenceType_;
            } else {//New requestType
                preferenceType = new PreferenceType();
            }

            preferenceType.setType(txtPreferenceType.getText());
            preferenceType = preferencesEJB.savePreferencesType(preferenceType);
            preferenceTypeParam = preferenceType;
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
                    savePreferenceType(preferenceTypeParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    savePreferenceType(preferenceTypeParam);
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(preferenceTypeParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(preferenceTypeParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
