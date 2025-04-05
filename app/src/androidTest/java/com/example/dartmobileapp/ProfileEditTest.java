package com.example.dartmobileapp;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.dartmobileapp.profile.edit.ChangePassword;
import com.example.dartmobileapp.profile.UserProfile;
import com.example.dartmobileapp.ui.main.MainFrame;
import com.example.dartmobileapp.utils.SessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Тесты для экранов редактирования профиля
 */
@RunWith(AndroidJUnit4.class)
public class ProfileEditTest {

    // Тестовые данные
    private static final String TEST_EMAIL = "testuser" + System.currentTimeMillis() + "@example.com";
    private static final String TEST_PASSWORD = "Password123";
    private static final String TEST_USERNAME = "testUser" + System.currentTimeMillis();
    private static final String NEW_USERNAME = "newUsername" + System.currentTimeMillis();
    private static final String NEW_EMAIL = "newemail" + System.currentTimeMillis() + "@example.com";
    private static final String NEW_PASSWORD = "NewPassword123";
    private static final String TEST_TOKEN = "test_token"; // Добавляем токен для createSession

    // Контекст приложения
    private Context appContext;
    private SessionManager sessionManager;

    @Rule
    public ActivityTestRule<MainFrame> activityRule = 
            new ActivityTestRule<>(MainFrame.class, true, false);

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sessionManager = new SessionManager(appContext);
        
        // Очистка сессии перед каждым тестом
        sessionManager.logout();
        
        // Отключение анимаций (без использования ServiceManager)
        try {
            disableAnimations();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Настройка тестовой сессии (добавляем токен)
        sessionManager.createSession(TEST_TOKEN, "testUserId", TEST_USERNAME, TEST_EMAIL);
        
        // Запуск активности
        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        // Очистка после выполнения тестов
        sessionManager.logout();
    }

    /**
     * Тест изменения имени пользователя
     */
    @Test
    public void testChangeUsername() {
        // Перейти на экран профиля
        onView(withId(R.id.profile_icon))
                .perform(click());

        // Нажать на имя пользователя для редактирования
        onView(withId(R.id.usernameText))
                .perform(click());

        // Проверить, что мы перешли на экран верификации пароля
        onView(withId(R.id.password_input))
                .check(matches(isDisplayed()));

        // Ввести пароль для проверки
        onView(withId(R.id.password_input))
                .perform(EspressoTestMatchers.safeTypeText(TEST_PASSWORD), closeSoftKeyboard());

        // Нажать на кнопку подтверждения
        onView(withId(R.id.nextButton))
                .perform(click());

        // Проверить, что мы перешли на экран изменения имени пользователя
        onView(withId(R.id.usernameChangeInput))
                .check(matches(isDisplayed()));

        // Ввести новое имя пользователя
        onView(withId(R.id.usernameChangeInput))
                .perform(EspressoTestMatchers.safeTypeText(NEW_USERNAME), closeSoftKeyboard());

        // Нажать на кнопку обновления
        onView(withId(R.id.updateButton))
                .perform(click());

        // Проверить, что изменения сохранены
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Проверяем, что отобразился экран успешного обновления
        onView(withText(org.hamcrest.Matchers.containsString("успешно изменено")))
                .check(matches(isDisplayed()));

        // Нажимаем на кнопку "Дальше" на экране успеха
        onView(withId(R.id.nextButton_btn))
                .perform(click());

        // Проверить, что имя пользователя обновлено
        onView(withId(R.id.usernameText))
                .check(matches(withText("@" + NEW_USERNAME)));
    }

    /**
     * Тест изменения email
     */
    @Test
    public void testChangeEmail() {
        // Перейти на экран профиля
        onView(withId(R.id.profile_icon))
                .perform(click());

        // Нажать на email для редактирования
        onView(withId(R.id.emailtext))
                .perform(click());

        // Проверить, что мы перешли на экран верификации пароля
        onView(withId(R.id.password_input))
                .check(matches(isDisplayed()));

        // Ввести пароль для проверки
        onView(withId(R.id.password_input))
                .perform(EspressoTestMatchers.safeTypeText(TEST_PASSWORD), closeSoftKeyboard());

        // Нажать на кнопку подтверждения
        onView(withId(R.id.nextButton))
                .perform(click());

        // Проверить, что мы перешли на экран изменения email
        onView(withId(R.id.emailChangeInput))
                .check(matches(isDisplayed()));

        // Ввести новый email
        onView(withId(R.id.emailChangeInput))
                .perform(EspressoTestMatchers.safeTypeText(NEW_EMAIL), closeSoftKeyboard());

        // Нажать на кнопку обновления
        onView(withId(R.id.updateButton))
                .perform(click());

        // Проверить, что изменения сохранены
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Проверяем, что отобразился экран успешного обновления
        onView(withText(org.hamcrest.Matchers.containsString("успешно изменена")))
                .check(matches(isDisplayed()));

        // Нажимаем на кнопку "Дальше" на экране успеха
        onView(withId(R.id.nextButton_btn))
                .perform(click());

        // Проверить, что email обновлен
        onView(withId(R.id.emailtext))
                .check(matches(withText(NEW_EMAIL)));
    }

    /**
     * Тест изменения пароля
     */
    @Test
    public void testChangePassword() {
        // Перейти на экран профиля
        onView(withId(R.id.profile_icon))
                .perform(click());

        // Нажать на поле пароля для редактирования
        onView(withId(R.id.paswordtext))
                .perform(click());

        // Проверить, что мы перешли на экран изменения пароля
        // Вводим текущий пароль
        onView(withId(R.id.currentpassword_input))
                .check(matches(isDisplayed()))
                .perform(EspressoTestMatchers.safeTypeText(TEST_PASSWORD), closeSoftKeyboard());

        // Вводим новый пароль
        onView(withId(R.id.newpassword_input))
                .perform(EspressoTestMatchers.safeTypeText(NEW_PASSWORD), closeSoftKeyboard());

        // Подтверждаем новый пароль
        onView(withId(R.id.newpassworconfirm_input))
                .perform(EspressoTestMatchers.safeTypeText(NEW_PASSWORD), closeSoftKeyboard());

        // Нажать на кнопку обновления
        onView(withId(R.id.updateButton))
                .perform(click());

        // Проверить, что изменения сохранены
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Проверяем, что отобразился экран успешного обновления пароля
        onView(withText(org.hamcrest.Matchers.containsString("пароль был успешно")))
                .check(matches(isDisplayed()));

        // Нажимаем на кнопку "Дальше" на экране успеха
        onView(withId(R.id.nextButton_btn))
                .perform(click());

        // Пароль не отображается в UI, так что мы просто проверяем, что мы вернулись на экран профиля
        onView(withId(R.id.usernameText))
                .check(matches(isDisplayed()));
    }

    /**
     * Отключение анимаций для тестирования
     */
    private void disableAnimations() throws Exception {
        // Безопасное отключение анимаций через системные настройки
        // Для API 28 и выше нужно использовать другой подход
        if (android.os.Build.VERSION.SDK_INT <= 27) {
            try {
                Class<?> windowManagerGlobal = Class.forName("android.view.WindowManagerGlobal");
                Object instance = windowManagerGlobal.getMethod("getInstance").invoke(null);
                
                Class<?> stableClass = instance.getClass();
                android.util.Log.d("Test", "Successfully got window manager class");
            } catch (Exception e) {
                android.util.Log.e("Test", "Error disabling animations: " + e.getMessage());
            }
        }
    }
} 