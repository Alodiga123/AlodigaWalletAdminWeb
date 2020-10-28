package com.alodiga.wallet.admin.web.controllers;

//import com.alodiga.ws.cumpliments.services.OFACMethodWSProxy;
//import com.alodiga.ws.cumpliments.services.WsExcludeListResponse;
//import com.alodiga.ws.cumpliments.services.WsLoginResponse;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.Utils;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.NaturalPerson;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.Person;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.StatusRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ListApplicantOFACController extends GenericAbstractListController<NaturalPerson> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private User currentUser;
    private Profile currentProfile;
    private List<NaturalPerson> applicantList = null;
    private AffiliationRequest affiliationRequetsParam;
    private Button btnSave;
    private Integer eventType;
    private Tab tabApplicantOFAC;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            affiliationRequetsParam = null;
        } else {
            affiliationRequetsParam = (Sessions.getCurrent().getAttribute("object") != null) ? (AffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
        }
        initialize();
        startListener();
    }

    @Override
    public void checkPermissions() {
        try {
//            btnAdd.setVisible(PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.ADD_ADDRESS));
            permissionEdit = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.EDIT_ADDRESS);
            permissionRead = PermissionManager.getInstance().hasPermisssion(currentProfile.getId(), Permission.VIEW_ADDRESS);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateApplicantOFAC", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {
            public void onEvent(Event evt) {
                getData();
                loadList(applicantList);
            }
        });
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            currentUser = AccessControl.loadCurrentUser();
            currentProfile = currentUser.getCurrentProfile();
            checkPermissions();
            adminPage = "/adminApplicantOFAC.zul";
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            getData();
            loadList(applicantList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onSelect$tabApplicantOFAC() {
        try {
            doAfterCompose(self);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        applicantList = new ArrayList<NaturalPerson>();
        NaturalPerson naturalPerson = null;
        AffiliationRequest affiliationRequest = null;
        Person person = null;
        try {
            //Solicitud de Tarjeta
            person = affiliationRequetsParam.getBusinessPersonId();
            
//            AdminBusinnessAffiliationRequestsNaturalController adminRequestController = new AdminBusinnessAffiliationRequestsNaturalController();
//            if (adminRequestController.getBusinessAffiliationRequets().getId() != null) {
//                affiliationRequest = adminRequestController.getBusinessAffiliationRequets();
//            }
            if (person.getNaturalPerson() != null) {
                //Solicitante Principal de Tarjeta
                naturalPerson = person.getNaturalPerson();
            }
            
            EJBRequest request1 = new EJBRequest();
            request1.setParam(naturalPerson.getId());
            applicantList = personEJB.getNaturalPerson(request1);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } finally {
            applicantList.add(naturalPerson);
        }
    }

    
    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.businessAffiliationRequests.ofac.list"));
            AccessControl.saveAction(Permission.LIST_BUSINESS_AFFILIATION_REQUESTS, "Se descargo la lista OFAC en formato excel");
        } catch (Exception ex) {
            showError(ex);
        }
    }

