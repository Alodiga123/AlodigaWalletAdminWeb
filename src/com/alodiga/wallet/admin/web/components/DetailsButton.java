package com.alodiga.wallet.admin.web.components;

import org.zkoss.zul.Button;

import com.alodiga.wallet.admin.web.utils.WebConstants;

public class DetailsButton extends Button{

    public DetailsButton(String view, Object obj,Long permissionId){
        this.setImage("/images/icon-eye.png");
        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_VIEW, view, obj,permissionId));
        
    }

    public DetailsButton(String view, Object obj,String images, Long permissionId){
       this.setImage("/images/icon-eye.png");
        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_VIEW, view, obj,permissionId));

    }
}
