//package com.alodiga.wallet.admin.web.controllers;
//
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.SuspendNotAllowedException;
//import org.zkoss.zul.Button;
//import org.zkoss.zul.Textbox;
//import org.zkoss.zul.Window;
//
//import com.alodiga.services.provider.commons.ejbs.ProductEJB;
//import com.alodiga.services.provider.commons.models.Permission;
//import com.alodiga.services.provider.commons.models.Provider;
//import com.alodiga.services.provider.commons.utils.EJBServiceLocator;
//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
//import com.alodiga.wallet.admin.web.utils.AccessControl;
//import com.alodiga.wallet.admin.web.utils.WebConstants;
//
//public class AddProviderController extends GenericAbstractAdminController {
//
//    private static final long serialVersionUID = -9145887024839938515L;
//    private Textbox txtName;
//    private Textbox txtAddress;
//    private Window winAddProviderView;
//    private ProductEJB productEJB = null;
//    private Provider providerParam;
//    private Button btnSave;
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//        initialize();
//    }
//
//    @Override
//    public void initView(int eventType, String adminView) {
//        super.initView(eventType, "sp.crud.provider");
//    }
//
//    @Override
//    public void initialize() {
//        super.initialize();
//        try {
//        	productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void clearFields() {
//        
//    }
//
//    public void blockFields() {
//    	  txtName.setReadonly(true);
//          txtAddress.setReadonly(true);
//          btnSave.setVisible(false);
//    }
//
//    public Boolean validateEmpty() {
//    	if (txtName.getText().isEmpty()) {
//            txtName.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        } else if (txtAddress.getText().isEmpty()) {
//        	txtAddress.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        } else {
//            return true;
//        }
//        return false;
//
//    }
//
//    private void save(Provider _provider) {
//        Provider provider = new Provider();
//        try {
//            if(_provider != null && _provider.getId() != null){
//            provider.setId(_provider.getId());
//            }
//            provider.setName(txtName.getText());
//            provider.setAddress(txtAddress.getText());
//            provider.setEnabled(true);
//            request.setParam(provider);
//            providerParam = productEJB.saveProvider(request);
//            providerParam = provider;
//            eventType = WebConstants.EVENT_EDIT;
//            this.showMessage("sp.common.save.success", false, null);
//            AccessControl.saveAction(Permission.ADD_PROVIDER, "Ingreso el proveedor= " + provider.getName());
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//   
//    
//    public void onClick$btnSave() {
//        if (validateEmpty()) {
//            switch (eventType) {
//                case WebConstants.EVENT_ADD:
//                    save(null);
//                    break;
//                case WebConstants.EVENT_EDIT:
//                    save(providerParam);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//	public void loadData() {
//		switch (eventType) {
//		case WebConstants.EVENT_EDIT:
//			break;
//		case WebConstants.EVENT_VIEW:
//			break;
//		case WebConstants.EVENT_ADD:
//			break;
//		default:
//			break;
//		}
//
//	}
//	
//	 public void onClick$btnBack() {
//		 winAddProviderView.detach();
//	   	 Window window = (Window)Executions.createComponents("catProviders.zul", null, null);
//	        try {
//				window.doModal();
//			} catch (SuspendNotAllowedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	   }
//}
