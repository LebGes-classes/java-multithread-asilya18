import java.util.Map;
import java.util.LinkedHashMap;

public class WeatherService {
    public static final Map<String, String> fakeWeatherData = new LinkedHashMap<>() {{
        // что-то типа статической базы данных
        put("Москва", "☀️ +20°C, ясно");
        put("Санкт-Петербург", "🌧 +15°C, дождь");
        put("Лондон", "☁️ +12°C, облачно");
        put("Нью-Йорк", "⛅ +18°C, переменная облачность");
        put("Токио", "🌦 +22°C, небольшой дождь");
    }};

    public static String getWeather(String city) { // основное - возвращает погоду
        for (Map.Entry<String, String> entry : fakeWeatherData.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(city)) {
                return "Погода в " + entry.getKey() + ": " + entry.getValue();
            }
        }
        return getCityNotFoundMessage(); // иначе ошибка
    }

    public static String getAvailableCities() { // список городов из базы данных
        StringBuilder sb = new StringBuilder("Доступные города:\n");
        fakeWeatherData.keySet().forEach(city -> sb.append("• ").append(city).append("\n"));
        return sb.toString();
    }

    private static String getCityNotFoundMessage() { // если города нет в нашей базе данных
        return "Город не найден.\n" + getAvailableCities();
    }
}