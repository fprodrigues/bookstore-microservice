import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IShoppingCart, NewShoppingCart } from '../shopping-cart.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IShoppingCart for edit and NewShoppingCartFormGroupInput for create.
 */
type ShoppingCartFormGroupInput = IShoppingCart | PartialWithRequiredKeyOf<NewShoppingCart>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IShoppingCart | NewShoppingCart> = Omit<T, 'purchaseDate'> & {
  purchaseDate?: string | null;
};

type ShoppingCartFormRawValue = FormValueOf<IShoppingCart>;

type NewShoppingCartFormRawValue = FormValueOf<NewShoppingCart>;

type ShoppingCartFormDefaults = Pick<NewShoppingCart, 'id' | 'purchaseDate'>;

type ShoppingCartFormGroupContent = {
  id: FormControl<ShoppingCartFormRawValue['id'] | NewShoppingCart['id']>;
  purchaseDate: FormControl<ShoppingCartFormRawValue['purchaseDate']>;
  status: FormControl<ShoppingCartFormRawValue['status']>;
  order: FormControl<ShoppingCartFormRawValue['order']>;
};

export type ShoppingCartFormGroup = FormGroup<ShoppingCartFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ShoppingCartFormService {
  createShoppingCartFormGroup(shoppingCart: ShoppingCartFormGroupInput = { id: null }): ShoppingCartFormGroup {
    const shoppingCartRawValue = this.convertShoppingCartToShoppingCartRawValue({
      ...this.getFormDefaults(),
      ...shoppingCart,
    });
    return new FormGroup<ShoppingCartFormGroupContent>({
      id: new FormControl(
        { value: shoppingCartRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      purchaseDate: new FormControl(shoppingCartRawValue.purchaseDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(shoppingCartRawValue.status, {
        validators: [Validators.required],
      }),
      order: new FormControl(shoppingCartRawValue.order),
    });
  }

  getShoppingCart(form: ShoppingCartFormGroup): IShoppingCart | NewShoppingCart {
    return this.convertShoppingCartRawValueToShoppingCart(form.getRawValue() as ShoppingCartFormRawValue | NewShoppingCartFormRawValue);
  }

  resetForm(form: ShoppingCartFormGroup, shoppingCart: ShoppingCartFormGroupInput): void {
    const shoppingCartRawValue = this.convertShoppingCartToShoppingCartRawValue({ ...this.getFormDefaults(), ...shoppingCart });
    form.reset(
      {
        ...shoppingCartRawValue,
        id: { value: shoppingCartRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ShoppingCartFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      purchaseDate: currentTime,
    };
  }

  private convertShoppingCartRawValueToShoppingCart(
    rawShoppingCart: ShoppingCartFormRawValue | NewShoppingCartFormRawValue
  ): IShoppingCart | NewShoppingCart {
    return {
      ...rawShoppingCart,
      purchaseDate: dayjs(rawShoppingCart.purchaseDate, DATE_TIME_FORMAT),
    };
  }

  private convertShoppingCartToShoppingCartRawValue(
    shoppingCart: IShoppingCart | (Partial<NewShoppingCart> & ShoppingCartFormDefaults)
  ): ShoppingCartFormRawValue | PartialWithRequiredKeyOf<NewShoppingCartFormRawValue> {
    return {
      ...shoppingCart,
      purchaseDate: shoppingCart.purchaseDate ? shoppingCart.purchaseDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
