//package com.alodiga.wallet.admin.web.controllers;
//
//import java.sql.Timestamp;
//
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.SuspendNotAllowedException;
//import org.zkoss.zul.Button;
//import org.zkoss.zul.Textbox;
//import org.zkoss.zul.Window;
//
//import com.alodiga.services.provider.commons.ejbs.AuditoryEJB;
//import com.alodiga.services.provider.commons.ejbs.CustomerEJB;
//import com.alodiga.services.provider.commons.genericEJB.EJBRequest;
//import com.alodiga.services.provider.commons.managers.PermissionManager;
//import com.alodiga.services.provider.commons.models.Audit;
//import com.alodiga.services.provider.commons.models.Customer;
//import com.alodiga.services.provider.commons.models.Event;
//import com.alodiga.services.provider.commons.models.Permission;
//import com.alodiga.services.provider.commons.models.User;
//import com.alodiga.services.provider.commons.utils.EJBServiceLocator;
//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
//import com.alodiga.wallet.admin.web.utils.AccessControl;
//import com.alodiga.wallet.admin.web.utils.WebConstants;
//
//public class AddCustomerController extends GenericAbstractAdminController {
//
//    private static final long serialVersionUID = -9145887024839938515L;
//    private Textbox txtDNI;
//    private Textbox txtFirstName;
//    private Textbox txtLastName;
//    private Textbox txtEmail;
//    private Textbox txtPhoneNumber;
//    private Textbox txtAddress;
//    private Customer customerParam;
//    private Window winAddCustomerView;
//    private Button btnSave;
//    private CustomerEJB customerEJB;
//    private AuditoryEJB auditoryEJB;
//    private String ipAddress;
//    private User user;
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//        user = AccessControl.loadCurrentUser();
//        initialize();
//    }
//
//    @Override
//    public void initView(int eventType, String adminView) {
//        super.initView(eventType, "sp.crud.customer");
//    }
//
//    @Override
//    public void initialize() {
//        super.initialize();
//        try {
//        	ipAddress = Executions.getCurrent().getRemoteAddr();
//        	auditoryEJB = (AuditoryEJB) EJBServiceLocator.getInstance().get(EjbConstants.AUDITORY_EJB);
//            customerEJB = (CustomerEJB) EJBServiceLocator.getInstance().get(EjbConstants.CUSTOMER_EJB);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void clearFields() {
//        
//    }
//
////    private void loadFields(Customer customer) {
////        if (customer != null) {//UPDATE
////            cbxEnabled.setChecked(customer.getEnabled());
////            txtFirstName.setText(customer.getFirstName());
////            txtLastName.setText(customer.getLastName());
////            txtEmail.setText(customer.getEmail());
////            txtAddress.setText(customer.getAddress());
////            txtDNI.setText(customer.getDni());
////            txtPhoneNumber.setText(customer.getPhoneNumber());
////        } 
////    }
//
//    public void blockFields() {
////        cbxEnabled.setDisabled(true);
//        txtFirstName.setReadonly(true);
//        txtLastName.setReadonly(true);
//        txtEmail.setReadonly(true);
//        txtDNI.setReadonly(true);
//        txtAddress.setReadonly(true);
//        txtPhoneNumber.setReadonly(true);
//        btnSave.setVisible(false);
//    }
//
//    public Boolean validateEmpty() {
//    	if (txtFirstName.getText().isEmpty()) {
//            txtFirstName.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        }else if (txtEmail.getText().isEmpty()) {
//            txtEmail.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        }   else if (txtDNI.getText().isEmpty()) {
//        	txtDNI.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        }  else {
//            return true;
//        }
//        return false;
//
//    }
//
//    private void save(Customer _customer) {
//        try {
//            Customer customer = new Customer();
//            if (_customer != null) {
//               customer.setId(customerParam.getId());
//               customer.setCreationDate(customerParam.getCreationDate());
//            }
//            customer.setAddress(txtAddress.getText());
//            customer.setFirstName(txtFirstName.getText());
//            customer.setLastName(txtLastName.getText());
//            customer.setEmail(txtEmail.getText());
//            customer.setPhoneNumber(txtPhoneNumber.getText());
//            customer.setCreationDate(new Timestamp((new java.util.Date().getTime())));
//            customer.setEnabled(true);
//            customer.setDni(txtDNI.getText());
//            customer.setPhoneNumber(txtPhoneNumber.getText());
//            request.setParam(customer);
//            customerParam = customerEJB.saveCustomer(request);
//            blockFields();
//            this.showMessage("sp.common.save.success", false, null);
//            AccessControl.saveAction(Permission.EDIT_CUSTOMER, "Ingreso el cliente= " + customer.getFirstName() + " "+customer.getLastName());
////            saveAudit(_customer, customer);
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
//                    save(customerParam);
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
//		 winAddCustomerView.detach();
//	   	 Window window = (Window)Executions.createComponents("catCustomers.zul", null, null);
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
//	 
//	  
//	    public void saveAudit(Customer fCustomerOld ,Customer fCustomerNew){
//	        EJBRequest request1 = new EJBRequest();
//	        EJBRequest request2 = new EJBRequest();            
//	        String result = "";
//	         String oldValue ="";
//	        request1.setParam(fCustomerOld);
//	        request2.setParam(fCustomerNew);
//
//	        try {
//	            result = auditoryEJB.getNaturalFieldCustomer(request1, request2);
//	        } catch (Exception ex) {
//	            
//	        }
//
//	        if(!result.isEmpty()||!"".equals(result)){
//	            String name = fCustomerOld.getFirstName();
//	            String lastName = fCustomerOld.getLastName();
//	            String phoneNumber = fCustomerOld.getPhoneNumber();
//	            String email = fCustomerOld.getEmail();
//	            String dni = fCustomerOld.getDni();
//	            String address = fCustomerOld.getAddress();
//	            oldValue = "Name:"+name+"|LastName:"+lastName+"|PhoneCellNumber:"+phoneNumber
//	                    +"|DNI:"+dni+"|Address:"+address+"|Email:"+email;
//	            
//	            try {
//					EJBRequest ejbRequest = new EJBRequest();
//					ejbRequest.setParam(eventType);
//					Event ev = auditoryEJB.loadEvent(ejbRequest);
//					Audit audit = new Audit();
//					EJBRequest auditRequest = new EJBRequest();
//					audit.setUser(user);
//					audit.setEvent(ev);
//					Permission permission = PermissionManager.getInstance().getPermissionById(2L);
//					audit.setPermission(permission);
//					audit.setCreationDate(new Timestamp((new java.util.Date().getTime())));
//					audit.setTableName("Customer");
//					audit.setRemoteIp(ipAddress);
//					audit.setOriginalValues(oldValue);
//					audit.setNewValues(result);
//					audit.setResponsibleType("usuario");
//					auditRequest.setParam(audit);
//					audit = auditoryEJB.saveAudit(auditRequest);
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//	        }
//	    }
//}
