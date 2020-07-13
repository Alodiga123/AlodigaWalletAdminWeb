package com.alodiga.wallet.admin.web.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

import com.alodiga.wallet.common.ejb.AccessControlEJB;
import com.alodiga.wallet.common.ejb.AuditoryEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.ejb.UtilsEJB;
import com.alodiga.wallet.common.exception.DisabledUserException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.InvalidPasswordException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.exception.RegisterNotFoundException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.manager.PermissionManager;
import com.alodiga.wallet.common.model.Audit;
import com.alodiga.wallet.common.model.AuditAction;
import com.alodiga.wallet.common.model.Enterprise;
import com.alodiga.wallet.common.model.Event;
import com.alodiga.wallet.common.model.Language;
import com.alodiga.wallet.common.model.Permission;
import com.alodiga.wallet.common.model.PermissionHasProfile;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.model.UserHasProfileHasEnterprise;
import com.alodiga.wallet.common.utils.Constants;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.common.utils.Encoder;
import com.alodiga.wallet.common.utils.SendMailTherad;





public class AccessControl {

	 private static AccessControlEJB accessEjb = null;
	    private static AuditoryEJB auditoryEJB = null;
	    private static boolean needUpdate = true;
	    static Map<String, Object> params = null;
	    static EJBRequest request = null;
	    private static UserEJB userEJB = null;
	    private static UtilsEJB utilsEJB;
		LanguageDefinition definition;

