package com.alodiga.wallet.admin.web.controllers;
//package com.alodiga.services.provider.web.controllers;
//
//import com.alodiga.services.provider.commons.ejbs.UserEJB;
//import com.alodiga.services.provider.commons.models.Account;
//import com.alodiga.services.provider.commons.models.User;
//import com.alodiga.services.provider.commons.utils.EJBServiceLocator;
//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.services.provider.commons.utils.Encoder;
//import com.alodiga.services.provider.web.generic.controllers.GenericAbstractAdminController;
//import org.zkoss.zul.Window;
//import org.zkoss.zul.Label;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zul.Textbox;
//import org.zkoss.util.resource.Labels;
//
//public class EditPasswordViewController extends GenericAbstractAdminController {
//
//    private static final long serialVersionUID = -9145887024839938515L;
//    private Textbox txtOldPassword;
//    private Textbox txtNewPassword1;
//    private Textbox txtNewPassword2;
//    private Label lblInfo;
//    
//    private Account account;
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//        Object object = (Executions.getCurrent().getArg().get("object") != null) ? (Object) Executions.getCurrent().getArg().get("object") : null;
//        account = (Account) object;
//        
//        initialize();
//        initView(eventType, "sp.crud.account");
//    }
//
//    @Override
//    public void initView(int eventType, String view) {
//        super.initView(eventType, view);
//    }
//
//    @Override
//    public void initialize() {
//        try {
//            super.initialize();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            lblInfo.setValue(Labels.getLabel("sp.error.general"));
//        }
//    }
//
//    public void clearFields() {
//    }
//
//    public void onClick$btnSavePass() {
//        lblInfo.setValue("");
//        try {
//            if (account != null) {
//                if (account.getAccountPassword().equals(Encoder.MD5(txtOldPassword.getText()))) {
//                    if (txtNewPassword1.getText().equals(txtNewPassword2.getText())) {
//                        account.setAccountPassword(Encoder.MD5(txtNewPassword1.getText()));
//                        UserEJB userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
//                        request.setParam(account);
////                        userEJB.saveAccount(request);
//                        
//                        lblInfo.setValue(Labels.getLabel("sp.common.save.update"));
//                    } else {
//                        lblInfo.setValue(Labels.getLabel("sp.error.confirmationPasswordNotEqual"));
//                    }
//                } else {
//                    lblInfo.setValue(Labels.getLabel("sp.error.wrongPassword"));
//                }
//            } else {
//                lblInfo.setValue(Labels.getLabel("sp.error.general"));
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            lblInfo.setValue(Labels.getLabel("sp.error.general"));
//        }
//    }
//
//    @Override
//    public void blockFields() {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void loadData() {
//        // TODO Auto-generated method stub
//    }
//}
