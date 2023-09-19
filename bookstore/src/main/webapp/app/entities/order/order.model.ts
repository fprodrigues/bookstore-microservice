import dayjs from 'dayjs/esm';

export interface IOrder {
  id: number;
  orderDate?: dayjs.Dayjs | null;
  status?: string | null;
}

export type NewOrder = Omit<IOrder, 'id'> & { id: null };
