package uk.ac.mmu.mypastebinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button buttonSendPaste;
    EditText PasteBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSendPaste = findViewById(R.id.buttonSendPaste);
        PasteBox = findViewById(R.id.editTextPasteText);

        buttonSendPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pastetxt = PasteBox.getText().toString();
                new SendPasteAsync().execute(pastetxt);
                //System.out.println(response);
            }
        });

    }

    public String makePaste(String pasteText) {
        String line, response = "";

        try {
            URL url = new URL("https://pastebin.com/api/api_post.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // connection configuration
            // Time is in milliseconds
            // If it takes longer than 5 seconds send information to the server or receive
            // information then drop out and end the connection
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Because we are sending data first thing we need is a stream writer - lets us
            // send things
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            String reqParams = "api_dev_key="+AppConstants.APIKEY+"&api_option=paste&api_paste_code="+pasteText;
            writer.write(reqParams);

            // bad manners - should always flush the stream
            writer.flush();
            writer.close();
            os.close();

            // was the connection successful?
            int responseCode = connection.getResponseCode();

            // HTTP_OK IS 200 IS A CONSTANT AS IT ALWAYS STAYS THE SAME
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // Talk to the server don't stop until it sends you EOF (end of file) - Get me
                // everything the server is sending back to me
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }

            else {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // Talk to the server don't stop until it sends you EOF (end of file) - Get me
                // everything the server is sending back to me
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("adan", response);

        return response;
    }

    class SendPasteAsync extends AsyncTask<String, Void, String>{

        @Override
        //what we want to pass. <Argument, Update, Return>
        protected String doInBackground(String... strings) {
            //returns index 0
            String pasteString = strings[0];

            return makePaste(pasteString);
        }

        @Override
        protected void onPostExecute(String resp) {
            Toast.makeText(getApplicationContext(), "URL Copied ..", Toast.LENGTH_SHORT).show();

            System.out.println(resp);
        }
    }
}
