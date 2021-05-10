package com.alodiga.wallet.admin.web.controllers;
import com.alodiga.wallet.common.ejb.ReportEJB;
import com.alodiga.wallet.common.ejb.UserEJB;
import com.alodiga.wallet.common.exception.EmptyListException;
import com.alodiga.wallet.common.exception.GeneralException;
import com.alodiga.wallet.common.exception.NullParameterException;
import com.alodiga.wallet.common.genericEJB.EJBRequest;
import com.alodiga.wallet.common.model.Report;
import com.alodiga.wallet.common.model.ReportParameter;
import com.alodiga.wallet.common.model.ReportType;
import com.alodiga.wallet.common.model.User;
import com.alodiga.wallet.common.utils.EJBServiceLocator;
import com.alodiga.wallet.common.utils.EjbConstants;
import com.alodiga.wallet.admin.web.generic.controllers.GenericAbstractController;
import com.alodiga.wallet.admin.web.utils.AccessControl;
import com.alodiga.wallet.admin.web.utils.WebConstants;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Group;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

public class ManagementReportController extends GenericAbstractController {

    private static final long serialVersionUID = -9145887024839938515L;
    UserEJB userEjb = null;
    private Listbox lbxReport;
    private ReportEJB reportEJB = null;
    protected Grid gridParams;
    protected Rows filasParams;
    protected Row rowInsumo;
    protected List<ReportParameter> reportParameters = null;
    private User currentUser;
    private Rows rowsReports;

    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }


    @Override
    public void initialize() {
       try {
            reportEJB = (ReportEJB) EJBServiceLocator.getInstance().get(EjbConstants.REPORT_EJB);
            currentUser = AccessControl.loadCurrentUser();
            User currentUser = AccessControl.loadCurrentUser();
//            lblUser.setValue(currentUser.getFirstName() + " " + currentUser.getLastName());
            loadData();
        } catch (Exception ex) {
            this.showMessage("msj.error.general", true, ex);
        }
    }

    public void clearFields() {
        gridParams.getRows().getChildren().clear();
        lbxReport.getChildren().clear();
    }

    public void loadData() {
        EJBRequest request = new EJBRequest();
        List<ReportType> reportTypes = new ArrayList<ReportType>();
        List<Report> reports = new ArrayList<Report>();
        try {
            reportTypes = reportEJB.getReportTypes(request);
        } catch (EmptyListException e1) {
            this.showMessage("msj.error.general", true, e1);
        } catch (GeneralException e1) {
            this.showMessage("msj.error.general", true, e1);
        } catch (NullParameterException e1) {
            this.showMessage("msj.error.general", true, e1);
        }
        int ok = 0;
        for (ReportType type : reportTypes) {
            try {
                reports = reportEJB.getReportByReportTypeId(type.getId(), currentUser);
                ok++;
                Group group = new Group();
                group.setLabel(type.getName());
                group.setOpen(ok == 1 ? true : false);
                group.setParent(rowsReports);
                for (Report rp : reports) {
                    Row row = new Row();
                    row.setParent(rowsReports);
                    Label label = new Label();
                    label = initReport(rp);
                    label.setParent(row);
                }

            } catch (NullParameterException e) {
                this.showMessage("msj.error.general", true, e);
            } catch (GeneralException e) {
                this.showMessage("msj.error.general", true, e);
            } catch (EmptyListException e) {
                continue;
            }
        }
    }

    private Label initReport(final Report rp) {
        Label label = new Label();
        EventListener event = new EventListener() {

            public void onEvent(Event arg0) throws Exception {
                Executions.getCurrent().sendRedirect("managementAdminReport.zul");
                Sessions.getCurrent().setAttribute(WebConstants.SESSION_REPORT, rp);
            }
        };
        label.setValue(rp.getName());
        label.setStyle("cursor: pointer; color: dimGray; text-decoration: underline;");
        label.addEventListener("onClick", event);
        label.setTooltiptext(rp.getDescription());
        return label;
    }

   }

