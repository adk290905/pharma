package controllers;

import entities.ConsultationBonCommande;
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

@Named("consultationBonCommandeController")
@SessionScoped
public class ConsultationBonCommandeController implements Serializable {

    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    private ConsultationBonCommande current;
    private DataModel items = null;
    private ConsultationBonCommandeJpaController jpaController = null;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public ConsultationBonCommandeController() {
    }

    public ConsultationBonCommande getSelected() {
        if (current == null) {
            current = new ConsultationBonCommande();
            current.setConsultationBonCommandePK(new entities.ConsultationBonCommandePK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private ConsultationBonCommandeJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new ConsultationBonCommandeJpaController(utx, emf);
        }
        return jpaController;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getJpaController().getConsultationBonCommandeCount();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getJpaController().findConsultationBonCommandeEntities(getPageSize(), getPageFirstItem()));
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
        current = (ConsultationBonCommande) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new ConsultationBonCommande();
        current.setConsultationBonCommandePK(new entities.ConsultationBonCommandePK());
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            current.getConsultationBonCommandePK().setIdUtilisateur(current.getUtilisateur().getIdUtilisateur());
            current.getConsultationBonCommandePK().setIdBon(current.getBonDeCommande().getIdBon());
            getJpaController().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ConsultationBonCommandeCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (ConsultationBonCommande) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            current.getConsultationBonCommandePK().setIdUtilisateur(current.getUtilisateur().getIdUtilisateur());
            current.getConsultationBonCommandePK().setIdBon(current.getBonDeCommande().getIdBon());
            getJpaController().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ConsultationBonCommandeUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (ConsultationBonCommande) getItems().getRowData();
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
            getJpaController().destroy(current.getConsultationBonCommandePK());
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ConsultationBonCommandeDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getJpaController().getConsultationBonCommandeCount();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getJpaController().findConsultationBonCommandeEntities(1, selectedItemIndex).get(0);
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
        return JsfUtil.getSelectItems(getJpaController().findConsultationBonCommandeEntities(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findConsultationBonCommandeEntities(), true);
    }

    @FacesConverter(forClass = ConsultationBonCommande.class)
    public static class ConsultationBonCommandeControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConsultationBonCommandeController controller = (ConsultationBonCommandeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "consultationBonCommandeController");
            return controller.getJpaController().findConsultationBonCommande(getKey(value));
        }

        entities.ConsultationBonCommandePK getKey(String value) {
            entities.ConsultationBonCommandePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new entities.ConsultationBonCommandePK();
            key.setIdUtilisateur(values[0]);
            key.setIdBon(values[1]);
            key.setDateConsultation(java.sql.Date.valueOf(values[2]));
            return key;
        }

        String getStringKey(entities.ConsultationBonCommandePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getIdUtilisateur());
            sb.append(SEPARATOR);
            sb.append(value.getIdBon());
            sb.append(SEPARATOR);
            sb.append(value.getDateConsultation());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ConsultationBonCommande) {
                ConsultationBonCommande o = (ConsultationBonCommande) object;
                return getStringKey(o.getConsultationBonCommandePK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ConsultationBonCommande.class.getName());
            }
        }

    }

}
