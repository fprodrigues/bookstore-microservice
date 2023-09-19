import dayjs from 'dayjs/esm';
import { IOrder } from 'app/entities/order/order.model';

export interface IShoppingCart {
  id: number;
  purchaseDate?: dayjs.Dayjs | null;
  status?: string | null;
  order?: Pick<IOrder, 'id'> | null;
}

export type NewShoppingCart = Omit<IShoppingCart, 'id'> & { id: null };
