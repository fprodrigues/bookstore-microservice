package com.orderapi.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orderapi.app.IntegrationTest;
import com.orderapi.app.domain.ShoppingCart;
import com.orderapi.app.repository.ShoppingCartRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ShoppingCartResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restShoppingCartMockMvc;

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

    @BeforeEach
    public void initTest() {
        shoppingCart = createEntity(em);
    }

    @Test
    @Transactional
    void createShoppingCart() throws Exception {
        int databaseSizeBeforeCreate = shoppingCartRepository.findAll().size();
        // Create the ShoppingCart
        restShoppingCartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoppingCart)))
            .andExpect(status().isCreated());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeCreate + 1);
        ShoppingCart testShoppingCart = shoppingCartList.get(shoppingCartList.size() - 1);
        assertThat(testShoppingCart.getPurchaseDate()).isEqualTo(DEFAULT_PURCHASE_DATE);
        assertThat(testShoppingCart.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createShoppingCartWithExistingId() throws Exception {
        // Create the ShoppingCart with an existing ID
        shoppingCart.setId(1L);

        int databaseSizeBeforeCreate = shoppingCartRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShoppingCartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoppingCart)))
            .andExpect(status().isBadRequest());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPurchaseDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = shoppingCartRepository.findAll().size();
        // set the field null
        shoppingCart.setPurchaseDate(null);

        // Create the ShoppingCart, which fails.

        restShoppingCartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoppingCart)))
            .andExpect(status().isBadRequest());

        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = shoppingCartRepository.findAll().size();
        // set the field null
        shoppingCart.setStatus(null);

        // Create the ShoppingCart, which fails.

        restShoppingCartMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoppingCart)))
            .andExpect(status().isBadRequest());

        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShoppingCarts() throws Exception {
        // Initialize the database
        shoppingCartRepository.saveAndFlush(shoppingCart);

        // Get all the shoppingCartList
        restShoppingCartMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shoppingCart.getId().intValue())))
            .andExpect(jsonPath("$.[*].purchaseDate").value(hasItem(DEFAULT_PURCHASE_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getShoppingCart() throws Exception {
        // Initialize the database
        shoppingCartRepository.saveAndFlush(shoppingCart);

        // Get the shoppingCart
        restShoppingCartMockMvc
            .perform(get(ENTITY_API_URL_ID, shoppingCart.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shoppingCart.getId().intValue()))
            .andExpect(jsonPath("$.purchaseDate").value(DEFAULT_PURCHASE_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingShoppingCart() throws Exception {
        // Get the shoppingCart
        restShoppingCartMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShoppingCart() throws Exception {
        // Initialize the database
        shoppingCartRepository.saveAndFlush(shoppingCart);

        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().size();

        // Update the shoppingCart
        ShoppingCart updatedShoppingCart = shoppingCartRepository.findById(shoppingCart.getId()).get();
        // Disconnect from session so that the updates on updatedShoppingCart are not directly saved in db
        em.detach(updatedShoppingCart);
        updatedShoppingCart.purchaseDate(UPDATED_PURCHASE_DATE).status(UPDATED_STATUS);

        restShoppingCartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShoppingCart.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedShoppingCart))
            )
            .andExpect(status().isOk());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
        ShoppingCart testShoppingCart = shoppingCartList.get(shoppingCartList.size() - 1);
        assertThat(testShoppingCart.getPurchaseDate()).isEqualTo(UPDATED_PURCHASE_DATE);
        assertThat(testShoppingCart.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().size();
        shoppingCart.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoppingCartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shoppingCart.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shoppingCart))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().size();
        shoppingCart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoppingCartMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shoppingCart))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().size();
        shoppingCart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoppingCartMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shoppingCart)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShoppingCartWithPatch() throws Exception {
        // Initialize the database
        shoppingCartRepository.saveAndFlush(shoppingCart);

        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().size();

        // Update the shoppingCart using partial update
        ShoppingCart partialUpdatedShoppingCart = new ShoppingCart();
        partialUpdatedShoppingCart.setId(shoppingCart.getId());

        partialUpdatedShoppingCart.status(UPDATED_STATUS);

        restShoppingCartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoppingCart.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShoppingCart))
            )
            .andExpect(status().isOk());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
        ShoppingCart testShoppingCart = shoppingCartList.get(shoppingCartList.size() - 1);
        assertThat(testShoppingCart.getPurchaseDate()).isEqualTo(DEFAULT_PURCHASE_DATE);
        assertThat(testShoppingCart.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateShoppingCartWithPatch() throws Exception {
        // Initialize the database
        shoppingCartRepository.saveAndFlush(shoppingCart);

        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().size();

        // Update the shoppingCart using partial update
        ShoppingCart partialUpdatedShoppingCart = new ShoppingCart();
        partialUpdatedShoppingCart.setId(shoppingCart.getId());

        partialUpdatedShoppingCart.purchaseDate(UPDATED_PURCHASE_DATE).status(UPDATED_STATUS);

        restShoppingCartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShoppingCart.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShoppingCart))
            )
            .andExpect(status().isOk());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
        ShoppingCart testShoppingCart = shoppingCartList.get(shoppingCartList.size() - 1);
        assertThat(testShoppingCart.getPurchaseDate()).isEqualTo(UPDATED_PURCHASE_DATE);
        assertThat(testShoppingCart.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().size();
        shoppingCart.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShoppingCartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shoppingCart.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shoppingCart))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().size();
        shoppingCart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoppingCartMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shoppingCart))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShoppingCart() throws Exception {
        int databaseSizeBeforeUpdate = shoppingCartRepository.findAll().size();
        shoppingCart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShoppingCartMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(shoppingCart))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShoppingCart in the database
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShoppingCart() throws Exception {
        // Initialize the database
        shoppingCartRepository.saveAndFlush(shoppingCart);

        int databaseSizeBeforeDelete = shoppingCartRepository.findAll().size();

        // Delete the shoppingCart
        restShoppingCartMockMvc
            .perform(delete(ENTITY_API_URL_ID, shoppingCart.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findAll();
        assertThat(shoppingCartList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
