package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.ProductEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.BusinessAffiliationRequest;
import com.alodiga.wallet.common.model.CollectionsRequest;
import com.alodiga.wallet.common.model.RequestHasCollectionRequest;
import com.alodiga.wallet.common.model.ReviewBusinessAffiliationRequest;
import com.alodiga.wallet.common.model.ReviewType;
import com.alodiga.wallet.common.model.StatusBusinessAffiliationRequest;
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
    private ReviewBusinessAffiliationRequest reviewBusinessRequestParam;
    private List<ReviewBusinessAffiliationRequest> reviewCollectionsRequest;
    private BusinessAffiliationRequest businessAffiliationRequestParam;
    private List<RequestHasCollectionRequest> requestHasCollectionsRequestList;
    private List<CollectionsRequest> collectionsByRequestList;
    private ProductEJB productEJB = null;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private Button btnSave;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if (eventType == WebConstants.EVENT_ADD) {
            businessAffiliationRequestParam = null;
        } else {
            businessAffiliationRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (BusinessAffiliationRequest) Sessions.getCurrent().getAttribute("object") : null;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            user = AccessControl.loadCurrentUser();
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            dtbReviewDate.setValue(new Timestamp(new java.util.Date().getTime()));
            this.clearMessage();
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public ReviewBusinessAffiliationRequest getReviewBusinessRequestParam() {
        reviewBusinessRequestParam = null;
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_BUSINESS_AFFILIATION_REQUEST_ID, businessAffiliationRequestParam.getId());
            params.put(QueryConstants.PARAM_REVIEW_TYPE_ID, Constants.REVIEW_REQUEST_TYPE);
            request1.setParams(params);
            reviewCollectionsRequest = utilsEJB.getReviewBusinessRequestByRequest(request1);
            for (ReviewBusinessAffiliationRequest r : reviewCollectionsRequest) {
                reviewBusinessRequestParam = r;
            }
        } catch (Exception ex) {
        }
        return reviewBusinessRequestParam;
    }

    public void clearFields() {
        dtbReviewDate.setRawValue(null);
        txtObservations.setRawValue(null);
    }

    private void loadField(BusinessAffiliationRequest affiliationRequest) {
        try {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            if (affiliationRequest.getNumberRequest() != null) {
                lblRequestNumber.setValue(affiliationRequest.getNumberRequest());
                lblRequestDate.setValue(simpleDateFormat.format(affiliationRequest.getDateRequest()));
                lblStatusRequest.setValue(affiliationRequest.getStatusBusinessAffiliationRequestId().getDescription());
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

    private void loadFields(ReviewBusinessAffiliationRequest reviewBusinessAffiliation) throws EmptyListException, GeneralException, NullParameterException {
        try {
            if (reviewBusinessAffiliation != null) {
                NumberFormat n = NumberFormat.getCurrencyInstance();
                lblAssessorName.setValue(reviewBusinessAffiliation.getUserReviewId().getFirstName() + " " + reviewBusinessAffiliation.getUserReviewId().getLastName());
                if (reviewBusinessAffiliation.getReviewDate() != null) {
                    dtbReviewDate.setValue(reviewBusinessAffiliation.getReviewDate());
                }
                if (reviewBusinessAffiliation.getObservations() != null) {
                    txtObservations.setText(reviewBusinessAffiliation.getObservations());
                }
                if (reviewBusinessAffiliation.getIndApproved() != null) {
                    if (reviewBusinessAffiliation.getIndApproved() == true) {
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
        if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("sp.error.observations", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveReviewCollectionsRequest(ReviewBusinessAffiliationRequest _reviewCollectionsRequest) {
        try {
            ReviewBusinessAffiliationRequest reviewCollectionsRequest = null;
            boolean indApproved;
            int indReviewCollectionApproved = 0;
            int indReviewCollectionIncomplete = 0;

            if (_reviewCollectionsRequest != null) {
                reviewCollectionsRequest = _reviewCollectionsRequest;
            } else {
                reviewCollectionsRequest = new ReviewBusinessAffiliationRequest();
            }

            if (rApprovedYes.isChecked()) {
                indApproved = true;
            } else {
                indApproved = false;
            }

            //Obtiene el tipo de revision Recaudos
            EJBRequest request = new EJBRequest();
            request.setParam(Constants.REVIEW_REQUEST_TYPE);
            ReviewType reviewType = utilsEJB.loadReviewType(request);

            if (rApprovedYes.isChecked()) {
                //Recaudos que han sido revisados por Agente Comercial
                Map params = new HashMap();
                EJBRequest request1 = new EJBRequest();
                params.put(Constants.BUSINESS_AFFILIATION_REQUEST_KEY, businessAffiliationRequestParam.getNumberRequest());
                request1.setParams(params);
//                requestHasCollectionsRequestList = utilsEJB.getRequestsHasCollectionsRequestByBusinessAffiliationRequest(request1);
                //Recaudos asociados a la Solicitud
//                params = new HashMap();
//                params.put(Constants.COUNTRY_KEY, adminRequest.getRequest().getCountryId().getId());
//                params.put(Constants.PROGRAM_KEY, adminRequest.getRequest().getProgramId().getId());
//                params.put(Constants.PRODUCT_TYPE_KEY, adminRequest.getRequest().getProductTypeId().getId());
//                params.put(Constants.PERSON_TYPE_KEY, adminRequest.getRequest().getPersonTypeId().getId());
//                request1.setParams(params);
//                collectionsByRequestList = requestEJB.getCollectionsByRequest(request1);
//                //Se chequea si hay recaudos sin revisar
//                if (collectionsByRequestList.size() > requestHasCollectionsRequestList.size()) {
//                    indReviewCollectionIncomplete = 1;
//                }
//                for (RequestHasCollectionsRequest r : requestHasCollectionsRequestList) {
//                    if (r.getIndApproved() == 0) {
//                        indReviewCollectionApproved = 1;
//                    }
//                    if (r.getUrlImageFile() == null) {
//                        indReviewCollectionIncomplete = 1;
//                    }
//                }
            }

            //Guarda la revision
            reviewCollectionsRequest.setBusinessAffiliationRequestId(businessAffiliationRequestParam);
            reviewCollectionsRequest.setUserReviewId(user);
            reviewCollectionsRequest.setReviewDate(dtbReviewDate.getValue());
            reviewCollectionsRequest.setObservations(txtObservations.getText());
            reviewCollectionsRequest.setIndApproved(indApproved);
            reviewCollectionsRequest.setReviewTypeId(reviewType);
            reviewCollectionsRequest.setCreateDate(new Timestamp(new Date().getTime()));
            reviewCollectionsRequest = utilsEJB.saveReviewBusinessAffiliationRequest(reviewCollectionsRequest);

            //Actualiza el agente comercial en la solictud de tarjeta
//            requestCard.setUserId(user);
//            requestCard = requestEJB.saveRequest(requestCard);
            //Si los recaudos están incompletos, se rechaza la solicitud
//            if (indReviewCollectionIncomplete == 1) {
////                updateRequestByCollectionsIncomplete(businessAffiliationRequestParam);
//            } else {
//                //Verificar que todos los recaudos estén aprobados y que la solicitud este aprobada por el agente comercial
//                if (indReviewCollectionApproved == 0 && reviewCollectionsRequest.getIndApproved() == true) {
//                    //Se aprueba la solicitud
//                    businessAffiliationRequestParam.setStatusBusinessAffiliationRequestId(getStatusBusinessAffiliationRequest(businessAffiliationRequestParam, Constants.STATUS_REQUEST_APPROVED));
//                    businessAffiliationRequestParam = utilsEJB.saveBusinessAffiliationRequest(businessAffiliationRequestParam);
//                    //Verificar si el solicitante es jurídico o natural
////                    if (requestCard.getIndPersonNaturalRequest() == true) {
////                        //Se crea el cliente natural
////                        saveNaturalCustomer(requestCard);
////                    } else {
////                        //Se crea el cliente jurídico
////                        saveLegalCustomer(requestCard);
////                    }
//                    this.showMessage("cms.common.save.success.customer", false, null);
////                } else {
////                    UpdateRequestWithoutApproving(reviewCollectionsRequest);
//                }
//            }
            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public StatusBusinessAffiliationRequest getStatusBusinessAffiliationRequest(BusinessAffiliationRequest businessAffiliation, int statusRequestId) {
        StatusBusinessAffiliationRequest statusRequest = businessAffiliation.getStatusBusinessAffiliationRequestId();
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(statusRequestId);
            statusRequest = utilsEJB.loadStatusBusinessAffiliationRequest(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusRequest;
    }

//    public void UpdateRequestWithoutApproving(ReviewRequest reviewCollectionsRequest) {
//        boolean indApproved;
//        try {
//            updateRequestByCollectionsWithoutApproval(requestCard);
//            rApprovedYes.setChecked(false);
//            rApprovedNo.setChecked(true);
//            indApproved = false;
//            reviewCollectionsRequest.setIndApproved(indApproved);
//            reviewCollectionsRequest = requestEJB.saveReviewRequest(reviewCollectionsRequest);
//            this.showMessage("cms.common.requestNotApproved", false, null);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//
//    }
//    public void updateRequestByCollectionsWithoutApproval(Request requestCard) {
//        try {
//            EJBRequest request = new EJBRequest();
//            request.setParam(Constants.STATUS_REQUEST_COLLECTIONS_WITHOUT_APPROVAL);
//            StatusRequest statusRequestRejected = requestEJB.loadStatusRequest(request);
//            requestCard.setStatusRequestId(statusRequestRejected);
//            requestCard = requestEJB.saveRequest(requestCard);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void updateRequestByCollectionsIncomplete(Request requestCard) {
//        try {
//            EJBRequest request = new EJBRequest();
//            request.setParam(Constants.STATUS_REQUEST_REJECTED);
//            StatusRequest statusRequestRejected = requestEJB.loadStatusRequest(request);
//
//            requestCard.setStatusRequestId(statusRequestRejected);
//            request.setParam(Constants.REASON_REQUEST_REJECTED_BY_COLLECTIONS);
//            ReasonRejectionRequest reasonRejectionRequest = requestEJB.loadReasonRejectionRequest(request);
//
//            requestCard.setReasonRejectionRequestId(reasonRejectionRequest);
//            requestCard = requestEJB.saveRequest(requestCard);
//            this.showMessage("cms.common.requestRejected", false, null);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    
//
//    
//
//    public StatusCustomer getStatusActiveCustomer() {
//        StatusCustomer statusCustomer = null;
//        try {
//            EJBRequest request = new EJBRequest();
//            request.setParam(Constants.STATUS_CUSTOMER_ACTIVE);
//            statusCustomer = personEJB.loadStatusCustomer(request);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//        return statusCustomer;
//    }
    
    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveReviewCollectionsRequest(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveReviewCollectionsRequest(reviewBusinessRequestParam);
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
                    loadField(businessAffiliationRequestParam);
                    loadUser();
                    if (getReviewBusinessRequestParam() != null) {
                        loadFields(reviewBusinessRequestParam);
                    }else{
                        loadUser();
                        loadDate();
                        dtbReviewDate.setDisabled(true);
                    }
                    break;
                case WebConstants.EVENT_VIEW:
                    loadField(businessAffiliationRequestParam);
                    loadUser();
                    if (getReviewBusinessRequestParam() != null) {
                        loadFields(reviewBusinessRequestParam);
                    } else {
                        loadUser();
                        loadDate();
                        dtbReviewDate.setDisabled(true);
                    }
                    blockFields();
                    break;
                case WebConstants.EVENT_ADD:
                    loadField(businessAffiliationRequestParam);
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
