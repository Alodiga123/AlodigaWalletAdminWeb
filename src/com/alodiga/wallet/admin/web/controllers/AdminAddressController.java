package com.alodiga.wallet.admin.web.controllers;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.model.Address;
import com.alodiga.wallet.common.model.BusinessAffiliationRequets;
import com.alodiga.wallet.common.model.PersonHasAddress;
import java.text.SimpleDateFormat;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

public class AdminAddressController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private Label lblCountry;
    private Label lblCounty;
    private Label lblCity;
    private Label lblUbanization;
    private Label lblStreetType;
    private Label lblNameStreet;
    private Label lblEdificationType;
    private Label lblNameEdification;
    private Label lblTower;
    private Label lblFloor;
    private Label lblZipCode;
    private Label lblAddressTypes;
    private Label lblAddressLine1;
    private Label lblAddressLine2;
    private Radio rMainAddressYes;
    private Radio rMainAddressNo;
    public static Address addressParent = null;
    private PersonHasAddress personHasAddressParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminPersonAddress;
    private BusinessAffiliationRequets afilationRequest;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);

        if (eventType == WebConstants.EVENT_ADD) {
            personHasAddressParam = null;
        } else {
            personHasAddressParam = (PersonHasAddress) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {

    }

    private void loadFieldR() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {

            AdminBusinnessAffiliationRequestsNaturalController adminRequestN = new AdminBusinnessAffiliationRequestsNaturalController();
            AdminBusinnessAffiliationRequestsLegalController adminRequestL = new AdminBusinnessAffiliationRequestsLegalController();
            if (adminRequestN.getBusinessAffiliationRequets() != null){
                afilationRequest = adminRequestN.getBusinessAffiliationRequets();
            }
            if (adminRequestL.getBusinessAffiliationRequets() != null){
                afilationRequest = adminRequestL.getBusinessAffiliationRequets();
            }

            if (afilationRequest != null) {
                lblRequestNumber.setValue(afilationRequest.getNumberRequest());
                lblRequestDate.setValue(simpleDateFormat.format(afilationRequest.getDateRequest()));
                lblStatusRequest.setValue(afilationRequest.getStatusBusinessAffiliationRequestId().getDescription());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFields(PersonHasAddress personHasAddress) {
        try {
            
            lblCountry.setValue(personHasAddress.getAddressId().getCountryId().getName());
            lblCounty.setValue(personHasAddress.getAddressId().getCountyId().getName());
            lblCity.setValue(personHasAddress.getAddressId().getCityId().getName());
            
            if (personHasAddress.getAddressId().getUrbanization() != null) {
                lblUbanization.setValue(personHasAddress.getAddressId().getUrbanization());
            }
            lblStreetType.setValue(personHasAddress.getAddressId().getStreetTypeId().getDescription());
            if (personHasAddress.getAddressId().getNameStreet() != null) {
                lblNameStreet.setValue(personHasAddress.getAddressId().getNameStreet());
            }
            lblEdificationType.setValue(personHasAddress.getAddressId().getEdificationTypeId().getDescription());
            if (personHasAddress.getAddressId().getNameEdification() != null) {
                lblNameEdification.setValue(personHasAddress.getAddressId().getNameEdification());
            }
            if (personHasAddress.getAddressId().getTower() != null) {
                lblTower.setValue(personHasAddress.getAddressId().getTower());
            }
            if (personHasAddress.getAddressId().getFloor() != null) {
                lblFloor.setValue(personHasAddress.getAddressId().getFloor().toString());
            }
            lblZipCode.setValue(personHasAddress.getAddressId().getZipCode());
            lblAddressTypes.setValue(personHasAddress.getAddressId().getAddressTypeId().getDescription());
            if (personHasAddress.getAddressId().getIndMainAddress() == true) {
                rMainAddressYes.setChecked(true);
            } else {
                rMainAddressNo.setChecked(true);
            }
            if (personHasAddress.getAddressId().getAddressLine1() != null) {
                lblAddressLine1.setValue(personHasAddress.getAddressId().getAddressLine1());
            }
            if (personHasAddress.getAddressId().getAddressLine2() != null) {
                lblAddressLine2.setValue(personHasAddress.getAddressId().getAddressLine2());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        rMainAddressYes.setDisabled(true);
        rMainAddressNo.setDisabled(true);
        btnSave.setVisible(false);
    }

    public void onClick$btnSave() {
    }

    public void onClick$btnBack() {
        winAdminPersonAddress.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFieldR();
                loadFields(personHasAddressParam);
                blockFields();
                break;
            case WebConstants.EVENT_VIEW:
                loadFieldR();
                loadFields(personHasAddressParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadFieldR();
                break;
        }
    }
}
