package controllers;

import entities.LigneEchange;
import controllers.util.JsfUtil;
import controllers.util.PaginationHelper;

import java.io.Serializable;
import java.util.ResourceBundle;
import jakarta.annotation.Resource;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.faces.model.SelectItem;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.UserTransaction;

@Named("ligneEchangeController")
@SessionScoped
public class LigneEchangeController implements Serializable {

    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    private LigneEchange current;
    private DataModel items = null;
    private LigneEchangeJpaController jpaController = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public LigneEchangeController() {
    }

    public LigneEchange getSelected() {
        if (current == null) {
            current = new LigneEchange();
            current.setLigneEchangePK(new entities.LigneEchangePK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private LigneEchangeJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new LigneEchangeJpaController(utx, emf);
        }
        return jpaController;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getJpaController().getLigneEchangeCount();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getJpaController().findLigneEchangeEntities(getPageSize(), getPageFirstItem()));
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
        current = (LigneEchange) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new LigneEchange();
        current.setLigneEchangePK(new entities.LigneEchangePK());
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            current.getLigneEchangePK().setIdEchange(current.getEchange().getIdEchange());
            current.getLigneEchangePK().setNLot(current.getProduit().getNLot());
            getJpaController().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("LigneEchangeCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (LigneEchange) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            current.getLigneEchangePK().setIdEchange(current.getEchange().getIdEchange());
            current.getLigneEchangePK().setNLot(current.getProduit().getNLot());
            getJpaController().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("LigneEchangeUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (LigneEchange) getItems().getRowData();
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
            getJpaController().destroy(current.getLigneEchangePK());
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("LigneEchangeDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getJpaController().getLigneEchangeCount();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getJpaController().findLigneEchangeEntities(1, selectedItemIndex).get(0);
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
        return JsfUtil.getSelectItems(getJpaController().findLigneEchangeEntities(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findLigneEchangeEntities(), true);
    }

    @FacesConverter(forClass = LigneEchange.class)
    public static class LigneEchangeControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LigneEchangeController controller = (LigneEchangeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ligneEchangeController");
            return controller.getJpaController().findLigneEchange(getKey(value));
        }

        entities.LigneEchangePK getKey(String value) {
            entities.LigneEchangePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new entities.LigneEchangePK();
            key.setNLot(values[0]);
            key.setIdEchange(values[1]);
            return key;
        }

        String getStringKey(entities.LigneEchangePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getNLot());
            sb.append(SEPARATOR);
            sb.append(value.getIdEchange());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof LigneEchange) {
                LigneEchange o = (LigneEchange) object;
                return getStringKey(o.getLigneEchangePK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + LigneEchange.class.getName());
            }
        }

    }

}
