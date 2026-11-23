package services;

import entities.LigneVente;
import entities.Vente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Service de génération et d'exportation de facture en PDF via JasperReports
 */
@ApplicationScoped
public class FacturePdfService implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Génère le flux binaire PDF de la facture
     */
    public byte[] genererFacturePdf(Vente vente, List<LigneVente> lignes) throws Exception {
        InputStream reportStream = getClass().getResourceAsStream("/reports/facture_vente.jrxml");
        if (reportStream == null) {
            throw new IllegalStateException("Modèle JasperReports introuvable : /reports/facture_vente.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateStr = (vente.getDateVente() != null) ? sdf.format(vente.getDateVente()) : sdf.format(new java.util.Date());

        String nomVendeur = "Pharmacien";
        if (vente.getIdUtilisateur() != null) {
            nomVendeur = vente.getIdUtilisateur().getPrenom() + " " + vente.getIdUtilisateur().getNom();
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("NUM_FACTURE", vente.getIdV());
        parameters.put("DATE_FACTURE", dateStr);
        parameters.put("NOM_PHARMACIE", "Pharmacie Centrale");
        parameters.put("ADRESSE_PHARMACIE", "Avenue de l'Indépendance");
        parameters.put("TEL_PHARMACIE", "0550 12 34 56");
        parameters.put("NOM_VENDEUR", nomVendeur);
        parameters.put("TOTAL_VENTE", vente.getTotal() != null ? vente.getTotal() : BigDecimal.ZERO);

        List<LigneFactureDTO> items = new ArrayList<>();
        if (lignes != null && !lignes.isEmpty()) {
            for (LigneVente lv : lignes) {
                String nLot = (lv.getProduit() != null) ? lv.getProduit().getNLot() : lv.getLigneVentePK().getNLot();
                String nom = (lv.getProduit() != null) ? lv.getProduit().getNomC() : "Médicament";
                int qte = lv.getQuantite();
                BigDecimal pu = lv.getPrixUnitaire();
                BigDecimal totalLigne = pu.multiply(BigDecimal.valueOf(qte));
                items.add(new LigneFactureDTO(nLot, nom, qte, pu, totalLigne));
            }
        } else {
            // Si aucune ligne n'est passée, on crée une ligne synthétique à partir de l'entête
            String nom = (vente.getNomC() != null) ? vente.getNomC() : "Vente comptoir";
            int qte = (vente.getQte() > 0) ? vente.getQte() : 1;
            BigDecimal pu = (vente.getPpa() != null) ? vente.getPpa() : (vente.getTotal() != null ? vente.getTotal() : BigDecimal.ZERO);
            BigDecimal totalLigne = vente.getTotal() != null ? vente.getTotal() : pu;
            items.add(new LigneFactureDTO(vente.getIdV(), nom, qte, pu, totalLigne));
        }

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    /**
     * Diffuse directement la facture générée au navigateur pour téléchargement ou affichage
     */
    public void diffuserFacturePdf(Vente vente, List<LigneVente> lignes) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            byte[] pdfBytes = genererFacturePdf(vente, lignes);

            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseContentLength(pdfBytes.length);
            externalContext.setResponseHeader("Content-Disposition", "inline; filename=\"Facture_" + vente.getIdV() + ".pdf\"");

            OutputStream output = externalContext.getResponseOutputStream();
            output.write(pdfBytes);
            output.flush();

            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Erreur PDF", "Impossible de générer la facture : " + e.getMessage()));
        }
    }
}
