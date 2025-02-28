package com.example.sanhaijing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StoryApp.db";
    private static final int DATABASE_VERSION = 3;

    // Table names
    private static final String TABLE_USERS = "users";
    public static final String TABLE_STORIES = "stories";
    private static final String TABLE_VISIT_STATS = "visit_stats";

    // User table columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Story table columns
    public static final String COLUMN_STORY_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DESCRIBE = "describe";
    public static final String COLUMN_TYPE = "type";

    private static final String COLUMN_VISIT_ID = "visit_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_VISIT_DATE = "visit_date";
    private static final String COLUMN_VISIT_COUNT = "visit_count";

    // Additional table names
    private static final String TABLE_FAVORITES = "favorites";
    private static final String TABLE_HISTORY = "history";

    // Favorites table columns
    private static final String COLUMN_FAVORITE_ID = "favorite_id";
    private static final String COLUMN_FAVORITE_DATE = "favorite_date";

    // History table columns
    private static final String COLUMN_HISTORY_ID = "history_id";
    private static final String COLUMN_HISTORY_DATE = "history_date";

    private static final String TABLE_FEEDBACK = "feedback";

    // Add feedback table columns
    private static final String COLUMN_FEEDBACK_ID = "feedback_id";
    private static final String COLUMN_FEEDBACK_CONTENT = "content";
    private static final String COLUMN_FEEDBACK_EMAIL = "email";
    private static final String COLUMN_FEEDBACK_DATE = "feedback_date";
    private static final String COLUMN_FEEDBACK_STATUS = "status";

    // Create feedback table query
    private static final String CREATE_FEEDBACK_TABLE = "CREATE TABLE " + TABLE_FEEDBACK + "("
            + COLUMN_FEEDBACK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER,"
            + COLUMN_FEEDBACK_CONTENT + " TEXT,"
            + COLUMN_FEEDBACK_EMAIL + " TEXT,"
            + COLUMN_FEEDBACK_DATE + " TEXT,"
            + COLUMN_FEEDBACK_STATUS + " INTEGER DEFAULT 0"
            + ")";

    // Create favorites table query
    private static final String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
            + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER,"
            + COLUMN_STORY_ID + " INTEGER,"
            + COLUMN_FAVORITE_DATE + " TEXT,"
            + "UNIQUE(" + COLUMN_USER_ID + ", " + COLUMN_STORY_ID + ")"
            + ")";

    // Create history table query
    private static final String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
            + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER,"
            + COLUMN_STORY_ID + " INTEGER,"
            + COLUMN_HISTORY_DATE + " TEXT"
            + ")";


    // Create users table query
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT,"
            + COLUMN_EMAIL + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT"
            + ")";

    // Create stories table query
    private static final String CREATE_STORIES_TABLE = "CREATE TABLE " + TABLE_STORIES + "("
            + COLUMN_STORY_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_IMAGE + " TEXT,"
            + COLUMN_DESCRIBE + " TEXT,"
            + COLUMN_TYPE + " TEXT"
            + ")";

    // Create visit stats table query
    private static final String CREATE_VISIT_STATS_TABLE = "CREATE TABLE " + TABLE_VISIT_STATS + "("
            + COLUMN_VISIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER,"
            + COLUMN_STORY_ID + " INTEGER,"
            + COLUMN_VISIT_DATE + " TEXT,"
            + COLUMN_VISIT_COUNT + " INTEGER,"
            + "UNIQUE(" + COLUMN_USER_ID + ", " + COLUMN_STORY_ID + ", " + COLUMN_VISIT_DATE + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_STORIES_TABLE);
        db.execSQL(CREATE_VISIT_STATS_TABLE);
        db.execSQL(CREATE_FAVORITES_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
        db.execSQL(CREATE_FEEDBACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add new tables for version 2
            db.execSQL(CREATE_FAVORITES_TABLE);
            db.execSQL(CREATE_HISTORY_TABLE);
        }
        if(oldVersion <3){
            db.execSQL(CREATE_FEEDBACK_TABLE);
        }
    }

    // User related methods
    public long addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return userId;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERNAME},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        String userName = "";
        if (cursor != null && cursor.moveToFirst()) {
            userName = cursor.getString(0);
            cursor.close();
        }
        db.close();
        return userName;
    }

    // Story related methods
    public void insertStoryData(String jsonData) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            JSONArray categories = new JSONArray(jsonData);

            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                String categoryName = category.getString("category");
                JSONArray creatures = category.getJSONArray("creatures");

                for (int j = 0; j < creatures.length(); j++) {
                    JSONObject story = creatures.getJSONObject(j);

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_STORY_ID, story.getInt("id"));
                    values.put(COLUMN_NAME, story.getString("name"));
                    values.put(COLUMN_IMAGE, story.getString("image"));
                    values.put(COLUMN_DESCRIBE, story.getString("describe"));
                    values.put(COLUMN_TYPE, categoryName);

                    db.insertWithOnConflict(TABLE_STORIES, null, values,
                            SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Updated Story retrieval methods

    public List<Story> getAllStories() {
        List<Story> stories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_STORY_ID,
                COLUMN_NAME,
                COLUMN_IMAGE,
                COLUMN_DESCRIBE,
                COLUMN_TYPE
        };

        Cursor cursor = db.query(TABLE_STORIES, columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_STORY_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME);
            int imageIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE);
            int describeIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIBE);
            int typeIndex = cursor.getColumnIndexOrThrow(COLUMN_TYPE);

            do {
                Story story = new Story(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getString(imageIndex),
                        cursor.getString(describeIndex),
                        cursor.getString(typeIndex)
                );
                stories.add(story);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return stories;
    }

    public Story getStoryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_STORY_ID,
                COLUMN_NAME,
                COLUMN_IMAGE,
                COLUMN_DESCRIBE,
                COLUMN_TYPE
        };

        Cursor cursor = db.query(TABLE_STORIES, columns,
                COLUMN_STORY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        Story story = null;
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_STORY_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME);
            int imageIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE);
            int describeIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIBE);
            int typeIndex = cursor.getColumnIndexOrThrow(COLUMN_TYPE);

            story = new Story(
                    cursor.getInt(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(imageIndex),
                    cursor.getString(describeIndex),
                    cursor.getString(typeIndex)
            );
        }

        if (cursor != null) {
            cursor.close();
        }

        return story;
    }

    public boolean isStoriesTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_STORIES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }

    public void recordStoryVisit(int userId, int storyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        try {
            // Try to update existing record
            ContentValues values = new ContentValues();
            String[] whereArgs = {
                    String.valueOf(userId),
                    String.valueOf(storyId),
                    today
            };

            // Check if record exists
            Cursor cursor = db.query(TABLE_VISIT_STATS,
                    new String[]{COLUMN_VISIT_COUNT},
                    COLUMN_USER_ID + "=? AND " + COLUMN_STORY_ID + "=? AND " + COLUMN_VISIT_DATE + "=?",
                    whereArgs,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Update existing record
                int currentCount = cursor.getInt(0);
                values.put(COLUMN_VISIT_COUNT, currentCount + 1);
                db.update(TABLE_VISIT_STATS, values,
                        COLUMN_USER_ID + "=? AND " + COLUMN_STORY_ID + "=? AND " + COLUMN_VISIT_DATE + "=?",
                        whereArgs);
            } else {
                // Insert new record
                values.put(COLUMN_USER_ID, userId);
                values.put(COLUMN_STORY_ID, storyId);
                values.put(COLUMN_VISIT_DATE, today);
                values.put(COLUMN_VISIT_COUNT, 1);
                db.insert(TABLE_VISIT_STATS, null, values);
            }

            if (cursor != null) {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    public int getTodayVisitCount(int userId, int storyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Cursor cursor = db.query(TABLE_VISIT_STATS,
                new String[]{COLUMN_VISIT_COUNT},
                COLUMN_USER_ID + "=? AND " + COLUMN_STORY_ID + "=? AND " + COLUMN_VISIT_DATE + "=?",
                new String[]{String.valueOf(userId), String.valueOf(storyId), today},
                null, null, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return count;
    }

    public List<VisitStat> getUserVisitStats(int userId) {
        List<VisitStat> stats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VISIT_STATS,
                new String[]{COLUMN_STORY_ID, COLUMN_VISIT_DATE, COLUMN_VISIT_COUNT},
                COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, COLUMN_VISIT_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                VisitStat stat = new VisitStat(
                        userId,
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2)
                );
                stats.add(stat);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return stats;
    }

    public List<WeeklyVisitStat> getWeeklyVisitStats(int userId) {
        List<WeeklyVisitStat> weeklyStats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get date for 7 days ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6); // Start from 6 days ago to include today
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String date = sdf.format(calendar.getTime());

            // Query for total visits on this date
            Cursor cursor = db.rawQuery(
                    "SELECT SUM(" + COLUMN_VISIT_COUNT + ") FROM " + TABLE_VISIT_STATS +
                            " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_VISIT_DATE + "=?",
                    new String[]{String.valueOf(userId), date}
            );

            int totalVisits = 0;
            if (cursor != null && cursor.moveToFirst()) {
                totalVisits = cursor.getInt(0);
                cursor.close();
            }

            weeklyStats.add(new WeeklyVisitStat(date, totalVisits));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        db.close();
        return weeklyStats;
    }

    public List<StoryVisitStat> getStoryVisitRanking(int userId) {
        List<StoryVisitStat> ranking = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get total visits for each story
        String query = "SELECT s." + COLUMN_STORY_ID + ", s." + COLUMN_NAME +
                ", SUM(v." + COLUMN_VISIT_COUNT + ") as total_visits" +
                " FROM " + TABLE_STORIES + " s" +
                " INNER JOIN " + TABLE_VISIT_STATS + " v" +
                " ON s." + COLUMN_STORY_ID + " = v." + COLUMN_STORY_ID +
                " WHERE v." + COLUMN_USER_ID + "=?" +
                " GROUP BY s." + COLUMN_STORY_ID +
                " ORDER BY total_visits DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                StoryVisitStat stat = new StoryVisitStat(
                        cursor.getInt(0),    // story_id
                        cursor.getString(1),  // story_name
                        cursor.getInt(2)      // total_visits
                );
                ranking.add(stat);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return ranking;
    }

    public void addToFavorites(int userId, int storyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_STORY_ID, storyId);
            values.put(COLUMN_FAVORITE_DATE,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date()));

            db.insertWithOnConflict(TABLE_FAVORITES, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            db.close();
        }
    }

    public void removeFromFavorites(int userId, int storyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_FAVORITES,
                    COLUMN_USER_ID + "=? AND " + COLUMN_STORY_ID + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(storyId)});
        } finally {
            db.close();
        }
    }

    public boolean isFavorite(int userId, int storyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, new String[]{COLUMN_FAVORITE_ID},
                COLUMN_USER_ID + "=? AND " + COLUMN_STORY_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(storyId)},
                null, null, null);

        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        return isFavorite;
    }

    public List<Story> getFavoriteStories(int userId) {
        List<Story> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.* FROM " + TABLE_STORIES + " s"
                + " INNER JOIN " + TABLE_FAVORITES + " f"
                + " ON s." + COLUMN_STORY_ID + " = f." + COLUMN_STORY_ID
                + " WHERE f." + COLUMN_USER_ID + "=?"
                + " ORDER BY f." + COLUMN_FAVORITE_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Story story = new Story(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STORY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIBE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                );
                favorites.add(story);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return favorites;
    }

    // History related methods
    public void addToHistory(int userId, int storyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_STORY_ID, storyId);
            values.put(COLUMN_HISTORY_DATE,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date()));

            db.insert(TABLE_HISTORY, null, values);
        } finally {
            db.close();
        }
    }

    public List<Story> getHistoryStories(int userId) {
        List<Story> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.*, MAX(h." + COLUMN_HISTORY_DATE + ") as last_view"
                + " FROM " + TABLE_STORIES + " s"
                + " INNER JOIN " + TABLE_HISTORY + " h"
                + " ON s." + COLUMN_STORY_ID + " = h." + COLUMN_STORY_ID
                + " WHERE h." + COLUMN_USER_ID + "=?"
                + " GROUP BY s." + COLUMN_STORY_ID
                + " ORDER BY last_view DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Story story = new Story(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STORY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIBE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                );
                history.add(story);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return history;
    }

    public void clearHistory(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_HISTORY,
                    COLUMN_USER_ID + "=?",
                    new String[]{String.valueOf(userId)});
        } finally {
            db.close();
        }
    }

    public long submitFeedback(int userId, String content, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_FEEDBACK_CONTENT, content);
        values.put(COLUMN_FEEDBACK_EMAIL, email);
        values.put(COLUMN_FEEDBACK_DATE,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date()));
        values.put(COLUMN_FEEDBACK_STATUS, 0); // 0 = pending, 1 = processed

        long result = db.insert(TABLE_FEEDBACK, null, values);
        db.close();
        return result;
    }

    public List<Feedback> getFeedbackList(int userId) {
        List<Feedback> feedbackList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_FEEDBACK +
                " WHERE " + COLUMN_USER_ID + "=?" +
                " ORDER BY " + COLUMN_FEEDBACK_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Feedback feedback = new Feedback(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_STATUS))
                );
                feedbackList.add(feedback);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return feedbackList;
    }
}