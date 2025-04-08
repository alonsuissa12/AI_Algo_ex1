
// קובץ Q1_main.java
import java.util.List;

public class Q1_main {
    public static void main(String[] args) {
        process_data pd = new process_data("files/alarm_net.xml");
        List<element> elements = pd.getElements();

        if (elements == null || elements.isEmpty()) {
            System.out.println("No elements found.");
            return;
        }

        // הדפסה של כל אלמנט והמערך outcome שלו
        for (element e : elements) {
            if (e == null) {
                System.out.println("Element is null.");
                continue;
            }
            System.out.println(e.toString());
            System.out.println();
        }

        System.out.println("---------------------------------------------------");
        build_net.Graph net = new build_net.Graph(elements);
        net.printGraph();

    }
}