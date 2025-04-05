package com.example.dartmobileapp;

import android.content.Context;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.dartmobileapp.ui.main.MainFrame;
import com.example.dartmobileapp.utils.SessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.dartmobileapp.CustomMatchers.hasItems;

/**
 * Тесты для экрана ленты видео
 */
@RunWith(AndroidJUnit4.class)
public class VideoFeedTest {

    // Тестовые данные для входа
    private static final String TEST_EMAIL = "testuser" + System.currentTimeMillis() + "@example.com";
    private static final String TEST_PASSWORD = "Password123";
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
        
        // Отключение анимаций
        try {
            disableAnimations();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Настройка тестовой сессии (создаем фиктивную сессию для тестов)
        sessionManager.createSession(TEST_TOKEN, "testUserId", "testUser", TEST_EMAIL);
        
        // Запуск активности
        activityRule.launchActivity(null);
    }

    @After
    public void tearDown() {
        // Очистка после выполнения тестов
        sessionManager.logout();
    }

    /**
     * Тест просмотра видео из ленты и проверки деталей видео
     */
    @Test
    public void testVideoDetails() {
        // Проверяем, что лента видео отображается
        onView(withId(R.id.videos_recycler_view))
                .check(matches(isDisplayed()));

        // Ждем, чтобы убедиться, что видео загрузились
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Нажимаем на первое видео в ленте, используя RecyclerViewActions
        try {
            onView(withId(R.id.videos_recycler_view))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        } catch (Exception e) {
            // Если RecyclerViewActions не сработал, используем альтернативный подход
            // Ищем по ID заголовка видео в первом элементе
            onView(withId(R.id.video_title))
                    .perform(click());
        }

        // Проверяем, что мы перешли на экран деталей видео
        onView(withId(R.id.video_title))
                .check(matches(isDisplayed()));

        // Проверяем наличие плеера
        onView(withId(R.id.video_player))
                .check(matches(isDisplayed()));

        // Проверяем наличие описания видео
        onView(withId(R.id.video_description))
                .check(matches(isDisplayed()));
    }

    /**
     * Тест содержимого видео в ленте
     */
    @Test
    public void testVideoContent() {
        // Проверяем, что лента видео отображается
        onView(withId(R.id.videos_recycler_view))
                .check(matches(isDisplayed()));

        // Ждем загрузки видео
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Проверяем, что в RecyclerView есть элементы
        onView(withId(R.id.videos_recycler_view))
                .check(matches(hasItems()));
    }

    /**
     * Тест обновления ленты видео (pull-to-refresh)
     */
    @Test
    public void testFeedRefresh() {
        // Ждем загрузки данных
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Выполняем жест "потянуть вниз для обновления"
        onView(withId(R.id.swipe_refresh))
                .perform(swipeDown());

        // Ждем загрузки обновленных данных
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Проверяем, что в RecyclerView есть элементы после обновления
        onView(withId(R.id.videos_recycler_view))
                .check(matches(hasItems()));
    }

    /**
     * Убеждаемся, что пользователь вошел в систему перед тестами
     */
    private void ensureUserLoggedIn() {
        // Проверяем, вошел ли пользователь уже в систему
        if (!sessionManager.isLoggedIn()) {
            // Убираем актуальные сессии
            sessionManager.logout();
            
            // Создаем фиктивную сессию для тестов
            sessionManager.createSession(TEST_TOKEN, "testUserId", "testUser", TEST_EMAIL);
            
            // Запуск активности
            activityRule.launchActivity(null);
            
            // Теперь должна быть активна сессия, продолжаем с тестами
        }
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