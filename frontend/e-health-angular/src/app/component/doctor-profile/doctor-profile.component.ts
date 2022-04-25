import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
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
import { AuthenticationService } from 'src/app/service/authentication.service';
import { Visitor } from 'src/app/model/visitor';
import { Role } from 'src/app/enum/role.enum';
import { CustomHttpRespone } from 'src/app/model/custom-http-response';
import { User } from 'src/app/model/user';
import { Speciality } from 'src/app/model/speciality';
import { City } from 'src/app/model/city';
import { Country } from 'src/app/model/country';
import { State } from 'src/app/model/state';
import { FormService } from 'src/app/service/form.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Address } from 'src/app/model/address';
import { HeaderType } from 'src/app/enum/header-type.enum';
@Component({
  selector: 'app-doctor-profile',
  templateUrl: './doctor-profile.component.html',
  styleUrls: ['./doctor-profile.component.css'],
})
export class DoctorProfileComponent implements OnInit {
  private subscriptions: Subscription[] = [];
  loggedInUser = this.authenticationService.getUserFromLocalCache();
  doctor: Doctor = new Doctor();
  reviews: Review[];
  doctorUsername: string;
  nbOfReviews: number = 0;

  showLoading: boolean = false;
  specialities: Speciality[];
  countries: Country[];
  states: State[];
  cities: City[];

  fileName: string;
  profileImage: File;

  rating = {
    value: 1.5,
    count: this.nbOfReviews,
  };

  ratingStyle = {
    starsStyle: { height: '22px', width: '22px' },
    ratingStyle: { 'font-size': '18px' },
    countStyle: { 'font-size': '14px' },
  };

  reviewStyle = {
    starsStyle: { 'font-size': '20px' },
  };

  public form: FormGroup;

  rating1 = 0;

  constructor(
    private route: ActivatedRoute,
    private notificationService: NotificationService,
    private userService: UserService,
    private reviewService: ReviewService,
    private authenticationService: AuthenticationService,
    private formService: FormService
  ) {}

  ngOnInit(): void {
    // console.log(this.isLoggedIn);
    this.route.paramMap.subscribe(() => {
      this.doctorUsername = this.route.snapshot.paramMap.get('username');
      this.getDoctorInfo();
      this.getDoctorReviews();
    });
  }

  getDoctorInfo() {
    this.subscriptions.push(
      this.userService.getDoctorInfo(this.doctorUsername).subscribe(
        (response: Doctor) => {
          this.doctor = response;

          this.nbOfReviews = this.doctor.nbOfReviews;
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

  getDoctorReviews() {
    this.subscriptions.push(
      this.reviewService.getDoctorReviews(this.doctorUsername).subscribe(
        (response: Review[]) => {
          this.reviews = response;
          // console.log(this.reviews);
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

  addReview(reviewContent: any) {
    const review = new Review();
    review.content = reviewContent.value;
    const doctor = new Doctor();
    doctor.username = this.doctorUsername;
    review.doctor = doctor;

    // console.log(review);
    this.subscriptions.push(
      this.reviewService.addReview(review).subscribe(
        (response: number) => {
          console.log(response);
          this.getDoctorReviews();
          this.nbOfReviews = response;
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

  deleteReview(review: Review) {
    console.log(review.id);
    this.subscriptions.push(
      this.reviewService.deleteReview(review.id).subscribe(
        (response: number) => {
          console.log(response);
          this.getDoctorReviews();
          this.nbOfReviews = response;
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

  getSpecialities() {
    this.subscriptions.push(
      this.formService.getSpecialities().subscribe(
        (response: Speciality[]) => {
          this.specialities = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendErrorNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  getCountries() {
    this.subscriptions.push(
      this.formService.getCountries().subscribe(
        (response: Country[]) => {
          this.countries = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendErrorNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  getStates(countryName: string) {
    this.subscriptions.push(
      this.formService.getStates(countryName).subscribe(
        (response: State[]) => {
          this.states = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendErrorNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  getCities(stateName: string) {
    this.subscriptions.push(
      this.formService.getCities(stateName).subscribe(
        (response: City[]) => {
          this.cities = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendErrorNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  onEditDoctorProfile() {
    this.getSpecialities();
    this.getCountries();
    this.getStates(this.doctor.address.country);
    this.getCities(this.doctor.address.state);
  }

  clickDoctorProfileSubmitBtn() {
    this.clickButton('edit-doctor-profile-submit-btn');
  }

  editDoctorProfile() {
    // console.log(this.doctor);
    this.showLoading = true;
    this.subscriptions.push(
      this.userService.updateDoctor(this.doctor).subscribe(
        (response: HttpResponse<Doctor>) => {
          this.showLoading = false;
          const token = response.headers.get(HeaderType.JWT_TOKEN);
          this.authenticationService.saveToken(token);
          this.clickButton('edit-doctor-profile-close-btn');
        },
        (errorResponse: HttpErrorResponse) => {
          this.showLoading = false;
          this.sendErrorNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  clickProfileImageBtn() {
    this.clickButton('profile-image-input');
  }

  public onProfileImageChange(event: Event): void {
    // console.log(event);
    this.profileImage = (<HTMLInputElement>event.target).files[0];
    this.fileName = this.profileImage.name;
    console.log(this.profileImage);
    console.log('filename:' + this.fileName);
  }

  onUpdateProfileImage() {
    // console.log(this.profileImage);

    const formData = new FormData();
    formData.append('profileImage', this.profileImage);
    this.subscriptions.push(
      this.userService.updateProfileImage(formData).subscribe(
        (response: Doctor) => {
          this.doctor.profileImageUrl = `${
            response.profileImageUrl
          }?time=${new Date().getTime()}`;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendErrorNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  get isLoggedIn(): boolean {
    return this.authenticationService.isUserLoggedIn();
  }

  public get isVisitor(): boolean {
    if (!this.isLoggedIn) return false;
    else return this.getUserRole() === Role.VISITOR;
  }

  public get isDoctor(): boolean {
    if (!this.isLoggedIn) return false;
    else return this.getUserRole() === Role.DOCTOR;
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

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  private clickButton(buttonId: string) {
    document.getElementById(buttonId).click();
  }
}
