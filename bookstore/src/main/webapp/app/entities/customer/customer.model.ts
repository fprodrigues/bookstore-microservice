import { IOrder } from 'app/entities/order/order.model';

export interface ICustomer {
  id: number;
  name?: string | null;
  email?: string | null;
  address?: string | null;
  phone?: string | null;
  orders?: Pick<IOrder, 'id'> | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
