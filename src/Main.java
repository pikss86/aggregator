import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class Main {

    private static double avg(Item[] data) {
        if (data.length == 0)
            System.out.println("Input array length 0");

        double avgValue = 0;
        long nullCount = 0;
        for (Item item : data) {
            if (item.ups_adv_battery_run_time_remaining == null)
                nullCount++;
            else
                avgValue += item.ups_adv_battery_run_time_remaining;
        }
        avgValue = avgValue / (data.length - nullCount);
        return avgValue;
    }

    private static long max(Item[] data) {
        if (data.length == 0)
            System.out.println("Input array length 0");

        long maxValue = data[0].ups_adv_output_voltage;
        for (Item item : data) {
            if (item.ups_adv_output_voltage != null)
                if (item.ups_adv_output_voltage > maxValue)
                    maxValue = item.ups_adv_output_voltage;
        }
        return maxValue;
    }

    private static Set<String> values(Item[] data) {
        Set<String> valuesSet = new HashSet<>();
        for (Item item : data) {
            if (item.host != null)
                valuesSet.add(item.host);
        }
        return valuesSet;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Wrong params. Please use " +
                    "\"java -jar app_name.jar <Function Name> testData.json\"");
            System.exit(1);
        }
        String functionName = args[0];
        String fileName = args[1];
        Reader reader = new FileReader(fileName);
        Gson gson = new Gson();
        Item[] data = gson.fromJson(reader, Item[].class);
        if (data.length == 0) {
            System.out.println("Input array length 0");
            System.exit(2);
        }
        if ("avg".equals(functionName)) {
            double result = avg(data);
            System.out.println(result);
        } else if ("max".equals(functionName)) {
            long result = max(data);
            System.out.println(result);
        } else if ("values".equals(functionName)) {
            Set<String> result = values(data);
            System.out.println(gson.toJson(result));
        }
    }
}
