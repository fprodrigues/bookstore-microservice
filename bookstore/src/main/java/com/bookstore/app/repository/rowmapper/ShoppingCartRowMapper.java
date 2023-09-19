package com.bookstore.app.repository.rowmapper;

import com.bookstore.app.domain.ShoppingCart;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ShoppingCart}, with proper type conversions.
 */
@Service
public class ShoppingCartRowMapper implements BiFunction<Row, String, ShoppingCart> {

    private final ColumnConverter converter;

    public ShoppingCartRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ShoppingCart} stored in the database.
     */
    @Override
    public ShoppingCart apply(Row row, String prefix) {
        ShoppingCart entity = new ShoppingCart();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPurchaseDate(converter.fromRow(row, prefix + "_purchase_date", Instant.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        return entity;
    }
}
