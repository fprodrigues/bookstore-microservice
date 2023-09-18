import dayjs from 'dayjs/esm';

import { IOrder, NewOrder } from './order.model';

export const sampleWithRequiredData: IOrder = {
  id: 47761,
  orderDate: dayjs('2023-09-17T19:14'),
  status: 'New Parks Handcrafted',
};

export const sampleWithPartialData: IOrder = {
  id: 61522,
  orderDate: dayjs('2023-09-18T02:36'),
  status: 'Ville Account Cambridgeshire',
};

export const sampleWithFullData: IOrder = {
  id: 80079,
  orderDate: dayjs('2023-09-18T00:26'),
  status: 'violet reinvent International',
};

export const sampleWithNewData: NewOrder = {
  orderDate: dayjs('2023-09-17T20:48'),
  status: 'Unbranded 24/7 Intranet',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
