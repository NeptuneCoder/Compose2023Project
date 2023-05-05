package com.example.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")


class DataStoreUtil(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_AGE_KEY = intPreferencesKey("user_age")
        private val USER_IS_STUDENT_KEY = booleanPreferencesKey("user_is_student")
        private val USER_GRADE_KEY = floatPreferencesKey("user_grade")
    }

    suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    suspend fun setUserAge(age: Int) {
        dataStore.edit { preferences ->
            preferences[USER_AGE_KEY] = age
        }
    }

    suspend fun setUserIsStudent(isStudent: Boolean) {
        dataStore.edit { preferences ->
            preferences[USER_IS_STUDENT_KEY] = isStudent
        }
    }

    suspend fun setUserGrade(grade: Float) {
        dataStore.edit { preferences ->
            preferences[USER_GRADE_KEY] = grade
        }
    }

    val userNameFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY] ?: ""
        }

    val userAgeFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[USER_AGE_KEY] ?: 0
        }

    val userIsStudentFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[USER_IS_STUDENT_KEY] ?: false
        }

    val userGradeFlow: Flow<Float> = dataStore.data
        .map { preferences ->
            preferences[USER_GRADE_KEY] ?: 0f
        }
}
