package com.alodiga.wallet.admin.web.controllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractController;
import com.alodiga.wallet.admin.web.generic.controllers.GenericSPController;

public class ErrorController extends GenericAbstractController implements GenericSPController {

    private static final long serialVersionUID = 1L;
    Window winError;
    String message = "";
    private Label lblError;

    public void onClick$btnClose() {
        winError.detach();
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        if (arg.containsKey("message")) {
            message = (String) arg.get("message");
            lblError.setMultiline(true);
            lblError.setValue(message);

        }
    }

    @Override
    public void initialize() {
        super.initialize();

    }

//    @Override
//    public void loadPermission(AbstractSPEntity clazz) throws GeneralException {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
}
