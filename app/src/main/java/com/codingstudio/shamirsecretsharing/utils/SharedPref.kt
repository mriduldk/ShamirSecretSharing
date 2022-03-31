package com.codingstudio.shamirsecretsharing.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPref {

    private lateinit var sharedpreferences: SharedPreferences

    fun setInt(context: Context, key: String?, value: Int) {

        sharedpreferences = context.getSharedPreferences(Constant.MyPREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedpreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun setString(context: Context, key: String?, value: String?) {

        sharedpreferences = context.getSharedPreferences(
            Constant.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        val editor = sharedpreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setBoolean(context: Context, key: String?, value: Boolean) {
        sharedpreferences = context.getSharedPreferences(
            Constant.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        val editor = sharedpreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBooleanPref(context: Context, key: String?): Boolean {
        sharedpreferences = context.getSharedPreferences(
            Constant.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        return sharedpreferences.getBoolean(key, false)
    }

    fun getStringPref(context: Context, key: String?): String? {
        sharedpreferences = context.getSharedPreferences(
            Constant.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        return sharedpreferences.getString(key, "")
    }

    fun getIntPref(context: Context, key: String?): Int {
        sharedpreferences = context.getSharedPreferences(
            Constant.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        return sharedpreferences.getInt(key, 0)
    }

    fun getUserID(context: Context): String? {
        sharedpreferences = context.getSharedPreferences(
            Constant.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        return sharedpreferences.getString(Constant.USER_ID, "")
    }

    fun setUserID(context: Context, userId: String) {
        sharedpreferences = context.getSharedPreferences(
            Constant.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        val editor = sharedpreferences.edit()
        editor.putString(Constant.USER_ID, userId)
        editor.apply()
    }

    fun logoutUser(context: Context) {
        sharedpreferences = context.getSharedPreferences(Constant.MyPREFERENCES, Context.MODE_PRIVATE)

        val editor = sharedpreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun createLoginSession(context: Context, userID: String?, username: String?, firstName: String?, clientToken: String?, roleID: String?) {
        sharedpreferences = context.getSharedPreferences(
            Constant.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        val editor = sharedpreferences.edit()
        // Storing login value as TRUE
        editor.putBoolean(Constant.IS_LOGIN, true)
        // Storing username in pref
        editor.putString(Constant.USER_ID, userID)
        editor.putString(Constant.USERNAME, username)
        editor.putString(Constant.KEY_FIRSTNAME, firstName)
        editor.putString(Constant.KEY_CLIENTTOKEN, clientToken)
        editor.putString(Constant.KEY_ROLEID, roleID)
        // commit changes
        editor.apply()
    }


}