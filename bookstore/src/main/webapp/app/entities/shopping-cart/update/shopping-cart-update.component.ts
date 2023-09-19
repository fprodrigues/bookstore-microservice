import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ShoppingCartFormService, ShoppingCartFormGroup } from './shopping-cart-form.service';
import { IShoppingCart } from '../shopping-cart.model';
import { ShoppingCartService } from '../service/shopping-cart.service';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';

@Component({
  selector: 'jhi-shopping-cart-update',
  templateUrl: './shopping-cart-update.component.html',
})
export class ShoppingCartUpdateComponent implements OnInit {
  isSaving = false;
  shoppingCart: IShoppingCart | null = null;

  ordersSharedCollection: IOrder[] = [];

  editForm: ShoppingCartFormGroup = this.shoppingCartFormService.createShoppingCartFormGroup();

  constructor(
    protected shoppingCartService: ShoppingCartService,
    protected shoppingCartFormService: ShoppingCartFormService,
    protected orderService: OrderService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOrder = (o1: IOrder | null, o2: IOrder | null): boolean => this.orderService.compareOrder(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ shoppingCart }) => {
      this.shoppingCart = shoppingCart;
      if (shoppingCart) {
        this.updateForm(shoppingCart);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const shoppingCart = this.shoppingCartFormService.getShoppingCart(this.editForm);
    if (shoppingCart.id !== null) {
      this.subscribeToSaveResponse(this.shoppingCartService.update(shoppingCart));
    } else {
      this.subscribeToSaveResponse(this.shoppingCartService.create(shoppingCart));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IShoppingCart>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(shoppingCart: IShoppingCart): void {
    this.shoppingCart = shoppingCart;
    this.shoppingCartFormService.resetForm(this.editForm, shoppingCart);

    this.ordersSharedCollection = this.orderService.addOrderToCollectionIfMissing<IOrder>(this.ordersSharedCollection, shoppingCart.order);
  }

  protected loadRelationshipsOptions(): void {
    this.orderService
      .query()
      .pipe(map((res: HttpResponse<IOrder[]>) => res.body ?? []))
      .pipe(map((orders: IOrder[]) => this.orderService.addOrderToCollectionIfMissing<IOrder>(orders, this.shoppingCart?.order)))
      .subscribe((orders: IOrder[]) => (this.ordersSharedCollection = orders));
  }
}
