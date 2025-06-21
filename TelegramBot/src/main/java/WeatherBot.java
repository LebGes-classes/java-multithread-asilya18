import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherBot extends TelegramLongPollingBot {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    // пул потоков для многопоточности
    @Override
    public String getBotUsername() {
        return "weather_11_401_bot";
    }

    @Override
    public String getBotToken() {
        return "8089537233:AAHK2vAlv7kzTLJWshaB9GX8yNJTmbFuYqo";
    }

    @Override
    public void onUpdateReceived(Update update) { // update - обьект,
        // с информацией по действиям пользователя
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update); // вызов текстовых команд
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update); // нажатие на inline-кнопки
            // (кликабельные кнопки)
        }
    }

    private void handleTextMessage(Update update) { // метод обработки текстовых команд
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId(); // идентификатор чата

        switch (messageText) {
            case "/start":
                sendWelcomeMessage(chatId);
                break;
            case "/help":
                sendHelpMessage(chatId);
                break;
            case "/weather":
                sendCitySelectionMenu(chatId);
                break;
            default:
                sendUnknownCommandMessage(chatId); // какая-то неизвестная команда
        }
    }

    private void handleCallbackQuery(Update update) { // обработка нажатий на кнопки
        String callbackData = update.getCallbackQuery().getData();
        // данные , переданные в кнопке
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callbackData.startsWith("city_")) {
            String city = callbackData.substring(5);
            executor.submit(() -> { // запускаем в отдельном потоке , чтобы не блокировать
                // поток работы бота
                String weatherInfo = WeatherService.getWeather(city);
                sendText(chatId, weatherInfo); // отправляем данные по погоде в чат
            });
        }
    }

    private void sendWelcomeMessage(long chatId) {
        String message = "🌤️ <b>Привет! Я бот для проверки погоды.</b>\n\n" +
                "Нажмите кнопку ниже, чтобы выбрать город, или введите /help для справки.";
        InlineKeyboardMarkup keyboard = createCitySelectionKeyboard();
        // создание клавиатуры с кликабельными кнопками
        sendHtmlMessage(chatId, message, keyboard);
    }

    private void sendHelpMessage(long chatId) {
        String message = "❓ <b>Справка по боту</b>\n\n" +
                "Доступные команды:\n" +
                "/start - Начальное приветствие\n" +
                "/help - Эта справка\n" +
                "/weather - Выбрать город из списка\n\n" +
                "Просто нажмите /weather, и я покажу меню с городами!";
        sendHtmlMessage(chatId, message, null);
    }

    private void sendCitySelectionMenu(long chatId) {
        String message = "🏙️ <b>Выберите город:</b>";
        InlineKeyboardMarkup keyboard = createCitySelectionKeyboard();
        sendHtmlMessage(chatId, message, keyboard);
    }

    private InlineKeyboardMarkup createCitySelectionKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();


        for (String city : WeatherService.fakeWeatherData.keySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(city); // текст на кнопке - название города
            button.setCallbackData("city_" + city); // данные боту при нажатии кнопки
            rows.add(Collections.singletonList(button));
        }

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private void sendUnknownCommandMessage(long chatId) {
        String message = "⚠️ Неизвестная команда.\n\n" +
                "Используйте /help для списка команд.";
        sendText(chatId, message);
    }

    private void sendText(long chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    private void sendHtmlMessage(long chatId, String htmlText, InlineKeyboardMarkup keyboard) {
        try { // для использования эмодзи в сообщениях от бота
            SendMessage message = SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(htmlText)
                    .parseMode("HTML")
                    .build();

            if (keyboard != null) {
                message.setReplyMarkup(keyboard); // прикрепляет кликабельную клавиатуру к сообщению
            }

            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки HTML сообщения: " + e.getMessage());
        }
    }
}