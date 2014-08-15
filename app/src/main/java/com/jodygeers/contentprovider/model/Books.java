package com.jodygeers.contentprovider.model;

/**
 * Entity for book
 */
public class Books {

    private int _id;
    private String title;
    private String isbn;

    public int get_id() {
        return _id;
    }

    public void set_id( int _id ) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn( String isbn ) {
        this.isbn = isbn;
    }

}