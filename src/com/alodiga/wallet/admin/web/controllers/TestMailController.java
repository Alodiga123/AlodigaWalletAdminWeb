package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Textbox;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractController;

public class TestMailController extends GenericAbstractController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtEmail;
    private Textbox txtSubject;
    private Textbox txtContent;
//    private UtilsEJB utilsEJB = null;
//    private Enterprise enterprise;
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
//            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
//            enterprise = utilsEJB.loadEnterprise(new EJBRequest(Enterprise.TURBINES));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnCancel() {
        clearFields();
    }




    public Boolean validateEmpty() {
        if (txtEmail.getText().isEmpty()) {
            txtEmail.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
        } else if (txtSubject.getText().isEmpty()) {
            txtSubject.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
        } else if (txtContent.getText().isEmpty()) {
            txtContent.setFocus(true);
            this.showMessage("msj.error.field.cannotNull", true, null);
        }  else {
            return true;
        }
        return false;

    }

    public void onClick$btnSend() {
        if (validateEmpty()) {
//            try {
//                ArrayList<String> recipents = new ArrayList<String>();
//                recipents.add(txtEmail.getText());
//                Mail mail = ServiceMails.getTestMail(enterprise, recipents, txtSubject.getText(), txtContent.getText());
//                utilsEJB.sendMail(mail);
//                this.showMessage(Labels.getLabel("wallet.common.sent"), false, null);
//            } catch (Exception ex) {
//                showError(ex);
//            }
    }

    }

    public void clearFields() {
        txtContent.setRawValue(null);
        txtSubject.setRawValue(null);
        txtEmail.setRawValue(null);
    }

  }
