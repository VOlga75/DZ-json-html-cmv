import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Main {
    final public static String fileJsonName = "data.jsn";
    final public static String fileJsonNameXml = "data2.jsn";

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list = parseCSV(columnMapping, "data.csv");//получаем список сотрудников
        if (toFile(listToJson(list), fileJsonName)) {
            System.out.println(" Данные из формата csv в json успешно записаны");
        } else {
            System.out.println(" Запись данных из формате csv в json  не удалась");
        }
        list.clear();
        list = parseXml("data.xml");
        if (toFile(listToJson(list), fileJsonNameXml)) {
            System.out.println(" Данные из формата xml в json успешно записаны");
        } else {
            System.out.println(" Запись данных из формате xml в json  не удалась");
        }
    }

    public static List<Employee> parseXml(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        //Node root = doc.getDocumentElement();
        NodeList nodeList = doc.getDocumentElement().getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            employees.add(read(node));
        }
        return employees;
    }

    public static Employee read(Node node) {
        int id = 0;
        String firstName = "";
        String lastName = "";
        String country = "";
        int age = 0;
        NodeList emplFilds = node.getChildNodes();
        for (int f = 0; f < emplFilds.getLength(); f++) {
            Node emplFild = emplFilds.item(f);
            if (Node.ELEMENT_NODE == emplFild.getNodeType()) {
                switch (emplFild.getNodeName()) {
                    case "id":
                        id = Integer.parseInt(emplFild.getTextContent());
                        break;
                    case "firstName":
                        firstName = emplFild.getTextContent();
                        break;
                    case "lastName":
                        lastName = emplFild.getTextContent();
                        break;
                    case "country":
                        country = emplFild.getTextContent();
                        break;
                    case "age":
                        age = Integer.parseInt(emplFild.getTextContent());
                        break;
                    default:
                        break;
                }
            }
        }
        return new Employee(id, firstName, lastName, country, age);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        if (list.isEmpty()) {
            return "Нет объектов для конвертации";
        } else {
            StringBuilder rez = new StringBuilder();
            GsonBuilder builder = new GsonBuilder();
            Gson gs = builder.setPrettyPrinting().create();
            ListIterator iterator = list.listIterator();
            while (iterator.hasNext()) {
                rez = rez.append(gs.toJson(iterator.next()) + "\n");
            }
            return rez.toString();
        }
    }

    public static boolean toFile(String json, String fileName) {
        File myFile = new File(fileName);
// создадим новый файл
        try {
            if (myFile.createNewFile()) {
                FileWriter file = new FileWriter(fileName);
                file.write(String.valueOf(json));
                file.close();
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}

