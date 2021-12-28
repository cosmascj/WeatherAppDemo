package com.example.myweatherapp

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CallAPILoginAsyncTask().execute()
    }
    private inner class CallAPILoginAsyncTask(): AsyncTask<Any, Void, String>(){

        private lateinit var customDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDiaglog()
        }
        override fun doInBackground(vararg params: Any?): String {

            var result: String

            var connection: HttpURLConnection? = null

            try {
               // val url = URL("https://run.mocky.io/v3/80b46c74-b0de-44ae-8b63-1f5bbe06445f")
                val url = URL("https://run.mocky.io/v2/5e3939193200006700ddf815")
              //  val url = URL("https://pokeapi.co/api/v2")

                connection = url.openConnection() as HttpURLConnection

                // doinput and dooutput are properties that define if we would be getting data or sending data
                connection.doInput = true
                connection.doOutput = true

                val httpResult: Int = connection.responseCode

                if (httpResult == HttpURLConnection.HTTP_OK){

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?

                try {
                    while (reader.readLine().also { line = it }!= null){
                        stringBuilder.append(line + "\n")

                    }

                } catch (e: IOException){
                    e.printStackTrace()
                }finally {
                    try {
                        inputStream.close()
                    }catch (e:IOException){
                        e.printStackTrace()
                    }

                }
                    result = stringBuilder.toString()
            }
                else {
                    result =connection.responseMessage
                }
            } catch (e: SocketTimeoutException){
                result = "Connection Timeout"
            }catch (e: Exception){
                result = "Error: " + e.message
            }finally {
                connection?.disconnect()
            }
return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            cancelProgressDialog()
            Log.i("JSON RESULT", result!!)
            var jsonObject = JSONObject(result!!)
//
//            var message = jsonObject.optString("abilities")
//            Log.i("abilities", message)
            //To access standalone properties of a Json file
            var userId = jsonObject.optString("user_id")
            Log.i("userID", "$userId")

            var email = jsonObject.optString("email")
            Log.i("email", "$email")

            // To assess properties of a particular json object

            var profileDetails = jsonObject.optJSONObject("profile_details")

            var isProfileCompleted = profileDetails.optBoolean("is_profile_completed")
            Log.i("Is Profile Completed", "$isProfileCompleted")

            var rating = profileDetails.optInt("rating")
            Log.i("rating", "$rating")

            /*
            * To access tthe details of a list for a Json object
            *
            * */

            val dataList = jsonObject.optJSONArray("data_list")
            Log.i("dataList", "${dataList.length()}")

            for (item in 0 until dataList.length()){
                Log.i("Value $item", "${dataList[item]}")

                var dataItemObject: JSONObject = dataList[item] as JSONObject
                var id = dataItemObject.optInt("id")
                Log.i("ID", "$id")

            }


        }
        private fun showProgressDiaglog(){
            customDialog= Dialog(this@MainActivity)
            customDialog.setContentView(R.layout.custom_dialog)
            customDialog.show()
        }

        private fun cancelProgressDialog(){
            customDialog.dismiss()
        }

    }
}