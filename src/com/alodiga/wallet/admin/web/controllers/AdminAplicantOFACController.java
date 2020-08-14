package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.Country;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.StatusApplicant;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;

public class AdminAplicantOFACController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblName;
    private Label lblDocumentType;
    private Label lblIdentificationNumber;
    private Label lblPercentageMatch;
    private Combobox cmbStatusApplicant;
    private PersonEJB personEJB = null;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private Person personParam;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            personParam = null;
        } else {
            personParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Person) Sessions.getCurrent().getAttribute("object") : null;
        }

        initialize();
        initView(eventType, "crud.aplicantOFAC");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.ofac.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.ofac.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("sp.crud.ofac.add"));
                break;
            default:
                break;
        }
        try {
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
    }

    private void loadFields(Person person) {

        Float percentageMatchApplicant = 0.00F;
        try {
//            AdminBusinnessAffiliationRequestsNaturalController adminRequestN = new AdminBusinnessAffiliationRequestsNaturalController();
//            if (adminRequestN.getBusinessAffiliationRequets() != null) {
//                afilationRequest = adminRequestN.getBusinessAffiliationRequets();
//            }
            person.getBusinessAffiliationRequest().getNumberRequest();
            if (person.getPersonTypeId().getIndNaturalPerson() == true) {
                lblName.setValue(person.getNaturalPerson().getFirstName() + " " + person.getNaturalPerson().getLastName());
                lblDocumentType.setValue(person.getNaturalPerson().getDocumentsPersonTypeId().getDescription());
                lblIdentificationNumber.setValue(person.getNaturalPerson().getIdentificationNumber());
            } else {
                if (person.getPersonClassificationId().getId() == 2) {
                    lblName.setValue(person.getLegalPerson().getBusinessName());
                    lblDocumentType.setValue(person.getLegalPerson().getDocumentsPersonTypeId().getDescription());
                    lblIdentificationNumber.setValue(person.getLegalPerson().getIdentificationNumber());
                } else if (person.getPersonClassificationId().getId() == 3) {
                    lblName.setValue(person.getLegalRepresentative().getFirstNames() + " " + person.getLegalRepresentative().getLastNames());
                    lblDocumentType.setValue(person.getLegalRepresentative().getDocumentsPersonTypeId().getDescription());
                    lblIdentificationNumber.setValue(person.getLegalRepresentative().getIdentificationNumber());
                }
            }

            lblPercentageMatch.setValue(percentageMatchApplicant.toString());

            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
     public void blockFields() {
        cmbStatusApplicant.setDisabled(true);
        
        btnSave.setVisible(false);
    }
     
    private void saveAplicantOfac(Person _person) {
        try {
            Person person = null;

            if (_person != null) {
                person = _person;
            } else {//New country
                person = new Person();
            }

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

    public void onClick$btnCancel() {
        clearFields();
    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveAplicantOfac(personParam);
                break;
            case WebConstants.EVENT_EDIT:
                saveAplicantOfac(personParam);
                break;
            default:
                break;
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(personParam);
                loadCmbStatusApplicant(eventType);
                blockFields();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(personParam);
                loadCmbStatusApplicant(eventType);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbStatusApplicant(eventType);
                blockFields();
                break;
            default:
                break;
        }
    }

    private void loadCmbStatusApplicant(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<StatusApplicant> statusApplicants;
        try {
            statusApplicants = personEJB.getStatusApplicant(request1);
            if (personParam.getPersonTypeId().getIndNaturalPerson() == true) {
                loadGenericCombobox(statusApplicants, cmbStatusApplicant, "description", evenInteger, Long.valueOf(personParam != null ? personParam.getNaturalPerson().getStatusApplicantId().getId() : 0));
            } else if (personParam.getPersonClassificationId().getId() == 2) {
                loadGenericCombobox(statusApplicants, cmbStatusApplicant, "description", evenInteger, Long.valueOf(personParam != null ? personParam.getLegalPerson().getStatusApplicantId().getId() : 0));
            } else if (personParam.getPersonClassificationId().getId() == 3) {
                loadGenericCombobox(statusApplicants, cmbStatusApplicant, "description", evenInteger, Long.valueOf(personParam != null ? personParam.getLegalRepresentative().getStatusApplicantId().getId() : 0));
            }

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
