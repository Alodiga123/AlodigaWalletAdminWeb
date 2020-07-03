package com.alodiga.wallet.admin.web.components;

import org.zkoss.zul.Button;

import com.alodiga.wallet.admin.web.utils.WebConstants;

public class AddButton extends Button{
    public AddButton(){
        this.setImage("/images/icon-add.png");
    }
    public AddButton(String view, Object obj, Long permissionId){
        this.setImage("/images/icon-add.png");
        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_ADD, view, obj,permissionId));
        
    }
}
