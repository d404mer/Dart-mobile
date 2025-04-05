package com.example.dartmobileapp;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Кастомные матчеры для Espresso тестов
 */
public class CustomMatchers {

    /**
     * Возвращает матчер для элемента RecyclerView по позиции
     */
    public static Matcher<View> atPosition(final int position, final Matcher<View> itemMatcher) {
        if (itemMatcher == null) {
            throw new NullPointerException("itemMatcher cannot be null");
        }
        
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // Положение недействительно или элемент был прокручен за пределы экрана
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    /**
     * Возвращает матчер для проверки видимости индикатора загрузки
     */
    public static Matcher<View> withProgressVisible(final boolean visible) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with progress visible: " + visible);
            }

            @Override
            protected boolean matchesSafely(View view) {
                return view.getVisibility() == (visible ? View.VISIBLE : View.GONE);
            }
        };
    }

    /**
     * Возвращает матчер для проверки количества элементов в RecyclerView
     */
    public static Matcher<View> withItemCount(final int count) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with item count: " + count);
            }

            @Override
            protected boolean matchesSafely(RecyclerView view) {
                RecyclerView.Adapter adapter = view.getAdapter();
                return adapter != null && adapter.getItemCount() == count;
            }
        };
    }

    /**
     * Возвращает матчер для проверки, что RecyclerView не пустой
     */
    public static Matcher<View> hasItems() {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has items");
            }

            @Override
            protected boolean matchesSafely(RecyclerView view) {
                RecyclerView.Adapter adapter = view.getAdapter();
                return adapter != null && adapter.getItemCount() > 0;
            }
        };
    }

    /**
     * Матчер для поиска элемента с заданным заголовком видео
     */
    public static Matcher<View> withVideoTitle(final String title) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with video title: " + title);
            }

            @Override
            protected boolean matchesSafely(View view) {
                android.widget.TextView titleView = view.findViewById(R.id.video_title);
                if (titleView == null) {
                    return false;
                }
                String viewText = titleView.getText().toString();
                return title.equals(viewText);
            }
        };
    }
} 