package com.example.dartmobileapp;

import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import org.hamcrest.Matcher;

public class EspressoTestMatchers {

    /**
     * Метод для безопасного ввода текста в TextInputEditText и другие поля ввода
     */
    public static ViewAction safeTypeText(final String text) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isDisplayed();
            }

            @Override
            public String getDescription() {
                return "safe type text: " + text;
            }

            @Override
            public void perform(UiController uiController, View view) {
                // Сначала очищаем поле
                if (view instanceof android.widget.EditText) {
                    ((android.widget.EditText) view).setText("");
                }
                
                // Убедимся, что все прошлые операции завершены
                uiController.loopMainThreadUntilIdle();
                
                // Добавляем задержку
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Вводим текст
                if (view instanceof android.widget.EditText) {
                    ((android.widget.EditText) view).setText(text);
                }
                
                // Ждем завершения ввода
                uiController.loopMainThreadUntilIdle();
            }
        };
    }
} 