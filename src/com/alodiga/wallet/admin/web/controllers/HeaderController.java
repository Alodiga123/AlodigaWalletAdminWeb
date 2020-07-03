package com.alodiga.wallet.admin.web.controllers;
//package com.alodiga.services.provider.web.controllers;
//
//import com.alodiga.services.provider.commons.ejbs.UtilsEJB;
//import com.alodiga.services.provider.commons.exceptions.GeneralException;
//import com.alodiga.services.provider.commons.genericEJB.AbstractSPEntity;
//import com.alodiga.services.provider.commons.models.Enterprise;
//import com.alodiga.services.provider.commons.utils.EJBServiceLocator;
//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.services.provider.web.generic.controllers.GenericAbstractController;
//import com.alodiga.services.provider.web.generic.controllers.GenericSPController;
//import com.alodiga.services.provider.web.utils.AccessControl;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zul.Label;
//
//public class HeaderController extends GenericAbstractController implements GenericSPController {
//
//    private static final long serialVersionUID = -9145887024839938515L;
//    private Label lblAtcNumber;
//    private UtilsEJB utilsEJB;
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//
//        initialize();
//
//    }
//
//        @Override
//    public void initialize() {
//        try {
//
//            if (AccessControl.loadCurrentAccount() != null) {
//                lblAtcNumber.setValue(AccessControl.loadCurrentAccount().getEnterprise().getAtcNumber());
//            } else {
//                utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
//                request.setParam(Enterprise.ALODIGA_USA);
//                Enterprise enterprise = utilsEJB.loadEnterprise(request);
//                lblAtcNumber.setValue(enterprise.getAtcNumber());
//            }
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    @Override
//    public void loadPermission(AbstractSPEntity clazz) throws GeneralException {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
//}
