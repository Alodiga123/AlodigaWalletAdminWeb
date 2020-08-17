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
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Category;
import com.alodiga.wallet.common.model.Enterprise;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.StatusCard;
import com.alodiga.wallet.common.model.StatusCardHasFinalState;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class AdminStatusCardFinalController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbEnterprise;
    private Textbox txtStatus;
    private UtilsEJB utilsEJB = null;
    private AccessControlEJB accessEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private StatusCardHasFinalState statusCardFinalParam;
    public static StatusCard statusCardParent = null;
    private Integer eventType;
    public Window winStatusCardFinal;
    private StatusCard statusCard = null;
    private Profile currentProfile;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            statusCardFinalParam = null;
        } else {
            statusCardFinalParam = (Sessions.getCurrent().getAttribute("object") != null) ? (StatusCardHasFinalState) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.produt");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
//                disableTab();
                tbbTitle.setLabel(Labels.getLabel("sp.crud.product.edit"));
                break;
            case WebConstants.EVENT_VIEW:
//                disableTab();
                tbbTitle.setLabel(Labels.getLabel("sp.crud.product.view"));
                break;
            case WebConstants.EVENT_ADD:
                //disableTab();
                tbbTitle.setLabel(Labels.getLabel("sp.crud.product.add"));
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
    
   public void obtainStatusCard(){
       this.clearMessage();
       AdminStatusCardController adminStatusCard = new AdminStatusCardController();
       if (adminStatusCard.getStatusCardParent().getId() != null){
           statusCard = adminStatusCard.getStatusCardParent();
       }    
   }
    
    public void clearFields() {
    }

    private void loadFields(StatusCardHasFinalState statusCardHasFinalState) {
        try {

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtStatus.setReadonly(true);
        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        this.showMessage("", false, null);
        return true;

    }

    private void saveStatusFinal(StatusCardHasFinalState _statusCardHasFinalState) {
        try {
        
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
                    saveStatusFinal(statusCardFinalParam);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveStatusFinal(statusCardFinalParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(statusCardFinalParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(statusCardFinalParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }

}
