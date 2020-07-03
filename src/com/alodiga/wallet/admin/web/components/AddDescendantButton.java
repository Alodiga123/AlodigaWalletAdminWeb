package com.alodiga.wallet.admin.web.components;

import org.zkoss.zul.Button;

import com.alodiga.wallet.admin.web.utils.WebConstants;

public class AddDescendantButton extends Button{
    public AddDescendantButton(){
        this.setImage("/images/icon-add.png");
    }
    public AddDescendantButton(String view, Object obj, Long permissionId){
        this.setImage("/images/icon-add.png");
        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_ADD_DESCENDANT, view, obj,permissionId));
        
    }
}
