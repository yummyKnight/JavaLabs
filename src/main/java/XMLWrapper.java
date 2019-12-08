import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class XMLWrapper {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(XMLWrapper.class);
    static void writeXML(JTable table, String fileName) throws ParserConfigurationException, TransformerException, IOException {
        logger.debug("Начало создание XML-файла");
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();

        Node root = document.createElement("Schedule");
        document.appendChild(root);
        for (int i = 0; i < table.getRowCount(); i++) {
            Element route = document.createElement("Route");
            root.appendChild(route);
            Element id = document.createElement("ID");
            id.setAttribute("id", Integer.toString ((int)table.getValueAt(i, 0)));
            route.appendChild(id);
            Element drivers = document.createElement("Drivers");
            String allDrivers = (String) table.getValueAt(i, 1);
            for (String driver : allDrivers.split(",")) {
                Element drEl = document.createElement("driver");
                drEl.setAttribute("FIO", driver);
                drivers.appendChild(drEl);
            }
            route.appendChild(drivers);
            Element stops = document.createElement("Stops");
            stops.setAttribute("stopsToString", (String) table.getValueAt(i, 2));
            route.appendChild(stops);
            Element time = document.createElement("Time");
            time.setAttribute("shortTime", (String) table.getValueAt(i, 3));
            route.appendChild(time);
        }
        // Запись в файл
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        java.io.FileWriter fw = new FileWriter(fileName);
        trans.transform(new DOMSource(document), new StreamResult(fw));
        logger.debug("Конец создания XML - файла {}");
    }

    static void readXML(DefaultTableModel model, String fileName) throws ParserConfigurationException, IOException, SAXException {
        logger.debug("Начало считывания XML файла");
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        // Чтение документа из файла
        Document document;
        document = builder.parse(new File(fileName));
        // Нормализация документа
        document.getDocumentElement().normalize();
        NodeList routes = document.getElementsByTagName("Route");
        for (int i = 0; i < routes.getLength(); i++) {
            StringBuilder sb = new StringBuilder();
            String time = "";
            String shortStops = "";
            String id = "";
            if (routes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                NodeList route = routes.item(i).getChildNodes();
                for (int j = 0; j < route.getLength(); j++) {
                    if (route.item(j).getNodeName().equals("Drivers") && route.item(j).getNodeType() == Node.ELEMENT_NODE ) {
                        NodeList drivers = route.item(j).getChildNodes();
                        for (int k = 0; k < drivers.getLength(); k++) {
                            if (drivers.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                String driver_name = drivers.item(k).getAttributes().getNamedItem("FIO").getNodeValue();
                                sb.append(driver_name);
                                sb.append(",");
                            }
                        }
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    if (route.item(j).getNodeName().equals("ID") && route.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        id = route.item(j).getAttributes().getNamedItem("id").getNodeValue();
                    }
                    if (route.item(j).getNodeName().equals("Stops") && route.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        shortStops = route.item(j).getAttributes().getNamedItem("stopsToString").getNodeValue();
                    }
                    if (route.item(j).getNodeName().equals("Time") && route.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        time = route.item(j).getAttributes().getNamedItem("shortTime").getNodeValue();
                    }
                }
            }
            Object[] row = new Object[]{id,sb.toString(), shortStops, time};
            model.addRow(row);
        }
        logger.debug("Конец считывания XML файла");
    }
}
