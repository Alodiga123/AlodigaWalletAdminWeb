package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import com.alodiga.wallet.admin.web.components.ListcellEditButton;
import com.alodiga.wallet.admin.web.components.ListcellViewButton;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.LegalPerson;
import com.alodiga.wallet.common.model.LegalRepresentative;
import com.alodiga.wallet.common.model.NaturalPerson;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.ReviewOfac;
import com.alodiga.wallet.common.model.StatusApplicant;
import com.alodiga.wallet.common.model.StatusRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import com.alodiga.ws.cumpliments.services.OFACMethodWSProxy;
import com.alodiga.ws.cumpliments.services.WsExcludeListResponse;
import com.alodiga.ws.cumpliments.services.WsLoginResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListAplicantOFACController extends GenericAbstractListController<Person> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private UserEJB userEJB = null;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private List<Person> personList = null;
    private LegalPerson legalPersonParam;
    private List<LegalPerson> legalPerson;
    private User currentUser;
    private Profile currentProfile;
    private AdminBusinnessAffiliationRequestsLegalController legalRequest;
    private AdminBusinnessAffiliationRequestsNaturalController naturalRequest;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void checkPermissions() {
        try {
//            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_APLICANT_OFAC));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_APLICANT_OFAC);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_APLICANT_OFAC);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void startListener() {

    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "adminAplicantOFAC.zul";
            getData();
            loadList(personList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<Person> list) {
        String applicantNameLegal = "";
        String tipo = "";
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                btnDownload.setVisible(true);

                for (Person aplicant : list) {
                    item = new Listitem();
                    item.setValue(aplicant);
                    if (aplicant.getPersonTypeId().getIndNaturalPerson() == true) {
                        if ((aplicant.getPersonClassificationId().getId() == 1) || (aplicant.getPersonClassificationId().getId() == 2)) {
                            StringBuilder applicantNameNatural = new StringBuilder(aplicant.getNaturalPerson().getFirstName());
                            applicantNameNatural.append(" ");
                            applicantNameNatural.append(aplicant.getNaturalPerson().getLastName());
                            item.appendChild(new Listcell(applicantNameNatural.toString()));
                            item.appendChild(new Listcell(aplicant.getNaturalPerson().getDocumentsPersonTypeId().getCodeIdentification()));
                            item.appendChild(new Listcell(aplicant.getNaturalPerson().getIdentificationNumber()));
                            item.appendChild(new Listcell(Labels.getLabel("sp.tab.businessAffiliationRequests.naturalPerson")));
                            item.appendChild(new Listcell(Labels.getLabel("sp.common.yes")));
                            item.appendChild(new Listcell(aplicant.getNaturalPerson().getStatusApplicantId().getDescription()));
                            item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                            item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());
                        }
                    } else {
                        if (aplicant.getPersonClassificationId().getId() != 3) {
                            applicantNameLegal = aplicant.getLegalPerson().getBusinessName();
                            item.appendChild(new Listcell(applicantNameLegal));
                            item.appendChild(new Listcell(aplicant.getLegalPerson().getDocumentsPersonTypeId().getCodeIdentification()));
                            item.appendChild(new Listcell(aplicant.getLegalPerson().getIdentificationNumber()));
                            item.appendChild(new Listcell(Labels.getLabel("sp.tab.businessAffiliationRequests.legalPerson")));
                            item.appendChild(new Listcell(Labels.getLabel("sp.common.yes")));
                            item.appendChild(new Listcell(aplicant.getLegalPerson().getStatusApplicantId().getDescription()));
                            item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                            item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());
                        } else {
                            StringBuilder applicantLegalR = new StringBuilder(aplicant.getLegalRepresentative().getFirstNames());
                            applicantLegalR.append(" ");
                            applicantLegalR.append(aplicant.getLegalRepresentative().getLastNames());
                            item.appendChild(new Listcell(applicantLegalR.toString()));
                            item.appendChild(new Listcell(aplicant.getLegalRepresentative().getDocumentsPersonTypeId().getCodeIdentification()));
                            item.appendChild(new Listcell(aplicant.getLegalRepresentative().getIdentificationNumber()));
                            item.appendChild(new Listcell(Labels.getLabel("sp.tab.businessAffiliationRequests.legalPerson")));
                            item.appendChild(new Listcell(Labels.getLabel("sp.common.no")));
                            item.appendChild(new Listcell(aplicant.getLegalRepresentative().getStatusApplicantId().getDescription()));
                            item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, aplicant, Permission.EDIT_APLICANT_OFAC) : new Listcell());
                            item.appendChild(permissionRead ? new ListcellViewButton(adminPage, aplicant, Permission.VIEW_APLICANT_OFAC) : new Listcell());
                        }
                    }
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        personList = new ArrayList<Person>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            personList = personEJB.getPerson(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    private void showEmptyList() {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.businessAffiliationRequests.ofac.list"));
            AccessControl.saveAction(Permission.LIST_APLICANT_OFAC, "Se descargo listado OFAC en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {

    }

    public void onClick$btnSearch() throws InterruptedException {
        try {
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnReviewOFAC() {
        int indBlackList = 0;
        String lastName = "";
        String firstName = "";
        float ofacPercentege = 0.5F;
        NaturalPerson naturalPerson = new NaturalPerson();
        LegalPerson legalPerson = new LegalPerson();
        LegalRepresentative legalRepresentative = new LegalRepresentative();
        AffiliationRequest affiliatinRequest = new AffiliationRequest();
        OFACMethodWSProxy ofac = new OFACMethodWSProxy();
        try {
            WsLoginResponse loginResponse = new WsLoginResponse();
            loginResponse = ofac.loginWS("alodiga", "d6f80e647631bb4522392aff53370502");
            WsExcludeListResponse ofacResponse = new WsExcludeListResponse();
            for (Person applicant : personList) {
                if (applicant.getPersonTypeId().getIndNaturalPerson() == true) {
                    affiliatinRequest = applicant.getBusinessAffiliationRequest();
                    naturalPerson = applicant.getNaturalPerson();
                    lastName = applicant.getNaturalPerson().getLastName();
                    firstName = applicant.getNaturalPerson().getFirstName();
                } else if (applicant.getPersonTypeId().getIndNaturalPerson() == false) {
                    if (applicant.getPersonClassificationId().getId() == 3) {
                        if (getLegalPersonParam(applicant.getLegalRepresentative()) != null) {
                            legalPerson = legalPersonParam;
                        }
                        affiliatinRequest = legalPerson.getPersonId().getBusinessAffiliationRequest();
                        legalRepresentative = applicant.getLegalRepresentative();
                        lastName = applicant.getLegalRepresentative().getLastNames();
                        firstName = applicant.getLegalRepresentative().getFirstNames();
                    } else {
                        lastName = null;
                        firstName = null;
                    }

                }
                if (lastName != null && firstName != null) {
                    ofacResponse = ofac.queryOFACList(loginResponse.getToken(), lastName, firstName, null, null, null, null, ofacPercentege);

                    //Se guarda el registro de la revision OFAC
                    saveReviewOfac(applicant, ofacResponse, affiliatinRequest);

                    //Actualizar el estatus del solicitante si tiene coincidencia con lista OFAC
                    if (applicant.getPersonTypeId().getIndNaturalPerson() == true) {
                        if (Double.parseDouble(ofacResponse.getPercentMatch()) <= 0.75) {
                            naturalPerson.setStatusApplicantId(getStatusApplicant(applicant.getNaturalPerson().getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST));
                            indBlackList = 1;
                        } else {
                            naturalPerson.setStatusApplicantId(getStatusApplicant(applicant.getNaturalPerson().getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                        }
                        naturalPerson = personEJB.saveNaturalPerson(naturalPerson);
                    } else {
                        if (Double.parseDouble(ofacResponse.getPercentMatch()) <= 0.75) {
                            legalPerson.setStatusApplicantId(getStatusApplicant(legalPerson.getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST));
                            legalRepresentative.setStatusApplicantId(getStatusApplicant(applicant.getLegalRepresentative().getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST));
                            indBlackList = 1;
                        } else {
                            legalPerson.setStatusApplicantId(getStatusApplicant(legalPerson.getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                            legalRepresentative.setStatusApplicantId(getStatusApplicant(applicant.getLegalRepresentative().getStatusApplicantId(), Constants.STATUS_APPLICANT_BLACK_LIST_OK));
                        }
                        legalPerson = personEJB.saveLegalPerson(legalPerson);
                        legalRepresentative = personEJB.saveLegalRepresentative(legalRepresentative);
                    }
                }
                //Si algunos solicitante(s) coincide(n) con la Lista OFAC se actualiza estatus de la solicitud
                if (ofacResponse != null) {
                    if (indBlackList == 1) {
                        affiliatinRequest.setStatusRequestId(getStatusAffiliationRequest(affiliatinRequest.getStatusRequestId(), Constants.STATUS_REQUEST_PENDING));
                    } else {
                        affiliatinRequest.setStatusRequestId(getStatusAffiliationRequest(affiliatinRequest.getStatusRequestId(), Constants.STATUS_REQUEST_BLACK_LIST_OK));
                    }
                    affiliatinRequest = utilsEJB.saveAffiliationRequest(affiliatinRequest);

                    getData();
                    loadList(personList);
                    this.showMessage("sp.common.finishReviewOFAC", false, null);
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public LegalPerson getLegalPersonParam(LegalRepresentative legalRepresentative) {
        legalPersonParam = null;
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_LEGAL_REPRESENTATIVE_ID, legalRepresentative.getId());
            request1.setParams(params);
            legalPerson = personEJB.getLegalPersonByLegalRepresentative(request1);
            for (LegalPerson r : legalPerson) {
                legalPersonParam = r;
            }
        } catch (Exception ex) {
        }
        return legalPersonParam;
    }

    public void saveReviewOfac(Person applicant, WsExcludeListResponse ofacResponse, AffiliationRequest affiliationRequest) {
        ReviewOfac reviewOFAC = new ReviewOfac();
        try {
            //Guardar el resultado de revisión en lista OFAC para cada solicitante
            reviewOFAC.setPersonId(applicant);
            reviewOFAC.setResultReview(Float.parseFloat(ofacResponse.getPercentMatch()));
            reviewOFAC.setAffiliationRequestId(affiliationRequest);
            reviewOFAC.setUserReviewId(currentUser);
            reviewOFAC.setCreateDate(new Timestamp(new Date().getTime()));

            reviewOFAC = utilsEJB.saveReviewOfac(reviewOFAC);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public StatusApplicant getStatusApplicant(StatusApplicant statusAplicant, int statusRequestId) {
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(statusRequestId);
            statusAplicant = personEJB.loadStatusApplicant(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusAplicant;
    }

    public StatusRequest getStatusAffiliationRequest(StatusRequest statusRequest, int statusRequestId) {
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(statusRequestId);
            statusRequest = utilsEJB.loadStatusRequest(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusRequest;
    }

    @Override
    public List<Person> getFilteredList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
