package com.jodygeers.contentprovider.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.jodygeers.contentprovider.model.Books;
import com.jodygeers.contentprovider.sqlite.BooksHelper;

import java.util.ArrayList;
import java.util.List;


public class BooksProvider extends ContentProvider {


    public static final String PROVIDER_NAME = "com.jodygeers.provider.Books";

    public static final Uri CONTENT_URI = Uri.parse( "content://" + PROVIDER_NAME + "/books" );

    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String ISBN = "isbn";

    private static final int BOOKS = 1;
    private static final int BOOK_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
        uriMatcher.addURI( PROVIDER_NAME, "books", BOOKS );
        uriMatcher.addURI( PROVIDER_NAME, "books/#", BOOK_ID );
    }

    // database
    SQLiteDatabase booksDB;

    @Override
    public boolean onCreate() {

        Context context = getContext();

        BooksHelper dbHelper = new BooksHelper( context );

        booksDB = dbHelper.getWritableDatabase();

        return ( booksDB == null )? false : true;

    }

    /**
     * Get data
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {

        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables( BooksHelper.DATABASE_TABLE );

        // single book request
        if ( uriMatcher.match(uri) == BOOK_ID )
            sqlBuilder.appendWhere( _ID + " = " + uri.getPathSegments().get( 1 ) );

        if ( sortOrder == null || sortOrder == "" )
            sortOrder = TITLE;

        // get data update uri
        Cursor cursor = sqlBuilder.query(
                booksDB,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        cursor.setNotificationUri( getContext().getContentResolver(), uri );

        return cursor;

    }

    /**
     * Requests
     * @param uri
     * @return
     */
    @Override
    public String getType( Uri uri ) {

        switch ( uriMatcher.match( uri ) ){

            case BOOKS:
                // get all
                return "vnd.android.cursor.dir/vnd.jodygeers.books ";

            case BOOK_ID:
                // get single by id
                return "vnd.android.cursor.item/vnd.jodygeers.books ";

            default:
                throw new IllegalArgumentException( "Unsupported URI: " + uri );

        }

    }

    /**
     * Post and save data
     * @param uri
     * @param contentValues
     * @return
     */
    @Override
    public Uri insert( Uri uri, ContentValues contentValues ) {

        // add a new book
        long rowID = booksDB.insert( BooksHelper.DATABASE_TABLE, "", contentValues );

        // if added successfully
        if ( rowID > 0 ) {
            Uri _uri = ContentUris.withAppendedId( CONTENT_URI, rowID );
            getContext().getContentResolver().notifyChange( _uri, null );
            return _uri;
        }

        System.out.println( "Failed to insert row into " + uri );
        return null;

    }

    /**
     * Delete data
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete( Uri uri, String selection, String[] selectionArgs ) {

        int count=0;
        switch ( uriMatcher.match( uri ) ) {

            case BOOKS:
                count = booksDB.delete( BooksHelper.DATABASE_TABLE, selection, selectionArgs );
                break;

            case BOOK_ID:
                String id = uri.getPathSegments().get(1);
                count = booksDB.delete( BooksHelper.DATABASE_TABLE, _ID + " = " + id + ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ")" : ""), selectionArgs );
                break;

            default:
                throw new IllegalArgumentException( "Unknown URI " + uri );

        }

        getContext().getContentResolver().notifyChange( uri, null );

        return count;

    }

    /**
     * Update data
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update( Uri uri, ContentValues contentValues, String selection, String[] selectionArgs ) {

        int count = 0;
        switch ( uriMatcher.match( uri ) ) {

            case BOOKS:
                count = booksDB.update(
                        BooksHelper.DATABASE_TABLE,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;

            case BOOK_ID:
                count = booksDB.update(
                        BooksHelper.DATABASE_TABLE,
                        contentValues,
                        _ID + " = " + uri.getPathSegments().get( 1 ) + ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ")" : "" ),
                        selectionArgs
                );
                break;

            default:
                throw new IllegalArgumentException( "Unknown URI " + uri );

        }
        getContext().getContentResolver().notifyChange( uri, null );

        return count;
    }

    /**
     * Turn provider cursor into Model Obj List
     * @param cursor
     * @return
     */
    public static ArrayList<Books> cursorToModel( Cursor cursor ) {

        ArrayList<Books> returnListOfBooks = new ArrayList<Books>();

        for ( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() ) {

            Books book = new Books();
            book.set_id( cursor.getInt( cursor.getColumnIndex( BooksProvider._ID ) ) );
            book.setTitle( cursor.getString( cursor.getColumnIndex( BooksProvider.TITLE ) ) );
            book.setIsbn( cursor.getString( cursor.getColumnIndex( BooksProvider.ISBN ) ) );

            returnListOfBooks.add( book );

        }

        return returnListOfBooks;
    }

}
