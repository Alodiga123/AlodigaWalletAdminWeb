package com.alodiga.wallet.admin.web.controllers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.StatusBusinessAffiliationHasFinalState;
import com.alodiga.wallet.common.model.StatusBusinessAffiliationRequest;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;

public class AdminStatusBusinessAffiliationController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbStatus;
    private Combobox cmbFinal;
    private UtilsEJB utilsEJB = null;
    private StatusBusinessAffiliationHasFinalState statusParam;
    private Button btnSave;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        statusParam = (Sessions.getCurrent().getAttribute("object") != null) ? (StatusBusinessAffiliationHasFinalState) Sessions.getCurrent().getAttribute("object") : null;
        initialize();
        initView(eventType, "sp.crud.status.business.affiliation");
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

    public void clearFields() {
        btnSave.setVisible(false);
    }

    private void loadFields(StatusBusinessAffiliationHasFinalState state) {
        btnSave.setVisible(true);
    }

   
    
    public void blockFields() {
        btnSave.setVisible(false);
    }

    private void saveStatus(StatusBusinessAffiliationHasFinalState _status) {

        try {
            if (_status != null) {
            	_status.setUpdateDate(new Timestamp(new Date().getTime()));
            }else {
            	_status = new StatusBusinessAffiliationHasFinalState();
            	_status.setCreateDate(new Timestamp(new Date().getTime()));
            }
			_status.setStatusBusinessAffiliationRequetsId((StatusBusinessAffiliationRequest) cmbStatus.getSelectedItem().getValue());
			_status.setFinalStateId((StatusBusinessAffiliationRequest) cmbFinal.getSelectedItem().getValue());
			if (utilsEJB.validateStatusBusinessAffiliationHasFinalState(_status.getStatusBusinessAffiliationRequetsId().getId(),_status.getFinalStateId().getId()))
			_status = utilsEJB.saveStatusBusinessAffiliationHasFinalState(_status);
			this.showMessage("sp.common.save.success", false, null);
			if (eventType == WebConstants.EVENT_ADD) {
				btnSave.setVisible(false);
			 } 
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            showError(ex);
        }
    }

   
    public Boolean validateEmpty() {
        if (cmbStatus.getSelectedItem() == null) {
        	cmbStatus.setFocus(true);
            this.showMessage("sp.error.status.business.affiliation.init", true, null);
        } else if (cmbFinal.getText().isEmpty()) {
            cmbFinal.setFocus(true);
            this.showMessage("sp.error.status.business.affiliation.final", true, null);
        } else {
            return true;
        }
        return false;
    }

    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveStatus(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveStatus(statusParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(statusParam);
                loadCmbStatus(statusParam);
                loadCmbFinal(statusParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(statusParam);
                blockFields();
                loadCmbStatus(statusParam);
                loadCmbFinal(statusParam);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbStatus(statusParam);
                loadCmbFinal(statusParam);
                break;
            default:
                break;
        }
    }

    private void loadCmbStatus(StatusBusinessAffiliationHasFinalState _status) {
        EJBRequest request1 = new EJBRequest();
        List<StatusBusinessAffiliationRequest> requests;
        try {
        	requests = utilsEJB.getStatusBusinessAffiliationRequest(request1);
            loadGenericCombobox(requests, cmbStatus, "description", eventType, Long.valueOf(statusParam != null ? statusParam.getStatusBusinessAffiliationRequetsId().getId() : 0));
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

    private void loadCmbFinal(StatusBusinessAffiliationHasFinalState _status) {
        EJBRequest request1 = new EJBRequest();
        List<StatusBusinessAffiliationRequest> requests;
        try {
        	requests = utilsEJB.getStatusBusinessAffiliationRequest(request1);
            loadGenericCombobox(requests, cmbFinal, "description", eventType, Long.valueOf(statusParam != null ? statusParam.getFinalStateId().getId() : 0));
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
