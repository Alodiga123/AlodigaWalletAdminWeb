package com.alodiga.wallet.admin.web.controllers;

import java.util.Locale;

import org.zkoss.util.Locales;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;

import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;

public class MenuOptionsController extends GenericForwardComposer {

    private static final long serialVersionUID = -9145887024839938515L;
    Hlayout hltImages;
    Button btnLogout;
    Button btnSpanish;
    Button btnEnglish;
    Button btnGoToMobileSite;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    public void initialize() {
        try {
            if (Locales.getCurrent().getLanguage().equals("en")) {
                if (btnSpanish != null && btnEnglish != null) {
                    btnSpanish.setVisible(true);
                    btnEnglish.setVisible(false);
                }
            } else {
                if (btnSpanish != null && btnEnglish != null) {
                    btnSpanish.setVisible(false);
                    btnEnglish.setVisible(true);
                }
            }
//            if (AccessControl.loadCurrentAccount() != null || AccessControl.loadCurrentUser() != null) {
//                btnLogout.setVisible(true);
//            }

            if (Sessions.getCurrent().getAttribute(WebConstants.SESSION_FULLSITE) != null) {
                btnGoToMobileSite.setVisible(true);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onClick$btnLogout() {
        AccessControl.logout();
        Executions.sendRedirect("./index.zul");
    }

    public void onClick$btnGoToMobileSite() {
        Sessions.getCurrent().removeAttribute(WebConstants.SESSION_FULLSITE);
        Executions.sendRedirect("./index.zul");
    }

    public void onClick$btnEnglish() {

        if (!Locales.getCurrent().getLanguage().equals("en")) {
            Locale _newLocal = new Locale("en", "US", "en");
            session.setAttribute(Attributes.PREFERRED_LOCALE, _newLocal);
            Executions.sendRedirect(null);
        }
    }

    public void onClick$btnSpanish() {
        btnSpanish.setVisible(false);
        btnEnglish.setVisible(true);
        if (!Locales.getCurrent().getLanguage().equals("es")) {
            Locale _newLocal = new Locale("es", "ES", "es");
            session.setAttribute(Attributes.PREFERRED_LOCALE, _newLocal);
            Executions.sendRedirect(null);
        }
    }

    public void onClick$btnEnglish1() {
        btnEnglish.setVisible(false);
        btnSpanish.setVisible(true);
        if (!Locales.getCurrent().getLanguage().equals("en")) {
            Locale _newLocal = new Locale("en", "US", "en");
            session.setAttribute(Attributes.PREFERRED_LOCALE, _newLocal);
            Executions.sendRedirect(null);
        }
    }

    public void onClick$btnSpanish1() {
        if (!Locales.getCurrent().getLanguage().equals("es")) {
            Locale _newLocal = new Locale("es", "ES", "es");
            session.setAttribute(Attributes.PREFERRED_LOCALE, _newLocal);
            Executions.sendRedirect(null);
        }
    }

    public void onClick$btnAccessNumbers() {
        Executions.getCurrent().sendRedirect("/docs/access-numbers.pdf", "_blank");
    }

    public void onClick$btnRates() {
        Executions.getCurrent().sendRedirect("/docs/new_rates.pdf", "_blank");
    }

    public void onClick$btnTerms() {
        Executions.getCurrent().sendRedirect("/docs/terms.pdf", "_blank");
    }
}
