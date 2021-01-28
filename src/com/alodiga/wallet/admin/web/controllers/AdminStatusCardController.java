package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.model.StatusCard;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminStatusCardController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtCode;
    private Textbox txtDescription;
    private Tab tabStatusCardFinal;
    private UtilsEJB utilsEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private StatusCard statusCardParam;
    public static StatusCard statusCardParent = null;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            statusCardParam = null;
        } else {
            statusCardParam = (Sessions.getCurrent().getAttribute("object") != null) ? (StatusCard) Sessions.getCurrent().getAttribute("object") : null;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.status.card.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.status.card.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.status.card.add"));
                tabStatusCardFinal.setDisabled(true);
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
        txtDescription.setRawValue(null);
        txtCode.setRawValue(null);
    }
    
    public StatusCard getStatusCardParent(){
        return this.statusCardParent;
    }

    private void loadFields(StatusCard statusCard) {
        try {
            txtCode.setText(statusCard.getCode());
            txtDescription.setText(statusCard.getDescription());
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtCode.setReadonly(true);
        txtDescription.setReadonly(true);
        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (txtCode.getText().isEmpty()) {
            txtCode.setFocus(true);
            this.showMessage("msj.error.country.code", true, null);
        } else if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("msj.error.businessCategory.description", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveStatusCard(StatusCard _statusCard) {
        try {
            StatusCard statusCard = null;

            if (_statusCard != null) {
                statusCard = _statusCard;
            } else {
                statusCard = new StatusCard();
            }

            statusCard.setCode(txtCode.getText());
            statusCard.setDescription(txtDescription.getText());
            statusCard = utilsEJB.saveStatusCard(statusCard);
            statusCardParam = statusCard;
            statusCardParent = statusCard;
            tabStatusCardFinal.setDisabled(false);
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

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveStatusCard(statusCardParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveStatusCard(statusCardParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(statusCardParam);
                statusCardParent = statusCardParam;
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(statusCardParam);
                statusCardParent = statusCardParam;
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
