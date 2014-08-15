package com.jodygeers.contentprovider.app;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jodygeers.contentprovider.model.Books;
import com.jodygeers.contentprovider.provider.BooksProvider;

import java.util.ArrayList;


public class MainActivity extends Activity {

    /**
     * Bootstrap
     * @param savedInstanceState
     */
    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

    }

    /**
     * UI - Add Book
     * @param view
     */
    public void handleAddTitle( View view ) {

        // get data from ui
        ContentValues values = new ContentValues();
        values.put( BooksProvider.TITLE, ( ( EditText ) findViewById( R.id.editTextTitle ) ).getText().toString() );
        values.put( BooksProvider.ISBN, ( ( EditText ) findViewById( R.id.editTextIsbn ) ).getText().toString() );

        // update db
        Uri uri = getContentResolver().insert( BooksProvider.CONTENT_URI, values );

        // update ui
        Toast.makeText( getBaseContext(), uri.toString(), Toast.LENGTH_LONG ).show();

    }

    /**
     * UI - Get Books
     * @param view
     */
    public void handleRetrieveTitles( View view ) {

        // get data from provider
        Uri allTitles = Uri.parse( "content://com.jodygeers.provider.Books/books" );

        Cursor cursor;
        if ( android.os.Build.VERSION.SDK_INT < 11 ) {

            cursor = managedQuery( allTitles, null, null, null, "title desc" );

        } else {

            CursorLoader cursorLoader = new CursorLoader(
                    this,
                    allTitles, null, null, null,
                    "title desc"
            );
            cursor = cursorLoader.loadInBackground();

        }

        // turn cursor into model obj's
        ArrayList<Books> returnListOfBooks = BooksProvider.cursorToModel( cursor );

        // update UI
        System.out.println( "****************************************" );
        System.out.println( "*********  BOOKS MainActivity **********" );
        System.out.println( "****************************************" );
        for ( Books book : returnListOfBooks ) {

            System.out.println(
                    "_id:" + book.get_id() + ", "
                            + "title:" + book.getTitle() + ", "
                            + "isbn:" + book.getIsbn()
            );

        }

    }

}