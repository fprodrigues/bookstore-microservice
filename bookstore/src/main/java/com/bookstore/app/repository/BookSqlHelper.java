package com.bookstore.app.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BookSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("author", table, columnPrefix + "_author"));
        columns.add(Column.aliased("publication_year", table, columnPrefix + "_publication_year"));
        columns.add(Column.aliased("genre", table, columnPrefix + "_genre"));
        columns.add(Column.aliased("price", table, columnPrefix + "_price"));
        columns.add(Column.aliased("quantity_in_stock", table, columnPrefix + "_quantity_in_stock"));

        columns.add(Column.aliased("shopping_carts_id", table, columnPrefix + "_shopping_carts_id"));
        return columns;
    }
}
