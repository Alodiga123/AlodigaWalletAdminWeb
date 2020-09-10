package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.CalendarDays;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.ExchangeRate;
import com.alodiga.wallet.common.model.Product;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.Date;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminOfHolidaysController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbCountry;
    private Datebox dtbHolidayDate;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private CalendarDays calendarDaysParam;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            calendarDaysParam = null;
        } else {
            calendarDaysParam = (Sessions.getCurrent().getAttribute("object") != null) ? (CalendarDays) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.calendarDays");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.calendarDays.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.calendarDays.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.calendarDays.add"));
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
        dtbHolidayDate.setRawValue(null);
    }

    private void loadFields(CalendarDays calendarDays) {
        try {
            txtName.setValue(calendarDays.getDescription());
            dtbHolidayDate.setValue(calendarDays.getHolidayDate());
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setDisabled(true);
        dtbHolidayDate.setDisabled(true);
        cmbCountry.setReadonly(true);
        btnSave.setVisible(false);
    }

    public boolean validateEmpty() {
        Date today = new Date();

        if (cmbCountry.getSelectedItem() == null) {
            cmbCountry.setFocus(true);
            this.showMessage("sp.error.calendarDays.notSelected", true, null);
        } else if (dtbHolidayDate.getText().isEmpty()) {
            dtbHolidayDate.setFocus(true);
            this.showMessage("sp.error.calendarDays.value", true, null);
        } else if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.calendarDays.description", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void saveCalendarDays(CalendarDays _calendarDays) {
        try {
            CalendarDays calendarDays = null;

            if (_calendarDays != null) {
                calendarDays = _calendarDays;
                _calendarDays.setUpdateDate(dtbHolidayDate.getValue());
                _calendarDays.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
                _calendarDays.setHolidayDate(dtbHolidayDate.getValue());
                _calendarDays.setDescription(txtName.getValue().toUpperCase());
                
            } else {//New country
                calendarDays = new CalendarDays();
                calendarDays.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
                calendarDays.setHolidayDate(dtbHolidayDate.getValue());
                calendarDays.setDescription(txtName.getValue().toUpperCase());
                calendarDays.setCreateDate(dtbHolidayDate.getValue());
            }

            try {
                calendarDays = utilsEJB.saveCalendarDays(calendarDays);
                calendarDaysParam = calendarDays;
                eventType = WebConstants.EVENT_EDIT;
                this.showMessage("sp.common.save.success", false, null);
            } catch (Exception e) {
                e.printStackTrace();
                this.showMessage("sp.common.calendarDays.save.fail", true, null);
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
                    saveCalendarDays(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCalendarDays(calendarDaysParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(calendarDaysParam);
                loadCmbCountry(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(calendarDaysParam);
                blockFields();
                loadCmbCountry(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(calendarDaysParam != null ? calendarDaysParam.getCountryId().getId() : 0));
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
