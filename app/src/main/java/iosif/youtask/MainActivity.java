package iosif.youtask;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //create new array data for adapter
    private ArrayList<String> listData = new ArrayList<>();
    DBHelper dbHelper;
    ListView listView;
    Button button;
    EditText editText;
    SQLiteDatabase sqLiteDatabase;
    DataAdapter dataAdapter = null;
    final static String SAVED_TEXT = "saved_text";
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new SecondTask(this).execute();
        // find elements ListView
        listView = (ListView) findViewById(R.id.list);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        //create context menu
        registerForContextMenu(button);
        // create adapter
        dataAdapter = new DataAdapter(this, R.layout.iosif, listData);
        // set adapter
        listView.setAdapter(dataAdapter);
        loadText();


                button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = editText.getText().toString();
                listData.add(s);
                dataAdapter.notifyDataSetChanged();
                //go to list end
                listView.setSelection(listData.size());
                ContentValues cv = new ContentValues();
                // put data
                cv.put("name", s);
                sqLiteDatabase.insert("mytable", null, cv);
                editText.setText("");
            }
        });
    }
        @Override
    protected void onDestroy() {
        super.onDestroy();
        // close BD
        dbHelper.close();
        }

    void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, editText.getText().toString());
        ed.commit();
        Toast.makeText(getBaseContext(), "Text saved", Toast.LENGTH_SHORT).show();
    }

    void loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_TEXT, "");
        editText.setText(savedText);
        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        saveText();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button:
                menu.add(0, 1, 0, "Clear and Quit");
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
                case 1:
                // delete BD
                sqLiteDatabase.delete("mytable", null, null);
                finish();
                break;
        }
        return super.onContextItemSelected(item);
    }


    class SecondTask extends AsyncTask<Void, Integer, Void> {
        Context context;

        public SecondTask(Context context) {
            this.context = context;
        }
        @Override


        protected Void doInBackground(Void... unused) {
            // create object for manage BD
            dbHelper = new DBHelper(context);
            // connect to BD
            sqLiteDatabase = dbHelper.getWritableDatabase();
            // get data from from table mytable
            Cursor c = sqLiteDatabase.query("mytable", null, null, null, null, null, null);
            // put position cursor on first line
            // if in table, not rows return false
            if (c.moveToFirst()) {
                // set index of columns by column name
                int nameColIndex = c.getColumnIndex("name");
                do {
                    if ((c.getString(nameColIndex)) != null) {
                        listData.add(c.getString(nameColIndex));
                    }
                    // jump on nexy rows
                    // and if next rows not - false - end cycle
                } while (c.moveToNext());
                c.close();
            } else {
                c.close();
            }
            return(null);
        }



        @Override
        protected void onProgressUpdate(Integer... items) {
        }


        @Override
        protected void onPostExecute(Void unused) {
            Toast.makeText(getApplicationContext(), "Задача завершена", Toast.LENGTH_SHORT)
                    .show();
        }
    }

}



