package com.alodiga.wallet.admin.web.components;

import org.zkoss.zul.Button;

import com.alodiga.wallet.admin.web.utils.WebConstants;

public class ViewButton extends Button{
    public ViewButton(){
        this.setImage("/images/icon-invoice.png");
    }
    public ViewButton(String view, Object obj,Long permissionId){
        this.setImage("/images/icon-invoice.png");
        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_VIEW, view, obj,permissionId));
        
    }
}
