package com.bookstore.app.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.bookstore.app.domain.ShoppingCart;
import com.bookstore.app.repository.rowmapper.OrderRowMapper;
import com.bookstore.app.repository.rowmapper.ShoppingCartRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the ShoppingCart entity.
 */
@SuppressWarnings("unused")
class ShoppingCartRepositoryInternalImpl extends SimpleR2dbcRepository<ShoppingCart, Long> implements ShoppingCartRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final OrderRowMapper orderMapper;
    private final ShoppingCartRowMapper shoppingcartMapper;

    private static final Table entityTable = Table.aliased("shopping_cart", EntityManager.ENTITY_ALIAS);
    private static final Table orderTable = Table.aliased("jhi_order", "e_order");

    public ShoppingCartRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        OrderRowMapper orderMapper,
        ShoppingCartRowMapper shoppingcartMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ShoppingCart.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.orderMapper = orderMapper;
        this.shoppingcartMapper = shoppingcartMapper;
    }

    @Override
    public Flux<ShoppingCart> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ShoppingCart> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ShoppingCartSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(OrderSqlHelper.getColumns(orderTable, "order"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(orderTable)
            .on(Column.create("order_id", entityTable))
            .equals(Column.create("id", orderTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ShoppingCart.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ShoppingCart> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ShoppingCart> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ShoppingCart process(Row row, RowMetadata metadata) {
        ShoppingCart entity = shoppingcartMapper.apply(row, "e");
        entity.setOrder(orderMapper.apply(row, "order"));
        return entity;
    }

    @Override
    public <S extends ShoppingCart> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
