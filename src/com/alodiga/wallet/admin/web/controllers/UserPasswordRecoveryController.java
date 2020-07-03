//package com.alodiga.wallet.admin.web.controllers;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.zkoss.util.resource.Labels;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.util.GenericForwardComposer;
//import org.zkoss.zul.Label;
//import org.zkoss.zul.Textbox;
//
//import com.alodiga.services.provider.commons.ejbs.UserEJB;
//import com.alodiga.services.provider.commons.exceptions.RegisterNotFoundException;
//import com.alodiga.services.provider.commons.models.User;
//import com.alodiga.services.provider.commons.utils.EJBServiceLocator;
//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.services.provider.commons.utils.QueryConstants;
//import com.alodiga.wallet.admin.web.generic.controllers.GenericSPController;
//import com.alodiga.wallet.admin.web.utils.AccessControl;
//
//
//public class UserPasswordRecoveryController extends GenericForwardComposer implements GenericSPController {
//
//    private static final long serialVersionUID = -9145887024839938515L;
////    private Window winPasswordRecoveryView;
//    private Textbox txtLogin;
//    private Label lblInfo;
//    private UserEJB userEJB = null;
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//        initialize();
//    }
//
//    public void initialize() {
//        try {
//            userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
//        } catch (Exception e) {
//            lblInfo.setValue(Labels.getLabel("sp.error.general"));
//        }
//    }
//
//    public void onClick$tbbRecoverPass() throws InterruptedException {
//        if (validateEmpty()) {
//            try {
//                User user = null;
//                Map params = new HashMap<String, Object>();
//                params.put(QueryConstants.PARAM_LOGIN, txtLogin.getText());
//                request.setParams(params);
//                user = userEJB.loadUserByLogin(request);
//                AccessControl.generateNewPassword(user, false);
//                lblInfo.setValue(Labels.getLabel("sp.common.recoveryPassword.success"));
//            } catch (RegisterNotFoundException e) {
//                lblInfo.setValue(Labels.getLabel("sp.common.recoveryPassword.notFound"));
//            } catch (Exception e) {
//                e.printStackTrace();
//                lblInfo.setValue(Labels.getLabel("sp.error.general"));
//            }
//        }
//    }
//
//    public Boolean validateEmpty() {
//        Boolean valid = true;
//        if (txtLogin.getText().isEmpty()) {
//            valid = false;
//            lblInfo.setValue(Labels.getLabel("sp.error.field.cannotNull"));
//            txtLogin.setFocus(true);
//        }
//        return valid;
//    }
//
////    public void loadPermission(AbstractDistributionEntity clazz) throws Exception {
////        throw new UnsupportedOperationException("Not supported yet.");
////    }
//}
