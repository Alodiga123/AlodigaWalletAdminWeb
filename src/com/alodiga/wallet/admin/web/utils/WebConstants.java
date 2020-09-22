package com.alodiga.wallet.admin.web.utils;

import java.io.File;

public class WebConstants {

    public static final int EVENT_ADD = 1;
    public static final int EVENT_EDIT = 2;
    public static final int EVENT_DELETE = 3;
    public static final int EVENT_VIEW = 4;
    public static final int EVENT_ADD_DESCENDANT = 5;
    public static final String EVENTYPE = "eventType";
    public static final String SESSION_ACCOUNT = "sp_account";
    public static final String SESSION_DISTRIBUTOR = "sp_loguedDistributor";
    public static final String SESSION_FULLSITE = "sp_fullSite";
    public static final String SESSION_USER = "sp_user";
    public static final String SESSION_REPORT = "sp_report";
    public static final String SESSION_CUSTOMER = "sp_customer";
    public static final String PHONE_NUMBER_CUSTOMER = "sp_phoneNumber";
    public static final String SESSION_PERMISSION = "sp_permission";
    public static final String SESSION_TRANSACTION = "sp_transaction";
    public static final String NEW_CUSTOMER_SHOP = "sp_new_customer_shop";
    public static final String PREFIX_SOLICITUDE_TYPE = "sp_common.solicitude.type.";
    public static final String PREFIX_PROFILE = "sp.common.profile.";
    public static final String PREFIX_SOLICITUDE_STATUS = "sp.common.solicitude.status.";
    public static final String PREFIX_ACCOUNT = "acc_";
    public static final String PREFIX_DISTRIBUTOR = "dis_";
    public static final String PREFIX_ALL_STORE = "ALL_C";
    public static final String PREFIX_CUSTOMER = "cos_";
    public static final String PREFIX_COMMISSION_CHANGE = "com_";
    public static final String PREFIX_PRODUCT = "pro_";
    public static final String PREFIX_T_STATUS = "tSta_";
    public static final String PREFIX_PROVIDER = "prov__";
    public static final String CONNECTIONPOOL = "jdbc/AlodigaWallet";
    public static final String COLUMN_LABEL = "columnLabel";
    public static final String DATA = "data";
    public static final String ID_PARAM_REPORT_OPEN = "/*<accountSql>*/";
    public static final String ID_PARAM_REPORT_CLOSED = "/*</accountSql>*/";
    public static final String ID_ELEMENT = "id";
    public static final int INTEGER = 1;
    public static final int FLOAT = 2;
    public static final int STRING = 3;
    public static final int DATE = 4;
    public static final int BOOLEAN = 10;
    public static final int DISTRIBUTOR = 6;
    public static final int CUSTOMER = 7;
    public static final int PHONENUMBER = 8;
    public static final int PRODUCT = 9;
    public static final int T_STATUS = 11;
    public static final int STORE = 12;
    public static final int CHECK_WALLET = 13;
    public static final int TOP_UP_COMMISSION_CHANGE = 14;
    public static final int PROMOTION_TYPE = 15;
    public static final String ATTACHMENT_FILE_EXCEL = File.separator + "var" + File.separator + "excelFile" + File.separator;
    public static final String ATTACHMENT_FILE_PACH = File.separator + "var" + File.separator + "ticketAttachment" + File.separator;
    //public static final String BANNER_FILE_PACH = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/")  + "images" + File.separator + "banners" + File.separator;
    public static final String BANNER_FILE_PACH = File.separator + "var" + File.separator + "banners" + File.separator;
    public static final String SESSION_ENTERPRISE = "enterprise";
    public static final String PREFERENCES_MAP = "preferenceMap";
    public static final String ANY = "ninguno";
    public static final String ALL = "todos";
    /*public static final Integer NOTIFICATION_LOGIN = 1;
    public static final Integer NOTIFICATION_PASSDWORD = 2;
    public static final Integer NOTIFICATION_SERIAL = 3;
    public static final Integer NOTIFICATION_ACCESS_NUMBER = 4;
    public static final Integer NOTIFICATION_TOTAL_AMOUNT = 5;
    public static final Integer NOTIFICATION_MAIL_ADDRESS = 6;
    public static final Integer NOTIFICATION_NAME_CUSTOMER = 7;
    public static final Integer NOTIFICATION_CUSTOMER_ENTERPRISE = 8;
    public static final Integer NOTIFICATION_CURRENCY = 9;
    public static final Integer NOTIFICATION_DNI = 10;
    public static final Integer NOTIFICATION_SECRET_PIN = 11;*/

