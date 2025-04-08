import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import java.util.HashMap;
import java.util.Map;

class element {


    String name;
    String[] outcome;
    Map<String[], double[]> probabilities; // Added dictionary field.


    public element(String name, String[] outcome) {
        this.name = name;
        this.outcome = outcome;
        this.probabilities = new HashMap<>(); // Initialize dictionary field.
    }

    public element(String name, boolean outcome) {
        this.name = name;
        this.outcome = new String[]{"T", "F"};
        this.probabilities = new HashMap<>(); // Initialize dictionary field.
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getOutcome() {
        return outcome;
    }

    public void setOutcome(String[] outcome) {
        this.outcome = outcome;
    }

    public void setOutcome(boolean outcome) {
        this.outcome = new String[]{"T", "F"};
    }

    public Map<String[], double[]> getDictionary() { // Added getter for dictionary.
        return probabilities;
    }

    public void setDictionary(Map<String[], double[]> dictionary) { // Added setter for dictionary.
        this.probabilities = dictionary;
    }

        public void addProbability(String[] key, double[] probability) {
        if (probabilities == null) {
            probabilities = new HashMap<>();
        }
        probabilities.put(key, probability);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Element{name='" + name + "', outcome=");
        if (outcome != null) {
            sb.append("[");
            for (int i = 0; i < outcome.length; i++) {
                sb.append(outcome[i]);
                if (i < outcome.length - 1)
                    sb.append(", ");
            }
            sb.append("]");
        } else {
            sb.append("null");
        }
        sb.append(", probabilities=");
        if (probabilities != null && !probabilities.isEmpty()) {
            probabilities.forEach((key, value) -> {
                sb.append("[Given: ");
                for (int i = 0; i < key.length; i++) {
                    sb.append(key[i]);
                    if (i < key.length - 1)
                        sb.append(", ");
                }
                sb.append(" Probabilities: ");
                for (int i = 0; i < value.length; i++) {
                    sb.append(value[i]);
                    if (i < value.length - 1)
                        sb.append(", ");
                }
                sb.append("]");
            });
        } else {
            sb.append("empty");
        }
        sb.append("}");
        return sb.toString();
    }

}

/**
 * process_data reads an XML file and processes its nodes to extract VARIABLE and DEFINITION information.
 * It builds a list of element objects, which include outcome settings and optional probability definitions.
 * It uses DOM parsing and XPath to retrieve and process the data from the XML.
 */
public class process_data {
    private Document doc;

    /**
     * Retrieves a list of outcome values associated with a specific variable name
     * from an XML document.
     *
     * The method utilizes XPath to locate all OUTCOME nodes within VARIABLE
     * elements matching the specified variable name in the provided XML document.
     * It extracts the text content of these OUTCOME nodes and returns them as
     * a list of strings.
     *
     * @param doc The XML document to search for the specified variable and its outcomes.
     * @param variableName The name of the variable whose outcomes need to be retrieved.
     * @return A list of outcome values as strings associated with the specified variable.
     *         Returns an empty list if no outcomes are found or if the document is invalid.
     */
    public List<String> getOutcomesForVariableName(Document doc, String variableName) {
        List<String> outcomes = new ArrayList<>();
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            String expression = "//VARIABLE[NAME='" + variableName + "']/OUTCOME";
            NodeList outcomeNodes = (NodeList) xPath.evaluate(expression, doc, XPathConstants.NODESET);
            for (int i = 0; i < outcomeNodes.getLength(); i++) {
                outcomes.add(outcomeNodes.item(i).getTextContent()); //todo change to int
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return outcomes;
    }


    public process_data(String file_name) {
        try {
            File xmlFile = new File(file_name);
            if (!xmlFile.exists()) {
                System.out.println("File not found: " + xmlFile.getAbsolutePath());
                return;
            }
            // Create a DocumentBuilderFactory and configure it
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // Parse the XML file
            doc = dBuilder.parse(xmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // מתודה זו מעבדת את ה XML ומחזירה רשימה של אובייקטי element
    public List<element> getElements() {
        List<element> elementsList = new ArrayList<>();
        if (doc == null) {
            return elementsList;
        }
        // השגת השורש
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        // מעבר על כל הילדים של השורש
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            // מעבד רק את צמתי האלמנטים
            if (node.getNodeName().equals("VARIABLE")) {
                Element elem = (Element) node;
                String name = elem.getElementsByTagName("NAME").item(0).getTextContent();

                element elemObj;


                List<String> outcomeOptions = getOutcomesForVariableName(doc, name);
                System.out.println("Variable: " + name + " has " + outcomeOptions.size() + " outcomes.");

                if (outcomeOptions == null || outcomeOptions.isEmpty()) {
                    // אין תכונת outcome - נשתמש בברירת המחדל
                    elemObj = new element(name, null);
                } else {
                    // בדיקה אם הערך הוא ביטוי בוליאני
                    if (outcomeOptions.get(0).equalsIgnoreCase("T") || outcomeOptions.get(0).equalsIgnoreCase("F")) {
                        elemObj = new element(name, true);
                    } else {
                        String[] outcomes = new String[outcomeOptions.size()];
                        try {
                            for (int j = 0; j < outcomeOptions.size(); j++) {
                                outcomes[j] = outcomeOptions.get(j);
                            }
                            elemObj = new element(name, outcomes);
                        } catch (NumberFormatException e) {
                            // אם נתקלה שגיאה בפירוש למספרים, נשתמש בברירת המחדל
                            throw new NumberFormatException("Invalid outcome value for variable: " + name);
                        }
                    }
                }
                elementsList.add(elemObj);
            }
        }
        add_probabilities(doc, elementsList);
        return elementsList;
    }

    public List<element> add_probabilities(Document doc, List<element> elementsList) {
        if (doc == null || elementsList == null) {
            return elementsList;
        }
        // השגת השורש
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        // מעבר על כל הילדים של השורש
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            // מעבד רק את צמתי האלמנטים
            if (node.getNodeName().equals("DEFINITION")) {
                Element definitionElem = (Element) node;
                String name = definitionElem.getElementsByTagName("FOR").item(0).getTextContent();
                NodeList givenNodes = definitionElem.getElementsByTagName("GIVEN");
                String prob = definitionElem.getElementsByTagName("TABLE").item(0).getTextContent();
                String[] probArray = prob.split(" ");
                double[] probs  = new double[probArray.length];
                for (int k = 0; k < probArray.length; k++) {
                    probs[k] = Double.parseDouble(probArray[k]);
                }

                for (element e : elementsList) {
                    if (e.getName().equals(name)) {

                        String[] given = new String[givenNodes.getLength()];

                        if (givenNodes.getLength() > 0) {
                            for (int j = 0; j < givenNodes.getLength(); j++) {
                                given[j] = givenNodes.item(j).getTextContent();
                            }
                        }
                        e.addProbability(given, probs);
                    }
                }
            }
        }
        return elementsList;


    }


}