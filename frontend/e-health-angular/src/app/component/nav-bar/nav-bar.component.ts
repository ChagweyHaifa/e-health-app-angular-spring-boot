import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css'],
})
export class NavBarComponent implements OnInit {
  constructor(
    private authenticationService: AuthenticationService,
    private notificationService: NotificationService,
    private router: Router
  ) {}
  public loggedInUser: User;
  isLoggedIn: boolean;

  ngOnInit(): void {
    this.authenticationService.isUserLoggedIn();
    this.authenticationService.isLoggedIn.subscribe(
      (data) => (this.isLoggedIn = data)
    );
    // this.loggedInUser = this.authenticationService.getUserFromLocalCache();
    // console.log(this.isLoggedIn);
  }

  onLogOut(): void {
    this.authenticationService.logOut();
    // this.authenticationService.isLoggedIn.next(false);
    this.router.navigate(['/login']);
    this.sendNotification(
      NotificationType.SUCCESS,
      `You've been successfully logged out`
    );
  }

  private sendNotification(
    notificationType: NotificationType,
    message: string
  ): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(
        notificationType,
        'An error occurred. Please try again.'
      );
    }
  }
}
