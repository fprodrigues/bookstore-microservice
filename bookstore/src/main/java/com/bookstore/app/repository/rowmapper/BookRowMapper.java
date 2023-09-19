package com.bookstore.app.repository.rowmapper;

import com.bookstore.app.domain.Book;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Book}, with proper type conversions.
 */
@Service
public class BookRowMapper implements BiFunction<Row, String, Book> {

    private final ColumnConverter converter;

    public BookRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Book} stored in the database.
     */
    @Override
    public Book apply(Row row, String prefix) {
        Book entity = new Book();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setAuthor(converter.fromRow(row, prefix + "_author", String.class));
        entity.setPublicationYear(converter.fromRow(row, prefix + "_publication_year", Integer.class));
        entity.setGenre(converter.fromRow(row, prefix + "_genre", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Double.class));
        entity.setQuantityInStock(converter.fromRow(row, prefix + "_quantity_in_stock", Integer.class));
        entity.setShoppingCartsId(converter.fromRow(row, prefix + "_shopping_carts_id", Long.class));
        return entity;
    }
}
