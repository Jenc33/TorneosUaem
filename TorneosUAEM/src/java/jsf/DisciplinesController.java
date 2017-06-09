package jsf;

import jpa.entities.Disciplines;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.DisciplinesFacade;

import java.io.Serializable;
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
import jpa.entities.Tournaments;
/**
 * 
 * 
 * @version 2.0 9 de junio del 2017
 */
@Named("disciplinesController")
@SessionScoped
public class DisciplinesController implements Serializable {

    private Disciplines current;
    private DataModel items = null; 
    @EJB
    private jpa.session.DisciplinesFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<Tournaments> d;
    
    /**
     * 
     * @return lista de torneos que seran mostrados para las consultas
     */
    public List<Tournaments> getT() {
        return d ;
    }
    
    public DisciplinesController() {
    }

    public Disciplines getSelected() {
        if (current == null) {
            current = new Disciplines();
            selectedItemIndex = -1;
        }
        return current;
    }

    private DisciplinesFacade getFacade() {
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

    public String prepareName(){
        try{
            System.out.println("MIRA DISCIPLINE: "+current.getName());
            String name = String.valueOf(current.getName());
            System.out.println("NAME DISCIPLINE:  "+name);
            this.d = getFacade().findDiscipline(name);
            return "searchTournamentsByDiscipline";
        } catch (Exception e){
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return "error";
        }
    }
    
    public String prepareList() {
        recreateModel();
        return "ListDiscipline";
    }

    public String prepareView() {
        current = (Disciplines) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "viewDiscipline";
    }

    public String prepareCreate() {
        current = new Disciplines();
        selectedItemIndex = -1;
        recreatePagination();
        recreateModel();
        return "ListDiscipline";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("DisciplinesCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Disciplines) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "EditDiscipline";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("DisciplinesUpdated"));
            return "viewDiscipline";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Disciplines) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "ListDiscipline";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "viewDiscipline";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "ListDiscipline";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("DisciplinesDeleted"));
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
     * recupera el arreglo de tournaments para mostrarlos
     * @return llamada al metodo getT()
     * @see getT()
     */
    public List<Tournaments> getItems2() {
        return getT();
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
        return "ListDiscipline";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "ListDiscipline";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Disciplines getDisciplines(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Disciplines.class)
    public static class DisciplinesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DisciplinesController controller = (DisciplinesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "disciplinesController");
            return controller.getDisciplines(getKey(value));
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
            if (object instanceof Disciplines) {
                Disciplines o = (Disciplines) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Disciplines.class.getName());
            }
        }

    }

}
