package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.enumeraciones.StatusRequestE;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.AffiliationRequest;
import com.alodiga.wallet.common.model.CollectionsRequest;
import com.alodiga.wallet.common.model.RequestHasCollectionRequest;
import com.alodiga.wallet.common.model.ReviewAffiliationRequest;
import com.alodiga.wallet.common.model.ReviewType;
import com.alodiga.wallet.common.model.StatusRequest;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.QueryConstants;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class AdminApplicationReviewController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private Label lblAssessorName;
    private Datebox dtbReviewDate;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private Textbox txtObservations;
    private User user = null;
    private ReviewAffiliationRequest reviewAffiliationRequestParam;
    private AffiliationRequest affiliationRequestParam;
    private List<ReviewAffiliationRequest> reviewCollectionsRequest;
    private List<RequestHasCollectionRequest> requestHasCollectionsRequestList;
    private List<CollectionsRequest> collectionsByRequestList;
    private UtilsEJB utilsEJB = null;
    private Button btnSave;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if (eventType == WebConstants.EVENT_ADD) {
            affiliationRequestParam = null;
        } else {
            affiliationRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (AffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            user = AccessControl.loadCurrentUser();
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            dtbReviewDate.setValue(new Timestamp(new java.util.Date().getTime()));
            this.clearMessage();
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public ReviewAffiliationRequest getReviewRequestParam() {
        reviewAffiliationRequestParam = null;
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_AFFILIATION_REQUEST_ID, affiliationRequestParam.getId());
            params.put(QueryConstants.PARAM_REVIEW_TYPE_ID, Constants.REVIEW_REQUEST_TYPE);
            request1.setParams(params);
            reviewCollectionsRequest = utilsEJB.getReviewRequestByRequest(request1);
            for (ReviewAffiliationRequest r : reviewCollectionsRequest) {
                reviewAffiliationRequestParam = r;
            }
        } catch (Exception ex) {
        }
        return reviewAffiliationRequestParam;
    }

    public void clearFields() {
        dtbReviewDate.setRawValue(null);
        txtObservations.setRawValue(null);
    }

    private void loadField(AffiliationRequest affiliationRequest) {
        try {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            if (affiliationRequest.getNumberRequest() != null) {
                lblRequestNumber.setValue(affiliationRequest.getNumberRequest());
                lblRequestDate.setValue(simpleDateFormat.format(affiliationRequest.getDateRequest()));
                lblStatusRequest.setValue(affiliationRequest.getStatusRequestId().getDescription());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadUser() {
        lblAssessorName.setValue(user.getFirstName() + " " + user.getLastName());
    }

    private void loadDate() {
        Date today = new Date();
        dtbReviewDate.setValue(today);
    }

    private void loadFields(ReviewAffiliationRequest reviewAffiliation) throws EmptyListException, GeneralException, NullParameterException {
        try {
            if (reviewAffiliation != null) {
                NumberFormat n = NumberFormat.getCurrencyInstance();
                lblAssessorName.setValue(reviewAffiliation.getUserReviewId().getFirstName() + " " + reviewAffiliation.getUserReviewId().getLastName());
                if (reviewAffiliation.getReviewDate() != null) {
                    dtbReviewDate.setValue(reviewAffiliation.getReviewDate());
                }
                if (reviewAffiliation.getObservations() != null) {
                    txtObservations.setText(reviewAffiliation.getObservations());
                }
                if (reviewAffiliation.getIndApproved() != null) {
                    if (reviewAffiliation.getIndApproved() == true) {
                        rApprovedYes.setChecked(true);
                    } else {
                        rApprovedNo.setChecked(true);
                    }
                }
            } else {
                lblAssessorName = null;
            }
        } catch (Exception ex) {
            showError(ex);
        } finally {
            lblAssessorName.setValue(user.getFirstName() + " " + user.getLastName());
        }
    }

    public void blockFields() {
        txtObservations.setReadonly(true);
        rApprovedYes.setDisabled(true);
        rApprovedNo.setDisabled(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if ((!rApprovedYes.isChecked()) && (!rApprovedNo.isChecked())) {
            this.showMessage("msj.error.indApprove", true, null);
        }else if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("msj.error.observations", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveReviewCollectionsRequest(ReviewAffiliationRequest _reviewAffiliationRequest) {
        try {
            ReviewAffiliationRequest reviewAffiliationRequest = null;
            boolean indApproved;
            int indReviewCollectionApproved = 0;
            int indReviewCollectionIncomplete = 0;

            if (_reviewAffiliationRequest != null) {
                reviewAffiliationRequest = _reviewAffiliationRequest;
            } else {
                reviewAffiliationRequest = new ReviewAffiliationRequest();
            }

            if (rApprovedYes.isChecked()) {
                indApproved = true;
            } else {
                indApproved = false;
            }

            //Obtiene el tipo de revision Solicitud de Afiliación de Usuarios
            EJBRequest request = new EJBRequest();
            request.setParam(Constants.REVIEW_REQUEST_TYPE);
            ReviewType reviewType = utilsEJB.loadReviewType(request);

            if (rApprovedYes.isChecked()) {
                //Recaudos que han sido revisados por Agente Comercial
                Map params = new HashMap();
                EJBRequest request1 = new EJBRequest();
                params.put(Constants.AFFILIATION_REQUEST_KEY, affiliationRequestParam.getId());
                request1.setParams(params);
                requestHasCollectionsRequestList = utilsEJB.getRequestsHasCollectionsRequestByBusinessAffiliationRequest(request1);
                //Recaudos asociados a la Solicitud
                if(affiliationRequestParam.getBusinessPersonId() != null){
                    params = new HashMap();
                    params.put(Constants.PERSON_TYPE_KEY, affiliationRequestParam.getBusinessPersonId().getPersonTypeId().getId());
                    request1.setParams(params);
                    collectionsByRequestList = utilsEJB.getCollectionsByPersonType(request1); 
                } else if(affiliationRequestParam.getUserRegisterUnifiedId() != null){
                    params = new HashMap();
                    params.put(Constants.PERSON_TYPE_KEY, affiliationRequestParam.getUserRegisterUnifiedId().getPersonTypeId().getId());
                    request1.setParams(params);
                    collectionsByRequestList = utilsEJB.getCollectionsByPersonType(request1);  
                }
                //Se chequea si hay recaudos sin revisar
                if (collectionsByRequestList.size() > requestHasCollectionsRequestList.size()) {
                    indReviewCollectionIncomplete = 1;
                }
                boolean rejected = false; 
                for (RequestHasCollectionRequest r : requestHasCollectionsRequestList) {
                    if (r.getIndApproved() == rejected) {
                        indReviewCollectionApproved = 1;
                    }
                    if (r.getImageFileUrl() == null) {
                        indReviewCollectionIncomplete = 1;
                    }
                }
            }

            //Guarda la revision
            reviewAffiliationRequest.setAffiliationRequestId(affiliationRequestParam);
            reviewAffiliationRequest.setUserReviewId(user);
            reviewAffiliationRequest.setReviewDate(dtbReviewDate.getValue());
            reviewAffiliationRequest.setObservations(txtObservations.getText());
            reviewAffiliationRequest.setIndApproved(indApproved);
            reviewAffiliationRequest.setReviewTypeId(reviewType);
            reviewAffiliationRequest.setCreateDate(new Timestamp(new Date().getTime()));
            reviewAffiliationRequest = utilsEJB.saveReviewAffiliationRequest(reviewAffiliationRequest);

            //Si los recaudos están incompletos, se rechaza la solicitud
            if (indReviewCollectionIncomplete == 1) {
                updateRequestByCollectionsIncomplete(affiliationRequestParam);
                //Se coloca la solicitud en no aprobada
                UpdateRequestWithoutApproving(reviewAffiliationRequest);
            } else {
                //Verificar que todos los recaudos estén aprobados y que la solicitud este aprobada por el agente comercial
                if (indReviewCollectionApproved == 0 && reviewAffiliationRequest.getIndApproved() == true) {
                    //Se aprueba la solicitud
                    affiliationRequestParam.setStatusRequestId(getStatusAffiliationRequest(affiliationRequestParam, StatusRequestE.APROBA.getId()));
                    affiliationRequestParam = utilsEJB.saveAffiliationRequest(affiliationRequestParam);
                } else {
                    //Se coloca la solicitud en no aprobada
                    UpdateRequestWithoutApproving(reviewAffiliationRequest);
                }
            }
            loadField(affiliationRequestParam);
            this.showMessage("wallet.msj.save.success", false, null);
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public StatusRequest getStatusAffiliationRequest(AffiliationRequest affiliation, int statusRequestId) {
        StatusRequest statusRequest = affiliation.getStatusRequestId();
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(statusRequestId);
            statusRequest = utilsEJB.loadStatusRequest(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusRequest;
    }

    public void UpdateRequestWithoutApproving(ReviewAffiliationRequest reviewAffiliationRequest) {
        boolean indApproved;
        try {
            updateRequestByCollectionsWithoutApproval(affiliationRequestParam);
            rApprovedYes.setChecked(false);
            rApprovedNo.setChecked(true);
            indApproved = false;
            reviewAffiliationRequest.setIndApproved(indApproved);
            reviewAffiliationRequest = utilsEJB.saveReviewAffiliationRequest(reviewAffiliationRequest);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void updateRequestByCollectionsWithoutApproval(AffiliationRequest affiliationRequest) {
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(Constants.STATUS_REQUEST_COLLECTIONS_WITHOUT_APPROVAL);
            StatusRequest statusRequestRejected = utilsEJB.loadStatusRequest(request);

            affiliationRequest.setStatusRequestId(statusRequestRejected);
            affiliationRequest = utilsEJB.saveAffiliationRequest(affiliationRequest);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void updateRequestByCollectionsIncomplete(AffiliationRequest affiliationRequest) {
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(Constants.STATUS_REQUEST_REJECTED);
            StatusRequest statusRequestRejected = utilsEJB.loadStatusRequest(request);

            affiliationRequest.setStatusRequestId(statusRequestRejected);
            affiliationRequest = utilsEJB.saveAffiliationRequest(affiliationRequest);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveReviewCollectionsRequest(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveReviewCollectionsRequest(reviewAffiliationRequestParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        try {
            switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    loadField(affiliationRequestParam);
                    loadUser();
                    if (getReviewRequestParam() != null) {
                        loadFields(reviewAffiliationRequestParam);
                    } else {
                        loadUser();
                        loadDate();
                        dtbReviewDate.setDisabled(true);
                    }
                    break;
                case WebConstants.EVENT_VIEW:
                    loadField(affiliationRequestParam);
                    loadUser();
                    if (getReviewRequestParam() != null) {
                        loadFields(reviewAffiliationRequestParam);
                    } else {
                        loadUser();
                        loadDate();
                        dtbReviewDate.setDisabled(true);
                    }
                    blockFields();
                    break;
                case WebConstants.EVENT_ADD:
                    loadField(affiliationRequestParam);
                    loadUser();
                    loadDate();
                    dtbReviewDate.setDisabled(true);
                    break;
            }
        } catch (EmptyListException ex) {
            Logger.getLogger(AdminApplicationReviewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(AdminApplicationReviewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminApplicationReviewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
