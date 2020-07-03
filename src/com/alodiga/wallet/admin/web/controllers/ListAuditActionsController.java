//package com.alodiga.wallet.admin.web.controllers;
//
//import java.sql.Timestamp;
//import java.util.List;
//
//import org.zkoss.util.resource.Labels;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zul.Combobox;
//import org.zkoss.zul.Comboitem;
//import org.zkoss.zul.Datebox;
//import org.zkoss.zul.Label;
//import org.zkoss.zul.Listbox;
//import org.zkoss.zul.Listcell;
//import org.zkoss.zul.Listitem;
//import org.zkoss.zul.Textbox;
//
//import com.alodiga.services.provider.commons.ejbs.AccessControlEJB;
//import com.alodiga.services.provider.commons.ejbs.AuditoryEJB;
//import com.alodiga.services.provider.commons.exceptions.EmptyListException;
//import com.alodiga.services.provider.commons.genericEJB.EJBRequest;
//import com.alodiga.services.provider.commons.models.AuditAction;
//import com.alodiga.services.provider.commons.models.Permission;
//import com.alodiga.services.provider.commons.utils.EJBServiceLocator;
//import com.alodiga.services.provider.commons.utils.EjbConstants;
//import com.alodiga.services.provider.commons.utils.GeneralUtils;
//import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractListController;
//import com.alodiga.wallet.admin.web.utils.Utils;
//
//public class ListAuditActionsController extends GenericAbstractListController<AuditAction> {
//
//    private static final long serialVersionUID = -9145887024839938515L;
//    private Listbox lbxRecords;
//    private Textbox txtName;
//    private Textbox txtLogin;
//    private Combobox cmbPermissions;
//    private AccessControlEJB accessControlEJB;
//    private AuditoryEJB auditoryEJB;
//    private Datebox dtbBeginningDate;
//    private Datebox dtbEndingDate;
//    private Label lblInfo;
//
//    @Override
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);
//        initialize();
//    }
//
//
//    public void startListener() {
//    }
//
//    @Override
//    public void initialize() {
//        super.initialize();
//        try {
//            adminPage = "adminAccount.zul";
//            checkPermissions();
//            dtbBeginningDate.setFormat("yyyy/MM/dd");
//            dtbBeginningDate.setValue(new Timestamp(new java.util.Date().getTime()));
//            dtbEndingDate.setFormat("yyyy/MM/dd");
//            dtbEndingDate.setValue(new Timestamp(new java.util.Date().getTime()));
//            accessControlEJB = (AccessControlEJB) EJBServiceLocator.getInstance().get(EjbConstants.ACCESS_CONTROL_EJB);
//            auditoryEJB = (AuditoryEJB) EJBServiceLocator.getInstance().get(EjbConstants.AUDITORY_EJB);
//            loadPermisssions();
//
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void loadList(List<AuditAction> list) {
//        try {
//            lbxRecords.getItems().clear();
//            Listitem item = null;
//            if (list != null && !list.isEmpty()) {
//                btnDownload.setVisible(true);
//                for (AuditAction auditAction : list) {
//
//                    item = new Listitem();
//                    item.setValue(auditAction);
//                    item.appendChild(new Listcell(auditAction.getUser().getLogin()));
//                    item.appendChild(new Listcell(auditAction.getUser().getFirstName() + " " + auditAction.getUser().getLastName()));
//                    try{
//                    	item.appendChild(new Listcell(auditAction.getPermission() != null ? auditAction.getPermission().getPermissionDataByLanguageId(languageId).getAlias() : ""));
//                    } catch (Exception ex) {
//                    	 item.appendChild(new Listcell());
//                    }
//                    item.appendChild(new Listcell(GeneralUtils.date2String(auditAction.getDate(), GeneralUtils.FORMAT_DATE_USA)));
////                    item.appendChild(new Listcell(auditAction.getHost()));
//                    item.appendChild(new Listcell(auditAction.getInfo()));
//                    item.setParent(lbxRecords);
//                }
//            } else {
//                btnDownload.setVisible(false);
//                item = new Listitem();
//                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
//                item.appendChild(new Listcell());
//                item.appendChild(new Listcell());
//                item.appendChild(new Listcell());
//                item.appendChild(new Listcell());
//                item.appendChild(new Listcell());
//                item.setParent(lbxRecords);
//            }
//
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void getData() {
//        
//    }
//
//    public void onClick$btnDownload() throws InterruptedException {
//        try {
//            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.auditActions.list"));
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void onClick$btnClear() throws InterruptedException {
//        txtName.setText("");
//        txtLogin.setText("");
//        cmbPermissions.setSelectedIndex(0);
//        lbxRecords.getItems().clear();
//        lblInfo.setVisible(false);
//    }
//
//    @Override
//    public void onClick$btnSearch() throws InterruptedException {
//        try {
//
//            if (dtbBeginningDate.getValue()==null || dtbBeginningDate.getValue()==null) {
//                this.showMessage("sp.error.dateSelectInvalid.Invalid", true, null);
//            }else if (dtbBeginningDate.getValue().getTime() > dtbEndingDate.getValue().getTime()) {
//                this.showMessage("sp.error.dateSelectInvalid.Invalid", true, null);
//            }
//            String login = !txtLogin.getText().isEmpty() ? txtLogin.getText() : null;
//            String fullName = !txtName.getText().isEmpty() ? txtName.getText() : null;
//            Long permissionId = cmbPermissions.getSelectedIndex() > 0 ? ((Permission) cmbPermissions.getSelectedItem().getValue()).getId() : null;
//            loadList(auditoryEJB.searchAuditAction(login, fullName, permissionId, dtbBeginningDate.getValue(), dtbEndingDate.getValue()));
//
//        }catch (EmptyListException ex) {
//        	lblInfo.setVisible(true);
//        	lblInfo.setValue(Labels.getLabel("sp.error.empty.list"));
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    private void loadPermisssions() {
//        List<Permission> permissions = null;
//        try {
//
//            cmbPermissions.getItems().clear();
//            permissions = accessControlEJB.getPermissions(new EJBRequest());
//            Comboitem cmbItem = new Comboitem();
//            cmbItem.setLabel(Labels.getLabel("sp.common.combobox.all"));
//            cmbItem.setValue(null);
//            cmbItem.setParent(cmbPermissions);
//            for (Permission p : permissions) {
//                System.out.println("p.id "+ p.getId());
//                cmbItem = new Comboitem();
//                cmbItem.setLabel(p.getPermissionDataByLanguageId(languageId).getAlias());
//                cmbItem.setDescription(p.getPermissionGroup().getPermissionGroupDataByLanguageId(languageId).getAlias());
//                cmbItem.setValue(p);
//                cmbItem.setParent(cmbPermissions);
//            }
//            cmbPermissions.setSelectedIndex(0);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public List<AuditAction> getFilteredList(String filter) {
//        return null;
//    }
//
//    public void onClick$btnAdd() throws InterruptedException {
//    }
//}
