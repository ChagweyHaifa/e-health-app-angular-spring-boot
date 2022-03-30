import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent implements OnInit, OnDestroy {
  showLoading: boolean = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/user/management');
    }
  }
  onRegister(user: User) {
    // console.log(user);
    this.showLoading = true;
    this.subscriptions.push(
      this.authenticationService.register(user).subscribe(
        (response: User) => {
          // console.log(response);
          this.showLoading = false;
          this.sendNotification(
            NotificationType.SUCCESS,
            `A new account was created for ${response.firstName}.
          Please check your email for password to log in.`
          );
        },
        (errorResponse: HttpErrorResponse) => {
          // console.log(errorResponse);
          this.showLoading = false;
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  private sendNotification(
    notificationType: NotificationType,
    message: string
  ): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      // if we didn't get any message from the backend, for example in case we didn't start the backend server
      this.notificationService.notify(
        notificationType,
        'An error occurred. Please try again.'
      );
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
