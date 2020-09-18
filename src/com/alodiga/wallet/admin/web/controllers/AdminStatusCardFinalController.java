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
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
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
import java.sql.Timestamp;
import java.util.Date;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class AdminStatusCardFinalController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblStatusCard;
    private Combobox cmbFinal;
    private UtilsEJB utilsEJB = null;
    private AccessControlEJB accessEJB = null;
    private Button btnSave;
    private Button btnAdd;
    private Toolbarbutton tbbTitle;
    private StatusCardHasFinalState statusCardFinalParam;
    public static StatusCard statusCardParent = null;
    private Integer eventType;
    public Window winStatusFinal;
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
    }

    @Override
    public void initialize() {
        super.initialize();
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
       lblStatusCard.setValue(statusCard.getDescription());
   }
   
   public void onClick$btnAdd() {
        btnAdd.setVisible(true);
        cmbFinal.setValue("");
        this.clearMessage();
    }
    
    public void clearFields() {
    }
    
    private void loadFields(StatusCardHasFinalState statusCardHasFinalState) {
        try {
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {;
        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (cmbFinal.getSelectedItem() == null) {
            cmbFinal.setFocus(true);
            this.showMessage("sp.error.status.card.hasFinal.error", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void saveStatusFinal(StatusCardHasFinalState _statusCardHasFinalState) {
        try {
            if (_statusCardHasFinalState != null) {
            	_statusCardHasFinalState.setUpdateDate(new Timestamp(new Date().getTime()));
            }else {
            	_statusCardHasFinalState = new StatusCardHasFinalState ();
            	_statusCardHasFinalState.setCreateDate(new Timestamp(new Date().getTime()));
            }
      
            _statusCardHasFinalState.setStatusCardId(statusCard);
            _statusCardHasFinalState.setStatusCardFinalStateId((StatusCard) cmbFinal.getSelectedItem().getValue());
            if ((eventType.equals(WebConstants.EVENT_ADD) && utilsEJB.validateStatusCardHasFinalState(_statusCardHasFinalState.getStatusCardId().getId(),_statusCardHasFinalState.getStatusCardFinalStateId().getId()))
					|| eventType.equals(WebConstants.EVENT_EDIT)){
                _statusCardHasFinalState =utilsEJB.saveStatusCardHasFinalState(_statusCardHasFinalState);
                this.showMessage("sp.common.save.success", false, null);
            }else
                                this.showMessage("sp.error.status.card.hasFinal.exist", true, null); 
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
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
    
      public void onClick$btnBack() {
        winStatusFinal.detach();
    }
     
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(statusCardFinalParam);
                loadCmbFinal(statusCardFinalParam);
                obtainStatusCard();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(statusCardFinalParam);
                loadCmbFinal(statusCardFinalParam);
                blockFields();
                obtainStatusCard();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbFinal(statusCardFinalParam);
                obtainStatusCard();
                break;
            default:
                break;
        }
    }
    
    private void loadCmbFinal(StatusCardHasFinalState _statusCard) {
        cmbFinal.getItems().clear();
        EJBRequest request1 = new EJBRequest();
        List<StatusCard> requests;
        try {
            requests = utilsEJB.getStatusCard(request1);
            loadGenericCombobox(requests, cmbFinal, "description", eventType, Long.valueOf(statusCardFinalParam != null ? statusCardFinalParam.getStatusCardFinalStateId().getId() : 0));
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
