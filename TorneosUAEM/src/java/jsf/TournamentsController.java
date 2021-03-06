package jsf;

import jpa.entities.Tournaments;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.TournamentsFacade;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("tournamentsController")
@SessionScoped
public class TournamentsController implements Serializable {

    private Tournaments current;
    private DataModel items = null;
    @EJB
    private jpa.session.TournamentsFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    private List<Tournaments> t;
    
    /**
     * 
     * @return lista de torneos que seran mostrados en las consultas 
     */
    public List<Tournaments> getT() {
        return t;
    }

    public void setT(List<Tournaments> t) {
        this.t = t;
    }

    public TournamentsController() {
    }

    public Tournaments getSelected() {
        if (current == null) {
            current = new Tournaments();
            selectedItemIndex = -1;
        }
        return current;
    }

    private TournamentsFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Tournaments) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "ViewTournament";
    }

    public String prepareCreate() {
        current = new Tournaments();
        selectedItemIndex = -1;
        recreatePagination();
        recreateModel();
        return "CreateTournament";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("TournamentsCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Tournaments) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "EditTournament";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("TournamentsUpdated"));
            return "ViewTournament";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Tournaments) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "ListTournament";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "ViewTournament";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "ListTournament";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("TournamentsDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }
    
    /**
     * Obtención del arreglo de tournaments para mostrarlos
     * @return llamada al metodo get()
     * @see getT()
     */
    public List<Tournaments> getItems2() {
        return getT();
    }
 
    public String prepareDate(){
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current.getInscriptionStartDate());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            DateFormat df = new SimpleDateFormat("YYYY/MM/dd");
            String reportDate = df.format(calendar.getTime());
            System.out.println("FECHA 1: " + current.getInscriptionStartDate().toString());
            System.out.println("FECHA 2: " + reportDate);
            System.out.println("FECHA 3: " + calendar.getTime().toString());
            this.t = getFacade().findDate(reportDate);
            return "searchTournamentsByDate";
        } catch (Exception e){
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return "error";
        }
    }
    
    public String prepareName(){
        try{
            System.out.println("MIRA: "+current.getName());
            String name = String.valueOf(current.getName());
            System.out.println("NAME:  "+name);
            this.t = getFacade().findName(name);
            return "searchTournamentsByName";
        } catch (Exception e){
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return "error";
        }
    }
    
    public String prepareRules() {
        current = (Tournaments) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "viewRules";
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "ListTournament";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "ListTournament";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Tournaments getTournaments(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Tournaments.class)
    public static class TournamentsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TournamentsController controller = (TournamentsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tournamentsController");
            return controller.getTournaments(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Tournaments) {
                Tournaments o = (Tournaments) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Tournaments.class.getName());
            }
        }

    }

}
