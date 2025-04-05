package com.example.dartmobileapp;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.dartmobileapp.profile.UserProfile;
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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

/**
 * Тесты для функций профиля пользователя
 */
@RunWith(AndroidJUnit4.class)
public class UserProfileTest {

    // Тестовые данные для авторизации
    private static final String TEST_USERNAME = "TestUserProfile";
    private static final String TEST_EMAIL = "testuserprofile@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_NEW_PASSWORD = "newPassword123";

    @Rule
    public ActivityScenarioRule<UserProfile> activityRule =
            new ActivityScenarioRule<>(UserProfile.class);

    @Before
    public void setUp() {
        // Создаем сессию пользователя перед запуском тестов
        SessionManager sessionManager = new SessionManager(InstrumentationRegistry.getInstrumentation().getTargetContext());
        if (!sessionManager.isLoggedIn()) {
            sessionManager.createSession(
                    "test_token",
                    "test_user_id",
                    TEST_USERNAME,
                    TEST_EMAIL
            );
            
            // Сохраняем тестовый пароль для проверок
            sessionManager.updatePassword(TEST_PASSWORD);
        }
    }

    @After
    public void tearDown() {
        // Очистка после тестов не нужна, так как сессию сохраняем
    }

    /**
     * Тест отображения данных профиля
     */
    @Test
    public void testProfileDisplay() {
        // Проверяем, что имя пользователя отображается корректно
        onView(withId(R.id.usernameText))
                .check(matches(withText("@" + TEST_USERNAME)));

        // Проверяем, что email отображается корректно
        onView(withId(R.id.emailtext))
                .check(matches(withText(TEST_EMAIL)));

        // Проверяем, что пароль отображается как звездочки
        onView(withId(R.id.paswordtext))
                .check(matches(withText("••••••••")));
    }

    /**
     * Тест изменения имени пользователя
     */
    @Test
    public void testChangeUsername() {
        // Текущее время для создания уникального имени
        String newUsername = "TestUser" + System.currentTimeMillis();

        // Нажимаем на поле имени пользователя
        onView(withId(R.id.usernameText))
                .perform(click());

        // Ожидаем открытия экрана проверки пароля
        onView(withId(R.id.password_input))
                .check(matches(isDisplayed()))
                .perform(typeText(TEST_PASSWORD), closeSoftKeyboard());

        // Нажимаем кнопку "Дальше"
        onView(withId(R.id.nextButton))
                .perform(click());

        // Ожидаем открытия экрана изменения имени пользователя
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Вводим новое имя пользователя
        onView(ViewMatchers.withId(R.id.usernameChangeInput))
                .check(matches(isDisplayed()))
                .perform(typeText(newUsername), closeSoftKeyboard());

        // Нажимаем кнопку обновления
        onView(withId(R.id.updateButton))
                .perform(click());

        // Проверяем переход на экран успешного обновления
        onView(withText(containsString("успешно изменено")))
                .check(matches(isDisplayed()));

        // Нажимаем кнопку "Дальше" на экране успеха
        onView(withId(R.id.nextButton_btn))
                .perform(click());

        // Проверяем, что новое имя пользователя отображается в профиле
        // (Возможно, нужно снова открыть профиль)
        onView(withId(R.id.profile_icon))
                .perform(click());

        onView(withId(R.id.usernameText))
                .check(matches(withText("@" + newUsername)));
    }

    /**
     * Тест изменения email
     */
    @Test
    public void testChangeEmail() {
        // Текущее время для создания уникального email
        String newEmail = "testemail" + System.currentTimeMillis() + "@example.com";

        // Нажимаем на поле email
        onView(withId(R.id.emailtext))
                .perform(click());

        // Ожидаем открытия экрана проверки пароля
        onView(withId(R.id.password_input))
                .check(matches(isDisplayed()))
                .perform(typeText(TEST_PASSWORD), closeSoftKeyboard());

        // Нажимаем кнопку "Дальше"
        onView(withId(R.id.nextButton))
                .perform(click());

        // Ожидаем открытия экрана изменения email
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Вводим новый email
        onView(withId(R.id.emailChangeInput))
                .check(matches(isDisplayed()))
                .perform(typeText(newEmail), closeSoftKeyboard());

        // Нажимаем кнопку обновления
        onView(withId(R.id.updateButton))
                .perform(click());

        // Проверяем переход на экран успешного обновления
        onView(withText(containsString("успешно изменена")))
                .check(matches(isDisplayed()));

        // Нажимаем кнопку "Дальше" на экране успеха
        onView(withId(R.id.nextButton_btn))
                .perform(click());

        // Проверяем, что новый email отображается в профиле
        onView(withId(R.id.profile_icon))
                .perform(click());

        onView(withId(R.id.emailtext))
                .check(matches(withText(newEmail)));
    }

    /**
     * Тест изменения пароля
     */
    @Test
    public void testChangePassword() {
        // Нажимаем на поле пароля
        onView(withId(R.id.paswordtext))
                .perform(click());

        // Ожидаем открытия экрана изменения пароля
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Вводим текущий пароль
        onView(withId(R.id.currentpassword_input))
                .check(matches(isDisplayed()))
                .perform(typeText(TEST_PASSWORD), closeSoftKeyboard());

        // Вводим новый пароль
        onView(withId(R.id.newpassword_input))
                .check(matches(isDisplayed()))
                .perform(typeText(TEST_NEW_PASSWORD), closeSoftKeyboard());

        // Подтверждаем новый пароль
        onView(withId(R.id.newpassworconfirm_input))
                .check(matches(isDisplayed()))
                .perform(typeText(TEST_NEW_PASSWORD), closeSoftKeyboard());

        // Нажимаем кнопку обновления
        onView(withId(R.id.updateButton))
                .perform(click());

        // Проверяем переход на экран успешного обновления
        onView(withText(containsString("пароль был успешно")))
                .check(matches(isDisplayed()));

        // Обновляем тестовый пароль в SessionManager для будущих тестов
        SessionManager sessionManager = new SessionManager(InstrumentationRegistry.getInstrumentation().getTargetContext());
        sessionManager.updatePassword(TEST_NEW_PASSWORD);
    }

    /**
     * Тест выхода из аккаунта
     */
    @Test
    public void testLogout() {
        // Нажимаем кнопку выхода
        onView(withId(R.id.logout_button))
                .perform(click());

        // Проверяем, что мы вернулись на экран входа/регистрации
        onView(withId(R.id.loginButton))
                .check(matches(isDisplayed()));

        onView(withId(R.id.signupButton))
                .check(matches(isDisplayed()));
    }
} 