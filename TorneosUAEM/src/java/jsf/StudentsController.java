package jsf;

import jpa.entities.Students;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.StudentsFacade;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import jpa.entities.Equips;


@Named("studentsController")
@SessionScoped
public class StudentsController implements Serializable {

    private Students current;
    private DataModel items = null;
    @EJB
    private jpa.session.StudentsFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private boolean loggedIn;
    private boolean loggedIn2;
    
    private String username;
    private Integer id;
    private Integer noMembers;
    private Integer idEquip;

    public Integer getNoMembers() {
        return noMembers;
    }

    public void setNoMembers(Integer noMembers) {
        this.noMembers = noMembers;
    }

    public Integer getIdEquip() {
        return idEquip;
    }

    public void setIdEquip(Integer idEquip) {
        this.idEquip = idEquip;
    } 
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public StudentsController() {
    }

    public Students getSelected() {
        if (current == null) {
            current = new Students();
            selectedItemIndex = -1;
        }
        return current;
    }

    private StudentsFacade getFacade() {
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
        current = (Students) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }
    /**
     * Muestra los datos del estudiante a eliminarse del equipo
     * @return vista a estudiante 
     */
    public String prepareView2() {
        current = (Students) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "viewStudent";
    }

    public String prepareCreate() {
        current = new Students();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("StudentsCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    
    /**
     * control para verificar numero de integrantes en el equipo
     * @return vistaUsuario o vista de creación 
     */
    public String prepareCreate2() {
        current = new Students();
        selectedItemIndex = -1;
        if(this.noMembers > 0 ){
            return "createStudentsByEquip";
        }else{
            return "/vistaUsuario";
        }
    }
    /**
     * Creación del alumno por equipo
     * @return preparacion a creacion de un uevo integrante o salida
     */
    public String create2() {
        this.setIdEquip((Integer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("idEquip"));
        this.setNoMembers((Integer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("MembersEquip"));
        try {
            Equips e2 = new Equips();
            e2.setId(idEquip);
            current.setIdEquip(e2);
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("StudentsCreated"));
            this.noMembers--;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("MembersEquip", this.noMembers);
            return prepareCreate2();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Students) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }
    
    /**
     * preparación para eliminación de integrante
     * @return llamada a metodo update2()
     * @see update2()
     */
    public String prepareEdit2() {
        current = (Students) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return update2();
    }
    
    /**
     * Desvinculación de alumno de un equipo
     * @return 
     */
    public String update2() {
        try {
            current.setIdEquip(null);
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("StudentsUpdated"));
            return "deleteStudentEquip";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("StudentsUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    
    /**
     * Inicio de sesión
     * @return vistas de acuerdo al nivel de usuario
     */
    public String sessionStart() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            List<Students> stuList = getFacade().findAll();
            for (Students student : stuList) {
                if (student.getUsername().equals(current.getUsername())) {
                    if (student.getPassword().equals(current.getPassword())) {
                        context.getExternalContext().getSessionMap().put("id", student.getId());
                        context.getExternalContext().getSessionMap().put("user", student.getUsername());
                        context.getExternalContext().getSessionMap().put("level", student.getLevel());
                        if (student.getLevel() == 1) {
                            loggedIn = true;
                            username = student.getUsername();
                            this.id = student.getId();
                            return "vistaAdmin?faces-redirect=true";
                        } else {
                            username = student.getUsername();
                            this.id = student.getId();
                            loggedIn2 = true;
                            return "vistaUsuario?faces-redirect=true";
                        }
                    }
                }
            }
            loggedIn = false;
            loggedIn2 = false;
            context.addMessage(null, new FacesMessage("Unknown login, try again"));
        } catch (Exception e) {
            loggedIn = false;
            loggedIn2 = false;
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return "registro?faces-redirect=true";
        }
        return "registro?faces-redirect=true";
    }
    
    /**
     * Detención de la sesión de administrador
     * @return redireccion a la pagina principal
     */
    public String sessionStop() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        loggedIn = false;
        return "login?faces-redirect=true";
    }
    
    /**
     * Detención de la sesión de usuario
     * @return redirección a la pagina principal
     */
    public String sessionStop2() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        loggedIn2 = false;
        return "login?faces-redirect=true";
    }
    
    /**
     * Verificación de una sesión iniciada por parte del administrador
     * @param event 
     */
    public void checkLogin(ComponentSystemEvent event) {
        if (!loggedIn) {
            FacesContext context = FacesContext.getCurrentInstance();
            ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler) context.
                    getApplication().
                    getNavigationHandler();
            handler.performNavigation("login");
        }
    }
    
    /**
     * Verificación de una sesión iniciada por parte del usuario
     * @param event 
     */
    public void checkLogin2(ComponentSystemEvent event) {
        if (!loggedIn2) {
            FacesContext context = FacesContext.getCurrentInstance();
            ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler) context.
                    getApplication().
                    getNavigationHandler();

            handler.performNavigation("login");
        }
    }

    public String destroy() {
        current = (Students) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("StudentsDeleted"));
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

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Students getStudents(java.lang.Integer id) {
        return ejbFacade.find(id);
    }
    

    @FacesConverter(forClass = Students.class)
    public static class StudentsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            StudentsController controller = (StudentsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "studentsController");
            return controller.getStudents(getKey(value));
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
            if (object instanceof Students) {
                Students o = (Students) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Students.class.getName());
            }
        }

    }

}
