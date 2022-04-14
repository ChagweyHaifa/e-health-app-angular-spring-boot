import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { City } from 'src/app/model/city';
import { Country } from 'src/app/model/country';
import { Doctor } from 'src/app/model/doctor';
import { Speciality } from 'src/app/model/speciality';
import { Address } from 'src/app/model/address';
import { State } from 'src/app/model/state';

import { FormService } from 'src/app/service/form.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-doctors',
  templateUrl: './doctors.component.html',
  styleUrls: ['./doctors.component.css'],
})
export class DoctorsComponent implements OnInit {
  default: string = null;
  private subscriptions: Subscription[] = [];
  specialities: Speciality[];
  countries: Country[];
  states: State[];
  cities: City[];
  doctors: Doctor[];
  constructor(
    private formService: FormService,
    private userService: UserService,
    private notificationService: NotificationService,
    private router: Router
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
    // console.log(searchForm.value);

    const doctor = new Doctor();
    // if (searchForm.value.specialityName !== '') {
    const speciality = new Speciality();
    speciality.name = searchForm.value.specialityName;
    doctor.speciality = speciality;
    // }
    const address = new Address();
    address.country = searchForm.value.country;
    address.state = searchForm.value.state;
    address.city = searchForm.value.city;
    doctor.address = address;
    console.log(doctor);
    this.subscriptions.push(
      this.userService.searchForDoctors(doctor).subscribe(
        (response: Doctor[]) => {
          this.doctors = response;
          // console.log(this.doctors);
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

  getDoctorProfile(DoctorUsername: string) {
    // let url: string = '/find-a-doctor/' + DoctorUsername;
    // this.router.navigate([url]);
    const url = this.router.serializeUrl(
      this.router.createUrlTree([`/find-a-doctor/${DoctorUsername}`])
    );
    window.open(url, '_blank');
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
