import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DoctorProfileComponent } from './component/doctor-profile/doctor-profile.component';
import { DoctorsComponent } from './component/doctors/doctors.component';

import { LoginComponent } from './component/login/login.component';
import { QuestionsComponent } from './component/questions/questions.component';
import { RegisterComponent } from './component/register/register.component';
import { SettingsComponent } from './component/settings/settings.component';
import { UserComponent } from './component/user/user.component';
import { AuthenticationGuard } from './guard/authentication.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'user/management', component: UserComponent },
  { path: 'find-a-doctor/:username', component: DoctorProfileComponent },
  { path: 'find-a-doctor', component: DoctorsComponent },
  { path: 'ask-a-question', component: QuestionsComponent },
  {
    path: 'settings',
    component: SettingsComponent,
    canActivate: [AuthenticationGuard],
  },
  { path: '', redirectTo: '/find-a-doctor', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
