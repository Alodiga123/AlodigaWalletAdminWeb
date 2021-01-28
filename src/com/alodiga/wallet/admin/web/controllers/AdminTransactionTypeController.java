package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.ejb.PreferencesEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.Preference;
import com.alodiga.wallet.common.model.TransactionType;
import com.alodiga.wallet.common.model.PreferenceType;
import com.alodiga.wallet.common.utils.Constants;

import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminTransactionTypeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtValue;
    private Textbox txtCode;
    private Textbox txtDescription;
    private UtilsEJB utilsEJB = null;
    private PreferencesEJB preferencesEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private TransactionType transactionTypeParam;
    private Integer eventType;
    List<TransactionType> transactionTypeList = new ArrayList<TransactionType>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            transactionTypeParam = null;
        } else {
            transactionTypeParam = (Sessions.getCurrent().getAttribute("object") != null) ? (TransactionType) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.transactionType.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.transactionType.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("walet.crud.transactionType.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            preferencesEJB = (PreferencesEJB) EJBServiceLocator.getInstance().get(EjbConstants.PREFERENCES_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void clearFields() {
        txtValue.setRawValue(null);
    }

    private void loadFields(TransactionType transactionType) {
        try {
            txtValue.setText(transactionType.getValue());
            txtCode.setText(transactionType.getCode());  
            txtDescription.setText(transactionType.getDescription()); 
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
       txtValue.setReadonly(true);
       txtCode.setReadonly(true); 
       txtDescription.setReadonly(true);
       btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (txtCode.getText().isEmpty()) {
            txtCode.setFocus(true);
            this.showMessage("msj.error.transactionType.code", true, null);
        } else if (txtValue.getText().isEmpty()) {
            txtValue.setFocus(true);
            this.showMessage("msj.error.transactionType.value", true, null);
        } else if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("msj.error.transactionType.description", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public boolean validateTransactionTypeByCode() {
        TransactionType transactionType = null;
        transactionTypeList.clear();
        try{
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PARAM_CODE, txtCode.getText());
            request.setParams(params);
            transactionTypeList = preferencesEJB.getTransactionTypeByCode(request);
        } catch (Exception ex) {
            showError(ex);
        } if(transactionTypeList.size() > 0){
            this.showMessage("msj.error.preferences.code", true, null);
                txtCode.setFocus(true);
                return false;
        }
        return true;
    }
    

    private void saveTransactionType(TransactionType _transactionType) {
        try {
            TransactionType transactions = null;
            short indEnable = 1;

            if (_transactionType!= null) {
                transactions = _transactionType;
            } else {
                transactions = new TransactionType();
            }

            transactions.setValue(txtValue.getText());
            transactions.setDescription(txtDescription.getText());
            transactions.setCode(txtCode.getText());
            request.setParam(transactions);
            transactions = preferencesEJB.saveTransactionType(request);
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
                    if(validateTransactionTypeByCode()){
                      saveTransactionType(null);  
                    }
                    break;
                case WebConstants.EVENT_EDIT:
                      saveTransactionType(transactionTypeParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(transactionTypeParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(transactionTypeParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }

}
