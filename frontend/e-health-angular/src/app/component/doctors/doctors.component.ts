import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  NgForm,
  Validators,
} from '@angular/forms';
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
export class DoctorsComponent implements OnInit, OnDestroy {
  default: string = null;
  private subscriptions: Subscription[] = [];
  specialities: Speciality[];
  countries: Country[];
  states: State[];
  cities: City[];
  doctors: Doctor[];
  defaultCountry: string = 'Tunisie';

  doctorSearchForm: FormGroup;
  constructor(
    private formService: FormService,
    private userService: UserService,
    private notificationService: NotificationService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.doctorSearchForm = this.formBuilder.group({
      speciality: this.formBuilder.group({
        id: '',
        name: '',
      }),
      address: this.formBuilder.group({
        id: '',
        country: '',
        state: '',
        city: '',
      }),
    });
    this.doctorSearchForm.controls.address
      .get('country')
      .setValue(this.defaultCountry);
  }

  ngOnInit(): void {
    this.getSpecialities();
    this.getCountries();
    this.getStates(this.defaultCountry);
    this.onSearchDoctor();
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

  get f() {
    return this.doctorSearchForm.controls;
  }

  resetAddress(address: string) {
    switch (address) {
      case 'country': {
        this.f.address.patchValue({
          state: '',
          city: '',
        });
        break;
      }
      case 'state': {
        this.f.address.patchValue({
          city: '',
        });
        break;
      }
    }
    // console.log(this.doctorSearchForm.value);
  }

  onSearchDoctor() {
    // console.log(this.doctorSearchForm.value);
    this.subscriptions.push(
      this.userService.searchForDoctors(this.doctorSearchForm.value).subscribe(
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

  private clickButton(buttonId: string) {
    document.getElementById(buttonId).click();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
