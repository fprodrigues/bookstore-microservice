import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'book',
        data: { pageTitle: 'bookstoreApp.orderApiBook.home.title' },
        loadChildren: () => import('./orderApi/book/book.module').then(m => m.OrderApiBookModule),
      },
      {
        path: 'customer',
        data: { pageTitle: 'bookstoreApp.customerApiCustomer.home.title' },
        loadChildren: () => import('./customerApi/customer/customer.module').then(m => m.CustomerApiCustomerModule),
      },
      {
        path: 'order',
        data: { pageTitle: 'bookstoreApp.orderApiOrder.home.title' },
        loadChildren: () => import('./orderApi/order/order.module').then(m => m.OrderApiOrderModule),
      },
      {
        path: 'shopping-cart',
        data: { pageTitle: 'bookstoreApp.orderApiShoppingCart.home.title' },
        loadChildren: () => import('./orderApi/shopping-cart/shopping-cart.module').then(m => m.OrderApiShoppingCartModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
