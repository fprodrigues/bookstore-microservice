package com.bookstore.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.bookstore.app.IntegrationTest;
import com.bookstore.app.domain.ShoppingCart;
import com.bookstore.app.repository.EntityManager;
import com.bookstore.app.repository.ShoppingCartRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ShoppingCartResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ShoppingCartResourceIT {

    private static final Instant DEFAULT_PURCHASE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PURCHASE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shopping-carts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ShoppingCart shoppingCart;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoppingCart createEntity(EntityManager em) {
        ShoppingCart shoppingCart = new ShoppingCart().purchaseDate(DEFAULT_PURCHASE_DATE).status(DEFAULT_STATUS);
        return shoppingCart;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShoppingCart createUpdatedEntity(EntityManager em) {
        ShoppingCart shoppingCart = new ShoppingCart().purchaseDate(UPDATED_PURCHASE_DATE).status(UPDATED_STATUS);
        return shoppingCart;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ShoppingCart.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        shoppingCart = createEntity(em);
    }

    @Test
    void createShoppingCart() throws Exception {
        int databaseSizeBeforeCreate = shoppingCartRepository.findAll().collectList().block().size();
        // Create the ShoppingCart
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeCreate + 1);
        ShoppingCart testShoppingCart = shoppingCartList.get(shoppingCartList.size() - 1);
        assertThat(testShoppingCart.getPurchaseDate()).isEqualTo(DEFAULT_PURCHASE_DATE);
        assertThat(testShoppingCart.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createShoppingCartWithExistingId() throws Exception {
        // Create the ShoppingCart with an existing ID
        shoppingCart.setId(1L);

        int databaseSizeBeforeCreate = shoppingCartRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkPurchaseDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = shoppingCartRepository.findAll().collectList().block().size();
        // set the field null
        shoppingCart.setPurchaseDate(null);

        // Create the ShoppingCart, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = shoppingCartRepository.findAll().collectList().block().size();
        // set the field null
        shoppingCart.setStatus(null);

        // Create the ShoppingCart, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllShoppingCartsAsStream() {
        // Initialize the database
        shoppingCartRepository.save(shoppingCart).block();

        List<ShoppingCart> shoppingCartList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ShoppingCart.class)
            .getResponseBody()
            .filter(shoppingCart::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(shoppingCartList).isNotNull();
        assertThat(shoppingCartList).hasSize(1);
        ShoppingCart testShoppingCart = shoppingCartList.get(0);
        assertThat(testShoppingCart.getPurchaseDate()).isEqualTo(DEFAULT_PURCHASE_DATE);
        assertThat(testShoppingCart.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void getAllShoppingCarts() {
        // Initialize the database
        shoppingCartRepository.save(shoppingCart).block();

        // Get all the shoppingCartList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(shoppingCart.getId().intValue()))
            .jsonPath("$.[*].purchaseDate")
            .value(hasItem(DEFAULT_PURCHASE_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS));
    }

    @Test
    void getShoppingCart() {
        // Initialize the database
        shoppingCartRepository.save(shoppingCart).block();

        // Get the shoppingCart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, shoppingCart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(shoppingCart.getId().intValue()))
            .jsonPath("$.purchaseDate")
            .value(is(DEFAULT_PURCHASE_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS));
    }

    @Test
    void getNonExistingShoppingCart() {
        // Get the shoppingCart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingShoppingCart() throws Exception {
        // Initialize the database
        shoppingCartRepository.save(shoppingCart).block();

        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().collectList().block().size();

        // Update the shoppingCart
        ShoppingCart updatedShoppingCart = shoppingCartRepository.findById(shoppingCart.getId()).block();
        updatedShoppingCart.purchaseDate(UPDATED_PURCHASE_DATE).status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedShoppingCart.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedShoppingCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
        ShoppingCart testShoppingCart = shoppingCartList.get(shoppingCartList.size() - 1);
        assertThat(testShoppingCart.getPurchaseDate()).isEqualTo(UPDATED_PURCHASE_DATE);
        assertThat(testShoppingCart.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().collectList().block().size();
        shoppingCart.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shoppingCart.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().collectList().block().size();
        shoppingCart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().collectList().block().size();
        shoppingCart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateShoppingCartWithPatch() throws Exception {
        // Initialize the database
        shoppingCartRepository.save(shoppingCart).block();

        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().collectList().block().size();

        // Update the shoppingCart using partial update
        ShoppingCart partialUpdatedShoppingCart = new ShoppingCart();
        partialUpdatedShoppingCart.setId(shoppingCart.getId());

        partialUpdatedShoppingCart.status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShoppingCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedShoppingCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
        ShoppingCart testShoppingCart = shoppingCartList.get(shoppingCartList.size() - 1);
        assertThat(testShoppingCart.getPurchaseDate()).isEqualTo(DEFAULT_PURCHASE_DATE);
        assertThat(testShoppingCart.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateShoppingCartWithPatch() throws Exception {
        // Initialize the database
        shoppingCartRepository.save(shoppingCart).block();

        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().collectList().block().size();

        // Update the shoppingCart using partial update
        ShoppingCart partialUpdatedShoppingCart = new ShoppingCart();
        partialUpdatedShoppingCart.setId(shoppingCart.getId());

        partialUpdatedShoppingCart.purchaseDate(UPDATED_PURCHASE_DATE).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShoppingCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedShoppingCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
        ShoppingCart testShoppingCart = shoppingCartList.get(shoppingCartList.size() - 1);
        assertThat(testShoppingCart.getPurchaseDate()).isEqualTo(UPDATED_PURCHASE_DATE);
        assertThat(testShoppingCart.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().collectList().block().size();
        shoppingCart.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, shoppingCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().collectList().block().size();
        shoppingCart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().collectList().block().size();
        shoppingCart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(shoppingCart))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteShoppingCart() {
        // Initialize the database
        shoppingCartRepository.save(shoppingCart).block();

        int databaseSizeBeforeDelete = shoppingCartRepository.findAll().collectList().block().size();

        // Delete the shoppingCart
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, shoppingCart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll().collectList().block();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
