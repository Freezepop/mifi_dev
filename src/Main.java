import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Вас приветствует мега погодная программа.\n");

        String digRegexp = "^\\d\\d\\.\\d\\d$";

        boolean validate_input_lat = false;
        boolean validate_input_lon = false;

        System.out.println("Введите долготу в формате координат в десятичных градусах (например, 55.75):\n");
        String lat = "";
        String lon = "";

        while (!validate_input_lat) {
            lat = scanner.nextLine();
            if (lat.matches(digRegexp)) {
                validate_input_lat = true;
            } else {
                System.out.println("Ввод некорректен, повторите попытку:\n");
            }

        }

        System.out.println("Введите широту в формате координат в десятичных градусах (например, 37.62):\n");

        while (!validate_input_lon) {
            lon = scanner.nextLine();
            if (lon.matches(digRegexp)) {
                validate_input_lon = true;
            } else {
                System.out.println("Ввод некорректен, повторите попытку:\n");
            }

        }

        int limit = 3;
        System.out.println("Введите количество дней для прогноза от 1 до 9 (например 2. По умолчанию 3): \n");

        if (scanner.hasNextInt()) {
            int custom_limit = scanner.nextInt();
            if (custom_limit >= 1 && custom_limit <= 9) {
                limit = custom_limit;
                System.out.println("Установлен кастомный лимит в " + limit + " дня/дней\n");

            }

        }
        else {
            System.out.println("Используем дефолтный лимит в " + limit + " дня\n");
        }


        scanner.close();
        System.out.println("Вы ввели lat: " + lat + " и lon " + lon + ". Действует лимит в " + limit + " дня/дней");

        String result_json = get_weather(lat, lon, limit);
        double average_temp = json_processing_avg(result_json);
        int fact_temp = json_processing_fact_temp(result_json);
        String beauty_json = json_beautifier(result_json);

        if (!beauty_json.equals("Json сломался в обработке, извините =(")) {
            System.out.println("Сер, Ваш красивый Json целиком: \n" + beauty_json + "\n");
        }
        else {
            System.out.println(beauty_json);
        }

        if (average_temp != 999999 && !Double.isNaN(average_temp)) {
            System.out.println("Средняя температура за указанный период " + limit + " дня/дней составляет: " + average_temp + " градусов.\n");
        }
        else {
            System.out.println("К сожалению, среднюю температуру посчитать не удалось =(\n");
        }

        if (fact_temp != 999999) {
            System.out.println("Отдельно температура из поля fact.temp: " + fact_temp + " градусов.\n");
        }
        else {
            System.out.println("К сожалению, температуру из поля fact.temp посчитать не удалось =(\n");
        }

    }

    private static String get_weather(String lat, String lon, int limit) {

        String url = String.format("https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s&limit=%d", lat, lon, limit);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        HttpRequest request = HttpRequest.newBuilder().
                uri(URI.create(url))
                .header("X-Yandex-Weather-Key", "") // Надо свой токен вводить =
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static double json_processing_avg(String json) {

        ObjectMapper objectMapper = new ObjectMapper();

        try{
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode dataArray = rootNode.path("forecasts");

            int sum = 0;
            int count = 0;

            for (JsonNode node : dataArray) {
                // Да-да, в задании не было указано какой именно температуры, поэтому я решил себе упростить жизнь и считать из поля day_short для каждого дня
                int day_temp = node.path("parts").path("day_short").path("temp").intValue();
                sum += day_temp;
                count++;
            }

            return (double) sum / count;

        }
        catch (JsonProcessingException e) {
            return 999999;
        }
    }

    private static int json_processing_fact_temp(String json) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode check_exists = rootNode.path("fact").findValue("temp");
            if (check_exists != null) {
                return rootNode.path("fact").path("temp").intValue();
            }
            else {
                return 999999;
            }
        } catch (JsonProcessingException e) {
            return 999999;
        }
    }

    private static String json_beautifier(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            Object json_str = objectMapper.readValue(json, Object.class);
            return objectMapper.writeValueAsString(json_str);
        } catch (JsonProcessingException e) {
            return "Json сломался в обработке, извините =(";
        }
    }
}
