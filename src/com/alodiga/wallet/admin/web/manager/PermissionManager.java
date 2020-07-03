package com.alodiga.wallet.admin.web.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alodiga.wallet.rest.response.PermissionResponse;
import com.alodiga.wallet.rest.response.ProfileListResponse;
import com.alodiga.wallet.rest.response.ProfileResponse;
import com.alodiga.wallet.rest.response.UserResponse;
import com.alodiga.wallet.ws.test.TestServiceRest;
import com.alodiga.wallet.rest.response.PermissionGroupResponse;
import com.alodiga.wallet.admin.web.utils.RestClient;
import com.alodiga.wallet.respuestas.ResponseCode;
import com.alodiga.wallet.rest.request.PermissionRequest;
import com.alodiga.wallet.rest.request.ProfileRequest;
import com.alodiga.wallet.rest.response.PermissionGroupListResponse;
import com.alodiga.wallet.rest.response.PermissionListResponse;

@SuppressWarnings("all")
public class PermissionManager {

    private static PermissionManager instance;
    private List<PermissionGroupResponse> permissionGroups;
    private List<PermissionResponse> permissions;
    private Map<Long, List<PermissionResponse>> permissionByGroup;
    private Map<Long, List<PermissionResponse>> permissionByProfile;

    public static synchronized PermissionManager getInstance() throws Exception {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }

    public static void refresh() throws Exception {
        instance = new PermissionManager();
    }

    private PermissionManager() throws Exception {
    	RestClient rest = new RestClient();
    	PermissionGroupListResponse groupListResponse = (PermissionGroupListResponse) rest.getResponse("getPermissionGroups",null,PermissionGroupListResponse.class);
    	if (groupListResponse.getCodigoRespuesta().equals(ResponseCode.EXITO.getCodigo())) {		
    		permissionGroups = groupListResponse.getPermissionGroupResponses();
    	}
        permissionByGroup = new HashMap<Long, List<PermissionResponse>>();
        permissionByProfile = new HashMap<Long, List<PermissionResponse>>();
        List<PermissionResponse> ps = null;
        for (PermissionGroupResponse permissionGroup : permissionGroups) {
            	PermissionRequest permissionRequest = new PermissionRequest();
            	permissionRequest.setGroupId(permissionGroup.getId());
            	PermissionListResponse listResponse = (PermissionListResponse) rest.getResponse("getPermissionByGroupId",permissionRequest,PermissionListResponse.class);
            	if (listResponse.getCodigoRespuesta().equals(ResponseCode.EXITO.getCodigo())) {		
            		permissionByGroup.put(permissionGroup.getId(), ps);
            	}        
        }
        PermissionListResponse listResponse = (PermissionListResponse) rest.getResponse("getPermissions",null,PermissionListResponse.class);
    	if (listResponse.getCodigoRespuesta().equals(ResponseCode.EXITO.getCodigo())) {		
    		permissions = listResponse.getPermissionResponses();
    	}  
        List<ProfileResponse> profiles = null;
        try {
        	 ProfileListResponse listProfileResponse = (ProfileListResponse) rest.getResponse("getProfiles",null,ProfileListResponse.class);
         	if (listProfileResponse.getCodigoRespuesta().equals(ResponseCode.EXITO.getCodigo())) {		
         		profiles = listProfileResponse.getProfileResponses();
         	} 
            for (ProfileResponse profile : profiles) {
                	 ProfileRequest profileRequest = new ProfileRequest();
                	 profileRequest.setId(profile.getId());
                	 PermissionListResponse listPermissionResponse = (PermissionListResponse) rest.getResponse("getPermissionByProfileId",profileRequest,PermissionListResponse.class);
                 	if (listPermissionResponse.getCodigoRespuesta().equals(ResponseCode.EXITO.getCodigo())) {		
                 		ps = listPermissionResponse.getPermissionResponses();
                 		permissionByProfile.put(profile.getId(), ps);
                 	} 

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<PermissionResponse> getPermissionByGroupId(Long groupId) {
        return permissionByGroup.get(groupId);
    }

    public List<PermissionGroupResponse> getPermissionGroups() {
        return permissionGroups;
    }

    public List<PermissionResponse> getPermissions()  {
        return permissions;
    }

    public PermissionResponse getPermissionById(Long permissionId)  {

        for (PermissionResponse permission : permissions) {
            if (permission.getId().equals(permissionId)) {
                return permission;
            }
        }
        return null;
    }

    public PermissionGroupResponse getPermissionGroupById(Long permissionGroupId)  {
        for (PermissionGroupResponse permissionGroup : permissionGroups) {
            if (permissionGroup.getId().equals(permissionGroupId)) {
                return permissionGroup;
            }
        }
        return null;
    }

    public List<PermissionResponse> getPermissionByProfileId(Long profileId) {
        return permissionByProfile.get(profileId);
    }

    public boolean hasPermisssion(Long profileId, Long permissionId)  {
         for(PermissionResponse permission : permissionByProfile.get(profileId)){
            if(permission.getId().equals(permissionId)){
            return true;
            }
         }
         return false;
    }

}
