package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.exception.DuplicateEntryException;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminCountryController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private Textbox txtShortName;
    private Textbox txtCode;
    private Textbox txtAlternativeName1;
    private Textbox txtAlternativeName2;
    private Textbox txtAlternativeName3;
    private UtilsEJB utilsEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private Country countryParam;
    private Integer eventType;
    List<Country> countryCodeList = new ArrayList<Country>();
    List<Country> countryNameList = new ArrayList<Country>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            countryParam = null;
        } else {
            countryParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Country) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.country");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.country.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.country.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.country.add"));
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
        txtName.setRawValue(null);
        txtShortName.setRawValue(null);
        txtCode.setRawValue(null);
        txtAlternativeName1.setRawValue(null);
        txtAlternativeName2.setRawValue(null);
        txtAlternativeName3.setRawValue(null);
    }

    private void loadFields(Country country) {
        try {
            txtName.setText(country.getName());
            txtShortName.setText(country.getShortName());
            txtCode.setText(country.getCode());
            if (country.getAlternativeName1() != null){
                txtAlternativeName1.setText(country.getAlternativeName1());
            }
            if (country.getAlternativeName2() != null){
                txtAlternativeName2.setText(country.getAlternativeName2());
            }
            if (country.getAlternativeName3() != null){
                txtAlternativeName3.setText(country.getAlternativeName3());
            }

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        txtShortName.setReadonly(true);
        txtCode.setReadonly(true);
        txtAlternativeName1.setReadonly(true);
        txtAlternativeName2.setReadonly(true);
        txtAlternativeName3.setReadonly(true);

        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.country.name", true, null);
        } else if (txtShortName.getText().isEmpty()) {
            txtShortName.setFocus(true);
            this.showMessage("sp.error.country.shortName", true, null);
        } else if (txtCode.getText().isEmpty()) {
            txtCode.setFocus(true);
            this.showMessage("sp.error.country.code", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public boolean validateCountry() {
     try {
            //Valida si ya existe el codigo del pais
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PARAM_CODE, txtCode.getValue());
            request1.setParams(params);
            countryCodeList = utilsEJB.getValidateCountryByCode(request1);
        } catch (Exception ex) {
            showError(ex);
        } finally {
            if(countryCodeList.size() > 0){
            this.showMessage("sp.crud.country.error.code", true, null);
            txtCode.setFocus(true);  
            return false;  
          }    
        } try {
                //Valida si el nombre del pais ingresado ya existe en BD
                EJBRequest request1 = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.PARAM_NAME, txtName.getValue());
                request1.setParams(params);
                countryNameList= utilsEJB.getValidateCountryByName(request1);
            } catch (Exception ex) {
                showError(ex);
            } finally {
                if (countryNameList.size() > 0) {
                    this.showMessage("sp.crud.country.error.name", true, null);
                    txtName.setFocus(true);
                    return false;
                }
            }                        
        return true;
    }

    private void saveCountry(Country _country) {
        try {
            Country country = null;

            if (_country != null) {
                country = _country;
            } else {//New country
                country = new Country();
            }

            country.setName(txtName.getText());
            country.setShortName(txtShortName.getText());
            country.setCode(txtCode.getText());
            if (txtAlternativeName1.getValue() != null) {
                country.setAlternativeName1(txtAlternativeName1.getText());
            }
            if (txtAlternativeName2.getValue() != null) {
                country.setAlternativeName2(txtAlternativeName2.getText());
            }
            if (txtAlternativeName3.getValue() != null) {
                country.setAlternativeName3(txtAlternativeName3.getText());
            }

            try {
                country = utilsEJB.saveNewCountry(country);
                countryParam = country;
                eventType = WebConstants.EVENT_EDIT;
                this.showMessage("sp.common.save.success", false, null);
            } catch (DuplicateEntryException e) {
                this.showMessage("sp.common.save.fail", false, null);
                return;
            }

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
                    if (validateCountry()){
                        saveCountry(countryParam);    
                    }
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCountry(countryParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(countryParam);;
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(countryParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
