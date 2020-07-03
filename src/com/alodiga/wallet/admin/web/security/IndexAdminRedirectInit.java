package com.alodiga.wallet.admin.web.security;

import java.util.Map;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

import com.alodiga.wallet.admin.web.utils.AccessControl;

public class IndexAdminRedirectInit implements Initiator {

    public void doInit(Page page, Map map) throws Exception {
        if (AccessControl.loadCurrentUser() != null) {
            Executions.sendRedirect("home-admin.zul");
        }
    }

    public void doAfterCompose(Page page) throws Exception {
    }

    public boolean doCatch(Throwable thrwbl) throws Exception {
        return true;

    }

    public void doFinally() throws Exception {
    }
}
