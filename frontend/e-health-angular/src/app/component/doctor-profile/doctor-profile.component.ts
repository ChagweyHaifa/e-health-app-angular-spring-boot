import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { Doctor } from 'src/app/model/doctor';
import { Review } from 'src/app/model/review';
import { NotificationService } from 'src/app/service/notification.service';
import { ReviewService } from 'src/app/service/review.service';
import { UserService } from 'src/app/service/user.service';
import { faCoffee, faCalendar } from '@fortawesome/free-solid-svg-icons';
@Component({
  selector: 'app-doctor-profile',
  templateUrl: './doctor-profile.component.html',
  styleUrls: ['./doctor-profile.component.css'],
})
export class DoctorProfileComponent implements OnInit {
  // faCoffee = faCoffee;
  // faCalendar = faCalendar;
  private subscriptions: Subscription[] = [];
  doctor: Doctor;
  reviews: Review[];
  constructor(
    private route: ActivatedRoute,
    private notificationService: NotificationService,
    private userService: UserService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      const doctorUsername: string =
        this.route.snapshot.paramMap.get('username');
      // console.log(doctorUsername);
      this.doctorInfo(doctorUsername);
      this.doctorReviews(doctorUsername);
    });
  }

  doctorInfo(doctorUsername: string) {
    this.subscriptions.push(
      this.userService.getDoctorInfo(doctorUsername).subscribe(
        (response: Doctor) => {
          this.doctor = response;
          console.log(this.doctor);
        },
        (errorResponse: HttpErrorResponse) => {
          // console.log(errorResponse);
          this.sendErrorNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  doctorReviews(doctorUsername: string) {
    this.subscriptions.push(
      this.reviewService.getDoctorReviews(doctorUsername).subscribe(
        (response: Review[]) => {
          this.reviews = response;
          console.log(this.reviews);
        },
        (errorResponse: HttpErrorResponse) => {
          // console.log(errorResponse);
          this.sendErrorNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  private sendErrorNotification(
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
}
