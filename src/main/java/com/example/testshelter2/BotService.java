package com.example.testshelter2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.example.testshelter2.BotConstants.*;

@Slf4j
@Component
public class BotService extends TelegramLongPollingBot{

    final BotConfiguration botConfiguration;

    private final InlineKeyboardMaker inlineKeyboardMaker;

    public BotService(BotConfiguration botConfiguration, InlineKeyboardMaker inlineKeyboardMaker){
        this.botConfiguration=botConfiguration;
        this.inlineKeyboardMaker = inlineKeyboardMaker;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/callVolunteer", "Позвать волонтера"));
        listOfCommands.add(new BotCommand("/requestCall", "Перезвоните мне"));
        listOfCommands.add(new BotCommand("/Start", "Запуск"));
        listOfCommands.add(new BotCommand("/data", "Мои данные"));
        listOfCommands.add(new BotCommand("/deleteData", "Удалить мои данные"));
        listOfCommands.add(new BotCommand("/help", "Справка"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
        //log.error("Command list error");
        }
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText){
                case INITIAL_CMD:
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                default: sendMessage(chatId, "Sorry, no such command");
            }
        }
        else if(update.hasCallbackQuery()){
                String messageData = update.getCallbackQuery().getData();
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                switch (messageData) {

                    case FINAL_CMD:
                        endCommandReceived(chatId) ;
                        break;
                    default:
                        sendMessage(chatId, "Sorry, no such Bottom");
                }
        }
    }


    private void startCommandReceived(long chatId, String name){
        String answer = name + GREETING_MSG;
        log.info("Replied to user: " + name);
        //sendMessage(chatId,answer);
        sendMessage1(chatId,answer);
    }

    private void endCommandReceived(long chatId){
        log.info("Replied to user: " );
        sendMessage(chatId, " Ну типа пока");

    }
    private void sendMessage1(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtons());
        try {
            execute(message);
        }
        catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        }
        catch (TelegramApiException e){
          log.error("Error occurred: " + e.getMessage());
        }
    }


}
