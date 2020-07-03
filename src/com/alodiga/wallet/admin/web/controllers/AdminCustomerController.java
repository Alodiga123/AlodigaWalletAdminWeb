//package com.alodiga.wallet.admin.web.controllers;
//
//import java.sql.Timestamp;
//import java.util.Map;
//
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zul.Button;
//import org.zkoss.zul.Checkbox;


//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractAdminController;
//import com.alodiga.wallet.admin.web.utils.AccessControl;
//import com.alodiga.wallet.admin.web.utils.WebConstants;
//
//public class AdminCustomerController extends GenericAbstractAdminController {
//
//    private static final long serialVersionUID = -9145887024839938515L;
//    private Textbox txtDNI;
//    private Textbox txtFirstName;
//    private Textbox txtLastName;
//    private Textbox txtEmail;
////    private Textbox txtCountryCode;
////    private Textbox txtRegionCode;
//    private Textbox txtPhoneNumber;
//    private Textbox txtAddress;
//    private Checkbox cbxEnabled;
//    private Customer customerParam;
//    private Button btnSave;
//    private CustomerEJB customerEJB;
//    private AuditoryEJB auditoryEJB;
//    private String ipAddress;
//    private User user;
//    Map params = null;
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//        customerParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Customer) Sessions.getCurrent().getAttribute("object") : null;
//        initialize();
//        initView(eventType, "sp.crud.customer");
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
//        	user = AccessControl.loadCurrentUser();
//        	ipAddress = Executions.getCurrent().getRemoteAddr();
//        	auditoryEJB = (AuditoryEJB) EJBServiceLocator.getInstance().get(EjbConstants.AUDITORY_EJB);
//            customerEJB = (CustomerEJB) EJBServiceLocator.getInstance().get(EjbConstants.CUSTOMER_EJB);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
////   
//    public void clearFields() {
//        
//    }
//
//    private void loadFields(Customer customer) {
//        if (customer != null) {//UPDATE
//            cbxEnabled.setChecked(customer.getEnabled());
//            txtFirstName.setText(customer.getFirstName());
//            txtLastName.setText(customer.getLastName());
//            txtEmail.setText(customer.getEmail());
//            txtAddress.setText(customer.getAddress());
//            txtDNI.setText(customer.getDni());
//            txtPhoneNumber.setText(customer.getPhoneNumber());
//        } 
//    }
//
//    public void blockFields() {
//        cbxEnabled.setDisabled(true);
//        txtFirstName.setReadonly(true);
//        txtLastName.setReadonly(true);
//        txtEmail.setReadonly(true);
//        txtDNI.setReadonly(true);
//        txtAddress.setReadonly(true);
////        txtCountryCode.setReadonly(true);
////        txtRegionCode.setReadonly(true);
//        txtPhoneNumber.setReadonly(true);
//        btnSave.setVisible(false);
//    }
//
//    public Boolean validateEmpty() {
//    	if (txtFirstName.getText().isEmpty()) {
//            txtFirstName.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        }  else if (txtEmail.getText().isEmpty()) {
//            txtEmail.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        } else if (txtDNI.getText().isEmpty()) {
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
//            }else
//            	customer.setCreationDate(new Timestamp((new java.util.Date().getTime())));
//            customer.setAddress(txtAddress.getText());
//            customer.setFirstName(txtFirstName.getText());
//            customer.setLastName(txtLastName.getText());
//            customer.setEmail(txtEmail.getText());
//            customer.setPhoneNumber(txtPhoneNumber.getText());
//            customer.setEnabled(cbxEnabled.isChecked());
//            customer.setDni(txtDNI.getText());
//            customer.setPhoneNumber(txtPhoneNumber.getText());
//            request.setParam(customer);
//            customerParam = customerEJB.saveCustomer(request);
//            this.showMessage("sp.common.save.success", false, null);
//            AccessControl.saveAction(Permission.EDIT_CUSTOMER, "Ingreso el cliente= " + customer.getFirstName() + " "+customer.getLastName());
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
//                case WebConstants.EVENT_ADD_DESCENDANT:
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
//    public void loadData() {
//        switch (eventType) {
//            case WebConstants.EVENT_EDIT:
//                loadFields(customerParam);
//
//                break;
//            case WebConstants.EVENT_VIEW:
//                loadFields(customerParam);
//
//                break;
//            default:
//                break;
//        }
//    }
//
// 
//    public void saveAudit(Customer fCustomerOld ,Customer fCustomerNew){
//        EJBRequest request1 = new EJBRequest();
//        EJBRequest request2 = new EJBRequest();            
//        String result = "";
//         String oldValue ="";
//        request1.setParam(fCustomerOld);
//        request2.setParam(fCustomerNew);
//
//        try {
//            result = auditoryEJB.getNaturalFieldCustomer(request1, request2);
//        } catch (Exception ex) {
//            
//        }
//
//        if(!result.isEmpty()||!"".equals(result)){
//            String name = fCustomerOld.getFirstName();
//            String lastName = fCustomerOld.getLastName();
//            String mail = fCustomerOld.getEmail();
//            String phone = fCustomerOld.getPhoneNumber();
//            String DNI = fCustomerOld.getDni();
//            String address = fCustomerOld.getAddress();
//            oldValue = "Name:"+name+"|LastName:"+lastName+"|Mail:"+mail
//                    +"|PhoneNumber:"+phone+"|DNI:"+DNI+"|Address:"+address;
//            
//            try {
//				EJBRequest ejbRequest = new EJBRequest();
//				ejbRequest.setParam(eventType);
//				Event ev = auditoryEJB.loadEvent(ejbRequest);
//				Audit audit = new Audit();
//				EJBRequest auditRequest = new EJBRequest();
//				audit.setUser(user);
//				audit.setEvent(ev);
//				Permission permission = PermissionManager.getInstance().getPermissionById(2L);
//				audit.setPermission(permission);
//				audit.setCreationDate(new Timestamp((new java.util.Date().getTime())));
//				audit.setTableName("Customer");
//				audit.setRemoteIp(ipAddress);
//				audit.setOriginalValues(oldValue);
//				audit.setNewValues(result);
//				audit.setResponsibleType("usuario");
//				auditRequest.setParam(audit);
//				audit = auditoryEJB.saveAudit(auditRequest);
//            } catch (Exception ex) {
//				ex.printStackTrace();
//			}
//        }
//    }
//}
