import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrder } from '../order.model';
import { OrderService } from '../service/order.service';

@Injectable({ providedIn: 'root' })
export class OrderRoutingResolveService implements Resolve<IOrder | null> {
  constructor(protected service: OrderService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrder | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((order: HttpResponse<IOrder>) => {
          if (order.body) {
            return of(order.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
