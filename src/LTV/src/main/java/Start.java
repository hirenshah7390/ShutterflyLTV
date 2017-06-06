import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by hiren on 6/3/2017.
 */
public class Start {

    public static void main(String... args) throws Exception {

        //read the input file and convert as json array
        File inputFile = new File("input//input.txt");
        FileReader file = new FileReader(inputFile);
        BufferedReader fileReader = new BufferedReader(file);
        String jsonData = "", line = "";
        while ((line = fileReader.readLine()) != null) {
            jsonData += line + "\n";
        }
        fileReader.close();
        JSONArray array = new JSONArray(jsonData);

        //reading json
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (int i = 0; i < array.length(); i++) {
            String type = array.getJSONObject(i).getString("type").toUpperCase();
            //only considering required object and not all
            if (type.equals("CUSTOMER")) {
                Customer customer = new Customer();
                customer.key = array.getJSONObject(i).getString("key");
                customer.event_time = (DateFormat.parse(array.getJSONObject(i).getString("event_time")));
                customer.adr_city = array.getJSONObject(i).getString("adr_city");
                customer.last_name = array.getJSONObject(i).getString("last_name");
                customer.adr_state = array.getJSONObject(i).getString("adr_state");
                if ("NEW".equalsIgnoreCase(array.getJSONObject(i).getString("verb"))) {
                    customer.First_Visit_Date = customer.event_time;
                    customer.addCustomer();
                } else if ("UPDATE".equalsIgnoreCase(array.getJSONObject(i).getString("verb"))) {
                    //replicate same event date as existing one on update.
                    customer.First_Visit_Date = CountLTV.CustomerList.get(customer.key).First_Visit_Date;
                    customer.update();
                }
                customer.reset_time(customer.event_time);
            } else if (type.equals("SITE_VISIT")) {
                SiteVisit siteVisit = new SiteVisit();
                siteVisit.key = array.getJSONObject(i).getString("key");
                siteVisit.customer_id = array.getJSONObject(i).getString("customer_id");
                siteVisit.event_time = DateFormat.parse(array.getJSONObject(i).getString("event_time"));
                JSONObject tagsJson = array.getJSONObject(i).getJSONObject("tags");
                siteVisit.tags = new HashMap();
                Iterator<String> keys = tagsJson.keys();
                String key = "";
                while (keys.hasNext()) {
                    key = keys.next();
                    siteVisit.tags.put(key, tagsJson.getString(key));
                }
                if ("NEW".equalsIgnoreCase(array.getJSONObject(i).getString("verb"))) {
                    siteVisit.newVisit();
                }

            } else if (type.equals("ORDER")) {
                Order order = new Order();
                order.customerid = array.getJSONObject(i).getString("customer_id");
                order.event_time = DateFormat.parse(array.getJSONObject(i).getString("event_time"));
                order.key = array.getJSONObject(i).getString("key");
                Double amt = Double.parseDouble(array.getJSONObject(i).getString("total_amount").replaceAll("USD", "").trim());
                order.amount = amt.intValue();
                if ("NEW".equalsIgnoreCase(array.getJSONObject(i).getString("verb"))) {
                    order.newOrder();
                } else if ("UPDATE".equalsIgnoreCase(array.getJSONObject(i).getString("verb"))) {
                    order.update();
                }

            } else if (type.equals("IMAGE")) {
                Image image = new Image();
                image.cameraMake = array.getJSONObject(i).getString("camera_make");
                image.cameraModel = array.getJSONObject(i).getString("camera_model");
                image.customer_id = array.getJSONObject(i).getString("customer_id");
                image.event_time = DateFormat.parse(array.getJSONObject(i).getString("event_time"));
                image.key = array.getJSONObject(i).getString("key");
                if ("UPLOAD".equalsIgnoreCase(array.getJSONObject(i).getString("verb"))) {
                    image.upload();
                }
            }
        }


        //Print the top x customers with the highest LTV from as  data eventsList
        CountLTV.topXSimpleLTVCustomers(10, CountLTV.CustomerList);


    }

}
