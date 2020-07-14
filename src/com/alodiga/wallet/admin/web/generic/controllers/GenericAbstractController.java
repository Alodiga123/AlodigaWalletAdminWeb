package com.alodiga.wallet.admin.web.generic.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Window;

import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.common.genericEJB.EJBRequest;

public class GenericAbstractController extends GenericForwardComposer implements GenericSPController {

    public Integer eventType = null;
    public boolean permissionEdit;
    public boolean permissionAdd;
    public boolean permissionRead;
    public boolean permissionResend;
    public boolean permissionChangeStatus;
    public boolean permissionDelete;
    public Long languageId = 1L;
    public Div divInfo;
    public EJBRequest request = new EJBRequest();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        eventType = (Sessions.getCurrent().getAttribute("eventType") != null) ? (Integer) Sessions.getCurrent().getAttribute("eventType") : -1;
        super.doAfterCompose(comp);
    }

    public void initialize() {
        languageId = AccessControl.getLanguage();
    }

    public void checkPermissions(){

    }
    
//    public void loadPermission(AbstractSPEntity clazz) throws Exception {
//        try {
//            permissionRead = AccessControl.hasPermission(clazz.getTableName(), PermissionConstants.EVENT_READ);
//            permissionEdit = AccessControl.hasPermission(clazz.getTableName(), PermissionConstants.EVENT_EDIT);
//            permissionAdd = AccessControl.hasPermission(clazz.getTableName(), PermissionConstants.EVENT_ADD);
//        } catch (TableNotFoundException ex) {
//            //e.printStackTrace();
//            throw new Exception("error.general");
//        }
//    }

    public void showMessage(String message, boolean isError, Exception exception) {
        divInfo.getChildren().clear();
        divInfo.setVisible(true);
        Hlayout hlayout = new Hlayout();
        Separator separator = new Separator();
        separator.setOrient("horizontal");
        separator.setParent(hlayout);
        Image icon = new Image();
//        icon.setSrc(isError ? "/images/icon-cancel.png" : "/images/icon-enable.png");
        icon.setParent(hlayout);
        separator = new Separator();
        separator.setOrient("horizontal");
        separator.setParent(hlayout);
        Label lblMessage = new Label();
        lblMessage.setStyle("font-size:16px; font-weight: bold;font-style: italic;color: #424242;");
        lblMessage.setValue(Labels.getLabel(message) == null || Labels.getLabel(message).isEmpty() ? message : Labels.getLabel(message));
        lblMessage.setParent(hlayout);
        hlayout.setParent(divInfo);
        divInfo.setStyle(isError ? "background:#F4AFAF; border-bottom-right-radius:10px;border-bottom-left-radius:10px;border-top-left-radius:10px;border-top-right-radius:10px;" : "background:#B6E59E; border-bottom-right-radius:10px;border-bottom-left-radius:10px;border-top-left-radius:10px;border-top-right-radius:10px;");
        divInfo.setHeight("25px");
        if (exception != null) {
            exception.printStackTrace();
        }
    }

    public void clearMessage() {
        divInfo.setVisible(false);
        divInfo.getChildren().clear();
    }

    public void showError(Exception exParam) {
        try {
            exParam.printStackTrace();
            String toPrint = exParam != null ? joinStackTrace(exParam.fillInStackTrace()): "";
            HashMap map = new HashMap<String, String>();
            map.put("message", toPrint);
            Window window = (Window) Executions.createComponents("error.zul", null, map);
            window.doModal();
        } catch (Exception ex) {
            Logger.getLogger(GenericAbstractController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showError(String message) {
        try {
            HashMap map = new HashMap<String, String>();
            map.put("message", message);
            Window window = (Window) Executions.createComponents("error.zul", null, map);
            window.doModal();
        } catch (Exception ex) {
            Logger.getLogger(GenericAbstractController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static String joinStackTrace(Throwable e) {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            joinStackTrace(e, writer);
            return writer.toString();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e1) {
                    // ignore
                }
            }
        }
    }

    public static void joinStackTrace(Throwable e, StringWriter writer) {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(writer);

            while (e != null) {

                printer.println(e);
                StackTraceElement[] trace = e.getStackTrace();
                for (int i = 0; i < trace.length; i++) {
                    printer.println("\tat " + trace[i]);
                }

                e = e.getCause();
                if (e != null) {
                    printer.println("Caused by:\r\n");
                }
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }
}
