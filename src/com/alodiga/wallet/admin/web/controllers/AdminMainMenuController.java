package com.alodiga.wallet.admin.web.controllers;

import java.util.ArrayList;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.PermissionGroup;
import com.alodiga.wallet.common.model.Profile;
import com.alodiga.wallet.common.model.User;

public class AdminMainMenuController extends GenericForwardComposer {

    private static final long serialVersionUID = -9145887024839938515L;
    User currentuser = null;
    Listcell ltcFullName;
    Listcell ltcProfile;
    Listcell ltcLogin;
    private static String OPTION = "option";
    private static String OPTION_NONE = "none";
    private static String OPTION_CUSTOMERS_LIST = "ltcCustomerList";
    private List<Permission> permissions;
    private List<PermissionGroup> permissionGroups;
    private List<PermissionGroup> pGroups;
    private PermissionManager pm = null;
    private Listbox lbxPermissions;
    private Profile currentProfile = null;
    private Long languageId;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    public void initialize() {
        try {
            pm = PermissionManager.getInstance();
            languageId = AccessControl.getLanguage();
            loadAccountData();
            loadPemissions();
            checkOption();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkOption() {
        String option = getOptionInSession();
        if (option.equals(OPTION_NONE)) {
        } else if (option.equals(OPTION_CUSTOMERS_LIST)) {
            //ltcCustomerList.setImage("/images/icon-target.png");
        }

    }

    private void loadAccountData() {
    	 try {
             currentuser = AccessControl.loadCurrentUser();
             currentProfile = currentuser.getCurrentProfile();
             ltcFullName.setLabel(currentuser.getFirstName() + " " + currentuser.getLastName());
             ltcLogin.setLabel(currentuser.getLogin());
             ltcProfile.setLabel(currentProfile.getProfileDataByLanguageId(languageId).getAlias());
         } catch (Exception ex) {
             ex.printStackTrace();
         }
    }

    private void loadMenu() {
        try {
            pGroups = new ArrayList<PermissionGroup>();
            permissionGroups = pm.getPermissionGroups();
            for (PermissionGroup pg : permissionGroups) {
                if (existPermissionInGroup(permissions, pg.getId())) {
                    pGroups.add(pg);
                }
            }

            if (!pGroups.isEmpty()) {//ES USUARIO TIENE AL MENOS UN PERMISO ASOCIADO A UN GRUPO
                for (PermissionGroup pg : pGroups) {
                    switch (pg.getId().intValue()) {
                        case 1://Operational Management
                            loadOperationalManagementGroup(pg);
                            break;
                        case 2://Secutiry Management
                            loadSecurityManagementGroup(pg);
                            break;
                        case 3://Configurations Management
                            loadConfigurationsManagementGroup(pg);
                            break;
                        case 5://Configurations Management
                        	loadTransactionalManagementGroup(pg);
                            break;
                        default:
                            break;
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private boolean existPermissionInGroup(List<Permission> ps, Long permissionGroupId) {
        for (Permission p : ps) {
            if (p.getPermissionGroup().getId().equals(permissionGroupId)) {
                return true;
            }
        }
        return false;
    }

    private Permission loadPermission(Long permissionId) {

        for (Permission p : permissions) {
            if (p.getId().equals(permissionId)) {
                return p;
            }
        }
        return null;
    }

    private void loadPemissions() {
        try {
                permissions = pm.getPermissionByProfileId(currentuser.getCurrentProfile().getId());
            if (permissions != null && !permissions.isEmpty()) {
                loadMenu();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getOptionInSession() {
        return Sessions.getCurrent().getAttribute(OPTION) != null ? Sessions.getCurrent().getAttribute(OPTION).toString() : OPTION_NONE;
    }

    private Listgroup createListGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = new Listgroup();
        listgroup.setOpen(false);
        Listcell listcell = new Listcell();
        listcell.setLabel(permissionGroup.getPermissionGroupDataByLanguageId(languageId).getAlias());
        listcell.setParent(listgroup);
        listgroup.setParent(lbxPermissions);
        return listgroup;
    }

    private void loadOperationalManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Permission.LIST_COUNTRIES, "listCountries.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_CURRENCIES, "listCurrency.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_PRODUCTS, "listProducts.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_REPORTS, "listReports.zul", permissionGroup, listgroup);
        createCell(Permission.REPORT_EXECUTE, "managementReport.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_BANK, "listBank.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_EXCHANGE_RATE, "listExchangeRate.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_DOCUMENTS_PERSON_TYPE, "listDocumentsPersonType.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_COLLECTIONS_TYPE, "listCollectionTypes.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_BUSINESS_CATEGORY, "listBusinnesCategory.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_COLLECTIONS_REQUEST, "listCollectionsRequest.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_AFFILIATIONSTATUSPRERELATIONSHIPS, "listStatusBusinessAffiliation.zul", permissionGroup, listgroup);
    }

    private void loadSecurityManagementGroup(PermissionGroup permissionGroup) {

        Listgroup listgroup = createListGroup(permissionGroup);
//        createCell(Permission.AUDIT_ACTIONS, "listAuditActions.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_AUDI, "listAuditActions.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_PROFILES, "listProfiles.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_USERS, "listUsers.zul", permissionGroup, listgroup);
    }

    private void loadConfigurationsManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Permission.LIST_PREFERENCES, "listSpecificsSetting.zul", permissionGroup, listgroup);
        createCell(Permission.ADMIN_SETTINGS, "adminSettings.zul", permissionGroup, listgroup);
        createCell(Permission.BALANCE_ADJUSMENT, "balanceAdjusmentView.zul", permissionGroup, listgroup);
    }

    private void loadTransactionalManagementGroup(PermissionGroup permissionGroup) {
        Listgroup listgroup = createListGroup(permissionGroup);
        createCell(Permission.LIST_COMMISSION, "listCommission.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_TRANSACTION, "listTransactions.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_OPERATION_BANK, "listBankingOperations.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_MANUAL_RECHARGUES_APPROVAL, "listManualRecharge.zul", permissionGroup, listgroup);
        createCell(Permission.LIST_MANUAL_WITHDRAWAL_APPROVAL, "listManualWithdrawalApproval.zul", permissionGroup, listgroup);
     } 
    
    
    private void createCell(Long permissionId, String view, PermissionGroup permissionGroup, Listgroup listgroup) {
        Permission permission = loadPermission(permissionId);
        if (permission != null) {
            Listitem item = new Listitem();
            Listcell listCell = new Listcell();
            listCell.setLabel(permission.getPermissionDataByLanguageId(languageId).getAlias());
            listCell.addEventListener("onClick", new RedirectListener(view, permissionId, permissionGroup));
            listCell.setId(permission.getId().toString());
            if (Sessions.getCurrent().getAttribute(WebConstants.VIEW) != null && (Sessions.getCurrent().getAttribute(WebConstants.VIEW).equals(view))) {
                if ((!WebConstants.HOME_ADMIN_ZUL.equals("/" + view))) {
                    listgroup.setOpen(true);
                    listCell.setStyle("background-color: #D8D8D8");
                    listCell.setLabel(">> " + listCell.getLabel());
                }
            }

            listCell.setParent(item);
            item.setParent(lbxPermissions);
        }
    }

    class RedirectListener implements EventListener {

        private String view = null;
        private Long permissionId = null;
        private PermissionGroup permissionGroup;

        public RedirectListener() {
        }

        public RedirectListener(String view, Long permissionId, PermissionGroup permissionGroup) {
            this.view = view;
            this.permissionId = permissionId;
            this.permissionGroup = permissionGroup;
        }

        @Override
        public void onEvent(Event event) throws UiException, InterruptedException {
            AccessControl.saveAction(permissionId, view);
            Executions.sendRedirect(view);
            Sessions.getCurrent().setAttribute(WebConstants.VIEW, view);
            Sessions.getCurrent().setAttribute(WebConstants.PERMISSION_GROUP, permissionGroup.getId());
        }
    }
}