    public static final String MENU_SECURITY = "menu.security";
    public static final String MENUITEM_SECURITY_PROFILE = "menu.security.profile";
    public static final String MENUITEM_SECURITY_USER = "menu.security.user";
    public static final String MENUITEM_SECURITY_AUDITORY = "menu.security.audit";
    public static final String MENUITEM_SECURITY_PREFERENCE = "menu.security.preference";
    public static final String MENU_INVENTORY = "menu.inventory";
    public static final String MENUITEM_INVENTORY_PRODUCT = "menu.inventory.product";
    public static final String MENUITEM_INVENTORY_MOBILE_OPERATOR = "menu.inventory.mobileOperator";
    public static final String MENUITEM_INVENTORY_PROVIDER = "menu.inventory.provider";
    //public static final String MENUITEM_INVENTORY_COMMISION = "menu.inventory.file";
    public static final String MENUITEM_INVENTORY_TOP_UP_COMMISSION = "menu.inventory.topUp.commission";
    public static final String MENU_CONTENT = "menu.content";
    public static final String MENUITEM_CONTENT_REPORT = "menu.content.report";
    public static final String MENUITEM_CONTENT_PROMOTION = "menu.content.promotion";
    public static final String MENUITEM_CONTENT_PROMOTION_NOTIFICATION = "menu.content.promotion.notification";
    public static final String MENUITEM_CONTENT_SOLICITUDE = "menu.content.solicitude";
    public static final String MENUITEM_CONTENT_NOTIFICATION = "menu.content.notification";
    public static final String MENUITEM_CONTENT_TICKET = "menu.content.ticket";
    public static final String MENUITEM_CONTENT_BANNER = "menu.content.banner";
    public static final String MENU_MANAGEMENT = "menu.management";
    public static final String MENUITEM_MANAGEMENT_CUSTOMER = "menu.management.customer";
    public static final String MENUITEM_MANAGEMENT_ACCOUNT = "menu.management.account";
    public static final String MENUITEM_MANAGEMENT_ACCOUNT_TREE = "menu.management.account.tree";
    public static final String MENUITEM_MANAGEMENT_TRANSACTION = "menu.management.transaction";
    public static final String MENUITEM_MANAGEMENT_CALLS = "menu.management.calls";
    public static final String MENUITEM_MANAGEMENT_AUTOMATIC_SERVICE = "menu.management.automatic.service";
    public static final String MENUITEM_MANAGEMENT_COMMISSIONS = "menu.management.commission";
    public static final String MENUITEM_MANAGEMENT_REPORT = "menu.management.report";
    public static final String MENUITEM_MANAGEMENT_BALANCE_ADJUSTMENT = "menu.security.balanceAdjustment";
    public static final String MENUITEM_MANAGEMENT_SMS_TEST = "menu.security.test.sms";
    public static final String MENUITEM_MANAGEMENT_MAIL_TEST = "menu.security.test.mail";
    public static final String MENUITEM_MANAGEMENT_PROMOTION_AUTOMATIC_SERVICE = "menu.management.promotion.automatic.service";
    public static final String MENUITEM_MANAGEMENT_ALOPOINS_SHOW = "menu.management.alopoints";
    public static final String MENUITEM_MANAGEMENT_COUNTRY = "menu.management.country";
    public static final String MENU_REPORT = "menu.report";
    public static final String MENUITEM_REPORT_SHOW = "menu.module.reports";

    public static final String VIEW = "view";
    public static final String PERMISSION_GROUP = "permission_group";
    public static final String HOME_ADMIN_ZUL = "home-admin.zul";
    
    public static final int USA_CODE = 1;
    public static final int MAX_LOGIN_DIGITS = 8;
    public static final int MAX_PASSWORD_DIGITS = 4;
    public static String DEV_REPORT = "dev-reports@interaxmedia.com";
    public static final String MAIN_APPLICANT = "Solicitante Principal";
    
    //CRUD Employee
    public static final Boolean IND_NATURAL_PERSON = true;
    
}
