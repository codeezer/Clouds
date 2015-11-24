package com.ajambari.code.clouds;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText editUsername,editEmail,editPassword,editDetail;
    Button btnAdd,btnViewAll,btnUpdate,btnDelete,btnEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myDb = new DatabaseHelper(this);
        editUsername = (EditText) findViewById(R.id.editText_username);
        editEmail = (EditText) findViewById(R.id.editText_email);
        editPassword = (EditText) findViewById(R.id.editText_password);
        editDetail = (EditText) findViewById(R.id.detail_text);
        btnAdd = (Button) findViewById(R.id.button_add);
        btnViewAll = (Button) findViewById(R.id.button_view);
        btnUpdate = (Button) findViewById(R.id.button_update);
        btnDelete = (Button) findViewById(R.id.button_delete);
        btnEmail = (Button) findViewById(R.id.button_email);
        AddData();
        ViewAllData();
        UpdateData();
        DeleteData();
        SendEmail();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void AddData(){
        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editEmail.getText()==null || editUsername.getText()==null || editPassword.getText()==null){
                            Toast.makeText(DetailActivity.this,"* empty textbox",Toast.LENGTH_LONG).show();
                            return;
                        }
                        boolean isInserted = myDb.insertData(editUsername.getText().toString(),
                                editEmail.getText().toString(),
                                editPassword.getText().toString());
                        if (isInserted == true){
                            Toast.makeText(DetailActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
                            editEmail.setText("");
                            editPassword.setText("");
                            editUsername.setText("");
                        }else{
                            //Toast.makeText(DetailActivity.this,"Ohhh f***",Toast.LENGTH_LONG).show();
                            Snackbar.make(v, "Ohh f***"+"ohhh you did very very bad :D", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    }
                }
        );
    }

    public void DeleteData(){
        btnDelete.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Integer deletedRows = myDb.deleteData(editEmail.getText().toString());
                        if (deletedRows > 0){
                            Toast.makeText(DetailActivity.this,"Deleted",Toast.LENGTH_LONG).show();
                        }else{
                            Snackbar.make(v, "Not Deleted", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void UpdateData(){
        btnUpdate.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        boolean isUpdated = myDb.updateData(editUsername.getText().toString(),
                                editEmail.getText().toString(),
                                editPassword.getText().toString());
                        if(isUpdated == true){
                            Toast.makeText(DetailActivity.this,"Updated email: "+editEmail.getText().toString(),Toast.LENGTH_LONG).show();
                            editEmail.setText("");
                            editPassword.setText("");
                            editUsername.setText("");
                        }else {
                            Toast.makeText(DetailActivity.this, "Error happened", Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
    }

    public void ViewAllData(){
        btnViewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor cResult = myDb.getAllData();
                        if(cResult.getCount()==0){
                            showMessage("Error", "No Data Found");
                            return;
                        }
                        StringBuffer buffer = new StringBuffer();
                        while(cResult.moveToNext()){
                            buffer.append("Id :"+cResult.getString(0)+"\n");
                            buffer.append("Username :"+cResult.getString(1)+"\n");
                            buffer.append("Email :"+cResult.getString(2)+"\n");
                            buffer.append("Password :"+cResult.getString(3)+"\n\n");
                        }
                        showMessage("Data", buffer.toString());
                    }
                }
        );
    }

    protected void SendEmail(){
        btnEmail.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Log.i("Send Email","");
                        String[] TO = {editEmail.getText().toString()};
                        String[] CC = {""};
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);

                        emailIntent.setData(Uri.parse("mailto:"));
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                        emailIntent.putExtra(Intent.EXTRA_CC, CC);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "About Weather");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Weather Details: "+editDetail.getText().toString());
                        try {
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            finish();
                            Log.i("Finished sending email.", "");
                        }
                        catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(DetailActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void showMessage(String title,String message){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        DetailActivityFragment dAf = new DetailActivityFragment();
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(dAf.createShareForecastIntent());
        }else {
            Log.d(dAf.LOG_TAG,"Share Provider is null?");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(this,SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