	    public static boolean savePermissionUser(List<UserHasProfileHasEnterprise> userHasProfiles, Long enterpriseId) {
	        HashMap<String, List<String>> permissionsMap = new HashMap<String, List<String>>();

	        try {
	            for (UserHasProfileHasEnterprise userHasProfileHasEnterprise : userHasProfiles) {
	                if (userHasProfileHasEnterprise.getEnterprise().getId().equals(enterpriseId) && userHasProfileHasEnterprise.getEndingDate() == null) {
	                    List<PermissionHasProfile> permissionHasProfiles = userHasProfileHasEnterprise.getProfile().getPermissionHasProfiles();
	                    for (PermissionHasProfile permissionHasProfile : permissionHasProfiles) {
	                        Permission permission = permissionHasProfile.getPermission();
	                        if (permission.getEnabled()) {
	                            List<String> permissionAction = new ArrayList<String>();
	                            if (permissionsMap.containsKey(permission.getEntity())) {
	                                permissionAction = permissionsMap.get(permission.getEntity());
	                                permissionAction.add(permission.getAction());
	                            } else {
	                                permissionAction.add(permission.getAction());
	                            }
	                            permissionsMap.put(permission.getEntity(), permissionAction);
	                        }
	                    }
	                }
	            }
	            if (permissionsMap != null && !permissionsMap.isEmpty()) {
	                Sessions.getCurrent().setAttribute(WebConstants.SESSION_PERMISSION, permissionsMap);
	                return true;
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            return false;
	        }
	        return false;
	    }

	    public static boolean hasPermission(String entity, String action) {
	        HashMap<String, List<String>> permissionsMap = (HashMap<String, List<String>>) Sessions.getCurrent().getAttribute(WebConstants.SESSION_PERMISSION);
	        if (permissionsMap != null && permissionsMap.containsKey(entity)) {
	            List<String> permissions = permissionsMap.get(entity);
	            if (permissions.contains(action)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    public static boolean validateUser(String login, String password) throws InvalidPasswordException, RegisterNotFoundException, GeneralException, NoSuchAlgorithmException, UnsupportedEncodingException, NullParameterException, DisabledUserException {
	        boolean valid = false;
	        User user = null;
	        accessEjb = (AccessControlEJB) EJBServiceLocator.getInstance().get(EjbConstants.ACCESS_CONTROL_EJB);
		    utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
	        //     request.setAuditData(getCurrentAudit());
	        user = accessEjb.validateUser(login, Encoder.MD5(password));
	        List<UserHasProfileHasEnterprise> userHasProfileHasEnterprises = user.getUserHasProfileHasEnterprises();
	        if (userHasProfileHasEnterprises != null && userHasProfileHasEnterprises.size() > 0 && user.getEnabled()) {
	            AccessControl.savePermissionUser(userHasProfileHasEnterprises, Enterprise.ALODIGA);
	            Sessions.getCurrent().setAttribute(WebConstants.SESSION_USER, user);
	            saveAction(null, Permission.LOG_IN);
	            valid = true;
	        }
	        return valid;
	    }

	    public static void logout() {
	        try {
	            saveAction(null, Permission.LOG_OUT);
	            Sessions.getCurrent().removeAttribute(WebConstants.SESSION_ACCOUNT);
	            Sessions.getCurrent().removeAttribute(WebConstants.SESSION_USER);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }

	    }

	    public static User loadCurrentUser() throws RegisterNotFoundException, GeneralException, Exception {
	        return (User) Sessions.getCurrent().getAttribute(WebConstants.SESSION_USER);
	    }

	    public static Long getLanguage() {

	        Locale locale = Locales.getCurrent();
	        if (locale.getLanguage().equals("es")) {
	            return Language.SPANISH;
	        } else {
	            return Language.ENGLISH;
	        }
	    }

	    public static String getRandomLogin(String login) throws GeneralException {
	        try {

	            if (validateExistingDistributor(login)) {
	                login = GeneralUtils.getRamdomNumber(Constants.MAX_LOGIN_DIGITS);
	                getRandomLogin(login);
	            }
	            return login;

	        } catch (GeneralException ex) {
	            throw new GeneralException(ex.getMessage());
	        }
	    }

	    public static boolean validateExistingDistributor(String login) throws GeneralException {
	        userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
//	        try {
//	            userEJB.loadAccountByLogin(login);
//	        } catch (RegisterNotFoundException ex) {
//	            return false;
//	        } catch (Exception ex) {
//	            throw new GeneralException(ex.getMessage());
//	        }
	        return true;

	    }

	    public static void generateNewPassword(User user, boolean isForward) throws GeneralException {

	        try {
	            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);

	            request = new EJBRequest();
	            if (user != null) {

	                String oldPassword = user.getPassword();
	                userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
	                String newPassword = GeneralUtils.getRamdomPassword(Constants.MAX_PASSWORD_DIGITS);
	                user.setPassword(GeneralUtils.encryptMD5(newPassword));
	                request.setParam(user);
	                userEJB.saveUser(request);
	                request = new EJBRequest();
	                request.setParam(Enterprise.ALODIGA);
	                Enterprise enterprise = utilsEJB.loadEnterprise(request);
	                try {
	                    sendUserRecoveryPasswordMail(user, newPassword, enterprise);
	                } catch (Exception ex) {
	                    /*Si ocurre un error al enviar el correo se guarda el
	                    usuario con el password que tenia previamente.*/
	                    user.setPassword(oldPassword);
	                    request.setParam(user);
	                    userEJB.saveUser(request);
	                    throw new GeneralException(ex.getMessage());
	                }
	            
	            }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            throw new GeneralException(ex.getMessage());
	        }
	    }

	    private static void sendUserRecoveryPasswordMail(User user, String newPassword, Enterprise enterprise) throws GeneralException {
	        try {
	            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
	            SendMailTherad sendMailTherad = new SendMailTherad("ES",user, newPassword, enterprise,Constants.SEND_TYPE_EMAIL_RECOVER_PASSWORD);
	            sendMailTherad.run();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            throw new GeneralException(ex.getMessage());
	        }
	    }

	      public static void saveAction(Long permissionId, String info) {
//	        try {
//	            AuditAction action = new AuditAction();
//	            String host = Sessions.getCurrent().getRemoteHost();
//	            Sessions.getCurrent().getDeviceType();
//	            Sessions.getCurrent().getRemoteAddr();
//	            action.setDate(new Timestamp(new Date().getTime()));
//	            action.setHost(host);
//	            action.setUser(loadCurrentUser());
//	            Permission permission = PermissionManager.getInstance().getPermissionById(permissionId);
//	            action.setPermission(permission);
//	            action.setInfo(info);
//	            auditoryEJB = (AuditoryEJB) EJBServiceLocator.getInstance().get(EjbConstants.AUDITORY_EJB);
//	            auditoryEJB.saveAuditAction(action);
//	        } catch (Exception ex) {
//	            ex.printStackTrace();
//	        }
	    }


	    public static boolean getNeedUpdate() {
	        return needUpdate;
	    }


	         public static List<Audit> getCurrentAudit() {
	        List<Audit> audits = new ArrayList<Audit>();
	        Audit audit = new Audit();
	        try {
	        	EJBRequest ejbRequest = new EJBRequest();
	        	ejbRequest.setParam(1);
	            Event ev = auditoryEJB.loadEvent(ejbRequest);
	            audit.setEvent(ev);
	            //            audit.setNewValues("new values");
	            //            audit.setOriginalValues("");
	            audit.setRemoteIp(Executions.getCurrent().getRemoteAddr());
	            	if (loadCurrentUser() != null) {
	                audit.setUser(loadCurrentUser());
	                audit.setResponsibleType("User");
	            } 
	            audit.setTableName("");
	            audit.setCreationDate(new Timestamp(new Date().getDate()));
	            audits.add(audit);
	            return audits;
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return audits;
	    }
}
