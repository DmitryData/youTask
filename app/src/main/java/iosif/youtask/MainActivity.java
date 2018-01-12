package iosif.youtask;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

    private ArrayList<String> listData = new ArrayList<>();  //освной массив, передаваемый адаптеру
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

        // получаем элемент ListView
        listView = (ListView) findViewById(R.id.list);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        //создаем контекстное меню
        registerForContextMenu(button);
        // создаем адаптер
        dataAdapter = new DataAdapter(this, R.layout.iosif, listData);
        // устанавливаем адаптер
        listView.setAdapter(dataAdapter);
        loadText();
        // создаем объект для управления БД
        dbHelper = new DBHelper(this);
        // подключаемся к БД
        sqLiteDatabase = dbHelper.getWritableDatabase();
        // Берем все данные из таблицы mytable и помещаем в c
        Cursor c = sqLiteDatabase.query("mytable", null, null, null, null, null, null);
        // ставим позицию курсора на первую строку
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {
            // определяем индекс столбцов по имени колонки
            int nameColIndex = c.getColumnIndex("name");
            do {
                if ((c.getString(nameColIndex)) != null) {
                    listData.add(c.getString(nameColIndex));
                }
                // переход на следующую строку
                // а если следующей нет, то false - выходим из цикла
            } while (c.moveToNext());
        } else {
        c.close();
        }

                button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = editText.getText().toString();
                listData.add(s);
                dataAdapter.notifyDataSetChanged();
                //переход в конец списка
                listView.setSelection(listData.size());
                ContentValues cv = new ContentValues();
                // вставляем данные
                cv.put("name", s);
                sqLiteDatabase.insert("mytable", null, cv);
                editText.setText("");
            }
        });
    }

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // создаем таблицу с полями
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "name text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // закрываем БД
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
                // удаляем БД
                sqLiteDatabase.delete("mytable", null, null);
                finish();
                break;
        }
        return super.onContextItemSelected(item);

    }
}