//    public void onClick$btnReviewOFAC() {
//        int indBlackList = 0;
//        String lastName = "";
//        String firstName = "";
//        float ofacPercentege = 0.5F;
//        Request request = adminRequest.getRequest();
////        OFACMethodWSProxy ofac = new OFACMethodWSProxy();
//        try {
////            WsLoginResponse loginResponse = new WsLoginResponse();
////            loginResponse = ofac.loginWS("alodiga", "d6f80e647631bb4522392aff53370502");
////            WsExcludeListResponse ofacResponse = new WsExcludeListResponse();   
////            for (ApplicantNaturalPerson applicant: applicantList) {
////                lastName = applicant.getLastNames();
////                firstName = applicant.getFirstNames();
////                ofacResponse = ofac.queryOFACList(loginResponse.getToken(),lastName, firstName, null, null, null, null, ofacPercentege);
////                
////                //Guardar el resultado de revisión en lista OFAC para cada solicitante
////                ReviewOFAC reviewOFAC = new ReviewOFAC();
////                reviewOFAC.setPersonId(applicant.getPersonId());
////                reviewOFAC.setRequestId(request);
////                reviewOFAC.setResultReview(ofacResponse.getPercentMatch());
////                reviewOFAC = requestEJB.saveReviewOFAC(reviewOFAC);
////                
////                //Actualizar el estatus del solicitante si tiene coincidencia con lista OFAC
////                if (Double.parseDouble(ofacResponse.getPercentMatch()) <= 0.75) {
////                    applicant.setStatusApplicantId(getStatusApplicant(applicant, Constants.STATUS_APPLICANT_BLACK_LIST));
////                    indBlackList = 1;
////                } else {
////                  applicant.setStatusApplicantId(getStatusApplicant(applicant, Constants.STATUS_APPLICANT_BLACK_LIST_OK));  
////                }
////                applicant = personEJB.saveApplicantNaturalPerson(applicant);
////            }
//            //Si algun(os) solicitante(s) coincide(n) con la Lista OFAC se actualiza estatus de la solicitud
////            if (indBlackList == 1) {
////                request.setStatusRequestId(getStatusRequest(request,Constants.STATUS_REQUEST_PENDING_APPROVAL));
////            } else {
////                request.setStatusRequestId(getStatusRequest(request,Constants.STATUS_REQUEST_BLACK_LIST_OK));
////            }
//            request = requestEJB.saveRequest(request);
//            getData();
//            loadDataList(applicantList);
//            this.showMessage("sp.common.finishReviewOFAC", false, null);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }


    public StatusRequest getStatusAffiliationRequest(AffiliationRequest requestAfilation, int statusRequestId) {
        StatusRequest statusRequest = requestAfilation.getStatusRequestId();
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(statusRequestId);
            statusRequest = utilsEJB.loadStatusRequest(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusRequest;
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void loadList(List<NaturalPerson> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (NaturalPerson naturalPerson : list) {
                    item = new Listitem();
                    item.setValue(naturalPerson);
                    StringBuilder builder = new StringBuilder(naturalPerson.getFirstName());
                    builder.append(" ");
                    builder.append(naturalPerson.getLastName());
                    item.appendChild(new Listcell(builder.toString()));
                    item.appendChild(new Listcell(naturalPerson.getDocumentsPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(naturalPerson.getIdentificationNumber()));
                    item.appendChild(new Listcell(WebConstants.MAIN_APPLICANT));
                    item.appendChild(createButtonEditModal(naturalPerson));
                    item.appendChild(createButtonViewModal(naturalPerson));
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

    public Listcell createButtonEditModal(final Object obg) {
        Listcell listcellEditModal = new Listcell();
        try {
            Button button = new Button();
            button.setImage("/images/icon-edit.png");
            button.setTooltiptext(Labels.getLabel("sp.common.actions.edit"));
            button.setClass("open orange");
            button.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(Event arg0) throws Exception {
                    Sessions.getCurrent().setAttribute("object", obg);
                    Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_EDIT);
                    Map<String, Object> paramsPass = new HashMap<String, Object>();
                    paramsPass.put("object", obg);
                    final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
                    window.doModal();
                }
            });
            button.setParent(listcellEditModal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listcellEditModal;
    }

    public Listcell createButtonViewModal(final Object obg) {
        Listcell listcellViewModal = new Listcell();
        try {
            Button button = new Button();
            button.setImage("/images/icon-invoice.png");
            button.setTooltiptext(Labels.getLabel("sp.common.actions.view"));
            button.setClass("open orange");
            button.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(Event arg0) throws Exception {
                    Sessions.getCurrent().setAttribute("object", obg);
                    Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_VIEW);
                    Map<String, Object> paramsPass = new HashMap<String, Object>();
                    paramsPass.put("object", obg);
                    final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
                    window.doModal();
                }

            });
            button.setParent(listcellViewModal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listcellViewModal;
    }

//    public List<NaturalPerson> getFilterList(String filter) {
//        List<NaturalPerson> naturalPersonList_ = new ArrayList<NaturalPerson>();
//        NaturalPerson naturalPerson = null;
//
//        try {
//            if (filter != null && !filter.equals("")) {
//                //Solicitante de Tarjeta
//                NaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
//                if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
//                    applicantNaturalPerson = adminNaturalPerson.getApplicantNaturalPerson();
//                }
//                EJBRequest request1 = new EJBRequest();
//                Map params = new HashMap();
//                params.put(Constants.APPLICANT_NATURAL_PERSON_KEY, applicantNaturalPerson.getId());
//                params.put(Constants.PARAM_APPLICANT_NATURAL_PERSON_NAME_KEY, filter);
//
//                request1.setParams(params);
//                //applicantList = personEJB.getCardComplementaryByApplicant(request1); 
//                naturalPersonList_ = personEJB.searchCardComplementaryByApplicantOFAC(request1);
//
//            } else {
//                return applicantList;
//            }
//        } catch (Exception ex) {
//            showError(ex);
//        }
//        return naturalPersonList_;
//    }

    public void onClick$btnSearch() throws InterruptedException {
//        try {
//            loadDataList(getFilterList(txtName.getText()));
//        } catch (Exception ex) {
//            showError(ex);
//        }
    }

    @Override
    public List<NaturalPerson> getFilteredList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onClick$btnAdd() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
