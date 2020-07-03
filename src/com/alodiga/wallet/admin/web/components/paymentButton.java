package com.alodiga.wallet.admin.web.components;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;

import com.alodiga.wallet.admin.web.utils.WebConstants;

public class paymentButton extends Button{

    public paymentButton(String view, Object obj,Long permissionId){
        this.setLabel(Labels.getLabel("/images/icon-show.png"));
        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_VIEW, view, obj,permissionId));
        
    }

    public paymentButton(String view, Object obj,String images, Long permissionId){
//        this.setImage("/images/icon-show.png");
        this.addEventListener("onClick", new ShowAdminViewListener(WebConstants.EVENT_VIEW, view, obj,permissionId));

    }
}
