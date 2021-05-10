package com.alodiga.wallet.admin.web.controllers;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Textbox;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.ejb.PersonEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.PasswordChangeRequest;
import com.alodiga.wallet.common.model.Sequences;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.Encoder;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminPasswordChangeRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtCurrentPassword;
    private Textbox txtNewPassword;
    private Textbox txtRepeatNewPassword;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblIdentificationNumber;
    private Label lblUser;
    private Label lblComercialAgency;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private UserEJB userEJB = null;
    private PasswordChangeRequest passwordChangeRequestParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    private User user;
    private String numberRequest = "";
    private int attempts = 0;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        passwordChangeRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (PasswordChangeRequest) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            passwordChangeRequestParam = null;
        } else {
            passwordChangeRequestParam = (PasswordChangeRequest) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
        UserInformation();
        initView(eventType, "crud.passwordChangeRequest");
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.passwordChange.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("wallet.crud.passwordChange.add"));
                rApprovedYes.setVisible(false);
                rApprovedNo.setVisible(false);
                break;
            default:
                break;
        }
        try {
            user = AccessControl.loadCurrentUser();
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void onClick$imgEye() {
        if (txtCurrentPassword.getType().equals("password")) {
            txtCurrentPassword.setType("text");
        } else {
            txtCurrentPassword.setType("password");
        }              
    }
    
    public void onClick$imgEye1() {
        if (txtNewPassword.getType().equals("password")) {
            txtNewPassword.setType("text");
        } else {
            txtNewPassword.setType("password");
        }              
    }
    
    public void onClick$imgEye2() {
        if (txtRepeatNewPassword.getType().equals("password")) {
            txtRepeatNewPassword.setType("text");
        } else {
            txtRepeatNewPassword.setType("password");
        }              
    }
    
    public void UserInformation(){
        lblUser.setValue(user.getFirstName() + " " + user.getLastName());
        lblIdentificationNumber.setValue(user.getIdentificationNumber());
        lblComercialAgency.setValue(user.getEmployeeId().getComercialAgencyId().getName());
    }

    public void onFocus$txtCurrentPassword() {
        txtCurrentPassword.setText("");
        txtNewPassword.setText("");
        txtRepeatNewPassword.setText("");
    }

    public void onFocus$txtNewPassword() {
        txtRepeatNewPassword.setText("");
    }

    public void clearFields() {
        txtCurrentPassword.setRawValue(null);
        txtNewPassword.setRawValue(null);
        txtRepeatNewPassword.setRawValue(null);
        lblRequestNumber.setValue(null);
        lblRequestDate.setValue(null);
        lblIdentificationNumber.setValue(null);
        lblUser.setValue(null);
        lblComercialAgency.setValue(null);
    }

    private void loadFields(PasswordChangeRequest passwordChangeRequest) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            lblRequestNumber.setValue(passwordChangeRequest.getRequestNumber().toString());
            lblRequestDate.setValue(simpleDateFormat.format(passwordChangeRequest.getRequestDate()));
            lblIdentificationNumber.setValue(passwordChangeRequest.getUserId().getIdentificationNumber());
            lblUser.setValue(passwordChangeRequest.getUserId().getFirstName());
            lblComercialAgency.setValue(passwordChangeRequest.getUserId().getEmployeeId().getComercialAgencyId().getName());
            txtNewPassword.setText(passwordChangeRequest.getNewPassword());
            txtRepeatNewPassword.setText(passwordChangeRequest.getNewPassword());

            if (passwordChangeRequest.getCurrentPassword() != null) {
                txtCurrentPassword.setValue(passwordChangeRequest.getCurrentPassword());
            }

            if (passwordChangeRequest.getIndApproved() != null) {
                if (passwordChangeRequest.getIndApproved() == true) {
                    rApprovedYes.setChecked(true);
                } else {
                    rApprovedNo.setChecked(true);
                }
            }
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtCurrentPassword.setReadonly(true);
        txtNewPassword.setReadonly(true);
        txtRepeatNewPassword.setReadonly(true);
        btnSave.setVisible(false);
    }
    
    public boolean validatePasswordChange() {
        //Valida que la confirmación de la nueva contraseña coincida con la nueva contraseña
        if (!txtRepeatNewPassword.getValue().equals(txtNewPassword.getValue())) {
            txtRepeatNewPassword.setValue("");
            txtRepeatNewPassword.setFocus(true);
            this.showMessage("msj.fieldsPasswordNotEquals", true, null);
        } else if (txtNewPassword.getValue().equals(txtCurrentPassword.getValue())) {
            txtNewPassword.setValue("");
            txtRepeatNewPassword.setValue("");
            txtNewPassword.setFocus(true);
            this.showMessage("msj.fieldsCurrentPasswordEqualsNewPassword", true, null);
        } else {
            return true;
        }
        return false;
    }

    public Boolean validateEmpty() {
        if (txtCurrentPassword.getText().isEmpty()) {
            txtCurrentPassword.setFocus(true);
            this.showMessage("msj.error.currentPassword", true, null);
        } else if (txtNewPassword.getText().isEmpty()) {
            txtNewPassword.setFocus(true);
            this.showMessage("msj.error.newPassword", true, null);
        } else if (txtRepeatNewPassword.getText().isEmpty()) {
            txtRepeatNewPassword.setFocus(true);
            this.showMessage("msj.error.repeatNewPassword", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void savePasswordChangeRequest(PasswordChangeRequest _passwordChangeRequest) throws RegisterNotFoundException, NullParameterException, GeneralException, EmptyListException, NoSuchAlgorithmException, UnsupportedEncodingException {
        boolean indApproved = true;
        EJBRequest request1 = new EJBRequest();
        Date dateRequest = null;
        List<User> userList = null;
        PasswordChangeRequest passwordChangeRequest = null;

        try {
            if (_passwordChangeRequest != null) {
                passwordChangeRequest = _passwordChangeRequest;
                dateRequest = passwordChangeRequest.getRequestDate();
            } else {
                passwordChangeRequest = new PasswordChangeRequest();
            }

            if (rApprovedYes.isChecked()) {
                indApproved = true;
            } else {
                indApproved = false;
            }

            //Valida si la contraseña actual es correcta
            Map params = new HashMap();
            params.put(Constants.CURRENT_PASSWORD, Encoder.MD5(txtCurrentPassword.getText()));
            params.put(Constants.USER_KEY, user.getId());
            request1.setParams(params);
            userList = userEJB.validatePassword(request1);

            if (userList.size() > 0) {
                //Obtiene el numero de secuencia para Solicitud de Cambio de Contraseña
                numberRequest = generateNumberSequence();
                dateRequest = new Date();
                lblRequestNumber.setValue(numberRequest);
                lblRequestDate.setValue(dateRequest.toString());

                //Se aprueba la solicitud automaticamente
                indApproved = true;
                //Se crea el objeto passwordChangeRequest
                createPasswordChangeRequest(passwordChangeRequest, numberRequest, dateRequest, indApproved);

                //Guardar la solicitud de cambio de contraseña en la BD
                passwordChangeRequest = personEJB.savePasswordChangeRequest(passwordChangeRequest);
                passwordChangeRequestParam = passwordChangeRequest;

                //Actualizar la contraseña del usuario en la BD
                user.setPassword(Encoder.MD5(txtNewPassword.getText()));
                user = userEJB.saveUser(user);

                rApprovedYes.setVisible(true);
                rApprovedNo.setVisible(true);
                if (passwordChangeRequest.getIndApproved() == true) {
                    rApprovedYes.setChecked(true);
                } else {
                    rApprovedNo.setChecked(true);
                }
                rApprovedYes.setDisabled(true);
                rApprovedNo.setDisabled(true);

                this.showMessage("msj.crud.passwordChangedRequestApproved", false, null);
                btnSave.setVisible(false);
            }
        } catch (WrongValueException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showError(ex);
        } finally {
            if (userList == null) {
                attempts++;
                if (attempts != 3) {
                    this.showMessage("msj.errorCurrentPasswordNotMatchInBD", true, null);
                    txtCurrentPassword.setText("");
                    txtNewPassword.setText("");
                    txtRepeatNewPassword.setText("");
                }
            }
            if (attempts == 3) {
                //Obtiene el numero de secuencia para Solicitud de Cambio de Contraseña
                numberRequest = generateNumberSequence();
                dateRequest = new Date();
                lblRequestNumber.setValue(numberRequest);
                lblRequestDate.setValue(dateRequest.toString());

                //Se rechaza la solicitud automaticamente
                indApproved = false;
                //Se crea el objeto passwordChangeRequest
                createPasswordChangeRequest(passwordChangeRequest, numberRequest, dateRequest, indApproved);

                //Guardar la solicitud de cambio de contraseña en la BD
                passwordChangeRequest = personEJB.savePasswordChangeRequest(passwordChangeRequest);
                passwordChangeRequestParam = passwordChangeRequest;

                rApprovedYes.setVisible(true);
                rApprovedNo.setVisible(true);
                if (passwordChangeRequest.getIndApproved() == true) {
                    rApprovedYes.setChecked(true);
                } else {
                    rApprovedNo.setChecked(true);
                }
                rApprovedYes.setDisabled(true);
                rApprovedNo.setDisabled(true);

                this.showMessage("msj.passwordChangedRequestRejected", true, null);
                btnSave.setVisible(false);
            }
            loadFields(passwordChangeRequestParam);
        }
    }

    public String generateNumberSequence() {
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.DOCUMENT_TYPE_KEY, Constants.DOCUMENT_TYPE_RENEWAL_PASSWORD);
            request1.setParams(params);
            List<Sequences> sequence = utilsEJB.getSequencesByDocumentType(request1);
            numberRequest = utilsEJB.generateNumberSequence(sequence, Constants.ORIGIN_APPLICATION_ADMIN_ID);
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            showError(ex);
        }
        return numberRequest;
    }

    public void createPasswordChangeRequest(PasswordChangeRequest passwordChangeRequest, String numberRequest, Date requestDate, boolean indApproved) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        passwordChangeRequest.setRequestNumber(numberRequest);
        passwordChangeRequest.setRequestDate(requestDate);
        passwordChangeRequest.setUserId(user);
        passwordChangeRequest.setCurrentPassword(Encoder.MD5(txtCurrentPassword.getText()));
        passwordChangeRequest.setNewPassword(Encoder.MD5(txtNewPassword.getText()));
        passwordChangeRequest.setIndApproved(indApproved);
        passwordChangeRequest.setCreateDate(new Timestamp(new Date().getTime()));
    }

    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException, EmptyListException, NoSuchAlgorithmException, UnsupportedEncodingException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    if (validatePasswordChange()) {
                        savePasswordChangeRequest(null);
                    }
                break;
                default:
                    break;
            }
        }
    }

    public void onclick$btnBack() {
        Executions.getCurrent().sendRedirect("listPasswordChangeRequest.zul");
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_VIEW:
                loadFields(passwordChangeRequestParam);
                txtCurrentPassword.setReadonly(true);
                txtNewPassword.setReadonly(true);
                txtRepeatNewPassword.setDisabled(false);
                blockFields();
                rApprovedYes.setDisabled(true);
                rApprovedNo.setDisabled(true);
                break;
            case WebConstants.EVENT_ADD:
                lblComercialAgency.setValue(user.getEmployeeId().getComercialAgencyId().getName());
                lblUser.setValue(user.getFirstName() + " " + user.getLastName());
                lblIdentificationNumber.setValue(user.getIdentificationNumber());
                break;
            default:
                break;
        }
    }

    private Object getSelectedItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
