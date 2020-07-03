package com.alodiga.wallet.admin.web.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

import com.alodiga.wallet.model.Enterprise;
import com.alodiga.wallet.model.Language;
import com.alodiga.wallet.model.Permission;
import com.alodiga.wallet.respuestas.ResponseCode;
import com.alodiga.wallet.rest.request.EnterpriseRequest;
import com.alodiga.wallet.rest.request.ProfileRequest;
import com.alodiga.wallet.rest.request.UserRequest;
import com.alodiga.wallet.rest.response.EnterpriseResponse;
import com.alodiga.wallet.rest.response.PermissionListResponse;
import com.alodiga.wallet.rest.response.PermissionResponse;
import com.alodiga.wallet.rest.response.UserHasProfileHasEnterpriseResponse;
import com.alodiga.wallet.rest.response.UserResponse;
import com.alodiga.wallet.utils.Encoder;
import com.alodiga.wallet.web.exceptions.GeneralException;



public class AccessControl {

	private static final Logger logger = Logger.getLogger(AccessControl.class);
	private static final ServiceMails ServiceMails = null;

	static Map<String, Object> params = null;
	public static RestClient client = new RestClient();
	LanguageDefinition definition;

	public static boolean savePermissionUser(List<UserHasProfileHasEnterpriseResponse> userHasProfiles, Long enterpriseId) {
        HashMap<String, List<String>> permissionsMap = new HashMap<String, List<String>>();

        try {
            for (UserHasProfileHasEnterpriseResponse userHasProfileHasEnterprise : userHasProfiles) {
                if (userHasProfileHasEnterprise.getEnterpriseId().equals(enterpriseId) && userHasProfileHasEnterprise.getEndingDate() == null) {
                	ProfileRequest profileRequest = new ProfileRequest();
                	profileRequest.setId(userHasProfileHasEnterprise.getProfileId());
                	PermissionListResponse response = (PermissionListResponse) client.getResponse("getPermissionByProfileId", profileRequest, PermissionListResponse.class);
                	if (response.getCodigoRespuesta().equals(ResponseCode.EXITO.getCodigo())) {
                		
	                    for (PermissionResponse permission : response.getPermissionResponses()) {
	                        if (permission.isEnabled()) {
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

	public static boolean validateUser(String login, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		boolean valid = false;

		UserRequest userRequest = new UserRequest();
		userRequest.setLogin(login);
		userRequest.setPassword(Encoder.MD5(password));
		UserResponse userResponse = (UserResponse) client.getResponse("validateUser", userRequest, UserResponse.class);
		if (userResponse.getCodigoRespuesta().equals(ResponseCode.EXITO.getCodigo())) {

			List<UserHasProfileHasEnterpriseResponse> userHasProfileHasEnterprises = userResponse.getUserHasProfileHasEnterpriseResponse();
			if (userHasProfileHasEnterprises != null && userHasProfileHasEnterprises.size() > 0 && userResponse.isEnabled()) {
				AccessControl.savePermissionUser(userHasProfileHasEnterprises, Enterprise.ALODIGA);
				Sessions.getCurrent().setAttribute(WebConstants.SESSION_USER, userResponse);
				saveAction(null, Permission.LOG_IN);
				valid = true;
			}
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

    public static UserResponse loadCurrentUser() {
        return (UserResponse) Sessions.getCurrent().getAttribute(WebConstants.SESSION_USER);
    }

    public static Long getLanguage() {

        Locale locale = Locales.getCurrent();
        if (locale.getLanguage().equals("es")) {
            return Language.SPANISH;
        } else {
            return Language.ENGLISH;
        }
    }



    public static void generateNewPassword(UserRequest user, boolean isForward) throws GeneralException {
        try {
            if (user != null) {

                String oldPassword = user.getPassword();
                String newPassword = GeneralUtils.getRamdomPassword(WebConstants.MAX_PASSWORD_DIGITS);
                user.setPassword(GeneralUtils.encryptMD5(newPassword));
        		UserResponse userResponse = (UserResponse) client.getResponse("updateUserPassword", user, UserResponse.class);
        		if (userResponse.getCodigoRespuesta().equals(ResponseCode.EXITO.getCodigo())) {
        			EnterpriseRequest enterpriseRequest = new EnterpriseRequest();
        			enterpriseRequest.setId(Enterprise.ALODIGA);
        			EnterpriseResponse enterpriseResponse = (EnterpriseResponse) client.getResponse("getEnterpriseById", enterpriseRequest, EnterpriseResponse.class);
        			if (enterpriseResponse.getCodigoRespuesta().equals(ResponseCode.EXITO.getCodigo())) {
        				try {
        					sendUserRecoveryPasswordMail(userResponse, newPassword, enterpriseResponse);
        				} catch (Exception ex) {
        					/*Si ocurre un error al enviar el correo se guarda el
                            usuario con el password que tenia previamente.*/
        					user.setPassword(oldPassword);
        					userResponse = (UserResponse) client.getResponse("updateUserPassword", user, UserResponse.class);
        					throw new GeneralException(ex.getMessage());
        				}
        				
        			}
        			
        		}
            
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new GeneralException(ex.getMessage());
        }
    }

    private static void sendUserRecoveryPasswordMail(UserResponse user, String newPassword, EnterpriseResponse enterprise) throws GeneralException {
        try {
               Mail mail = ServiceMails.getUserRecoveryPasswordMail(user, newPassword, enterprise);
               SendMail SendMail = new SendMail();
               try {
                   SendMail.sendMail(mail);
               } catch (Exception ex) {
                   throw new GeneralException(logger, ex.getMessage(), ex);
               }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new GeneralException(ex.getMessage());
        }
    }

      public static void saveAction(Long permissionId, String info) {
//        try {
//            AuditAction action = new AuditAction();
//            String host = Sessions.getCurrent().getRemoteHost();
//            Sessions.getCurrent().getDeviceType();
//            Sessions.getCurrent().getRemoteAddr();
//            action.setDate(new Timestamp(new Date().getTime()));
//            action.setHost(host);
//            action.setUser(loadCurrentUser());
//            Permission permission = PermissionManager.getInstance().getPermissionById(permissionId);
//            action.setPermission(permission);
//            action.setInfo(info);
//            auditoryEJB = (AuditoryEJB) EJBServiceLocator.getInstance().get(EjbConstants.AUDITORY_EJB);
//            auditoryEJB.saveAuditAction(action);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
//
//
//    public static boolean getNeedUpdate() {
//        return needUpdate;
//    }
//
//
//         public static List<Audit> getCurrentAudit() {
//        List<Audit> audits = new ArrayList<Audit>();
//        Audit audit = new Audit();
//        try {
//        	EJBRequest ejbRequest = new EJBRequest();
//        	ejbRequest.setParam(1);
//            Event ev = auditoryEJB.loadEvent(ejbRequest);
//            audit.setEvent(ev);
//            //            audit.setNewValues("new values");
//            //            audit.setOriginalValues("");
//            audit.setRemoteIp(Executions.getCurrent().getRemoteAddr());
//            	if (loadCurrentUser() != null) {
//                audit.setUser(loadCurrentUser());
//                audit.setResponsibleType("User");
//            } 
//            audit.setTableName("");
//            audit.setCreationDate(new Timestamp(new Date().getDate()));
//            audits.add(audit);
//            return audits;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return audits;
//    }
//
//    public static Customer loadCurrentCustomer() throws RegisterNotFoundException, GeneralException, Exception {
//        return (Customer) Sessions.getCurrent().getAttribute(WebConstants.SESSION_CUSTOMER);
//    }
}
