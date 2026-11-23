package controllers;

import entities.LigneVente;
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

@Named("ligneVenteController")
@SessionScoped
public class LigneVenteController implements Serializable {

    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    private LigneVente current;
    private DataModel items = null;
    private LigneVenteJpaController jpaController = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public LigneVenteController() {
    }

    public LigneVente getSelected() {
        if (current == null) {
            current = new LigneVente();
            current.setLigneVentePK(new entities.LigneVentePK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private LigneVenteJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new LigneVenteJpaController(utx, emf);
        }
        return jpaController;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getJpaController().getLigneVenteCount();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getJpaController().findLigneVenteEntities(getPageSize(), getPageFirstItem()));
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
        current = (LigneVente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new LigneVente();
        current.setLigneVentePK(new entities.LigneVentePK());
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            current.getLigneVentePK().setIdV(current.getVente().getIdV());
            current.getLigneVentePK().setNLot(current.getProduit().getNLot());
            getJpaController().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("LigneVenteCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (LigneVente) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            current.getLigneVentePK().setIdV(current.getVente().getIdV());
            current.getLigneVentePK().setNLot(current.getProduit().getNLot());
            getJpaController().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("LigneVenteUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (LigneVente) getItems().getRowData();
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
            getJpaController().destroy(current.getLigneVentePK());
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("LigneVenteDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getJpaController().getLigneVenteCount();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getJpaController().findLigneVenteEntities(1, selectedItemIndex).get(0);
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
        return JsfUtil.getSelectItems(getJpaController().findLigneVenteEntities(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findLigneVenteEntities(), true);
    }

    @FacesConverter(forClass = LigneVente.class)
    public static class LigneVenteControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LigneVenteController controller = (LigneVenteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ligneVenteController");
            return controller.getJpaController().findLigneVente(getKey(value));
        }

        entities.LigneVentePK getKey(String value) {
            entities.LigneVentePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new entities.LigneVentePK();
            key.setNLot(values[0]);
            key.setIdV(values[1]);
            return key;
        }

        String getStringKey(entities.LigneVentePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getNLot());
            sb.append(SEPARATOR);
            sb.append(value.getIdV());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof LigneVente) {
                LigneVente o = (LigneVente) object;
                return getStringKey(o.getLigneVentePK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + LigneVente.class.getName());
            }
        }

    }

}
