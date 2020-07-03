package com.alodiga.wallet.admin.web.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.alodiga.wallet.rest.response.EnterpriseResponse;
import com.alodiga.wallet.rest.response.UserResponse;
import com.alodiga.wallet.web.exceptions.GeneralException;



public class ServiceMails {

    public static String SUPPORT_MAIL = "yamealm@gmail.com";
    public static String ALODIGA_MAIL = "turbinesweb@gmail.com";
   
    public static Mail getUserRecoveryPasswordMail(UserResponse user, String newPassword, EnterpriseResponse enterprise) throws GeneralException {

        String hello = "Hola";
        String subject = "Alodiga: Recuperación de clave.";
        String text1 = "Nos complace notificarle que su clave de acceso al portal AlodigaWalletAdminWeb ha sido generada automaticamente.";
        String text2 = "Datos de su cuenta:";
        String text3 = "Recuperacion de clave";
        String distributorName = "Cuenta: ";
        String login = "Usuario(Login): ";
        String pass = "Nueva Clave: ";
        String messageFooter1 = "Este mensaje ha sido enviado desde una cuenta de correo electr&oacute;nico exclusivamente de notificaciones que no admite mensajes. No responda esta comunicaci&oacute;n.";
        String allRights = "Todos los derechos reservados";
        Properties propiedades = new Properties();
        try {
     			propiedades.load(new FileInputStream("C:\\Users\\yamea\\git\\AlodigaWallet\\src\\config.properties"));
     		} catch (FileNotFoundException e) {
     			e.printStackTrace();
     		} catch (IOException e) {
     			e.printStackTrace();
     		}
        String imagen = propiedades.getProperty("prop.logo");
        String style1 = "style='font:13px/0.6em Arial,Helvetica,sans-serif,lighter; color: #666; font-size:13px;'";
        String style2 = "style='background-color: #555555;color:#ffffff;font:12px/1.8em Arial,Helvetica,sans-serif,lighter;font-weight:bold;padding-left:10px'";
        String body = "";
        body = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>";
        body += "<html xmlns='http://www.w3.org/1999/xhtml'>";
        body += "<head>";
        body += "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/><style type='text/css'>.Estilo11 {font:13px/0.6em Arial,Helvetica,sans-serif,lighter; color: #333333; font-size:13px; font-weight:bold;}</style><style type='text/css'>.Estilo12 {font:13px/0.6em Arial,Helvetica,sans-serif,lighter; color: #666; font-size:13px;}</style><style type='text/css'>.EstiloColumn {background-color: #555555;color:#7CBF4F;font:12px/1.8em Arial,Helvetica,sans-serif,lighter;font-weight:bold;padding-left:10px}</style>" + "<div align='center'>"
                + "<table width='756' height='600' border='0'>"
                + "<tr><th width='750' height='595'><p>"
                + "<img src='"+imagen+"' align='left' width='200' height='90' longdesc='Logo Alodiga' />"
                + "</p><p>&nbsp;</p>" + "<p>&nbsp;</p>"
                + "<table  width='730' border='0' >"
                + "<tr><th width='728' height='20' align='right' bgcolor='#0095cd' style='color:#ffffff;font:14px/1.8em Arial,Helvetica,sans-serif,lighter;'>" + text3 + "</th></tr>"
                + "<tr><th width='728' height='5' bgcolor='#232323'></th></tr>"
                + "</table>"
                + "<table width='728' border='0'>"
                + "<tr><th width='728'>"
                + "<p align='left' class='Estilo11'><br/><br/>&iexcl;"
                + hello + " " + user.getFirstName() + " " + user.getLastName() + "&nbsp;" + "!</p>"
                + "<p align='left' class='Estilo11'>"
                + text1 + "<br><br></p>"
                + "</th>"
                + "</tr>"
                + "<tr>"
                + "<th><p align='left' style='font: 16px/1.8em Arial,Helvetica,sans-serif,lighter ; color: #666; font-weight:bold; display: table;  margin: 0; padding:0;' >"
                + text2
                + "</p></th></tr>"
                + "<tr height='3px'><th width='728' bgcolor='#232323'></th></tr>"
                + "<tr>"
                + "<th>"
                + "<div><table width='705' border='0' cellpadding='2' cellspancing='0' style='border:inherit'>"
                + "<tr height='30px'><td " + style2 + " width='305'><div align='left'>"
                + distributorName + "</div></td>"
                + "<td>"
                + "<div align='left' style='font:13px/0.6em Arial,Helvetica,sans-serif,lighter; color: #666; font-size:13px;'>"
                + user.getFirstName() + " " + user.getLastName() + "</div>"
                + "</td>"
                + "</tr>"
                + "<tr height='30px'><td " + style2 + " width='305'><div align='left' >"
                + login + "</div></td>"
                + "<td><div align='left' style='font:13px/0.6em Arial,Helvetica,sans-serif,lighter; color: #666; font-size:13px;'>"
                + user.getLogin() + "</div></td></tr>"
                + "<tr height='30px'><td " + style2 + " width='305'><div align='left' >"
                + pass + "</div></td>"
                + "<td><div align='left' style='font:13px/0.6em Arial,Helvetica,sans-serif,lighter; color: #666; font-size:13px;'>"
                + newPassword + "</div></td></tr></div></table>"
                + "<tr height='3px'><th width='728' bgcolor='#232323'></th></tr>"
                        + "<tr height='40px'>"
                        + "</tr>"
                        + "<tr>"
                        + "<th height='31' bordercolor='#999999'><div align='center'>"
                        + " <p align='center' style='font: 10px/1.8em Arial,Helvetica,sans-serif,lighter ; color: #666; display: table;  margin: 0; padding:0;'>"
                        + messageFooter1
                        + "</p>"
                        + "</div>"
                        + "</th>"
                        + "</tr>"
                        + " </table>"
                        + "<div align='center'>"
                        + "<p align='center' style='font: 10px/1.8em Arial,Helvetica,sans-serif,lighter ; color: #666; display: table;  margin: 0; padding:0;'>&copy; Copyright 2019 - CG TURBINES SRL " + allRights + "<br> "
                        + "</div></th></tr>"
                        + "</table></div></body></html>";
        Mail mail = new Mail();
        mail.setEnterpriseResponse(enterprise);
        mail.setSubject(subject);
        mail.setFrom(enterprise.getInfoEmail());
        mail.setBody(body);
        ArrayList<String> recipients = new ArrayList<String>();
        recipients.add(user.getEmail());
        mail.setTo(recipients);
        //Copia oculta
        recipients = new ArrayList<String>();
        recipients.add(SUPPORT_MAIL);
        mail.setBcc(recipients);
        return mail;
    }
}
