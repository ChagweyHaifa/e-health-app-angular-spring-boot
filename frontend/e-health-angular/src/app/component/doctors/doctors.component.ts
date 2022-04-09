import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { City } from 'src/app/model/city';
import { Country } from 'src/app/model/country';
import { Speciality } from 'src/app/model/speciality';
import { State } from 'src/app/model/state';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { FormService } from 'src/app/service/form.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-doctors',
  templateUrl: './doctors.component.html',
  styleUrls: ['./doctors.component.css'],
})
export class DoctorsComponent implements OnInit {
  private subscriptions: Subscription[] = [];
  specialities: Speciality[];
  countries: Country[];
  states: State[];
  cities: City[];
  constructor(
    private formService: FormService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.getSpecialities();
    this.getCountries();
  }

  getSpecialities() {
    this.subscriptions.push(
      this.formService.getSpecialities().subscribe(
        (response: Speciality[]) => {
          this.specialities = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
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
          this.sendNotification(
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
          this.sendNotification(
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
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  onSearchDoctor(searchForm: NgForm) {
    console.log(searchForm.value);
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
}
