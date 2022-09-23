import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { NotificationType } from '../enum/notification-type.enum';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationGuard implements CanActivate {
  constructor(
    private authenticationService: AuthenticationService,
    private router: Router,
    private notificationService: NotificationService
  ) {}
  // canActivate(
  //   route: ActivatedRouteSnapshot,
  //   state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
  //   return true;
  // }
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    let url: string = state.url;
    return this.isUserLoggedIn(route, state);
  }

  private isUserLoggedIn(route: ActivatedRouteSnapshot, url: any): boolean {
    if (this.authenticationService.isUserLoggedIn()) {
      const userRole = this.authenticationService.getUserFromLocalCache().role;
      if (route.data.role && route.data.role.indexOf(userRole) === -1) {
        this.router.navigate(['/login']);
        this.notificationService.notify(
          NotificationType.ERROR,
          `Only admin has access to this page.`
        );
        return false;
      }
      return true;
    }
    this.router.navigate(['/login']);
    this.notificationService.notify(
      NotificationType.ERROR,
      `You need to log in to access this page.`
    );
    return false;
  }
}
