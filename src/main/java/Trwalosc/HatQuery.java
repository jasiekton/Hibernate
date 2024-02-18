package Trwalosc;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class HatQuery {

    private Session session = null;
    private List<Hats> hatList = null;
    private Query q = null;
 

    public List<Hats> getHatsList(boolean orderByBrand) {
        try {
            org.hibernate.Transaction tx = session.beginTransaction();
            if (orderByBrand) {
                q = session.createQuery("from Hats order by brand");
            } else {
                q = session.createQuery("from Hats");
            }
            hatList = (List<Hats>) q.list();
            tx.commit();
        } catch (HibernateException e) {
            throw e;
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return hatList;
    }

    public String getHatListHTML(List<Hats> hatList, boolean includeID, boolean includeBrand, boolean includeMaterial,
            boolean includeType, boolean includeStyleColor, boolean includeHasBrim, boolean includeHasRim) {
        StringBuilder row = new StringBuilder("<table><tr>");
        if (includeID) {
            row.append("<td><b>ID</b></td>");
        }
        if (includeBrand) {
            row.append("<td><b>Brand</b></td>");
        }
        if (includeMaterial) {
            row.append("<td><b>Material</b></td>");
        }
        if (includeType) {
            row.append("<td><b>Type</b></td>");
        }
        if (includeStyleColor) {
            row.append("<td><b>Style Color</b></td>");
        }
        if (includeHasBrim) {
            row.append("<td><b>Has Brim</b></td>");
        }
        if (includeHasRim) {
            row.append("<td><b>Has Rim</b></td>");
        }
        row.append("</tr>");

        for (Hats hat : hatList) {
            row.append("<tr>");
            if (includeID) {
                row.append("<td>").append(hat.getHatId()).append("</td>");
            }
            if (includeBrand) {
                row.append("<td>").append(hat.getBrand()).append("</td>");
            }
            if (includeMaterial) {
                row.append("<td>").append(hat.getMaterial()).append("</td>");
            }
            if (includeType) {
                row.append("<td>").append(hat.getType()).append("</td>");
            }
            if (includeStyleColor) {
                row.append("<td>").append(hat.getStyleColor()).append("</td>");
            }
            if (includeHasBrim) {
                row.append("<td>").append(hat.getHasBrim()).append("</td>");
            }
            if (includeHasRim) {
                row.append("<td>").append(hat.getHasRim()).append("</td>");
            }
            row.append("</tr>");
        }
        row.append("</table>");
        return row.toString();
    }

    public void addHat(String ID, String brand, String material, String type, String styleColor, boolean hasBrim, boolean hasRim) {
        try {
            org.hibernate.Transaction tx = session.beginTransaction();

            Hats newHat = new Hats();
            newHat.setHatId(Short.parseShort(ID));
            newHat.setBrand(brand);
            newHat.setMaterial(material);
            newHat.setType(type);
            newHat.setStyleColor(styleColor);
            newHat.setHasBrim(hasBrim);
            newHat.setHasRim(hasRim);

            session.save(newHat);

            tx.commit();
        } catch (HibernateException e) {
            throw e;
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
    }

    public void removeHat(String id) {
    try {
        short shortId = Short.parseShort(id);

        org.hibernate.Transaction tx = session.beginTransaction();

        Hats hat = (Hats) session.load(Hats.class, shortId);
        session.delete(hat);

        tx.commit();
    } catch (NumberFormatException | HibernateException e) {
        throw e;
    } finally {
        if (session.isOpen()) {
            session.close();
        }
    }
}

public void modifyHat(String id, String newBrand, String newMaterial, String newType, String newStyleColor,
        boolean newHasBrim, boolean newHasRim) {
    try {
        short shortId = Short.parseShort(id);

        org.hibernate.Transaction tx = session.beginTransaction();

        Hats hat = (Hats) session.load(Hats.class, shortId);
        hat.setBrand(newBrand);
        hat.setMaterial(newMaterial);
        hat.setType(newType);
        hat.setStyleColor(newStyleColor);
        hat.setHasBrim(newHasBrim);
        hat.setHasRim(newHasRim);

        session.update(hat);

        tx.commit();
    } catch (NumberFormatException | HibernateException e) {
        throw e;
    } finally {
        if (session.isOpen()) {
            session.close();
        }
    }
}

 public static void addToTransactionHistory(HttpServletRequest request, String logEntry) {
        HttpSession session = request.getSession(true);
        List<String> transactionHistory = (List<String>) session.getAttribute("transactionHistory");

        if (transactionHistory == null) {
            transactionHistory = new ArrayList<>();
        }

        transactionHistory.add(logEntry);
        session.setAttribute("transactionHistory", transactionHistory);
    }

    public static List<String> getTransactionHistory(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (List<String>) session.getAttribute("transactionHistory");
    }

    public HatQuery() {
        this.session = NewHibernateUtil.getSessionFactory().openSession();
    }
}
